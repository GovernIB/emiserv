/**
 * 
 */
package es.caib.emiserv.core.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.emiserv.core.api.dto.FitxerDto;
import es.caib.emiserv.core.api.dto.InformeGeneralEstatDto;
import es.caib.emiserv.core.api.dto.PaginaDto;
import es.caib.emiserv.core.api.dto.PaginacioParamsDto;
import es.caib.emiserv.core.api.dto.PermisDto;
import es.caib.emiserv.core.api.dto.ProcedimentDto;
import es.caib.emiserv.core.api.dto.ServeiConfigScspDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.dto.ServeiRutaDestiDto;
import es.caib.emiserv.core.api.dto.ServeiTipusEnumDto;
import es.caib.emiserv.core.api.dto.ServeiXsdDto;
import es.caib.emiserv.core.api.dto.XsdTipusEnumDto;
import es.caib.emiserv.core.api.exception.NotFoundException;
import es.caib.emiserv.core.api.exception.PermissionDeniedException;
import es.caib.emiserv.core.api.exception.ValidationException;
import es.caib.emiserv.core.api.service.ServeiService;
import es.caib.emiserv.core.entity.ScspCoreServicioEntity;
import es.caib.emiserv.core.entity.ScspEmisorBackofficeEntity;
import es.caib.emiserv.core.entity.ServeiEntity;
import es.caib.emiserv.core.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.core.helper.ConversioTipusHelper;
import es.caib.emiserv.core.helper.PaginacioHelper;
import es.caib.emiserv.core.helper.PermisosHelper;
import es.caib.emiserv.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.emiserv.core.helper.SecurityHelper;
import es.caib.emiserv.core.helper.ServeiXsdHelper;
import es.caib.emiserv.core.repository.RedireccioPeticioRepository;
import es.caib.emiserv.core.repository.ScspCoreEmisorCertificadoRepository;
import es.caib.emiserv.core.repository.ScspCorePeticionRespuestaRepository;
import es.caib.emiserv.core.repository.ScspCoreServicioRepository;
import es.caib.emiserv.core.repository.ScspCoreTransmisionRepository;
import es.caib.emiserv.core.repository.ScspEmisorBackofficeRepository;
import es.caib.emiserv.core.repository.ServeiRepository;
import es.caib.emiserv.core.repository.ServeiRutaDestiRepository;
import es.caib.emiserv.core.security.ExtendedPermission;

