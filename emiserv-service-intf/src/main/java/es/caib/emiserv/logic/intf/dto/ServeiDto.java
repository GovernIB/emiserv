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
	private boolean actiu;

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
