package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.base.service.BaseNoDatabaseMutableResourceService;
import es.caib.emiserv.logic.intf.base.exception.AnswerRequiredException;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotDeletedException;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.emiserv.logic.intf.base.model.FieldOption;
import es.caib.emiserv.logic.intf.dto.ServeiConfigScspDto;
import es.caib.emiserv.logic.intf.dto.ServeiDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.PermissionDeniedException;
import es.caib.emiserv.logic.intf.model.ServeiConfigScspResource;
import es.caib.emiserv.logic.intf.service.ScspService;
import es.caib.emiserv.logic.intf.service.ServeiConfigScspResourceService;
import es.caib.emiserv.logic.intf.service.ServeiService;
import es.caib.emiserv.persist.base.entity.NoDatabaseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servei del recurs de configuració SCSP d'un servei (interfície REACT). Fa el mateix que
 * ServeiConfigController (serveiConfigScsp.jsp): llegeix i desa amb ServeiService, que comprova el
 * permís de l'usuari sobre el servei (EMS_RESP, només els seus) i gestiona el pas de la gestió XSD.
 *
 * @author Limit Tecnologies
 */
@Service
public class ServeiConfigScspResourceServiceImpl
		extends BaseNoDatabaseMutableResourceService<ServeiConfigScspResource, Long>
		implements ServeiConfigScspResourceService {

	/** Tipus de seguretat, com la JSP. */
	private static final List<String> TIPUS_SEGURETAT = Arrays.asList("XMLSignature", "WS-Security");

	@Autowired
	private ServeiService serveiService;
	@Autowired
	private ScspService scspService;
	@PersistenceContext
	private EntityManager entityManager;

	@PostConstruct
	public void init() {
		// Opcions dels desplegables: les mateixes llistes que la JSP
		register(ServeiConfigScspResource.Fields.emisorId, (FieldOptionsProvider)(fieldName, params) ->
				scspService.emissorFindAll().stream().
						sorted(Comparator.comparing(e -> e.getNom() != null ? e.getNom() : "", String.CASE_INSENSITIVE_ORDER)).
						map(e -> new FieldOption(String.valueOf(e.getId()), e.getNom() + " (" + e.getCif() + ")")).
						collect(Collectors.toList()));
		register(ServeiConfigScspResource.Fields.claveFirma, (FieldOptionsProvider)(fieldName, params) ->
				scspService.clauPrivadaFindAll().stream().
						map(c -> new FieldOption(String.valueOf(c.getId()), c.getNom() + " (" + c.getAlies() + ")")).
						collect(Collectors.toList()));
		register(ServeiConfigScspResource.Fields.claveCifrado, (FieldOptionsProvider)(fieldName, params) ->
				scspService.clauPublicaFindAll().stream().
						map(c -> new FieldOption(String.valueOf(c.getId()), c.getNom() + " (" + c.getAlies() + ")")).
						collect(Collectors.toList()));
		register(ServeiConfigScspResource.Fields.tipoSeguridad, (FieldOptionsProvider)(fieldName, params) ->
				TIPUS_SEGURETAT.stream().map(t -> new FieldOption(t, t)).collect(Collectors.toList()));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ServeiConfigScspResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		// Recurs d'un sol element per servei: només es consulta pel seu id
		return new PageImpl<>(List.of());
	}

	@Override
	@Transactional(readOnly = true)
	public ServeiConfigScspResource getOne(Long id, String[] perspectives) throws ResourceNotFoundException {
		ServeiDto servei;
		try {
			servei = serveiService.findById(id);
		} catch (NotFoundException | PermissionDeniedException ex) {
			servei = null;
		}
		if (servei == null) {
			throw new ResourceNotFoundException(ServeiConfigScspResource.class, String.valueOf(id));
		}
		ServeiConfigScspResource resource;
		if (servei.isConfiguratScsp()) {
			resource = toResource(serveiService.configuracioScspFindByServei(id));
			resource.setConfigurat(true);
			// Com la JSP: amb la gestió XSD activa, l'esquema que es mostra és el de la còpia
			if (servei.isXsdGestioActiva()) {
				resource.setXsdGestioActiva(true);
				resource.setEsquemas(servei.getXsdEsquemaBackup());
			}
		} else {
			resource = new ServeiConfigScspResource();
			resource.setCodigoCertificado(servei.getCodi());
		}
		resource.setId(id);
		return resource;
	}

	@Override
	public ServeiConfigScspResource create(ServeiConfigScspResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new ResourceNotCreatedException(ServeiConfigScspResource.class, "La configuració SCSP es desa amb el servei (PUT)");
	}

	@Override
	@Transactional
	public ServeiConfigScspResource update(Long id, ServeiConfigScspResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		ServeiConfigScspResource actual = getOne(id, null);
		// El codi és el del servei
		resource.setCodigoCertificado(actual.getCodigoCertificado());
		serveiService.configuracioScspUpdate(id, toDto(resource));
		entityManager.flush();
		entityManager.clear();
		return getOne(id, null);
	}

	@Override
	public void delete(Long id, Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new ResourceNotDeletedException(ServeiConfigScspResource.class, String.valueOf(id), "La configuració SCSP no es pot esborrar");
	}

	@Override
	protected NoDatabaseResourceEntity<ServeiConfigScspResource, Long> getEntity(Long id) throws ResourceNotFoundException {
		return NoDatabaseResourceEntity.<ServeiConfigScspResource, Long>builder().id(id).resource(getOne(id, null)).build();
	}

	private static String text(Long id) {
		return id != null ? String.valueOf(id) : null;
	}

	/** Id d'una opció; null si és buida o no és un número (la validació ja ha comprovat els obligatoris). */
	private static Long id(String valor) {
		try {
			return valor != null && !valor.trim().isEmpty() ? Long.valueOf(valor.trim()) : null;
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private static ServeiConfigScspResource toResource(ServeiConfigScspDto dto) {
		ServeiConfigScspResource r = new ServeiConfigScspResource();
		r.setCodigoCertificado(dto.getCodigoCertificado());
		r.setEmisorId(text(dto.getEmisorId()));
		r.setFechaAlta(dto.getFechaAlta());
		r.setFechaBaja(dto.getFechaBaja());
		r.setCaducidad(dto.getCaducidad());
		r.setUrlSincrona(dto.getUrlSincrona());
		r.setUrlAsincrona(dto.getUrlAsincrona());
		r.setActionSincrona(dto.getActionSincrona());
		r.setActionAsincrona(dto.getActionAsincrona());
		r.setActionSolicitud(dto.getActionSolicitud());
		r.setVersionEsquema(dto.getVersionEsquema());
		r.setEsquemas(dto.getEsquemas());
		r.setTipoSeguridad(dto.getTipoSeguridad());
		r.setClaveFirma(text(dto.getClaveFirma()));
		r.setClaveCifrado(text(dto.getClaveCifrado()));
		r.setXpathCifradoSincrono(dto.getXpathCifradoSincrono());
		r.setXpathCifradoAsincrono(dto.getXpathCifradoAsincrono());
		r.setAlgoritmoCifrado(dto.getAlgoritmoCifrado());
		r.setValidacionFirma(dto.getValidacionFirma());
		r.setPrefijoPeticion(dto.getPrefijoPeticion());
		r.setNumeroMaximoReenvios(dto.getNumeroMaximoReenvios());
		r.setMaxSolicitudesPeticion(dto.getMaxSolicitudesPeticion());
		r.setPrefijoIdTransmision(dto.getPrefijoIdTransmision());
		r.setXpathCodigoError(dto.getXpathCodigoError());
		r.setXpathLiteralError(dto.getXpathLiteralError());
		r.setTimeout(dto.getTimeout());
		return r;
	}

	private static ServeiConfigScspDto toDto(ServeiConfigScspResource r) {
		ServeiConfigScspDto dto = new ServeiConfigScspDto();
		dto.setCodigoCertificado(r.getCodigoCertificado());
		dto.setEmisorId(id(r.getEmisorId()));
		dto.setFechaAlta(r.getFechaAlta());
		dto.setFechaBaja(r.getFechaBaja());
		dto.setCaducidad(r.getCaducidad());
		dto.setUrlSincrona(r.getUrlSincrona());
		dto.setUrlAsincrona(r.getUrlAsincrona());
		dto.setActionSincrona(r.getActionSincrona());
		dto.setActionAsincrona(r.getActionAsincrona());
		dto.setActionSolicitud(r.getActionSolicitud());
		dto.setVersionEsquema(r.getVersionEsquema());
		dto.setEsquemas(r.getEsquemas());
		dto.setXsdGestioActiva(r.isXsdGestioActiva());
		dto.setTipoSeguridad(r.getTipoSeguridad());
		dto.setClaveFirma(id(r.getClaveFirma()));
		dto.setClaveCifrado(id(r.getClaveCifrado()));
		dto.setXpathCifradoSincrono(r.getXpathCifradoSincrono());
		dto.setXpathCifradoAsincrono(r.getXpathCifradoAsincrono());
		dto.setAlgoritmoCifrado(r.getAlgoritmoCifrado());
		dto.setValidacionFirma(r.getValidacionFirma());
		dto.setPrefijoPeticion(r.getPrefijoPeticion());
		dto.setNumeroMaximoReenvios(r.getNumeroMaximoReenvios() != null ? r.getNumeroMaximoReenvios() : 0);
		dto.setMaxSolicitudesPeticion(r.getMaxSolicitudesPeticion() != null ? r.getMaxSolicitudesPeticion() : 0);
		dto.setPrefijoIdTransmision(r.getPrefijoIdTransmision());
		dto.setXpathCodigoError(r.getXpathCodigoError());
		dto.setXpathLiteralError(r.getXpathLiteralError());
		dto.setTimeout(r.getTimeout() != null ? r.getTimeout() : 0);
		return dto;
	}

}
