/**
 *
 */
package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.dto.BackofficeAutenticacioTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiProvaDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudTransmision;
import es.caib.emiserv.logic.resolver.EntitatResolver;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.persist.repository.ServeiRutaDestiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;

/**
 * Resol el backoffice al qual s'ha d'enviar una petició:
 *
 * - Serveis de tipus BACKOFFICE: el backoffice configurat al servei.
 * - Serveis enrutadors: el backoffice del cedent configurat a la ruta de l'entitat que resol el
 *   resolutor d'entitats del servei (rutes de tipus BACKOFFICE).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class BackofficeDestiHelper {

	static final String NS_PETICION = "http://intermediacion.redsara.es/scsp/esquemas/V3/peticion";
	static final String NS_DATOS_ESPECIFICOS = "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";
	public static final String PROPIETAT_USUARI = "caib.auth.username";
	public static final String PROPIETAT_SECRET = "caib.auth.password";
	private static final int TIMEOUT_PROVA_MS = 10000;

	@Autowired
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	@Autowired
	private Environment environment;

	public BackofficeDesti getDesti(
			ServeiEntity servei,
			Peticion peticion) {
		if (!servei.isEnrutador()) {
			if (!servei.isConfigurat()) {
				throw new ValidationException("El servei (codi=" + servei.getCodi() + ") està donat d'alta però encara no s'ha configurat");
			}
			return new BackofficeDesti(
					servei.getCodi(),
					null,
					servei.getBackofficeCaibUrl(),
					servei.getBackofficeCaibAutenticacio());
		}
		if (ServeiTipusEnumDto.ENRUTADOR_MULTIPLE.equals(servei.getTipus())) {
			// Els enrutadors múltiples consulten tots els backoffices de les rutes (getDestinsEnrutadorMultiple)
			throw new ValidationException("Els enrutadors múltiples no tenen un únic backoffice de destí (codi=" + servei.getCodi() + ")");
		}
		if (peticion == null) {
			throw new ValidationException("No es pot resoldre la ruta del servei enrutador (codi=" + servei.getCodi() + ") sense la petició");
		}
		String entitatCodi = resoldreEntitat(servei, peticion);
		ServeiRutaDestiEntity ruta = serveiRutaDestiRepository.findByServeiAndEntitatCodi(
				servei,
				entitatCodi);
		if (ruta == null) {
			throw new ValidationException("No s'ha trobat cap ruta per a l'entitat (servei=" + servei.getCodi() + ", entitat=" + entitatCodi + ")");
		}
		if (!ruta.isDestiBackoffice()) {
			throw new ValidationException("La ruta de l'entitat no és de tipus BACKOFFICE (servei=" + servei.getCodi() + ", entitat=" + entitatCodi + ")");
		}
		return new BackofficeDesti(
				servei.getCodi(),
				entitatCodi,
				ruta.getUrl(),
				ruta.getBackofficeAutenticacio());
	}

	/**
	 * Propietat de configuració del backoffice, de la més específica a la més general:
	 * es.caib.emiserv.backoffice.[SERVEI.[ENTITAT.]]nom
	 */
	public String getPropietat(BackofficeDesti desti, String nom) {
		for (String prefix: desti.getPrefixosPropietats()) {
			String valor = environment.getProperty(prefix + nom);
			if (valor != null) {
				return valor;
			}
		}
		return null;
	}

	/** Indica si estan configurades les credencials que necessita l'autenticació del backoffice. */
	public boolean isCredencialsConfigurades(BackofficeDesti desti) {
		if (BackofficeAutenticacioTipusEnumDto.NINGUNA.equals(desti.getAutenticacio())) {
			return true;
		}
		return getPropietat(desti, PROPIETAT_USUARI) != null && getPropietat(desti, PROPIETAT_SECRET) != null;
	}

	/**
	 * Comprova la connexió amb el destí d'una ruta consultant-ne el WSDL (el servei web del backoffice
	 * o el de l'emisor SCSP del cedent). Si l'autenticació és en text clar s'envien les credencials.
	 */
	public ServeiRutaDestiProvaDto provarConnexio(BackofficeDesti desti, boolean destiBackoffice) {
		long inici = System.currentTimeMillis();
		ServeiRutaDestiProvaDto prova = new ServeiRutaDestiProvaDto();
		prova.setUrl(desti.getUrl() + (desti.getUrl().contains("?") ? "&" : "?") + "wsdl");
		HttpURLConnection connexio = null;
		try {
			connexio = (HttpURLConnection)new URL(prova.getUrl()).openConnection();
			connexio.setConnectTimeout(TIMEOUT_PROVA_MS);
			connexio.setReadTimeout(TIMEOUT_PROVA_MS);
			if (destiBackoffice && BackofficeAutenticacioTipusEnumDto.TEXT_CLAR.equals(desti.getAutenticacio()) && isCredencialsConfigurades(desti)) {
				String credencials = getPropietat(desti, PROPIETAT_USUARI) + ":" + getPropietat(desti, PROPIETAT_SECRET);
				connexio.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(credencials.getBytes(StandardCharsets.UTF_8)));
			}
			int codiHttp = connexio.getResponseCode();
			prova.setCodiHttp(codiHttp);
			String contingut = codiHttp < 400 ? llegir(connexio.getInputStream()) : "";
			boolean esWsdl = contingut.contains("definitions");
			boolean esBackoffice = contingut.contains("EmiservBackoffice");
			prova.setCorrecta(codiHttp == 200 && esWsdl && (!destiBackoffice || esBackoffice));
			if (prova.isCorrecta()) {
				prova.setMissatge(destiBackoffice ? "El servei web EmiservBackoffice respon correctament" : "L'emisor SCSP respon correctament");
			} else if (codiHttp != 200) {
				prova.setMissatge("El destí ha respost amb el codi HTTP " + codiHttp);
			} else if (!esWsdl) {
				prova.setMissatge("El destí no ha retornat cap WSDL");
			} else {
				prova.setMissatge("El WSDL retornat no és el del servei web EmiservBackoffice");
			}
		} catch (Exception ex) {
			prova.setCorrecta(false);
			prova.setMissatge("No s'ha pogut connectar amb el destí: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
		} finally {
			if (connexio != null) {
				connexio.disconnect();
			}
		}
		if (prova.isCorrecta() && destiBackoffice && !isCredencialsConfigurades(desti)) {
			prova.setCredencialsPendents(true);
			prova.setMissatge(prova.getMissatge() + ", però falten les credencials (" + desti.getPrefixosPropietats()[0] + "caib.auth.username/password)");
		}
		prova.setTempsMs(System.currentTimeMillis() - inici);
		return prova;
	}

	private static String llegir(InputStream is) throws Exception {
		try (InputStream entrada = is) {
			return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	/**
	 * Backoffices de les rutes de tipus BACKOFFICE d'un enrutador múltiple, en l'ordre de preferència
	 * de les rutes (el que fa servir el resolutor de respostes en cas d'empat).
	 */
	public List<BackofficeDesti> getDestinsEnrutadorMultiple(ServeiEntity servei) {
		List<BackofficeDesti> destins = new ArrayList<BackofficeDesti>();
		for (ServeiRutaDestiEntity ruta: serveiRutaDestiRepository.findByServeiOrderByOrdreAsc(servei)) {
			if (ruta.isDestiBackoffice()) {
				destins.add(new BackofficeDesti(
						servei.getCodi(),
						ruta.getEntitatCodi(),
						ruta.getUrl(),
						ruta.getBackofficeAutenticacio()));
			}
		}
		if (destins.isEmpty()) {
			throw new ValidationException("El servei enrutador múltiple no té cap ruta de tipus BACKOFFICE (codi=" + servei.getCodi() + ")");
		}
		return destins;
	}

	private String resoldreEntitat(
			ServeiEntity servei,
			Peticion peticion) {
		if (servei.getResolverClass() == null) {
			throw new ValidationException("El servei enrutador no té configurat cap resolutor d'entitats (codi=" + servei.getCodi() + ")");
		}
		try {
			@SuppressWarnings("unchecked")
			Class<EntitatResolver> resolverClass = (Class<EntitatResolver>)Class.forName(servei.getResolverClass());
			EntitatResolver resolver = resolverClass.getDeclaredConstructor().newInstance();
			String entitatCodi = resolver.resolve(crearPeticionScsp(peticion));
			log.debug("Entitat resolta per a la petició (servei=" + servei.getCodi() + ", idPeticion=" + peticion.getAtributos().getIdPeticion() + ", entitat=" + entitatCodi + ")");
			return entitatCodi;
		} catch (ValidationException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ValidationException("No s'ha pogut resoldre l'entitat de la petició (servei=" + servei.getCodi() + "): " + ex.getMessage());
		}
	}

	/**
	 * Els resolutors d'entitats treballen sobre la petició SCSP (Peticion/Solicitudes/SolicitudTransmision/DatosEspecificos).
	 * Es construeix aquesta estructura amb les dades específiques de la primera sol·licitud.
	 */
	static Element crearPeticionScsp(Peticion peticion) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document document = dbf.newDocumentBuilder().newDocument();
		Element peticionElement = document.createElementNS(NS_PETICION, "Peticion");
		document.appendChild(peticionElement);
		Element solicitudes = document.createElementNS(NS_PETICION, "Solicitudes");
		peticionElement.appendChild(solicitudes);
		Element solicitudTransmision = document.createElementNS(NS_PETICION, "SolicitudTransmision");
		solicitudes.appendChild(solicitudTransmision);
		Node datosEspecificos = getDatosEspecificos(peticion);
		if (datosEspecificos != null) {
			solicitudTransmision.appendChild(document.importNode(datosEspecificos, true));
		}
		return peticionElement;
	}

	private static Node getDatosEspecificos(Peticion peticion) throws Exception {
		if (peticion.getSolicitudes() == null || peticion.getSolicitudes().getSolicitudTransmision() == null || peticion.getSolicitudes().getSolicitudTransmision().isEmpty()) {
			return null;
		}
		SolicitudTransmision solicitud = peticion.getSolicitudes().getSolicitudTransmision().get(0);
		Object datosEspecificos = solicitud.getDatosEspecificos();
		if (datosEspecificos instanceof Document) {
			return ((Document)datosEspecificos).getDocumentElement();
		} else if (datosEspecificos instanceof Node) {
			return (Node)datosEspecificos;
		} else if (datosEspecificos instanceof String) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			return dbf.newDocumentBuilder().parse(new InputSource(new StringReader((String)datosEspecificos))).getDocumentElement();
		}
		return null;
	}

	/** Backoffice al qual s'envia la petició. */
	public static class BackofficeDesti {
		private final String serveiCodi;
		private final String entitatCodi;
		private final String url;
		private final BackofficeAutenticacioTipusEnumDto autenticacio;
		public BackofficeDesti(
				String serveiCodi,
				String entitatCodi,
				String url,
				BackofficeAutenticacioTipusEnumDto autenticacio) {
			this.serveiCodi = serveiCodi;
			this.entitatCodi = entitatCodi;
			this.url = url;
			this.autenticacio = autenticacio != null ? autenticacio : BackofficeAutenticacioTipusEnumDto.NINGUNA;
		}
		public String getServeiCodi() {
			return serveiCodi;
		}
		/** Entitat de la ruta (només per als serveis enrutadors). */
		public String getEntitatCodi() {
			return entitatCodi;
		}
		public String getUrl() {
			return url;
		}
		public BackofficeAutenticacioTipusEnumDto getAutenticacio() {
			return autenticacio;
		}
		/**
		 * Prefixos de les propietats de configuració del backoffice, del més específic al més general:
		 * es.caib.emiserv.backoffice.SERVEI.ENTITAT., es.caib.emiserv.backoffice.SERVEI., es.caib.emiserv.backoffice.
		 */
		public String[] getPrefixosPropietats() {
			if (entitatCodi != null) {
				return new String[] {
						"es.caib.emiserv.backoffice." + serveiCodi + "." + entitatCodi + ".",
						"es.caib.emiserv.backoffice." + serveiCodi + ".",
						"es.caib.emiserv.backoffice."};
			}
			return new String[] {
					"es.caib.emiserv.backoffice." + serveiCodi + ".",
					"es.caib.emiserv.backoffice."};
		}
		@Override
		public String toString() {
			return "BackofficeDesti [serveiCodi=" + serveiCodi + ", entitatCodi=" + entitatCodi + ", url=" + url + ", autenticacio=" + autenticacio + "]";
		}
	}

}
