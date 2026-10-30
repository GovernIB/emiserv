package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.base.helper.BasePermissionHelper;
import es.caib.emiserv.logic.base.helper.JasperReportsHelper;
import es.caib.emiserv.logic.base.helper.ObjectMappingHelper;
import es.caib.emiserv.logic.base.helper.ResourceEntityMappingHelper;
import es.caib.emiserv.logic.base.helper.ResourceReferenceToEntityHelper;
import es.caib.emiserv.logic.helper.ExportacioHelper;
import es.caib.emiserv.logic.helper.PermisosHelper;
import es.caib.emiserv.logic.helper.SecurityHelper;
import es.caib.emiserv.logic.intf.base.model.DownloadableFile;
import es.caib.emiserv.logic.intf.base.model.ReportFileType;
import es.caib.emiserv.logic.intf.dto.ServeiDiagnosticEnumDto;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.emiserv.logic.intf.exception.ExportacioMassaGranException;
import es.caib.emiserv.logic.intf.exception.OrdenacioNoValidaException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.model.ServeiResource;
import es.caib.emiserv.logic.intf.service.ServeiService;
import es.caib.emiserv.persist.base.repository.BaseRepositoryImpl;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import es.caib.emiserv.persist.resourceentity.ServeiResourceEntity;
import es.caib.emiserv.persist.resourcerepository.ServeiResourceRepository;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Proves del recurs de serveis amb una BD HSQLDB en memòria (consultes, filtres, permisos i
 * comptadors reals) i ServeiService/ACL simulats.
 */
