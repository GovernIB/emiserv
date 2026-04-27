/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació per a filtrar un parametre de SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ScspParametreFiltreDto {

    private String nombre;
    private String valor;
    private String descripcion;

    @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
