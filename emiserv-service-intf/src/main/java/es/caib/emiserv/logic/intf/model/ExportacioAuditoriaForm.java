package es.caib.emiserv.logic.intf.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Paràmetres de l'exportació dels llistats d'auditoria: el mateix filtre i ordre que la graella. El
 * format (CSV o XLSX) és el paràmetre fileType de la petició de l'informe.
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
public class ExportacioAuditoriaForm implements Serializable {

	private String quickFilter;
	private String filter;
	/** Ordenació en format Spring Data ("camp" o "camp,asc|desc"). */
	private String[] sort;

	private static final long serialVersionUID = 1L;

}
