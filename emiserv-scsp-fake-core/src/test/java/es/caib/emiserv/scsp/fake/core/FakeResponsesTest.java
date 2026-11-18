package es.caib.emiserv.scsp.fake.core;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FakeResponsesTest {

	@Test
	void respostaSenseDadesDelPadroNoTeInscripcions() throws IOException {
		FakeRequest r = FakeRequest.fromXml(peticioPadro());

		String senseDades = FakeResponses.datosEspecificos(r, true);
		String ambDades = FakeResponses.datosEspecificos(r, false);

		assertFalse(senseDades.contains("<Inscripcion"));
		assertFalse(senseDades.contains("<Persona>"));
		assertTrue(senseDades.contains("<DatosEspecificos"));
		// El generador de padró no usa l'element Inscripcion: les inscripcions són les Persona
		assertTrue(ambDades.contains("<Persona>"));
	}

	@Test
	void respostaRepeteixDadesPerCadaSolicitud() throws IOException {
		FakeRequest r = FakeRequest.fromXml(peticioPadro());
		assertEquals(2, r.getSolicitudIds().size());

		String xml = FakeResponses.scspResponse(r, FakeResponses.datosEspecificos(r, false), "[FAKE] OK");

		assertEquals(2, count(xml, "<TransmisionDatos>"));
		assertEquals(2, count(xml, "<DatosEspecificos "));
		assertTrue(xml.contains("<LiteralError>[FAKE] OK</LiteralError>"));
		assertTrue(xml.contains("<CodigoEstado>0003</CodigoEstado>"));
		assertTrue(xml.contains("<IdSolicitud>SOL-1</IdSolicitud>"));
		assertTrue(xml.contains("<IdSolicitud>SOL-2</IdSolicitud>"));
	}

	@Test
	void llegeixPeticioDelModelBackoffice() throws IOException {
		String xml = "<peticion xmlns=\"http://caib.es/emiserv/backoffice\">"
			+ "<atributos><codigoCertificado>SVDTEST</codigoCertificado><idPeticion>PET-9</idPeticion></atributos>"
			+ "</peticion>";

		FakeRequest r = FakeRequest.fromXml(xml);

		assertEquals("SVDTEST", r.getServiceCode());
		assertEquals("PET-9", r.getIdPeticion());
		assertEquals(1, r.getSolicitudIds().size());
	}

	@Test
	void rebutjaPeticionsAmbDoctypeIEntitatsExternes() {
		String xml = "<?xml version=\"1.0\"?><!DOCTYPE peticion [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>"
			+ "<peticion><atributos><idPeticion>&xxe;</idPeticion></atributos></peticion>";

		assertThrows(IOException.class, () -> FakeRequest.fromXml(xml));
	}

	@Test
	void faultAmbCodiIMissatge() throws IOException {
		FakeRequest r = FakeRequest.fromXml(peticioPadro());

		String xml = FakeResponses.scspFault(r, "0502", "[FAKE] Error simulat");

		assertTrue(xml.contains("<a:CodigoEstado>0502</a:CodigoEstado>"));
		assertTrue(xml.contains("<a:LiteralError>[FAKE] Error simulat</a:LiteralError>"));
		assertTrue(xml.contains("<a:IdPeticion>PET-PADRO-1</a:IdPeticion>"));
	}

	@Test
	void liniaDelTitularIEstilDeSignatura() throws IOException {
		FakeRequest r = FakeRequest.fromXml(peticioPadro());

		assertEquals("SCDCPAJU", r.getServiceCode());
		assertEquals("JOAN", r.getTitularNom());
		assertEquals("PONS", r.getTitularLlinatge1());
		assertEquals(SignatureStyle.XML_SIGNATURE, r.getSignatureStyle());
	}

	private static int count(String text, String part) {
		int n = 0;
		for (int i = text.indexOf(part); i >= 0; i = text.indexOf(part, i + part.length())) {
			n++;
		}
		return n;
	}

	private static String peticioPadro() throws IOException {
		try (InputStream in = FakeResponsesTest.class.getResourceAsStream("/peticio-scdcpaju.xml")) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
