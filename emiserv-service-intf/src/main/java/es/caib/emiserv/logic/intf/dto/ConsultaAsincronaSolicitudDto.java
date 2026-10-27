/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Sol·licitud d'una consulta asíncrona processada com a síncrona.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ConsultaAsincronaSolicitudDto implements Serializable {

	private Long id;
	private String solicitudId;
	private String entitatCodi;
	private BackofficeSolicitudEstatEnumDto estat;
	private int intents;
	private Date dataEnviament;
	private Date dataProperIntent;
	/** IdPeticion de la petició al cedent (rutes de tipus EMISERV). */
	private String peticioDestiId;
	private String errorCodi;
	private String darrerError;

	private static final long serialVersionUID = 2915703640212918754L;

}
