package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.dto.ServeiFakeResultatEnumDto;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.scsp.fake.core.FakeRequest;
import es.caib.emiserv.scsp.fake.core.FakeResponses;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Activació del mode fake dels serveis i generació de les dades específiques de les
 * respostes fake.
 * <p>
 * Les plantilles de dades específiques poden contenir marcadors {@code ${nom}} o
 * {@code ${nom:valor per defecte}} (el valor per defecte s'aplica si la petició no informa el
 * camp). Els serveis coneguts tenen una plantilla per defecte, vàlida segons l'XSD del servei, a
 * {@code es/caib/emiserv/logic/fake/plantilles/<CODI>.xml}: el formulari del servei l'ofereix
 * quan el servei no en té cap.
 */
@Component
public class ServeiFakeHelper {

	public static final String PROPIETAT_PERMES = "es.caib.emiserv.fake.permes";
	public static final String LITERAL_PREFIX = "[FAKE] ";

	private static final Pattern MARCADOR = Pattern.compile("\\$\\{([A-Za-z0-9]+)(?::([^}]*))?}");
	private static final String PLANTILLES_PATH = "es/caib/emiserv/logic/fake/plantilles/";
	private static final Pattern CODI_PLANTILLA = Pattern.compile("[A-Za-z0-9_]+");

	/** Plantilles per defecte ja llegides, per codi de servei (buit si el servei no en té). */
	private final Map<String, Optional<String>> plantillesPerDefecte = new ConcurrentHashMap<>();

	private final Environment environment;

	public ServeiFakeHelper(Environment environment) {
		this.environment = environment;
	}

	/** Indica si la propietat global permet el mode fake. */
	public boolean isPermes() {
		return environment.getProperty(PROPIETAT_PERMES, Boolean.class, false);
	}

	/** Indica si el servei s'ha de resoldre amb una resposta fake. */
	public boolean isFake(ServeiEntity servei) {
		return isPermes() && servei != null && servei.isFake();
	}

	/**
	 * Retorna l'element DatosEspecificos: la plantilla del servei amb els marcadors
	 * substituïts (escapats com a XML) o, si no n'hi ha, el generat pel servei fake.
	 */
	public String dadesEspecifiques(ServeiEntity servei, FakeRequest peticio, String idSolicitud) {
		String plantilla = servei.getFakeDadesEspecifiques();
		if (plantilla == null || plantilla.trim().isEmpty()) {
			return FakeResponses.datosEspecificos(
					peticio,
					servei.getFakeResultat() == ServeiFakeResultatEnumDto.SENSE_DADES);
		}
		Matcher m = MARCADOR.matcher(plantilla);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String valor = valorMarcador(m.group(1), peticio, idSolicitud);
			if (valor != null && valor.trim().isEmpty() && m.group(2) != null) {
				// El valor per defecte del marcador ja és XML de la plantilla: no s'escapa
				m.appendReplacement(sb, Matcher.quoteReplacement(m.group(2)));
			} else {
				m.appendReplacement(sb, Matcher.quoteReplacement(valor != null ? escapa(valor) : m.group()));
			}
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Plantilla de dades específiques per defecte del servei, o null si no en té.
	 *
	 * @param codi codi del servei (codi de certificat SCSP).
	 */
	public String plantillaPerDefecte(String codi) {
		if (codi == null || !CODI_PLANTILLA.matcher(codi.trim()).matches()) {
			return null;
		}
		return plantillesPerDefecte.computeIfAbsent(
				codi.trim().toUpperCase(),
				c -> Optional.ofNullable(llegirPlantilla(c))).orElse(null);
	}

	private static String llegirPlantilla(String codi) {
		try (InputStream is = ServeiFakeHelper.class.getClassLoader().getResourceAsStream(PLANTILLES_PATH + codi + ".xml")) {
			return is != null ? new String(is.readAllBytes(), StandardCharsets.UTF_8) : null;
		} catch (IOException ex) {
			throw new IllegalStateException("No s'ha pogut llegir la plantilla fake per defecte del servei " + codi, ex);
		}
	}

	/** Literal d'estat de la resposta fake. */
	public String literalEstat(ServeiEntity servei) {
		return LITERAL_PREFIX + (servei.getFakeResultat() == ServeiFakeResultatEnumDto.SENSE_DADES
				? "Sin datos."
				: "Peticion procesada correctamente.");
	}

	private String valorMarcador(String nom, FakeRequest peticio, String idSolicitud) {
		switch (nom) {
		case "codigoCertificado": return nullABuit(peticio.getServiceCode());
		case "idPeticion": return nullABuit(peticio.getIdPeticion());
		case "idSolicitud": return nullABuit(idSolicitud);
		case "documentacion": return primerNoBuit(peticio.getTitularDocumentacio(), peticio.getDocumentSolicitud());
		case "tipoDocumentacion": return nullABuit(peticio.getTipusDocumentacio());
		case "provinciaSolicitud": return nullABuit(peticio.getProvinciaSolicitud());
		case "municipioSolicitud": return nullABuit(peticio.getMunicipioSolicitud());
		case "nombre": return nullABuit(peticio.getTitularNom());
		case "apellido1": return nullABuit(peticio.getTitularLlinatge1());
		case "apellido2": return nullABuit(peticio.getTitularLlinatge2());
		default: return null;
		}
	}

	private static String primerNoBuit(String... valors) {
		for (String valor: valors) {
			if (valor != null && !valor.trim().isEmpty()) {
				return valor;
			}
		}
		return "";
	}

	private static String nullABuit(String s) {
		return s != null ? s : "";
	}

	private static String escapa(String s) {
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&apos;");
	}

}
