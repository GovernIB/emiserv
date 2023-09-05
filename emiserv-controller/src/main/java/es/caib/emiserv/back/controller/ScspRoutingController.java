/**
 * 
 */
package es.caib.emiserv.back.controller;

import es.caib.emiserv.logic.intf.dto.RedireccioProcessarResultatDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.service.RedireccioService;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

/**
 * Controlador per a la redirecció de peticions SCSP i que substitueix la
 * funcionalitat del bus de serveis.
 * Per a serveis de tipus enrutador simple fa una redirecció en el cas del POST.
 * Basat en:
 *     http://www.adrianwalker.org/2009/09/http-proxy-servlet.html
 *     https://svn.apache.org/repos/asf/rave/donations/ogce-gadget-container/ishindig-webapp/src/main/java/cgl/shindig/layoutmanager/servlet/ProxyServlet.java
 * Per a serveis de tipus enrutador múltiple redirigeix la petició POST cap a múltiples emissors
 * i es tracten les respostes per retornar una única resposta al requirent.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ScspRoutingController extends BaseController {

	private static final String SERVLET_PATH_PREFIX = "/scspRouting";

	private static final String STRING_LOCATION_HEADER = "Location";
	private static final String STRING_HOST_HEADER_NAME = "Host";
	private static final String STRING_CONTENT_LENGTH_HEADER_NAME = "Content-Length";

	@Autowired
	private RedireccioService redireccioService;

	@RequestMapping(value = SERVLET_PATH_PREFIX + "/**", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void doGet(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String proxyUrl = getProxyUrl(
				request,
				null);
		logger.debug(
				"Processant petició GET a l'enrutador (" +
				"requestURI=" + request.getRequestURI() + ", " +
				"proxyUrl=" + proxyUrl + ")");
		if (proxyUrl != null) {
			GetMethod getMethod = new GetMethod(proxyUrl);
			setProxyRequestHeaders(
					request,
					getMethod,
					proxyUrl);
			int proxyResponseCode = executeProxyRequest(
					getMethod,
					null);
			processProxyResponse(
					request, 
					response, 
					proxyResponseCode, 
					proxyUrl, 
					getMethod, 
					null);
		}
	}

	@RequestMapping(value = SERVLET_PATH_PREFIX + "/**", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void doPost(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (ServletFileUpload.isMultipartContent(request)) {
			throw new ServletException("Multipart request not supported");
		} else {
			logger.debug(
					"Processant petició POST a l'enrutador (" +
					"requestURI=" + request.getRequestURI() + ")");
			int proxyResponseCode = -1;
			String proxyUrl = null;
			PostMethod postMethod = null;
			// Processa la petició d'entrada
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(
					request.getInputStream(),
					baos);
			byte[] xml = baos.toByteArray();
			RequestEntity requestEntity = new ByteArrayRequestEntity(xml);
			RedireccioProcessarResultatDto resultat = redireccioService.processarPeticio(xml);
			logger.debug(
					"Dades del processament de la petició SCSP (" +
					"tipus=" + resultat.getTipus() + ", " +
					"serveiCodi=" + resultat.getAtributCodigoCertificado() + ", " +
					"peticioId=" + resultat.getAtributPeticioId() + ", " +
					"urlDesti=" + resultat.getUrlDesti() + ", " +
					"urlDestins=" + resultat.getUrlDestins() + ", " +
					"error=" + resultat.isError() + ")");
			if (!resultat.isError()) {
				// Fa la petició segons el tipus d'enrutador simple o múltiple
				if (ServeiTipusEnumDto.ENRUTADOR.equals(resultat.getTipus()) ) {
					// ENRUTADOR SIMPLE
					proxyUrl = getProxyUrl(
							request,
							resultat.getUrlDesti());
					postMethod = new PostMethod(proxyUrl);
					copiarCapsaleresHttp(
							request,
							postMethod,
							proxyUrl);
					postMethod.setRequestEntity(requestEntity);
					logger.debug(
							"Executant la redirecció simple de la petició (proxyUrl=" + proxyUrl + ")");
					proxyResponseCode = executeProxyRequest(
							postMethod,
							resultat);
				} else {
					// ENRUTADOR MÚLTIPLE
					// Crea un servei per manejar els threads de cada petició
					ExecutorService service = Executors.newFixedThreadPool(resultat.getUrlDestins().size());
					// Crea la llista d'objectes per a les peticions en paral·lel.
					List<EnrutamentMultipleThread> peticionsMultiples = new ArrayList<EnrutamentMultipleThread>();
					for (String codiEntitat: resultat.getUrlDestins().keySet()) {
						peticionsMultiples.add(
								new EnrutamentMultipleThread(
									request,
									requestEntity,
									codiEntitat,
									resultat.getUrlDestins().get(codiEntitat),
									resultat.copy()));
					}
					// Llença totes les peticions en paral·lel
					logger.debug("Iniciant " + resultat.getUrlDestins().size() + " peticions múltiples en paral·lel (" +
							"serveiCodi=" + resultat.getAtributCodigoCertificado() + ", " +
							"peticioId=" + resultat.getAtributPeticioId() + ", " +
							"urlDestins=" + resultat.getUrlDestins() + ")");
					List<Future<EnrutamentMultipleThreadResult>> respostesThreadsPeticio = null;
					try {
						respostesThreadsPeticio = service.invokeAll(peticionsMultiples);
					} catch(Exception err) {
						err.printStackTrace();
					}
					service.shutdown();
					logger.debug("Peticions múltiples en paral·lel finalitzades (" +
							"serveiCodi=" + resultat.getAtributCodigoCertificado() + ", " +
							"peticioId=" + resultat.getAtributPeticioId() + ", " +
							"urlDestins=" + resultat.getUrlDestins() + ")");
					Map<String, byte[]> xmlsPerEscollir = new HashMap<String, byte[]>();
					Map<String, EnrutamentMultipleThreadResult> respostesPeticions = new HashMap<String, ScspRoutingController.EnrutamentMultipleThreadResult>();
					if (respostesThreadsPeticio != null) {
						for (Future<EnrutamentMultipleThreadResult> r: respostesThreadsPeticio) {
							try {
								// Guarda la petició per informar la resposta
								respostesPeticions.put(r.get().codiEntitat, r.get());
								// Prepara les respostes per a escollir-ne una
								xmlsPerEscollir.put(r.get().codiEntitat, r.get().getXml());
							} catch (Exception ex) {
								logger.error("Error processant les respostes de les peticions múltiples (" +
										"serveiCodi=" + resultat.getAtributCodigoCertificado() + ", " +
										"peticioId=" + resultat.getAtributPeticioId() + ", " +
										"urlDestins=" + resultat.getUrlDestins() + ")", ex);
							}
						}
					}
					// Processar les respostes amb el mètode del servei d'enrutament
					String respostaEscollida = redireccioService.escollirResposta(
							resultat,
							xmlsPerEscollir);
					resultat.setEntitatCodiRedireccio(respostaEscollida);
					// Retornar resultat amb la resposta escollida
					EnrutamentMultipleThreadResult resposta = respostesPeticions.get(respostaEscollida);
					if (resposta != null) {
						proxyResponseCode = resposta.getProxyResponseCode();
						proxyUrl = resposta.getMethod().getURI().toString();
						postMethod = resposta.getMethod();
					}
				}
			}
			// Tractament comú de la resposta
			this.processProxyResponse(
					request, 
					response, 
					proxyResponseCode, 
					proxyUrl, 
					postMethod,
					resultat);
		} 
	}

	/*
	 * Processa la resposta i si tot va bé escriu el contingut en el httpResponse.
	 */
	private void processProxyResponse(
			HttpServletRequest request, 
			HttpServletResponse response, 
			int proxyResponseCode,
			String proxyUrl, 
			HttpMethod postMethod, 
			RedireccioProcessarResultatDto resultat) throws ServletException, IOException {
		if (resultat != null && resultat.isError()) {
			logger.debug(
					"Processant resposta errònia d'una petició a l'enrutador (" +
					"peticioId=" + resultat.getAtributPeticioId() + ", " +
					"serveiCodi=" + resultat.getAtributCodigoCertificado() + ", " +
					"errorCodi=" + resultat.getErrorCodi() + ", " +
					"errorDescripcio=" + resultat.getErrorDescripcio() + ")");
			String soapFault = redireccioService.generarSoapFault(resultat);
			try {
				redireccioService.processarResposta(
						resultat.getAtributPeticioId(),
						resultat.getAtributCodigoCertificado(),
						soapFault.getBytes(),
						resultat.getEntitatCodiRedireccio());
				response.getOutputStream().write(soapFault.getBytes());
			} catch (Exception ex) {
				logger.error("Error processant resposta errònia d'una petició a l'enrutador: " + 
						ex.getClass().getName() + ": " +
						ex.getLocalizedMessage());
			}
		} else if (proxyResponseCode != -1) {
			if (proxyResponseCode >= HttpServletResponse.SC_MULTIPLE_CHOICES /* 300 */
					&& proxyResponseCode < HttpServletResponse.SC_NOT_MODIFIED /* 304 */) {
				logger.debug(
						"Processant resposta d'una petició a l'enrutador amb redireccions (" +
						"peticioId=" + (resultat != null ? resultat.getAtributPeticioId() : "") + ", " +
						"serveiCodi=" + (resultat != null ? resultat.getAtributCodigoCertificado() : "") + ", " +
						"proxyResponseCode=" + proxyResponseCode + ")");
				String statusCode = Integer.toString(proxyResponseCode);
				String location = postMethod.getResponseHeader(STRING_LOCATION_HEADER).getValue();
				if (location == null) {
					logger.error(
							"S'ha rebut un codi d'estat " + statusCode + " del servidor remot " + 
							"però no s'ha trobat cap capçalera " + STRING_LOCATION_HEADER + " a la resposta " +
							getParametresPerExcepcio(proxyUrl, resultat));
					throw new ServletException(
							"S'ha rebut un codi d'estat " + statusCode + " del servidor remot " + 
							"però no s'ha trobat cap capçalera " + STRING_LOCATION_HEADER + " a la resposta " +
							getParametresPerExcepcio(proxyUrl, resultat));
				}
				String myHostName = request.getServerName();
				if (request.getServerPort() != 80) {
					myHostName += ":" + request.getServerPort();
				}
				myHostName += request.getContextPath();
				try {
					response.sendRedirect(
							location.replace(
									getProxyHostAndPort(proxyUrl),
									myHostName));
				} catch (IOException ex) {
					logger.error(
							"S'ha rebut un codi d'estat " + statusCode + " del servidor remot " + 
							"i s'ha produit un error fent la redirecció " +
							getParametresPerExcepcio(proxyUrl, resultat),
							ex);
					throw new ServletException(
							"S'ha rebut un codi d'estat " + statusCode + " del servidor remot " + 
							"i s'ha produit un error fent la redirecció " +
							getParametresPerExcepcio(proxyUrl, resultat));
				}
				return;
			} else if (proxyResponseCode == HttpServletResponse.SC_NOT_MODIFIED) {
				response.setIntHeader(STRING_CONTENT_LENGTH_HEADER_NAME, 0);
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
			Header[] headerArrayResponse = postMethod.getResponseHeaders();
			String contentEncoding = "";
			for (Header header: headerArrayResponse) {
				if (header.getName().equals("Content-Encoding"))
					contentEncoding = header.getValue();
			}
			try {
//				// MOCK
//				if (proxyUrl.equals("http://localhost:8080/mockservices/peticionSincrona")) {
//					redireccioService.processarResposta(
//							resultat.getAtributPeticioId(),
//							resultat.getAtributCodigoCertificado(),
//							XML_MOCK.getBytes(),
//							resultat.getEntitatCodiRedireccio());
//					response.setStatus(proxyResponseCode);
//					for (Header header: headerArrayResponse) {
//						if (header.getName().equals("Transfer-Encoding") && header.getValue().equals("chunked"))
//							continue;
//						response.setHeader(header.getName(), header.getValue());
//					}
//					response.getOutputStream().write(XML_MOCK.getBytes());
//					return;
//				}

				// REAL
				byte[] respostaBytes = contentEncoding.toLowerCase().contains("gzip") ?
							IOUtils.toByteArray(new GZIPInputStream(new ByteArrayInputStream(postMethod.getResponseBody())))
							: postMethod.getResponseBody();
				if (resultat != null) {
					logger.debug(
							"Retornant resposta final d'una petició POST a l'enrutador (" +
							"peticioId=" + resultat.getAtributPeticioId() + ", " +
							"serveiCodi=" + resultat.getAtributCodigoCertificado() + ", " +
							"resposta=" + new String(respostaBytes) + ")");
					try {
						redireccioService.processarResposta(
								resultat.getAtributPeticioId(),
								resultat.getAtributCodigoCertificado(),
								respostaBytes,
								resultat.getEntitatCodiRedireccio());
						// Retorna la resposta al requirent
						response.setStatus(proxyResponseCode);
						for (Header header: headerArrayResponse) {
							if (header.getName().equals("Transfer-Encoding") && header.getValue().equals("chunked"))
								continue;
							response.setHeader(
									header.getName(),
									header.getValue());
						}
						response.getOutputStream().write(postMethod.getResponseBody());
					} catch (Exception ex) {
						// Prepara un SOAPFault com a resposta
						String errorDesc = ex.getClass().getName() + ": " + ex.getLocalizedMessage();
						resultat.setError(true);
						resultat.setErrorCodi("0502");
						resultat.setErrorDescripcio("[EMISERV] Error retornant la resposta d'una petició POST a l'enrutador: " + errorDesc);
						logger.error(
								"Error retornant la resposta d'una petició POST a l'enrutador (" +
								"peticioId=" + resultat.getAtributPeticioId() + ", " +
								"serveiCodi=" + resultat.getAtributCodigoCertificado() + "): " + errorDesc);
						String soapFault = redireccioService.generarSoapFault(resultat);
						try {
							redireccioService.processarResposta(
									resultat.getAtributPeticioId(),
									resultat.getAtributCodigoCertificado(),
									soapFault.getBytes(),
									resultat.getEntitatCodiRedireccio());
							response.getOutputStream().write(soapFault.getBytes());
						} catch (Exception ex2) {
							logger.error("Error processant el resultat d'error: " + ex.getLocalizedMessage());
							ex.printStackTrace();
						}
					}
				} else if (request.getMethod().equalsIgnoreCase("GET")) {
					logger.debug(
							"Retornant resposta final d'una petició GET a l'enrutador (" +
							"resposta=" + new String(respostaBytes) + ")");
					response.setStatus(proxyResponseCode);
					response.getOutputStream().write(respostaBytes);
				}
			} catch (Exception ex) {
				logger.error(
						"Error retornant la resposta d'una petició a l'enrutador" +
						getParametresPerExcepcio(proxyUrl, resultat),
						ex);
			}
		}
	}

	/**
	 * Mètode per fer la petició a la url proxyUrl
	 * @param method
	 * @param resultat
	 * @return Retorna el proxyResponseCode d'executar el mètode.
	 * @throws IOException
	 * @throws ServletException
	 */
	private int executeProxyRequest(
			HttpMethod method,
			RedireccioProcessarResultatDto resultat) throws IOException, ServletException {
		HttpClient httpClient = new HttpClient();
		method.setFollowRedirects(false);
		// Per establir el timeout a 30 segons: method.getParams().setSoTimeout(30000);
//		method.getParams().setSoTimeout(2000);
//		httpClient.getParams().setSoTimeout(2000);
//		httpClient.setConnectionTimeout(2000);
//		httpClient.setTimeout(2000);
		int proxyResponseCode = -1;
		try {
			proxyResponseCode = httpClient.executeMethod(method);
		} catch (Exception ex) {
			logger.error(
					"Error fent la petició al emisor SCSP destí " +
					getParametresPerExcepcio(method.getURI().toString(), resultat),
					ex);
			if (resultat != null) {
				resultat.setError(true);
				resultat.setErrorCodi("0502");
				resultat.setErrorDescripcio("[EMISERV] Error fent la petició al emisor SCSP destí: " + ex.getMessage());
			}
		}
		return proxyResponseCode;
	}

	@SuppressWarnings("rawtypes")
	private void setProxyRequestHeaders(
			HttpServletRequest request,
			HttpMethod method,
			String proxyUrl) {
		Enumeration enumerationOfHeaderNames = request.getHeaderNames();
		while (enumerationOfHeaderNames.hasMoreElements()) {
			String headerName = (String)enumerationOfHeaderNames.nextElement();
			if (headerName.equalsIgnoreCase(STRING_CONTENT_LENGTH_HEADER_NAME))
				continue;
			Enumeration headerValues = request.getHeaders(headerName);
			while (headerValues.hasMoreElements()) {
				String headerValue = (String)headerValues.nextElement();
				if (headerName.equalsIgnoreCase(STRING_HOST_HEADER_NAME)) {
					headerValue = getProxyHostAndPort(proxyUrl);
				}
				Header header = new Header(headerName, headerValue);
				method.setRequestHeader(header);
			}
		}
	}

	private String getProxyUrl(
			HttpServletRequest request,
			String urlDesti) throws IOException {
		StringBuilder proxyUrl = new StringBuilder();
		if (urlDesti == null) {
			// Si urlDesti és null vol dir que la petició que s'està processant
			// és del tipus HTTP GET i, per tant, s'està consultant el WSDL del
			// servei web. Per tant, redirigim la petició cap al servei de l'emissor
			// SCSP.
			proxyUrl.append(request.getScheme());
			proxyUrl.append("://");
			proxyUrl.append(request.getServerName());
			proxyUrl.append(":");
			proxyUrl.append(request.getServerPort());
			proxyUrl.append("/emiservapi/externa");
			proxyUrl.append("/services/");
		} else {
			// Si la urlDesti no és null vol dir que la petició que s'està processant
			// és del tipus HTTP POST i, per tant, és una petició SOAP al servei web.
			proxyUrl.append(urlDesti);
		}
		if (proxyUrl.lastIndexOf("/") == proxyUrl.length() - 1) {
			proxyUrl.append(request.getServletPath().substring(SERVLET_PATH_PREFIX.length() + 1));
		}
		if (request.getQueryString() != null) {
			proxyUrl.append("?");
			proxyUrl.append(request.getQueryString());
		}
		return proxyUrl.toString();
	}

	private String getProxyHostAndPort(String proxyUrl) {
		int slashIndex;
		if (proxyUrl.contains("//")) {
			slashIndex = proxyUrl.indexOf("/", proxyUrl.indexOf("//") + "//".length());
		} else {
			slashIndex = proxyUrl.indexOf("/");
		}
		if (slashIndex > 0) {
			return proxyUrl.substring(0, proxyUrl.indexOf("/"));
		} else {
			return proxyUrl;
		}
	}

	private String getParametresPerExcepcio(
			String proxyUrl,
			RedireccioProcessarResultatDto resultat) {
		if (resultat != null) {
			return "(tipusPeticio=POST, " +
					"codigoCertificado=" + resultat.getAtributCodigoCertificado() + ", " +
					"peticioId=" + resultat.getAtributPeticioId() + ", " +
					"urlDesti=" + proxyUrl + ")";
		} else {
			return "(tipusPeticio=GET, " +
					"url=" + proxyUrl + ")";
		}
	}

	private void copiarCapsaleresHttp(
			HttpServletRequest request,
			PostMethod postMethod,
			String proxyUrl) throws MalformedURLException {
		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String header = headers.nextElement();
			String value;
			if (header.equalsIgnoreCase("host")) {
				URL url = new URL(proxyUrl);
				int port = url.getPort();
				value = url.getHost() + ((port != -1) ? ":" + port : "");
			} else {
				value = request.getHeader(header);
			}
			postMethod.setRequestHeader(
					header,
					value);
			logger.debug("Copiant capçalera HTTP (name=" + header + ", value=" + request.getHeader(header) + ")");
		}
	}

	/*
	 * Classe per retornar el resultat d'una petició d'enrutament des del thread d'enrutament
	 * múltiple.
	 */
	public class EnrutamentMultipleThreadResult {		
		private String codiEntitat;
		private int proxyResponseCode = -1;
		private PostMethod method;
		private RedireccioProcessarResultatDto resultat;
		/** XML del body de la resposta. Com que pot venir comprimit es descomprimeix una sola vegada
		 * pel seu processament.
		 */
		private byte[] xml;
		
		public String getCodiEntitat() {
			return codiEntitat;
		}
		public void setCodiEntitat(String codiEntitat) {
			this.codiEntitat = codiEntitat;
		}
		public int getProxyResponseCode() {
			return proxyResponseCode;
		}
		public void setProxyResponseCode(int proxyResponseCode) {
			this.proxyResponseCode = proxyResponseCode;
		}
		public PostMethod getMethod() {
			return method;
		}
		public void setMethod(PostMethod method) {
			this.method = method;
		}
		public RedireccioProcessarResultatDto getResultat() {
			return resultat;
		}
		public void setResultat(RedireccioProcessarResultatDto resultat) {
			this.resultat = resultat;
		}
		public byte[] getXml() {
			return xml;
		}
		public void setXml(byte[] xml) {
			this.xml = xml;
		}
	}

	/**
	 * Classe per llençar una petició en paral·lel cap a un emissor de l'enrutament múltiple.
	 */
	public class EnrutamentMultipleThread implements Callable<EnrutamentMultipleThreadResult> {
		String codiEntitat;
		String urlDesti;
		RequestEntity requestEntity;
		HttpServletRequest request;
		RedireccioProcessarResultatDto resultat;
		public EnrutamentMultipleThread(
				HttpServletRequest request,
				RequestEntity requestEntity,
				String codiEntitat,
				String urlDesti,
				RedireccioProcessarResultatDto resultat) {
			this.request = request;
			this.requestEntity = requestEntity;
			this.codiEntitat = codiEntitat;
			this.urlDesti = urlDesti;
			this.resultat = resultat;
		}
		@Override
		public EnrutamentMultipleThreadResult call() throws Exception {
//			// MOCK
//			if (urlDesti.startsWith("mock://")) {
//				return mockResponse();
//			}

			// REAL
			EnrutamentMultipleThreadResult ret = new EnrutamentMultipleThreadResult();
			ret.setCodiEntitat(this.codiEntitat);
			ret.setResultat(this.resultat);
			String proxyUrl = getProxyUrl(
					this.request,
					this.urlDesti);
			PostMethod method = new PostMethod(proxyUrl);
			copiarCapsaleresHttp(
					request,
					method,
					proxyUrl);
			method.setRequestEntity(this.requestEntity);
			ret.setMethod(method);			
			try {
				//executeProxyRequest
				logger.debug("Executant redirecció múltiple de la petició (" +
						"codiCertificat=" + resultat.getAtributCodigoCertificado() + ", " +
						"codiEntitat=" + codiEntitat + ", " +
						"proxyUrl=" + proxyUrl + ")");
				ret.setProxyResponseCode( 
						executeProxyRequest(method, resultat));
				Header[] headerArrayResponse = method.getResponseHeaders();
				String contentEncoding = "";
				for (Header header: headerArrayResponse) {
					if (header.getName().equals("Content-Encoding")) {
						contentEncoding = header.getValue();
						break;
					}
				}
				ret.setXml(contentEncoding.toLowerCase().contains("gzip") ?
								IOUtils.toByteArray( 
										new GZIPInputStream( 
												new ByteArrayInputStream(
														method.getResponseBody())))
								: method.getResponseBody());
			} catch (Exception ex) {
				logger.error(
						"Error fent la petició al emisor SCSP destí " +
						getParametresPerExcepcio(proxyUrl, resultat),
						ex);
				resultat.setError(true);
				resultat.setErrorCodi("0502");
				resultat.setErrorDescripcio("[EMISERV] Error fent la petició al emisor SCSP destí: " + ex.getMessage());
			}
			return ret;		
		}

//		MOCK
//		------------------------------------------------------------------------------------------------
//
//		private EnrutamentMultipleThreadResult mockResponse() throws MalformedURLException {
//			EnrutamentMultipleThreadResult ret = new EnrutamentMultipleThreadResult();
//			ret.setCodiEntitat(this.codiEntitat);
//			ret.setResultat(this.resultat);
//			PostMethod method = new PostMethod("http://localhost:8080/mockservices/peticionSincrona");
//			copiarCapsaleresHttp(request, method, "http://localhost:8080/mockservices/peticionSincrona");
//			method.setRequestEntity(this.requestEntity);
//			ret.setMethod(method);
//			ret.setProxyResponseCode(200);
//			ret.setXml(XML_MOCK.getBytes());
//			return ret;
//		}
	}

