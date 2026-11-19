/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informació d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
public class ServeiDto extends ObjecteAmbPermisosDto {

	private Long id;
	private String codi;
	private String nom;
	private ServeiTipusEnumDto tipus;
	private String descripcio;
	private String backofficeClass;
	private String backofficeCaibUrl;
	private BackofficeAutenticacioTipusEnumDto backofficeCaibAutenticacio;
	private BackofficeAsyncTipusEnumDto backofficeCaibAsyncTipus;
	private int backofficeCaibAsyncTer;
	private String resolverClass;
	private String urlPerDefecte;
	private String responseResolverClass;
	private boolean xsdGestioActiva;
	private String xsdEsquemaBackup;
	private boolean configurat;
	/** Indica si el servei està configurat a l'emisor SCSP (CORE_SERVICIO), necessari per a les autoritzacions i les rutes de tipus BACKOFFICE. */
	private boolean configuratScsp;
	/**
	 * Enrutadors: admeten rutes cap al backoffice dels cedents. Només aquests enrutadors tenen
	 * configuració SCSP i autoritzacions (les de les rutes cap als cedents).
	 */
	private boolean rutesBackoffice;
	/** Enrutadors: nombre de rutes cap al backoffice dels cedents (només a la consulta per id). */
	private long rutesBackofficeCount;
	/** Nombre d'autoritzacions de l'emisor SCSP (només al llistat paginat). */
	private long autoritzacionsCount;
	/** Enrutadors: s'accepten peticions asíncrones processant-les com a síncrones a cada cedent. */
	private boolean asyncSincron;
	/** Mode fake: respostes simulades sense consultar el cedent. */
	private boolean fake;
	private ServeiFakeResultatEnumDto fakeResultat;
	/** Plantilla opcional de DatosEspecificos de la resposta simulada. */
	private String fakeDadesEspecifiques;
	/** Temps mínim entre peticions a un mateix cedent (ms). Si és null, el valor global. */
	private Integer asyncInterval;
	/** Peticions simultànies màximes a un mateix cedent. Si és null, el valor global. */
	private Integer asyncConcurrencia;
	private boolean actiu;

	/**
	 * Les peticions asíncrones es processen dins EMISERV com a síncrones a cada destí: enrutadors amb
	 * asyncSincron i backoffices amb el tipus asíncron PROCESSAR_SINCRONES (els altres backoffices
	 * reenvien l'asíncrona al seu backoffice).
	 */
	public boolean isAsyncProcessatSincron() {
		if (ServeiTipusEnumDto.BACKOFFICE.equals(tipus)) {
			return BackofficeAsyncTipusEnumDto.PROCESSAR_SINCRONES.equals(backofficeCaibAsyncTipus);
		}
		return tipus != null && asyncSincron;
	}

	public String getCodiNom() {
		if ((codi == null || codi.isBlank()) && (nom == null || nom.isBlank()))
			return null;
		if (codi == null || codi.isBlank())
			return nom;
		if ((nom == null || nom.isBlank()))
			return codi;
		return this.codi + " - " + this.nom;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
