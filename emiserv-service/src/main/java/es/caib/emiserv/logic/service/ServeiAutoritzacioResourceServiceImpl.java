package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.base.service.BaseNoDatabaseMutableResourceService;
import es.caib.emiserv.logic.helper.SecurityHelper;
import es.caib.emiserv.logic.helper.SubrecursServeiHelper;
import es.caib.emiserv.logic.intf.base.exception.AnswerRequiredException;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.emiserv.logic.intf.base.model.FieldOption;
import es.caib.emiserv.logic.intf.dto.AplicacioDto;
import es.caib.emiserv.logic.intf.dto.AutoritzacioDto;
import es.caib.emiserv.logic.intf.dto.AutoritzacioFiltreDto;
import es.caib.emiserv.logic.intf.dto.OrganismeDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.model.ServeiAutoritzacioResource;
import es.caib.emiserv.logic.intf.model.ServeiResource;
import es.caib.emiserv.logic.intf.service.ScspService;
import es.caib.emiserv.logic.intf.service.ServeiAutoritzacioResourceService;
import es.caib.emiserv.persist.base.entity.NoDatabaseResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servei del recurs d'autoritzacions dels serveis (interfície REACT). Només EMS_ADMIN, com a la
 * JSP (AutoritzacioController); les operacions deleguen a ScspService.
 * <p/>
 * La consulta és paginada al servidor, perquè un servei pot tenir moltes autoritzacions. El filtre
 * ràpid cerca per aplicació, com el filtre de la JSP.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Service
public class ServeiAutoritzacioResourceServiceImpl extends BaseNoDatabaseMutableResourceService<ServeiAutoritzacioResource, Long> implements ServeiAutoritzacioResourceService {

	@Autowired
	private ScspService scspService;
	@Autowired
	private SecurityHelper securityHelper;
	@Autowired
	private SubrecursServeiHelper subrecursServeiHelper;

	@PostConstruct
	public void init() {
		// Opcions dels desplegables: les mateixes llistes que la JSP (AutoritzacioController)
		register(ServeiAutoritzacioResource.Fields.aplicacioId, (FieldOptionsProvider)(fieldName, params) ->
				scspService.aplicacioFindAll().stream().
						sorted(Comparator.comparing(a -> a.getCn() != null ? a.getCn() : "", String.CASE_INSENSITIVE_ORDER)).
						map(a -> new FieldOption(String.valueOf(a.getId()), descripcio(a))).
						collect(Collectors.toList()));
		register(ServeiAutoritzacioResource.Fields.organismeId, (FieldOptionsProvider)(fieldName, params) ->
				scspService.organismeFindAll().stream().
						sorted(Comparator.comparing(o -> o.getNom() != null ? o.getNom() : "", String.CASE_INSENSITIVE_ORDER)).
						map(o -> new FieldOption(String.valueOf(o.getId()), descripcio(o))).
						collect(Collectors.toList()));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ServeiAutoritzacioResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		comprovarAdministrador();
		AutoritzacioFiltreDto filtre = new AutoritzacioFiltreDto();
		filtre.setServeiId(subrecursServeiHelper.getServeiIdDelFiltre(filter));
		if (quickFilter != null && !quickFilter.trim().isEmpty()) {
			filtre.setAplicacio(quickFilter.trim());
		}
		Pageable pagina = pageable != null && pageable.isPaged() ? pageable : PageRequest.of(0, Integer.MAX_VALUE);
		PaginacioParamsDto paginacio = new PaginacioParamsDto();
		paginacio.setPaginaNum(pagina.getPageNumber());
		paginacio.setPaginaTamany(pagina.getPageSize());
		PaginaDto<AutoritzacioDto> resultat = scspService.autoritzacioFindByFiltrePaginat(filtre, paginacio);
		List<ServeiAutoritzacioResource> contingut = resultat.getContingut().stream().
				map(ServeiAutoritzacioResourceServiceImpl::toResource).
				collect(Collectors.toList());
		return new PageImpl<>(contingut, pagina, resultat.getElementsTotal());
	}

	@Override
	@Transactional(readOnly = true)
	public ServeiAutoritzacioResource getOne(Long id, String[] perspectives) throws ResourceNotFoundException {
		comprovarAdministrador();
		try {
			return toResource(scspService.autoritzacioFindById(id));
		} catch (NotFoundException ex) {
			throw new ResourceNotFoundException(ServeiAutoritzacioResource.class, String.valueOf(id));
		}
	}

	@Override
	@Transactional
	public ServeiAutoritzacioResource create(ServeiAutoritzacioResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		comprovarAdministrador();
		AutoritzacioDto creada = scspService.autoritzacioCreate(toDto(resource));
		return getOne(creada.getId(), null);
	}

	@Override
	@Transactional
	public ServeiAutoritzacioResource update(Long id, ServeiAutoritzacioResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		ServeiAutoritzacioResource actual = getOne(id, null);
		AutoritzacioDto dto = toDto(resource);
		dto.setId(id);
		// autoritzacioFindById no informa el servei: es pren el de la graella si no hi és
		dto.setServeiId(actual.getServeiId() != null ? actual.getServeiId() : resource.getServeiId());
		scspService.autoritzacioUpdate(dto);
		return getOne(id, null);
	}

	@Override
	@Transactional
	public void delete(Long id, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		getOne(id, null);
		scspService.autoritzacioDelete(id);
	}

	@Override
	protected NoDatabaseResourceEntity<ServeiAutoritzacioResource, Long> getEntity(Long id) throws ResourceNotFoundException {
		return NoDatabaseResourceEntity.<ServeiAutoritzacioResource, Long>builder().
				id(id).
				resource(getOne(id, null)).
				build();
	}

	private void comprovarAdministrador() {
		if (!securityHelper.hasRole(ServeiResource.ROL_ADMIN)) {
			throw new AccessDeniedException("Operació reservada al rol " + ServeiResource.ROL_ADMIN);
		}
	}

	private static String descripcio(AplicacioDto aplicacio) {
		return aplicacio.getCertificatNif() != null ?
				aplicacio.getCn() + " (" + aplicacio.getCertificatNif() + ")" :
				aplicacio.getCn();
	}

	private static String descripcio(OrganismeDto organisme) {
		return organisme.getCif() != null ?
				organisme.getNom() + " (" + organisme.getCif() + ")" :
				organisme.getNom();
	}

	private static ServeiAutoritzacioResource toResource(AutoritzacioDto dto) {
		ServeiAutoritzacioResource resource = new ServeiAutoritzacioResource();
		resource.setId(dto.getId());
		resource.setServeiId(dto.getServeiId());
		resource.setAplicacioId(dto.getAplicacioId() != null ? dto.getAplicacioId().longValue() : null);
		resource.setAplicacioNom(dto.getAplicacioNom());
		resource.setOrganismeId(dto.getOrganismeId());
		resource.setOrganismeNom(dto.getOrganismeNom());
		resource.setDataAlta(dto.getDataAlta());
		resource.setDataBaixa(dto.getDataBaixa());
		return resource;
	}

	private static AutoritzacioDto toDto(ServeiAutoritzacioResource resource) {
		AutoritzacioDto dto = new AutoritzacioDto();
		dto.setServeiId(resource.getServeiId());
		dto.setAplicacioId(resource.getAplicacioId() != null ? resource.getAplicacioId().intValue() : null);
		dto.setOrganismeId(resource.getOrganismeId());
		dto.setDataAlta(resource.getDataAlta());
		dto.setDataBaixa(resource.getDataBaixa());
		return dto;
	}

}
