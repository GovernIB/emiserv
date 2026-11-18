package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.dto.ServeiFakeResultatEnumDto;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.scsp.fake.core.FakeRequest;
import es.caib.emiserv.scsp.fake.core.FakeResponses;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Activació del mode fake dels serveis i generació de les dades específiques de les
 * respostes fake.
 */
@Component
public class ServeiFakeHelper {

	public static final String PROPIETAT_PERMES = "es.caib.emiserv.fake.permes";
	public static final String LITERAL_PREFIX = "[FAKE] ";

	private static final Pattern MARCADOR = Pattern.compile("\\$\\{([A-Za-z0-9]+)}");

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
			m.appendReplacement(sb, Matcher.quoteReplacement(valor != null ? escapa(valor) : m.group()));
		}
		m.appendTail(sb);
		return sb.toString();
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
		case "documentacion": return nullABuit(peticio.getTitularDocumentacio());
		case "nombre": return nullABuit(peticio.getTitularNom());
		case "apellido1": return nullABuit(peticio.getTitularLlinatge1());
		case "apellido2": return nullABuit(peticio.getTitularLlinatge2());
		default: return null;
		}
	}

	private static String nullABuit(String s) {
		return s != null ? s : "";
	}

	private static String escapa(String s) {
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&apos;");
	}

}
