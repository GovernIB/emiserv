package es.caib.emiserv.logic.helper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.env.MockEnvironment;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.StringReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Les plantilles fake per defecte (amb el valor per defecte de cada marcador) són vàlides segons
 * l'XSD de dades específiques del servei ({@code src/test/resources/fake/xsd/<CODI>.xsd}, els
 * mateixos XSD que té configurats EMISERV a PRE).
 * <p>
 * SVDNIVCAT i SVDCCAAIRPHWS01 no hi són: els seus backoffices reals no segueixen l'XSD (elements
 * en minúscules: {@code retorno/codigo/descripcion/persona}, {@code Estado/codigoEstado}) i les
 * plantilles reprodueixen les respostes reals, que són les que reben els consumidors.
 */
class ServeiFakePlantillesXsdTest {

	private static final Pattern MARCADOR = Pattern.compile("\\$\\{[A-Za-z0-9]+:([^}]*)}");

	@ParameterizedTest
	@ValueSource(strings = {
			"SCDCPAJU", "SCDHPAJU", "SCMCEDU", "SVDCCAACPASWS01", "SVDCCAACPCWS01",
			"SVDSCDDWS01", "SVDSCDDWS02", "SVDSCDWS01", "SVDSCTFNWS01" })
	void plantillaValidaSegonsXsd(String codi) throws Exception {
		String plantilla = new ServeiFakeHelper(new MockEnvironment()).plantillaPerDefecte(codi);
		assertNotNull(plantilla, codi);
		Matcher m = MARCADOR.matcher(plantilla);
		String xml = m.replaceAll(r -> Matcher.quoteReplacement(r.group(1)));
		URL xsd = getClass().getResource("/fake/xsd/" + codi + ".xsd");
		assertNotNull(xsd, "Falta l'XSD de " + codi);
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// Només els fitxers locals (imports relatius), sense accés a la xarxa
		factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "file");
		Validator validator = factory.newSchema(xsd).newValidator();
		assertDoesNotThrow(() -> validator.validate(new StreamSource(new StringReader(xml))), codi);
	}

}
