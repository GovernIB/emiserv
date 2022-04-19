/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Informació d'una aplicació per a autoritzacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AplicacioDto {

	private Integer id;
	private String certificatNif;
	private String cn;
	private String numeroSerie;
	private Date darreraComprovacio;
	private String codiCa;
	private AutoritatCertificacioDto autoridadCertificacio;
	private Date dataAlta;
	private Date dataBaixa;

}
