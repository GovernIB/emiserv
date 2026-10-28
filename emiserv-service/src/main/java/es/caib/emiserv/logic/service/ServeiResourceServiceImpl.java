package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.base.service.BaseMutableResourceService;
import es.caib.emiserv.logic.helper.ExportacioHelper;
import es.caib.emiserv.logic.helper.PermisosHelper;
import es.caib.emiserv.logic.helper.SecurityHelper;
import es.caib.emiserv.logic.intf.base.exception.ActionExecutionException;
import es.caib.emiserv.logic.intf.base.exception.AnswerRequiredException;
import es.caib.emiserv.logic.intf.base.exception.ReportGenerationException;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.emiserv.logic.intf.base.model.DownloadableFile;
import es.caib.emiserv.logic.intf.base.model.ExportField;
import es.caib.emiserv.logic.intf.base.model.ReportFileType;
import es.caib.emiserv.logic.intf.dto.ServeiDiagnosticEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.exception.ExportacioMassaGranException;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.PermissionDeniedException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.model.ServeiResource;
import es.caib.emiserv.logic.intf.service.ServeiResourceService;
import es.caib.emiserv.logic.intf.service.ServeiService;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import es.caib.emiserv.persist.resourceentity.ServeiResourceEntity;
import es.caib.emiserv.persist.resourcerepository.ServeiResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servei del recurs {@link ServeiResource}.
 * <p/>
 * Permisos (els mateixos que la JSP, vegeu ServeiServiceImpl.findAllPaginat):
 * <ul>
 *   <li>EMS_ADMIN veu tots els serveis; la resta d'usuaris només els serveis amb permís ACL
 *   ADMINISTRATION. Si no en tenen cap, la consulta torna una pàgina buida (sense IN buit).</li>
 *   <li>Les accions, l'esborrat i l'exportació només es permeten a EMS_ADMIN, i deleguen a
 *   ServeiService perquè les regles de negoci i l'auditoria siguin les de la JSP.</li>
 * </ul>
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Service
public class ServeiResourceServiceImpl extends BaseMutableResourceService<ServeiResource, Long, ServeiResourceEntity> implements ServeiResourceService {

	/** Màxim d'ids per IN (límit d'Oracle). */
	private static final int MAX_IN = 1000;
	private static final String ROL_ADMIN = ServeiResource.ROL_ADMIN;

	/** Columnes de l'exportació per defecte (en aquest ordre). */
	public static final List<String> COLUMNES_EXPORTACIO = Collections.unmodifiableList(Arrays.asList(
			ServeiResource.Fields.codi,
			ServeiResource.Fields.nom,
			ServeiResource.Fields.tipus,
			ServeiResource.Fields.actiu,
			ServeiResource.Fields.configuratScsp,
			ServeiResource.Fields.permisosCount,
			ServeiResource.Fields.rutesCount,
			ServeiResource.Fields.diagnostic));
	private static final String NOM_FITXER_EXPORTACIO = "serveis";
	private static final ResourceBundleMessageSource MISSATGES_EXPORTACIO = new ResourceBundleMessageSource();
	static {
		MISSATGES_EXPORTACIO.setBasename("es/caib/emiserv/logic/i18n/exportacio");
		MISSATGES_EXPORTACIO.setDefaultEncoding("UTF-8");
		MISSATGES_EXPORTACIO.setFallbackToSystemLocale(false);
	}

	@Autowired
	private ServeiService serveiService;
	@Autowired
	private ServeiResourceRepository serveiResourceRepository;
	@Autowired
	private ScspCoreServicioRepository scspCoreServicioRepository;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private SecurityHelper securityHelper;
	@Autowired
	private ExportacioHelper exportacioHelper;

