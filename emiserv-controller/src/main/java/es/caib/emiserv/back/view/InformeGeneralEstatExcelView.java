package es.caib.emiserv.back.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import es.caib.emiserv.logic.intf.dto.InformeGeneralEstatDto;

/**
 * Vista per a generar l'informe general d'estat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformeGeneralEstatExcelView extends AbstractXlsView implements MessageSourceAware {

	private MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(
			Map<String, Object> model,
			Workbook workbook,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "Inline; filename=informeEmisor.xls");
		List<InformeGeneralEstatDto> informeDades = (List<InformeGeneralEstatDto>)model.get("informeDades");
		Sheet sheet = workbook.createSheet(
				getMessage(
						request,
						"informe.general.estat.excel.fulla.titol"));
		int filaInicial = 0;
		int columnaInicial = 0;
		CellStyle capsaleraEntitatStyle = workbook.createCellStyle();
		capsaleraEntitatStyle.setAlignment(HorizontalAlignment.LEFT);
		capsaleraEntitatStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		CellStyle capsaleraStyle = workbook.createCellStyle();
		capsaleraStyle.setAlignment(HorizontalAlignment.CENTER);
		capsaleraStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		Font capsaleraFont = workbook.createFont();
		capsaleraFont.setFontName("Arial");
		capsaleraFont.setFontHeightInPoints((short)10);
		capsaleraFont.setBold(true);
		capsaleraStyle.setFont(capsaleraFont);
		Row titolsColumna = sheet.createRow(filaInicial);
		Cell tipusPeticioCell = titolsColumna.createCell(columnaInicial);
		tipusPeticioCell.setCellStyle(capsaleraStyle);
		tipusPeticioCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.peticio.tipus"));
		Cell codiCell = titolsColumna.createCell(columnaInicial +1);
		codiCell.setCellStyle(capsaleraStyle);
		codiCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.entitat.nom"));
		Cell capCell = titolsColumna.createCell(columnaInicial + 2);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.entitat.cif"));
		capCell = titolsColumna.createCell(columnaInicial + 3);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.departament"));
		capCell = titolsColumna.createCell(columnaInicial + 4);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.procediment.codi"));
		capCell = titolsColumna.createCell(columnaInicial + 5);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.procediment.nom"));
		capCell = titolsColumna.createCell(columnaInicial + 6);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.servei.codi"));
		capCell = titolsColumna.createCell(columnaInicial + 7);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.servei.nom"));
		capCell = titolsColumna.createCell(columnaInicial + 8);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.servei.emisor"));
		capCell = titolsColumna.createCell(columnaInicial + 9);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.peticions.correctes"));
		capCell = titolsColumna.createCell(columnaInicial + 10);
		capCell.setCellStyle(capsaleraStyle);
		capCell.setCellValue(
				getMessage(
						request,
						"informe.general.estat.excel.columna.peticions.erronies"));
		int rowIndex = 1;
		for (InformeGeneralEstatDto informeDada: informeDades) {
			Row filaDada = sheet.createRow(filaInicial + rowIndex++);
			Cell dadaCell = filaDada.createCell(columnaInicial);
			switch(informeDada.getPeticioTipus()) {
			case BACKOFFICE:
				dadaCell.setCellValue(getMessage(request, "servei.tipus.enum.BACKOFFICE"));
				break;
			default:
				dadaCell.setCellValue(getMessage(request, "servei.tipus.enum.ENRUTADOR"));
				break;
			}
			dadaCell = filaDada.createCell(columnaInicial + 1);
			dadaCell.setCellValue(informeDada.getEntitatNom());
			dadaCell = filaDada.createCell(columnaInicial + 2);
			dadaCell.setCellValue(informeDada.getEntitatCif());
			dadaCell = filaDada.createCell(columnaInicial + 3);
			dadaCell.setCellValue(informeDada.getDepartament());
			dadaCell = filaDada.createCell(columnaInicial + 4);
			dadaCell.setCellValue(informeDada.getProcedimentCodi());
			dadaCell = filaDada.createCell(columnaInicial + 5);
			dadaCell.setCellValue(informeDada.getProcedimentNom());
			dadaCell = filaDada.createCell(columnaInicial + 6);
			dadaCell.setCellValue(informeDada.getServeiCodi());
			dadaCell = filaDada.createCell(columnaInicial + 7);
			dadaCell.setCellValue(informeDada.getServeiNom());
			dadaCell = filaDada.createCell(columnaInicial + 8);
			dadaCell.setCellValue(informeDada.getEmissorCif());
			dadaCell = filaDada.createCell(columnaInicial + 9);
			dadaCell.setCellValue(informeDada.getPeticionsCorrectes());
			dadaCell = filaDada.createCell(columnaInicial + 10);
			dadaCell.setCellValue(informeDada.getPeticionsErronies());
		}
		autoSize(sheet, 11);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private void autoSize(
			Sheet sheet,
			int numCells) {
		for (int colNum = 0; colNum <= numCells; colNum++)
			sheet.autoSizeColumn(colNum);
	}

	private String getMessage(
			HttpServletRequest request,
			String key) {
		String message = messageSource.getMessage(
				key,
				null,
				"???" + key + "???",
				new RequestContext(request).getLocale());
		return message;
	}

}
