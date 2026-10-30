package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.BackofficeDestiHelper;
import es.caib.emiserv.logic.helper.PermisosHelper;
import es.caib.emiserv.logic.helper.SecurityHelper;
import es.caib.emiserv.logic.intf.dto.BackofficeAutenticacioTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiDiagnosticEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmisorCertificadoEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.ServeiRutaDestiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Proves del diagnòstic de configuració dels serveis. S'empren mocks dels repositoris perquè
 * emiserv-service no disposa d'infraestructura de proves amb context Spring i BD.
 */
class ServeiDiagnosticTest {

	private ServeiRepository serveiRepository;
	private ServeiRutaDestiRepository rutaRepository;
	private ScspCoreServicioRepository coreRepository;
	private Environment environment;
	private SecurityHelper securityHelper;
	private PermisosHelper permisosHelper;
	private ServeiServiceImpl service;
	private final List<ServeiEntity> serveis = new ArrayList<>();
	private final List<ScspCoreServicioEntity> cores = new ArrayList<>();
	private final List<ServeiRutaDestiEntity> rutes = new ArrayList<>();

	@BeforeEach
	void setUp() {
		serveiRepository = mock(ServeiRepository.class);
		rutaRepository = mock(ServeiRutaDestiRepository.class);
		coreRepository = mock(ScspCoreServicioRepository.class);
		environment = mock(Environment.class);
		when(serveiRepository.findAllById(anyIterable())).thenReturn(serveis);
		when(coreRepository.findByCodigoCertificadoIn(any())).thenReturn(cores);
		when(rutaRepository.findByServeiIdIn(any())).thenReturn(rutes);
		BackofficeDestiHelper helper = new BackofficeDestiHelper();
		ReflectionTestUtils.setField(helper, "environment", environment);
		service = new ServeiServiceImpl();
		ReflectionTestUtils.setField(service, "serveiRepository", serveiRepository);
		ReflectionTestUtils.setField(service, "serveiRutaDestiRepository", rutaRepository);
		ReflectionTestUtils.setField(service, "scspCoreServicioRepository", coreRepository);
		ReflectionTestUtils.setField(service, "backofficeDestiHelper", helper);
		securityHelper = mock(SecurityHelper.class);
		when(securityHelper.hasRole("EMS_ADMIN")).thenReturn(true);
		permisosHelper = mock(PermisosHelper.class);
		ReflectionTestUtils.setField(service, "securityHelper", securityHelper);
		ReflectionTestUtils.setField(service, "permisosHelper", permisosHelper);
	}

	private ServeiEntity servei(long id, String codi, ServeiTipusEnumDto tipus, boolean configurat) {
		ServeiEntity s = ServeiEntity.getBuilder(codi, codi, tipus).build();
		ReflectionTestUtils.setField(s, "id", id);
		s.updateConfigurat(configurat);
		serveis.add(s);
		return s;
	}

	private void core(String codi, boolean ambEmisor) {
		ScspCoreServicioEntity c = mock(ScspCoreServicioEntity.class);
		when(c.getCodigoCertificado()).thenReturn(codi);
		when(c.getEmisor()).thenReturn(ambEmisor ? mock(ScspCoreEmisorCertificadoEntity.class) : null);
		cores.add(c);
	}

	private void ruta(ServeiEntity servei, ServeiRutaDestiTipusEnumDto tipus, BackofficeAutenticacioTipusEnumDto aut) {
		rutes.add(ServeiRutaDestiEntity.getBuilder(servei, "ENT", "http://x", (long) rutes.size()).desti(tipus, aut).build());
	}

	private List<ServeiDiagnosticEnumDto> diag(long id) {
		return service.diagnosticFindByServeis(Collections.singletonList(id)).get(id);
	}

	@Test
	void backofficeSenseCoreServicio() {
		servei(1, "BO", ServeiTipusEnumDto.BACKOFFICE, true);
		assertEquals(Collections.singletonList(ServeiDiagnosticEnumDto.FALTA_CONFIG_SCSP), diag(1));
	}

	@Test
	void backofficeNoConfiguratAmbCoreServicioNomesFaltaConfig() {
		servei(1, "BO", ServeiTipusEnumDto.BACKOFFICE, false);
		core("BO", false);
		assertEquals(Collections.singletonList(ServeiDiagnosticEnumDto.FALTA_CONFIG_SCSP), diag(1));
	}

	@Test
	void backofficeSenseEmissor() {
		servei(1, "BO", ServeiTipusEnumDto.BACKOFFICE, true);
		core("BO", false);
		assertEquals(Collections.singletonList(ServeiDiagnosticEnumDto.FALTA_EMISOR), diag(1));
	}

	@Test
	void backofficeCorrecte() {
		servei(1, "BO", ServeiTipusEnumDto.BACKOFFICE, true);
		core("BO", true);
		assertTrue(diag(1).isEmpty());
	}

	@Test
	void enrutadorSenseRutes() {
		servei(1, "EN", ServeiTipusEnumDto.ENRUTADOR, true);
		assertEquals(Collections.singletonList(ServeiDiagnosticEnumDto.ENRUTADOR_SENSE_RUTES), diag(1));
	}

