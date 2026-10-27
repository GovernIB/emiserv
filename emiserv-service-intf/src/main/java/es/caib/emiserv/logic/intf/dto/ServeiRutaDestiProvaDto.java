/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Resultat de la prova de connexió amb el destí d'una ruta d'un servei enrutador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ServeiRutaDestiProvaDto implements Serializable {

	private boolean correcta;
	private String url;
	private Integer codiHttp;
	private String missatge;
	private long tempsMs;
	/** La connexió és correcta però falten les credencials que necessita l'autenticació del backoffice. */
	private boolean credencialsPendents;

	private static final long serialVersionUID = 4271349858183946150L;

}
