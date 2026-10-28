package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.exception.ExportacioMassaGranException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Proves de la generació dels fitxers d'exportació i del control del màxim de files.
 */
class ExportacioHelperTest {

	private static final List<String> COLUMNES = Arrays.asList("codi", "nom", "actiu", "comptador", "llista");

	private ExportacioHelper helper(String maxim) {
		MockEnvironment env = new MockEnvironment();
		if (maxim != null) {
			env.setProperty(ExportacioHelper.PROP_EXPORTACIO_MAX, maxim);
		}
		return new ExportacioHelper(env);
	}

	private List<Fila> files() {
		return Arrays.asList(
				new Fila("A1", "Nom; amb \"cometes\"", true, 3, Arrays.asList("X", "Y")),
				new Fila("=B2", "Àvila", false, -1, Collections.emptyList()),
				new Fila("C3", null, true, 0, null));
	}

	@Test
	void superaMaximLlança() {
		ExportacioMassaGranException ex = assertThrows(
				ExportacioMassaGranException.class,
				() -> helper("10").comprovarMaxim(11));
		assertEquals(11, ex.getTotal());
		assertEquals(10, ex.getMaxim());
	}

	@Test
	void dinsMaximOk() {
		assertDoesNotThrow(() -> helper("10").comprovarMaxim(10));
		assertDoesNotThrow(() -> helper("10").comprovarMaxim(0));
	}

	@Test
	void maximPerDefecte() {
		assertEquals(10000, helper(null).getMaxim());
		assertEquals(10000, helper("no-numeric").getMaxim());
		assertDoesNotThrow(() -> helper(null).comprovarMaxim(10000));
		assertThrows(ExportacioMassaGranException.class, () -> helper(null).comprovarMaxim(10001));
	}

	@Test
	void csvTeCapcaleraIFiles() {
		byte[] csv = helper(null).generar(files(), COLUMNES, "CSV");
		// BOM UTF-8
		assertEquals((byte)0xEF, csv[0]);
		assertEquals((byte)0xBB, csv[1]);
		assertEquals((byte)0xBF, csv[2]);
		String text = new String(csv, 3, csv.length - 3, StandardCharsets.UTF_8);
		String[] linies = text.split("\r\n");
		assertEquals(4, linies.length);
		assertEquals("codi;nom;actiu;comptador;llista", linies[0]);
		assertEquals("A1;\"Nom; amb \"\"cometes\"\"\";true;3;X, Y", linies[1]);
		// Fórmula neutralitzada; número negatiu intacte; UTF-8
		assertEquals("'=B2;Àvila;false;-1;", linies[2]);
		assertEquals("C3;;true;0;", linies[3]);
	}

	@Test
	void csvAmbCapcaleresPersonalitzades() {
		byte[] csv = helper(null).generar(files(), COLUMNES, Arrays.asList("Codi", "Nom", "Actiu", "N", "L"), "csv");
		String text = new String(csv, 3, csv.length - 3, StandardCharsets.UTF_8);
		assertTrue(text.startsWith("Codi;Nom;Actiu;N;L\r\n"));
	}

	@Test
	void xlsxEsLlegible() throws Exception {
		byte[] xlsx = helper(null).generar(files(), COLUMNES, "XLSX");
		try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(xlsx))) {
			Sheet sheet = wb.getSheetAt(0);
			// Capçalera + 3 files
			assertEquals(3, sheet.getLastRowNum());
			assertEquals(4, sheet.getPhysicalNumberOfRows());
			assertEquals("codi", sheet.getRow(0).getCell(0).getStringCellValue());
			Row fila1 = sheet.getRow(1);
			assertEquals("A1", fila1.getCell(0).getStringCellValue());
			assertEquals("Nom; amb \"cometes\"", fila1.getCell(1).getStringCellValue());
			assertTrue(fila1.getCell(2).getBooleanCellValue());
			assertEquals(3d, fila1.getCell(3).getNumericCellValue());
			assertEquals("X, Y", fila1.getCell(4).getStringCellValue());
			assertEquals("Àvila", sheet.getRow(2).getCell(1).getStringCellValue());
		}
	}

	@Test
	void formatNoSuportat() {
		assertThrows(IllegalArgumentException.class, () -> helper(null).generar(files(), COLUMNES, "PDF"));
	}

	public static class Fila {
		private final String codi;
		private final String nom;
		private final boolean actiu;
		private final long comptador;
		private final List<String> llista;
		Fila(String codi, String nom, boolean actiu, long comptador, List<String> llista) {
			this.codi = codi;
			this.nom = nom;
			this.actiu = actiu;
			this.comptador = comptador;
			this.llista = llista;
		}
		public String getCodi() { return codi; }
		public String getNom() { return nom; }
		public boolean isActiu() { return actiu; }
		public long getComptador() { return comptador; }
		public List<String> getLlista() { return llista; }
	}

}
