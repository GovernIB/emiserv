/**
 * 
 */
package es.caib.emiserv.client.dadesobertes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import es.caib.emiserv.client.comu.ServeiTipus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Estructura d'un element de la resposta per a les dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@XmlRootElement
public class DadesObertesRespostaConsulta {

	private String entitatCodi;
	private String entitatNom;
	private String entitatNif;
//	private String entitatTipus;
	private String departamentCodi;
	private String departamentNom;
	private String procedimentCodi;
	private String procedimentNom;
	private String serveiCodi;
	private String serveiNom;
	private String emissor;
	private String emissorNif;
	private String consentiment;
	private String finalitat;
	private String titularTipusDoc;
	private String solicitudId;
	private Date data;
	private ServeiTipus tipus;
	private DadesObertesConsultaResultat resultat;

	public DadesObertesRespostaConsulta(
			String entitatCodi,
			String entitatNom,
			String entitatNif,
			String departamentNom,
			String procedimentCodi,
			String procedimentNom,
			String serveiCodi,
			String serveiNom,
			String emissor,
			String emissorNif,
			String consentiment,
			String finalitat,
			String titularTipusDoc,
			String solicitudId,
			Date data,
			Enum tipus,
			String estat) {
		this(
				entitatCodi,
				entitatNom,
				entitatNif,
				departamentNom,
				procedimentCodi,
				procedimentNom,
				serveiCodi,
				serveiNom,
				emissor,
				emissorNif,
				consentiment,
				finalitat,
				titularTipusDoc,
				solicitudId,
				data,
				"ENRUTADOR_MULTIPLE".equals(tipus.name()) ? "ENRUTADOR" : tipus.name(),
				estat);
	}

	public DadesObertesRespostaConsulta(
			String entitatCodi,
			String entitatNom,
			String entitatNif,
			String departamentNom,
			String procedimentCodi,
			String procedimentNom,
			String serveiCodi,
			String serveiNom,
			String emissor,
			String emissorNif,
			String consentiment,
			String finalitat,
			String titularTipusDoc,
			String solicitudId,
			Date data,
			String tipus,
			String estat) {
		super();
		this.entitatCodi = entitatCodi;
		this.entitatNom = entitatNom;
		this.entitatNif = entitatNif;
		this.departamentNom = departamentNom;
		this.procedimentCodi = procedimentCodi;
		this.procedimentNom = procedimentNom;
		this.serveiCodi = serveiCodi;
		this.serveiNom = serveiNom;
		this.emissor = emissor;
		this.emissorNif = emissorNif;
		this.consentiment = consentiment;
		if (finalitat != null) {
			int index = finalitat.lastIndexOf("#");
			if (index != -1) {
				this.finalitat = finalitat.substring(index + 1);
			} else {
				this.finalitat = finalitat;
			}
		}
		this.titularTipusDoc = titularTipusDoc;
		this.solicitudId = solicitudId;
		this.data = data;
		this.tipus = ServeiTipus.valueOf(tipus);

		if (estat == null) {
			this.resultat = DadesObertesConsultaResultat.PENDENT;
		} else {
			if (estat.startsWith("00")) {
				if (estat.equals("0001"))
					this.resultat = DadesObertesConsultaResultat.PENDENT;
				else if (estat.equals("0002"))
					this.resultat = DadesObertesConsultaResultat.PROCES;
				else if (estat.equals("0003"))
					this.resultat = DadesObertesConsultaResultat.OK;
				else if (estat.equals("0004"))
					this.resultat = DadesObertesConsultaResultat.PROCES;
			} else {
				this.resultat = DadesObertesConsultaResultat.ERROR;
			}
		}
	}

	// Mètodes custom pel builder
	public DadesObertesRespostaConsulta finalitat(String finalitat) {
		if (finalitat != null) {
			int index = finalitat.lastIndexOf("#");
			if (index != -1) {
				this.finalitat = finalitat.substring(index + 1);
			} else {
				this.finalitat = finalitat;
			}
		}
		return this;
	}

	public DadesObertesRespostaConsulta resultat(String estat) {
		if (estat == null) {
			this.resultat = DadesObertesConsultaResultat.PENDENT;
		} else {
			if (estat.startsWith("00")) {
				if (estat.equals("0001"))
					this.resultat = DadesObertesConsultaResultat.PENDENT;
				else if (estat.equals("0002"))
					this.resultat = DadesObertesConsultaResultat.PROCES;
				else if (estat.equals("0003"))
					this.resultat = DadesObertesConsultaResultat.OK;
				else if (estat.equals("0004"))
					this.resultat = DadesObertesConsultaResultat.PROCES;
			} else {
				this.resultat = DadesObertesConsultaResultat.ERROR;
			}
		}
		return this;
	}

	// Enumerats
	public enum DadesObertesConsultaResultat {
		PENDENT,
		OK,
		PROCES,
		ERROR
	}

}
