/**
 * 
 */
package es.caib.emiserv.backoffice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import es.caib.emiserv.backoffice.util.DatosEspecificosHandler;
import es.caib.emiserv.backoffice.util.PropertiesHelper;
import es.caib.emiserv.backoffice.util.SpringApplicationContext;
import es.caib.emiserv.backoffice.util.WsClientHelper;
import es.caib.emiserv.backoffice.ws.EmiservBackoffice;
import es.caib.emiserv.core.api.dto.ServeiConfigScspDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.exception.NotActiveException;
import es.caib.emiserv.core.api.exception.NotFoundException;
import es.caib.emiserv.core.api.service.BackofficeService;
import es.caib.loginModule.auth.ControladorSesion;
import es.caib.loginModule.client.AuthorizationToken;
import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Respuesta;
import es.scsp.bean.common.SolicitudRespuesta;
import es.scsp.bean.common.SolicitudTransmision;
import es.scsp.bean.common.Transmision;
import es.scsp.bean.common.TransmisionDatos;
import es.scsp.common.backoffice.BackOffice;
import es.scsp.common.exceptions.ScspException;
import es.scsp.common.utils.DateUtils;

/**
 * Backoffice genèric per a accedir als backoffices emissors
 * desplegats a la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CaibBackofficeAntic implements BackOffice {

	public Respuesta NotificarSincrono(
			Peticion peticion) throws ScspException {
		LOGGER.debug("Processant petició síncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		Respuesta respuesta = notificarSincronoBackoffice(peticion);
		LOGGER.debug("Retornant resposta síncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		return respuesta;
	}

	public ConfirmacionPeticion NotificarAsincrono(
			Peticion peticion) throws ScspException {
		LOGGER.debug("Processant petició asíncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		ConfirmacionPeticion confirmacionPeticion = notificarAsincronoBackoffice(peticion);
		LOGGER.debug("Retornant resposta síncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		return confirmacionPeticion;
	}

	public Respuesta SolicitudRespuesta(
			SolicitudRespuesta solicitudRespuesta) throws ScspException {
		LOGGER.debug("Processant sol·licitud resposta " + getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + ".");
		Respuesta respuesta = solicitarRespuestaBackoffice(solicitudRespuesta);
		LOGGER.debug("Retornant sol·licitud resposta " + getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + ".");
		return respuesta;
	}



	private Respuesta notificarSincronoBackoffice(
			Peticion peticion) throws ScspException {
		try {
			modificarDatosEspecificosPeticio(peticion);
			DatosEspecificosHandler datosEspecificosHandler = new DatosEspecificosHandler();
			EmiservBackoffice emiservBackoffice = getEmiservBackoffice(
					getIdentificacioDelsAtributsScsp(peticion.getAtributos()),
					peticion.getAtributos().getCodigoCertificado(),
					datosEspecificosHandler);
			Respuesta respuesta = emiservBackoffice.peticionSincrona(peticion);
			if (respuesta.getTransmisiones() != null && respuesta.getTransmisiones().getTransmisionDatos() != null) {
				for (TransmisionDatos transmisionDatos: respuesta.getTransmisiones().getTransmisionDatos()) {
					if (transmisionDatos != null && transmisionDatos.getDatosGenericos() != null && transmisionDatos.getDatosGenericos().getTransmision() != null) {
						String idSolicitud = transmisionDatos.getDatosGenericos().getTransmision().getIdSolicitud();
						Element datosEspecificos = datosEspecificosHandler.getDatosEspecificosRespostaComElement(
								idSolicitud);
						if (datosEspecificos != null) {
							LOGGER.debug("Copiant dades específiques a la resposta(idSolicitud=" + idSolicitud + "): " + datosEspecificosHandler.getDatosEspecificosRespostaComString(idSolicitud) + ".");
							transmisionDatos.setDatosEspecificos(datosEspecificos);
						}
					}
				}
			}
			modificarDatosEspecificosResposta(
					respuesta,
					peticion.getAtributos().getCodigoCertificado());
			emplenarCampsBuits(respuesta);
	        return respuesta;
		} catch (Exception ex) {
			LOGGER.error("Error al invocar el mètode peticionSincrona del backoffice per a la petició " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()), ex);
			throw ScspException.getScspException(
					"0227",
					new String[] {ex.getMessage()});
		}
	}

	private ConfirmacionPeticion notificarAsincronoBackoffice(
			Peticion peticion) throws ScspException {
		try {
			EmiservBackoffice emiservBackoffice = getEmiservBackoffice(
					getIdentificacioDelsAtributsScsp(peticion.getAtributos()),
					peticion.getAtributos().getCodigoCertificado(),
					null);
			ConfirmacionPeticion confirmacionPeticion = emiservBackoffice.peticionAsincrona(peticion);
	        return confirmacionPeticion;
		} catch (Exception ex) {
			LOGGER.error("Error al invocar el mètode peticionAsincrona del backoffice per a la petició " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()), ex);
			throw ScspException.getScspException(
					"0227",
					new String[] {ex.getMessage()});
		}
	}

	private Respuesta solicitarRespuestaBackoffice(
			SolicitudRespuesta solicitudRespuesta) throws ScspException {
		try {
			DatosEspecificosHandler datosEspecificosHandler = new DatosEspecificosHandler();
			EmiservBackoffice emiservBackoffice = getEmiservBackoffice(
					getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()),
					solicitudRespuesta.getAtributos().getCodigoCertificado(),
					datosEspecificosHandler);
			Respuesta respuesta = emiservBackoffice.solicitarRespuesta(solicitudRespuesta);
			if (respuesta.getTransmisiones() != null && respuesta.getTransmisiones().getTransmisionDatos() != null) {
				for (TransmisionDatos transmisionDatos: respuesta.getTransmisiones().getTransmisionDatos()) {
					if (transmisionDatos != null && transmisionDatos.getDatosGenericos() != null && transmisionDatos.getDatosGenericos().getTransmision() != null) {
						String idSolicitud = transmisionDatos.getDatosGenericos().getTransmision().getIdSolicitud();
						Element datosEspecificos = datosEspecificosHandler.getDatosEspecificosRespostaComElement(
								idSolicitud);
						if (datosEspecificos != null) {
							LOGGER.debug("Copiant dades específiques a la reposta(idSolicitud=" + idSolicitud + "): " + datosEspecificosHandler.getDatosEspecificosRespostaComString(idSolicitud) + ".");
							transmisionDatos.setDatosEspecificos(datosEspecificos);
						}
					}
				}
			}
			modificarDatosEspecificosResposta(
					respuesta,
					solicitudRespuesta.getAtributos().getCodigoCertificado());
			emplenarCampsBuits(respuesta);
	        return respuesta;
		} catch (Exception ex) {
			LOGGER.error("Error al invocar el mètode solicitarRespuesta del backoffice per a la petició " + getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()), ex);
			throw ScspException.getScspException(
					"0227",
					new String[] {ex.getMessage()});
		}
	}

	private String getIdentificacioDelsAtributsScsp(Atributos atributos) {
		String codigoCertificado = "???";
		String idPeticion = "???";
		if (atributos != null) {
			codigoCertificado = atributos.getCodigoCertificado();
			idPeticion = atributos.getIdPeticion();
		}
		return "(codigoCertificado=" + codigoCertificado + ", idPeticion=" + idPeticion + ")";
	}

	private EmiservBackoffice getEmiservBackoffice(
			String identificacio,
			String codigoCertificado,
			DatosEspecificosHandler datosEspecificosHandler) throws Exception {
		LOGGER.debug("Cercant backoffice per a la petició " + identificacio + ".");
		try {
			ServeiDto servei = getServeiAmbCodi(codigoCertificado);
			if (!servei.isConfigurat()) {
				LOGGER.error("El servei està donat d'alta però encara no s'ha configurat (" +
						"idPeticion=" + identificacio + ", " +
						"codigoCertificado=" + codigoCertificado + ")");
				throw new Exception("El servei està donat d'alta però encara no s'ha configurat");
			}
			String backofficeUrl = servei.getBackofficeCaibUrl();
			LOGGER.debug("URL del servei del backoffice: " + backofficeUrl);
			String username = null;
			String password = null;
			switch(servei.getBackofficeCaibAutenticacio()) {
			case TOKEN_CAIB:
				ControladorSesion controlador = new ControladorSesion();
				controlador.autenticar(
						getBackofficePropertyUsername(codigoCertificado),
						getBackofficePropertyPassword(codigoCertificado));
				AuthorizationToken token = controlador.getToken();
				username = token.getUser();
				password = token.getPassword();
				break;
			case TEXT_CLAR:
				username = getBackofficePropertyUsername(codigoCertificado);
				password = getBackofficePropertyPassword(codigoCertificado);
				break;
			case NINGUNA:
			default:
				break;
			}
			EmiservBackoffice port = new WsClientHelper<EmiservBackoffice>().generarClientWs(
					getClass().getResource("/es/caib/emiserv/backoffice/EmiservBackoffice.wsdl"),
					backofficeUrl,
					new QName(
							"http://caib.es/emiserv/backoffice",
							"EmiservBackofficeService"),
					username,
					password,
					getBackofficePropertySoapAction(codigoCertificado),
					EmiservBackoffice.class,
					datosEspecificosHandler);
			return port;
		} catch (NotFoundException ex) {
			LOGGER.error("No s'ha trobat la informació del servei (" +
					"idPeticion=" + identificacio + ", " +
					"codigoCertificado=" + codigoCertificado + ")");
			throw new Exception("No s'ha trobat la configuració del servei per a la petició");
		} catch (NotActiveException ex) {
			LOGGER.error("El servei no està actiu (" +
					"idPeticion=" + identificacio + ", " +
					"codigoCertificado=" + codigoCertificado + ")");
			throw new Exception("El servei no es troba actiu");
		}
	}

	private ServeiDto getServeiAmbCodi(
			String serveiCodi) {
		BackofficeService backofficeService = SpringApplicationContext.getBean(BackofficeService.class);
		return backofficeService.serveiFindByCodi(serveiCodi);
	}

	private ServeiConfigScspDto getConfiguracioServei(
			String serveiCodi) {
		BackofficeService backofficeService = SpringApplicationContext.getBean(BackofficeService.class);
		return backofficeService.serveiFindConfiguracioScsp(serveiCodi);
	}

	private void modificarDatosEspecificosPeticio(
			Peticion peticion) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException, SAXException, IOException {
		// Elimina els atributs xmlns de l'element DatosEspecificos
		for (SolicitudTransmision solicitudTransmision: peticion.getSolicitudes().getSolicitudTransmision()) {
			if (solicitudTransmision.getDatosEspecificos() != null) {
				Element datosEspecificos = (Element)solicitudTransmision.getDatosEspecificos();
				String datosEspecificosText = nodeToString(datosEspecificos);
				String token = "<DatosEspecificos";
				int indexInici = datosEspecificosText.indexOf(token);
				if (indexInici != -1) {
					indexInici += token.length();
				} else {
					token = "<datosEspecificos";
					indexInici = datosEspecificosText.indexOf(token);
					if (indexInici != -1) {
						indexInici += token.length();
					}
				}
				if (indexInici != -1) {
					int indexFi = datosEspecificosText.indexOf(">", indexInici);
					String datosEspecificosProcessat = datosEspecificosText.substring(0, indexInici) + datosEspecificosText.substring(indexFi);
					solicitudTransmision.setDatosEspecificos(
							stringToElement(datosEspecificosProcessat));
				}
			}
		}
	}

	private void modificarDatosEspecificosResposta(
			Respuesta respuesta,
			String serveiCodi) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException, SAXException, IOException {
		for (TransmisionDatos transmisionDatos: respuesta.getTransmisiones().getTransmisionDatos()) {
			if (transmisionDatos.getDatosEspecificos() != null) {
				Element datosEspecificos = (Element)transmisionDatos.getDatosEspecificos();
				String datosEspecificosText = nodeToString(datosEspecificos);
				String token1 = "DatosEspecificos";
				String token2 = "datosEspecificos";
				int indexDatespInici = datosEspecificosText.indexOf(token1);
				int indexDatespFi = 0;
				if (indexDatespInici != -1) {
					indexDatespFi = indexDatespInici + token1.length();
				} else {
					indexDatespInici = datosEspecificosText.indexOf(token2);
					if (indexDatespInici != -1) {
						indexDatespFi = indexDatespInici + token2.length();
					}
				}
				int indexFi = datosEspecificosText.indexOf(">");
				if (indexDatespInici != -1) {
					String datosEspecificosAmbNs1 = datosEspecificosText.substring(1, indexDatespInici) + token1;
					StringBuilder datosEspecificosSb = new StringBuilder();
					datosEspecificosSb.append("<");
					datosEspecificosSb.append(datosEspecificosAmbNs1);
					int indexXmlns = datosEspecificosText.indexOf("xmlns");
					int indexXmlnsDatesp = datosEspecificosText.indexOf("esquemas/datosespecificos");
					boolean conteXmlns = indexXmlns != -1 && indexXmlnsDatesp != -1 && indexXmlnsDatesp < indexFi;
					if (!conteXmlns) {
						String xmlns;
						ServeiConfigScspDto config = getConfiguracioServei(serveiCodi);
						if (config.getVersionEsquema().contains("2")) {
							xmlns = " xmlns=\"http://www.map.es/scsp/esquemas/datosespecificos\"";
						} else if (config.getVersionEsquema().contains("3")) {
							xmlns = " xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\"";
						} else {
							xmlns = "";
						}
						datosEspecificosSb.append(xmlns);
					}
					int indexDatespBarraInici = datosEspecificosText.indexOf("/" + datosEspecificosAmbNs1);
					if (indexDatespBarraInici == -1) {
						String datosEspecificosAmbNs2 = datosEspecificosText.substring(1, indexDatespInici) + token2;
						indexDatespBarraInici = datosEspecificosText.indexOf("/" + datosEspecificosAmbNs2);
					}
					datosEspecificosSb.append(datosEspecificosText.substring(indexDatespFi, indexDatespBarraInici + 1));
					datosEspecificosSb.append(datosEspecificosAmbNs1);
					datosEspecificosSb.append(">");
					transmisionDatos.setDatosEspecificos(
							stringToElement(datosEspecificosSb.toString()));
				}
			}
		}
	}

	private void emplenarCampsBuits(
			Respuesta respuesta) {
		if (respuesta.getTransmisiones() != null && respuesta.getTransmisiones().getTransmisionDatos() != null) {
			for (TransmisionDatos transmisionDatos: respuesta.getTransmisiones().getTransmisionDatos()) {
				if (transmisionDatos.getDatosGenericos() != null && transmisionDatos.getDatosGenericos().getTransmision() != null) {
					Transmision transmision = transmisionDatos.getDatosGenericos().getTransmision();
					if (transmision.getFechaGeneracion() == null || transmision.getFechaGeneracion().isEmpty()) {
						transmision.setFechaGeneracion(
								DateUtils.parseISO8601(new Date()));
					}
					if (transmision.getIdTransmision() == null || transmision.getIdTransmision().isEmpty()) {
						transmision.setIdTransmision(transmision.getIdSolicitud());
					}
				}
			}
		}
	}

	private String nodeToString(Node node) throws TransformerFactoryConfigurationError, TransformerException {
		if (node == null)
			return null;
		StringWriter writer = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new DOMSource(node), new StreamResult(writer));
		return writer.toString();
	}
	public Element stringToElement(String xml) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		return document.getDocumentElement();
	}

	public String getBackofficePropertyUsername(String serveiCodi) {
		String username = PropertiesHelper.getProperties().getProperty(
				"es.caib.emiserv.backoffice." + serveiCodi + ".caib.auth.username");
		if (username == null) {
			username = PropertiesHelper.getProperties().getProperty(
					"es.caib.emiserv.backoffice.caib.auth.username");
		}
		return username;
	}
	public String getBackofficePropertyPassword(String serveiCodi) {
		String password = PropertiesHelper.getProperties().getProperty(
				"es.caib.emiserv.backoffice." + serveiCodi + ".caib.auth.password");
		if (password == null) {
			password = PropertiesHelper.getProperties().getProperty(
					"es.caib.emiserv.backoffice.caib.auth.password");
		}
		return password;
	}
	public String getBackofficePropertySoapAction(String serveiCodi) {
		String soapAction = PropertiesHelper.getProperties().getProperty(
				"es.caib.emiserv.backoffice." + serveiCodi + ".caib.soap.action");
		if (soapAction == null) {
			soapAction = PropertiesHelper.getProperties().getProperty(
					"es.caib.emiserv.backoffice.caib.soap.action");
		}
		return soapAction;
	}
	private static final Logger LOGGER = LoggerFactory.getLogger(CaibBackoffice.class);

}
