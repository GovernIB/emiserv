/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Destí (cedent o backoffice) de les sol·licituds de les consultes asíncrones, amb les que té
 * pendents. Les rutes dels enrutadors es poden pausar.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ConsultaAsincronaCedentDto implements Serializable {

	/** Ruta de l'enrutador (null per als serveis de tipus backoffice o les sol·licituds sense entitat). */
	private Long rutaId;
	private String serveiCodi;
	private String serveiNom;
	private String entitatCodi;
	private ServeiRutaDestiTipusEnumDto destiTipus;
	private boolean pausada;
	private long pendents;
	private long enviant;
	/** Temps mínim entre enviaments (ms) i enviaments simultanis que s'apliquen al destí. */
	private long intervalMs;
	private int concurrencia;
	private Date dataFiPrevista;

	private static final long serialVersionUID = -2203398420861297740L;

}
