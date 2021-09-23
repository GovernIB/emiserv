/**
 * 
 */
package es.caib.emiserv.back.controller;

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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import es.caib.emiserv.logic.intf.dto.RedireccioProcessarResultatDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.service.RedireccioService;

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
					logger.debug("Iniciant " + resultat.getUrlDestins().size() + " peticions múltiples en paral·lel pel servei " + resultat.getAtributCodigoCertificado() );
					List<Future<EnrutamentMultipleThreadResult>> respostesThreadsPeticio = null;
					try {
						respostesThreadsPeticio = service.invokeAll(peticionsMultiples);
					} catch(Exception err) {
						err.printStackTrace();
					}
					service.shutdown();
					logger.debug("Peticions múltiples en paral·lel pel servei " + resultat.getAtributCodigoCertificado() + " finalitzades.");
					Map<String, byte[]> xmlsPerEscollir = new HashMap<String, byte[]>();
					Map<String, EnrutamentMultipleThreadResult> respostesPeticions = new HashMap<String, ScspRoutingController.EnrutamentMultipleThreadResult>();
					if (respostesThreadsPeticio != null) {
						for (Future<EnrutamentMultipleThreadResult> r: respostesThreadsPeticio) {
							try {
								// Guarda la petició per informar la resposta
								respostesPeticions.put(r.get().codiEntitat, r.get());
								// Prepara les respostes per a escollir-ne una
								xmlsPerEscollir.put(r.get().codiEntitat, r.get().getXml());
							} catch (Exception e) {
								logger.error("Error inesperat processant les respostes de les peticions d'enrutament múltiples");
								e.printStackTrace();
							}
						}
					}
					// Processar les respostes amb el mètode del servei d'enrutament
					String respostaEscollida = redireccioService.escollirResposta(
							resultat,
							xmlsPerEscollir);
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
						soapFault.getBytes());
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
								respostaBytes);
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
									soapFault.getBytes());
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
			EnrutamentMultipleThreadResult ret = new EnrutamentMultipleThreadResult();
			ret.setCodiEntitat(this.codiEntitat);
			ret.setResultat(this.resultat);
			String proxyUrl = getProxyUrl(this.request,
										  this.urlDesti);
			PostMethod method = new PostMethod(proxyUrl);
			copiarCapsaleresHttp(	request,
									method,
									proxyUrl);
			method.setRequestEntity(this.requestEntity);
			ret.setMethod(method);			
			try {
				//executeProxyRequest
				logger.debug("Executant redirecció múltiple de la petició (codiCertificat=" + resultat.getAtributCodigoCertificado() + 
						 ",codiEntitat=" + codiEntitat + 
						 ", proxyUrl=" + proxyUrl + ")");			
				ret.setProxyResponseCode( 
						executeProxyRequest(method, resultat));
								
				Header[] headerArrayResponse = method.getResponseHeaders();
				String contentEncoding = "";
				for (Header header: headerArrayResponse) 
					if (header.getName().equals("Content-Encoding")) {
						contentEncoding = header.getValue();
						break;
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
	}

	private static final Logger logger = LoggerFactory.getLogger(ScspRoutingController.class);

}
