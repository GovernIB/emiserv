/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una sol·licitud per auditoria.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSolicitudId() {
		return solicitudId;
	}
	public void setSolicitudId(String solicitudId) {
		this.solicitudId = solicitudId;
	}
	public String getTransmisioId() {
		return transmisioId;
	}
	public void setTransmisioId(String transmisioId) {
		this.transmisioId = transmisioId;
	}
	public String getSolicitantId() {
		return solicitantId;
	}
	public void setSolicitantId(String solicitantId) {
		this.solicitantId = solicitantId;
	}
	public String getSolicitantNom() {
		return solicitantNom;
	}
	public void setSolicitantNom(String solicitantNom) {
		this.solicitantNom = solicitantNom;
	}
	public String getTitularDocument() {
		return titularDocument;
	}
	public void setTitularDocument(String titularDocument) {
		this.titularDocument = titularDocument;
	}
	public String getTitularNom() {
		return titularNom;
	}
	public void setTitularNom(String titularNom) {
		this.titularNom = titularNom;
	}
	public String getTitularLlinatge1() {
		return titularLlinatge1;
	}
	public void setTitularLlinatge1(String titularLlinatge1) {
		this.titularLlinatge1 = titularLlinatge1;
	}
	public String getTitularLlinatge2() {
		return titularLlinatge2;
	}
	public void setTitularLlinatge2(String titularLlinatge2) {
		this.titularLlinatge2 = titularLlinatge2;
	}
	public String getTitularNomSencer() {
		return titularNomSencer;
	}
	public void setTitularNomSencer(String titularNomSencer) {
		this.titularNomSencer = titularNomSencer;
	}
	public String getFuncionariDocument() {
		return funcionariDocument;
	}
	public void setFuncionariDocument(String funcionariDocument) {
		this.funcionariDocument = funcionariDocument;
	}
	public String getFuncionariNom() {
		return funcionariNom;
	}
	public void setFuncionariNom(String funcionariNom) {
		this.funcionariNom = funcionariNom;
	}
	public Date getDataGeneracio() {
		return dataGeneracio;
	}
	public void setDataGeneracio(Date dataGeneracio) {
		this.dataGeneracio = dataGeneracio;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getProcedimentNom() {
		return procedimentNom;
	}
	public void setProcedimentNom(String procedimentNom) {
		this.procedimentNom = procedimentNom;
	}
	public String getUnitatTramitadora() {
		return unitatTramitadora;
	}
	public void setUnitatTramitadora(String unitatTramitadora) {
		this.unitatTramitadora = unitatTramitadora;
	}
	public String getFinalitat() {
		return finalitat;
	}
	public void setFinalitat(String finalitat) {
		this.finalitat = finalitat;
	}
	public String getConsentiment() {
		return consentiment;
	}
	public void setConsentiment(String consentiment) {
		this.consentiment = consentiment;
	}
	public String getEstat() {
		return estat;
	}
	public void setEstat(String estat) {
		this.estat = estat;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
