package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.base.service.BaseNoDatabaseMutableResourceService;
import es.caib.emiserv.logic.helper.SubrecursServeiHelper;
import es.caib.emiserv.logic.intf.base.exception.ActionExecutionException;
import es.caib.emiserv.logic.intf.base.exception.AnswerRequiredException;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiDto;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiProvaDto;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiTipusEnumDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.PermissionDeniedException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.model.ServeiRutaResource;
import es.caib.emiserv.logic.intf.service.ServeiRutaResourceService;
import es.caib.emiserv.logic.intf.service.ServeiService;
import es.caib.emiserv.persist.base.entity.NoDatabaseResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servei del recurs de rutes dels serveis enrutadors (interfície REACT).
 * <p/>
 * Totes les operacions deleguen a ServeiService, com la JSP (ServeiRutesController), i abans
 * comproven que l'usuari pot accedir al servei (EMS_RESP només als serveis on té permís).
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Service
public class ServeiRutaResourceServiceImpl extends BaseNoDatabaseMutableResourceService<ServeiRutaResource, Long> implements ServeiRutaResourceService {

	/** Màxim de rutes d'un servei que es consulten d'un cop (en té poques). */
	private static final int MAX_RUTES = 10000;

	@Autowired
	private ServeiService serveiService;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private SubrecursServeiHelper subrecursServeiHelper;