	@PostConstruct
	public void init() {
		register(ServeiResource.ACTION_ACTIVAR_CODE, new ActiuActionExecutor(true));
		register(ServeiResource.ACTION_DESACTIVAR_CODE, new ActiuActionExecutor(false));
		register(ServeiResource.ACTION_ESBORRAR_CODE, new EsborrarActionExecutor());
		register(ServeiResource.REPORT_EXPORTAR_CODE, new ExportarReportGenerator());
	}

	@Override
	@Transactional
	public <P extends Serializable> Serializable artifactActionExec(
			Long id,
			String code,
			P params) {
		comprovarAdministrador();
		try {
			return super.artifactActionExec(id, code, params);
		} catch (ActionExecutionException ex) {
			// Els errors de negoci de ServeiService arriben al client amb el seu missatge
			throw excepcioNegociOMateixa(ex);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public <P extends Serializable> List<?> artifactReportGenerateData(
			Long id,
			String code,
			P params) {
		comprovarAdministrador();
		try {
			return super.artifactReportGenerateData(id, code, params);
		} catch (ReportGenerationException ex) {
			throw excepcioNegociOMateixa(ex);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public DownloadableFile artifactReportGenerateFile(
			String code,
			List<?> data,
			ReportFileType fileType,
			OutputStream out) {
		comprovarAdministrador();
		try {
			return super.artifactReportGenerateFile(code, data, fileType, out);
		} catch (ReportGenerationException ex) {
			throw excepcioNegociOMateixa(ex);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public DownloadableFile export(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			org.springframework.data.domain.Pageable pageable,
			ExportField[] fields,
			ReportFileType fileType,
			OutputStream out) {
		comprovarAdministrador();
		List<String> columnes;
		List<String> capcaleres;
		if (fields != null && fields.length > 0) {
			columnes = Arrays.stream(fields).map(ExportField::getName).collect(Collectors.toList());
			capcaleres = Arrays.stream(fields).
					map(f -> f.getLabel() != null ? f.getLabel() : capcalera(f.getName())).
					collect(Collectors.toList());
		} else {
			columnes = COLUMNES_EXPORTACIO;
			capcaleres = capcaleresPerDefecte();
		}
		List<ServeiResource> files = consultarPerExportar(
				quickFilter,
				filter,
				pageable != null ? pageable.getSort() : Sort.unsorted());
		return generarFitxer(files, columnes, capcaleres, fileType);
	}

	@Override
	@Transactional
	public void delete(Long id, Map<String, AnswerRequiredException.AnswerValue> answers) {
		comprovarAdministrador();
		// Mateixa lògica de negoci que la JSP
		serveiService.delete(id);
	}

	@Override
	public ServeiResource create(ServeiResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new ResourceNotCreatedException(ServeiResource.class, "Operació no disponible");
	}

	@Override
	public ServeiResource update(Long id, ServeiResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new ResourceNotUpdatedException(ServeiResource.class, String.valueOf(id), "Operació no disponible");
	}

	@Override
	protected Specification<ServeiResourceEntity> additionalSpecification(String[] namedQueries, boolean isSingleResult) {
		if (securityHelper.hasRole(ROL_ADMIN)) {
			return null;
		}
		List<Long> idsPermesos = findIdsPermesos();
		return (root, query, cb) -> {
			if (idsPermesos.isEmpty()) {
				// Cap servei permès: pàgina buida sense IN buit
				return cb.disjunction();
			}
			List<Predicate> predicats = new ArrayList<>();
			for (int i = 0; i < idsPermesos.size(); i += MAX_IN) {
				predicats.add(root.get("id").in(idsPermesos.subList(i, Math.min(i + MAX_IN, idsPermesos.size()))));
			}
			return cb.or(predicats.toArray(new Predicate[0]));
		};
	}

	@Override
	protected void afterConversion(ServeiResourceEntity entity, ServeiResource resource) {
		afterConversion(Collections.singletonList(entity), Collections.singletonList(resource));
	}

	@Override
	protected void afterConversion(List<ServeiResourceEntity> entities, List<ServeiResource> resources) {
		if (resources == null || resources.isEmpty()) {
			return;
		}
		// Diagnòstic: una sola crida per a tota la pàgina (ids ja filtrats per permisos)
		Map<Long, List<ServeiDiagnosticEnumDto>> diagnostics = serveiService.diagnosticFindByServeis(
				resources.stream().map(ServeiResource::getId).collect(Collectors.toList()));
		// Configuració SCSP: només cal consultar CORE_SERVICIO pels enrutadors
		List<String> codisEnrutadors = resources.stream().
				filter(r -> !ServeiTipusEnumDto.BACKOFFICE.equals(r.getTipus())).
				map(ServeiResource::getCodi).
				collect(Collectors.toList());
		Set<String> codisAmbCore = new HashSet<>();
		for (int i = 0; i < codisEnrutadors.size(); i += MAX_IN) {
			for (ScspCoreServicioEntity core: scspCoreServicioRepository.findByCodigoCertificadoIn(
					codisEnrutadors.subList(i, Math.min(i + MAX_IN, codisEnrutadors.size())))) {
				codisAmbCore.add(core.getCodigoCertificado());
			}
		}
		for (ServeiResource resource: resources) {
			List<ServeiDiagnosticEnumDto> diagnostic = diagnostics != null ? diagnostics.get(resource.getId()) : null;
			resource.setDiagnostic(diagnostic != null ? diagnostic : new ArrayList<>());
			resource.setConfiguratScsp(ServeiServiceImpl.isConfiguratScsp(
					resource.getTipus(),
					resource.isConfigurat(),
					() -> codisAmbCore.contains(resource.getCodi())));
		}
	}

	/**
	 * Consulta per a l'exportació: compta primer (per no carregar res si se supera el màxim) i
	 * després consulta amb el mateix filtre (inclosos els permisos) i ordre que la graella.
	 */
	private List<ServeiResource> consultarPerExportar(String quickFilter, String filter, Sort sort) {
		Specification<ServeiResourceEntity> specification = toFindProcessedSpecification(
				quickFilter,
				filter,
				null);
		long total = entityRepository.count(specification);
		exportacioHelper.comprovarMaxim(total);
		List<ServeiResourceEntity> entities = entityRepository.findAll(
				specification,
				toProcessedSort(sort != null ? sort : Sort.unsorted()));
		List<ServeiResource> resources = entitiesToResources(entities);
		afterConversion(entities, resources);
		return resources;
	}

	private DownloadableFile generarFitxer(
			List<?> files,
			List<String> columnes,
			List<String> capcaleres,
			ReportFileType fileType) {
		if (fileType != ReportFileType.CSV && fileType != ReportFileType.XLSX) {
			throw new ValidationException("Format d'exportació no suportat (CSV o XLSX): " + fileType);
		}
		String format = fileType.name();
		return new DownloadableFile(
				exportacioHelper.getNomFitxer(NOM_FITXER_EXPORTACIO, format),
				exportacioHelper.getContentType(format),
				exportacioHelper.generar(files, columnes, capcaleres, format));
	}

	private List<String> capcaleresPerDefecte() {
		return COLUMNES_EXPORTACIO.stream().map(this::capcalera).collect(Collectors.toList());
	}

	/**
	 * Capçalera traduïda d'una columna. Es llegeix d'un fitxer de missatges propi del servei perquè
	 * el context Spring dels EJB no té els missatges de la interfície (messages_emiserv).
	 */
	private String capcalera(String camp) {
		return MISSATGES_EXPORTACIO.getMessage(
				"servei.exportacio.columna." + camp,
				null,
				camp,
				LocaleContextHolder.getLocale());
	}

	/** Ids dels serveis amb permís ACL ADMINISTRATION per a l'usuari actual (com findAllPaginat). */
	private List<Long> findIdsPermesos() {
		List<Long> ids = new ArrayList<>(serveiResourceRepository.findAllIds());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		permisosHelper.filterGrantedAll(
				ids,
				id -> id,
				ServeiEntity.class,
				new Permission[] {BasePermission.ADMINISTRATION},
				auth);
		return ids;
	}

	private void comprovarAdministrador() {
		if (!securityHelper.hasRole(ROL_ADMIN)) {
			throw new AccessDeniedException("Operació reservada al rol " + ROL_ADMIN);
		}
	}

	private RuntimeException excepcioNegociOMateixa(RuntimeException ex) {
		Throwable causa = ex.getCause();
		return isExcepcioNegoci(causa) ? (RuntimeException)causa : ex;
	}

	private static boolean isExcepcioNegoci(Throwable ex) {
		return ex instanceof ValidationException ||
				ex instanceof PermissionDeniedException ||
				ex instanceof NotFoundException ||
				ex instanceof AccessDeniedException ||
				ex instanceof ExportacioMassaGranException;
	}

	/**
	 * Executa la lògica d'una acció embolcallant els errors de negoci en una ActionExecutionException
	 * (la classe base les propaga sense registrar-les com a error) que artifactActionExec desembolica.
	 */
	private Serializable executarAccio(String code, ServeiResourceEntity entity, Runnable logica) {
		try {
			logica.run();
			return null;
		} catch (RuntimeException ex) {
			if (isExcepcioNegoci(ex)) {
				throw new ActionExecutionException(ServeiResource.class, entity.getId(), code, ex.getMessage(), ex);
			}
			throw ex;
		}
	}

	private class ActiuActionExecutor implements ActionExecutor<ServeiResourceEntity, Serializable, Serializable> {
		private final boolean actiu;
		ActiuActionExecutor(boolean actiu) {
			this.actiu = actiu;
		}
		@Override
		public Serializable exec(String code, ServeiResourceEntity entity, Serializable params) {
			return executarAccio(code, entity, () -> serveiService.updateActiu(entity.getId(), actiu));
		}
		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
		}
	}

	private class EsborrarActionExecutor implements ActionExecutor<ServeiResourceEntity, Serializable, Serializable> {
		@Override
		public Serializable exec(String code, ServeiResourceEntity entity, Serializable params) {
			return executarAccio(code, entity, () -> serveiService.delete(entity.getId()));
		}
		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
		}
	}

	private class ExportarReportGenerator implements ReportGenerator<ServeiResourceEntity, ServeiResource.ExportacioForm, ServeiResource> {
		@Override
		public List<ServeiResource> generateData(String code, ServeiResourceEntity entity, ServeiResource.ExportacioForm params) {
			try {
				return consultarPerExportar(
						params != null ? params.getQuickFilter() : null,
						params != null ? params.getFilter() : null,
						params != null ? toSort(params.getSort()) : Sort.unsorted());
			} catch (ExportacioMassaGranException ex) {
				// Embolcallada perquè la classe base no la registri com a error (es desembolica a artifactReportGenerateData)
				throw new ReportGenerationException(ServeiResource.class, null, code, ex.getMessage(), ex);
			}
		}
		@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
			return generarFitxer(data, COLUMNES_EXPORTACIO, capcaleresPerDefecte(), fileType);
		}
		@Override
		public void onChange(Serializable id, ServeiResource.ExportacioForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ServeiResource.ExportacioForm target) {
		}
	}

	/** Converteix "camp" o "camp,asc|desc" a Sort (com el paràmetre sort de Spring Data). */
	static Sort toSort(String[] sort) {
		if (sort == null || sort.length == 0) {
			return Sort.unsorted();
		}
		List<Sort.Order> orders = new ArrayList<>();
		for (String s: sort) {
			if (s == null || s.trim().isEmpty()) {
				continue;
			}
			String[] parts = s.split(",");
			String camp = parts[0].trim();
			boolean desc = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim());
			orders.add(desc ? Sort.Order.desc(camp) : Sort.Order.asc(camp));
		}
		return Sort.by(orders);
	}

}
