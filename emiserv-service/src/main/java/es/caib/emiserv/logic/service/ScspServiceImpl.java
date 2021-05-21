/**
 * 
 */
package es.caib.emiserv.logic.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.emiserv.logic.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.helper.PaginacioHelper;
import es.caib.emiserv.logic.helper.PropertiesHelper;
import es.caib.emiserv.logic.helper.PaginacioHelper.Converter;
import es.caib.emiserv.logic.intf.dto.AplicacioDto;
import es.caib.emiserv.logic.intf.dto.AutoritatCertificacioDto;
import es.caib.emiserv.logic.intf.dto.AutoritzacioDto;
import es.caib.emiserv.logic.intf.dto.AutoritzacioFiltreDto;
import es.caib.emiserv.logic.intf.dto.ClauPrivadaDto;
import es.caib.emiserv.logic.intf.dto.ClauPublicaDto;
import es.caib.emiserv.logic.intf.dto.EmisorDto;
import es.caib.emiserv.logic.intf.dto.OrganismeCessionariDto;
import es.caib.emiserv.logic.intf.dto.OrganismeDto;
import es.caib.emiserv.logic.intf.dto.OrganismeFiltreDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.service.ScspService;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreClavePrivadaEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreClavePublicaEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAplicacionEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAutorizacionAutoridadCertEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAutorizacionCertificadoEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAutorizacionOrganismoEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreOrganismoCessionarioEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreClavePrivadaRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreClavePublicaRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreEmAplicacionRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreEmAutorizacionAutoridadCertRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreEmAutorizacionCertificadoRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreEmAutorizacionOrganismoRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreOrganismoCessionarioRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació dels mètodes per a gestionar els manteniments
 * d'informació SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class ScspServiceImpl implements ScspService {

	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private ScspCoreServicioRepository scspCoreServicioRepository;
	@Autowired
	private ScspCoreEmAplicacionRepository scspCoreEmAutorizacionAplicacionRepository;
	@Autowired
	private ScspCoreEmAutorizacionOrganismoRepository scspCoreEmAutorizacionOrganismoRepository;
	@Autowired
	private ScspCoreEmAutorizacionAutoridadCertRepository scspCoreEmAutorizacionAutoridadCertRepository;
	@Autowired
	private ScspCoreEmAutorizacionCertificadoRepository scspCoreEmAutorizacionCertificadoRepository;
	@Autowired
	private ScspCoreClavePrivadaRepository clauPrivadaRepository;
	@Autowired
	private ScspCoreClavePublicaRepository clauPublicaRepository;
	@Autowired
	private ScspCoreOrganismoCessionarioRepository organismeCessionariRepository;
	@Autowired
	private PropertiesHelper propertiesHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;

	@Transactional
	@Override
	public AplicacioDto aplicacioCreate(
			AplicacioDto aplicacio) {
		log.debug("Creant una nova aplicació (aplicacio=" + aplicacio + ")");
		ScspCoreEmAplicacionEntity entity = ScspCoreEmAplicacionEntity.getBuilder(
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
		log.debug("Modificant l'aplicació (aplicacio=" + aplicacio + ")");
		ScspCoreEmAplicacionEntity entity = scspCoreEmAutorizacionAplicacionRepository.getOne(
				aplicacio.getId());
		if (entity == null) {
			throw new NotFoundException(
					aplicacio.getId(),
					ScspCoreEmAplicacionEntity.class);
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
		log.debug("Esborrant l'aplicació (id=" + id + ")");
		ScspCoreEmAplicacionEntity entity = scspCoreEmAutorizacionAplicacionRepository.getOne(
				id);
		if (entity == null) {
			throw new NotFoundException(
					id,
					ScspCoreEmAplicacionEntity.class);
		}
		scspCoreEmAutorizacionAplicacionRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	@Override
	public AplicacioDto aplicacioFindById(
			Integer id) {
		log.debug("Esborrant l'aplicació (id=" + id + ")");
		ScspCoreEmAplicacionEntity entity = scspCoreEmAutorizacionAplicacionRepository.getOne(
				id);
		if (entity == null) {
			throw new NotFoundException(
					id,
					ScspCoreEmAplicacionEntity.class);
		}
		return toAplicacioDto(entity);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<AplicacioDto> aplicacioFindByFiltrePaginat(
			String filtre,
			PaginacioParamsDto paginacioParams) {
		log.debug("Consultant aplicacions amb paginació (" +
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
				new Converter<ScspCoreEmAplicacionEntity, AplicacioDto>() {
					@Override
					public AplicacioDto convert(ScspCoreEmAplicacionEntity source) {
						return toAplicacioDto(source);
					}
				});
	}

	@Transactional
	@Override
	public OrganismeDto organismeCreate(
			OrganismeDto organismo) {
		log.debug("Creant un nou organisme (organismo=" + organismo + ")");
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
		log.debug("Modificant l'organisme (organismo=" + organismo + ")");
		ScspCoreEmAutorizacionOrganismoEntity entity = scspCoreEmAutorizacionOrganismoRepository.getOne(
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
		log.debug("Esborrant l'organisme (id=" + id + ")");
		ScspCoreEmAutorizacionOrganismoEntity entity = scspCoreEmAutorizacionOrganismoRepository.getOne(
				id);
		if (entity == null) {
			throw new NotFoundException(
					id,
					ScspCoreEmAutorizacionOrganismoEntity.class);
		}
		scspCoreEmAutorizacionOrganismoRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	@Override
	public OrganismeDto organismeFindById(Long id) {
		log.debug("Consultant l'organisme per id (id=" + id + ")");
		ScspCoreEmAutorizacionOrganismoEntity entity = scspCoreEmAutorizacionOrganismoRepository.getOne(
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
		log.debug("Consultant organismes amb paginació (" +
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
		log.debug("Creant una nova autorització (autoritzacio=" + autoritzacio + ")");
		ScspCoreServicioEntity servicio = getScspCoreServicioPerServeiId(
				autoritzacio.getServeiId());
		ScspCoreEmAplicacionEntity aplicacion = scspCoreEmAutorizacionAplicacionRepository.getOne(
				autoritzacio.getAplicacioId());
		if (aplicacion == null) {
			throw new NotFoundException(
					autoritzacio.getAplicacioId(),
					ScspCoreEmAplicacionEntity.class);
		}
		ScspCoreEmAutorizacionOrganismoEntity organismo = scspCoreEmAutorizacionOrganismoRepository.getOne(
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
		log.debug("Modificant l'autorització (autoritzacio=" + autoritzacio + ")");
		ScspCoreEmAutorizacionCertificadoEntity entity = scspCoreEmAutorizacionCertificadoRepository.getOne(
				autoritzacio.getId());
		if (entity == null) {
			throw new NotFoundException(
					autoritzacio.getId(),
					ScspCoreEmAutorizacionCertificadoEntity.class);
		}
		ScspCoreServicioEntity servicio = getScspCoreServicioPerServeiId(
				autoritzacio.getServeiId());
		ScspCoreEmAplicacionEntity aplicacion = scspCoreEmAutorizacionAplicacionRepository.getOne(
				autoritzacio.getAplicacioId());
		if (aplicacion == null) {
			throw new NotFoundException(
					autoritzacio.getAplicacioId(),
					ScspCoreEmAplicacionEntity.class);
		}
		ScspCoreEmAutorizacionOrganismoEntity organismo = scspCoreEmAutorizacionOrganismoRepository.getOne(
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
		log.debug("Esborrant l'autorització (id=" + id + ")");
		ScspCoreEmAutorizacionCertificadoEntity entity = scspCoreEmAutorizacionCertificadoRepository.getOne(
				id);
		if (entity == null) {
			throw new NotFoundException(
					id,
					ScspCoreEmAutorizacionCertificadoEntity.class);
		}
		scspCoreEmAutorizacionCertificadoRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	@Override
	public AutoritzacioDto autoritzacioFindById(
			Long id) throws NotFoundException {
		log.debug("Consultant l'autorització per id (id=" + id + ")");
		ScspCoreEmAutorizacionCertificadoEntity entity = scspCoreEmAutorizacionCertificadoRepository.getOne(
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
		log.debug("Consultant autoritzacions per servei amb paginació (" +
				"serveiId=" + serveiId + ", " +
				"paginacioParams=" + paginacioParams + ")");
		ScspCoreServicioEntity servicio = getScspCoreServicioPerServeiId(serveiId);
		Map<String, String> mapeigOrdenacio = new HashMap<String, String>();
		mapeigOrdenacio.put("aplicacioNom", "aplicacion.cn");
		mapeigOrdenacio.put("organismeNom", "organismo.nombreOrganismo");
		ScspCoreEmAutorizacionOrganismoEntity organisme = filtre.getOrganismeId() != null ? 
																scspCoreEmAutorizacionOrganismoRepository.getOne(filtre.getOrganismeId()) : 
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
		log.debug("Consulta la llista d'emissors SCSP");
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
		log.debug("Consulta la llista de claus públiques SCSP");
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
		log.debug("Consulta la llista de claus públiques SCSP");
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
		log.debug("Consulta la llista d'aplicacions SCSP");
		List<ScspCoreEmAplicacionEntity> aplicacions = scspCoreEmAutorizacionAplicacionRepository.findAll(
				Sort.by(Order.asc("cn")));
		List<AplicacioDto> resposta = new ArrayList<AplicacioDto>();
		for (ScspCoreEmAplicacionEntity aplicacio: aplicacions) {
			resposta.add(toAplicacioDto(aplicacio));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<OrganismeDto> organismeFindAll() {
		log.debug("Consulta la llista d'organismes SCSP");
		List<ScspCoreEmAutorizacionOrganismoEntity> organismes = scspCoreEmAutorizacionOrganismoRepository.findAll(
				Sort.by(Order.asc("nombreOrganismo")));
		List<OrganismeDto> resposta = new ArrayList<OrganismeDto>();
		for (ScspCoreEmAutorizacionOrganismoEntity organisme: organismes) {
			resposta.add(toOrganismeDto(organisme));
		}
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganismeCessionariDto> findAllOrganismeCessionari() {
		log.debug("Consulta de tots els organismes cessionaris");
		List<ScspCoreOrganismoCessionarioEntity> llista = organismeCessionariRepository.findAll();
		return conversioTipusHelper.convertirList(
				llista,
				OrganismeCessionariDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ClauPublicaDto> clauPublicaFindByFiltrePaginat(PaginacioParamsDto paginacioParams) {
		log.debug("Consulta de tots els claus públiques");
		Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams); 
		Page<ScspCoreClavePublicaEntity> page = clauPublicaRepository.findAll(pageable);
		return paginacioHelper.toPaginaDto(page, ClauPublicaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public ClauPublicaDto findClauPublicaById(Long id) throws NotFoundException {
		log.debug("Consulta un clau publica (id = " + id + ")");
		return conversioTipusHelper.convertir(
				clauPublicaRepository.findById(id).get(),
				ClauPublicaDto.class);
	}

	@Override
	@Transactional
	public ClauPublicaDto clauPublicaCreate(ClauPublicaDto item) {
		log.debug("Creant una nou emissor de certificat : " + item);
		ScspCoreClavePublicaEntity entity = ScspCoreClavePublicaEntity.getBuilder(
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
		log.debug("Actualitzant el clau publica (id = " + item.getId() +
					 ") amb la informació: " + item);
		ScspCoreClavePublicaEntity entity = clauPublicaRepository.getOne(item.getId());
		if (entity == null) {
			log.debug("No s'ha trobat el clau publica (id = " + item.getId() + ")");
			throw new NotFoundException(item.getId(), ScspCoreClavePublicaEntity.class);
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
		log.debug("Esborrant el clau publica (id =" + id + ")");
		ScspCoreClavePublicaEntity entity = clauPublicaRepository.getOne(id);
		if (entity == null) {
			log.debug("No s'ha trobat el clau publica (id = " + id + ")");
			throw new NotFoundException(id, ScspCoreClavePublicaEntity.class);
		}
		clauPublicaRepository.delete(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ClauPrivadaDto> clauPrivadaFindByFiltrePaginat(PaginacioParamsDto paginacioParams) {
		log.debug("Consulta de tots les claus privades");
		Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams); 
		Page<ScspCoreClavePrivadaEntity> page = clauPrivadaRepository.findAll(pageable);
		return paginacioHelper.toPaginaDto(page, ClauPrivadaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public ClauPrivadaDto findClauPrivadaById(Long id) throws NotFoundException {
		log.debug("Consulta una clau privada (id = " + id + ")");
		return conversioTipusHelper.convertir(
				clauPrivadaRepository.findById(id).get(),
				ClauPrivadaDto.class);
	}

	@Override
	@Transactional
	public ClauPrivadaDto clauPrivadaCreate(ClauPrivadaDto item) {
		log.debug("Creant una nova clau privada : " + item);
		ScspCoreOrganismoCessionarioEntity organisme = organismeCessionariRepository.getOne(
				item.getOrganisme());
		ScspCoreClavePrivadaEntity entity = ScspCoreClavePrivadaEntity.builder()
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
		log.debug("Actualitzant la clau privada (id = " + item.getId() +
					 ") amb la informació: " + item);
		ScspCoreClavePrivadaEntity entity = clauPrivadaRepository.getOne(item.getId());
		if (entity == null) {
			log.debug("No s'ha trobat la clau privada (id = " + item.getId() + ")");
			throw new NotFoundException(item.getId(), ScspCoreClavePublicaEntity.class);
		}
		ScspCoreOrganismoCessionarioEntity organisme = organismeCessionariRepository.getOne(
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
		log.debug("Esborrant la clau privada (id =" + id + ")");
		ScspCoreClavePrivadaEntity entity = clauPrivadaRepository.getOne(id);
		if (entity == null) {
			log.debug("No s'ha trobat la clau privada (id = " + id + ")");
			throw new NotFoundException(id, ScspCoreClavePublicaEntity.class);
		}
		clauPrivadaRepository.delete(entity);
	}

	@Transactional(readOnly = true)
	@Override
	public List<AutoritatCertificacioDto> autoridadCertificacionFindAll() {
		log.debug("Consulta la llista d'autoritats de certificació SCSP");
		List<ScspCoreEmAutorizacionAutoridadCertEntity> autoritats = scspCoreEmAutorizacionAutoridadCertRepository.findAll(
				Sort.by(Order.asc("nombre")));
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
	@Transactional(readOnly = true)
	public PaginaDto<AutoritatCertificacioDto> autoritatCertificacioFindByFiltrePaginat(
			PaginacioParamsDto paginacioParams) {
		log.debug("Consulta de tots les autoritat de certificació");
		Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams); 
		Page<ScspCoreEmAutorizacionAutoridadCertEntity> page = scspCoreEmAutorizacionAutoridadCertRepository.findAll(pageable);
		return paginacioHelper.toPaginaDto(page, AutoritatCertificacioDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public AutoritatCertificacioDto autoritatCertificacioFindById(Long id) throws NotFoundException {
		log.debug("Consulta una autoritat de certificació (id = " + id + ")");
		return conversioTipusHelper.convertir(
				scspCoreEmAutorizacionAutoridadCertRepository.findById(id),
				AutoritatCertificacioDto.class);
	}

	@Override
	@Transactional
	public AutoritatCertificacioDto autoritatCertificacioCreate(AutoritatCertificacioDto item) {
		log.debug("Creant una nova autoritat de certificació : " + item);
		ScspCoreEmAutorizacionAutoridadCertEntity entity = ScspCoreEmAutorizacionAutoridadCertEntity.builder()
				.codca(item.getCodca())
				.nombre(item.getNombre())
				.build();
		return conversioTipusHelper.convertir(
				scspCoreEmAutorizacionAutoridadCertRepository.save(entity),
				AutoritatCertificacioDto.class);
	}

	@Override
	@Transactional
	public AutoritatCertificacioDto autoritatCertificacioUpdate(AutoritatCertificacioDto item) throws NotFoundException {
		log.debug("Actualitzant la autoritat de certificació (id = " + item.getId() +
					 ") amb la informació: " + item);
		ScspCoreEmAutorizacionAutoridadCertEntity entity = scspCoreEmAutorizacionAutoridadCertRepository.findById(item.getId());
		if (entity == null) {
			log.debug("No s'ha trobat la autoritat de certificació (id = " + item.getId() + ")");
			throw new NotFoundException(item.getId(), ScspCoreClavePublicaEntity.class);
		}
		entity.update(
				item.getCodca(),
				item.getNombre());
		return conversioTipusHelper.convertir(
				entity,
				AutoritatCertificacioDto.class);
	}

	@Override
	@Transactional
	public void autoritatCertificacioDelete(Long id) throws NotFoundException {
		log.debug("Esborrant autoritat de certificació (id =" + id + ")");
		ScspCoreEmAutorizacionAutoridadCertEntity entity = scspCoreEmAutorizacionAutoridadCertRepository.findById(id);
		if (entity == null) {
			log.debug("No s'ha trobat la autoritat de certificació (id = " + id + ")");
			throw new NotFoundException(id, ScspCoreClavePublicaEntity.class);
		}
		scspCoreEmAutorizacionAutoridadCertRepository.delete(entity);
	}

	@Override
	@Transactional
	public void propagateScspPropertiesToDb() {
		propertiesHelper.propagateScspPropertiesToDb();
	}

	private AplicacioDto toAplicacioDto(
			ScspCoreEmAplicacionEntity entity) {
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
		ServeiEntity servei = serveiRepository.getOne(serveiId);
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

}
