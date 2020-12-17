/**
 * 
 */
package es.caib.emiserv.core.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.emiserv.core.api.dto.AplicacioDto;
import es.caib.emiserv.core.api.dto.AutoritatCertificacioDto;
import es.caib.emiserv.core.api.dto.AutoritzacioDto;
import es.caib.emiserv.core.api.dto.AutoritzacioFiltreDto;
import es.caib.emiserv.core.api.dto.ClauPrivadaDto;
import es.caib.emiserv.core.api.dto.ClauPublicaDto;
import es.caib.emiserv.core.api.dto.EmisorDto;
import es.caib.emiserv.core.api.dto.OrganismeCessionariDto;
import es.caib.emiserv.core.api.dto.OrganismeDto;
import es.caib.emiserv.core.api.dto.OrganismeFiltreDto;
import es.caib.emiserv.core.api.dto.PaginaDto;
import es.caib.emiserv.core.api.dto.PaginacioParamsDto;
import es.caib.emiserv.core.api.exception.NotFoundException;
import es.caib.emiserv.core.api.service.ScspService;
import es.caib.emiserv.core.entity.ScspCoreEmAutorizacionAplicacionEntity;
import es.caib.emiserv.core.entity.ScspCoreEmAutorizacionCertificadoEntity;
import es.caib.emiserv.core.entity.ScspCoreEmAutorizacionOrganismoEntity;
import es.caib.emiserv.core.entity.ScspCoreServicioEntity;
import es.caib.emiserv.core.entity.ServeiEntity;
import es.caib.emiserv.core.entity.scsp.ClauPrivadaEntity;
import es.caib.emiserv.core.entity.scsp.ClauPublicaEntity;
import es.caib.emiserv.core.entity.scsp.OrganismeCessionariEntity;
import es.caib.emiserv.core.entity.scsp.ScspCoreEmAutorizacionAutoridadCertEntity;
import es.caib.emiserv.core.helper.ConversioTipusHelper;
import es.caib.emiserv.core.helper.PaginacioHelper;
import es.caib.emiserv.core.helper.PaginacioHelper.Converter;
import es.caib.emiserv.core.repository.ScspCoreEmAutorizacionAplicacionRepository;
import es.caib.emiserv.core.repository.ScspCoreEmAutorizacionCertificadoRepository;
import es.caib.emiserv.core.repository.ScspCoreEmAutorizacionOrganismoRepository;
import es.caib.emiserv.core.repository.ScspCoreServicioRepository;
import es.caib.emiserv.core.repository.ServeiRepository;
import es.caib.emiserv.core.repository.scsp.ClauPrivadaRepository;
import es.caib.emiserv.core.repository.scsp.ClauPublicaRepository;
import es.caib.emiserv.core.repository.scsp.OrganismeCessionariRepository;
import es.caib.emiserv.core.repository.scsp.ScspCoreEmAutorizacionAutoridadCertRepository;

