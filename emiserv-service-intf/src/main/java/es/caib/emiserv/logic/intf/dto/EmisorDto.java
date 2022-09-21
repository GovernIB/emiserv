/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'un EmisorCertificado SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
public class EmisorDto implements Serializable {

	private Long id;
	private String nom;
	private String cif;
	private Date dataAlta;
	private Date dataBaixa;

	private static final long serialVersionUID = -8620175604318725073L;

}
