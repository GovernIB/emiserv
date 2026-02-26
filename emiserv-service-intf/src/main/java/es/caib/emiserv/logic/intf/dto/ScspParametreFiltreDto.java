/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació per a filtrar un parametre de SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspParametreFiltreDto {

    private String nombre;
    private String valor;
    private String descripcion;

    public String getNombre() {
        return nombre;
    }
    public String getValor() {
        return valor;
    }
    public String getDescripcion() {
        return descripcion;
    }

    @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