@DataJpaTest(properties = {
		// sql.enforce_types: comparacions entre tipus incompatibles (p. ex. varchar = bigint) donen
		// error, com a PostgreSQL (on ems_acl_object_identity.object_id_identity és VARCHAR(36))
		"spring.datasource.url=jdbc:hsqldb:mem:serveiresource;sql.enforce_types=true",
		"spring.datasource.username=sa",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.HSQLDialect",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.liquibase.enabled=false",
		"spring.sql.init.schema-locations=classpath:servei-resource-test-schema.sql",
		"es.caib.emiserv.exportacio.max=3" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = ServeiResourceServiceTest.Config.class)
class ServeiResourceServiceTest {

	@Configuration
	@EntityScan(basePackageClasses = ServeiResourceEntity.class)
	@EnableJpaRepositories(
			basePackageClasses = ServeiResourceRepository.class,
			repositoryBaseClass = BaseRepositoryImpl.class)
	@Import({
			ServeiResourceServiceImpl.class,
			ObjectMappingHelper.class,
			ResourceEntityMappingHelper.class,
			ExportacioHelper.class,
			SecurityHelper.class })
	static class Config {
	}

	@Autowired
	private ServeiResourceServiceImpl service;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@MockBean
	private ServeiService serveiService;
	@MockBean
	private ScspCoreServicioRepository scspCoreServicioRepository;
	@MockBean
	private PermisosHelper permisosHelper;
	@MockBean
	private JasperReportsHelper jasperReportsHelper;
	@MockBean
	private BasePermissionHelper basePermissionHelper;
	@MockBean
	private ResourceReferenceToEntityHelper resourceReferenceToEntityHelper;

	@BeforeEach
	void setUp() {
		when(serveiService.diagnosticFindByServeis(anyList())).thenReturn(new HashMap<>());
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
		LocaleContextHolder.resetLocaleContext();
	}

	@Test
	void adminVeuTots() {
		crearServeisBasics();
		autenticar("EMS_ADMIN");
		Page<ServeiResource> page = service.findPage(null, null, null, null, PageRequest.of(0, 10));
		assertEquals(3, page.getTotalElements());
		// Ordenació per defecte: codi ASC
		assertEquals(Arrays.asList("S1", "S2", "S3"), codis(page));
		verifyNoInteractions(permisosHelper);
	}

	@Test
	void respVeuNomesPermesos() {
		crearServeisBasics();
		autenticar("EMS_RESP");
		permetreNomes(2L);
		Page<ServeiResource> page = service.findPage(null, null, null, null, PageRequest.of(0, 10));
		assertEquals(1, page.getTotalElements());
		assertEquals(Collections.singletonList("S2"), codis(page));
		// Mateixa comprovació ACL que findAllPaginat: ADMINISTRATION sobre ServeiEntity
		verify(permisosHelper).filterGrantedAll(
				anyCollection(),
				any(),
				eq(ServeiEntity.class),
				argThat(p -> p.length == 1 && p[0] == org.springframework.security.acls.domain.BasePermission.ADMINISTRATION),
				any());
	}

	@Test
	void respSensePermisosPaginaBuida() {
		crearServeisBasics();
		autenticar("EMS_RESP");
		permetreNomes();
		Page<ServeiResource> page = service.findPage(null, null, null, null, PageRequest.of(0, 10));
		assertEquals(0, page.getTotalElements());
		assertTrue(page.getContent().isEmpty());
		verify(serveiService, never()).diagnosticFindByServeis(anyList());
	}

	@Test
	void filtreRapidPerCodiINom() {
		inserirServei(1L, "ABC1", "Primer", 0, true, true);
		inserirServei(2L, "ZZ", "Servei aBc", 1, true, true);
		inserirServei(3L, "XX", "Altre", 0, true, true);
		autenticar("EMS_ADMIN");
		Page<ServeiResource> page = service.findPage("abc", null, null, null, PageRequest.of(0, 10));
		assertEquals(Arrays.asList("ABC1", "ZZ"), codis(page));
	}

	@Test
	void filtrePerTipusIActiu() {
		inserirServei(1L, "S1", "Primer", 0, true, true);
		inserirServei(2L, "S2", "Segon", 1, true, true);
		inserirServei(3L, "S3", "Tercer", 1, false, true);
		autenticar("EMS_ADMIN");
		assertEquals(
				Arrays.asList("S2", "S3"),
				codis(service.findPage(null, "tipus:'ENRUTADOR'", null, null, PageRequest.of(0, 10))));
		assertEquals(
				Collections.singletonList("S2"),
				codis(service.findPage(null, "tipus:'ENRUTADOR' and actiu:true", null, null, PageRequest.of(0, 10))));
	}

	@Test
	void comptadorsPermisosIRutes() {
		crearServeisBasics();
		// Servei 1: dues rutes i entrades ACL de dos principals (un amb dos permisos)
		jdbcTemplate.update("insert into ems_servei_ruta_desti (id, servei_id) values (1, 1), (2, 1), (3, 2)");
		jdbcTemplate.update("insert into ems_acl_class (id, class) values (1, ?), (2, 'altra.Classe')", ServeiResourceEntity.ACL_CLASS);
		jdbcTemplate.update("insert into ems_acl_object_identity (id, object_id_class, object_id_identity) values (1, 1, 1), (2, 2, 2)");
		jdbcTemplate.update("insert into ems_acl_entry (id, acl_object_identity, sid) values (1, 1, 10), (2, 1, 10), (3, 1, 11), (4, 2, 12)");
		autenticar("EMS_ADMIN");
		Map<String, ServeiResource> perCodi = service.findPage(null, null, null, null, PageRequest.of(0, 10)).
				getContent().stream().collect(Collectors.toMap(ServeiResource::getCodi, r -> r));
		assertEquals(2, perCodi.get("S1").getPermisosCount());
		assertEquals(2, perCodi.get("S1").getRutesCount());
		// Les ACL d'una altra classe amb el mateix id no compten
		assertEquals(0, perCodi.get("S2").getPermisosCount());
		assertEquals(1, perCodi.get("S2").getRutesCount());
		assertEquals(0, perCodi.get("S3").getRutesCount());
	}

	@Test
	void classeAclCoincideixAmbServeiEntity() {
		assertEquals(ServeiEntity.class.getName(), ServeiResourceEntity.ACL_CLASS);
	}

	@Test
	void diagnosticOmplert() {
		crearServeisBasics();
		autenticar("EMS_ADMIN");
		Map<Long, List<ServeiDiagnosticEnumDto>> diagnostics = new HashMap<>();
		diagnostics.put(1L, Collections.singletonList(ServeiDiagnosticEnumDto.FALTA_CONFIG_SCSP));
		when(serveiService.diagnosticFindByServeis(anyList())).thenReturn(diagnostics);
		List<ServeiResource> resources = service.findPage(null, null, null, null, PageRequest.of(0, 10)).getContent();
		assertEquals(Collections.singletonList(ServeiDiagnosticEnumDto.FALTA_CONFIG_SCSP), resources.get(0).getDiagnostic());
		assertTrue(resources.get(1).getDiagnostic().isEmpty());
		// Una sola crida per a tota la pàgina
		verify(serveiService, times(1)).diagnosticFindByServeis(argThat(ids -> ids.containsAll(Arrays.asList(1L, 2L, 3L))));
	}

	@Test
	void configuratScspSegonsTipus() {
		inserirServei(1L, "BO", "Backoffice configurat", 0, true, true);
		inserirServei(2L, "BN", "Backoffice no configurat", 0, true, false);
		inserirServei(3L, "EC", "Enrutador amb core", 1, true, true);
		inserirServei(4L, "EN", "Enrutador sense core", 1, true, true);
		es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity core = mock(es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity.class);
		when(core.getCodigoCertificado()).thenReturn("EC");
		when(scspCoreServicioRepository.findByCodigoCertificadoIn(anyList())).thenReturn(Collections.singletonList(core));
		autenticar("EMS_ADMIN");
		Map<String, ServeiResource> perCodi = service.findPage(null, null, null, null, PageRequest.of(0, 10)).
				getContent().stream().collect(Collectors.toMap(ServeiResource::getCodi, r -> r));
		assertTrue(perCodi.get("BO").isConfiguratScsp());
		assertFalse(perCodi.get("BN").isConfiguratScsp());
		assertTrue(perCodi.get("EC").isConfiguratScsp());
		assertFalse(perCodi.get("EN").isConfiguratScsp());
		// Només es consulta CORE_SERVICIO pels enrutadors
		verify(scspCoreServicioRepository).findByCodigoCertificadoIn(argThat(c -> c.size() == 2 && c.containsAll(Arrays.asList("EC", "EN"))));
	}

	@Test
	void activarDesactivarNomesAdmin() {
		crearServeisBasics();
		autenticar("EMS_RESP");
		permetreNomes(1L, 2L, 3L);
		assertThrows(AccessDeniedException.class, () -> service.artifactActionExec(1L, ServeiResource.ACTION_ACTIVAR_CODE, null));
		assertThrows(AccessDeniedException.class, () -> service.artifactActionExec(1L, ServeiResource.ACTION_DESACTIVAR_CODE, null));
		assertThrows(AccessDeniedException.class, () -> service.artifactActionExec(1L, ServeiResource.ACTION_ESBORRAR_CODE, null));
		assertThrows(AccessDeniedException.class, () -> service.delete(1L, null));
		verifyNoInteractions(serveiService);
		autenticar("EMS_ADMIN");
		service.artifactActionExec(1L, ServeiResource.ACTION_DESACTIVAR_CODE, null);
		verify(serveiService).updateActiu(1L, false);
		service.artifactActionExec(1L, ServeiResource.ACTION_ACTIVAR_CODE, null);
		verify(serveiService).updateActiu(1L, true);
	}

	@Test
	void esborrarPropagaErrorNegoci() {
		crearServeisBasics();
		autenticar("EMS_ADMIN");
		String missatge = "Aquest servei te peticions de backoffice associades i no es pot esborrar";
		when(serveiService.delete(2L)).thenThrow(new ValidationException(missatge));
		ValidationException ex = assertThrows(
				ValidationException.class,
				() -> service.artifactActionExec(2L, ServeiResource.ACTION_ESBORRAR_CODE, null));
		assertEquals(missatge, ex.getMessage());
	}

	@Test
	void esborrarDelegaAServeiService() {
		crearServeisBasics();
		autenticar("EMS_ADMIN");
		service.artifactActionExec(3L, ServeiResource.ACTION_ESBORRAR_CODE, null);
		verify(serveiService).delete(3L);
	}

	@Test
	void exportarAmbFiltreIOrdre() throws Exception {
		inserirServei(1L, "S1", "Primer", 0, true, true);
		inserirServei(2L, "S2", "Segon", 1, true, true);
		inserirServei(3L, "S3", "Tercer", 1, false, true);
		inserirServei(4L, "S4", "Quart", 1, true, true);
		autenticar("EMS_ADMIN");
		ServeiResource.ExportacioForm form = new ServeiResource.ExportacioForm();
		form.setFilter("tipus:'ENRUTADOR'");
		form.setSort(new String[] {"codi,desc"});
		List<?> dades = service.artifactReportGenerateData(null, ServeiResource.REPORT_EXPORTAR_CODE, form);
		assertEquals(
				Arrays.asList("S4", "S3", "S2"),
				dades.stream().map(d -> ((ServeiResource)d).getCodi()).collect(Collectors.toList()));
		LocaleContextHolder.setLocale(new Locale("es"));
		DownloadableFile csv = service.artifactReportGenerateFile(ServeiResource.REPORT_EXPORTAR_CODE, dades, ReportFileType.CSV, null);
		String text = new String(csv.getContent(), StandardCharsets.UTF_8);
		String[] linies = text.substring(1).split("\r\n");
		assertEquals(4, linies.length);
		// Capçaleres traduïdes des dels missatges propis del servei
		assertEquals("Código;Nombre;Tipo;Activo;Configurado SCSP;Permisos;Rutas;Diagnóstico", linies[0]);
		assertTrue(linies[1].startsWith("S4;Quart;ENRUTADOR;true;"), linies[1]);
		assertEquals("serveis.csv", csv.getName());
		DownloadableFile xlsx = service.artifactReportGenerateFile(ServeiResource.REPORT_EXPORTAR_CODE, dades, ReportFileType.XLSX, null);
		try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(xlsx.getContent()))) {
			assertEquals(3, wb.getSheetAt(0).getLastRowNum());
		}
	}

	@Test
	void exportarSuperaMaxim() {
		inserirServei(1L, "S1", "Primer", 0, true, true);
		inserirServei(2L, "S2", "Segon", 1, true, true);
		inserirServei(3L, "S3", "Tercer", 1, false, true);
		inserirServei(4L, "S4", "Quart", 1, true, true);
		autenticar("EMS_ADMIN");
		ExportacioMassaGranException ex = assertThrows(
				ExportacioMassaGranException.class,
				() -> service.artifactReportGenerateData(null, ServeiResource.REPORT_EXPORTAR_CODE, new ServeiResource.ExportacioForm()));
		assertEquals(4, ex.getTotal());
		assertEquals(3, ex.getMaxim());
		// També per l'exportació genèrica de la graella
		assertThrows(
				ExportacioMassaGranException.class,
				() -> service.export(null, null, null, null, PageRequest.of(0, 10), null, ReportFileType.CSV, null));
	}

	@Test
	void respNoPotObtenirUnServeiAlie() {
		crearServeisBasics();
		autenticar("EMS_RESP");
		permetreNomes(1L);
		assertEquals("S1", service.getOne(1L, null).getCodi());
		assertThrows(ResourceNotFoundException.class, () -> service.getOne(2L, null));
	}

	@Test
	void exportarOrdenantPerUnCampNoOrdenableEsRebutja() {
		crearServeisBasics();
		autenticar("EMS_ADMIN");
		for (String sort: new String[] {"diagnostic", "configuratScsp,desc", "inexistent", ",desc"}) {
			ServeiResource.ExportacioForm form = new ServeiResource.ExportacioForm();
			form.setSort(new String[] {sort});
			assertThrows(
					OrdenacioNoValidaException.class,
					() -> service.artifactReportGenerateData(null, ServeiResource.REPORT_EXPORTAR_CODE, form),
					sort);
		}
		assertThrows(
				OrdenacioNoValidaException.class,
				() -> service.export(null, null, null, null, PageRequest.of(0, 10, Sort.by("diagnostic")), null, ReportFileType.CSV, null));
		// Els camps persistits, inclosos els comptadors, sí que es poden ordenar
		ServeiResource.ExportacioForm form = new ServeiResource.ExportacioForm();
		form.setSort(new String[] {"rutesCount,desc", "nom"});
		assertEquals(3, service.artifactReportGenerateData(null, ServeiResource.REPORT_EXPORTAR_CODE, form).size());
	}

	@Test
	void exportarNomesAdmin() {
		crearServeisBasics();
		autenticar("EMS_RESP");
		permetreNomes(1L);
		assertThrows(
				AccessDeniedException.class,
				() -> service.artifactReportGenerateData(null, ServeiResource.REPORT_EXPORTAR_CODE, new ServeiResource.ExportacioForm()));
		assertThrows(
				AccessDeniedException.class,
				() -> service.export(null, null, null, null, PageRequest.of(0, 10), null, ReportFileType.CSV, null));
	}

	private void crearServeisBasics() {
		inserirServei(1L, "S1", "Primer", 0, true, true);
		inserirServei(2L, "S2", "Segon", 1, true, true);
		inserirServei(3L, "S3", "Tercer", 2, false, true);
	}

	private void inserirServei(Long id, String codi, String nom, int tipus, boolean actiu, boolean configurat) {
		jdbcTemplate.update(
				"insert into ems_servei (id, codi, nom, tipus, actiu, configurat, xsd_activa) values (?, ?, ?, ?, ?, ?, false)",
				id, codi, nom, tipus, actiu, configurat);
	}

	@SuppressWarnings("unchecked")
	private void permetreNomes(Long... ids) {
		List<Long> permesos = Arrays.asList(ids);
		doAnswer(inv -> {
			((Collection<Long>)inv.getArgument(0)).removeIf(id -> !permesos.contains(id));
			return null;
		}).when(permisosHelper).filterGrantedAll(anyCollection(), any(), any(), any(), any());
	}

	private void autenticar(String... rols) {
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
				"usuari",
				"N/A",
				Arrays.stream(rols).map(SimpleGrantedAuthority::new).collect(Collectors.toList())));
	}

	private List<String> codis(Page<ServeiResource> page) {
		return page.getContent().stream().map(ServeiResource::getCodi).collect(Collectors.toList());
	}

}
