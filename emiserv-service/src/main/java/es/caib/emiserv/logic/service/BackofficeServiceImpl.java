/**
 * 
 */
package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.*;
import es.caib.emiserv.logic.helper.BackofficeHelper.ConfirmacionPeticionAmbException;
import es.caib.emiserv.logic.helper.BackofficeHelper.RespuestaAmbException;
import es.caib.emiserv.logic.helper.PaginacioHelper.Converter;
import es.caib.emiserv.logic.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.emiserv.logic.intf.dto.*;
import es.caib.emiserv.logic.intf.exception.BackofficeException;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.PermissionDeniedException;
import es.caib.emiserv.logic.intf.service.BackofficeService;
import es.caib.emiserv.logic.intf.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta;
import es.caib.emiserv.persist.entity.BackofficePeticioEntity;
import es.caib.emiserv.persist.entity.BackofficeSolicitudEntity;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCorePeticionRespuestaEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreTokenDataEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreTransmisionEntity;
import es.caib.emiserv.persist.repository.BackofficePeticioRepository;
import es.caib.emiserv.persist.repository.BackofficeSolicitudRepository;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCorePeticionRespuestaRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreTokenDataRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreTransmisionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementació del servei de backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class BackofficeServiceImpl implements BackofficeService {

	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private ScspCoreServicioRepository scspCoreServicioRepository;
	@Autowired
	private ScspCoreTransmisionRepository scspCoreTransmisionRepository;
	@Autowired
	private ScspCoreTokenDataRepository scspCoreTokenDataRepository;
	@Autowired
	private ScspCorePeticionRespuestaRepository scspCorePeticionRespuestaRepository;
	@Autowired
	private BackofficePeticioRepository backofficePeticioRepository;
	@Autowired
	private BackofficeSolicitudRepository backofficeSolicitudRepository;

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private SecurityHelper securityHelper;
	@Autowired
	private BackofficeHelper backofficeHelper;

	@Autowired
	private BackofficeClassloader backofficeClassloader;


	@Transactional(readOnly = true)
	@Override
	public PaginaDto<AuditoriaPeticioDto> peticioFindByFiltrePaginat(
			AuditoriaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		log.debug("Consulta amb filtre de peticions per a les auditories ("
				+ "filtre=" + filtre + ")");
		boolean nomesServeisPermesos = false;
		List<String> codisServeisPermesos = null;
		if (!securityHelper.hasRole("EMS_ADMIN")) {
			nomesServeisPermesos = true;
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			List<ServeiEntity> serveisPermesos = serveiRepository.findAll();
			permisosHelper.filterGrantedAll(
					serveisPermesos,
					new ObjectIdentifierExtractor<ServeiEntity>() {
						@Override
						public Serializable getObjectIdentifier(ServeiEntity servei) {
							return servei.getId();
						}
					},
					ServeiEntity.class,
					new Permission[] {BasePermission.ADMINISTRATION},
					auth);
			codisServeisPermesos = new ArrayList<String>();
			for (ServeiEntity serveiPermes: serveisPermesos) {
				codisServeisPermesos.add(serveiPermes.getCodi());
			}
		}
		Map<String, String> mapeigOrdenacio = new HashMap<String, String>();
		mapeigOrdenacio.put("peticioId", "peticionId");
		mapeigOrdenacio.put("dataPeticio", "fechaPeticion");
		mapeigOrdenacio.put("serveiCodi", "certificado");
		mapeigOrdenacio.put("estatCodi", "estado");
		Long scspServicioId = null;
		boolean esNullServei = filtre.getServei() == null;
		if (!esNullServei) {
			ServeiEntity servei = serveiRepository.getOne(filtre.getServei());
			ScspCoreServicioEntity servicio = scspCoreServicioRepository.findByCodigoCertificado(
					servei.getCodi());
			if (servicio != null) {
				scspServicioId = servicio.getId();
			}
		}
		PaginaDto<AuditoriaPeticioDto> resposta = paginacioHelper.toPaginaDto(
				scspCorePeticionRespuestaRepository.findByFiltrePaginat(
						filtre.getProcediment() == null || filtre.getProcediment().isEmpty(),
						filtre.getProcediment(),
						esNullServei,
						scspServicioId,
						filtre.getEstat() == null,
						PeticioEstatEnumDto.ERROR.equals(filtre.getEstat()),
						toEstatScsp(filtre.getEstat()),
						filtre.getDataInici() == null,
						filtre.getDataInici(),
						filtre.getDataFi() == null,
						filtre.getDataFi(),
						nomesServeisPermesos,
						codisServeisPermesos,
						filtre.getNumeroPeticio() == null || filtre.getNumeroPeticio().isEmpty(),
						filtre.getNumeroPeticio() != null ? filtre.getNumeroPeticio() : "",
						paginacioHelper.toSpringDataPageable(
								paginacioParams,
								mapeigOrdenacio)),
				AuditoriaPeticioDto.class,
				new Converter<ScspCorePeticionRespuestaEntity, AuditoriaPeticioDto>() {
					@Override
					public AuditoriaPeticioDto convert(
							ScspCorePeticionRespuestaEntity source) {
						return toAuditoriaPeticioDto(source);
					}
				});
		for (AuditoriaPeticioDto peticio: resposta.getContingut()) {
			String serveiCodi = peticio.getServeiCodi();
			for (ServeiEntity serveiPermes: serveiRepository.findAll()) {
				if (serveiPermes.getCodi().equals(serveiCodi)) {
					peticio.setServeiDescripcio(
							serveiPermes.getNom());
					break;
				}
			}
		}

		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public AuditoriaPeticioDto peticioFindById(
			String peticioId) {
		log.debug("Consulta dels detalls de la petició ("
				+ "peticioId=" + peticioId + ")");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ScspCorePeticionRespuestaEntity peticionRespuesta = scspCorePeticionRespuestaRepository.findByPeticionId(peticioId);
		if (peticionRespuesta == null) {
			throw new NotFoundException(
					peticioId,
					ScspCorePeticionRespuestaEntity.class);
		}
		ScspCoreServicioEntity coreServicio = backofficeHelper.findCoreServicioPerPeticionRespuesta(peticionRespuesta);
		ServeiEntity servei = serveiRepository.findByCodi(
				coreServicio.getCodigoCertificado());
		if (servei == null) {
			throw new NotFoundException(
					"(codi=" + peticionRespuesta.getCertificado() + ")",
					ServeiEntity.class);
		}
		if (!securityHelper.hasRole("EMS_ADMIN")) {
			boolean tePermisos = permisosHelper.isGrantedAll(
					servei.getId(),
					ServeiEntity.class,
					new Permission[] {BasePermission.ADMINISTRATION},
					auth);
			if (!tePermisos) {
				throw new PermissionDeniedException(
						servei.getId(),
						ServeiEntity.class,
						auth.getName(),
						BasePermission.ADMINISTRATION.toString());
			}
		}
		return toAuditoriaPeticioDto(peticionRespuesta);
	}

	@Transactional(readOnly = true)
	@Override
	public List<AuditoriaTransmisionDto> solicitudFindByPeticio(
			String peticioId) {
		log.debug("Consulta de les sol·licituds associades a una petició ("
				+ "peticioId=" + peticioId + ")");
		List<AuditoriaTransmisionDto> solicituds = conversioTipusHelper.convertirList(
				scspCoreTransmisionRepository.findByPeticionIdOrderBySolicitudIdAsc(peticioId),
				AuditoriaTransmisionDto.class);
		for (AuditoriaTransmisionDto solicitud: solicituds) {
			BackofficeSolicitudEntity backofficeSolicitud = backofficeSolicitudRepository.findByPeticioScspPeticionRespuestaPeticionIdAndSolicitudId(
					peticioId,
					solicitud.getSolicitudId());
			if (backofficeSolicitud != null) {
				solicitud.setBackofficeEstat(backofficeSolicitud.getEstat());
			}
		}
		return solicituds;
	}

	@Transactional(readOnly = true)
	@Override
	public AuditoriaTransmisionDto solicitudFindById(
			String peticioId,
			String solicitudId) {
		log.debug("Consulta una sol·licitud donat el seu id (" +
				"peticioId=" + peticioId + ", " +
				"solicitudId=" + solicitudId + ")");
		AuditoriaTransmisionDto solicitud = conversioTipusHelper.convertir(
				scspCoreTransmisionRepository.findByPeticionIdAndSolicitudId(
						peticioId,
						solicitudId),
				AuditoriaTransmisionDto.class);
		BackofficeSolicitudEntity backofficeSolicitud = backofficeSolicitudRepository.findByPeticioScspPeticionRespuestaPeticionIdAndSolicitudId(
				peticioId,
				solicitud.getSolicitudId());
		if (backofficeSolicitud != null) {
			solicitud.setBackofficeEstat(backofficeSolicitud.getEstat());
			if (backofficeSolicitud.getComunicacioDarrera() != null) {
				solicitud.setComunicacioBackofficeDisponible(true);
				solicitud.setComunicacioBackofficeError(
						backofficeSolicitud.getComunicacioDarrera().getError() != null);
			}
		}
		return solicitud;
	}

	@Transactional(readOnly = true)
	@Override
	public String peticioXmlPeticioScsp(
			String peticioId) {
		log.debug("Consulta del missatge XML de petició SCSP per a la petició (" +
				"peticioId=" + peticioId + ")");
		ScspCoreTokenDataEntity tokenData = scspCoreTokenDataRepository.findByPeticionIdAndTipoMensaje(
				peticioId,
				0);
		if (tokenData != null) {
			return tokenData.getDatos();
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	@Override
	public String peticioXmlRespostaScsp(
			String peticioId) {
		log.debug("Consulta del missatge XML de resposta SCSP per a la petició (" +
				"peticioId=" + peticioId + ")");
		ScspCorePeticionRespuestaEntity peticionRespuesta = scspCorePeticionRespuestaRepository.getOne(
				peticioId);
		if (peticionRespuesta != null) {
			ScspCoreTokenDataEntity tokenData = null;
			if ("0003".equals(peticionRespuesta.getEstado())) {
				tokenData = scspCoreTokenDataRepository.findByPeticionIdAndTipoMensaje(
						peticioId,
						3);
			} else {
				tokenData = scspCoreTokenDataRepository.findByPeticionIdAndTipoMensaje(
						peticioId,
						4);
			}
			if (tokenData != null) {
				return tokenData.getDatos();
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public String peticioXmlPeticioBackoffice(
			String peticioId) {
		log.debug("Consulta del missatge XML de petició al backoffice per a la petició (" +
				"peticioId=" + peticioId + ")");
		ScspCorePeticionRespuestaEntity peticionRespuesta = scspCorePeticionRespuestaRepository.getOne(
				peticioId);
		if (peticionRespuesta != null) {
			BackofficePeticioEntity backofficePeticio = backofficePeticioRepository.findByScspPeticionRespuesta(peticionRespuesta);
			if (backofficePeticio != null && backofficePeticio.getComunicacioDarrera() != null) {
				return backofficePeticio.getComunicacioDarrera().getPeticioXml();
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public String peticioXmlRespostaBackoffice(
			String peticioId) {
		log.debug("Consulta del missatge XML de resposta del backoffice per a la petició (" +
				"peticioId=" + peticioId + ")");
		ScspCorePeticionRespuestaEntity peticionRespuesta = scspCorePeticionRespuestaRepository.getOne(
				peticioId);
		if (peticionRespuesta != null) {
			BackofficePeticioEntity backofficePeticio = backofficePeticioRepository.findByScspPeticionRespuesta(peticionRespuesta);
			if (backofficePeticio != null && backofficePeticio.getComunicacioDarrera() != null) {
				return backofficePeticio.getComunicacioDarrera().getRespostaXml();
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public String peticioErrorComunicacioBackoffice(
			String peticioId) {
		log.debug("Consulta de l'error en la comunicació amb el backoffice per a la petició (" +
				"peticioId=" + peticioId + ")");
		ScspCorePeticionRespuestaEntity peticionRespuesta = scspCorePeticionRespuestaRepository.getOne(
				peticioId);
		if (peticionRespuesta != null) {
			BackofficePeticioEntity backofficePeticio = backofficePeticioRepository.findByScspPeticionRespuesta(peticionRespuesta);
			if (backofficePeticio != null && backofficePeticio.getComunicacioDarrera() != null) {
				return backofficePeticio.getComunicacioDarrera().getError();
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public String solicitudXmlPeticioBackoffice(
			String peticioId,
			String solicitudId) {
		log.debug("Consulta del missatge XML de resposta del backoffice per a la sol·licitud (" +
				"peticioId=" + peticioId + ")");
		BackofficeSolicitudEntity backofficeSolicitud = backofficeSolicitudRepository.findByPeticioScspPeticionRespuestaPeticionIdAndSolicitudId(
				peticioId,
				solicitudId);
		if (backofficeSolicitud != null && backofficeSolicitud.getComunicacioDarrera() != null) {
			return backofficeSolicitud.getComunicacioDarrera().getPeticioXml();
		}
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public String solicitudXmlRespostaBackoffice(
			String peticioId,
			String solicitudId) {
		log.debug("Consulta del missatge XML de resposta del backoffice per a la sol·licitud (" +
				"peticioId=" + peticioId + ", " +
				"solicitudId=" + solicitudId + ")");
		BackofficeSolicitudEntity backofficeSolicitud = backofficeSolicitudRepository.findByPeticioScspPeticionRespuestaPeticionIdAndSolicitudId(
				peticioId,
				solicitudId);
		if (backofficeSolicitud != null && backofficeSolicitud.getComunicacioDarrera() != null) {
			return backofficeSolicitud.getComunicacioDarrera().getRespostaXml();
		}
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public String solicitudErrorComunicacioBackoffice(
			String peticioId,
			String solicitudId) {
		log.debug("Consulta de l'error en la comunicació amb el backoffice per a la sol·licitud (" +
				"peticioId=" + peticioId + ", " +
				"solicitudId=" + solicitudId + ")");
		BackofficeSolicitudEntity backofficeSolicitud = backofficeSolicitudRepository.findByPeticioScspPeticionRespuestaPeticionIdAndSolicitudId(
				peticioId,
				solicitudId);
		if (backofficeSolicitud != null && backofficeSolicitud.getComunicacioDarrera() != null) {
			return backofficeSolicitud.getComunicacioDarrera().getError();
		}
		return null;
	}

	@Override
	public Respuesta peticioBackofficeSincrona(
			Peticion peticion) {
		/*es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion peticionBackoffice = conversioTipusHelper.convertir(
				peticion,
				es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion.class);*/
		/*copiarDatosEspecificosPeticion(
				peticion,
				peticionBackoffice);*/
		RespuestaAmbException respuestaAmbException = backofficeHelper.peticioSincrona(peticion/*peticionBackoffice*/);
		if (respuestaAmbException.getException() != null) {
			throw new BackofficeException(
					"Error processant petició síncrona: " +  ExceptionUtils.getRootCauseMessage(respuestaAmbException.getException()),
					respuestaAmbException.getException());
		} else {
			/*Respuesta respuesta = conversioTipusHelper.convertir(
					respuestaAmbException.getRespuesta(),
					Respuesta.class);*/
			/*copiarDatosEspecificosRespuesta(
					respuestaAmbException.getRespuesta(),
					respuesta);*/
			return respuestaAmbException.getRespuesta();
		}
	}

	@Override
	public ConfirmacionPeticion peticioBackofficeAsincrona(
			Peticion peticion) {
		/*es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion peticionBackoffice = conversioTipusHelper.convertir(
				peticion,
				es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion.class);
		copiarDatosEspecificosPeticion(
				peticion,
				peticionBackoffice);*/
		ConfirmacionPeticionAmbException confirmacionPeticionAmbException = backofficeHelper.peticioAsincrona(peticion/*peticionBackoffice*/);
		if (confirmacionPeticionAmbException.getException() != null) {
			throw new BackofficeException(
					"Error processant petició asíncrona: " + ExceptionUtils.getRootCauseMessage(confirmacionPeticionAmbException.getException()),
					confirmacionPeticionAmbException.getException());
		} else {
			return conversioTipusHelper.convertir(
					confirmacionPeticionAmbException.getConfirmacionPeticion(),
					ConfirmacionPeticion.class);
		}
	}

	@Override
	public Respuesta peticioBackofficeSolicitudRespuesta(
			SolicitudRespuesta solicitudRespuesta) {
		RespuestaAmbException respuestaAmbException = backofficeHelper.solicitudResposta(solicitudRespuesta/*
				conversioTipusHelper.convertir(
						solicitudRespuesta,
						es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta.class)*/);
		if (respuestaAmbException.getException() != null) {
			throw new BackofficeException(
					"Error processant sol·licitud de resposta: " +  ExceptionUtils.getRootCauseMessage(respuestaAmbException.getException()),
					respuestaAmbException.getException());
		} else {
			/*Respuesta respuesta = conversioTipusHelper.convertir(
					respuestaAmbException.getRespuesta(),
					Respuesta.class);*/
			/*copiarDatosEspecificosRespuesta(
					respuestaAmbException.getRespuesta(),
					respuesta);*/
			return respuestaAmbException.getRespuesta();
		}
	}

	@Override
	@Async
	@Scheduled(fixedRateString = "${es.caib.emiserv.tasca.backoffice.async.processar.pendents:10000}")
	public void peticioBackofficeAsyncProcessarPendents() {
		log.debug("Processant les peticions asíncrones als backoffices pendents");
		// Obté els codis dels servei amb el tipus de processament asíncron "Processar com síncrones"
		List<ServeiEntity> serveisPerConsultar = serveiRepository.findByBackofficeCaibAsyncTipus(
				BackofficeAsyncTipusEnumDto.PROCESSAR_SINCRONES);
		List<String> codisServeisPerConsultar = new ArrayList<String>();
		for (ServeiEntity servei: serveisPerConsultar) {
			codisServeisPerConsultar.add(servei.getCodi());
		}
		// Obté els ids dels serveis SCSP que corresponen als codis obtinguts
		if(codisServeisPerConsultar.size() > 0) {
			List<ScspCoreServicioEntity> servicios = scspCoreServicioRepository.findByCodigoCertificadoIn(codisServeisPerConsultar);
			List<Long> idsServicios = new ArrayList<Long>();
			for (ScspCoreServicioEntity servicio: servicios) {
				idsServicios.add(servicio.getId());
			}
			// Es cerquen les peticions que compleixin:
			//		En estat pendent
			//		Que siguin de tipus asíncron
			//		Que no estiguin en estat SCSP d'error (estat ha de començar amb "00")
			//		Que pertanyin a serveis amb el tipus de processament asíncron "Processar com síncrones"
			List<BackofficePeticioEntity> peticions = backofficePeticioRepository.findAsincronesPendentsDeProcessar(
					idsServicios);
			log.debug("S'han trobat " + peticions.size() + " peticions asíncrones pendents");
			for (BackofficePeticioEntity peticio: peticions) {
				backofficeHelper.processarPeticioPendent(peticio.getId());
			}
		}
	}

	@Override
	public List<String> getBackofficeClasses() {

		List<String> resposta = new ArrayList<>();
		try {
			ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
			provider.addIncludeFilter(new AbstractClassTestingTypeFilter() {
				@Override
				protected boolean match(ClassMetadata metadata) {
					for (String interfaceName : metadata.getInterfaceNames()) {
						if ("es.scsp.common.backoffice.BackOffice".equals(interfaceName))
							return true;
					}
					return false;
				}
			});
			provider.setResourceLoader(new PathMatchingResourcePatternResolver(backofficeClassloader));
			Set<BeanDefinition> components = provider.findCandidateComponents("es/caib/emiserv/backoffice");
			for (BeanDefinition component : components) {
				resposta.add(component.getBeanClassName());
			}
		} catch (Exception e) {
			log.error("No ha estat possible carregar les classes de backoffice", e);
		}
		return resposta;
	}

	private String toEstatScsp(PeticioEstatEnumDto estat) {
		if (PeticioEstatEnumDto.PENDENT.equals(estat))
			return "0001";
		if (PeticioEstatEnumDto.EN_PROCES.equals(estat))
			return "0002";
		if (PeticioEstatEnumDto.TRAMITADA.equals(estat))
			return "0003";
		if (PeticioEstatEnumDto.POLLING.equals(estat))
			return "0004";
		return null;
	}

	private AuditoriaPeticioDto toAuditoriaPeticioDto(
			ScspCorePeticionRespuestaEntity peticionRespuesta) {
		ScspCoreServicioEntity coreServicio = backofficeHelper.findCoreServicioPerPeticionRespuesta(peticionRespuesta);
		AuditoriaPeticioDto peticio = new AuditoriaPeticioDto();
		peticio.setPeticioId(peticionRespuesta.getPeticionId());
		peticio.setServeiCodi(coreServicio.getCodigoCertificado());
		peticio.setServeiDescripcio(coreServicio.getDescripcion());
		peticio.setDataPeticio(peticionRespuesta.getFechaPeticion());
		peticio.setDataResposta(peticionRespuesta.getFechaRespuesta());
		peticio.setTer(peticionRespuesta.getFechaEstimadaRespuesta());
		peticio.setDataDarreraComprovacio(peticionRespuesta.getFechaUltimoSondeo());
		peticio.setSincrona(peticionRespuesta.isSincrona());
		peticio.setNumEnviaments(peticionRespuesta.getNumEnvios());
		peticio.setNumTransmissions(peticionRespuesta.getNumTransmisiones());
		if (peticionRespuesta.getEstado() != null) {
			peticio.setEstatScsp(peticionRespuesta.getEstado());
			if (peticionRespuesta.getEstado().startsWith("00")) {
				if (peticionRespuesta.getEstado().equals("0001"))
					peticio.setEstat(PeticioEstatEnumDto.PENDENT);
				else if (peticionRespuesta.getEstado().equals("0002"))
					peticio.setEstat(PeticioEstatEnumDto.EN_PROCES);
				else if (peticionRespuesta.getEstado().equals("0003"))
					peticio.setEstat(PeticioEstatEnumDto.TRAMITADA);
				else if (peticionRespuesta.getEstado().equals("0004"))
					peticio.setEstat(PeticioEstatEnumDto.POLLING);
			} else {
				peticio.setEstat(PeticioEstatEnumDto.ERROR);
			}
		}
		peticio.setError(peticionRespuesta.getError());
		BackofficePeticioEntity backofficePeticio = backofficePeticioRepository.findByScspPeticionRespuesta(peticionRespuesta);
		if (backofficePeticio != null) {
			// Si l'estat SCSP és error no te sentit que revisem l'estat de la
			// petició a EMISERV.
			if (!PeticioEstatEnumDto.ERROR.equals(peticio.getEstat())) {
				peticio.setEstat(backofficePeticio.getEstat());
			}
			peticio.setProcessadesTotal(backofficePeticio.getProcessadesTotal());
			peticio.setProcessadesError(backofficePeticio.getProcessadesError());
			if (backofficePeticio.getComunicacioDarrera() != null) {
				peticio.setComunicacioBackofficeDisponible(true);
				peticio.setComunicacioBackofficeError(
						backofficePeticio.getComunicacioDarrera().getError() != null);
			}
		}
		List<ScspCoreTransmisionEntity> transmissions = scspCoreTransmisionRepository.findByPeticionIdOrderBySolicitudIdAsc(peticionRespuesta.getPeticionId());
		peticio.setProcedimentCodi(transmissions.stream().map(t -> t.getProcedimientoCodigo()).distinct().collect(Collectors.joining(", ")));
		peticio.setProcedimentNom(transmissions.stream().map(t -> t.getProcedimientoNombre()).distinct().collect(Collectors.joining(", ")));
		return peticio;
	}

/*	private void copiarDatosEspecificosPeticion(
			Peticion peticionOrigen,
			es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion peticionDesti) {
		// Si no es copies d'aquesta forma els DatosEspecificos copiats amb orika
		// queden com a Objects en lloc de org.w3c.dom.Elements i després sorgeixen
		// errors al processar-los.
		if (peticionOrigen.getSolicitudes() != null && peticionOrigen.getSolicitudes().getSolicitudTransmision() != null) {
			List<SolicitudTransmision> solicitudesOrigen = peticionOrigen.getSolicitudes().getSolicitudTransmision();
			List<es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudTransmision> solicitudesDesti = peticionDesti.getSolicitudes().getSolicitudTransmision();
			for (int i = 0; i < solicitudesOrigen.size(); i++) {
				solicitudesDesti.get(i).setDatosEspecificos(
						solicitudesOrigen.get(i).getDatosEspecificos());
			}
		}
	}

	private void copiarDatosEspecificosRespuestaa(
			es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta respuestaOrigen,
			Respuesta respuesta) {
		// Si no es copies d'aquesta forma els DatosEspecificos copiats amb orika
		// queden com a Objects en lloc de org.w3c.dom.Elements i després sorgeixen
		// errors al processar-los.
		if (respuestaOrigen.getTransmisiones() != null && respuestaOrigen.getTransmisiones().getTransmisionDatos() != null) {
			ArrayList<es.caib.emiserv.logic.intf.service.ws.backoffice.TransmisionDatos> transmisionesOrigen = respuestaOrigen.getTransmisiones().getTransmisionDatos();
			List<es.scsp.bean.common.TransmisionDatos> transmisionesDesti = respuesta.getTransmisiones().getTransmisionDatos();
			for (int i = 0; i < transmisionesOrigen.size(); i++) {
				transmisionesDesti.get(i).setDatosEspecificos(
						transmisionesOrigen.get(i).getDatosEspecificos());
			}
		}
	}*/

}
