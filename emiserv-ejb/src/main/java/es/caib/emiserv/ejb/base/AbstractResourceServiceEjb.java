/**
 * 
 */
package es.caib.emiserv.ejb.base;

import es.caib.emiserv.ejb.AbstractService;
import es.caib.emiserv.logic.intf.base.exception.*;
import es.caib.emiserv.logic.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.emiserv.logic.intf.base.model.*;
import es.caib.emiserv.logic.intf.base.service.MutableResourceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Classe base dels EJBs que exposen un servei de recurs ({@link MutableResourceService}).
 * Delega cada mètode (també els genèrics {@code artifactActionExec}, {@code artifactOnChange}
 * i {@code artifactReportGenerateData}) en el servei obtingut per {@link AbstractService}.
 * <p/>
 * Només serveis mutables: els serveis de només lectura necessitarien un altre pare.
 * <p/>
 * Cada recurs només ha de definir:
 * <pre>
 * &#64;Stateless
 * &#64;RolesAllowed("**")
 * public class XResourceServiceEjb
 *         extends AbstractResourceServiceEjb&lt;XResourceService, XResource, Long&gt;
 *         implements XResourceService {
 * }
 * </pre>
 * El primer paràmetre genèric ha de ser el servei: {@link AbstractService} el cerca
 * per obtenir el bean delegat.
 *
 * @param <S> interfície del servei del recurs.
 * @param <R> classe del recurs.
 * @param <ID> classe de la clau primària del recurs.
 *
 * @author Limit Tecnologies
 */
public abstract class AbstractResourceServiceEjb<
		S extends MutableResourceService<R, ID>,
		R extends Resource<? extends Serializable>,
		ID extends Serializable>
		extends AbstractService<S>
		implements MutableResourceService<R, ID> {

	@Override
	public R getOne(ID id, String[] perspectives) throws ResourceNotFoundException {
		return getDelegateService().getOne(id, perspectives);
	}

	@Override
	public Page<R> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		return getDelegateService().findPage(quickFilter, filter, namedQueries, perspectives, pageable);
	}

	@Override
	public DownloadableFile export(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable,
			ExportField[] fields,
			ReportFileType fileType,
			OutputStream out) {
		return getDelegateService().export(
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable,
				fields,
				fileType,
				out);
	}

	@Override
	public Optional<DownloadableFile> fieldDownload(
			ID id,
			String fieldName,
			OutputStream out) throws ResourceNotFoundException, ResourceFieldNotFoundException, FieldArtifactNotFoundException, IOException {
		return getDelegateService().fieldDownload(id, fieldName, out);
	}

	@Override
	public List<ResourceArtifact> artifactFindAll(ResourceArtifactType type) {
		return getDelegateService().artifactFindAll(type);
	}

	@Override
	public ResourceArtifact artifactGetOne(ResourceArtifactType type, String code) throws ArtifactNotFoundException {
		return getDelegateService().artifactGetOne(type, code);
	}

	@Override
	public <P extends Serializable> Map<String, Object> artifactOnChange(
			ResourceArtifactType type,
			String code,
			ID id,
			P previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerValue> answers) throws ArtifactNotFoundException, ResourceFieldNotFoundException, AnswerRequiredException {
		return getDelegateService().artifactOnChange(type, code, id, previous, fieldName, fieldValue, answers);
	}

	@Override
	public List<FieldOption> artifactFieldEnumOptions(
			ResourceArtifactType type,
			String code,
			String fieldName,
			Map<String, String[]> requestParameterMap) {
		return getDelegateService().artifactFieldEnumOptions(type, code, fieldName, requestParameterMap);
	}

	@Override
	public <P extends Serializable> List<?> artifactReportGenerateData(
			ID id,
			String code,
			P params) throws ArtifactNotFoundException, ReportGenerationException {
		return getDelegateService().artifactReportGenerateData(id, code, params);
	}

	@Override
	public DownloadableFile artifactReportGenerateFile(
			String code,
			List<?> data,
			ReportFileType fileType,
			OutputStream out) throws ArtifactNotFoundException, ReportGenerationException {
		return getDelegateService().artifactReportGenerateFile(code, data, fileType, out);
	}

	@Override
	public R newResourceInstance() {
		return getDelegateService().newResourceInstance();
	}

	@Override
	public R create(
			R resource,
			Map<String, AnswerValue> answers) throws ResourceAlreadyExistsException, ResourceNotCreatedException, AnswerRequiredException {
		return getDelegateService().create(resource, answers);
	}

	@Override
	public R update(
			ID id,
			R resource,
			Map<String, AnswerValue> answers) throws ResourceNotFoundException, ResourceNotUpdatedException, AnswerRequiredException {
		return getDelegateService().update(id, resource, answers);
	}

	@Override
	public void delete(
			ID id,
			Map<String, AnswerValue> answers) throws ResourceNotFoundException, ResourceNotDeletedException, AnswerRequiredException {
		getDelegateService().delete(id, answers);
	}

	@Override
	public Map<String, Object> onChange(
			ID id,
			R previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerValue> answers) throws ResourceFieldNotFoundException, AnswerRequiredException {
		return getDelegateService().onChange(id, previous, fieldName, fieldValue, answers);
	}

	@Override
	public R sync(R resource) throws ResourceNotSyncedException {
		return getDelegateService().sync(resource);
	}

	@Override
	public <P extends Serializable> Serializable artifactActionExec(
			ID id,
			String code,
			P params) throws ArtifactNotFoundException, ActionExecutionException {
		return getDelegateService().artifactActionExec(id, code, params);
	}

	@Override
	public List<FieldOption> fieldEnumOptions(
			String fieldName,
			Map<String, String[]> requestParameterMap) {
		return getDelegateService().fieldEnumOptions(fieldName, requestParameterMap);
	}

}
