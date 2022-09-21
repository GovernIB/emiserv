/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Informació d'una organisme cessionari SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
public class OrganismeCessionariDto implements Serializable {
	
	private Long id;
	private String nom;
	private String cif;
	private Date dataBaixa;
	private Date dataAlta;
	private Boolean bloquejat;
	private byte[] logo;
	private List<ClauPrivadaDto> claus;
	public String codiUnitatTramitadora;
	
	public String getCadenaIdentificadora() {
		return nom + "(" + cif + ")";
	}
	
	private static final long serialVersionUID = -8620175604318725073L;

}
