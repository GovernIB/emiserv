package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.exception.ExportacioMassaGranException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generació dels fitxers d'exportació dels llistats (CSV i XLSX) i control del màxim de files.
 * <ul>
 *   <li>CSV: UTF-8 amb BOM (perquè Excel el detecti), separador {@code ;} (Excel amb configuració
 *   regional espanyola/catalana), salts de línia CRLF i valors entre cometes quan cal (RFC 4180).
 *   Els textos que comencen amb {@code = + - @} es prefixen amb {@code '} per evitar la injecció
 *   de fórmules.</li>
 *   <li>XLSX: Apache POI (XSSF), amb la capçalera en negreta.</li>
 * </ul>
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Component
public class ExportacioHelper {

	public static final String PROP_EXPORTACIO_MAX = "es.caib.emiserv.exportacio.max";
	public static final long EXPORTACIO_MAX_PER_DEFECTE = 10000;

	public static final String FORMAT_CSV = "CSV";
	public static final String FORMAT_XLSX = "XLSX";

	private static final char SEPARADOR_CSV = ';';
	private static final byte[] BOM_UTF8 = new byte[] {(byte)0xEF, (byte)0xBB, (byte)0xBF};

	private final Environment environment;

	@Autowired
	public ExportacioHelper(Environment environment) {
		this.environment = environment;
	}

	/** Màxim de files exportables (propietat es.caib.emiserv.exportacio.max, per defecte 10000). */
	public long getMaxim() {
		String valor = environment.getProperty(PROP_EXPORTACIO_MAX);
		if (valor != null && !valor.trim().isEmpty()) {
			try {
				return Long.parseLong(valor.trim());
			} catch (NumberFormatException ex) {
				log.warn("Valor no vàlid per a la propietat {}: {}", PROP_EXPORTACIO_MAX, valor);
			}
		}
		return EXPORTACIO_MAX_PER_DEFECTE;
	}

	/**
	 * Comprova que el nombre de files a exportar no superi el màxim configurat.
	 *
	 * @throws ExportacioMassaGranException si total supera el màxim.
	 */
	public void comprovarMaxim(long total) {
		long maxim = getMaxim();
		if (total > maxim) {
			throw new ExportacioMassaGranException(total, maxim);
		}
	}

	/**
	 * Genera el fitxer amb les columnes indicades (la capçalera són els noms de les columnes).
	 *
	 * @param files objectes (propietats de bean) o mapes amb les dades.
	 * @param columnes noms de les propietats a exportar, en ordre.
	 * @param format CSV o XLSX.
	 */
	public byte[] generar(List<?> files, List<String> columnes, String format) {
		return generar(files, columnes, columnes, format);
	}

	/**
	 * Genera el fitxer amb les columnes i les capçaleres indicades.
	 */
	public byte[] generar(List<?> files, List<String> columnes, List<String> capcaleres, String format) {
		if (capcaleres == null || capcaleres.size() != columnes.size()) {
			capcaleres = columnes;
		}
		if (FORMAT_CSV.equalsIgnoreCase(format)) {
			return generarCsv(files, columnes, capcaleres);
		} else if (FORMAT_XLSX.equalsIgnoreCase(format)) {
			return generarXlsx(files, columnes, capcaleres);
		} else {
			throw new IllegalArgumentException("Format d'exportació no suportat: " + format);
		}
	}

	public String getContentType(String format) {
		if (FORMAT_CSV.equalsIgnoreCase(format)) {
			return "text/csv;charset=UTF-8";
		} else {
			return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		}
	}

	public String getNomFitxer(String nomBase, String format) {
		return nomBase + "." + format.toLowerCase();
	}

	private byte[] generarCsv(List<?> files, List<String> columnes, List<String> capcaleres) {
		StringBuilder sb = new StringBuilder();
		afegirLiniaCsv(sb, capcaleres.stream().map(this::valorCsv).collect(Collectors.toList()));
		if (files != null) {
			for (Object fila: files) {
				afegirLiniaCsv(
						sb,
						columnes.stream().
								map(c -> valorCsv(getValor(fila, c))).
								collect(Collectors.toList()));
			}
		}
		byte[] contingut = sb.toString().getBytes(StandardCharsets.UTF_8);
		byte[] resultat = new byte[BOM_UTF8.length + contingut.length];
		System.arraycopy(BOM_UTF8, 0, resultat, 0, BOM_UTF8.length);
		System.arraycopy(contingut, 0, resultat, BOM_UTF8.length, contingut.length);
		return resultat;
	}

	private void afegirLiniaCsv(StringBuilder sb, List<String> valors) {
		for (int i = 0; i < valors.size(); i++) {
			if (i > 0) {
				sb.append(SEPARADOR_CSV);
			}
			sb.append(escapeCsv(valors.get(i)));
		}
		sb.append("\r\n");
	}

	private String valorCsv(Object valor) {
		if (valor == null) {
			return "";
		}
		// Els números no es neutralitzen (un negatiu no és una fórmula)
		return valor instanceof Number ? valor.toString() : neutralitzarFormula(valorText(valor));
	}

	private String escapeCsv(String processat) {
		if (processat == null) {
			return "";
		}
		if (processat.indexOf(SEPARADOR_CSV) >= 0 || processat.indexOf('"') >= 0 ||
				processat.indexOf('\n') >= 0 || processat.indexOf('\r') >= 0) {
			return '"' + processat.replace("\"", "\"\"") + '"';
		}
		return processat;
	}

	private byte[] generarXlsx(List<?> files, List<String> columnes, List<String> capcaleres) {
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Dades");
			Font negreta = workbook.createFont();
			negreta.setBold(true);
			CellStyle estilCapcalera = workbook.createCellStyle();
			estilCapcalera.setFont(negreta);
			Row filaCapcalera = sheet.createRow(0);
			for (int i = 0; i < capcaleres.size(); i++) {
				Cell cell = filaCapcalera.createCell(i);
				cell.setCellValue(capcaleres.get(i));
				cell.setCellStyle(estilCapcalera);
			}
			int[] amplades = new int[columnes.size()];
			for (int i = 0; i < capcaleres.size(); i++) {
				amplades[i] = capcaleres.get(i).length();
			}
			int numFila = 1;
			if (files != null) {
				for (Object fila: files) {
					Row row = sheet.createRow(numFila++);
					for (int i = 0; i < columnes.size(); i++) {
						Object valor = getValor(fila, columnes.get(i));
						Cell cell = row.createCell(i);
						if (valor instanceof Number) {
							cell.setCellValue(((Number)valor).doubleValue());
						} else if (valor instanceof Boolean) {
							cell.setCellValue((Boolean)valor);
						} else if (valor != null) {
							cell.setCellValue(valorText(valor));
						}
						amplades[i] = Math.max(amplades[i], valorText(valor).length());
					}
				}
			}
			// Sense autoSizeColumn: necessita les fonts AWT, que poden faltar al servidor (headless)
			for (int i = 0; i < columnes.size(); i++) {
				sheet.setColumnWidth(i, Math.min(amplades[i] + 2, 100) * 256);
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			return baos.toByteArray();
		} catch (IOException ex) {
			throw new IllegalStateException("No s'ha pogut generar el fitxer XLSX", ex);
		}
	}

	private Object getValor(Object fila, String columna) {
		if (fila == null) {
			return null;
		}
		if (fila instanceof Map) {
			return ((Map<?, ?>)fila).get(columna);
		}
		BeanWrapperImpl bw = new BeanWrapperImpl(fila);
		return bw.isReadableProperty(columna) ? bw.getPropertyValue(columna) : null;
	}

	private String valorText(Object valor) {
		if (valor == null) {
			return "";
		} else if (valor instanceof Enum) {
			return ((Enum<?>)valor).name();
		} else if (valor instanceof Collection) {
			return ((Collection<?>)valor).stream().
					map(this::valorText).
					collect(Collectors.joining(", "));
		} else {
			return valor.toString();
		}
	}

	private String neutralitzarFormula(String valor) {
		if (!valor.isEmpty() && "=+-@".indexOf(valor.charAt(0)) >= 0) {
			return "'" + valor;
		}
		return valor;
	}

}
