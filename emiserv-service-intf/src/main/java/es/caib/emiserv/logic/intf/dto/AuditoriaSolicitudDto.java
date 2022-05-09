/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'una sol·licitud per auditoria.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
public class AuditoriaSolicitudDto implements Serializable {

	private Long id;
	private String solicitudId;
	private String transmisioId;
	private String solicitantId;
	private String solicitantNom;
	private String titularDocument;
	private String titularNom;
	private String titularLlinatge1;
	private String titularLlinatge2;
	private String titularNomSencer;
	private String funcionariDocument;
	private String funcionariNom;
	private Date dataGeneracio;
	private String procedimentCodi;
	private String procedimentNom;
	private String unitatTramitadora;
	private String finalitat;
	private String consentiment;
	private String estat;
	private String error;

	public String getProcedimentCodiNom() {
		if ((procedimentCodi == null || procedimentCodi.isBlank()) && (procedimentNom == null || procedimentNom.isBlank()))
			return null;
		if (procedimentCodi == null || procedimentCodi.isBlank())
			return procedimentNom;
		if ((procedimentNom == null || procedimentNom.isBlank()))
			return procedimentCodi;
		return procedimentCodi + " - " + procedimentNom;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
