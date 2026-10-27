package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto;
import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.service.ws.backoffice.TransmisionDatos;
import es.caib.emiserv.persist.entity.BackofficePeticioEntity;
import es.caib.emiserv.persist.entity.BackofficeSolicitudEntity;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCorePeticionRespuestaEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.repository.BackofficePeticioRepository;
import es.caib.emiserv.persist.repository.BackofficeSolicitudRepository;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreTransmisionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Enviament de les sol·licituds de les consultes asíncrones processades com a síncrones: reintents,
 * errors definitius, recompte de la petició, recuperació d'enviaments interromputs i transmissions
 * de les sol·licituds amb error.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class BackofficeHelperAsincronTest {

	private BackofficeHelper backofficeHelper;
	private BackofficeSolicitudRepository backofficeSolicitudRepository;
	private BackofficePeticioRepository backofficePeticioRepository;
	private ScspCoreTransmisionRepository scspCoreTransmisionRepository;
	private MockEnvironment environment;
	private BackofficePeticioEntity peticio;

	@BeforeEach
	void setUp() {
		backofficeHelper = new BackofficeHelper();
		backofficeSolicitudRepository = mock(BackofficeSolicitudRepository.class);
		backofficePeticioRepository = mock(BackofficePeticioRepository.class);
		scspCoreTransmisionRepository = mock(ScspCoreTransmisionRepository.class);
		ScspCoreServicioRepository scspCoreServicioRepository = mock(ScspCoreServicioRepository.class);
		ConfiguracioAsincronHelper configuracio = new ConfiguracioAsincronHelper();
		environment = new MockEnvironment();
		ReflectionTestUtils.setField(configuracio, "environment", environment);
		ReflectionTestUtils.setField(backofficeHelper, "configuracioAsincronHelper", configuracio);
		ReflectionTestUtils.setField(backofficeHelper, "backofficeSolicitudRepository", backofficeSolicitudRepository);
		ReflectionTestUtils.setField(backofficeHelper, "backofficePeticioRepository", backofficePeticioRepository);
		ReflectionTestUtils.setField(backofficeHelper, "scspCoreTransmisionRepository", scspCoreTransmisionRepository);
		ReflectionTestUtils.setField(backofficeHelper, "scspCoreServicioRepository", scspCoreServicioRepository);
		ReflectionTestUtils.setField(backofficeHelper, "xmlHelper", new XmlHelper());
		ScspCoreServicioEntity servicio = ScspCoreServicioEntity.getBuilder("SCDCPAJU").build();
		ReflectionTestUtils.setField(servicio, "versionEsquema", "V3");
		when(scspCoreServicioRepository.findByCodigoCertificado("SCDCPAJU")).thenReturn(servicio);
		// Servei de la petició (les sol·licituds dels serveis backoffice s'envien al backoffice)
		when(scspCoreServicioRepository.getOne(10L)).thenReturn(servicio);
		ServeiRepository serveiRepository = mock(ServeiRepository.class);
		when(serveiRepository.findByCodi("SCDCPAJU")).thenReturn(ServeiEntity.getBuilder("SCDCPAJU", "Padró", ServeiTipusEnumDto.BACKOFFICE).build());
		ReflectionTestUtils.setField(backofficeHelper, "serveiRepository", serveiRepository);
		peticio = BackofficePeticioEntity.getBuilder(
				ScspCorePeticionRespuestaEntity.getBuilder(10L, "PBLASYNC0001", "0002").build(),
				new Date()).build();
		ReflectionTestUtils.setField(peticio, "id", 1L);
	}

	@Test
	void unErrorTemporalEsReprogramaAmbEsperaCreixent() {
		environment.setProperty("es.caib.emiserv.async.reintents.espera", "60");
		BackofficeSolicitudEntity solicitud = solicitudEnviant(100L, 2);
		when(scspCoreTransmisionRepository.findByPeticionIdAndSolicitudId(any(), any())).thenThrow(new RuntimeException("Connection refused"));

		long abans = System.currentTimeMillis();
		backofficeHelper.enviarSolicitud(100L);

		assertEquals(BackofficeSolicitudEstatEnumDto.PENDENT, solicitud.getEstat());
		assertTrue(solicitud.getDarrerError().contains("Connection refused"));
		// Segon intent fallit: espera de 2 x 60 s
		long espera = solicitud.getDataProperIntent().getTime() - abans;
		assertTrue(espera >= 119000 && espera <= 121000, "Espera: " + espera);
	}

	@Test
	void esgotatsElsIntentsLaSolicitudFinalitzaAmbError() {
		BackofficeSolicitudEntity solicitud = solicitudEnviant(100L, 3);
		when(scspCoreTransmisionRepository.findByPeticionIdAndSolicitudId(any(), any())).thenThrow(new RuntimeException("Read timed out"));

		backofficeHelper.enviarSolicitud(100L);

		assertEquals(BackofficeSolicitudEstatEnumDto.ERROR, solicitud.getEstat());
		assertEquals("0503", solicitud.getErrorCodi());
		assertNull(solicitud.getDataProperIntent());
	}

	@Test
	void unErrorDeDadesEsDefinitiu() {
		// No es troba la transmissió de la sol·licitud: no té sentit reintentar-ho
		BackofficeSolicitudEntity solicitud = solicitudEnviant(100L, 1);
		when(scspCoreTransmisionRepository.findByPeticionIdAndSolicitudId(any(), any())).thenReturn(null);

		backofficeHelper.enviarSolicitud(100L);

		assertEquals(BackofficeSolicitudEstatEnumDto.ERROR, solicitud.getEstat());
		assertEquals(1, solicitud.getIntents());
	}

	@Test
	void unaSolicitudNoReservadaNoSEnvia() {
		BackofficeSolicitudEntity solicitud = BackofficeSolicitudEntity.getBuilder(peticio, "000001").build();
		when(backofficeSolicitudRepository.getOne(100L)).thenReturn(solicitud);

		backofficeHelper.enviarSolicitud(100L);

		assertEquals(BackofficeSolicitudEstatEnumDto.PENDENT, solicitud.getEstat());
		assertEquals(0, solicitud.getIntents());
	}

	@Test
	void laPeticioFinalitzaQuanTotesLesSolicitudsEstanProcessades() {
		when(backofficePeticioRepository.findAmbBloqueig(1L)).thenReturn(peticio);
		when(backofficeSolicitudRepository.countByPeticio(peticio)).thenReturn(3);
		when(backofficeSolicitudRepository.countByPeticioAndEstat(peticio, BackofficeSolicitudEstatEnumDto.TRAMITADA)).thenReturn(1);
		when(backofficeSolicitudRepository.countByPeticioAndEstat(peticio, BackofficeSolicitudEstatEnumDto.ERROR)).thenReturn(1);

		backofficeHelper.actualitzarEstatPeticio(1L, null);

		assertEquals(PeticioEstatEnumDto.PENDENT, peticio.getEstat());
		assertEquals(2, peticio.getProcessadesTotal());

		when(backofficeSolicitudRepository.countByPeticioAndEstat(peticio, BackofficeSolicitudEstatEnumDto.TRAMITADA)).thenReturn(2);

		backofficeHelper.actualitzarEstatPeticio(1L, null);

		assertEquals(PeticioEstatEnumDto.ERROR, peticio.getEstat());
		assertEquals(3, peticio.getProcessadesTotal());
		assertEquals(1, peticio.getProcessadesError());
	}

	@Test
	void recuperacioDelsEnviamentsInterromputs() {
		BackofficeSolicitudEntity enCurs = solicitudEnviant(100L, 1);
		BackofficeSolicitudEntity perReintentar = solicitudEnviant(101L, 1);
		BackofficeSolicitudEntity esgotada = solicitudEnviant(102L, 3);
		when(backofficeSolicitudRepository.findByEstatAndDataEnviamentBefore(eq(BackofficeSolicitudEstatEnumDto.ENVIANT), any())).thenReturn(
				List.of(enCurs, perReintentar, esgotada));

		Set<Long> peticions = backofficeHelper.recuperarEnviamentsInterromputs(Collections.singleton(100L));

		assertEquals(BackofficeSolicitudEstatEnumDto.ENVIANT, enCurs.getEstat());
		assertEquals(BackofficeSolicitudEstatEnumDto.PENDENT, perReintentar.getEstat());
		assertEquals(BackofficeSolicitudEstatEnumDto.ERROR, esgotada.getEstat());
		assertEquals(Set.of(1L), peticions);
	}

	@Test
	void transmissioDUnaSolicitudAmbError() throws Exception {
		BackofficeSolicitudEntity solicitud = BackofficeSolicitudEntity.getBuilder(peticio, "000002").build();
		solicitud.finalitzarAmbError("0503", "ConnectException: Connection refused <cedent>");

		TransmisionDatos transmision = backofficeHelper.solicitudErrorToTransmisionDatos(peticio, solicitud, "SCDCPAJU");

		assertEquals("000002", transmision.getDatosGenericos().getTransmision().getIdSolicitud());
		Element datosEspecificos = (Element)transmision.getDatosEspecificos();
		assertEquals("http://intermediacion.redsara.es/scsp/esquemas/datosespecificos", datosEspecificos.getNamespaceURI());
		assertEquals("0503", datosEspecificos.getElementsByTagNameNS("*", "CodigoEstado").item(0).getTextContent());
		assertNotNull(datosEspecificos.getElementsByTagNameNS("*", "LiteralError").item(0));
		assertTrue(datosEspecificos.getElementsByTagNameNS("*", "LiteralError").item(0).getTextContent().contains("<cedent>"));
	}

	private BackofficeSolicitudEntity solicitudEnviant(Long id, int intents) {
		BackofficeSolicitudEntity solicitud = BackofficeSolicitudEntity.getBuilder(peticio, "00000" + id).build();
		ReflectionTestUtils.setField(solicitud, "id", id);
		for (int i = 0; i < intents; i++) {
			solicitud.iniciarEnviament(new Date());
		}
		when(backofficeSolicitudRepository.getOne(id)).thenReturn(solicitud);
		return solicitud;
	}

}
