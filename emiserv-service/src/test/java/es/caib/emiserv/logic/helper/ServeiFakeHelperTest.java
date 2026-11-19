package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.dto.ServeiFakeResultatEnumDto;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.scsp.fake.core.FakeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de {@link ServeiFakeHelper}.
 */
class ServeiFakeHelperTest {

	private static String xmlPeticio() throws IOException {
		return new String(
				Files.readAllBytes(Paths.get("src/test/resources/peticio-scdcpaju.xml")),
				StandardCharsets.UTF_8);
	}

	private static FakeRequest peticio() throws IOException {
		return FakeRequest.fromXml(xmlPeticio());
	}

	/** Petició amb la documentació del titular als DatosGenericos (com les peticions SCSP reals). */
	private static FakeRequest peticioAmbDocument() throws IOException {
		return FakeRequest.fromXml(xmlPeticio().replace(
				"<scsp:Titular><scsp:Nombre>JOAN",
				"<scsp:Titular><scsp:Documentacion>12345678Z</scsp:Documentacion><scsp:Nombre>JOAN"));
	}

	private static ServeiEntity servei(boolean fake, ServeiFakeResultatEnumDto resultat, String plantilla) {
		ServeiEntity servei = ServeiEntity.getBuilder("SCDCPAJU", "Padró", null).build();
		servei.updateFake(fake, resultat, plantilla);
		return servei;
	}

	private static ServeiFakeHelper helper(Boolean permes) {
		MockEnvironment env = new MockEnvironment();
		if (permes != null) {
			env.setProperty(ServeiFakeHelper.PROPIETAT_PERMES, permes.toString());
		}
		return new ServeiFakeHelper(env);
	}

	@Test
	void noPermesSiLaPropietatNoHiEs() {
		assertFalse(helper(null).isPermes());
		assertTrue(helper(true).isPermes());
	}

	@Test
	void isFakeRequereixPropietatIFlag() {
		ServeiEntity fake = servei(true, ServeiFakeResultatEnumDto.OK, null);
		ServeiEntity noFake = servei(false, ServeiFakeResultatEnumDto.OK, null);
		assertTrue(helper(true).isFake(fake));
		assertFalse(helper(true).isFake(noFake));
		assertFalse(helper(false).isFake(fake));
		assertFalse(helper(false).isFake(noFake));
		assertFalse(helper(true).isFake(null));
	}

	@Test
	void plantillaSubstitueixMarcadors() throws IOException {
		ServeiEntity servei = servei(true, ServeiFakeResultatEnumDto.OK,
				"<X><Doc>${documentacion}</Doc><Cod>${codigoCertificado}</Cod><Pet>${idPeticion}</Pet>"
						+ "<Sol>${idSolicitud}</Sol><N>${nombre}</N>${desconegut}</X>");
		String xml = helper(true).dadesEspecifiques(servei, peticioAmbDocument(), "SOL-1");
		assertTrue(xml.contains("<Doc>12345678Z</Doc>"), xml);
		assertTrue(xml.contains("<Cod>SCDCPAJU</Cod>"), xml);
		assertTrue(xml.contains("<Pet>PET-PADRO-1</Pet>"), xml);
		assertTrue(xml.contains("<Sol>SOL-1</Sol>"), xml);
		assertTrue(xml.contains("<N>JOAN</N>"), xml);
		assertTrue(xml.contains("${desconegut}"), xml);
	}

	@Test
	void plantillaEscapaXml() throws IOException {
		FakeRequest peticio = FakeRequest.fromXml(xmlPeticio().replace("JOAN", "A&amp;B"));
		ServeiEntity servei = servei(true, ServeiFakeResultatEnumDto.OK, "<N>${nombre}</N>");
		assertEquals("<N>A&amp;B</N>", helper(true).dadesEspecifiques(servei, peticio, "SOL-1"));
	}

	@Test
	void senseplantillaFaServirElGenerador() throws IOException {
		String senseDades = helper(true).dadesEspecifiques(
				servei(true, ServeiFakeResultatEnumDto.SENSE_DADES, null), peticio(), "SOL-1");
		assertFalse(senseDades.contains("<Persona"), senseDades);
		String ok = helper(true).dadesEspecifiques(
				servei(true, ServeiFakeResultatEnumDto.OK, "  "), peticio(), "SOL-1");
		assertTrue(ok.contains("<Persona"), ok);
	}

	@Test
	void marcadorAmbValorPerDefecte() throws IOException {
		ServeiEntity servei = servei(true, ServeiFakeResultatEnumDto.OK,
				"<X><N>${nombre:PERSONA}</N><A>${apellido2:FAKE}</A><B>${apellido2}</B><Z>${desconegut:Z}</Z></X>");
		String xml = helper(true).dadesEspecifiques(servei, peticio(), "SOL-1");
		// El valor de la petició té preferència; si no n'hi ha, el per defecte
		assertTrue(xml.contains("<N>JOAN</N>"), xml);
		assertTrue(xml.contains("<A>FAKE</A>"), xml);
		assertTrue(xml.contains("<B></B>"), xml);
		assertTrue(xml.contains("${desconegut:Z}"), xml);
	}

	@Test
	void plantillesPerDefecte() {
		ServeiFakeHelper helper = helper(true);
		for (String codi: new String[] {
				"SCDCPAJU", "SCDHPAJU", "SCMCEDU", "SVDCCAACPASWS01", "SVDCCAACPCWS01",
				"SVDCCAAIRPHWS01", "SVDNIVCAT", "SVDSCDDWS01", "SVDSCDDWS02", "SVDSCDWS01", "SVDSCTFNWS01"}) {
			String plantilla = helper.plantillaPerDefecte(codi);
			assertNotNull(plantilla, codi);
			assertTrue(plantilla.startsWith("<DatosEspecificos xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">"), codi);
		}
		assertEquals(helper.plantillaPerDefecte("SCDCPAJU"), helper.plantillaPerDefecte(" scdcpaju "));
		assertNull(helper.plantillaPerDefecte("DESCONEGUT"));
		assertNull(helper.plantillaPerDefecte("../plantilles/SCDCPAJU"));
		assertNull(helper.plantillaPerDefecte(""));
		assertNull(helper.plantillaPerDefecte(null));
	}

	@Test
	void plantillaPerDefecteAmbLesDadesDeLaPeticio() throws IOException {
		ServeiFakeHelper helper = helper(true);
		ServeiEntity servei = servei(true, ServeiFakeResultatEnumDto.OK, helper.plantillaPerDefecte("SCDCPAJU"));
		String xml = helper.dadesEspecifiques(servei, peticio(), "SOL-1");
		assertFalse(xml.contains("${"), xml);
		assertTrue(xml.contains("<Nombre>JOAN</Nombre>"), xml);
	}

	@Test
	void literalEstat() {
		assertEquals("[FAKE] Peticion procesada correctamente.",
				helper(true).literalEstat(servei(true, ServeiFakeResultatEnumDto.OK, null)));
		assertEquals("[FAKE] Sin datos.",
				helper(true).literalEstat(servei(true, ServeiFakeResultatEnumDto.SENSE_DADES, null)));
	}

}
