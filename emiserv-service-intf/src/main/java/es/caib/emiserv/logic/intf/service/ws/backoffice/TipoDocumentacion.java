/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

import java.io.Serializable;

/**
 * Enumeració amb el TipoDocumentacion per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum TipoDocumentacion implements Serializable {
	CIF,
	NIF,
	DNI,
	Pasaporte,
	NIE;

	private TipoDocumentacion() {
	}

}
