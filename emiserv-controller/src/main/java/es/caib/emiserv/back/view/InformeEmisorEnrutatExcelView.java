package es.caib.emiserv.back.view;

import es.caib.emiserv.logic.intf.dto.InformeEmisorEnrutatDto;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Vista per a generar l'informe de peticions a cada emissor
 * dels serveis enrutats.
 */
@Component("informeEmisorEnrutatExcelView")
public class InformeEmisorEnrutatExcelView extends AbstractXlsView implements MessageSourceAware {

	private MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(
			Map<String, Object> model,
			Workbook workbook,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", "Inline; filename=informeEmisorEnrutat.xls");
		List<InformeEmisorEnrutatDto> informeDades = (List<InformeEmisorEnrutatDto>)model.get("informeDades");
		Sheet sheet = workbook.createSheet(
				getMessage(request, "informe.emisor.enrutat.excel.fulla.titol"));
		int filaInicial = 0;
		int columnaInicial = 0;

		CellStyle capsaleraStyle = workbook.createCellStyle();
		capsaleraStyle.setAlignment(HorizontalAlignment.CENTER);
		capsaleraStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		Font capsaleraFont = workbook.createFont();
		capsaleraFont.setFontName("Arial");
		capsaleraFont.setFontHeightInPoints((short)10);
		capsaleraFont.setBold(true);
		capsaleraStyle.setFont(capsaleraFont);

		CellStyle genericStyle = workbook.createCellStyle();
		Font genericFont = workbook.createFont();
		genericFont.setFontName("Arial");
		genericFont.setBold(true);
		genericStyle.setFont(genericFont);

		CellStyle detallMultipleStyle = workbook.createCellStyle();
		Font detallMultipleFont = workbook.createFont();
		detallMultipleFont.setFontName("Arial");
		detallMultipleFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
		detallMultipleFont.setBold(false);
		detallMultipleStyle.setFont(detallMultipleFont);

		Row titolsColumna = sheet.createRow(filaInicial);
		String[] keys = new String[] {
				"informe.emisor.enrutat.excel.columna.emisor",
				"informe.emisor.enrutat.excel.columna.servei.codi",
				"informe.emisor.enrutat.excel.columna.servei.nom",
				"informe.emisor.enrutat.excel.columna.enrutador.multiple",
				"informe.emisor.enrutat.excel.columna.entitat",
				"informe.emisor.enrutat.excel.columna.url.desti",
				"informe.emisor.enrutat.excel.columna.peticions.totals",
				"informe.emisor.enrutat.excel.columna.peticions.correctes",
				"informe.emisor.enrutat.excel.columna.peticions.erronies",
				"informe.emisor.enrutat.excel.columna.peticions.resposta.esperada"
		};
		for (int i = 0; i < keys.length; i++) {
			Cell capCell = titolsColumna.createCell(columnaInicial + i);
			capCell.setCellStyle(capsaleraStyle);
			capCell.setCellValue(getMessage(request, keys[i]));
		}

		int rowIndex = 1;
		for (InformeEmisorEnrutatDto informeDada: expandirFiles(informeDades)) {
			Row filaDada = sheet.createRow(filaInicial + rowIndex++);
			boolean esDetallMultiple = informeDada.isEnrutadorMultiple()
					&& !"TOTAL".equals(informeDada.getEntitatCodi());
			Cell dadaCell = filaDada.createCell(columnaInicial);
			dadaCell.setCellValue(informeDada.getEmissorCodi());
			dadaCell.setCellStyle(esDetallMultiple ? detallMultipleStyle : genericStyle);
			dadaCell = filaDada.createCell(columnaInicial + 1);
			dadaCell.setCellValue(informeDada.getServeiCodi());
			dadaCell.setCellStyle(esDetallMultiple ? detallMultipleStyle : genericStyle);
			dadaCell = filaDada.createCell(columnaInicial + 2);
			dadaCell.setCellValue(informeDada.getServeiNom());
			dadaCell.setCellStyle(esDetallMultiple ? detallMultipleStyle : genericStyle);
			dadaCell = filaDada.createCell(columnaInicial + 3);
			dadaCell.setCellValue(getMessage(
					request,
					informeDada.isEnrutadorMultiple()
							? "informe.emisor.enrutat.valor.si"
							: "informe.emisor.enrutat.valor.no"));
			dadaCell.setCellStyle(esDetallMultiple ? detallMultipleStyle : genericStyle);
			dadaCell = filaDada.createCell(columnaInicial + 4);
			if ("TOTAL".equals(informeDada.getEntitatCodi())) {
				dadaCell.setCellValue("TOTAL CONSULTES");
				sheet.addMergedRegion(new CellRangeAddress(
						filaDada.getRowNum(),
						filaDada.getRowNum(),
						columnaInicial + 4,
						columnaInicial + 5));
			} else {
				dadaCell.setCellValue(informeDada.getEntitatCodi());
			}
			dadaCell.setCellStyle(esDetallMultiple ? detallMultipleStyle : genericStyle);
			dadaCell = filaDada.createCell(columnaInicial + 5);
			if (!"TOTAL".equals(informeDada.getEntitatCodi())) {
				dadaCell.setCellValue(informeDada.getUrlDesti());
				if (esDetallMultiple) {
					dadaCell.setCellStyle(detallMultipleStyle);
				}
			}
			dadaCell = filaDada.createCell(columnaInicial + 6);
			dadaCell.setCellValue(informeDada.getPeticionsTotals());
			dadaCell.setCellStyle(esDetallMultiple ? detallMultipleStyle : genericStyle);
			dadaCell = filaDada.createCell(columnaInicial + 7);
			dadaCell.setCellValue(informeDada.getPeticionsCorrectes());
			dadaCell.setCellStyle(esDetallMultiple ? detallMultipleStyle : genericStyle);
			dadaCell = filaDada.createCell(columnaInicial + 8);
			dadaCell.setCellValue(informeDada.getPeticionsErronies());
			dadaCell.setCellStyle(esDetallMultiple ? detallMultipleStyle : genericStyle);
			dadaCell = filaDada.createCell(columnaInicial + 9);
			if (esDetallMultiple) dadaCell.setCellValue(informeDada.getPeticionsRespostaEsperada());
			dadaCell.setCellStyle(esDetallMultiple ? detallMultipleStyle : genericStyle);
		}

		try {
			autoSize(sheet, keys.length);
		} catch (Exception ex) {
			logger.error("No ha estat possible fer l'autoSize de les columnes", ex);
		}
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private void autoSize(
			Sheet sheet,
			int numCells) {
		for (int colNum = 0; colNum < numCells; colNum++) {
			sheet.autoSizeColumn(colNum);
		}
	}

	private String getMessage(
			HttpServletRequest request,
			String key) {
		return messageSource.getMessage(
				key,
				null,
					"???" + key + "???",
					new RequestContext(request).getLocale());
	}

	private List<InformeEmisorEnrutatDto> expandirFiles(List<InformeEmisorEnrutatDto> informeDades) {
		Map<String, List<InformeEmisorEnrutatDto>> agrupat = new LinkedHashMap<>();
		for (InformeEmisorEnrutatDto informeDada : informeDades) {
			String key = informeDada.getEmissorCodi() + "||" + informeDada.getServeiCodi();
			agrupat.computeIfAbsent(key, k -> new ArrayList<>()).add(informeDada);
		}

			List<InformeEmisorEnrutatDto> files = new ArrayList<>();
			for (List<InformeEmisorEnrutatDto> grup : agrupat.values()) {
				InformeEmisorEnrutatDto primera = grup.get(0);
				if (primera.isEnrutadorMultiple()) {
					files.add(new InformeEmisorEnrutatDto(
							primera.getEmissorCodi(),
							primera.getServeiTipus(),
							primera.getServeiCodi(),
							primera.getServeiNom(),
							"TOTAL",
							"",
							primera.getPeticionsTotalsServei(),
							primera.getPeticionsCorrectesServei(),
							primera.getPeticionsErroniesServei(),
							primera.getPeticionsRespostaEsperadaServei(),
							primera.getPeticionsTotalsServei(),
							primera.getPeticionsCorrectesServei(),
							primera.getPeticionsErroniesServei(),
							primera.getPeticionsRespostaEsperadaServei()));
				}
				files.addAll(grup);
			}
		return files;
	}
}
