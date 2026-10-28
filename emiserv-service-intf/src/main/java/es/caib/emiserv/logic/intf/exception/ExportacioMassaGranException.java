package es.caib.emiserv.logic.intf.exception;

/**
 * Excepció que es llança quan una exportació supera el màxim de files permès
 * (propietat es.caib.emiserv.exportacio.max).
 *
 * @author Limit Tecnologies
 */
public class ExportacioMassaGranException extends RuntimeException {

	private final long total;
	private final long maxim;

	public ExportacioMassaGranException(long total, long maxim) {
		super("L'exportació té " + total + " files i supera el màxim permès (" + maxim + ")");
		this.total = total;
		this.maxim = maxim;
	}

	public long getTotal() {
		return total;
	}
	public long getMaxim() {
		return maxim;
	}

	private static final long serialVersionUID = 1L;

}
