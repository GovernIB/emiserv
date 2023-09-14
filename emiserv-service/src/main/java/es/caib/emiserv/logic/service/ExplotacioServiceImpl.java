/**
 * 
 */
package es.caib.emiserv.logic.service;

import es.caib.emiserv.client.comu.ServeiTipus;
import es.caib.emiserv.client.dadesobertes.DadesObertesResposta;
import es.caib.emiserv.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.emiserv.logic.intf.dto.CarregaDto;
import es.caib.emiserv.logic.intf.dto.CarregaDto.CarregaDetailedCountDto;
import es.caib.emiserv.logic.intf.dto.ConsultaOpenDataDto;
import es.caib.emiserv.logic.intf.dto.EstadisticaDto;
import es.caib.emiserv.logic.intf.dto.EstadistiquesFiltreDto;
import es.caib.emiserv.logic.intf.dto.InformeGeneralEstatDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.service.ExplotacioService;
import es.caib.emiserv.persist.entity.OpenDataEntity;
import es.caib.emiserv.persist.repository.OpenDataRepository;
import es.caib.emiserv.persist.repository.RedireccioPeticioRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreTransmisionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
	private RedireccioPeticioRepository redireccioPeticioRepository;
	@Autowired
	private OpenDataRepository openDataRepository;

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


//	public void actualitzarEstadistiquesPeticio(
//			Long entitatCif,
//			List<Solicitud> solicituds,
//			boolean recobriment) {
//		initEstadistiquesCarrega();
//		if (solicituds != null && solicituds.size() > 0) {
//			Solicitud solicitud = solicituds.get(0);
//			String departamentNom = solicitud.getUnitatTramitadora();
//			String procedimentNom = solicitud.getProcedimentNom();
//			String serveiCodi = solicitud.getServeiCodi();
//			afegirConsultaEstadistiquesCarrega(entitatCif, departamentNom, procedimentNom, serveiCodi, carreguesAny);
//			afegirConsultaEstadistiquesCarrega(entitatCif, departamentNom, procedimentNom, serveiCodi, carreguesMes);
//			afegirConsultaEstadistiquesCarrega(entitatCif, departamentNom, procedimentNom, serveiCodi, carreguesDia);
//			afegirConsultaEstadistiquesCarrega(entitatCif, departamentNom, procedimentNom, serveiCodi, carreguesHora);
//			afegirConsultaEstadistiquesCarrega(entitatCif, departamentNom, procedimentNom, serveiCodi, carreguesMinut);
//		}
//	}

//	// Mètodes privats per a calculat les estadístiques de càrrega
//	private void initEstadistiquesCarrega() {
//		if (carreguesAny == null) {
//			carreguesAny = Collections.synchronizedList(
//					scspCoreTransmisionRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.YEAR)));
//			carreguesAny.addAll(redireccioPeticioRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.YEAR)));
//		}
//		if (carreguesMes == null) {
//			carreguesMes = Collections.synchronizedList(
//					scspCoreTransmisionRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.MONTH)));
//			carreguesMes.addAll(redireccioPeticioRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.MONTH)));
//		}
//		if (carreguesDia == null) {
//			carreguesDia = Collections.synchronizedList(
//					scspCoreTransmisionRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)));
//			carreguesDia.addAll(redireccioPeticioRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)));
//		}
//		if (carreguesHora == null) {
//			carreguesHora = Collections.synchronizedList(
//					scspCoreTransmisionRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.HOUR_OF_DAY)));
//			carreguesHora.addAll(redireccioPeticioRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.HOUR_OF_DAY)));
//		}
//		if (carreguesMinut == null) {
//			carreguesMinut = Collections.synchronizedList(
//					scspCoreTransmisionRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.MINUTE)));
//			carreguesMinut.addAll(redireccioPeticioRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.MINUTE)));
//		}
//	}

//	private void afegirConsultaEstadistiquesCarrega(
//			String entitatNom,
//			String entitatCif,
//			String departamentNom,
//			String procedimentCodi,
//			String procedimentNom,
//			String serveiCodi,
//			String serveiNom,
//			ServeiTipusEnumDto serveiTipus,
//			String emissorCodi,
//			List<CarregaDto> carregues) {
//		CarregaDto carrega = carreguesAny.stream()
//				.filter(c -> c.getEntitatCif().equals(entitatCif) && c.getDepartamentNom().equals(departamentNom) && c.getProcedimentNom().equals(procedimentNom) && c.getServeiCodi().equals(serveiCodi))
//				.findFirst()
//				.orElse(null);
//
//		if (carrega == null) {
//			carregues.add(CarregaDto.builder()
//					.count(1L)
//					.entitatNom(entitatNom)
//					.entitatCif(entitatCif)
//					.departamentNom(departamentNom)
//					.procedimentCodi(procedimentCodi)
//					.procedimentNom(procedimentNom)
//					.serveiCodi(serveiCodi)
//					.serveiNom(serveiNom)
//					.serveiTipus(serveiTipus)
//					.emisor(emissorCodi)
//					.build());
//		} else {
//			carrega.setCount(carrega.getCount() + 1);
//		}
//	}

}
