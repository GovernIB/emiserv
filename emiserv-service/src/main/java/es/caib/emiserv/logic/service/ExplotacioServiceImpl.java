/**
 * 
 */
package es.caib.emiserv.logic.service;

import es.caib.comanda.model.server.monitoring.DimensioDesc;
import es.caib.comanda.model.server.monitoring.IndicadorDesc;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.comanda.ms.estadistica.helper.EstadisticaHelper;
import es.caib.emiserv.client.comu.ServeiTipus;
import es.caib.emiserv.client.dadesobertes.DadesObertesResposta;
import es.caib.emiserv.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.emiserv.logic.intf.dto.*;
import es.caib.emiserv.logic.intf.dto.CarregaDto.CarregaDetailedCountDto;
import es.caib.emiserv.logic.intf.service.ExplotacioService;
import es.caib.emiserv.persist.entity.OpenDataEntity;
import es.caib.emiserv.persist.repository.EntitatRepository;
import es.caib.emiserv.persist.repository.OpenDataRepository;
import es.caib.emiserv.persist.repository.RedireccioPeticioRepository;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreEmisorCertificadoRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreTransmisionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static es.caib.comanda.model.server.monitoring.Format.LONG;
import static es.caib.emiserv.logic.service.ExplotacioServiceImpl.DimEnum.*;
import static es.caib.emiserv.logic.service.ExplotacioServiceImpl.IndEnum.PET_ERR;
import static es.caib.emiserv.logic.service.ExplotacioServiceImpl.IndEnum.PET_OK;
import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Implementació del servei de gestió de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class ExplotacioServiceImpl implements ExplotacioService {

	@Autowired
	private ScspCoreTransmisionRepository scspCoreTransmisionRepository;
	@Autowired
	private ScspCoreEmisorCertificadoRepository scspCoreEmisorCertificadoRepository;
	@Autowired
	private RedireccioPeticioRepository redireccioPeticioRepository;
	@Autowired
	private OpenDataRepository openDataRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private ServeiRepository serveiRepository;

//	private List<CarregaDto> carreguesAny;
//	private List<CarregaDto> carreguesMes;
//	private List<CarregaDto> carreguesDia;
//	private List<CarregaDto> carreguesHora;
//	private List<CarregaDto> carreguesMinut;


	@Override
	@Transactional(readOnly = true)
	public List<InformeGeneralEstatDto> informeGeneralEstat(
			Date dataInici, 
			Date dataFi,
			ServeiTipusEnumDto tipusPeticio) {
		
		log.debug("Consulta de dades per l'informe general d'estat (dataInici=" + dataInici + 
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

		List<InformeGeneralEstatDto> informe = new ArrayList<>();
		
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

	@Override
	@Transactional(readOnly = true)
	public List<DadesObertesRespostaConsulta> findOpenData(String entitatNif, Date dataInici, Date dataFi, String procedimentCodi, String serveiCodi) {
		log.debug("Consultant informació per opendata (" +
				"entitatNif=" + entitatNif + ", " +
				"dataInici=" + dataInici + ", " +
				"dataFi" + dataFi + ", " +
				"procedimentCodi=" + procedimentCodi + ", " +
				"serveiCodi=" + serveiCodi + ")");
		List<DadesObertesRespostaConsulta> openData = new ArrayList<>();
		List<DadesObertesRespostaConsulta> openDataBackoffice = scspCoreTransmisionRepository.findByOpendata(
				isBlank(entitatNif),
				!isBlank(entitatNif) ? entitatNif : null,
				isBlank(procedimentCodi),
				!isBlank(procedimentCodi) ? procedimentCodi : null,
				isBlank(serveiCodi),
				!isBlank(serveiCodi) ? serveiCodi : null,
				dataInici == null,
				dataInici,
				dataFi == null,
				dataFi);
		openData.addAll(openDataBackoffice);
		List<DadesObertesRespostaConsulta> openDataEnrutador = redireccioPeticioRepository.findByOpendata(
				isBlank(entitatNif),
				!isBlank(entitatNif) ? entitatNif : null,
				isBlank(procedimentCodi),
				!isBlank(procedimentCodi) ? procedimentCodi : null,
				isBlank(serveiCodi),
				!isBlank(serveiCodi) ? serveiCodi : null,
				dataInici == null,
				dataInici,
				dataFi == null,
				dataFi);
		openData.addAll(openDataEnrutador);
		return openData;
	}

    @Override
    public DadesObertesResposta findOpenDataV2(ConsultaOpenDataDto consultaOpenDataDto) {
		var sdf = new SimpleDateFormat("yyyy-MM-dd");

		var numElements = openDataRepository.countByFiltre(
				isBlank(consultaOpenDataDto.getEntitatNif()),
				!isBlank(consultaOpenDataDto.getEntitatNif()) ? consultaOpenDataDto.getEntitatNif() : null,
				isBlank(consultaOpenDataDto.getProcedimentCodi()),
				!isBlank(consultaOpenDataDto.getProcedimentCodi()) ? consultaOpenDataDto.getProcedimentCodi() : null,
				isBlank(consultaOpenDataDto.getServeiCodi()),
				!isBlank(consultaOpenDataDto.getServeiCodi()) ? consultaOpenDataDto.getServeiCodi() : null,
				consultaOpenDataDto.getTipus() == null,
				consultaOpenDataDto.getTipus() != null && ServeiTipus.BACKOFFICE.equals(consultaOpenDataDto.getTipus()),
				consultaOpenDataDto.getDataInici() == null,
				consultaOpenDataDto.getDataInici(),
				consultaOpenDataDto.getDataFi() == null,
				consultaOpenDataDto.getDataFi());
		var dades = openDataRepository.findByFiltre(
				isBlank(consultaOpenDataDto.getEntitatNif()),
				!isBlank(consultaOpenDataDto.getEntitatNif()) ? consultaOpenDataDto.getEntitatNif() : null,
				isBlank(consultaOpenDataDto.getProcedimentCodi()),
				!isBlank(consultaOpenDataDto.getProcedimentCodi()) ? consultaOpenDataDto.getProcedimentCodi() : null,
				isBlank(consultaOpenDataDto.getServeiCodi()),
				!isBlank(consultaOpenDataDto.getServeiCodi()) ? consultaOpenDataDto.getServeiCodi() : null,
				consultaOpenDataDto.getTipus() == null,
				consultaOpenDataDto.getTipus() != null && ServeiTipus.BACKOFFICE.equals(consultaOpenDataDto.getTipus()),
				consultaOpenDataDto.getDataInici() == null,
				consultaOpenDataDto.getDataInici(),
				consultaOpenDataDto.getDataFi() == null,
				consultaOpenDataDto.getDataFi(),
				PageRequest.of(consultaOpenDataDto.getPagina(), consultaOpenDataDto.getMida()));

		Integer totalPagines = (numElements.intValue() + consultaOpenDataDto.getMida() - 1)/consultaOpenDataDto.getMida();
		String nextUrl = null;
		if (totalPagines.intValue() > consultaOpenDataDto.getPagina().intValue() + 1) {
			nextUrl = consultaOpenDataDto.getAppPath();
			nextUrl += "?dataInici=" + sdf.format(consultaOpenDataDto.getDataInici());
			nextUrl += "&dataFi=" + sdf.format(consultaOpenDataDto.getDataFi());
			nextUrl += !isBlank(consultaOpenDataDto.getEntitatNif()) ? "&entitatNif=" + consultaOpenDataDto.getEntitatNif() : "";
			nextUrl += !isBlank(consultaOpenDataDto.getProcedimentCodi()) ? "&procedimentCodi=" + consultaOpenDataDto.getProcedimentCodi() : "";
			nextUrl += !isBlank(consultaOpenDataDto.getServeiCodi()) ? "&serveiCodi=" + consultaOpenDataDto.getServeiCodi() : "";
			nextUrl += consultaOpenDataDto.getTipus() != null ? "&tipus=" + (ServeiTipus.BACKOFFICE.equals(consultaOpenDataDto.getTipus()) ? ServeiTipus.BACKOFFICE.name() : ServeiTipus.ENRUTADOR.name()) : "";
			nextUrl += "&pagina=" + (consultaOpenDataDto.getPagina() + 1);
			nextUrl += "&mida=" + consultaOpenDataDto.getMida();
		}

		return DadesObertesResposta.builder()
				.totalElements(numElements)
				.paginaActual(consultaOpenDataDto.getPagina() + 1)
				.totalPagines(totalPagines)
				.properaPagina(nextUrl)
				.dades(dades != null ? dades.stream().map(this::toDadesObertes).collect(Collectors.toList()) : null)
				.build();
	}

	private DadesObertesRespostaConsulta toDadesObertes(OpenDataEntity openData) {
		if (openData == null)
			return null;

		return new DadesObertesRespostaConsulta(
				openData.getSolicitantCodi(),
				openData.getSolicitantNom(),
				openData.getSolicitantId(),
				openData.getUnitatTramitadora(),
				openData.getProcedimentCodi(),
				openData.getProcedimentNom(),
				openData.getServeiCodi(),
				openData.getServeiNom(),
				openData.getEmissorNom(),
				openData.getEmissorCodi(),
				openData.getConsentiment(),
				openData.getFinalitat(),
				openData.getTitularTipusDoc(),
				openData.getSolicitudId(),
				openData.getDataPeticio(),
				openData.getTipus().name(),
				openData.getEstat());
	}

	@Override
	@Transactional(readOnly = true)
	public List<CarregaDto> findEstadistiquesCarrega() {
		List<CarregaDto> carregues = new ArrayList<>();
//		initEstadistiquesCarrega();

		Date data = new Date();
		List<CarregaDto> carreguesAny = scspCoreTransmisionRepository.findCarrega(DateUtils.truncate(data, Calendar.YEAR));
		carreguesAny.addAll(redireccioPeticioRepository.findCarrega(DateUtils.truncate(data, Calendar.YEAR)));
		List<CarregaDto> carreguesMes = scspCoreTransmisionRepository.findCarrega(DateUtils.truncate(data, Calendar.MONTH));
		carreguesMes.addAll(redireccioPeticioRepository.findCarrega(DateUtils.truncate(data, Calendar.MONTH)));
		List<CarregaDto> carreguesDia = scspCoreTransmisionRepository.findCarrega(DateUtils.truncate(data, Calendar.DAY_OF_MONTH));
		carreguesDia.addAll(redireccioPeticioRepository.findCarrega(DateUtils.truncate(data, Calendar.DAY_OF_MONTH)));
		List<CarregaDto> carreguesHora = scspCoreTransmisionRepository.findCarrega(DateUtils.truncate(data, Calendar.HOUR_OF_DAY));
		carreguesHora.addAll(redireccioPeticioRepository.findCarrega(DateUtils.truncate(data, Calendar.HOUR_OF_DAY)));
		List<CarregaDto> carreguesMinut = scspCoreTransmisionRepository.findCarrega(DateUtils.truncate(data, Calendar.MINUTE));
		carreguesMinut.addAll(redireccioPeticioRepository.findCarrega(DateUtils.truncate(data, Calendar.MINUTE)));

		carreguesAny.forEach(c-> {
			carregues.add(CarregaDto.builder()
					.count(0L)
					.entitatCif(c.getEntitatCif())
					.entitatNom(c.getEntitatNom())
					.departamentNom(c.getDepartamentNom())
					.procedimentCodi(c.getProcedimentCodi())
					.procedimentNom(c.getProcedimentNom())
					.serveiCodi(c.getServeiCodi())
					.serveiNom(c.getServeiNom())
					.serveiTipus(c.getServeiTipus())
					.emisor(c.getEmisor())
					.detailedCount(CarregaDetailedCountDto.builder()
							.any(c.getCount())
							.mes(carreguesMes.stream().filter(cm -> cm.equals(c)).findFirst().orElse(CarregaDto.builder().build()).getCount())
							.dia(carreguesDia.stream().filter(cm -> cm.equals(c)).findFirst().orElse(CarregaDto.builder().build()).getCount())
							.hora(carreguesHora.stream().filter(cm -> cm.equals(c)).findFirst().orElse(CarregaDto.builder().build()).getCount())
							.minut(carreguesMinut.stream().filter(cm -> cm.equals(c)).findFirst().orElse(CarregaDto.builder().build()).getCount())
							.build())
					.build());
		});

		return carregues;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EstadisticaDto> findEstadistiquesByFiltre(EstadistiquesFiltreDto filtre) {
		List<EstadisticaDto> estadistiques = scspCoreTransmisionRepository.findEstadistiques(
				filtre.getEntitatNif(),
				isBlank(filtre.getProcedimentCodi()),
				filtre.getProcedimentCodi(),
				isBlank(filtre.getServeiCodi()),
				filtre.getServeiCodi(),
				filtre.getEstat() == null,
				filtre.isEstatPendent(),
				filtre.isEstatProcessant(),
				filtre.isEstatTramitada(),
				filtre.isEstatError(),
				filtre.getDataInici() == null,
				filtre.getDataInici(),
				filtre.getDataFi() == null,
				filtre.getDataFi());
		estadistiques.addAll(redireccioPeticioRepository.findEstadistiques(
				filtre.getEntitatNif(),
				isBlank(filtre.getProcedimentCodi()),
				filtre.getProcedimentCodi(),
				isBlank(filtre.getServeiCodi()),
				filtre.getServeiCodi(),
				filtre.getEstat() == null,
				filtre.isEstatPendent(),
				filtre.isEstatProcessant(),
				filtre.isEstatTramitada(),
				filtre.isEstatError(),
				filtre.getDataInici() == null,
				filtre.getDataInici(),
				filtre.getDataFi() == null,
				filtre.getDataFi()));
		return estadistiques;
	}

	@Override
	public List<DimensioDesc> getDimensions() {
		List<String> entitatNoms = entitatRepository.findAllNoms();
		List<String> serveisCodis = serveiRepository.findAllCodis();
		List<String> tipus = Arrays.stream(ServeiTipusEnumDto.values()).map(Enum::name).sorted().collect(Collectors.toList());
		// Procediments
		List<String> procedimentsBack = scspCoreTransmisionRepository.findAllProcediments();
		List<String> procedimentsEnrut = redireccioPeticioRepository.findAllProcediments();
		List<String> procediments = Stream.concat(
						procedimentsBack.stream(),
						procedimentsEnrut.stream()
				)
				.distinct()
				.sorted()
				.collect(Collectors.toList());
		// Departaments
		List<String> departamentsBack = scspCoreTransmisionRepository.findAllDepartaments();
		List<String> departamentsEnrut = redireccioPeticioRepository.findAllDepartaments();
		List<String> departaments = Stream.concat(
						departamentsBack.stream(),
						departamentsEnrut.stream()
				)
				.distinct()
				.sorted()
				.collect(Collectors.toList());
		// Emissors
		List<String> emisorsBack = scspCoreEmisorCertificadoRepository.findAllEmisors();
		List<String> emisorsEnrut = redireccioPeticioRepository.findAllEmisors();
		List<String> emisors = Stream.concat(
						emisorsBack.stream(),
						emisorsEnrut.stream()
				)
				.distinct()
				.sorted()
				.collect(Collectors.toList());

		return List.of(
				new DimensioDesc().codi(DimEnum.ENT.name()).nom(DimEnum.ENT.getNom()).descripcio(DimEnum.ENT.getDescripcio()).valors(entitatNoms),
				new DimensioDesc().codi(PRC.name()).nom(PRC.getNom()).descripcio(PRC.getDescripcio()).valors(procediments),
				new DimensioDesc().codi(SRV.name()).nom(SRV.getNom()).descripcio(SRV.getDescripcio()).valors(serveisCodis),
				new DimensioDesc().codi(DEP.name()).nom(DEP.getNom()).descripcio(DEP.getDescripcio()).valors(departaments),
				new DimensioDesc().codi(EMI.name()).nom(EMI.getNom()).descripcio(EMI.getDescripcio()).valors(emisors),
				new DimensioDesc().codi(TIP.name()).nom(TIP.getNom()).descripcio(TIP.getDescripcio()).valors(tipus)
		);
	}

	@Override
	public List<IndicadorDesc> getIndicadors() {
		return List.of(
				new IndicadorDesc().codi(PET_OK.name()).nom(PET_OK.getNom()).descripcio(PET_OK.getDescripcio()).format(LONG),
				new IndicadorDesc().codi(PET_ERR.name()).nom(PET_ERR.getNom()).descripcio(PET_ERR.getDescripcio()).format(LONG)
		);
	}

	@Override
	@Transactional(readOnly = true)
	public RegistresEstadistics consultaUltimesEstadistiques() {

		LocalDate ahir = LocalDate.now().minusDays(1);
		return getRegistresEstadisticsPerData(ahir);
	}

	@Override
	public RegistresEstadistics consultaEstadistiques(LocalDate date) {
		return getRegistresEstadisticsPerData(date);
	}

	@Override
	public List<RegistresEstadistics> consultaEstadistiques(LocalDate iniDate, LocalDate fiDate) {

		List<RegistresEstadistics> estadistiques = new ArrayList<>();
		iniDate.datesUntil(fiDate.plusDays(1))
				.forEach(d -> estadistiques.add(getRegistresEstadisticsPerData(d)));
		return estadistiques;
	}

	private RegistresEstadistics getRegistresEstadisticsPerData(LocalDate data) {

		Date iniciDia = Date.from(data.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		Date finalDia = Date.from(data.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
		List<EstadisticaDto> estadistiques = findEstadistiquesByFiltre(EstadistiquesFiltreDto.builder()
				.dataInici(iniciDia)
				.dataFi(finalDia)
				.build());

		return new RegistresEstadistics()
				.temps(iniciDia.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime())
				.fets(EstadisticaHelper.toRegistreEstadistic(estadistiques, dimFunc, fetFunc));
	}

	// Funció per mapejar les dimensions
	Function<EstadisticaDto, Map<String, String>> dimFunc = est -> {
		LinkedHashMap<String, String> m = new LinkedHashMap<>();
		m.put(ENT.name(), est.getEntitatNom());
		m.put(PRC.name(), est.getProcedimentCodi());
		m.put(SRV.name(), est.getServeiCodi());
		m.put(DEP.name(), est.getDepartamentNom());
		m.put(EMI.name(), est.getEmisor());
		m.put(TIP.name(), est.getServeiTipus().name());
		return m;
	};

	// Funció per mapejar els fets
	Function<EstadisticaDto, Map<String, ? extends Number>> fetFunc = est -> {
		LinkedHashMap<String, Number> m = new LinkedHashMap<>();
		m.put(PET_OK.name(), est.getSumatoriNumOk());
		m.put(PET_ERR.name(), est.getSumatoriNumError());
		return m;
	};

	public enum DimEnum {
		ENT ("Entitat", "Nom de l'entitat que realitza la consulta"),
		PRC ("Procediment", "Procediment al que pertany la consulta"),
		SRV ("Servei", "Servei al que pertany la consulta"),
		DEP ("Departament", "Departament que realitza la consulta"),
		EMI ("Emisor", "Emisor de la consulta"),
		TIP ("Tipus", "Tipus de servei consultat: Bacloffice, enrutador simple o enrutador múltiple");

		private String nom;
		private String descripcio;

		DimEnum(String nom, String descripcio) {
			this.nom = nom;
			this.descripcio = descripcio;
		}

		public String getNom() {
			return nom;
		}
		public String getDescripcio() {
			return descripcio;
		}
	}

	public enum IndEnum {
		PET_OK ("Peticions OK", "Peticions realitzades correctament"),
		PET_ERR ("Peticions ERROR", "Peticions processades amb error");

		private String nom;
		private String descripcio;

		IndEnum(String nom, String descripcio) {
			this.nom = nom;
			this.descripcio = descripcio;
		}

		public String getNom() {
			return nom;
		}
		public String getDescripcio() {
			return descripcio;
		}
	}
}