	@Test
	void enrutadorAmbRutaBackofficeSenseCredencials() {
		ServeiEntity s = servei(1, "EN", ServeiTipusEnumDto.ENRUTADOR_MULTIPLE, true);
		core("EN", true);
		ruta(s, ServeiRutaDestiTipusEnumDto.BACKOFFICE, BackofficeAutenticacioTipusEnumDto.TEXT_CLAR);
		assertEquals(Collections.singletonList(ServeiDiagnosticEnumDto.RUTA_BACKOFFICE_SENSE_CREDENCIALS), diag(1));
	}

	@Test
	void enrutadorAmbCredencialsConfiguradesNoAvisa() {
		ServeiEntity s = servei(1, "EN", ServeiTipusEnumDto.ENRUTADOR, true);
		core("EN", true);
		ruta(s, ServeiRutaDestiTipusEnumDto.BACKOFFICE, BackofficeAutenticacioTipusEnumDto.TEXT_CLAR);
		when(environment.getProperty("es.caib.emiserv.backoffice.caib.auth.username")).thenReturn("u");
		when(environment.getProperty("es.caib.emiserv.backoffice.caib.auth.password")).thenReturn("p");
		assertTrue(diag(1).isEmpty());
	}

	@Test
	void enrutadorAmbRutaBackofficeSenseCoreServicio() {
		ServeiEntity s = servei(1, "EN", ServeiTipusEnumDto.ENRUTADOR, true);
		ruta(s, ServeiRutaDestiTipusEnumDto.BACKOFFICE, BackofficeAutenticacioTipusEnumDto.NINGUNA);
		assertEquals(Collections.singletonList(ServeiDiagnosticEnumDto.FALTA_CONFIG_SCSP), diag(1));
	}

	@Test
	void enrutadorNomesRutesEmiservNoRequereixScsp() {
		ServeiEntity s = servei(1, "EN", ServeiTipusEnumDto.ENRUTADOR, true);
		ruta(s, ServeiRutaDestiTipusEnumDto.EMISERV, null);
		assertTrue(diag(1).isEmpty());
	}

	@Test
	void motiusEnOrdreDeLEnum() {
		ServeiEntity s = servei(1, "EN", ServeiTipusEnumDto.ENRUTADOR, true);
		core("EN", false);
		ruta(s, ServeiRutaDestiTipusEnumDto.BACKOFFICE, BackofficeAutenticacioTipusEnumDto.TEXT_CLAR);
		assertEquals(Arrays.asList(
				ServeiDiagnosticEnumDto.FALTA_EMISOR,
				ServeiDiagnosticEnumDto.RUTA_BACKOFFICE_SENSE_CREDENCIALS), diag(1));
	}

	@Test
	void llistaBuidaNoConsulta() {
		assertTrue(service.diagnosticFindByServeis(Collections.emptyList()).isEmpty());
		verifyNoInteractions(serveiRepository, rutaRepository, coreRepository);
	}

	@Test
	void consultesEnBlocSenseNPlusUn() {
		for (long i = 1; i <= 50; i++) {
			ServeiEntity s = servei(i, "S" + i, i % 2 == 0 ? ServeiTipusEnumDto.ENRUTADOR : ServeiTipusEnumDto.BACKOFFICE, true);
			core("S" + i, true);
			if (i % 2 == 0) {
				ruta(s, ServeiRutaDestiTipusEnumDto.EMISERV, null);
			}
		}
		List<Long> ids = new ArrayList<>();
		for (long i = 1; i <= 50; i++) {
			ids.add(i);
		}
		Map<Long, List<ServeiDiagnosticEnumDto>> r = service.diagnosticFindByServeis(ids);
		assertEquals(50, r.size());
		verify(serveiRepository, times(1)).findAllById(anyIterable());
		verify(coreRepository, times(1)).findByCodigoCertificadoIn(any());
		verify(rutaRepository, times(1)).findByServeiIdIn(any());
		verify(coreRepository, never()).findByCodigoCertificado(any());
	}
	@Test
	@SuppressWarnings("unchecked")
	void responsableNomesRepElDiagnosticDelsServeisPermesos() {
		servei(1, "BO1", ServeiTipusEnumDto.BACKOFFICE, true);
		servei(2, "BO2", ServeiTipusEnumDto.BACKOFFICE, true);
		when(securityHelper.hasRole("EMS_ADMIN")).thenReturn(false);
		// Mateixa regla que findAllPaginat: permís ADMINISTRATION sobre ServeiEntity
		doAnswer(inv -> {
			((Collection<ServeiEntity>)inv.getArgument(0)).removeIf(s -> s.getId() != 1L);
			return null;
		}).when(permisosHelper).filterGrantedAll(
				anyCollection(),
				any(),
				eq(ServeiEntity.class),
				argThat(p -> p.length == 1 && p[0] == BasePermission.ADMINISTRATION),
				any());

		Map<Long, List<ServeiDiagnosticEnumDto>> resultat = service.diagnosticFindByServeis(Arrays.asList(1L, 2L));

		assertEquals(Collections.singleton(1L), resultat.keySet());
	}

	@Test
	void administradorNoFiltraPerPermisos() {
		servei(1, "BO1", ServeiTipusEnumDto.BACKOFFICE, true);
		servei(2, "BO2", ServeiTipusEnumDto.BACKOFFICE, true);

		assertEquals(2, service.diagnosticFindByServeis(Arrays.asList(1L, 2L)).size());
		verifyNoInteractions(permisosHelper);
	}

}