	@PostConstruct
	public void init() {
		register(ServeiRutaResource.ACTION_MOURE_CODE, new MoureActionExecutor());
		register(ServeiRutaResource.ACTION_PROVAR_CODE, new ProvarActionExecutor());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ServeiRutaResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		Long serveiId = subrecursServeiHelper.getServeiIdDelFiltre(filter);
		subrecursServeiHelper.comprovarAccesServei(serveiId);
		return subrecursServeiHelper.pagina(rutesDelServei(serveiId), pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public ServeiRutaResource getOne(Long id, String[] perspectives) throws ResourceNotFoundException {
		Long serveiId = getServeiIdDeLaRuta(id);
		subrecursServeiHelper.comprovarAccesServei(serveiId);
		return rutesDelServei(serveiId).stream().
				filter(r -> id.equals(r.getId())).
				findFirst().
				orElseThrow(() -> new ResourceNotFoundException(ServeiRutaResource.class, String.valueOf(id)));
	}

	@Override
	@Transactional
	public ServeiRutaResource create(ServeiRutaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		subrecursServeiHelper.comprovarAccesServei(resource.getServeiId());
		ServeiRutaDestiDto creada = serveiService.rutaDestiCreate(resource.getServeiId(), toDto(resource));
		return getOne(creada.getId(), null);
	}

	@Override
	@Transactional
	public ServeiRutaResource update(Long id, ServeiRutaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		ServeiRutaResource actual = getOne(id, null);
		ServeiRutaDestiDto dto = toDto(resource);
		dto.setId(id);
		// L'ordre només es canvia amb l'acció MOURE
		dto.setOrdre(actual.getOrdre());
		serveiService.rutaDestiUpdate(actual.getServeiId(), dto);
		return getOne(id, null);
	}

	@Override
	@Transactional
	public void delete(Long id, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		ServeiRutaResource actual = getOne(id, null);
		serveiService.rutaDestiDelete(actual.getServeiId(), id);
	}

	@Override
	@Transactional
	public <P extends Serializable> Serializable artifactActionExec(Long id, String code, P params) {
		try {
			return super.artifactActionExec(id, code, params);
		} catch (ActionExecutionException ex) {
			// Els errors de negoci arriben al client amb el seu missatge
			Throwable causa = ex.getCause();
			if (causa instanceof ValidationException || causa instanceof NotFoundException ||
					causa instanceof PermissionDeniedException || causa instanceof AccessDeniedException ||
					causa instanceof ResourceNotFoundException) {
				throw (RuntimeException)causa;
			}
			throw ex;
		}
	}

	@Override
	protected NoDatabaseResourceEntity<ServeiRutaResource, Long> getEntity(Long id) throws ResourceNotFoundException {
		return NoDatabaseResourceEntity.<ServeiRutaResource, Long>builder().
				id(id).
				resource(getOne(id, null)).
				build();
	}

	/**
	 * Servei d'una ruta, sense carregar l'entitat de la ruta: si ja fos al context de persistència
	 * amb el servei com a proxy, la comprovació de ServeiServiceImpl (ruta.getServei().equals(servei),
	 * que compara les classes) fallaria.
	 */
	private Long getServeiIdDeLaRuta(Long rutaId) {
		List<Long> resultat = entityManager.createQuery(
				"select r.servei.id from ServeiRutaDestiEntity r where r.id = :id", Long.class).
				setParameter("id", rutaId).
				getResultList();
		if (resultat.isEmpty()) {
			throw new ResourceNotFoundException(ServeiRutaResource.class, String.valueOf(rutaId));
		}
		return resultat.get(0);
	}

	private List<ServeiRutaResource> rutesDelServei(Long serveiId) {
		PaginacioParamsDto paginacio = new PaginacioParamsDto();
		paginacio.setPaginaNum(0);
		paginacio.setPaginaTamany(MAX_RUTES);
		paginacio.afegirOrdre("ordre", PaginacioParamsDto.OrdreDireccioDto.ASCENDENT);
		return serveiService.rutaDestiFindByServei(serveiId, paginacio).getContingut().stream().
				map(dto -> toResource(serveiId, dto)).
				collect(Collectors.toList());
	}

	private static ServeiRutaResource toResource(Long serveiId, ServeiRutaDestiDto dto) {
		ServeiRutaResource resource = new ServeiRutaResource();
		resource.setId(dto.getId());
		resource.setServeiId(serveiId);
		resource.setOrdre(dto.getOrdre());
		resource.setEntitatCodi(dto.getEntitatCodi());
		resource.setUrl(dto.getUrl());
		resource.setDestiTipus(dto.getDestiTipus() != null ? dto.getDestiTipus() : ServeiRutaDestiTipusEnumDto.EMISERV);
		resource.setBackofficeAutenticacio(dto.getBackofficeAutenticacio());
		resource.setCredencialsPropietats(dto.getCredencialsPropietats());
		resource.setCredencialsConfigurades(dto.getCredencialsConfigurades());
		resource.setPausada(dto.isPausada());
		return resource;
	}

	private static ServeiRutaDestiDto toDto(ServeiRutaResource resource) {
		ServeiRutaDestiDto dto = new ServeiRutaDestiDto();
		dto.setEntitatCodi(resource.getEntitatCodi());
		dto.setUrl(resource.getUrl());
		dto.setDestiTipus(resource.getDestiTipus());
		// L'autenticació només s'aplica a les rutes cap a un backoffice, com a la JSP
		dto.setBackofficeAutenticacio(
				ServeiRutaDestiTipusEnumDto.BACKOFFICE.equals(resource.getDestiTipus()) ? resource.getBackofficeAutenticacio() : null);
		return dto;
	}

	private class MoureActionExecutor implements ActionExecutor<NoDatabaseResourceEntity<ServeiRutaResource, Long>, ServeiRutaResource.MoureForm, Serializable> {
		@Override
		public Serializable exec(String code, NoDatabaseResourceEntity<ServeiRutaResource, Long> entity, ServeiRutaResource.MoureForm params) {
			if (params == null || params.getPosicio() == null || params.getPosicio() < 0) {
				throw new ValidationException("Cal indicar una posició vàlida");
			}
			serveiService.rutaDestiMourePosicio(entity.getId(), params.getPosicio());
			return null;
		}
		@Override
		public void onChange(Serializable id, ServeiRutaResource.MoureForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ServeiRutaResource.MoureForm target) {
		}
	}

	private class ProvarActionExecutor implements ActionExecutor<NoDatabaseResourceEntity<ServeiRutaResource, Long>, Serializable, ServeiRutaResource.ProvaResultat> {
		@Override
		public ServeiRutaResource.ProvaResultat exec(String code, NoDatabaseResourceEntity<ServeiRutaResource, Long> entity, Serializable params) {
			ServeiRutaDestiProvaDto prova = serveiService.rutaDestiProvarConnexio(entity.getResource().getServeiId(), entity.getId());
			ServeiRutaResource.ProvaResultat resultat = new ServeiRutaResource.ProvaResultat();
			resultat.setCorrecta(prova.isCorrecta());
			resultat.setUrl(prova.getUrl());
			resultat.setCodiHttp(prova.getCodiHttp());
			resultat.setMissatge(prova.getMissatge());
			resultat.setTempsMs(prova.getTempsMs());
			resultat.setCredencialsPendents(prova.isCredencialsPendents());
			return resultat;
		}
		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
		}
	}

}
