package es.caib.emiserv.api.interna.scsp.pinbal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Configuració de les proves de l'emisor SCSP.
 *
 * Els valors es llegeixen del fitxer indicat amb la propietat de sistema
 * emiserv.scsp.prova.config i es poden sobreescriure amb propietats de sistema
 * (-Demiserv.scsp.prova.xxx=...). Totes les propietats tenen el prefix emiserv.scsp.prova.
 *
 * Les dades s'han de correspondre amb les que té configurades PINBAL per al servei:
 * certificat (keystore) i organisme autoritzats a EMISERV, codi de servei, emisor, etc.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConfiguracioProva {

	public static final String PREFIX = "emiserv.scsp.prova.";
	public static final String PROPIETAT_URL = PREFIX + "url";
	public static final String PROPIETAT_CONFIG = PREFIX + "config";

	private final Properties properties = new Properties();

	public ConfiguracioProva() {
		String config = System.getProperty(PROPIETAT_CONFIG);
		if (config != null && !config.isBlank()) {
			try (InputStream is = Files.newInputStream(Paths.get(config))) {
				properties.load(is);
			} catch (IOException ex) {
				throw new IllegalStateException("No s'ha pogut llegir la configuració de les proves (" + config + ")", ex);
			}
		}
		for (String nom: System.getProperties().stringPropertyNames()) {
			if (nom.startsWith(PREFIX)) {
				properties.setProperty(nom, System.getProperty(nom));
			}
		}
	}

	/** Indica si s'ha configurat l'adreça de l'emisor SCSP (sense adreça les proves no s'executen). */
	public static boolean isConfigurada() {
		ConfiguracioProva configuracio = new ConfiguracioProva();
		return configuracio.get("url") != null;
	}

	/** URL base de l'emisor SCSP. P.ex.: http://localhost:8080/emiservapi/interna/scsp */
	public String getUrlBase() {
		String url = getObligatori("url");
		return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
	}

	public Path getKeystorePath() {
		return Paths.get(getObligatori("keystore.path"));
	}
	public String getKeystoreType() {
		String path = getObligatori("keystore.path").toLowerCase();
		return get("keystore.type", path.endsWith(".p12") || path.endsWith(".pfx") ? "PKCS12" : "JKS");
	}
	public String getKeystorePassword() {
		return getObligatori("keystore.password");
	}
	public String getKeystoreAlias() {
		return getObligatori("keystore.alias");
	}
	public String getKeystoreAliasPassword() {
		return get("keystore.alias.password", getKeystorePassword());
	}

	public String getServeiCodi() {
		return getObligatori("servei");
	}
	/** Servei asíncron (opcional). Si no s'indica, no s'executen les proves de peticions asíncrones. */
	public String getServeiAsincronCodi() {
		return get("servei.asincron");
	}
	public String getEmisorNif() {
		return getObligatori("emisor.nif");
	}
	public String getEmisorNom() {
		return getObligatori("emisor.nom");
	}
	public String getSolicitantIdentificacio() {
		return getObligatori("solicitant.id");
	}
	public String getSolicitantNom() {
		return getObligatori("solicitant.nom");
	}
	public String getUnitatTramitadora() {
		return get("unitat", "Unitat de proves");
	}
	public String getUnitatTramitadoraCodi() {
		return get("unitat.codi");
	}
	public String getProcedimentCodi() {
		return get("procediment.codi", "PROVES");
	}
	public String getProcedimentNom() {
		return get("procediment.nom", "Procediment de proves");
	}
	public String getFinalitat() {
		return get("finalitat");
	}
	public String getConsentiment() {
		return get("consentiment", "Si");
	}
	public String getFuncionariNom() {
		return get("funcionari.nom", "Funcionari de proves");
	}
	public String getFuncionariNif() {
		return get("funcionari.nif", "12345678Z");
	}
	public String getTitularDocumentTipus() {
		return get("titular.tipus", "NIF");
	}
	public String getTitularDocument() {
		return get("titular.document", "12345678Z");
	}
	public String getTitularNom() {
		return get("titular.nom");
	}
	public String getTitularLlinatge1() {
		return get("titular.llinatge1");
	}
	public String getTitularLlinatge2() {
		return get("titular.llinatge2");
	}
	/** Fitxer XML amb l'element DatosEspecificos que s'enviarà a la petició (opcional). */
	public Path getDadesEspecifiquesPath() {
		String path = get("dades.especifiques");
		return path != null ? Paths.get(path) : null;
	}
	public String getIdPeticioPrefix() {
		return get("idpeticio.prefix", "PBLPROVA");
	}
	/** Codi d'estat esperat a la resposta síncrona (0003: tramitada). */
	public String getEstatEsperat() {
		return get("estat.esperat", "0003");
	}
	/** PINBAL valida la signatura de les respostes, per defecte s'exigeix que la resposta vagi signada. */
	public boolean isSignaturaRespostaObligatoria() {
		return Boolean.parseBoolean(get("signatura.resposta.obligatoria", "true"));
	}
	public int getTimeoutSegons() {
		return Integer.parseInt(get("timeout.segons", "60"));
	}

	public String get(String nom) {
		String valor = properties.getProperty(PREFIX + nom);
		return valor != null && !valor.isBlank() ? valor.trim() : null;
	}

	private String get(String nom, String valorPerDefecte) {
		String valor = get(nom);
		return valor != null ? valor : valorPerDefecte;
	}

	private String getObligatori(String nom) {
		String valor = get(nom);
		if (valor == null) {
			throw new IllegalStateException("Falta la propietat de configuració de les proves " + PREFIX + nom);
		}
		return valor;
	}

}
