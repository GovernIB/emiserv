package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.base.service.BaseNoDatabaseMutableResourceService;
import es.caib.emiserv.logic.helper.SecurityHelper;
import es.caib.emiserv.logic.helper.SubrecursServeiHelper;
import es.caib.emiserv.logic.intf.base.exception.AnswerRequiredException;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.emiserv.logic.intf.dto.PermisDto;
import es.caib.emiserv.logic.intf.dto.PrincipalTipusEnumDto;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.model.ServeiPermisResource;
import es.caib.emiserv.logic.intf.model.ServeiResource;
import es.caib.emiserv.logic.intf.service.ServeiPermisResourceService;
import es.caib.emiserv.logic.intf.service.ServeiService;
import es.caib.emiserv.persist.base.entity.NoDatabaseResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servei del recurs de permisos dels serveis (interfície REACT). Només EMS_ADMIN, com a la JSP
 * (ServeiPermisController); les operacions deleguen a ServeiService.
 * <p/>
 * L'identificador d'un permís és {@code <serveiId>-<tipus>-<principal en Base64 URL>}: els
 * identificadors de les entrades ACL canvien en modificar els permisos (i la memòria cau d'ACL no
 * sempre té els de la base de dades). L'entrada ACL que esborra ServeiService es pren de la mateixa
 * consulta de permisos.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Service
public class ServeiPermisResourceServiceImpl extends BaseNoDatabaseMutableResourceService<ServeiPermisResource, String> implements ServeiPermisResourceService {

	@Autowired
	private ServeiService serveiService;
	@Autowired
	private SecurityHelper securityHelper;
	@Autowired
	private SubrecursServeiHelper subrecursServeiHelper;

	@Override
	@Transactional(readOnly = true)
	public Page<ServeiPermisResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		comprovarAdministrador();
		Long serveiId = subrecursServeiHelper.getServeiIdDelFiltre(filter);
		List<ServeiPermisResource> permisos = serveiService.permisFindByServei(serveiId).stream().
				map(dto -> toResource(serveiId, dto)).
				sorted(Comparator.comparing(ServeiPermisResource::getPrincipalTipus).
						thenComparing(ServeiPermisResource::getPrincipalNom, String.CASE_INSENSITIVE_ORDER)).
				collect(Collectors.toList());
		return subrecursServeiHelper.pagina(permisos, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public ServeiPermisResource getOne(String id, String[] perspectives) throws ResourceNotFoundException {
		comprovarAdministrador();
		Clau clau = Clau.parse(id);
		return findPermis(clau).
				map(dto -> toResource(clau.serveiId, dto)).
				orElseThrow(() -> new ResourceNotFoundException(ServeiPermisResource.class, id));
	}

	@Override
	@Transactional
	public ServeiPermisResource create(ServeiPermisResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		comprovarAdministrador();
		Clau clau = new Clau(resource.getServeiId(), resource.getPrincipalTipus(), resource.getPrincipalNom());
		if (findPermis(clau).isPresent()) {
			throw new ValidationException("Ja hi ha un permís per a " + resource.getPrincipalNom() + ": modificau-lo");
		}
		serveiService.permisUpdate(resource.getServeiId(), toDto(resource));
		resource.setId(clau.toId());
		return resource;
	}

	@Override
	@Transactional
	public ServeiPermisResource update(String id, ServeiPermisResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		ServeiPermisResource actual = getOne(id, null);
		// El principal no es pot canviar, com a la JSP
		resource.setServeiId(actual.getServeiId());
		resource.setPrincipalTipus(actual.getPrincipalTipus());
		resource.setPrincipalNom(actual.getPrincipalNom());
		serveiService.permisUpdate(actual.getServeiId(), toDto(resource));
		resource.setId(id);
		return resource;
	}

	@Override
	@Transactional
	public void delete(String id, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		comprovarAdministrador();
		Clau clau = Clau.parse(id);
		PermisDto permis = findPermis(clau).orElseThrow(
				() -> new ResourceNotFoundException(ServeiPermisResource.class, id));
		serveiService.permisDelete(clau.serveiId, toLong(permis.getId()));
	}

	@Override
	protected NoDatabaseResourceEntity<ServeiPermisResource, String> getEntity(String id) throws ResourceNotFoundException {
		return NoDatabaseResourceEntity.<ServeiPermisResource, String>builder().
				id(id).
				resource(getOne(id, null)).
				build();
	}

	private Optional<PermisDto> findPermis(Clau clau) {
		return serveiService.permisFindByServei(clau.serveiId).stream().
				filter(p -> p.getPrincipalTipus() == clau.tipus && clau.nom.equals(p.getPrincipalNom())).
				findFirst();
	}

	private void comprovarAdministrador() {
		if (!securityHelper.hasRole(ServeiResource.ROL_ADMIN)) {
			throw new AccessDeniedException("Operació reservada al rol " + ServeiResource.ROL_ADMIN);
		}
	}

	private static ServeiPermisResource toResource(Long serveiId, PermisDto dto) {
		ServeiPermisResource resource = new ServeiPermisResource();
		resource.setId(new Clau(serveiId, dto.getPrincipalTipus(), dto.getPrincipalNom()).toId());
		resource.setServeiId(serveiId);
		resource.setPrincipalTipus(dto.getPrincipalTipus());
		resource.setPrincipalNom(dto.getPrincipalNom());
		resource.setAdministration(dto.isAdministration());
		return resource;
	}

	private static PermisDto toDto(ServeiPermisResource resource) {
		PermisDto dto = new PermisDto();
		dto.setPrincipalTipus(resource.getPrincipalTipus());
		dto.setPrincipalNom(resource.getPrincipalNom());
		dto.setAdministration(resource.isAdministration());
		return dto;
	}

	private static Long toLong(Serializable id) {
		if (id == null) {
			return null;
		}
		return id instanceof Number ? ((Number)id).longValue() : Long.valueOf(id.toString());
	}

	/** Identificador d'un permís: servei, tipus de principal i principal. */
	static class Clau {
		final Long serveiId;
		final PrincipalTipusEnumDto tipus;
		final String nom;
		Clau(Long serveiId, PrincipalTipusEnumDto tipus, String nom) {
			this.serveiId = serveiId;
			this.tipus = tipus;
			this.nom = nom;
		}
		String toId() {
			return serveiId + "-" + tipus.name() + "-" +
					Base64.getUrlEncoder().withoutPadding().encodeToString(nom.getBytes(StandardCharsets.UTF_8));
		}
		static Clau parse(String id) {
			String[] parts = id != null ? id.split("-", 3) : new String[0];
			try {
				return new Clau(
						Long.valueOf(parts[0]),
						PrincipalTipusEnumDto.valueOf(parts[1]),
						new String(Base64.getUrlDecoder().decode(parts[2]), StandardCharsets.UTF_8));
			} catch (RuntimeException ex) {
				throw new ResourceNotFoundException(ServeiPermisResource.class, id);
			}
		}
	}

}
