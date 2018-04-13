/**
 * 
 */
package es.caib.emiserv.core.backoffice;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import es.caib.emiserv.core.helper.BackofficeHelper;

/**
 * Test de modificació dels DatosEspecificos.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DatosEspecificosTest {

	private static final String DATOS_ESPECIFICOS_TEST_1 = "<DatosEspecificos xmlns=\"namespacedeprova\"><tag1>ValorTag1</tag1><tag2>ValorTag2</tag2></DatosEspecificos>";
	private static final String DATOS_ESPECIFICOS_TEST_2 = "<datosespecificos xmlns=\"namespacedeprova\"><tag1>ValorTag1</tag1><tag2>ValorTag2</tag2></datosespecificos>";
	private static final String DATOS_ESPECIFICOS_RESULTAT_1 = "<DatosEspecificos><tag1>ValorTag1</tag1><tag2>ValorTag2</tag2></DatosEspecificos>";
	private static final String DATOS_ESPECIFICOS_RESULTAT_2 = "<datosespecificos><tag1>ValorTag1</tag1><tag2>ValorTag2</tag2></datosespecificos>";

	private static final String DATOS_ESPECIFICOS_TEST_NS = "<ns1:DatosEspecificos xmlns:ns1=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\"><ns1:Consulta><ns1:CodigoComunidadAutonoma>04</ns1:CodigoComunidadAutonoma><ns1:CodigoProvincia>07</ns1:CodigoProvincia><ns1:ConsentimientoTiposDiscapacidad>S</ns1:ConsentimientoTiposDiscapacidad></ns1:Consulta></ns1:DatosEspecificos>";
	private static final String DATOS_ESPECIFICOS_RESULTAT_NS = "<DatosEspecificos><Consulta><CodigoComunidadAutonoma>04</CodigoComunidadAutonoma><CodigoProvincia>07</CodigoProvincia><ConsentimientoTiposDiscapacidad>S</ConsentimientoTiposDiscapacidad></Consulta></DatosEspecificos>";

	private BackofficeHelper backofficeHelper;

	@Before
	public void setUp() throws Exception {
		backofficeHelper = new BackofficeHelper();
		System.setProperty(
				"es.caib.emiserv.backoffice.processar.datos.especificos.peticio",
				"false");
	}

	@Test
	public void datosEspecificosPeticio() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Method metode = BackofficeHelper.class.getDeclaredMethod(
				"modificarDatosEspecificos",
				String.class,
				String.class);
		metode.setAccessible(true);
		String datosEspecificosFinals1 = (String)metode.invoke(
				backofficeHelper,
				DATOS_ESPECIFICOS_TEST_1,
				null);
		assertEquals(DATOS_ESPECIFICOS_RESULTAT_1, datosEspecificosFinals1);
		String datosEspecificosFinals2 = (String)metode.invoke(
				backofficeHelper,
				DATOS_ESPECIFICOS_TEST_2,
				null);
		assertEquals(DATOS_ESPECIFICOS_RESULTAT_2, datosEspecificosFinals2);
		System.setProperty(
				"es.caib.emiserv.backoffice.processar.datos.especificos.peticio",
				"true");
		String datosEspecificosFinals3 = (String)metode.invoke(
				backofficeHelper,
				DATOS_ESPECIFICOS_TEST_2,
				null);
		assertEquals(DATOS_ESPECIFICOS_RESULTAT_1, datosEspecificosFinals3);
	}

	@Test
	public void datosEspecificosAmbNamespaces() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Method metode = BackofficeHelper.class.getDeclaredMethod(
				"modificarDatosEspecificos",
				String.class,
				String.class);
		metode.setAccessible(true);
		String datosEspecificosFinals = (String)metode.invoke(
				backofficeHelper,
				DATOS_ESPECIFICOS_TEST_NS,
				null);
		assertEquals(DATOS_ESPECIFICOS_RESULTAT_NS, datosEspecificosFinals);
	}

}
