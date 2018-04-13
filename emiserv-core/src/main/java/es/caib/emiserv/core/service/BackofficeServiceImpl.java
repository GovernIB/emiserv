/**
 * 
 */
package es.caib.emiserv.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.emiserv.core.api.dto.AuditoriaFiltreDto;
import es.caib.emiserv.core.api.dto.AuditoriaPeticioDto;
import es.caib.emiserv.core.api.dto.AuditoriaTransmisionDto;
import es.caib.emiserv.core.api.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.core.api.dto.PaginaDto;
import es.caib.emiserv.core.api.dto.PaginacioParamsDto;
import es.caib.emiserv.core.api.dto.PeticioEstatEnumDto;
import es.caib.emiserv.core.api.dto.ServeiConfigScspDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.exception.BackofficeException;
import es.caib.emiserv.core.api.exception.NotActiveException;
import es.caib.emiserv.core.api.exception.NotFoundException;
import es.caib.emiserv.core.api.exception.PermissionDeniedException;
import es.caib.emiserv.core.api.service.BackofficeService;
import es.caib.emiserv.core.api.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.core.api.service.ws.backoffice.Peticion;
import es.caib.emiserv.core.api.service.ws.backoffice.Respuesta;
import es.caib.emiserv.core.api.service.ws.backoffice.SolicitudRespuesta;
import es.caib.emiserv.core.entity.BackofficePeticioEntity;
import es.caib.emiserv.core.entity.BackofficeSolicitudEntity;
import es.caib.emiserv.core.entity.ScspCorePeticionRespuestaEntity;
import es.caib.emiserv.core.entity.ScspCoreServicioEntity;
import es.caib.emiserv.core.entity.ScspCoreTokenDataEntity;
import es.caib.emiserv.core.entity.ServeiEntity;
import es.caib.emiserv.core.helper.BackofficeHelper;
import es.caib.emiserv.core.helper.BackofficeHelper.ConfirmacionPeticionAmbException;
import es.caib.emiserv.core.helper.BackofficeHelper.RespuestaAmbException;
import es.caib.emiserv.core.helper.ConversioTipusHelper;
import es.caib.emiserv.core.helper.PaginacioHelper;
import es.caib.emiserv.core.helper.PaginacioHelper.Converter;
import es.caib.emiserv.core.helper.PermisosHelper;
import es.caib.emiserv.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.emiserv.core.helper.SecurityHelper;
import es.caib.emiserv.core.repository.BackofficeComunicacioRepository;
import es.caib.emiserv.core.repository.BackofficePeticioRepository;
import es.caib.emiserv.core.repository.BackofficeSolicitudRepository;
import es.caib.emiserv.core.repository.ScspCorePeticionRespuestaRepository;
import es.caib.emiserv.core.repository.ScspCoreServicioRepository;
import es.caib.emiserv.core.repository.ScspCoreTokenDataRepository;
import es.caib.emiserv.core.repository.ScspCoreTransmisionRepository;
import es.caib.emiserv.core.repository.ServeiRepository;
import es.caib.emiserv.core.security.ExtendedPermission;

