package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.persist.entity.BackofficeSolicitudEntity;
import es.caib.emiserv.persist.entity.ServeiEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Configuració i model de les consultes asíncrones dels serveis enrutadors processades com a
 * peticions síncrones a cada cedent.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class ConfiguracioAsincronHelperTest {

	private ConfiguracioAsincronHelper helper;
	private MockEnvironment environment;

	@BeforeEach
	void setUp() {
		helper = new ConfiguracioAsincronHelper();
		environment = new MockEnvironment();
		ReflectionTestUtils.setField(helper, "environment", environment);
	}

	@Test
	void valorsPerDefecte() {
		assertEquals(10000, helper.getPeriodePlanificadorMs());
		assertEquals(10, helper.getConcurrenciaGlobal());
		assertEquals(1000, helper.getIntervalCedentMs(null));
		assertEquals(2, helper.getConcurrenciaCedent(null));
		assertEquals(120, helper.getTimeoutSegons());
		assertEquals(3, helper.getReintentsMaxim());
		assertEquals(600, helper.getEnviamentCaducatSegons());
		assertEquals(100, helper.getTerMargePercent());
		assertEquals(60, helper.getTerMinimMinuts());
		assertEquals(1440, helper.getTerMaximMinuts());
		assertEquals("EMS", helper.getPrefixIdPeticio());
		assertEquals("0227", helper.getCodiCancelacio());
	}

	@Test
	void valorsConfiguratsIValorsIncorrectes() {
		environment.setProperty("es.caib.emiserv.async.concurrencia.global", "4");
		environment.setProperty("es.caib.emiserv.async.cedent.interval", "abc");
		environment.setProperty("es.caib.emiserv.async.cedent.concurrencia", "0");
		environment.setProperty("es.caib.emiserv.async.idpeticio.prefix", " CAIB ");

		assertEquals(4, helper.getConcurrenciaGlobal());
		// Valors no numèrics o per sota del mínim: es fa servir el valor per defecte
		assertEquals(1000, helper.getIntervalCedentMs(null));
		assertEquals(2, helper.getConcurrenciaCedent(null));
		assertEquals("CAIB", helper.getPrefixIdPeticio());
	}

	@Test
	void ritmeDelServeiPrevalSobreElGlobal() {
		environment.setProperty("es.caib.emiserv.async.cedent.interval", "5000");
		ServeiEntity servei = enrutador();
		servei.updateAsyncSincron(true, 250, 4);

		assertEquals(250, helper.getIntervalCedentMs(servei));
		assertEquals(4, helper.getConcurrenciaCedent(servei));

		servei.updateAsyncSincron(true, null, null);

		assertEquals(5000, helper.getIntervalCedentMs(servei));
		assertEquals(2, helper.getConcurrenciaCedent(servei));
	}

	@Test
	void esperaEntreReintentsEsDobla() {
		environment.setProperty("es.caib.emiserv.async.reintents.espera", "60");

		assertEquals(60000, helper.getEsperaReintentMs(1));
		assertEquals(120000, helper.getEsperaReintentMs(2));
		assertEquals(240000, helper.getEsperaReintentMs(3));
	}

	@Test
	void terMaximNoPotSerInferiorAlMinim() {
		environment.setProperty("es.caib.emiserv.async.ter.minim", "120");
		environment.setProperty("es.caib.emiserv.async.ter.maxim", "30");

		assertEquals(120, helper.getTerMaximMinuts());
	}

	@Test
	void tempsEstimatDeResposta() {
		// 100 sol·licituds, 1 s entre enviaments, 2 simultanis (3 s / 2 = 1,5 s per sol·licitud), marge 100%: 5 min
		assertEquals(5, ConfiguracioAsincronHelper.calcularTerMinuts(100, 1000, 2, 100, 0, 1440));
		// Mínim
		assertEquals(60, ConfiguracioAsincronHelper.calcularTerMinuts(100, 1000, 2, 100, 60, 1440));
		// 100.000 sol·licituds: més de 24 h, es limita al màxim
		assertEquals(1440, ConfiguracioAsincronHelper.calcularTerMinuts(100000, 1000, 2, 100, 60, 1440));
		// L'interval és el factor limitant: 600 sol·licituds x 5 s x 1,5 = 75 min
		assertEquals(75, ConfiguracioAsincronHelper.calcularTerMinuts(600, 5000, 4, 50, 60, 1440));
	}

	@Test
	void nomesElsEnrutadorsAcceptenAsincronesProcessadesComASincrones() {
		ServeiEntity enrutador = enrutador();
		ServeiEntity backoffice = ServeiEntity.getBuilder("SVDSCDWS01", "Canvi de domicili", ServeiTipusEnumDto.BACKOFFICE).build();

		enrutador.updateAsyncSincron(true, 1000, 2);
		backoffice.updateAsyncSincron(true, 1000, 2);

		assertTrue(enrutador.isAsyncSincron());
		assertFalse(backoffice.isAsyncSincron());
		assertNull(backoffice.getAsyncInterval());

		enrutador.updateAsyncSincron(false, 1000, 2);

		assertFalse(enrutador.isAsyncSincron());
		assertNull(enrutador.getAsyncInterval());
		assertNull(enrutador.getAsyncConcurrencia());
	}

	@Test
	void transicionsDUnaSolicitud() {
		BackofficeSolicitudEntity solicitud = BackofficeSolicitudEntity.getBuilder(null, "000001").build();
		Date ara = new Date();

		solicitud.iniciarEnviament(ara);

		assertEquals(BackofficeSolicitudEstatEnumDto.ENVIANT, solicitud.getEstat());
		assertEquals(1, solicitud.getIntents());
		assertEquals(ara, solicitud.getDataEnviament());

		Date properIntent = new Date(ara.getTime() + 60000);
		solicitud.reprogramar(repetir("x", 5000), properIntent);

		assertEquals(BackofficeSolicitudEstatEnumDto.PENDENT, solicitud.getEstat());
		assertEquals(4000, solicitud.getDarrerError().length());
		assertEquals(properIntent, solicitud.getDataProperIntent());

		solicitud.iniciarEnviament(new Date());
		solicitud.reiniciarIntents();

		assertEquals(BackofficeSolicitudEstatEnumDto.PENDENT, solicitud.getEstat());
		assertEquals(0, solicitud.getIntents());
		assertNull(solicitud.getDataProperIntent());
	}

	private static ServeiEntity enrutador() {
		return ServeiEntity.getBuilder("SCDCPAJU", "Padró de convivència", ServeiTipusEnumDto.ENRUTADOR).build();
	}

	private static String repetir(String text, int vegades) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < vegades; i++) {
			sb.append(text);
		}
		return sb.toString();
	}

}
