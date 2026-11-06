package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.base.service.BaseNoDatabaseMutableResourceService;
import es.caib.emiserv.logic.helper.ConfiguracioResourceHelper;
import es.caib.emiserv.logic.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.helper.ExportacioHelper;
import es.caib.emiserv.logic.intf.base.exception.AnswerRequiredException;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.emiserv.logic.intf.base.model.Resource;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.persist.base.entity.NoDatabaseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base dels recursos dels manteniments de configuració de la interfície REACT (aplicacions,
 * emissors, organismes, claus, autoritats de certificació, mòduls, paràmetres i entitats). Són
 * recursos sense entitat pròpia que deleguen a les operacions de la JSP (ScspService i
 * EntitatService), que comproven el rol EMS_ADMIN i apliquen les regles de negoci.
 * <p/>
 * Les validacions de camp són les de les anotacions del recurs (les mateixes que els Command de la
 * JSP). Les que necessiten consultar la base de dades (CIF o codi repetit) les fa
 * {@link #validar(Resource, Serializable)} i es tornen com a error de validació (409). Esborrar un
 * registre en ús (clau forana) es torna com a error de validació amb un missatge entenedor.
 *
 * @param <R> classe del recurs.
 * @param <ID> classe de l'identificador.
 * @param <D> classe del DTO de la JSP.
 *
 * @author Limit Tecnologies
 */
public abstract class BaseConfiguracioResourceService<R extends Resource<ID>, ID extends Serializable, D>
		extends BaseNoDatabaseMutableResourceService<R, ID> {

	@Autowired
	protected ConfiguracioResourceHelper configuracioResourceHelper;
	@Autowired
	protected ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ExportacioHelper exportacioHelper;
	@PersistenceContext
	private EntityManager entityManager;

	/** Columnes ordenables: camp del recurs → camp de l'entitat. */
	protected abstract Map<String, String> getCampsOrdenables();
	/** Camp del recurs per a l'ordre per defecte. */
	protected abstract String getOrdrePerDefecte();
	/** Camps del filtre de la graella. */
	protected Collection<String> getCampsFiltrables() {
		return Collections.emptyList();
	}
	/** Consulta paginada de la JSP. */
	protected abstract PaginaDto<D> consultar(Map<String, String> filtre, String quickFilter, PaginacioParamsDto paginacio);
	/** Registre pel seu id; null si no existeix. */
	protected abstract D consultarPerId(ID id);
	protected abstract R toResource(D dto);
	protected abstract D toDto(R resource);
	/** Alta; retorna el registre creat (o null si l'operació de la JSP no el torna). */
	protected D crear(D dto) {
		throw new ValidationException(configuracioResourceHelper.missatge("configuracio.nomes.lectura"));
	}
	protected void actualitzar(D dto) {
		throw new ValidationException(configuracioResourceHelper.missatge("configuracio.nomes.lectura"));
	}
	protected void esborrar(ID id) {
		throw new ValidationException(configuracioResourceHelper.missatge("configuracio.nomes.lectura"));
	}
	/** Id del recurs creat quan l'operació de la JSP no torna el registre. */
	protected ID getIdCreat(R resource) {
		return resource.getId();
	}
	/**
	 * Validacions que consulten la base de dades (CIF o codi repetit).
	 *
	 * @param id id del registre que es modifica; null en una alta.
	 * @throws ValidationException si no és vàlid.
	 */
	protected void validar(R resource, ID id) {
	}

	@Override
	@Transactional(readOnly = true)
	public Page<R> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		Map<String, String> filtre = configuracioResourceHelper.toFiltre(filter, getCampsFiltrables());
		PaginacioParamsDto paginacio = configuracioResourceHelper.toPaginacio(
				pageable,
				getCampsOrdenables(),
				getOrdrePerDefecte(),
				exportacioHelper.getMaximInt());
		PaginaDto<D> pagina = consultar(
				filtre,
				quickFilter != null && !quickFilter.trim().isEmpty() ? quickFilter.trim() : null,
				paginacio);
		List<R> contingut = pagina != null && pagina.getContingut() != null ?
				pagina.getContingut().stream().map(this::toResource).collect(Collectors.toList()) :
				List.of();
		if (pageable == null || pageable.isUnpaged()) {
			return new PageImpl<>(contingut);
		}
		return new PageImpl<>(
				contingut,
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()),
				pagina != null ? pagina.getElementsTotal() : 0);
	}

	@Override
	@Transactional(readOnly = true)
	public R getOne(ID id, String[] perspectives) throws ResourceNotFoundException {
		D dto;
		try {
			dto = consultarPerId(id);
		} catch (NotFoundException | javax.persistence.EntityNotFoundException | java.util.NoSuchElementException ex) {
			// Algunes consultes de la JSP fan Optional.get() (claus): també és «no trobat»
			dto = null;
		}
		if (dto == null) {
			throw new ResourceNotFoundException(getResourceClass(), String.valueOf(id));
		}
		return toResource(dto);
	}

	@Override
	@Transactional
	public R create(R resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		validar(resource, null);
		D creat = crear(toDto(resource));
		entityManager.flush();
		return creat != null ? toResource(creat) : getOne(getIdCreat(resource), null);
	}

	@Override
	@Transactional
	public R update(ID id, R resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		getOne(id, null);
		resource.setId(id);
		validar(resource, id);
		actualitzar(toDto(resource));
		entityManager.flush();
		entityManager.clear();
		return getOne(id, null);
	}

	@Override
	@Transactional
	public void delete(ID id, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		getOne(id, null);
		try {
			esborrar(id);
			// Les claus foranes es comproven aquí, no en acabar la transacció (fora de l'abast del catch)
			entityManager.flush();
		} catch (DataIntegrityViolationException | PersistenceException ex) {
			throw new ValidationException(configuracioResourceHelper.missatge("configuracio.en.us"));
		}
	}

	@Override
	protected NoDatabaseResourceEntity<R, ID> getEntity(ID id) throws ResourceNotFoundException {
		return NoDatabaseResourceEntity.<R, ID>builder().id(id).resource(getOne(id, null)).build();
	}

	/** Llança l'error de validació si ja hi ha un altre registre (amb un altre id) amb el valor. */
	protected void comprovarNoRepetit(Object idExistent, Object idActual, String clauMissatge, String valor) {
		if (idExistent != null && !idExistent.equals(idActual)) {
			throw new ValidationException(configuracioResourceHelper.missatge(clauMissatge, valor));
		}
	}

}