/**
 * Implementació del servei de backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class BackofficeServiceImpl implements BackofficeService {

	@Resource
	private ServeiRepository serveiRepository;
	@Resource
	private ScspCoreServicioRepository scspCoreServicioRepository;
	@Resource
	private ScspCoreTransmisionRepository scspCoreTransmisionRepository;
	@Resource
	private ScspCoreTokenDataRepository scspCoreTokenDataRepository;
	@Resource
	private ScspCorePeticionRespuestaRepository scspCorePeticionRespuestaRepository;
	@Resource
	private BackofficePeticioRepository backofficePeticioRepository;
	@Resource
	private BackofficeSolicitudRepository backofficeSolicitudRepository;
	@Resource
	private BackofficeComunicacioRepository backofficeComunicacioRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private SecurityHelper securityHelper;
	@Resource
	private BackofficeHelper backofficeHelper;



	@Transactional(readOnly = true)
	@Override
	public ServeiDto serveiFindByCodi(String codi) {
		logger.debug(
				"Consulta de servei amb codi pel backoffice (" +
				"codi=" + codi + ")");
		ServeiEntity servei = serveiRepository.findByCodi(codi);
		if (servei == null) {
			throw new NotFoundException(
					codi,
					ServeiEntity.class);
		}
		if (!servei.isActiu()) {
			throw new NotActiveException(
					codi,
					ServeiEntity.class);
		}
		return conversioTipusHelper.convertir(
				servei,
				ServeiDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ServeiConfigScspDto serveiFindConfiguracioScsp(
			String codi) {
		logger.debug("Obté la configuració SCSP del servei per a un backoffice (" +
				"codi=" + codi + ")");
		ServeiEntity servei = serveiRepository.findByCodi(codi);
		if (servei == null) {
			throw new NotFoundException(
					codi,
					ServeiEntity.class);
		}
		if (!servei.isActiu()) {
			throw new NotActiveException(
					codi,
					ServeiEntity.class);
		}
		ScspCoreServicioEntity scspCoreServicio = scspCoreServicioRepository.findByCodigoCertificado(
				servei.getCodi());
		return conversioTipusHelper.convertir(
				scspCoreServicio,
				ServeiConfigScspDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<AuditoriaPeticioDto> peticioFindByFiltrePaginat(
			AuditoriaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta amb filtre de peticions per a les auditories ("
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
					new Permission[] {ExtendedPermission.ADMINISTRATION},
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
		PaginaDto<AuditoriaPeticioDto> resposta = paginacioHelper.toPaginaDto(
				scspCorePeticionRespuestaRepository.findByFiltrePaginat(
						filtre.getProcediment() == null || filtre.getProcediment().isEmpty(),
						filtre.getProcediment(),
						filtre.getServei() == null || filtre.getServei().isEmpty(),
						filtre.getServei(),
						filtre.getEstat() == null,
						PeticioEstatEnumDto.ERROR.equals(filtre.getEstat()),
						toEstatScsp(filtre.getEstat()),
						filtre.getDataInici() == null,
						filtre.getDataInici(),
						filtre.getDataFi() == null,
						filtre.getDataFi(),
						nomesServeisPermesos,
						codisServeisPermesos,
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
		logger.debug("Consulta dels detalls de la petició ("
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
			boolean tePermisos = permisosHelper.isGrantedAny(
					servei.getId(),
					ServeiEntity.class,
					new Permission[] {ExtendedPermission.ADMINISTRATION},
					auth);
			if (!tePermisos) {
				throw new PermissionDeniedException(
						servei.getId(),
						ServeiEntity.class,
						auth.getName(),
						ExtendedPermission.ADMINISTRATION.toString());
			}
		}
		return toAuditoriaPeticioDto(peticionRespuesta);
	}

	@Transactional(readOnly = true)
	@Override
	public List<AuditoriaTransmisionDto> solicitudFindByPeticio(
			String peticioId) {
		logger.debug("Consulta de les sol·licituds associades a una petició ("
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
		logger.debug("Consulta una sol·licitud donat el seu id (" +
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
		logger.debug("Consulta del missatge XML de petició SCSP per a la petició (" +
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
		logger.debug("Consulta del missatge XML de resposta SCSP per a la petició (" +
				"peticioId=" + peticioId + ")");
		ScspCorePeticionRespuestaEntity peticionRespuesta = scspCorePeticionRespuestaRepository.findOne(
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
		logger.debug("Consulta del missatge XML de petició al backoffice per a la petició (" +
				"peticioId=" + peticioId + ")");
		ScspCorePeticionRespuestaEntity peticionRespuesta = scspCorePeticionRespuestaRepository.findOne(
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
		logger.debug("Consulta del missatge XML de resposta del backoffice per a la petició (" +
				"peticioId=" + peticioId + ")");
		ScspCorePeticionRespuestaEntity peticionRespuesta = scspCorePeticionRespuestaRepository.findOne(
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
		logger.debug("Consulta de l'error en la comunicació amb el backoffice per a la petició (" +
				"peticioId=" + peticioId + ")");
		ScspCorePeticionRespuestaEntity peticionRespuesta = scspCorePeticionRespuestaRepository.findOne(
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
		logger.debug("Consulta del missatge XML de resposta del backoffice per a la sol·licitud (" +
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
		logger.debug("Consulta del missatge XML de resposta del backoffice per a la sol·licitud (" +
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
		logger.debug("Consulta de l'error en la comunicació amb el backoffice per a la sol·licitud (" +
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
		RespuestaAmbException respuestaAmbException = backofficeHelper.peticioSincrona(peticion);
		if (respuestaAmbException.getException() != null) {
			throw new BackofficeException(
					"Error processant petició síncrona: " +  ExceptionUtils.getRootCauseMessage(respuestaAmbException.getException()),
					respuestaAmbException.getException());
		} else {
			return respuestaAmbException.getRespuesta();
		}
	}

	@Override
	public ConfirmacionPeticion peticioBackofficeAsincrona(
			Peticion peticion) {
		ConfirmacionPeticionAmbException confirmacionPeticionAmbException = backofficeHelper.peticioAsincrona(peticion);
		if (confirmacionPeticionAmbException.getException() != null) {
			throw new BackofficeException(
					"Error processant petició asíncrona: " +  ExceptionUtils.getRootCauseMessage(confirmacionPeticionAmbException.getException()),
					confirmacionPeticionAmbException.getException());
		} else {
			return confirmacionPeticionAmbException.getConfirmacionPeticion();
		}
	}

	@Override
	public Respuesta peticioBackofficeSolicitudRespuesta(
			SolicitudRespuesta solicitudRespuesta) {
		RespuestaAmbException respuestaAmbException = backofficeHelper.solicitudResposta(solicitudRespuesta);
		if (respuestaAmbException.getException() != null) {
			throw new BackofficeException(
					"Error processant sol·licitud de resposta: " +  ExceptionUtils.getRootCauseMessage(respuestaAmbException.getException()),
					respuestaAmbException.getException());
		} else {
			return respuestaAmbException.getRespuesta();
		}
	}

	@Override
	@Async
	@Scheduled(fixedRateString = "${config:es.caib.emiserv.tasca.backoffice.async.processar.pendents}")
	public void peticioBackofficeAsyncProcessarPendents() {
		logger.debug("Processant les peticions asíncrones als backoffices pendents");
		// Obté els codis dels servei amb el tipus de processament asíncron "Processar com síncrones"
		List<ServeiEntity> serveisPerConsultar = serveiRepository.findByBackofficeCaibAsyncTipus(
				BackofficeAsyncTipusEnumDto.PROCESSAR_SINCRONES);
		List<String> codisServeisPerConsultar = new ArrayList<String>();
		for (ServeiEntity servei: serveisPerConsultar) {
			codisServeisPerConsultar.add(servei.getCodi());
		}
		// Obté els ids dels serveis SCSP que corresponen als codis obtinguts
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
		logger.debug("S'han trobat " + peticions.size() + " peticions asíncrones pendents");
		for (BackofficePeticioEntity peticio: peticions) {
			backofficeHelper.processarPeticioPendent(peticio.getId());
		}
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
		return peticio;
	}

	private static final Logger logger = LoggerFactory.getLogger(BackofficeServiceImpl.class);

}
