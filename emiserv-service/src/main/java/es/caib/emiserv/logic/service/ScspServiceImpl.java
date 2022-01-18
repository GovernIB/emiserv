/**
 * 
 */
package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.helper.PaginacioHelper;
import es.caib.emiserv.logic.helper.PaginacioHelper.Converter;
import es.caib.emiserv.logic.helper.PropertiesHelper;
import es.caib.emiserv.logic.intf.dto.*;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.service.ScspService;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.scsp.*;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.scsp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
	private ScspCoreEmisorCertificadoRepository scspCoreEmisorCertificadoRepository;
	@Autowired
	private ScspCoreClavePrivadaRepository clauPrivadaRepository;
	@Autowired
	private ScspCoreClavePublicaRepository clauPublicaRepository;
	@Autowired
	private ScspCoreOrganismoCessionarioRepository scspCoreOrganismoCessionarioRepository;
	@Autowired
	private ScspCoreModuloRepository scspCoreModuloRepository;
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
		Optional<ScspCoreEmAplicacionEntity> entity = scspCoreEmAutorizacionAplicacionRepository.findById(
				aplicacio.getId());
		if (!entity.isPresent()) {
			throw new NotFoundException(
					aplicacio.getId(),
					ScspCoreEmAplicacionEntity.class);
		}
		entity.get().update(
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
		Optional<ScspCoreEmAplicacionEntity> entity = scspCoreEmAutorizacionAplicacionRepository.findById(id);
		if (!entity.isPresent()) {
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
		Optional<ScspCoreEmAplicacionEntity> entity = scspCoreEmAutorizacionAplicacionRepository.findById(id);
		if (!entity.isPresent()) {
			throw new NotFoundException(
					id,
					ScspCoreEmAplicacionEntity.class);
		}
		return toAplicacioDto(entity.get());
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
		Optional<ScspCoreEmAutorizacionOrganismoEntity> entity = scspCoreEmAutorizacionOrganismoRepository.findById(
				organismo.getId());
		if (!entity.isPresent()) {
			throw new NotFoundException(
					organismo.getId(),
					ScspCoreEmAutorizacionOrganismoEntity.class);
		}
		entity.get().update(
				organismo.getCif(),
				organismo.getNom(),
				organismo.getDataAlta(),
				organismo.getDataBaixa());
	}

	@Transactional
	@Override
	public void organismeDelete(Long id) {
		log.debug("Esborrant l'organisme (id=" + id + ")");
		Optional<ScspCoreEmAutorizacionOrganismoEntity> entity = scspCoreEmAutorizacionOrganismoRepository.findById(id);
		if (!entity.isPresent()) {
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
		Optional<ScspCoreEmAutorizacionOrganismoEntity> entity = scspCoreEmAutorizacionOrganismoRepository.findById(id);
		if (!entity.isPresent()) {
			throw new NotFoundException(
					id,
					ScspCoreEmAutorizacionOrganismoEntity.class);
		}
		return toOrganismeDto(entity.get());
	}

	@Transactional(readOnly = true)
	@Override
	public OrganismeDto organismeFindByCif(String cif) throws NotFoundException {
		ScspCoreEmAutorizacionOrganismoEntity organisme = scspCoreEmAutorizacionOrganismoRepository.findByCif(cif);
		if (organisme != null) {
			return toOrganismeDto(organisme);
		} else {
			return null;
		}
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
				(filtre.getNom() != null && !filtre.getNom().isEmpty()) ? filtre.getNom(): "",
				(filtre.getCif() == null || filtre.getCif().isEmpty()),
				(filtre.getCif() != null && !filtre.getCif().isEmpty()) ? filtre.getCif(): "",
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
	public OrganismeDto organismeCessionariCreate(
			OrganismeDto organismo) {
		log.debug("Creant un nou organisme cessionari (organismo=" + organismo + ")");
		ScspCoreOrganismoCessionarioEntity entity = ScspCoreOrganismoCessionarioEntity.getBuilder(
				organismo.getNom(),
				organismo.getCif(),
				organismo.getDataAlta()).
				dataBaixa(organismo.getDataBaixa()).
				bloquejat(organismo.isBloquejat()).
				codiUnitatTramitadora(organismo.getCodiUnitatTramitadora()).
				build();
		return toOrganismeCessionariDto(
				scspCoreOrganismoCessionarioRepository.save(entity));
	}

	@Transactional
	@Override
	public void organismeCessionariUpdate(
			OrganismeDto organismo) {
		log.debug("Modificant l'organisme cessionari (organismo=" + organismo + ")");
		Optional<ScspCoreOrganismoCessionarioEntity> entity = scspCoreOrganismoCessionarioRepository.findById(
				organismo.getId());
		if (!entity.isPresent()) {
			throw new NotFoundException(
					organismo.getId(),
					ScspCoreOrganismoCessionarioEntity.class);
		}
		entity.get().update(
				organismo.getNom(),
				organismo.getCif(),
				organismo.getDataAlta(),
				organismo.getDataBaixa(),
				organismo.isBloquejat(),
				organismo.getCodiUnitatTramitadora());
	}

	@Transactional
	@Override
	public void organismeCessionariDelete(Long id) {
		log.debug("Esborrant l'organisme cessionari (id=" + id + ")");
		Optional<ScspCoreOrganismoCessionarioEntity> entity = scspCoreOrganismoCessionarioRepository.findById(id);
		if (!entity.isPresent()) {
			throw new NotFoundException(
					id,
					ScspCoreOrganismoCessionarioEntity.class);
		}
		scspCoreOrganismoCessionarioRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	@Override
	public OrganismeDto organismeCessionariFindById(Long id) {
		log.debug("Consultant l'organisme cessionari per id (id=" + id + ")");
		Optional<ScspCoreOrganismoCessionarioEntity> entity = scspCoreOrganismoCessionarioRepository.findById(id);
		if (!entity.isPresent()) {
			throw new NotFoundException(
					id,
					ScspCoreOrganismoCessionarioEntity.class);
		}
		return toOrganismeCessionariDto(entity.get());
	}

	@Transactional(readOnly = true)
	@Override
	public OrganismeDto organismeCessionariFindByCif(String cif) throws NotFoundException {
		ScspCoreOrganismoCessionarioEntity organisme = scspCoreOrganismoCessionarioRepository.findByCif(cif);
		if (organisme != null) {
			return toOrganismeCessionariDto(organisme);
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<OrganismeDto> organismeCessionariFindByFiltrePaginat(
			OrganismeFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		log.debug("Consultant organismes cessionaris amb paginació (" +
				"filtre=" + filtre + ", " +
				"paginacioParams=" + paginacioParams + ")");
		Page<ScspCoreOrganismoCessionarioEntity> page = scspCoreOrganismoCessionarioRepository.findByFiltrePaginat(
				(filtre == null || filtre.getNom() == null || filtre.getNom().isEmpty()),
				filtre != null && filtre.getNom() != null ? filtre.getNom() : "",
				(filtre == null || filtre.getCif() == null || filtre.getCif().isEmpty()),
				filtre != null && filtre.getCif() != null ? filtre.getCif() : "",
				paginacioHelper.toSpringDataPageable(paginacioParams));
		return paginacioHelper.toPaginaDto(
				page,
				OrganismeDto.class,
				new Converter<ScspCoreOrganismoCessionarioEntity, OrganismeDto>() {
					@Override
					public OrganismeDto convert(ScspCoreOrganismoCessionarioEntity source) {
						return toOrganismeCessionariDto(source);
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
		Optional<ScspCoreEmAplicacionEntity> aplicacion = scspCoreEmAutorizacionAplicacionRepository.findById(
				autoritzacio.getAplicacioId());
		if (!aplicacion.isPresent()) {
			throw new NotFoundException(
					autoritzacio.getAplicacioId(),
					ScspCoreEmAplicacionEntity.class);
		}
		Optional<ScspCoreEmAutorizacionOrganismoEntity> organismo = scspCoreEmAutorizacionOrganismoRepository.findById(
				autoritzacio.getOrganismeId());
		if (!organismo.isPresent()) {
			throw new NotFoundException(
					autoritzacio.getOrganismeId(),
					ScspCoreEmAutorizacionOrganismoEntity.class);
		}
		ScspCoreEmAutorizacionCertificadoEntity entity = ScspCoreEmAutorizacionCertificadoEntity.getBuilder(
				servicio,
				aplicacion.get(),
				organismo.get()).
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
		Optional<ScspCoreEmAutorizacionCertificadoEntity> entity = scspCoreEmAutorizacionCertificadoRepository.findById(
				autoritzacio.getId());
		if (!entity.isPresent()) {
			throw new NotFoundException(
					autoritzacio.getId(),
					ScspCoreEmAutorizacionCertificadoEntity.class);
		}
		ScspCoreServicioEntity servicio = getScspCoreServicioPerServeiId(
				autoritzacio.getServeiId());
		Optional<ScspCoreEmAplicacionEntity> aplicacion = scspCoreEmAutorizacionAplicacionRepository.findById(
				autoritzacio.getAplicacioId());
		if (!aplicacion.isPresent()) {
			throw new NotFoundException(
					autoritzacio.getAplicacioId(),
					ScspCoreEmAplicacionEntity.class);
		}
		Optional<ScspCoreEmAutorizacionOrganismoEntity> organismo = scspCoreEmAutorizacionOrganismoRepository.findById(
				autoritzacio.getOrganismeId());
		if (!organismo.isPresent()) {
			throw new NotFoundException(
					autoritzacio.getOrganismeId(),
					ScspCoreEmAutorizacionOrganismoEntity.class);
		}
		entity.get().update(
				servicio,
				aplicacion.get(),
				organismo.get(),
				autoritzacio.getDataAlta(),
				autoritzacio.getDataBaixa());
	}

	@Transactional
	@Override
	public void autoritzacioDelete(
			Long id) throws NotFoundException {
		log.debug("Esborrant l'autorització (id=" + id + ")");
		Optional<ScspCoreEmAutorizacionCertificadoEntity> entity = scspCoreEmAutorizacionCertificadoRepository.findById(
				id);
		if (!entity.isPresent()) {
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
		Optional<ScspCoreEmAutorizacionCertificadoEntity> entity = scspCoreEmAutorizacionCertificadoRepository.findById(
				id);
		if (!entity.isPresent()) {
			throw new NotFoundException(
					id,
					ScspCoreEmAutorizacionCertificadoEntity.class);
		}
		return toAutoritzacioDto(entity.get());
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
		ScspCoreEmAutorizacionOrganismoEntity organisme = null;
		if (filtre.getOrganismeId() != null) {
			Optional<ScspCoreEmAutorizacionOrganismoEntity> organismeOpt = scspCoreEmAutorizacionOrganismoRepository.findById(
					filtre.getOrganismeId());
			if (organismeOpt.isPresent()) {
				organisme = organismeOpt.get();
			}
		}
		return paginacioHelper.toPaginaDto(
				scspCoreEmAutorizacionCertificadoRepository.findByFiltre(
						servicio, 
						filtre.getAplicacio() == null || filtre.getAplicacio().isEmpty(), 
						(filtre.getAplicacio() != null && !filtre.getAplicacio().isEmpty()) ? filtre.getAplicacio() : "", 
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
		List<ScspCoreEmisorCertificadoEntity> emissors = scspCoreEmisorCertificadoRepository.findAll();
		List<EmisorDto> resposta = new ArrayList<EmisorDto>();
		for (ScspCoreEmisorCertificadoEntity emissor: emissors) {
			EmisorDto e = new EmisorDto();
			e.setId(emissor.getId());
			e.setCif(emissor.getCif());
			e.setNom(emissor.getNombre());
			resposta.add(e);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ClauPublicaDto> clauPublicaFindAll() {
		log.debug("Consulta la llista de claus públiques SCSP");
		return conversioTipusHelper.convertirList(
				clauPublicaRepository.findAll(),
				ClauPublicaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ClauPrivadaDto> clauPrivadaFindAll() {
		log.debug("Consulta la llista de claus privades SCSP");
		return conversioTipusHelper.convertirList(
				clauPrivadaRepository.findAll(),
				ClauPrivadaDto.class);
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
		List<ScspCoreOrganismoCessionarioEntity> llista = scspCoreOrganismoCessionarioRepository.findAll();
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
		Optional<ScspCoreClavePublicaEntity> entity = clauPublicaRepository.findById(item.getId());
		if (!entity.isPresent()) {
			log.debug("No s'ha trobat el clau publica (id = " + item.getId() + ")");
			throw new NotFoundException(item.getId(), ScspCoreClavePublicaEntity.class);
		}
		entity.get().update(
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
		Optional<ScspCoreClavePublicaEntity> entity = clauPublicaRepository.findById(id);
		if (!entity.isPresent()) {
			log.debug("No s'ha trobat el clau publica (id = " + id + ")");
			throw new NotFoundException(id, ScspCoreClavePublicaEntity.class);
		}
		clauPublicaRepository.delete(entity.get());
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
		ScspCoreOrganismoCessionarioEntity organisme = scspCoreOrganismoCessionarioRepository.getOne(
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
		Optional<ScspCoreClavePrivadaEntity> entity = clauPrivadaRepository.findById(item.getId());
		if (!entity.isPresent()) {
			log.debug("No s'ha trobat la clau privada (id = " + item.getId() + ")");
			throw new NotFoundException(item.getId(), ScspCoreClavePublicaEntity.class);
		}
		entity.get().update(
				item.getAlies(),
				item.getNom(),
				item.getPassword(),
				item.getNumSerie(),
				item.getDataBaixa(),
				item.getDataAlta(),
				item.getInteroperabilitat(),
				scspCoreOrganismoCessionarioRepository.getOne(item.getOrganisme()));
		return conversioTipusHelper.convertir(
				entity.get(),
				ClauPrivadaDto.class);
	}

	@Override
	@Transactional
	public void clauPrivadaDelete(Long id) throws NotFoundException {
		log.debug("Esborrant la clau privada (id =" + id + ")");
		Optional<ScspCoreClavePrivadaEntity> entity = clauPrivadaRepository.findById(id);
		if (!entity.isPresent()) {
			log.debug("No s'ha trobat la clau privada (id = " + id + ")");
			throw new NotFoundException(id, ScspCoreClavePublicaEntity.class);
		}
		clauPrivadaRepository.delete(entity.get());
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

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ScspModulDto> getScspModuls(PaginacioParamsDto paginacioParams) {
		log.debug("Obtenint el llistat de mòduls SCSP");
		Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams);
		Page<ScspCoreModuloEntity> page = scspCoreModuloRepository.findAll(pageable);
		return paginacioHelper.toPaginaDto(page, ScspModulDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public ScspModulDto getScspModul(String nom) throws NotFoundException {
		log.debug("Obtenint el mòdul SCSP amb nom: {}", nom);
		ScspCoreModuloEntity modul = scspCoreModuloRepository.getOne(nom);
		if (modul == null) {
			log.debug("No s'ha trobat el mòdul scsp (nom = {})", nom);
			throw new NotFoundException(nom, ScspCoreModuloEntity.class);
		}
		return conversioTipusHelper.convertir(modul, ScspModulDto.class);
	}

	@Override
	@Transactional
	public void updateScspModul(ScspModulDto modulDto) throws NotFoundException {
		log.debug("Obtenint el mòdul SCSP amb nom: {}", modulDto.getNom());
		ScspCoreModuloEntity modul = scspCoreModuloRepository.getOne(modulDto.getNom());
		if (modul == null) {
			log.debug("No s'ha trobat el mòdul scsp (nom = {})", modulDto.getNom());
			throw new NotFoundException(modulDto.getNom(), ScspCoreModuloEntity.class);
		}
		modul.update(modulDto.isActiuEntrada(), modulDto.isActiuSortida());
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

	private OrganismeDto toOrganismeCessionariDto(
			ScspCoreOrganismoCessionarioEntity entity) {
		OrganismeDto dto = new OrganismeDto();
		dto.setId(entity.getId());
		dto.setCif(entity.getCif());
		dto.setNom(entity.getNom());
		dto.setDataAlta(entity.getDataAlta());
		dto.setDataBaixa(entity.getDataBaixa());
		dto.setBloquejat(entity.isBloquejat());
		dto.setCodiUnitatTramitadora(entity.getCodiUnitatTramitadora());
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
		Optional<ServeiEntity> servei = serveiRepository.findById(serveiId);
		if (!servei.isPresent()) {
			throw new NotFoundException(
					serveiId,
					ServeiEntity.class);
		}
		ScspCoreServicioEntity servicio = scspCoreServicioRepository.findByCodigoCertificado(
				servei.get().getCodi());
		if (servicio == null) {
			throw new NotFoundException(
					servei.get().getCodi(),
					ScspCoreServicioEntity.class);
		}
		return servicio;
	}

}
