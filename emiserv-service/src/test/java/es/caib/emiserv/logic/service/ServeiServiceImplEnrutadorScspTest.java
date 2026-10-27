package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.helper.PermisosHelper;
import es.caib.emiserv.logic.helper.SecurityHelper;
import es.caib.emiserv.logic.intf.dto.ServeiConfigScspDto;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiDto;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmBackofficeEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.repository.RedireccioPeticioRepository;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.ServeiRutaDestiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreEmBackofficeRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreEmisorCertificadoRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCorePeticionRespuestaRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Configuració a l'emisor SCSP dels serveis enrutadors, necessària per a les rutes de tipus
 * BACKOFFICE (consulta directa al backoffice del cedent).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class ServeiServiceImplEnrutadorScspTest {

	private static final long SERVEI_ID = 10L;
	private static final long CORE_SERVICIO_ID = 20L;

	private ServeiServiceImpl serveiService;
	private ServeiRepository serveiRepository;
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	private ScspCoreServicioRepository scspCoreServicioRepository;
	private ScspCoreEmBackofficeRepository scspEmisorBackofficeRepository;
	private ServeiEntity servei;
	private ScspCoreServicioEntity scspCoreServicio;

	@BeforeEach
	void setUp() {
		serveiService = new ServeiServiceImpl();
		serveiRepository = mock(ServeiRepository.class);
		serveiRutaDestiRepository = mock(ServeiRutaDestiRepository.class);
		scspCoreServicioRepository = mock(ScspCoreServicioRepository.class);
		scspEmisorBackofficeRepository = mock(ScspCoreEmBackofficeRepository.class);
		SecurityHelper securityHelper = mock(SecurityHelper.class);
		when(securityHelper.hasRole("EMS_ADMIN")).thenReturn(true);
		ReflectionTestUtils.setField(serveiService, "serveiRepository", serveiRepository);
		ReflectionTestUtils.setField(serveiService, "serveiRutaDestiRepository", serveiRutaDestiRepository);
		ReflectionTestUtils.setField(serveiService, "scspCoreServicioRepository", scspCoreServicioRepository);
		ReflectionTestUtils.setField(serveiService, "scspEmisorBackofficeRepository", scspEmisorBackofficeRepository);
		ReflectionTestUtils.setField(serveiService, "scspCoreEmisorCertificadoRepository", mock(ScspCoreEmisorCertificadoRepository.class));
		ReflectionTestUtils.setField(serveiService, "securityHelper", securityHelper);
		ReflectionTestUtils.setField(serveiService, "conversioTipusHelper", mock(ConversioTipusHelper.class));
		ReflectionTestUtils.setField(serveiService, "scspCorePeticionRespuestaRepository", mock(ScspCorePeticionRespuestaRepository.class));
		ReflectionTestUtils.setField(serveiService, "redireccioPeticioRepository", mock(RedireccioPeticioRepository.class));
		ReflectionTestUtils.setField(serveiService, "permisosHelper", mock(PermisosHelper.class));
		servei = ServeiEntity.getBuilder("SCDCPAJU", "Padró de convivència", ServeiTipusEnumDto.ENRUTADOR).
				resolverClass("es.caib.emiserv.logic.resolver.EmpadronamentEntitatResolver").
				build();
		ReflectionTestUtils.setField(servei, "id", SERVEI_ID);
		when(serveiRepository.findById(SERVEI_ID)).thenReturn(Optional.of(servei));
		scspCoreServicio = ScspCoreServicioEntity.getBuilder("SCDCPAJU").build();
		ReflectionTestUtils.setField(scspCoreServicio, "id", CORE_SERVICIO_ID);
	}

	/** Simula que el servei s'ha configurat a l'emisor SCSP (existeix el CORE_SERVICIO). */
	private void configuratScsp() {
		when(scspCoreServicioRepository.findByCodigoCertificado("SCDCPAJU")).thenReturn(scspCoreServicio);
	}

	@Test
	void configuracioScspEnrutadorCreaBackofficeCaib() {
		configuratScsp();
		when(scspEmisorBackofficeRepository.findById(CORE_SERVICIO_ID)).thenReturn(Optional.empty());

		serveiService.configuracioScspUpdate(SERVEI_ID, new ServeiConfigScspDto());

		assertTrue(servei.isConfigurat());
		ArgumentCaptor<ScspCoreEmBackofficeEntity> backoffice = ArgumentCaptor.forClass(ScspCoreEmBackofficeEntity.class);
		verify(scspEmisorBackofficeRepository).save(backoffice.capture());
		assertEquals(CORE_SERVICIO_ID, backoffice.getValue().getCertificado());
		assertEquals(ServeiServiceImpl.BACKOFFICE_CLASS_ENRUTADOR, backoffice.getValue().getClassName());
	}

	@Test
	void backofficeClassSegonsTipusDeServei() {
		ServeiEntity serveiBackoffice = ServeiEntity.getBuilder("SVDSCDWS01", "Canvi de domicili", ServeiTipusEnumDto.BACKOFFICE).
				backofficeClass("es.caib.emiserv.backoffice.TestBackoffice").
				build();
		ServeiEntity serveiMultiple = ServeiEntity.getBuilder("SVDSCTFNWS01", "Família nombrosa", ServeiTipusEnumDto.ENRUTADOR_MULTIPLE).
				build();

		assertEquals("es.caib.emiserv.backoffice.TestBackoffice", ServeiServiceImpl.getBackofficeClassScsp(serveiBackoffice));
		assertEquals(ServeiServiceImpl.BACKOFFICE_CLASS_ENRUTADOR, ServeiServiceImpl.getBackofficeClassScsp(servei));
		assertEquals(ServeiServiceImpl.BACKOFFICE_CLASS_ENRUTADOR, ServeiServiceImpl.getBackofficeClassScsp(serveiMultiple));
	}

	@Test
	void rutaBackofficeRequereixServeiConfigurat() {
		// Els enrutadors sempre tenen configurat=true: el que compta és la configuració SCSP
		assertTrue(servei.isConfigurat());
		ServeiRutaDestiDto ruta = ruta(ServeiRutaDestiTipusEnumDto.BACKOFFICE);

		assertThrows(ValidationException.class, () -> serveiService.rutaDestiCreate(SERVEI_ID, ruta));

		verify(serveiRutaDestiRepository, never()).save(any());
	}

	@Test
	void rutaBackofficeAmbServeiConfigurat() {
		configuratScsp();
		when(serveiRutaDestiRepository.getNextOrdre(anyLong())).thenReturn(1L);

		serveiService.rutaDestiCreate(SERVEI_ID, ruta(ServeiRutaDestiTipusEnumDto.BACKOFFICE));

		ArgumentCaptor<ServeiRutaDestiEntity> entity = ArgumentCaptor.forClass(ServeiRutaDestiEntity.class);
		verify(serveiRutaDestiRepository).save(entity.capture());
		assertTrue(entity.getValue().isDestiBackoffice());
	}

	@Test
	void rutaEmiservNoRequereixServeiConfigurat() {
		when(serveiRutaDestiRepository.getNextOrdre(anyLong())).thenReturn(1L);

		serveiService.rutaDestiCreate(SERVEI_ID, ruta(null));

		ArgumentCaptor<ServeiRutaDestiEntity> entity = ArgumentCaptor.forClass(ServeiRutaDestiEntity.class);
		verify(serveiRutaDestiRepository).save(entity.capture());
		assertEquals(ServeiRutaDestiTipusEnumDto.EMISERV, entity.getValue().getDestiTipus());
	}

	@Test
	void modificacioARutaBackofficeRequereixServeiConfigurat() {
		ServeiRutaDestiEntity existent = ServeiRutaDestiEntity.getBuilder(servei, "7040", "https://cedent.es/emisor/services/peticionSincrona", 1L).build();
		ReflectionTestUtils.setField(existent, "id", 30L);
		when(serveiRutaDestiRepository.findById(30L)).thenReturn(Optional.of(existent));
		ServeiRutaDestiDto ruta = ruta(ServeiRutaDestiTipusEnumDto.BACKOFFICE);
		ruta.setId(30L);

		assertThrows(ValidationException.class, () -> serveiService.rutaDestiUpdate(SERVEI_ID, ruta));

		assertEquals(ServeiRutaDestiTipusEnumDto.EMISERV, existent.getDestiTipus());
	}

	@Test
	void esborrarEnrutadorSenseConfiguracioScsp() {
		serveiService.delete(SERVEI_ID);

		verify(serveiRepository).delete(servei);
		verify(scspEmisorBackofficeRepository, never()).deleteById(any());
	}

	@Test
	void esborrarEnrutadorAmbConfiguracioScsp() {
		configuratScsp();
		when(scspEmisorBackofficeRepository.existsById(CORE_SERVICIO_ID)).thenReturn(true);

		serveiService.delete(SERVEI_ID);

		verify(scspEmisorBackofficeRepository).deleteById(CORE_SERVICIO_ID);
	}

	private static ServeiRutaDestiDto ruta(ServeiRutaDestiTipusEnumDto destiTipus) {
		ServeiRutaDestiDto ruta = new ServeiRutaDestiDto();
		ruta.setEntitatCodi("7040");
		ruta.setUrl("https://cedent.es/backoffice/ws/EmiservBackoffice");
		ruta.setDestiTipus(destiTipus);
		return ruta;
	}

}