/**
 * Implementació del servei de gestió de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ServeiServiceImpl implements ServeiService {

	private static final int DEFAULT_BACKOFFICE_TER = 1;

	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	@Autowired
	private ScspEmisorBackofficeRepository scspEmisorBackofficeRepository;
	@Autowired
	private ScspCoreServicioRepository scspCoreServicioRepository;
	@Autowired
	private ScspCoreTransmisionRepository scspCoreTransmisionRepository;
	@Autowired
	private ScspCorePeticionRespuestaRepository scspCorePeticionRespuestaRepository;
	@Autowired
	private ScspCoreEmisorCertificadoRepository scspCoreEmisorCertificadoRepository;
	@Autowired
	private RedireccioPeticioRepository redireccioPeticioRepository;

	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private SecurityHelper securityHelper;
	@Autowired
	private ServeiXsdHelper serveiXsdHelper;



	@Transactional
	@Override
	public ServeiDto create(ServeiDto servei) {
		logger.debug("Creant un nou servei (servei=" + servei + ")");
		ServeiEntity entity;
		if (ServeiTipusEnumDto.BACKOFFICE.equals(servei.getTipus())) {
			entity = ServeiEntity.getBuilder(
					servei.getCodi(),
					servei.getNom(),
					servei.getTipus()).
					descripcio(servei.getDescripcio()).
					backofficeClass(servei.getBackofficeClass()).
					backofficeCaibUrl(servei.getBackofficeCaibUrl()).
					backofficeCaibAutenticacio(servei.getBackofficeCaibAutenticacio()).
					backofficeCaibAsyncTipus(servei.getBackofficeCaibAsyncTipus()).
					backofficeCaibAsyncTer(servei.getBackofficeCaibAsyncTer()).
					resolverClass(servei.getResolverClass()).
					urlPerDefecte(servei.getUrlPerDefecte()).
					responseResolverClass(servei.getResponseResolverClass()).
					build();
		} else {
			entity = ServeiEntity.getBuilder(
					servei.getCodi(),
					servei.getNom(),
					servei.getTipus()).
					descripcio(servei.getDescripcio()).
					resolverClass(servei.getResolverClass()).
					build();
		}
		return conversioTipusHelper.convertir(
				serveiRepository.save(entity),
				ServeiDto.class);
	}

	@Transactional
	@Override
	public ServeiDto update(
			ServeiDto servei) {
		logger.debug("Modificant el servei (" +
				"servei=" + servei + ")");
		ServeiEntity entity = comprovarServei(
				servei.getId());
		if (ServeiTipusEnumDto.BACKOFFICE.equals(servei.getTipus())) {
			entity.updateBackoffice(
					servei.getNom(),
					servei.getDescripcio(),
					servei.getBackofficeClass(),
					servei.getBackofficeCaibUrl(),
					servei.getBackofficeCaibAsyncTipus(),
					servei.getBackofficeCaibAsyncTer(),
					servei.getBackofficeCaibAutenticacio());
			actualitzarBackofficeSiConfigurat(entity);
		} else if(ServeiTipusEnumDto.ENRUTADOR.equals(servei.getTipus())){
			entity.updateEnrutador(
					servei.getNom(),
					servei.getDescripcio(),
					servei.getResolverClass(),
					servei.getUrlPerDefecte());
		} else {
			entity.updateEnrutadorMultiple(
					servei.getNom(),
					servei.getDescripcio(),
					servei.getResponseResolverClass());
		}
		return conversioTipusHelper.convertir(
				entity,
				ServeiDto.class);
	}

	@Transactional
	@Override
	public ServeiDto updateActiu(
			Long id,
			boolean actiu) {
		logger.debug("Actualitzant propietat activa d'un servei existent ("
				+ "id=" + id + ", "
				+ "actiu=" + actiu + ")");
		ServeiEntity entity = comprovarServei(
				id);
		entity.updateActiu(actiu);
		return conversioTipusHelper.convertir(
				entity,
				ServeiDto.class);
	}

	@Transactional
	@Override
	public ServeiDto delete(
			Long id) {
		logger.debug("Esborrant servei existent (id=" + id +  ")");
		ServeiEntity servei = comprovarServei(id);
		if (scspCorePeticionRespuestaRepository.countByCertificado(servei.getCodi()) > 0) {
			throw new ValidationException("Aquest servei te peticions de backoffice associades i no es pot esborrar");
		}
		if (redireccioPeticioRepository.countByServeiCodi(servei.getCodi()) > 0) {
			throw new ValidationException("Aquest servei te peticions de tipus enrutador associades i no es pot esborrar");
		}
		serveiRepository.delete(servei);
		permisosHelper.deleteAcl(
				id,
				ServeiEntity.class);
		if (servei.isConfigurat() && ServeiTipusEnumDto.BACKOFFICE.equals(servei.getTipus())) {
			Long coreServicioId = findCoreServicioIdPerServeiCodi(servei.getCodi());
			scspEmisorBackofficeRepository.delete(
					coreServicioId);
		}
		return conversioTipusHelper.convertir(
				servei,
				ServeiDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ServeiDto findById(Long id) {
		logger.debug("Consulta de servei amb id (id=" + id + ")");
		ServeiEntity servei = comprovarServei(
				id);
		ServeiDto dto = conversioTipusHelper.convertir(
				servei,
				ServeiDto.class);
		permisosHelper.omplirPermisos(
				dto,
				new ObjectIdentifierExtractor<ServeiDto>() {
					@Override
					public Serializable getObjectIdentifier(ServeiDto servei) {
						return servei.getId();
					}
				},
				ServeiEntity.class,
				true);
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public ServeiDto findByCodi(String codi) {
		logger.debug("Consulta de servei amb codi (codi=" + codi + ")");
		ServeiDto servei = conversioTipusHelper.convertir(
				serveiRepository.findByCodi(codi),
				ServeiDto.class);
		permisosHelper.omplirPermisos(
				servei,
				new ObjectIdentifierExtractor<ServeiDto>() {
					@Override
					public Serializable getObjectIdentifier(ServeiDto servei) {
						return servei.getId();
					}
				},
				ServeiEntity.class,
				true);
		return servei;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ServeiDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de tots els serveis paginats (" +
				"paginacioParams=" + paginacioParams + ")");
		List<ServeiEntity> serveisPermesos = serveiRepository.findAll();
		if (!securityHelper.hasRole("EMS_ADMIN")) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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
		}
		if (!serveisPermesos.isEmpty()) {
			PaginaDto<ServeiDto> resposta;
			if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
				resposta = paginacioHelper.toPaginaDto(
						serveiRepository.findPermesosPaginat(
								serveisPermesos,
								paginacioHelper.toSpringDataPageable(paginacioParams)),
						ServeiDto.class);
			} else {
				resposta = paginacioHelper.toPaginaDto(
						serveiRepository.findPermesosOrdenat(
								serveisPermesos,
								paginacioHelper.toSpringDataSort(paginacioParams)),
						ServeiDto.class);
			}
			permisosHelper.omplirPermisos(
					resposta.getContingut(),
					new ObjectIdentifierExtractor<ServeiDto>() {
						@Override
						public Serializable getObjectIdentifier(ServeiDto servei) {
							return servei.getId();
						}
					},
					ServeiEntity.class,
					true);
			return resposta;
		} else {
			return paginacioHelper.getPaginaDtoBuida(ServeiDto.class);
		}
	}

	@Override
	public List<String> resolverClassesFindAll() {
		logger.debug("Obté la llista de classes dels resolvers");
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AbstractClassTestingTypeFilter() {
			@Override
			protected boolean match(ClassMetadata metadata) {
				for (String interfaceName: metadata.getInterfaceNames()) {
					if ("es.caib.emiserv.core.resolver.EntitatResolver".equals(interfaceName))
					return true;
				}
				return false;
			}
		});
		Set<BeanDefinition> components = provider.findCandidateComponents("es/caib/emiserv/core/resolver");
		List<String> resposta = new ArrayList<String>();
		for (BeanDefinition component: components) {
			resposta.add(component.getBeanClassName());
		}
		return resposta;
	}

	@Override
	public List<String> responseResolverClassesFindAll() {
		logger.debug("Obté la llista de classes per discriminar respostes");
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AbstractClassTestingTypeFilter() {
			@Override
			protected boolean match(ClassMetadata metadata) {
				for (String interfaceName: metadata.getInterfaceNames()) {
					if ("es.caib.emiserv.core.resolver.ResponseResolver".equals(interfaceName))
					return true;
				}
				return false;
			}
		});
		Set<BeanDefinition> components = provider.findCandidateComponents("es/caib/emiserv/core/resolver");
		List<String> resposta = new ArrayList<String>();
		for (BeanDefinition component: components) {
			resposta.add(component.getBeanClassName());
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public ServeiConfigScspDto configuracioScspFindByServei(
			Long id) throws NotFoundException {
		logger.debug("Obté la configuració SCSP del servei (" +
				"id=" + id + ")");
		ServeiEntity servei = comprovarServei(id);
		ScspCoreServicioEntity scspCoreServicio = scspCoreServicioRepository.findByCodigoCertificado(
				servei.getCodi());
		if (scspCoreServicio == null) {
			throw new NotFoundException(
					servei.getCodi(),
					ScspCoreServicioEntity.class);
		}
		ServeiConfigScspDto dto = conversioTipusHelper.convertir(
				scspCoreServicio,
				ServeiConfigScspDto.class);
		if (scspCoreServicio.getEmisor() != null) {
			dto.setEmisorId(scspCoreServicio.getEmisor().getId());
		}
		return dto;
	}

	@Transactional
	@Override
	public void configuracioScspUpdate(
			Long id,
			ServeiConfigScspDto configuracio) throws NotFoundException {
		logger.debug("Actualitza la configuració SCSP del servei (" +
				"id=" + id + ")");
		ServeiEntity servei = comprovarServei(id);
		servei.updateConfigurat(true);
		ScspCoreServicioEntity scspCoreServicio = scspCoreServicioRepository.findByCodigoCertificado(
				servei.getCodi());
		boolean esCreacio = scspCoreServicio == null;
		if (esCreacio) {
			scspCoreServicio = ScspCoreServicioEntity.getBuilder(
					servei.getCodi()).build();
		}
		String esquemas = configuracio.getEsquemas();
		if (servei.isXsdGestioActiva() != configuracio.isXsdGestioActiva()) {
			if (!servei.isXsdGestioActiva() && configuracio.isXsdGestioActiva()) {
				// S'ha passat de gestió XSD desactivada a activada
				// Fer backup esquemes
				servei.updateXsd(
						configuracio.isXsdGestioActiva(),
						scspCoreServicio.getEsquemas());
				esquemas = serveiXsdHelper.getPathPerServei(servei);
			} else {
				// S'ha passat de gestió XSD activada a desactivada
				// Recuperar backup esquemes
				esquemas = servei.getXsdEsquemaBackup();
				servei.updateXsd(
						configuracio.isXsdGestioActiva(),
						null);
			}
		}
		scspCoreServicio.update(
				servei.getNom(),
				scspCoreEmisorCertificadoRepository.findOne(configuracio.getEmisorId()),
				configuracio.getFechaAlta(),
				configuracio.getFechaBaja(),
				configuracio.getCaducidad(),
				configuracio.getUrlSincrona(),
				configuracio.getUrlAsincrona(),
				configuracio.getActionSincrona(),
				configuracio.getActionAsincrona(),
				configuracio.getActionSolicitud(),
				configuracio.getVersionEsquema(),
				configuracio.getTipoSeguridad(),
				configuracio.getClaveFirma(),
				configuracio.getClaveCifrado(),
				configuracio.getXpathCifradoSincrono(),
				configuracio.getXpathCifradoAsincrono(),
				configuracio.getAlgoritmoCifrado(),
				configuracio.getValidacionFirma(),
				configuracio.getPrefijoPeticion(), 
				esquemas,
				configuracio.getNumeroMaximoReenvios(),
				configuracio.getMaxSolicitudesPeticion(),
				configuracio.getPrefijoIdTransmision(),
				configuracio.getXpathCodigoError(),
				configuracio.getXpathLiteralError(),
				configuracio.getTimeout());
		if (esCreacio) {
			scspCoreServicioRepository.save(scspCoreServicio);
		}
		comprovarScspBackofficeCreat(servei);
	}

	@Transactional
	@Override
	public ServeiRutaDestiDto rutaDestiCreate(
			Long id,
			ServeiRutaDestiDto rutaDesti) {
		logger.debug("Crea una ruta per a un servei (" +
				"id=" + id + ", " +
				"rutaDesti=" + rutaDesti + ")");
		ServeiEntity servei = comprovarServei(id);
		ServeiRutaDestiEntity entity = ServeiRutaDestiEntity.getBuilder(
				servei,
				rutaDesti.getEntitatCodi(),
				rutaDesti.getUrl(),
				serveiRutaDestiRepository.getNextOrdre(servei.getId())).build();
		return conversioTipusHelper.convertir(
				serveiRutaDestiRepository.save(entity),
				ServeiRutaDestiDto.class);
	}

	@Transactional
	@Override
	public ServeiRutaDestiDto rutaDestiUpdate(
			Long id,
			ServeiRutaDestiDto rutaDesti) {
		logger.debug("Modifica una ruta d'un servei (" +
				"id=" + id + ", " +
				"rutaDesti=" + rutaDesti + ")");
		ServeiEntity servei = comprovarServei(id);
		ServeiRutaDestiEntity entity = serveiRutaDestiRepository.findOne(
				rutaDesti.getId());
		if (entity == null || !entity.getServei().equals(servei)) {
			throw new NotFoundException(
					rutaDesti.getId(),
					ServeiRutaDestiEntity.class);
		} else {
			entity.update(
					rutaDesti.getEntitatCodi(),
					rutaDesti.getUrl());
			return conversioTipusHelper.convertir(
					entity,
					ServeiRutaDestiDto.class);
		}
	}

	@Transactional
	@Override
	public void rutaDestiDelete(
			Long id,
			Long rutaDestiId) {
		logger.debug("Esborra una ruta d'un servei (" +
				"id=" + id + ", " +
				"rutaDestiId=" + rutaDestiId + ")");
		ServeiEntity servei = comprovarServei(id);
		ServeiRutaDestiEntity entity = serveiRutaDestiRepository.findOne(
				rutaDestiId);
		if (entity == null || !entity.getServei().equals(servei)) {
			throw new NotFoundException(
					rutaDestiId,
					ServeiRutaDestiEntity.class);
		} else {
			serveiRutaDestiRepository.delete(entity);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ServeiRutaDestiDto> rutaDestiFindByServei(
			Long id,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta les rutes d'un servei (" +
				"id=" + id + ")");
		ServeiEntity servei = comprovarServei(id);
		return paginacioHelper.toPaginaDto(
				serveiRutaDestiRepository.findByServei(
						servei,
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				ServeiRutaDestiDto.class);
	}

	@Override
	@Transactional
	public boolean rutaDestiMourePosicio(
			Long rutaId,
			int posicio) {
		logger.debug("Mou la posició d'una ruta (" +
				"rutaId=" + rutaId + ", " +
				"posicio=" + posicio + ")");
		boolean ret = false;
		ServeiRutaDestiEntity ruta = serveiRutaDestiRepository.findOne(rutaId);
		if (ruta != null) {
			List<ServeiRutaDestiEntity> rutesDestins = serveiRutaDestiRepository.findByServeiOrderByOrdreAsc(
					ruta.getServei());
			int index = rutesDestins.indexOf(ruta);
			if(posicio != index) {	
				ruta = rutesDestins.get(index);
				rutesDestins.remove(ruta);
				rutesDestins.add(posicio, ruta);
				long i = 0;
				for (ServeiRutaDestiEntity c: rutesDestins) {
					c.setOrdre(i);
					serveiRutaDestiRepository.saveAndFlush(c);
					i++;
				}
			}
		}
		return ret;
	}

	@Transactional
	@Override
	public void xsdCreate(
			Long id,
			ServeiXsdDto xsd,
			byte[] contingut) throws IOException {
		logger.debug("Crea un fitxer XSD per a un servei (" +
				"id=" + id + ", " +
				"xsd=" + xsd + ")");
		ServeiEntity servei = comprovarServei(id);
		serveiXsdHelper.modificarXsd(servei, xsd, contingut);
	}

	@Transactional
	@Override
	public void xsdUpdate(
			Long id,
			ServeiXsdDto xsd,
			byte[] contingut) throws IOException {
		logger.debug("Modifica un fitxer XSD per a un servei (" +
				"id=" + id + ", " +
				"xsd=" + xsd + ")");
		ServeiEntity servei = comprovarServei(id);
		serveiXsdHelper.modificarXsd(servei, xsd, contingut);
	}

	@Transactional
	@Override
	public void xsdDelete(
			Long id,
			XsdTipusEnumDto tipus) throws IOException {
		logger.debug("Esborra un fitxer XSD per a un servei (" +
				"id=" + id + ", " +
				"tipus=" + tipus + ")");
		ServeiEntity servei = comprovarServei(id);
		serveiXsdHelper.esborrarXsd(servei, tipus);
	}

	@Transactional
	@Override
	public FitxerDto xsdDescarregarFitxer(
			Long id,
			XsdTipusEnumDto tipus) throws IOException {
		logger.debug("Descarrega un fitxer XSD per a un servei (" +
				"id=" + id + ", " +
				"tipus=" + tipus + ")");
		ServeiEntity servei = comprovarServei(id);
		return serveiXsdHelper.descarregarXsd(servei, tipus);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiXsdDto> xsdFindByServei(
			Long id) {
		logger.debug("Obtenint tots els fitxers XSD per a un servei (" +
				"id=" + id + ")");
		ServeiEntity servei = comprovarServei(id);
		return serveiXsdHelper.findAll(servei);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ProcedimentDto> procedimentFindAll() {
		logger.debug("Consulta la llista de tots els procediments disponibles");
		List<ServeiEntity> serveisPermesos = serveiRepository.findAll();
		if (!securityHelper.hasRole("EMS_ADMIN")) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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
		}
		List<String> codisServeisPermesos = new ArrayList<String>();
		for (ServeiEntity serveiPermes: serveisPermesos) {
			codisServeisPermesos.add(serveiPermes.getCodi());
		}
		if (!codisServeisPermesos.isEmpty()) {
			List<ProcedimentDto> resposta = new ArrayList<ProcedimentDto>();
			List<Object[]> procedimentsInfo = scspCoreTransmisionRepository.findProcedimentDistintPerServeisPermesos(
					codisServeisPermesos);
			for (Object[] procedimentInfo: procedimentsInfo) {
				ProcedimentDto procediment = new ProcedimentDto();
				procediment.setCodi((String)procedimentInfo[0]);
				procediment.setNom((String)procedimentInfo[1]);
				resposta.add(procediment);
			}
			return resposta;
		} else {
			return new ArrayList<ProcedimentDto>();
		}
	}

	@Transactional
	@Override
	public List<PermisDto> permisFindByServei(
			Long id) {
		logger.debug("Consulta dels permisos del servei (id=" + id + ")");
		comprovarServei(id);
		return permisosHelper.findPermisos(
				id,
				ServeiEntity.class);
	}
	@Transactional
	@Override
	public void permisUpdate(
			Long id,
			PermisDto permis) throws NotFoundException {
		logger.debug("Modificació del permis del servei ("
				+ "id=" + id + ", "
				+ "permis=" + permis + ")");
		comprovarServei(id);
		permisosHelper.updatePermis(
				id,
				ServeiEntity.class,
				permis);
	}
	@Transactional
	@Override
	public void permisDelete(
			Long id,
			Long permisId) throws NotFoundException {
		logger.debug("Eliminació del permis del servei ("
				+ "id=" + id + ", "
				+ "permisId=" + permisId + ")");
		comprovarServei(id);
		permisosHelper.deletePermis(
				id,
				ServeiEntity.class,
				permisId);
	}

	@Transactional(readOnly = true)
	@Override
	public List<InformeGeneralEstatDto> informeGeneralEstat(
			Date dataInici, 
			Date dataFi,
			ServeiTipusEnumDto tipusPeticio) {
		
		logger.debug("Consulta de dades per l'informe general d'estat (dataInici=" + dataInici + 
				", dataFi" + dataFi + 
				", tipusPeticio=" + tipusPeticio + ")");

		// Adequa les dates posant el primer dia a 0 i el segon a 0 del dia següent
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataInici);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		dataInici = cal.getTime();
		cal.setTime(dataFi);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		cal.add(Calendar.DATE, 1);
		dataFi = cal.getTime();

		List<InformeGeneralEstatDto> informe = new ArrayList<InformeGeneralEstatDto>();
		
		// Consulta als backoffices
		if (tipusPeticio == null || tipusPeticio.equals(ServeiTipusEnumDto.BACKOFFICE)) {
			List<InformeGeneralEstatDto> informeBackoffices = 
					scspCoreTransmisionRepository.informeGeneralEstat(
							dataInici,
							dataFi);		
			informe.addAll(informeBackoffices);
		}
		
		// Consulta als enrutadors
		if (tipusPeticio == null || !tipusPeticio.equals(ServeiTipusEnumDto.BACKOFFICE)) {
			List<InformeGeneralEstatDto> informeEnrutadors = 
					redireccioPeticioRepository.informeGeneralEstat(
							dataInici,
							dataFi);		
			informe.addAll(informeEnrutadors);
		}
		
		return informe;
	}



	private ServeiEntity comprovarServei(
			Long id) throws NotFoundException {
		ServeiEntity servei = serveiRepository.findOne(id);
		if (servei == null) {
			throw new NotFoundException(
					id,
					ServeiEntity.class);
		}
		if (!securityHelper.hasRole("EMS_ADMIN")) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			boolean esAdministradorServei = permisosHelper.isGrantedAll(
					id,
					ServeiEntity.class,
					new Permission[] {ExtendedPermission.ADMINISTRATION},
					auth);
			if (!esAdministradorServei) {
				throw new PermissionDeniedException(
						id,
						ServeiEntity.class,
						auth.getName(),
						"ADMINISTRATION");
			}
		}
		return servei;
	}

	private ScspEmisorBackofficeEntity comprovarScspBackofficeCreat(
			ServeiEntity servei) {
		Long coreServicioId = findCoreServicioIdPerServeiCodi(servei.getCodi());
		ScspEmisorBackofficeEntity scspEmisorBackoffice = scspEmisorBackofficeRepository.findOne(
				coreServicioId);
		if (scspEmisorBackoffice == null) {
			scspEmisorBackoffice = ScspEmisorBackofficeEntity.getBuilder(
					coreServicioId,
					servei.getBackofficeClass(),
					new Integer(DEFAULT_BACKOFFICE_TER)).build();
			scspEmisorBackofficeRepository.save(scspEmisorBackoffice);
		}
		return scspEmisorBackoffice;
	}
	private void actualitzarBackofficeSiConfigurat(
			ServeiEntity servei) {
		if (servei.isConfigurat()) {
			Long coreServicioId = findCoreServicioIdPerServeiCodi(servei.getCodi());
			ScspEmisorBackofficeEntity scspEmisorBackoffice = scspEmisorBackofficeRepository.findOne(
					coreServicioId);
			scspEmisorBackoffice.update(
					servei.getBackofficeClass(),
					new Integer(DEFAULT_BACKOFFICE_TER));
		}
	}

	private Long findCoreServicioIdPerServeiCodi(
			String serveiCodi) {
		ScspCoreServicioEntity scspCoreServicio = scspCoreServicioRepository.findByCodigoCertificado(
				serveiCodi);
		if (scspCoreServicio == null) {
			throw new NotFoundException(
					serveiCodi,
					ScspCoreServicioEntity.class);
		}
		return new Long(scspCoreServicio.getId());
	}

	private static final Logger logger = LoggerFactory.getLogger(ServeiServiceImpl.class);

}
