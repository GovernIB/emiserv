/**
 * 
 */
package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.helper.PaginacioHelper;
import es.caib.emiserv.logic.helper.PermisosHelper;
import es.caib.emiserv.logic.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.emiserv.logic.helper.SecurityHelper;
import es.caib.emiserv.logic.helper.ServeiXsdHelper;
import es.caib.emiserv.logic.intf.dto.*;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.PermissionDeniedException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.service.ServeiService;
import es.caib.emiserv.logic.resolver.EntitatResolver;
import es.caib.emiserv.logic.resolver.ResponseResolver;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmBackofficeEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.repository.RedireccioPeticioRepository;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.ServeiRutaDestiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreEmBackofficeRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreEmisorCertificadoRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCorePeticionRespuestaRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreTransmisionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementació del servei de gestió de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class ServeiServiceImpl implements ServeiService {

	//private static final String RESOLVER_PACKAGE_NAME = "es/caib/emiserv/logic/resolver";
	private static final int DEFAULT_BACKOFFICE_TER = 1;

	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	@Autowired
	private ScspCoreEmBackofficeRepository scspEmisorBackofficeRepository;
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
		log.debug("Creant un nou servei (servei=" + servei + ")");
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
		log.debug("Modificant el servei (" +
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
		log.debug("Actualitzant propietat activa d'un servei existent ("
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
		log.debug("Esborrant servei existent (id=" + id +  ")");
		ServeiEntity servei = comprovarServei(id);
		ScspCoreServicioEntity scspCoreServicio = scspCoreServicioRepository.findByCodigoCertificado(
				servei.getCodi());
		if (scspCoreServicio != null) {
			if (scspCorePeticionRespuestaRepository.countByCertificado(scspCoreServicio.getId()) > 0) {
				throw new ValidationException("Aquest servei te peticions de backoffice associades i no es pot esborrar");
			}
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
			scspEmisorBackofficeRepository.deleteById(
					coreServicioId);
		}
		return conversioTipusHelper.convertir(
				servei,
				ServeiDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ServeiDto findById(Long id) {
		log.debug("Consulta de servei amb id (id=" + id + ")");
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
		log.debug("Consulta de servei amb codi (codi=" + codi + ")");
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
	public PaginaDto<ServeiDto> findAllPaginat(ServeiFiltreDto filtre, PaginacioParamsDto paginacioParams) {
		log.debug("Consulta de tots els serveis paginats (" +
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
					new Permission[] {BasePermission.ADMINISTRATION},
					auth);
		}
		if (!serveisPermesos.isEmpty()) {
			PaginaDto<ServeiDto> resposta;
			if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
				resposta = paginacioHelper.toPaginaDto(
						serveiRepository.findPermesosPaginat(
								serveisPermesos,
								(filtre == null || Strings.isBlank(filtre.getCodi())) ,
								filtre != null && filtre.getCodi() != null ? filtre.getCodi() : "",
								(filtre == null || Strings.isBlank(filtre.getNom())),
								filtre != null && filtre.getNom() != null ? filtre.getNom() : "",
								(filtre == null || filtre.getTipus() == null),
								filtre != null ? filtre.getTipus() : null,
								(filtre == null || filtre.getActiu() == null),
								filtre != null ? filtre.getActiu() : null,
								paginacioHelper.toSpringDataPageable(paginacioParams)),
						ServeiDto.class);
			} else {
				resposta = paginacioHelper.toPaginaDto(
						serveiRepository.findPermesosOrdenat(
								serveisPermesos,
								(filtre == null || Strings.isBlank(filtre.getCodi())) ,
								filtre != null && filtre.getCodi() != null ? filtre.getCodi() : "",
								(filtre == null || Strings.isBlank(filtre.getNom())),
								filtre != null && filtre.getNom() != null ? filtre.getNom() : "",
								(filtre == null || filtre.getTipus() == null),
								filtre != null ? filtre.getTipus() : null,
								(filtre == null || filtre.getActiu() == null),
								filtre != null ? filtre.getActiu() : null,
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
		log.debug("Obté la llista de classes dels resolvers");
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AbstractClassTestingTypeFilter() {
			@Override
			protected boolean match(ClassMetadata metadata) {
				for (String interfaceName: metadata.getInterfaceNames()) {
					if (EntitatResolver.class.getName().equals(interfaceName))
					return true;
				}
				return false;
			}
		});
		Set<BeanDefinition> components = provider.findCandidateComponents(
				EntitatResolver.class.getPackage().getName().replace('.', '/'));
		List<String> resposta = new ArrayList<String>();
		for (BeanDefinition component: components) {
			resposta.add(component.getBeanClassName());
		}
		return resposta;
	}

	@Override
	public List<String> responseResolverClassesFindAll() {
		log.debug("Obté la llista de classes per discriminar respostes");
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AbstractClassTestingTypeFilter() {
			@Override
			protected boolean match(ClassMetadata metadata) {
				for (String interfaceName: metadata.getInterfaceNames()) {
					if (ResponseResolver.class.getName().equals(interfaceName))
					return true;
				}
				return false;
			}
		});
		Set<BeanDefinition> components = provider.findCandidateComponents(
				ResponseResolver.class.getPackage().getName().replace('.', '/'));
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
		log.debug("Obté la configuració SCSP del servei (" +
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
		log.debug("Actualitza la configuració SCSP del servei (" +
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
				scspCoreEmisorCertificadoRepository.getOne(configuracio.getEmisorId()),
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
		log.debug("Crea una ruta per a un servei (" +
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
		log.debug("Modifica una ruta d'un servei (" +
				"id=" + id + ", " +
				"rutaDesti=" + rutaDesti + ")");
		ServeiEntity servei = comprovarServei(id);
		Optional<ServeiRutaDestiEntity> entity = serveiRutaDestiRepository.findById(
				rutaDesti.getId());
		if (!entity.isPresent() || !entity.get().getServei().equals(servei)) {
			throw new NotFoundException(
					rutaDesti.getId(),
					ServeiRutaDestiEntity.class);
		} else {
			entity.get().update(
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
		log.debug("Esborra una ruta d'un servei (" +
				"id=" + id + ", " +
				"rutaDestiId=" + rutaDestiId + ")");
		ServeiEntity servei = comprovarServei(id);
		Optional<ServeiRutaDestiEntity> entity = serveiRutaDestiRepository.findById(rutaDestiId);
		if (!entity.isPresent() || !entity.get().getServei().equals(servei)) {
			throw new NotFoundException(
					rutaDestiId,
					ServeiRutaDestiEntity.class);
		} else {
			serveiRutaDestiRepository.delete(entity.get());
		}
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ServeiRutaDestiDto> rutaDestiFindByServei(
			Long id,
			PaginacioParamsDto paginacioParams) {
		log.debug("Consulta les rutes d'un servei (" +
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
		log.debug("Mou la posició d'una ruta (" +
				"rutaId=" + rutaId + ", " +
				"posicio=" + posicio + ")");
		boolean ret = false;
		Optional<ServeiRutaDestiEntity> ruta = serveiRutaDestiRepository.findById(rutaId);
		if (ruta.isPresent()) {
			List<ServeiRutaDestiEntity> rutesDestins = serveiRutaDestiRepository.findByServeiOrderByOrdreAsc(
					ruta.get().getServei());
			int index = -1;
			for (int i = 0; i < rutesDestins.size(); i++) {
				ServeiRutaDestiEntity rutaDesti = rutesDestins.get(i);
				if (ruta.get().getId().equals(rutaDesti.getId())) {
					index = i;
					break;
				}
			}
			if (posicio != index) {	
				ServeiRutaDestiEntity r = rutesDestins.get(index);
				rutesDestins.remove(r);
				rutesDestins.add(posicio, r);
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
		log.debug("Crea un fitxer XSD per a un servei (" +
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
		log.debug("Modifica un fitxer XSD per a un servei (" +
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
		log.debug("Esborra un fitxer XSD per a un servei (" +
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
		log.debug("Descarrega un fitxer XSD per a un servei (" +
				"id=" + id + ", " +
				"tipus=" + tipus + ")");
		ServeiEntity servei = comprovarServei(id);
		return serveiXsdHelper.descarregarXsd(servei, tipus);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiXsdDto> xsdFindByServei(
			Long id) {
		log.debug("Obtenint tots els fitxers XSD per a un servei (" +
				"id=" + id + ")");
		ServeiEntity servei = comprovarServei(id);
		return serveiXsdHelper.findAll(servei);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ProcedimentDto> procedimentFindAll() {
		log.debug("Consulta la llista de tots els procediments disponibles");
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
					new Permission[] {BasePermission.ADMINISTRATION},
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
		log.debug("Consulta dels permisos del servei (id=" + id + ")");
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
		log.debug("Modificació del permis del servei ("
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
		log.debug("Eliminació del permis del servei ("
				+ "id=" + id + ", "
				+ "permisId=" + permisId + ")");
		comprovarServei(id);
		permisosHelper.deletePermis(
				id,
				ServeiEntity.class,
				permisId);
	}


	private ServeiEntity comprovarServei(
			Long id) throws NotFoundException {
		Optional<ServeiEntity> servei = serveiRepository.findById(id);
		if (!servei.isPresent()) {
			throw new NotFoundException(
					id,
					ServeiEntity.class);
		}
		if (!securityHelper.hasRole("EMS_ADMIN")) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			boolean esAdministradorServei = permisosHelper.isGrantedAll(
					id,
					ServeiEntity.class,
					new Permission[] {BasePermission.ADMINISTRATION},
					auth);
			if (!esAdministradorServei) {
				throw new PermissionDeniedException(
						id,
						ServeiEntity.class,
						auth.getName(),
						"ADMINISTRATION");
			}
		}
		return servei.get();
	}

	private ScspCoreEmBackofficeEntity comprovarScspBackofficeCreat(
			ServeiEntity servei) {
		Long coreServicioId = findCoreServicioIdPerServeiCodi(servei.getCodi());
		Optional<ScspCoreEmBackofficeEntity> scspEmisorBackoffice = scspEmisorBackofficeRepository.findById(
				coreServicioId);
		if (!scspEmisorBackoffice.isPresent()) {
			return scspEmisorBackofficeRepository.save(
					ScspCoreEmBackofficeEntity.getBuilder(
							coreServicioId,
							servei.getBackofficeClass(),
							Integer.valueOf(DEFAULT_BACKOFFICE_TER)).
					build());
		} else {
			return scspEmisorBackoffice.get();
		}
	}
	private void actualitzarBackofficeSiConfigurat(
			ServeiEntity servei) {
		if (servei.isConfigurat()) {
			Long coreServicioId = findCoreServicioIdPerServeiCodi(servei.getCodi());
			ScspCoreEmBackofficeEntity scspEmisorBackoffice = scspEmisorBackofficeRepository.getOne(
					coreServicioId);
			scspEmisorBackoffice.update(
					servei.getBackofficeClass(),
					Integer.valueOf(DEFAULT_BACKOFFICE_TER));
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
		return Long.valueOf(scspCoreServicio.getId());
	}

}