/**
 * Implementació dels mètodes per a gestionar els manteniments
 * d'informació SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ScspServiceImpl implements ScspService {

	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private ScspCoreServicioRepository scspCoreServicioRepository;
	@Autowired
	private ScspCoreEmAutorizacionAplicacionRepository scspCoreEmAutorizacionAplicacionRepository;
	@Autowired
	private ScspCoreEmAutorizacionOrganismoRepository scspCoreEmAutorizacionOrganismoRepository;
	@Autowired
	private ScspCoreEmAutorizacionAutoridadCertRepository scspCoreEmAutorizacionAutoridadCertRepository;
	@Autowired
	private ScspCoreEmAutorizacionCertificadoRepository scspCoreEmAutorizacionCertificadoRepository;
	@Autowired
	private ClauPrivadaRepository clauPrivadaRepository;
	@Autowired
	private ClauPublicaRepository clauPublicaRepository;
	@Autowired
	private OrganismeCessionariRepository organismeCessionariRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;



	@Transactional
	@Override
	public AplicacioDto aplicacioCreate(
			AplicacioDto aplicacio) {
		logger.debug("Creant una nova aplicació (aplicacio=" + aplicacio + ")");
		ScspCoreEmAutorizacionAplicacionEntity entity = ScspCoreEmAutorizacionAplicacionEntity.getBuilder(
				aplicacio.getCertificatNif(),
				aplicacio.getNumeroSerie(),
				aplicacio.getCn(),
				aplicacio.getAutoridadCertifId()).
				fechaAlta(aplicacio.getDataAlta()).
				fechaBaja(aplicacio.getDataBaixa()).
				build();
		return toAplicacioDto(
				scspCoreEmAutorizacionAplicacionRepository.save(entity));
	}

	@Transactional
	@Override
	public void aplicacioUpdate(
			AplicacioDto aplicacio) {
		logger.debug("Modificant l'aplicació (aplicacio=" + aplicacio + ")");
		ScspCoreEmAutorizacionAplicacionEntity entity = scspCoreEmAutorizacionAplicacionRepository.findOne(
				aplicacio.getId());
		if (entity == null) {
			throw new NotFoundException(
					aplicacio.getId(),
					ScspCoreEmAutorizacionAplicacionEntity.class);
		}
		entity.update(
				aplicacio.getCertificatNif(),
				aplicacio.getNumeroSerie(),
				aplicacio.getCn(),
				aplicacio.getAutoridadCertifId(),
				aplicacio.getDataAlta(),
				aplicacio.getDataBaixa());
	}

	@Transactional
	@Override
	public void aplicacioDelete(
			Integer id) {
		logger.debug("Esborrant l'aplicació (id=" + id + ")");
		ScspCoreEmAutorizacionAplicacionEntity entity = scspCoreEmAutorizacionAplicacionRepository.findOne(
				id);
		if (entity == null) {
			throw new NotFoundException(
					id,
					ScspCoreEmAutorizacionAplicacionEntity.class);
		}
		scspCoreEmAutorizacionAplicacionRepository.delete(id);
	}

	@Transactional(readOnly = true)
	@Override
	public AplicacioDto aplicacioFindById(
			Integer id) {
		logger.debug("Esborrant l'aplicació (id=" + id + ")");
		ScspCoreEmAutorizacionAplicacionEntity entity = scspCoreEmAutorizacionAplicacionRepository.findOne(
				id);
		if (entity == null) {
			throw new NotFoundException(
					id,
					ScspCoreEmAutorizacionAplicacionEntity.class);
		}
		return toAplicacioDto(entity);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<AplicacioDto> aplicacioFindByFiltrePaginat(
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant aplicacions amb paginació (" +
				"filtre=" + filtre + ", " +
				"paginacioParams=" + paginacioParams + ")");
		Map<String, String> mapeigOrdenacio = new HashMap<String, String>();
		mapeigOrdenacio.put("certificatNif", "nifCertificado");
		return paginacioHelper.toPaginaDto(
				scspCoreEmAutorizacionAplicacionRepository.findAll(
						paginacioHelper.toSpringDataPageable(
								paginacioParams,
								mapeigOrdenacio)),
				AplicacioDto.class,
				new Converter<ScspCoreEmAutorizacionAplicacionEntity, AplicacioDto>() {
					@Override
					public AplicacioDto convert(ScspCoreEmAutorizacionAplicacionEntity source) {
						return toAplicacioDto(source);
					}
				});
	}

	@Transactional
	@Override
	public OrganismeDto organismeCreate(
			OrganismeDto organismo) {
		logger.debug("Creant un nou organisme (organismo=" + organismo + ")");
		ScspCoreEmAutorizacionOrganismoEntity entity = ScspCoreEmAutorizacionOrganismoEntity.getBuilder(
				organismo.getCif(),
				organismo.getNom(),
				organismo.getDataAlta()).
				fechaBaja(organismo.getDataBaixa()).
				build();
		return toOrganismeDto(
				scspCoreEmAutorizacionOrganismoRepository.save(entity));
	}

	@Transactional
	@Override
	public void organismeUpdate(
			OrganismeDto organismo) {
		logger.debug("Modificant l'organisme (organismo=" + organismo + ")");
		ScspCoreEmAutorizacionOrganismoEntity entity = scspCoreEmAutorizacionOrganismoRepository.findOne(
				organismo.getId());
		if (entity == null) {
			throw new NotFoundException(
					organismo.getId(),
					ScspCoreEmAutorizacionOrganismoEntity.class);
		}
		entity.update(
				organismo.getCif(),
				organismo.getNom(),
				organismo.getDataAlta(),
				organismo.getDataBaixa());
	}

	@Transactional
	@Override
	public void organismeDelete(Long id) {
		logger.debug("Esborrant l'organisme (id=" + id + ")");
		ScspCoreEmAutorizacionOrganismoEntity entity = scspCoreEmAutorizacionOrganismoRepository.findOne(
				id);
		if (entity == null) {
			throw new NotFoundException(
					id,
					ScspCoreEmAutorizacionOrganismoEntity.class);
		}
		scspCoreEmAutorizacionOrganismoRepository.delete(id);
	}

	@Transactional(readOnly = true)
	@Override
	public OrganismeDto organismeFindById(Long id) {
		logger.debug("Consultant l'organisme per id (id=" + id + ")");
		ScspCoreEmAutorizacionOrganismoEntity entity = scspCoreEmAutorizacionOrganismoRepository.findOne(
				id);
		if (entity == null) {
			throw new NotFoundException(
					id,
					ScspCoreEmAutorizacionOrganismoEntity.class);
		}
		return toOrganismeDto(entity);
	}
	
	public List<OrganismeDto> organismeFindByCif(String cif) throws NotFoundException {
		List<ScspCoreEmAutorizacionOrganismoEntity> organismes = scspCoreEmAutorizacionOrganismoRepository.findByCif(cif);
		List<OrganismeDto> resposta = new ArrayList<OrganismeDto>();
		for (ScspCoreEmAutorizacionOrganismoEntity organisme: organismes) {
			resposta.add(toOrganismeDto(organisme));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<OrganismeDto> organismeFindByFiltrePaginat(
			OrganismeFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant organismes amb paginació (" +
				"filtre=" + filtre + ", " +
				"paginacioParams=" + paginacioParams + ")");
		Map<String, String> mapeigOrdenacio = new HashMap<String, String>();
		mapeigOrdenacio.put("cif", "idorganismo");
		mapeigOrdenacio.put("nom", "nombreOrganismo");
		Page<ScspCoreEmAutorizacionOrganismoEntity> page = scspCoreEmAutorizacionOrganismoRepository.findByFiltrePaginat(
				(filtre.getNom() == null || filtre.getNom().isEmpty()),
				filtre.getNom() != null ? filtre.getNom(): "",
				(filtre.getCif() == null || filtre.getCif().isEmpty()),
				filtre.getCif(),
				paginacioHelper.toSpringDataPageable(
						paginacioParams,
						mapeigOrdenacio));
		return paginacioHelper.toPaginaDto(
				page,
				OrganismeDto.class,
				new Converter<ScspCoreEmAutorizacionOrganismoEntity, OrganismeDto>() {
					@Override
					public OrganismeDto convert(ScspCoreEmAutorizacionOrganismoEntity source) {
						return toOrganismeDto(source);
					}
				});
	}

	@Transactional
	@Override
	public AutoritzacioDto autoritzacioCreate(
			AutoritzacioDto autoritzacio) {
		logger.debug("Creant una nova autorització (autoritzacio=" + autoritzacio + ")");
		ScspCoreServicioEntity servicio = getScspCoreServicioPerServeiId(
				autoritzacio.getServeiId());
		ScspCoreEmAutorizacionAplicacionEntity aplicacion = scspCoreEmAutorizacionAplicacionRepository.findOne(
				autoritzacio.getAplicacioId());
		if (aplicacion == null) {
			throw new NotFoundException(
					autoritzacio.getAplicacioId(),
					ScspCoreEmAutorizacionAplicacionEntity.class);
		}
		ScspCoreEmAutorizacionOrganismoEntity organismo = scspCoreEmAutorizacionOrganismoRepository.findOne(
				autoritzacio.getOrganismeId());
		if (organismo == null) {
			throw new NotFoundException(
					autoritzacio.getOrganismeId(),
					ScspCoreEmAutorizacionOrganismoEntity.class);
		}
		ScspCoreEmAutorizacionCertificadoEntity entity = ScspCoreEmAutorizacionCertificadoEntity.getBuilder(
				servicio,
				aplicacion,
				organismo).
				fechaAlta(autoritzacio.getDataAlta()).
				fechaBaja(autoritzacio.getDataBaixa()).
				build();
		return toAutoritzacioDto(
				scspCoreEmAutorizacionCertificadoRepository.save(entity));
	}

	@Transactional
	@Override
	public void autoritzacioUpdate(
			AutoritzacioDto autoritzacio) {
		logger.debug("Modificant l'autorització (autoritzacio=" + autoritzacio + ")");
		ScspCoreEmAutorizacionCertificadoEntity entity = scspCoreEmAutorizacionCertificadoRepository.findOne(
				autoritzacio.getId());
		if (entity == null) {
			throw new NotFoundException(
					autoritzacio.getId(),
					ScspCoreEmAutorizacionCertificadoEntity.class);
		}
		ScspCoreServicioEntity servicio = getScspCoreServicioPerServeiId(
				autoritzacio.getServeiId());
		ScspCoreEmAutorizacionAplicacionEntity aplicacion = scspCoreEmAutorizacionAplicacionRepository.findOne(
				autoritzacio.getAplicacioId());
		if (aplicacion == null) {
			throw new NotFoundException(
					autoritzacio.getAplicacioId(),
					ScspCoreEmAutorizacionAplicacionEntity.class);
		}
		ScspCoreEmAutorizacionOrganismoEntity organismo = scspCoreEmAutorizacionOrganismoRepository.findOne(
				autoritzacio.getOrganismeId());
		if (organismo == null) {
			throw new NotFoundException(
					autoritzacio.getOrganismeId(),
					ScspCoreEmAutorizacionOrganismoEntity.class);
		}
		entity.update(
				servicio,
				aplicacion,
				organismo,
				autoritzacio.getDataAlta(),
				autoritzacio.getDataBaixa());
	}

	@Transactional
	@Override
	public void autoritzacioDelete(
			Long id) throws NotFoundException {
		logger.debug("Esborrant l'autorització (id=" + id + ")");
		ScspCoreEmAutorizacionCertificadoEntity entity = scspCoreEmAutorizacionCertificadoRepository.findOne(
				id);
		if (entity == null) {
			throw new NotFoundException(
					id,
					ScspCoreEmAutorizacionCertificadoEntity.class);
		}
		scspCoreEmAutorizacionCertificadoRepository.delete(id);
	}

	@Transactional(readOnly = true)
	@Override
	public AutoritzacioDto autoritzacioFindById(
			Long id) throws NotFoundException {
		logger.debug("Consultant l'autorització per id (id=" + id + ")");
		ScspCoreEmAutorizacionCertificadoEntity entity = scspCoreEmAutorizacionCertificadoRepository.findOne(
				id);
		if (entity == null) {
			throw new NotFoundException(
					id,
					ScspCoreEmAutorizacionCertificadoEntity.class);
		}
		return toAutoritzacioDto(entity);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<AutoritzacioDto> autoritzacioFindByFiltrePaginat(
			AutoritzacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		Long serveiId = filtre.getServeiId();
		logger.debug("Consultant autoritzacions per servei amb paginació (" +
				"serveiId=" + serveiId + ", " +
				"paginacioParams=" + paginacioParams + ")");
		ScspCoreServicioEntity servicio = getScspCoreServicioPerServeiId(serveiId);
		Map<String, String> mapeigOrdenacio = new HashMap<String, String>();
		mapeigOrdenacio.put("aplicacioNom", "aplicacion.cn");
		mapeigOrdenacio.put("organismeNom", "organismo.nombreOrganismo");
		
		ScspCoreEmAutorizacionOrganismoEntity organisme = filtre.getOrganismeId() != null ? 
																scspCoreEmAutorizacionOrganismoRepository.findOne(filtre.getOrganismeId()) : 
																null;

		return paginacioHelper.toPaginaDto(
				scspCoreEmAutorizacionCertificadoRepository.findByFiltre(
						servicio, 
						filtre.getAplicacio() == null || filtre.getAplicacio().length() == 0, 
						filtre.getAplicacio(), 
						organisme == null,
						organisme,
						paginacioHelper.toSpringDataPageable(
								paginacioParams,
								mapeigOrdenacio)),
				AutoritzacioDto.class,
				new Converter<ScspCoreEmAutorizacionCertificadoEntity, AutoritzacioDto>() {
					@Override
					public AutoritzacioDto convert(ScspCoreEmAutorizacionCertificadoEntity source) {
						return toAutoritzacioDto(source);
					}
				});
	}

	@Transactional(readOnly = true)
	@Override
	public List<EmisorDto> emissorFindAll() {
		logger.debug("Consulta la llista d'emissors SCSP");
		List<Object[]> emissors = scspCoreServicioRepository.findScspEmisorCertificadoAll();
		List<EmisorDto> resposta = new ArrayList<EmisorDto>();
		for (Object[] emisor: emissors) {
			EmisorDto e = new EmisorDto();
			e.setId(((BigDecimal)emisor[0]).longValue());
			e.setCif((String)emisor[1]);
			e.setNom((String)emisor[2]);
			resposta.add(e);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ClauPublicaDto> clauPublicaFindAll() {
		logger.debug("Consulta la llista de claus públiques SCSP");
		List<Object[]> claus = scspCoreServicioRepository.findScspClavePublicaAll();
		List<ClauPublicaDto> resposta = new ArrayList<ClauPublicaDto>();
		for (Object[] clau: claus) {
			ClauPublicaDto c = new ClauPublicaDto();
			c.setId(((BigDecimal)clau[0]).longValue());
			c.setAlies((String)clau[1]);
			c.setNom((String)clau[2]);
			c.setNumSerie((String)clau[3]);
			resposta.add(c);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ClauPrivadaDto> clauPrivadaFindAll() {
		logger.debug("Consulta la llista de claus públiques SCSP");
		List<Object[]> claus = scspCoreServicioRepository.findScspClavePrivadaAll();
		List<ClauPrivadaDto> resposta = new ArrayList<ClauPrivadaDto>();
		for (Object[] clau: claus) {
			ClauPrivadaDto c = new ClauPrivadaDto();
			c.setId(((BigDecimal)clau[0]).longValue());
			c.setAlies((String)clau[1]);
			c.setNom((String)clau[2]);
			c.setNumSerie((String)clau[3]);
			resposta.add(c);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<AplicacioDto> aplicacioFindAll() {
		logger.debug("Consulta la llista d'aplicacions SCSP");
		List<ScspCoreEmAutorizacionAplicacionEntity> aplicacions = scspCoreEmAutorizacionAplicacionRepository.findAll(
				new Sort(new Order(Direction.ASC, "cn")));
		List<AplicacioDto> resposta = new ArrayList<AplicacioDto>();
		for (ScspCoreEmAutorizacionAplicacionEntity aplicacio: aplicacions) {
			resposta.add(toAplicacioDto(aplicacio));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<OrganismeDto> organismeFindAll() {
		logger.debug("Consulta la llista d'organismes SCSP");
		List<ScspCoreEmAutorizacionOrganismoEntity> organismes = scspCoreEmAutorizacionOrganismoRepository.findAll(
				new Sort(new Order(Direction.ASC, "nombreOrganismo")));
		List<OrganismeDto> resposta = new ArrayList<OrganismeDto>();
		for (ScspCoreEmAutorizacionOrganismoEntity organisme: organismes) {
			resposta.add(toOrganismeDto(organisme));
		}
		return resposta;
	}



	private AplicacioDto toAplicacioDto(
			ScspCoreEmAutorizacionAplicacionEntity entity) {
		AplicacioDto dto = new AplicacioDto();
		dto.setId(entity.getIdAplicacion());
		dto.setCertificatNif(entity.getNifCertificado());
		dto.setNumeroSerie(entity.getNumeroSerie());
		dto.setCn(entity.getCn());
		dto.setDarreraComprovacio(entity.getTiempoComprobacion());
		dto.setAutoridadCertifId(entity.getAutoridadcertif());
		
		dto.setDataAlta(entity.getFechaAlta());
		dto.setDataBaixa(entity.getFechaBaja());
		return dto;
	}

	private OrganismeDto toOrganismeDto(
			ScspCoreEmAutorizacionOrganismoEntity entity) {
		OrganismeDto dto = new OrganismeDto();
		dto.setId(entity.getId());
		dto.setCif(entity.getIdorganismo());
		dto.setNom(entity.getNombreOrganismo());
		dto.setDataAlta(entity.getFechaAlta());
		dto.setDataBaixa(entity.getFechaBaja());
		return dto;
	}

	private AutoritzacioDto toAutoritzacioDto (
			ScspCoreEmAutorizacionCertificadoEntity entity) {
		AutoritzacioDto dto = new AutoritzacioDto();
		dto.setId(entity.getId());
		dto.setAplicacioId(
				entity.getAplicacion().getIdAplicacion());
		dto.setAplicacioNom(
				entity.getAplicacion().getCn());
		dto.setOrganismeId(
				entity.getOrganismo().getId());
		dto.setOrganismeNom(
				entity.getOrganismo().getNombreOrganismo());
		dto.setDataAlta(entity.getFechaAlta());
		dto.setDataBaixa(entity.getFechaBaja());
		return dto;
	}

	private ScspCoreServicioEntity getScspCoreServicioPerServeiId(
			Long serveiId) {
		ServeiEntity servei = serveiRepository.findOne(serveiId);
		if (servei == null) {
			throw new NotFoundException(
					serveiId,
					ServeiEntity.class);
		}
		ScspCoreServicioEntity servicio = scspCoreServicioRepository.findByCodigoCertificado(
				servei.getCodi());
		if (servicio == null) {
			throw new NotFoundException(
					servei.getCodi(),
					ScspCoreServicioEntity.class);
		}
		return servicio;
	}



	@Override
	@Transactional(readOnly = true)
	public List<OrganismeCessionariDto> findAllOrganismeCessionari() {
		logger.debug("Consulta de tots els organismes cessionaris");
		
		List<OrganismeCessionariEntity> llista = organismeCessionariRepository.findAll();
		
		return conversioTipusHelper.convertirList(
				llista,
				OrganismeCessionariDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ClauPublicaDto> clauPublicaFindByFiltrePaginat(PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de tots els claus públiques");
		Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams); 
		Page<ClauPublicaEntity> page = clauPublicaRepository.findAll(pageable);
		
		return paginacioHelper.toPaginaDto(page, ClauPublicaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public ClauPublicaDto findClauPublicaById(Long id) throws NotFoundException {
		logger.debug("Consulta un clau publica (id = " + id + ")");
		
		return conversioTipusHelper.convertir(
				clauPublicaRepository.findById(id),
				ClauPublicaDto.class);
	}

	@Override
	@Transactional
	public ClauPublicaDto clauPublicaCreate(ClauPublicaDto item) {
		logger.debug("Creant una nou emissor de certificat : " + item);
		
		ClauPublicaEntity entity = ClauPublicaEntity.getBuilder(
				item.getAlies(),
				item.getNom(),
				item.getNumSerie(),
				item.getDataAlta(),
				item.getDataBaixa()).build();
		
		return conversioTipusHelper.convertir(
				clauPublicaRepository.save(entity),
				ClauPublicaDto.class);
	}

	@Override
	@Transactional
	public ClauPublicaDto clauPublicaUpdate(ClauPublicaDto item) throws NotFoundException {
		logger.debug("Actualitzant el clau publica (id = " + item.getId() +
					 ") amb la informació: " + item);
		
		ClauPublicaEntity entity = clauPublicaRepository.findById(item.getId());
		if (entity == null) {
			logger.debug("No s'ha trobat el clau publica (id = " + item.getId() + ")");
			throw new NotFoundException(item.getId(), ClauPublicaEntity.class);
		}
		
		entity.update(
				item.getAlies(),
				item.getNom(),
				item.getNumSerie(),
				item.getDataAlta(),
				item.getDataBaixa());
		
		return conversioTipusHelper.convertir(
				entity,
				ClauPublicaDto.class);
	}

	@Override
	@Transactional
	public void clauPublicaDelete(Long id) throws NotFoundException {
		logger.debug("Esborrant el clau publica (id =" + id + ")");
		
		ClauPublicaEntity entity = clauPublicaRepository.findById(id);
		if (entity == null) {
			logger.debug("No s'ha trobat el clau publica (id = " + id + ")");
			throw new NotFoundException(id, ClauPublicaEntity.class);
		}
		
		clauPublicaRepository.delete(entity);
		
//		return dtoMappingHelper.getMapperFacade().map(
//				entity,
//				ClauPublicaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ClauPrivadaDto> clauPrivadaFindByFiltrePaginat(PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de tots les claus privades");
		Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams); 

		Page<ClauPrivadaEntity> page = clauPrivadaRepository.findAll(pageable);
		
		return paginacioHelper.toPaginaDto(page, ClauPrivadaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public ClauPrivadaDto findClauPrivadaById(Long id) throws NotFoundException {
		logger.debug("Consulta una clau privada (id = " + id + ")");
		
		return conversioTipusHelper.convertir(
				clauPrivadaRepository.findById(id),
				ClauPrivadaDto.class);
	}

	@Override
	@Transactional
	public ClauPrivadaDto clauPrivadaCreate(ClauPrivadaDto item) {
		logger.debug("Creant una nova clau privada : " + item);
		
		OrganismeCessionariEntity organisme = organismeCessionariRepository.findById(
				item.getOrganisme());
		ClauPrivadaEntity entity = ClauPrivadaEntity.builder()
				.alies(item.getAlies())
				.nom(item.getNom())
				.password(item.getPassword())
				.numSerie(item.getNumSerie())
				.dataBaixa(item.getDataBaixa())
				.dataAlta(item.getDataAlta())
				.interoperabilitat(item.getInteroperabilitat())
				.organisme(organisme).build();
		
		return conversioTipusHelper.convertir(
				clauPrivadaRepository.save(entity),
				ClauPrivadaDto.class);
	}

	@Override
	@Transactional
	public ClauPrivadaDto clauPrivadaUpdate(ClauPrivadaDto item) throws NotFoundException {
		logger.debug("Actualitzant la clau privada (id = " + item.getId() +
					 ") amb la informació: " + item);
		
		ClauPrivadaEntity entity = clauPrivadaRepository.findById(item.getId());
		if (entity == null) {
			logger.debug("No s'ha trobat la clau privada (id = " + item.getId() + ")");
			throw new NotFoundException(item.getId(), ClauPublicaEntity.class);
		}
		
		OrganismeCessionariEntity organisme = organismeCessionariRepository.findById(
				item.getOrganisme());
		entity.update(
				item.getAlies(),
				item.getNom(),
				item.getPassword(),
				item.getNumSerie(),
				item.getDataBaixa(),
				item.getDataAlta(),
				item.getInteroperabilitat(),
				organisme);
		
		return conversioTipusHelper.convertir(
				entity,
				ClauPrivadaDto.class);
	}

	@Override
	@Transactional
	public void clauPrivadaDelete(Long id) throws NotFoundException {
		logger.debug("Esborrant la clau privada (id =" + id + ")");
		
		ClauPrivadaEntity entity = clauPrivadaRepository.findById(id);
		if (entity == null) {
			logger.debug("No s'ha trobat la clau privada (id = " + id + ")");
			throw new NotFoundException(id, ClauPublicaEntity.class);
		}
		
		clauPrivadaRepository.delete(entity);
		
//		return conversioTipusHelper.convertir(
//				entity,
//				ClauPrivadaDto.class);
		
	}


	@Transactional(readOnly = true)
	@Override
	public List<AutoritatCertificacioDto> autoridadCertificacionFindAll() {
		logger.debug("Consulta la llista d'autoritats de certificació SCSP");
		List<ScspCoreEmAutorizacionAutoridadCertEntity> autoritats = scspCoreEmAutorizacionAutoridadCertRepository.findAll(
				new Sort(new Order(Direction.ASC, "nombre")));
		List<AutoritatCertificacioDto> resposta = new ArrayList<AutoritatCertificacioDto>();
		for (ScspCoreEmAutorizacionAutoridadCertEntity autoritat: autoritats) {
			AutoritatCertificacioDto dto = new AutoritatCertificacioDto();
			dto.setId(autoritat.getId());
			dto.setCodca(autoritat.getCodca());
			dto.setNombre(autoritat.getNombre());
			resposta.add(dto);
		}
		return resposta;
	}

	@Override
	public PaginaDto<AutoritatCertificacioDto> autoritatCertificacioFindByFiltrePaginat(
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de tots les autoritat de certificació");
		Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams); 

		Page<ScspCoreEmAutorizacionAutoridadCertEntity> page = scspCoreEmAutorizacionAutoridadCertRepository.findAll(pageable);
		
		return paginacioHelper.toPaginaDto(page, AutoritatCertificacioDto.class);
	}

	@Override
	public AutoritatCertificacioDto autoritatCertificacioFindById(Long id) throws NotFoundException {
		logger.debug("Consulta una autoritat de certificació (id = " + id + ")");
		
		return conversioTipusHelper.convertir(
				scspCoreEmAutorizacionAutoridadCertRepository.findById(id),
				AutoritatCertificacioDto.class);
	}

	@Override
	public AutoritatCertificacioDto autoritatCertificacioCreate(AutoritatCertificacioDto item) {
		logger.debug("Creant una nova autoritat de certificació : " + item);
		
		ScspCoreEmAutorizacionAutoridadCertEntity entity = ScspCoreEmAutorizacionAutoridadCertEntity.builder()
				.codca(item.getCodca())
				.nombre(item.getNombre())
				.build();
		
		return conversioTipusHelper.convertir(
				scspCoreEmAutorizacionAutoridadCertRepository.save(entity),
				AutoritatCertificacioDto.class);
	}

	@Override
	public AutoritatCertificacioDto autoritatCertificacioUpdate(AutoritatCertificacioDto item) throws NotFoundException {
		logger.debug("Actualitzant la autoritat de certificació (id = " + item.getId() +
					 ") amb la informació: " + item);
		
		ScspCoreEmAutorizacionAutoridadCertEntity entity = scspCoreEmAutorizacionAutoridadCertRepository.findById(item.getId());
		if (entity == null) {
			logger.debug("No s'ha trobat la autoritat de certificació (id = " + item.getId() + ")");
			throw new NotFoundException(item.getId(), ClauPublicaEntity.class);
		}
		
		entity.update(
				item.getCodca(),
				item.getNombre());
		
		return conversioTipusHelper.convertir(
				entity,
				AutoritatCertificacioDto.class);
	}

	@Override
	public void autoritatCertificacioDelete(Long id) throws NotFoundException {
		logger.debug("Esborrant autoritat de certificació (id =" + id + ")");
		
		ScspCoreEmAutorizacionAutoridadCertEntity entity = scspCoreEmAutorizacionAutoridadCertRepository.findById(id);
		if (entity == null) {
			logger.debug("No s'ha trobat la autoritat de certificació (id = " + id + ")");
			throw new NotFoundException(id, ClauPublicaEntity.class);
		}
		
		scspCoreEmAutorizacionAutoridadCertRepository.delete(entity);
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(ScspServiceImpl.class);
}