//	private static final String XML_MOCK = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//			"<soapenv:Envelope \n" +
//			"    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
//			"    <soapenv:Header>\n" +
//			"        <ds:Signature \n" +
//			"            xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
//			"            <ds:SignedInfo>\n" +
//			"                <ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"></ds:CanonicalizationMethod>\n" +
//			"                <ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></ds:SignatureMethod>\n" +
//			"                <ds:Reference URI=\"#MsgBody\">\n" +
//			"                    <ds:Transforms>\n" +
//			"                        <ds:Transform Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments\"></ds:Transform>\n" +
//			"                    </ds:Transforms>\n" +
//			"                    <ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></ds:DigestMethod>\n" +
//			"                    <ds:DigestValue>8WGpS/Dl88jmhGUef3tDQAMaL2M=</ds:DigestValue>\n" +
//			"                </ds:Reference>\n" +
//			"            </ds:SignedInfo>\n" +
//			"            <ds:SignatureValue>IeUTBJDPHn+0IlRFN4e1++AxCbhK2kCW5A/irUJOm3UaF+GIk8v3lMViyNIuqYU/za4cgjx3uAXpYSt7BvNz+wUTaWUc1V5mYMhNyw0Z8AvH2t9V0pC8db8X1F9lZhitUfEs5tmFqrGY/9ofsVtzVDHLUSizxqSlRWtEHmm4cSoTY+kSe6tKx3W3V86tmdOpmpTE6gq6UbFixiv3Y21+bLsh3lyekDUl77nzZKlpDbeojNG7H3NIeJIRv/6eooGuJ/wTd/5OZk3xhCIQYYgAfiiUXndaq0zF8I6eYio274981VfPFeuPnXgLmgHBdhkE8+kx+o1Xk0GfN5bHWL5VSQ==</ds:SignatureValue>\n" +
//			"            <ds:KeyInfo>\n" +
//			"                <ds:X509Data>\n" +
//			"                    <ds:X509Certificate>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</ds:X509Certificate>\n" +
//			"                </ds:X509Data>\n" +
//			"                <ds:KeyValue>\n" +
//			"                    <ds:RSAKeyValue>\n" +
//			"                        <ds:Modulus>wwK0I60l36otimE6iLqq5CHMck+ypHAAsYWXmfbklRAIt6hk5g1CeE5CbPWR/asqgiFQf/AoFnUqy9DTgF9BmJuHlCI2X69und5P7mqrZ5fLeMyXCnoWdq8UpMU41la58LdOASUuhuG4ok50ocCGAnUFBg+S1eC0eju8S5ZJa6ORevl1ZO0EVvdf6xOvPXa4hOt0rKomKGZduj5QPEWsQf0AqM7VJl3uEDK3nJffGswI0bo6+jU4FS/a3KI8aDjmcS53f+pX2vHYLRqgzK0/CGZ959PIBhXUeht0h/gLlCLmwhHTxoETZR191ASdJTtYjtoiHBUBGFVTJoNmpNSCSw==</ds:Modulus>\n" +
//			"                        <ds:Exponent>AQAB</ds:Exponent>\n" +
//			"                    </ds:RSAKeyValue>\n" +
//			"                </ds:KeyValue>\n" +
//			"            </ds:KeyInfo>\n" +
//			"        </ds:Signature>\n" +
//			"    </soapenv:Header>\n" +
//			"    <soapenv:Body Id=\"MsgBody\">\n" +
//			"        <Respuesta \n" +
//			"            xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta\">\n" +
//			"            <Atributos>\n" +
//			"                <IdPeticion>PINBAL00000000000000268652</IdPeticion>\n" +
//			"                <NumElementos>1</NumElementos>\n" +
//			"                <TimeStamp>2023-02-06T11:51:28.970+01:00</TimeStamp>\n" +
//			"                <Estado>\n" +
//			"                    <CodigoEstado>0003</CodigoEstado>\n" +
//			"                    <LiteralError>TRAMITADA</LiteralError>\n" +
//			"                    <TiempoEstimadoRespuesta>0</TiempoEstimadoRespuesta>\n" +
//			"                </Estado>\n" +
//			"                <CodigoCertificado>SVDSCTFNWS01</CodigoCertificado>\n" +
//			"            </Atributos>\n" +
//			"            <Transmisiones>\n" +
//			"                <TransmisionDatos>\n" +
//			"                    <DatosGenericos>\n" +
//			"                        <Emisor>\n" +
//			"                            <NifEmisor>S0711001H</NifEmisor>\n" +
//			"                            <NombreEmisor>Govern de les Illes Balears</NombreEmisor>\n" +
//			"                        </Emisor>\n" +
//			"                        <Solicitante>\n" +
//			"                            <IdentificadorSolicitante>S0711001H</IdentificadorSolicitante>\n" +
//			"                            <NombreSolicitante>Govern de les Illes Balears</NombreSolicitante>\n" +
//			"                            <UnidadTramitadora>IBAVI</UnidadTramitadora>\n" +
//			"                            <Procedimiento>\n" +
//			"                                <CodProcedimiento>CODSVDR_GBA_20121107</CodProcedimiento>\n" +
//			"                                <NombreProcedimiento>PRUEBAS DE INTEGRACION PARA GOBIERNO DE BALEARES</NombreProcedimiento>\n" +
//			"                            </Procedimiento>\n" +
//			"                            <Finalidad>CODSVDR_GBA_20121107#::##::#CODSVDR_GBA_20121107#::##::#test</Finalidad>\n" +
//			"                            <Consentimiento>Si</Consentimiento>\n" +
//			"                            <Funcionario>\n" +
//			"                                <NombreCompletoFuncionario>Nom Funcionari</NombreCompletoFuncionario>\n" +
//			"                                <NifFuncionario>00000000T</NifFuncionario>\n" +
//			"                            </Funcionario>\n" +
//			"                        </Solicitante>\n" +
//			"                        <Titular>\n" +
//			"                            <TipoDocumentacion>NIF</TipoDocumentacion>\n" +
//			"                            <Documentacion>12345678Z</Documentacion>\n" +
//			"                        </Titular>\n" +
//			"                        <Transmision>\n" +
//			"                            <CodigoCertificado>SVDSCTFNWS01</CodigoCertificado>\n" +
//			"                            <IdSolicitud>PINBAL00000000000000268652</IdSolicitud>\n" +
//			"                            <IdTransmision>TRSPINBAL00000000000000268652</IdTransmision>\n" +
//			"                            <FechaGeneracion>2023-02-06T08:55:16.301+01:00</FechaGeneracion>\n" +
//			"                        </Transmision>\n" +
//			"                    </DatosGenericos>\n" +
//			"                    <DatosEspecificos \n" +
//			"                        xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">\n" +
//			"                        <Retorno>\n" +
//			"                            <Estado>\n" +
//			"                                <CodigoEstado>0</CodigoEstado>\n" +
//			"                                <LiteralError>Petición correcta.</LiteralError>\n" +
//			"                            </Estado>\n" +
//			"                            <TituloFamiliaNumerosaRetorno>\n" +
//			"                                <CodigoComunidadAutonoma>07</CodigoComunidadAutonoma>\n" +
//			"                                <NumeroTitulo>000043</NumeroTitulo>\n" +
//			"                                <Categoria>G</Categoria>\n" +
//			"                                <TituloVigente>S</TituloVigente>\n" +
//			"                                <FechaExpedicion>11/05/2017</FechaExpedicion>\n" +
//			"                                <FechaCaducidad>11/05/2024</FechaCaducidad>\n" +
//			"                                <NumeroHijos>3</NumeroHijos>\n" +
//			"                            </TituloFamiliaNumerosaRetorno>\n" +
//			"                            <ListaBeneficiariosRetorno>\n" +
//			"                                <BeneficiarioRetorno>\n" +
//			"                                    <TipoDocumentacion>NIF</TipoDocumentacion>\n" +
//			"                                    <Documentacion>24255536N</Documentacion>\n" +
//			"                                    <FechaNacimiento>20/03/1971</FechaNacimiento>\n" +
//			"                                    <Nombre>FRANCISCO MANUEL</Nombre>\n" +
//			"                                    <Apellido1>CASTRO</Apellido1>\n" +
//			"                                    <Apellido2>JIMÉNEZ</Apellido2>\n" +
//			"                                    <Titular>S</Titular>\n" +
//			"                                </BeneficiarioRetorno>\n" +
//			"                                <BeneficiarioRetorno>\n" +
//			"                                    <TipoDocumentacion>NIF</TipoDocumentacion>\n" +
//			"                                    <Documentacion>41503905Z</Documentacion>\n" +
//			"                                    <FechaNacimiento>12/02/1975</FechaNacimiento>\n" +
//			"                                    <Nombre>M. CRISTINA</Nombre>\n" +
//			"                                    <Apellido1>BORDOY</Apellido1>\n" +
//			"                                    <Apellido2>MARTÍNEZ</Apellido2>\n" +
//			"                                    <Titular>S</Titular>\n" +
//			"                                </BeneficiarioRetorno>\n" +
//			"                                <BeneficiarioRetorno>\n" +
//			"                                    <TipoDocumentacion>NIF</TipoDocumentacion>\n" +
//			"                                    <Documentacion>41607979J</Documentacion>\n" +
//			"                                    <FechaNacimiento>24/06/2003</FechaNacimiento>\n" +
//			"                                    <Nombre>MARCOS</Nombre>\n" +
//			"                                    <Apellido1>MUÑOZ</Apellido1>\n" +
//			"                                    <Apellido2>BORDOY</Apellido2>\n" +
//			"                                    <Titular>N</Titular>\n" +
//			"                                </BeneficiarioRetorno>\n" +
//			"                                <BeneficiarioRetorno>\n" +
//			"                                    <TipoDocumentacion>NIF</TipoDocumentacion>\n" +
//			"                                    <Documentacion>41607012N</Documentacion>\n" +
//			"                                    <FechaNacimiento>07/02/2006</FechaNacimiento>\n" +
//			"                                    <Nombre>FRANCISCO</Nombre>\n" +
//			"                                    <Apellido1>CASTRO</Apellido1>\n" +
//			"                                    <Apellido2>DÍAZ</Apellido2>\n" +
//			"                                    <Titular>N</Titular>\n" +
//			"                                </BeneficiarioRetorno>\n" +
//			"                                <BeneficiarioRetorno>\n" +
//			"                                    <TipoDocumentacion>NIF</TipoDocumentacion>\n" +
//			"                                    <Documentacion>41638607M</Documentacion>\n" +
//			"                                    <FechaNacimiento>04/10/2010</FechaNacimiento>\n" +
//			"                                    <Nombre>ALEX</Nombre>\n" +
//			"                                    <Apellido1>CASTRO</Apellido1>\n" +
//			"                                    <Apellido2>DIAZ</Apellido2>\n" +
//			"                                    <Titular>N</Titular>\n" +
//			"                                </BeneficiarioRetorno>\n" +
//			"                            </ListaBeneficiariosRetorno>\n" +
//			"                        </Retorno>\n" +
//			"                    </DatosEspecificos>\n" +
//			"                </TransmisionDatos>\n" +
//			"            </Transmisiones>\n" +
//			"        </Respuesta>\n" +
//			"    </soapenv:Body>\n" +
//			"</soapenv:Envelope>";

	private static final Logger logger = LoggerFactory.getLogger(ScspRoutingController.class);

}
