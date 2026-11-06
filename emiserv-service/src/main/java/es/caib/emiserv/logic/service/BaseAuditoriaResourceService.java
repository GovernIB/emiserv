package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.base.service.BaseNoDatabaseMutableResourceService;
import es.caib.emiserv.logic.helper.AuditoriaResourceHelper;
import es.caib.emiserv.logic.helper.ExportacioHelper;
import es.caib.emiserv.logic.helper.SecurityHelper;
import es.caib.emiserv.logic.helper.SubrecursServeiHelper;
import es.caib.emiserv.logic.intf.base.exception.AnswerRequiredException;
import es.caib.emiserv.logic.intf.base.exception.ReportGenerationException;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotDeletedException;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.emiserv.logic.intf.base.model.DownloadableFile;
import es.caib.emiserv.logic.intf.base.model.ExportField;
import es.caib.emiserv.logic.intf.base.model.Resource;
import es.caib.emiserv.logic.intf.base.model.ReportFileType;
import es.caib.emiserv.logic.intf.dto.AuditoriaFiltreDto;
import es.caib.emiserv.logic.intf.dto.AuditoriaPeticioDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.exception.ExportacioMassaGranException;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.OrdenacioNoValidaException;
import es.caib.emiserv.logic.intf.exception.PermissionDeniedException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.model.ExportacioAuditoriaForm;
import es.caib.emiserv.logic.intf.model.ServeiResource;
import es.caib.emiserv.persist.base.entity.NoDatabaseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base dels recursos d'auditoria de la interfície REACT (peticions de backoffice i de l'enrutador):
 * recursos de només lectura que deleguen a la consulta paginada de la JSP i que s'exporten a CSV o
 * XLSX amb el mateix filtre i ordre que la graella (informe EXPORTAR).
 * <p/>
 * L'exportació fa una sola consulta: demana una pàgina de la mida màxima configurada
 * (es.caib.emiserv.exportacio.max) i, si el total la supera, no exporta res
 * (ExportacioMassaGranException, que l'API torna com a 422).
 *
 * @param <R> classe del recurs.
 * @param <ID> classe de l'identificador del recurs.
 *
 * @author Limit Tecnologies
 */
public abstract class BaseAuditoriaResourceService<R extends Resource<ID>, ID extends Serializable>
		extends BaseNoDatabaseMutableResourceService<R, ID> {

	public static final String REPORT_EXPORTAR_CODE = "EXPORTAR";

	private static final ResourceBundleMessageSource MISSATGES_EXPORTACIO = new ResourceBundleMessageSource();
	static {
		MISSATGES_EXPORTACIO.setBasename("es/caib/emiserv/logic/i18n/exportacio");
		MISSATGES_EXPORTACIO.setDefaultEncoding("UTF-8");
		MISSATGES_EXPORTACIO.setFallbackToSystemLocale(false);
	}

	@Autowired
	protected AuditoriaResourceHelper auditoriaResourceHelper;
	@Autowired
	private ExportacioHelper exportacioHelper;
	@Autowired
	private SecurityHelper securityHelper;
	@Autowired
	private SubrecursServeiHelper subrecursServeiHelper;

	/** Consulta paginada de la JSP. */
	protected abstract PaginaDto<AuditoriaPeticioDto> consultar(AuditoriaFiltreDto filtre, PaginacioParamsDto paginacio);
	/** Petició pel seu identificador; null si no existeix. */
	protected abstract AuditoriaPeticioDto consultarPerId(ID id);
	protected abstract R toResource(AuditoriaPeticioDto dto, Long serveiId);
	/** Columnes de l'exportació, en aquest ordre. */
	protected abstract List<String> getColumnesExportacio();
	/** Nom base del fitxer exportat. */
	protected abstract String getNomFitxerExportacio();

	@PostConstruct
	public void initAuditoria() {
		register(REPORT_EXPORTAR_CODE, new ExportarReportGenerator());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<R> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		AuditoriaFiltreDto filtre = auditoriaResourceHelper.toFiltre(filter, quickFilter);
		PaginacioParamsDto paginacio = auditoriaResourceHelper.toPaginacio(pageable, exportacioHelper.getMaximInt());
		PaginaDto<AuditoriaPeticioDto> pagina = consultar(filtre, paginacio);
		List<R> contingut = toResources(contingut(pagina));
		if (pageable == null || pageable.isUnpaged()) {
			return new PageImpl<>(contingut);
		}
		return new PageImpl<>(contingut, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()), pagina != null ? pagina.getElementsTotal() : 0);
	}

	@Override
	@Transactional(readOnly = true)
	public R getOne(ID id, String[] perspectives) throws ResourceNotFoundException {
		AuditoriaPeticioDto dto;
		try {
			dto = consultarPerId(id);
		} catch (NotFoundException ex) {
			dto = null;
		}
		if (dto == null) {
			throw new ResourceNotFoundException(getResourceClass(), String.valueOf(id));
		}
		R resource = toResources(List.of(dto)).get(0);
		comprovarAccesPeticio(id, dto.getServeiCodi());
		return resource;
	}

	@Override
	public R create(R resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new ResourceNotCreatedException(getResourceClass(), "Recurs de només lectura");
	}

	@Override
	public R update(ID id, R resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new ResourceNotUpdatedException(getResourceClass(), String.valueOf(id), "Recurs de només lectura");
	}

	@Override
	public void delete(ID id, Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new ResourceNotDeletedException(getResourceClass(), String.valueOf(id), "Recurs de només lectura");
	}

	@Override
	@Transactional(readOnly = true)
	public <P extends Serializable> List<?> artifactReportGenerateData(ID id, String code, P params) {
		try {
			return super.artifactReportGenerateData(id, code, params);
		} catch (ReportGenerationException ex) {
			throw excepcioNegociOMateixa(ex);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public DownloadableFile artifactReportGenerateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
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
			Pageable pageable,
			ExportField[] fields,
			ReportFileType fileType,
			OutputStream out) {
		List<R> files = consultarPerExportar(quickFilter, filter, pageable != null ? pageable.getSort() : Sort.unsorted());
		return generarFitxer(files, fileType);
	}

	@Override
	protected NoDatabaseResourceEntity<R, ID> getEntity(ID id) throws ResourceNotFoundException {
		return NoDatabaseResourceEntity.<R, ID>builder().id(id).resource(getOne(id, null)).build();
	}

	/**
	 * Un EMS_RESP només pot consultar les peticions dels serveis on té permís (com el llistat). Els
	 * mètodes de detall de BackofficeService i RedireccioService no ho comproven: el detall, les
	 * sol·licituds i els missatges de l'API passen abans per aquí. Es respon «no trobat» per no revelar
	 * l'existència de la petició.
	 */
	private void comprovarAccesPeticio(ID id, String serveiCodi) {
		if (securityHelper.hasRole(ServeiResource.ROL_ADMIN)) {
			return;
		}
		Long serveiId = serveiCodi != null ? auditoriaResourceHelper.getServeiIds(List.of(serveiCodi)).get(serveiCodi) : null;
		try {
			if (serveiId == null) {
				throw new AccessDeniedException("Petició d'un servei inexistent");
			}
			subrecursServeiHelper.comprovarAccesServei(serveiId);
		} catch (AccessDeniedException | PermissionDeniedException | ResourceNotFoundException ex) {
			throw new ResourceNotFoundException(getResourceClass(), String.valueOf(id));
		}
	}

	/** Contingut d'una pàgina de la consulta: buit si no n'hi ha (PaginacioHelper el deixa null si la pàgina és buida). */
	private static List<AuditoriaPeticioDto> contingut(PaginaDto<AuditoriaPeticioDto> pagina) {
		return pagina != null && pagina.getContingut() != null ? pagina.getContingut() : List.of();
	}

	/** Recursos d'una pàgina, amb l'id del servei de cada petició (una sola consulta per pàgina). */
	private List<R> toResources(List<AuditoriaPeticioDto> dtos) {
		Map<String, Long> serveiIds = auditoriaResourceHelper.getServeiIds(dtos.stream().
				map(AuditoriaPeticioDto::getServeiCodi).
				filter(c -> c != null).
				collect(Collectors.toSet()));
		return dtos.stream().
				map(dto -> toResource(dto, serveiIds.get(dto.getServeiCodi()))).
				collect(Collectors.toList());
	}

	private List<R> consultarPerExportar(String quickFilter, String filter, Sort sort) {
		long maxim = exportacioHelper.getMaxim();
		AuditoriaFiltreDto filtre = auditoriaResourceHelper.toFiltre(filter, quickFilter);
		PaginacioParamsDto paginacio = auditoriaResourceHelper.toPaginacio(
				PageRequest.of(0, exportacioHelper.getMaximInt(), sort != null ? sort : Sort.unsorted()),
				exportacioHelper.getMaximInt());
		PaginaDto<AuditoriaPeticioDto> pagina = consultar(filtre, paginacio);
		if (pagina != null && pagina.getElementsTotal() > maxim) {
			throw new ExportacioMassaGranException(pagina.getElementsTotal(), maxim);
		}
		return toResources(contingut(pagina));
	}

	private DownloadableFile generarFitxer(List<?> files, ReportFileType fileType) {
		if (fileType != ReportFileType.CSV && fileType != ReportFileType.XLSX) {
			throw new ValidationException("Format d'exportació no suportat (CSV o XLSX): " + fileType);
		}
		String format = fileType.name();
		List<String> capcaleres = getColumnesExportacio().stream().
				map(c -> MISSATGES_EXPORTACIO.getMessage("auditoria.exportacio.columna." + c, null, c, LocaleContextHolder.getLocale())).
				collect(Collectors.toList());
		return new DownloadableFile(
				exportacioHelper.getNomFitxer(getNomFitxerExportacio(), format),
				exportacioHelper.getContentType(format),
				exportacioHelper.generar(files, getColumnesExportacio(), capcaleres, format));
	}

	private static RuntimeException excepcioNegociOMateixa(RuntimeException ex) {
		Throwable causa = ex.getCause();
		boolean negoci = causa instanceof ValidationException ||
				causa instanceof PermissionDeniedException ||
				causa instanceof AccessDeniedException ||
				causa instanceof ExportacioMassaGranException ||
				causa instanceof OrdenacioNoValidaException;
		return negoci ? (RuntimeException)causa : ex;
	}

	private class ExportarReportGenerator implements ReportGenerator<NoDatabaseResourceEntity<R, ID>, ExportacioAuditoriaForm, R> {
		@Override
		public List<R> generateData(String code, NoDatabaseResourceEntity<R, ID> entity, ExportacioAuditoriaForm params) {
			try {
				return consultarPerExportar(
						params != null ? params.getQuickFilter() : null,
						params != null ? params.getFilter() : null,
						params != null ? ServeiResourceServiceImpl.toSort(params.getSort()) : Sort.unsorted());
			} catch (ExportacioMassaGranException | OrdenacioNoValidaException | ValidationException ex) {
				// Embolcallada perquè la classe base no la registri com a error (es desembolica a artifactReportGenerateData)
				throw new ReportGenerationException(getResourceClass(), null, code, ex.getMessage(), ex);
			}
		}
		@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
			return generarFitxer(data, fileType);
		}
		@Override
		public void onChange(Serializable id, ExportacioAuditoriaForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExportacioAuditoriaForm target) {
		}
	}

}
