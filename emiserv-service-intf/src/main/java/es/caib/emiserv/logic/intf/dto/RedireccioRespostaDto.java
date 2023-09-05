/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultat de processar la petició de redirecció SCSP.
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedireccioRespostaDto extends ObjecteAmbPermisosDto {

	private String entitat;
	private String xmlResposta;
	private boolean respostaEscollida;

	private static final long serialVersionUID = 3277509076965516362L;

}
