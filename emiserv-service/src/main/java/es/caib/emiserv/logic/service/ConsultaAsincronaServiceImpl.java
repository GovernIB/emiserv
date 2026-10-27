/**
 *
 */
package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.BackofficeHelper;
import es.caib.emiserv.logic.helper.ConfiguracioAsincronHelper;
import es.caib.emiserv.logic.helper.PaginacioHelper;
import es.caib.emiserv.logic.helper.PlanificadorAsincronHelper;
import es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaCedentDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaFiltreDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaFiltreEstatEnumDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaSolicitudDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.service.ConsultaAsincronaService;
import es.caib.emiserv.persist.entity.BackofficePeticioEntity;
import es.caib.emiserv.persist.entity.BackofficeSolicitudEntity;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.persist.repository.BackofficePeticioRepository;
import es.caib.emiserv.persist.repository.BackofficeSolicitudRepository;
import es.caib.emiserv.persist.repository.ServeiRutaDestiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Implementació del servei de gestió de les consultes asíncrones processades com a síncrones.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class ConsultaAsincronaServiceImpl implements ConsultaAsincronaService {

	@Autowired
	private BackofficePeticioRepository backofficePeticioRepository;
	@Autowired
	private BackofficeSolicitudRepository backofficeSolicitudRepository;
	@Autowired
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	@Autowired
	private PlanificadorAsincronHelper planificadorAsincronHelper;
	@Autowired
	private ConfiguracioAsincronHelper configuracioAsincronHelper;
	@Autowired
	private BackofficeHelper backofficeHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ConsultaAsincronaDto> findByFiltrePaginat(
			ConsultaAsincronaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		Map<Long, ServeiEntity> serveis = planificadorAsincronHelper.getServeisPerCertificat();
		List<Long> certificats = new ArrayList<Long>();
		for (Map.Entry<Long, ServeiEntity> entry: serveis.entrySet()) {
			if (filtre.getServei() == null || filtre.getServei().equals(entry.getValue().getId())) {
				certificats.add(entry.getKey());
			}
		}
		if (certificats.isEmpty()) {
			return paginacioHelper.getPaginaDtoBuida(ConsultaAsincronaDto.class);
		}
		List<PeticioEstatEnumDto> estats = Arrays.asList(PeticioEstatEnumDto.values());
		if (filtre.getEstat() == ConsultaAsincronaFiltreEstatEnumDto.EN_CURS || filtre.getEstat() == ConsultaAsincronaFiltreEstatEnumDto.PAUSADA) {
			estats = Collections.singletonList(PeticioEstatEnumDto.PENDENT);
		} else if (filtre.getEstat() == ConsultaAsincronaFiltreEstatEnumDto.FINALITZADA) {
			estats = Arrays.asList(PeticioEstatEnumDto.TRAMITADA, PeticioEstatEnumDto.ERROR);
		}
		Map<String, String> mapeigOrdenacio = new HashMap<String, String>();
		mapeigOrdenacio.put("peticioId", "scspPeticionRespuesta.peticionId");
		mapeigOrdenacio.put("dataPeticio", "scspPeticionRespuesta.fechaPeticion");
		mapeigOrdenacio.put("terData", "tempsEstimatRespostaData");
		boolean esNullPeticioId = filtre.getPeticioId() == null || filtre.getPeticioId().isBlank();
		Page<BackofficePeticioEntity> pagina = backofficePeticioRepository.findAsincronesByFiltre(
				certificats,
				filtre.getEstat() == null,
				estats,
				filtre.getEstat() == ConsultaAsincronaFiltreEstatEnumDto.PAUSADA,
				esNullPeticioId,
				esNullPeticioId ? "" : filtre.getPeticioId().trim(),
				paginacioHelper.toSpringDataPageable(paginacioParams, mapeigOrdenacio));
		List<Long> ids = new ArrayList<Long>();
		for (BackofficePeticioEntity peticio: pagina.getContent()) {
			ids.add(peticio.getId());
		}
		Map<Long, ConsultaAsincronaDto> consultes = new LinkedHashMap<Long, ConsultaAsincronaDto>();
		for (BackofficePeticioEntity peticio: pagina.getContent()) {
			consultes.put(peticio.getId(), toDto(peticio, serveis.get(peticio.getScspPeticionRespuesta().getCertificado())));
		}
		if (!ids.isEmpty()) {
			for (Object[] fila: backofficeSolicitudRepository.countByPeticioAndEstat(ids, new Date())) {
				ConsultaAsincronaDto consulta = consultes.get((Long)fila[0]);
				int num = ((Number)fila[2]).intValue();
				consulta.setTotal(consulta.getTotal() + num);
				switch ((BackofficeSolicitudEstatEnumDto)fila[1]) {
				case TRAMITADA:
					consulta.setTramitades(num);
					break;
				case ERROR:
					consulta.setErrors(num);
					break;
				case ENVIANT:
					consulta.setEnviant(num);
					break;
				default:
					consulta.setPendents(num);
					consulta.setReintents(fila[3] != null ? ((Number)fila[3]).intValue() : 0);
				}
			}
			Estimacio estimacio = estimar(serveis);
			for (ConsultaAsincronaDto consulta: consultes.values()) {
				if (!consulta.isFinalitzada()) {
					consulta.setDataFiPrevista(estimacio.dataFiPerPeticio.get(consulta.getId()));
					consulta.setDestinsPausats(estimacio.peticionsAmbDestinsPausats.contains(consulta.getId()));
				}
			}
		}
		return paginacioHelper.toPaginaDto(pagina, ConsultaAsincronaDto.class, peticio -> consultes.get(peticio.getId()));
	}

	@Transactional(readOnly = true)
	@Override
	public List<ConsultaAsincronaSolicitudDto> solicitudFindByConsulta(Long consultaId) {
		List<ConsultaAsincronaSolicitudDto> solicituds = new ArrayList<ConsultaAsincronaSolicitudDto>();
		for (BackofficeSolicitudEntity solicitud: backofficeSolicitudRepository.findByPeticioOrderByIdAsc(getPeticio(consultaId))) {
			ConsultaAsincronaSolicitudDto dto = new ConsultaAsincronaSolicitudDto();
			dto.setId(solicitud.getId());
			dto.setSolicitudId(solicitud.getSolicitudId());
			dto.setEntitatCodi(solicitud.getEntitatCodi());
			dto.setEstat(solicitud.getEstat());
			dto.setIntents(solicitud.getIntents());
			dto.setDataEnviament(solicitud.getDataEnviament());
			dto.setDataProperIntent(solicitud.getDataProperIntent());
			dto.setPeticioDestiId(solicitud.getPeticioDestiId());
			dto.setErrorCodi(solicitud.getErrorCodi());
			dto.setDarrerError(solicitud.getDarrerError());
			solicituds.add(dto);
		}
		return solicituds;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ConsultaAsincronaCedentDto> cedentFindAll() {
		Map<Long, ServeiEntity> serveis = planificadorAsincronHelper.getServeisPerCertificat();
		Map<String, ConsultaAsincronaCedentDto> cedents = new LinkedHashMap<String, ConsultaAsincronaCedentDto>();
		// Rutes dels enrutadors que accepten consultes asíncrones (es poden pausar encara que no tinguin pendents)
		for (Map.Entry<Long, ServeiEntity> entry: serveis.entrySet()) {
			ServeiEntity servei = entry.getValue();
			if (servei.isEnrutador() && servei.isAsyncSincron()) {
				for (ServeiRutaDestiEntity ruta: serveiRutaDestiRepository.findByServeiOrderByOrdreAsc(servei)) {
					getCedent(cedents, entry.getKey(), servei, ruta.getEntitatCodi());
				}
			}
		}
		Estimacio estimacio = estimar(serveis);
		for (Map.Entry<Desti, long[]> entry: estimacio.pendentsPerDesti.entrySet()) {
			ServeiEntity servei = serveis.get(entry.getKey().certificado);
			if (servei != null) {
				ConsultaAsincronaCedentDto cedent = getCedent(cedents, entry.getKey().certificado, servei, entry.getKey().entitatCodi);
				cedent.setPendents(entry.getValue()[0]);
				cedent.setEnviant(entry.getValue()[1]);
				cedent.setDataFiPrevista(cedent.isPausada() ? null : estimacio.dataFiPerDesti.get(entry.getKey()));
			}
		}
		List<ConsultaAsincronaCedentDto> llista = new ArrayList<ConsultaAsincronaCedentDto>(cedents.values());
		// Primer els destins amb sol·licituds per enviar o pausats
		llista.sort(Comparator.comparing((ConsultaAsincronaCedentDto c) -> c.getPendents() + c.getEnviant() == 0 && !c.isPausada()).
				thenComparing(ConsultaAsincronaCedentDto::getServeiCodi).
				thenComparing(c -> c.getEntitatCodi() != null ? c.getEntitatCodi() : ""));
		return llista;
	}

	@Transactional
	@Override
	public void pausar(Long consultaId) {
		BackofficePeticioEntity peticio = getPeticioEnCurs(consultaId);
		peticio.updatePausada(true);
		log.info("Consulta asíncrona pausada (idPeticion=" + peticio.getScspPeticionRespuesta().getPeticionId() + ", usuari=" + getUsuari() + ")");
	}

	@Transactional
	@Override
	public void reprendre(Long consultaId) {
		BackofficePeticioEntity peticio = getPeticioEnCurs(consultaId);
		peticio.updatePausada(false);
		log.info("Consulta asíncrona represa (idPeticion=" + peticio.getScspPeticionRespuesta().getPeticionId() + ", usuari=" + getUsuari() + ")");
	}

	@Transactional
	@Override
	public int reintentar(Long consultaId) {
		BackofficePeticioEntity peticio = getPeticioEnCurs(consultaId);
		int num = 0;
		for (BackofficeSolicitudEntity solicitud: backofficeSolicitudRepository.findByPeticioOrderByIdAsc(peticio)) {
			if (isReintentable(solicitud)) {
				solicitud.reiniciarIntents();
				num++;
			}
		}
		backofficeHelper.recomptarPeticio(peticio, null);
		log.info("Reintent de les sol·licituds d'una consulta asíncrona (idPeticion=" + peticio.getScspPeticionRespuesta().getPeticionId() + ", " +
				"solicituds=" + num + ", usuari=" + getUsuari() + ")");
		return num;
	}

	@Transactional
	@Override
	public void reintentarSolicitud(Long consultaId, Long solicitudId) {
		BackofficePeticioEntity peticio = getPeticioEnCurs(consultaId);
		BackofficeSolicitudEntity solicitud = backofficeSolicitudRepository.findById(solicitudId).orElse(null);
		if (solicitud == null || !solicitud.getPeticio().getId().equals(peticio.getId())) {
			throw new NotFoundException(solicitudId, BackofficeSolicitudEntity.class);
		}
		if (!isReintentable(solicitud)) {
			throw new ValidationException("La sol·licitud no té cap error ni cap reintent programat (id=" + solicitudId + ")");
		}
		solicitud.reiniciarIntents();
		backofficeHelper.recomptarPeticio(peticio, null);
		log.info("Reintent d'una sol·licitud d'una consulta asíncrona (idPeticion=" + peticio.getScspPeticionRespuesta().getPeticionId() + ", " +
				"idSolicitud=" + solicitud.getSolicitudId() + ", usuari=" + getUsuari() + ")");
	}

	@Transactional
	@Override
	public int cancelar(Long consultaId) {
		BackofficePeticioEntity peticio = getPeticioEnCurs(consultaId);
		String usuari = getUsuari();
		int num = 0;
		for (BackofficeSolicitudEntity solicitud: backofficeSolicitudRepository.findByPeticioOrderByIdAsc(peticio)) {
			// Si s'està enviant, l'enviament no es podrà desar (versió de la sol·licitud) i queda cancel·lada
			if (!solicitud.isFinalitzada()) {
				solicitud.finalitzarAmbError(
						configuracioAsincronHelper.getCodiCancelacio(),
						"Consulta cancel·lada" + (usuari != null ? " per l'usuari " + usuari : ""));
				num++;
			}
		}
		peticio.updatePausada(false);
		backofficeHelper.recomptarPeticio(peticio, null);
		log.info("Consulta asíncrona cancel·lada (idPeticion=" + peticio.getScspPeticionRespuesta().getPeticionId() + ", " +
				"solicituds=" + num + ", usuari=" + usuari + ")");
		return num;
	}

	@Transactional
	@Override
	public void updateCedentPausat(Long rutaId, boolean pausada) {
		ServeiRutaDestiEntity ruta = serveiRutaDestiRepository.findById(rutaId).orElseThrow(
				() -> new NotFoundException(rutaId, ServeiRutaDestiEntity.class));
		ruta.updatePausada(pausada);
		log.info("Enviaments al cedent " + (pausada ? "pausats" : "represos") + " (" +
				"servei=" + ruta.getServei().getCodi() + ", entitat=" + ruta.getEntitatCodi() + ", usuari=" + getUsuari() + ")");
	}

	/**
	 * Estimació de la finalització: les sol·licituds de cada destí s'envien per ordre d'arribada de
	 * les peticions al ritme del destí, de manera que una consulta acaba quan s'han enviat les seves
	 * sol·licituds i les de les consultes anteriors al mateix destí. Les consultes i els destins
	 * pausats no avancen (no tenen data prevista).
	 */
	Estimacio estimar(Map<Long, ServeiEntity> serveis) {
		Estimacio estimacio = new Estimacio();
		long ara = System.currentTimeMillis();
		Map<Desti, Long> acumulat = new HashMap<Desti, Long>();
		Map<Desti, Boolean> destiPausat = new HashMap<Desti, Boolean>();
		Set<Long> peticionsSenseData = new HashSet<Long>();
		for (Object[] fila: backofficeSolicitudRepository.findCuaPerEnviar()) {
			Long peticioId = (Long)fila[0];
			boolean peticioPausada = Boolean.TRUE.equals(fila[1]);
			Desti desti = new Desti((Long)fila[2], (String)fila[3]);
			boolean enviant = BackofficeSolicitudEstatEnumDto.ENVIANT.equals(fila[4]);
			long num = ((Number)fila[5]).longValue();
			Date properIntent = (Date)fila[6];
			ServeiEntity servei = serveis.get(desti.certificado);
			if (servei == null) {
				continue;
			}
			long[] pendents = estimacio.pendentsPerDesti.computeIfAbsent(desti, d -> new long[2]);
			pendents[enviant ? 1 : 0] += num;
			if (peticioPausada) {
				peticionsSenseData.add(peticioId);
				continue;
			}
			boolean pausat = destiPausat.computeIfAbsent(desti, d -> isRutaPausada(servei, d.entitatCodi));
			if (pausat) {
				peticionsSenseData.add(peticioId);
				estimacio.peticionsAmbDestinsPausats.add(peticioId);
				continue;
			}
			long cua = acumulat.merge(desti, num, Long::sum);
			long fi = ara + cua * configuracioAsincronHelper.getTempsPerSolicitudMs(servei);
			if (properIntent != null) {
				fi = Math.max(fi, properIntent.getTime() + configuracioAsincronHelper.getTempsPerSolicitudMs(servei));
			}
			Date dataFi = new Date(fi);
			estimacio.dataFiPerDesti.merge(desti, dataFi, (a, b) -> a.after(b) ? a : b);
			estimacio.dataFiPerPeticio.merge(peticioId, dataFi, (a, b) -> a.after(b) ? a : b);
		}
		for (Long peticioId: peticionsSenseData) {
			estimacio.dataFiPerPeticio.remove(peticioId);
		}
		return estimacio;
	}

	private ConsultaAsincronaCedentDto getCedent(
			Map<String, ConsultaAsincronaCedentDto> cedents,
			Long certificado,
			ServeiEntity servei,
			String entitatCodi) {
		return cedents.computeIfAbsent(certificado + ":" + entitatCodi, clau -> {
			ConsultaAsincronaCedentDto cedent = new ConsultaAsincronaCedentDto();
			cedent.setServeiCodi(servei.getCodi());
			cedent.setServeiNom(servei.getNom());
			cedent.setEntitatCodi(entitatCodi);
			cedent.setIntervalMs(configuracioAsincronHelper.getIntervalCedentMs(servei));
			cedent.setConcurrencia(configuracioAsincronHelper.getConcurrenciaCedent(servei));
			if (entitatCodi != null && servei.isEnrutador()) {
				ServeiRutaDestiEntity ruta = serveiRutaDestiRepository.findPrimeraByServeiAndEntitatCodi(servei, entitatCodi);
				if (ruta != null) {
					cedent.setRutaId(ruta.getId());
					cedent.setDestiTipus(ruta.getDestiTipus());
					cedent.setPausada(ruta.isPausada());
				}
			}
			return cedent;
		});
	}

	private boolean isRutaPausada(ServeiEntity servei, String entitatCodi) {
		if (entitatCodi == null || !servei.isEnrutador()) {
			return false;
		}
		ServeiRutaDestiEntity ruta = serveiRutaDestiRepository.findPrimeraByServeiAndEntitatCodi(servei, entitatCodi);
		return ruta != null && ruta.isPausada();
	}

	private static boolean isReintentable(BackofficeSolicitudEntity solicitud) {
		return BackofficeSolicitudEstatEnumDto.ERROR.equals(solicitud.getEstat()) ||
				(BackofficeSolicitudEstatEnumDto.PENDENT.equals(solicitud.getEstat()) && solicitud.getDataProperIntent() != null);
	}

	private ConsultaAsincronaDto toDto(BackofficePeticioEntity peticio, ServeiEntity servei) {
		ConsultaAsincronaDto dto = new ConsultaAsincronaDto();
		dto.setId(peticio.getId());
		dto.setPeticioId(peticio.getScspPeticionRespuesta().getPeticionId());
		dto.setDataPeticio(peticio.getScspPeticionRespuesta().getFechaPeticion());
		dto.setTerData(peticio.getTempsEstimatRespostaData());
		dto.setEstat(peticio.getEstat());
		dto.setPausada(peticio.isPausada());
		if (servei != null) {
			dto.setServeiCodi(servei.getCodi());
			dto.setServeiNom(servei.getNom());
		}
		return dto;
	}

	private BackofficePeticioEntity getPeticio(Long consultaId) {
		BackofficePeticioEntity peticio = backofficePeticioRepository.findById(consultaId).orElse(null);
		if (peticio == null || peticio.getScspPeticionRespuesta().isSincrona()) {
			throw new NotFoundException(consultaId, BackofficePeticioEntity.class);
		}
		return peticio;
	}

	/** Les accions només es poden fer sobre consultes que encara no han finalitzat. */
	private BackofficePeticioEntity getPeticioEnCurs(Long consultaId) {
		BackofficePeticioEntity peticio = getPeticio(consultaId);
		if (!PeticioEstatEnumDto.PENDENT.equals(peticio.getEstat())) {
			throw new ValidationException("La consulta ja ha finalitzat (id=" + consultaId + ")");
		}
		return peticio;
	}

	private static String getUsuari() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null ? auth.getName() : null;
	}

	static class Estimacio {
		final Map<Long, Date> dataFiPerPeticio = new HashMap<Long, Date>();
		final Map<Desti, Date> dataFiPerDesti = new HashMap<Desti, Date>();
		/** Sol·licituds pendents i en enviament de cada destí. */
		final Map<Desti, long[]> pendentsPerDesti = new LinkedHashMap<Desti, long[]>();
		final Set<Long> peticionsAmbDestinsPausats = new HashSet<Long>();
	}

	static class Desti {
		final Long certificado;
		final String entitatCodi;
		Desti(Long certificado, String entitatCodi) {
			this.certificado = certificado;
			this.entitatCodi = entitatCodi;
		}
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Desti)) return false;
			Desti desti = (Desti)o;
			return Objects.equals(certificado, desti.certificado) && Objects.equals(entitatCodi, desti.entitatCodi);
		}
		@Override
		public int hashCode() {
			return Objects.hash(certificado, entitatCodi);
		}
	}

}
