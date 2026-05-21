/**
 * 
 */
package es.caib.emiserv.logic.service;

import es.caib.comanda.model.server.monitoring.*;
import es.caib.comanda.ms.log.helper.LogFileStream;
import es.caib.comanda.ms.log.helper.LogHelper;
import es.caib.comanda.ms.salut.helper.EstatHelper;
import es.caib.emiserv.logic.helper.PropertiesHelper;
import es.caib.emiserv.logic.helper.SalutHelper;
import es.caib.emiserv.logic.intf.dto.SubsistemesEnum;
import es.caib.emiserv.logic.intf.service.AplicacioService;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.UsuariEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAplicacionEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAutorizacionCertificadoEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAutorizacionOrganismoEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.UsuariRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreEmAplicacionRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreEmAutorizacionCertificadoRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Implementació dels mètodes per a gestionar l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class AplicacioServiceImpl implements AplicacioService {

	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private PropertiesHelper propertiesHelper;
	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private ScspCoreEmAplicacionRepository scspCoreEmAplicacionRepository;
	@Autowired
	private ScspCoreEmAutorizacionCertificadoRepository scspCoreEmAutorizacionCertificadoRepository;
	@Autowired
	private Environment environment;

	@PersistenceContext
    private EntityManager em;

	private Clock clock = Clock.systemDefaultZone();
	private OffsetDateTime dataArrencada = OffsetDateTime.now(clock);
	private final AtomicReference<OffsetDateTime> darreraConsultaSalut = new AtomicReference<>();

	@Override
	@Transactional(readOnly = true)
	public String getIdiomaUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userName = auth != null ? auth.getName(): null;
		log.debug("Obtenint l'usuari actual (usuari=" + userName + ")");
		if (userName == null) {
			return null;
		}
		Optional<UsuariEntity> usuari = usuariRepository.findById(userName);
		return (usuari.isPresent()) ? usuari.get().getIdioma() : null;
	}

	@Override
	@Transactional
	public void updateIdiomaUsuariActual(String idioma) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Actualitzant idioma de l'usuari actual (" +
				"usuari=" + auth.getName() + "," +
				"idioma=" + idioma + ")");
		Optional<UsuariEntity> usuari = usuariRepository.findById(auth.getName());
		if (usuari.isPresent()) {
			usuari.get().update(idioma);
		} else {
			usuariRepository.save(
					UsuariEntity.getBuilder(auth.getName()).
					idioma(idioma).
					build());
		}
	}

    @Override
    public void propagateDbProperties() {
		propertiesHelper.reloadDbProperties();
    }


	// Salut
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void addSubsistemaExit(SubsistemesEnum subsistema, String serveiCodi, long duracioMs) {
		SalutHelper.addSubsistemaExit(subsistema, serveiCodi, duracioMs);
	}

	@Override
	public void addSubsistemaError(SubsistemesEnum subsistema, String serveiCodi) {
		SalutHelper.addSubsistemaError(subsistema, serveiCodi);
	}

    @Override
    public void addSubsistemaError(SubsistemesEnum subsistema) {
        SalutHelper.addSubsistemaError(subsistema);
    }

	@Override
	public void addIntegracioExit(String solicitantId, String serveiCodi, long duracioMs) {
		SalutHelper.addIntegracioExit(solicitantId, serveiCodi, duracioMs);
	}

	@Override
	public void addIntegracioError(String solicitantId, String serveiCodi) {
		SalutHelper.addIntegracioError(solicitantId, serveiCodi);
	}

	@Override
	@Transactional(readOnly = true)
	public List<IntegracioInfo> getIntegracionsInfo() {
		List<ScspCoreEmAplicacionEntity> aplicacions = getAplicacionsConfigurades();
		Map<String, Integer> comptadorCodis = new HashMap<>();
		List<IntegracioInfo> integracions = new ArrayList<>();

		for (ScspCoreEmAplicacionEntity aplicacio : aplicacions) {
			integracions.add(new IntegracioInfo()
					.codi(getIntegracioCodiUnic(getIntegracioCodi(aplicacio), comptadorCodis))
					.nom(limitaText(getIntegracioNom(aplicacio), 255)));
		}

		for (ServeiEntity servei : getServeisActiusOrdenats()) {
			integracions.add(new IntegracioInfo()
					.codi(getIntegracioCodiUnic(servei.getCodi(), comptadorCodis))
					.nom(limitaText(servei.getNom(), 255)));
		}

		return integracions;
	}

	@Override
	@Transactional(readOnly = true)
	public List<IntegracioSalut> getIntegracionsSalut() {
		return getIntegracionsSalut(null, null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<IntegracioSalut> getIntegracionsSalut(OffsetDateTime dataPeriode, OffsetDateTime dataTotal) {
		darreraConsultaSalut.getAndSet(OffsetDateTime.now(clock));

		List<IntegracioSalut> integracions = new ArrayList<>();
		Map<Integer, IntegracioPeticions> peticionsPerAplicacio = new LinkedHashMap<>();
		Map<String, IntegracioPeticions> peticionsPerServei = new LinkedHashMap<>();
		List<ScspCoreEmAplicacionEntity> aplicacions = getAplicacionsConfigurades();
		List<ServeiEntity> serveis = getServeisActiusOrdenats();
		Map<String, Integer> comptadorCodis = new HashMap<>();

		for (ScspCoreEmAplicacionEntity aplicacio : aplicacions) {
			IntegracioPeticions peticions = creaPeticionsBuides(null);
			peticionsPerAplicacio.put(aplicacio.getIdAplicacion(), peticions);
			integracions.add(new IntegracioSalut()
					.codi(getIntegracioCodiUnic(getIntegracioCodi(aplicacio), comptadorCodis))
					.estat(EstatSalutEnum.UNKNOWN)
					.peticions(peticions));
		}

		for (ServeiEntity servei : serveis) {
			IntegracioPeticions peticions = creaPeticionsBuides(getServeiEndpoint(servei));
			peticionsPerServei.put(servei.getCodi(), peticions);
			integracions.add(new IntegracioSalut()
					.codi(getIntegracioCodiUnic(servei.getCodi(), comptadorCodis))
					.estat(EstatSalutEnum.UNKNOWN)
					.peticions(peticions));
		}

		List<ScspCoreEmAutorizacionCertificadoEntity> autoritzacions = scspCoreEmAutorizacionCertificadoRepository.findAll();
		afegeixEntornsConfigurats(peticionsPerAplicacio, autoritzacions);
		aplicaIntegracioStatsMemoria(peticionsPerAplicacio, peticionsPerServei, autoritzacions);

		for (IntegracioSalut integracio : integracions) {
			integracio.estat(calculaEstatIntegracio(integracio.getPeticions()));
		}

		return integracions;
	}

	private void afegeixEntornsConfigurats(
			Map<Integer, IntegracioPeticions> peticionsPerAplicacio,
			List<ScspCoreEmAutorizacionCertificadoEntity> autoritzacions) {
		autoritzacions.stream()
				.sorted(Comparator
						.comparing(this::getAutoritzacioAplicacioId, Comparator.nullsLast(Integer::compareTo))
						.thenComparing(this::getAutoritzacioServeiCodi, Comparator.nullsLast(String::compareTo)))
				.forEach(autoritzacio -> {
					Integer aplicacioId = getAutoritzacioAplicacioId(autoritzacio);
					ScspCoreServicioEntity servei = autoritzacio.getServicio();
					ScspCoreEmAutorizacionOrganismoEntity organismo = autoritzacio.getOrganismo();
					if (aplicacioId == null || servei == null || !teText(servei.getCodigoCertificado())) {
						return;
					}
					afegeixEntorn(
							peticionsPerAplicacio.get(aplicacioId),
							getIntegracioEntornCodi(servei.getCodigoCertificado(), organismo.getNombreOrganismo()),
							getServeiEndpoint(servei.getUrlSincrona(), servei.getUrlAsincrona(), servei.getCodigoCertificado()));
				});
	}

	private void aplicaIntegracioStatsMemoria(
			Map<Integer, IntegracioPeticions> peticionsPerAplicacio,
			Map<String, IntegracioPeticions> peticionsPerServei,
			List<ScspCoreEmAutorizacionCertificadoEntity> autoritzacions) {
		// Construeix mapes indexats per "solicitantId|serveiCodi" (clau usada per SalutHelper).
		Map<String, Integer> statKeyToAplicacioId = new HashMap<>();
		Map<String, String> statKeyToEntornKey = new HashMap<>();
		for (ScspCoreEmAutorizacionCertificadoEntity scac : autoritzacions) {
			Integer aplicacioId = getAutoritzacioAplicacioId(scac);
			String serveiCodi = getAutoritzacioServeiCodi(scac);
			String organismeNom = scac.getOrganismo() != null ? scac.getOrganismo().getNombreOrganismo() : null;
			String solicitantId = scac.getOrganismo() != null ? scac.getOrganismo().getIdorganismo() : null;
			if (aplicacioId != null && teText(serveiCodi) && teText(solicitantId)) {
				String statKey = solicitantId + "|" + serveiCodi;
				statKeyToAplicacioId.put(statKey, aplicacioId);
				statKeyToEntornKey.put(statKey, getIntegracioEntornCodi(serveiCodi, organismeNom));
			}
		}
		// Aplica les estadístiques en memòria
		for (SubsistemaSalut stat : SalutHelper.getIntegracioStats()) {
			String key = stat.getCodi(); // "solicitantId|serveiCodi"

			String serveiCodi = getServeiCodiStatsIntegracio(key);
			if (teText(serveiCodi)) {
				afegeixStats(peticionsPerServei.get(serveiCodi), stat);
			}

			Integer aplicacioId = statKeyToAplicacioId.get(key);
			if (aplicacioId == null) continue;
			IntegracioPeticions peticions = peticionsPerAplicacio.get(aplicacioId);
			if (peticions == null) continue;
			String entornKey = statKeyToEntornKey.get(key);
			IntegracioPeticions entorn = afegeixEntorn(peticions, entornKey, null);
			afegeixStats(peticions, stat);
			afegeixStats(entorn, stat);
		}
	}

	private void afegeixStats(IntegracioPeticions peticions, SubsistemaSalut stat) {
		if (peticions == null) {
			return;
		}
		afegeixTotals(peticions, stat.getTotalOk(), stat.getTotalError());
		afegeixPeriode(peticions, stat.getPeticionsOkUltimPeriode(), stat.getPeticionsErrorUltimPeriode());
	}

	private IntegracioPeticions afegeixEntorn(IntegracioPeticions peticions, String serveiCodi, String endpoint) {
		if (peticions == null) {
			return null;
		}
		IntegracioPeticions entorn = peticions.getPeticionsPerEntorn().get(serveiCodi);
		if (entorn == null) {
			entorn = creaPeticionsBuides(endpoint);
			peticions.putPeticionsPerEntornItem(serveiCodi, entorn);
		}
		return entorn;
	}

	private IntegracioPeticions creaPeticionsBuides(String endpoint) {
		return new IntegracioPeticions()
				.totalOk(0L)
				.totalError(0L)
				.totalTempsMig(0)
				.peticionsOkUltimPeriode(0L)
				.peticionsErrorUltimPeriode(0L)
				.tempsMigUltimPeriode(0)
				.endpoint(limitaText(endpoint, 255))
				.peticionsPerEntorn(new LinkedHashMap<>());
	}

	private void afegeixTotals(IntegracioPeticions peticions, long ok, long error) {
		peticions.totalOk(peticions.getTotalOk() + ok);
		peticions.totalError(peticions.getTotalError() + error);
	}

	private void afegeixPeriode(IntegracioPeticions peticions, long ok, long error) {
		peticions.peticionsOkUltimPeriode(peticions.getPeticionsOkUltimPeriode() + ok);
		peticions.peticionsErrorUltimPeriode(peticions.getPeticionsErrorUltimPeriode() + error);
	}

	private EstatSalutEnum calculaEstatIntegracio(IntegracioPeticions peticions) {
		long peticionsPeriode = peticions.getPeticionsOkUltimPeriode() + peticions.getPeticionsErrorUltimPeriode();
		if (peticionsPeriode > 0) {
			return EstatHelper.calculaEstat(peticions.getPeticionsOkUltimPeriode(), peticions.getPeticionsErrorUltimPeriode());
		}

		long peticionsTotals = peticions.getTotalOk() + peticions.getTotalError();
		if (peticionsTotals > 0) {
			return EstatHelper.calculaEstat(peticions.getTotalOk(), peticions.getTotalError());
		}

		return EstatSalutEnum.UNKNOWN;
	}

	private Integer getAutoritzacioAplicacioId(ScspCoreEmAutorizacionCertificadoEntity autoritzacio) {
		return autoritzacio.getAplicacion() != null ? autoritzacio.getAplicacion().getIdAplicacion() : null;
	}

	private String getAutoritzacioServeiCodi(ScspCoreEmAutorizacionCertificadoEntity autoritzacio) {
		return autoritzacio.getServicio() != null ? autoritzacio.getServicio().getCodigoCertificado() : null;
	}

	private String getServeiEndpoint(String urlSincrona, String urlAsincrona, String serveiCodi) {
		if (teText(urlSincrona)) {
			return urlSincrona;
		}
		if (teText(urlAsincrona)) {
			return urlAsincrona;
		}
		return serveiCodi;
	}

	private String getServeiEndpoint(ServeiEntity servei) {
		if (servei == null) {
			return null;
		}
		if (teText(servei.getBackofficeCaibUrl())) {
			return servei.getBackofficeCaibUrl();
		}
		if (teText(servei.getUrlPerDefecte())) {
			return servei.getUrlPerDefecte();
		}
		return servei.getCodi();
	}

	private String getServeiCodiStatsIntegracio(String statCodi) {
		if (!teText(statCodi)) {
			return null;
		}
		int separador = statCodi.lastIndexOf('|');
		if (separador < 0 || separador == statCodi.length() - 1) {
			return statCodi;
		}
		return statCodi.substring(separador + 1);
	}

	private String getIntegracioEntornCodi(String serveiCodi, String organismeNom) {
		return serveiCodi + "|" + organismeNom;
	}

	private List<ScspCoreEmAplicacionEntity> getAplicacionsConfigurades() {
		return scspCoreEmAplicacionRepository.findAll().stream()
				.filter(aplicacio -> aplicacio.getIdAplicacion() != null)
				.sorted(Comparator.comparing(ScspCoreEmAplicacionEntity::getIdAplicacion))
				.collect(Collectors.toList());
	}

	private List<ServeiEntity> getServeisActiusOrdenats() {
		return serveiRepository.findByActiuTrue().stream()
				.sorted(Comparator.comparing(ServeiEntity::getCodi, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
	}

	private String getIntegracioCodi(ScspCoreEmAplicacionEntity aplicacio) {
		try {
			if (teText(aplicacio.getCn())) {
				String nom = aplicacio.getCn();
				if (nom.contains("PLATAFORMA DE INTERMEDIACION")) {
					return "PID";
				}
				if (nom.contains("PLATAFORMA D'INTEROPERABILITAT DE LES ILLES BALEARS")) {
					return "PBL";
				}
			}
		} catch (Exception e) {}
		return "APP-" + aplicacio.getIdAplicacion();
	}

	private String getIntegracioCodiUnic(
			String codiBase,
			Map<String, Integer> comptadorCodis) {
		int ocurrencia = comptadorCodis.getOrDefault(codiBase, 0) + 1;
		String codi = ocurrencia == 1 ? codiBase : codiBase + ocurrencia;
		comptadorCodis.put(codiBase, ocurrencia);
		return codi;
	}

	private String getIntegracioNom(ScspCoreEmAplicacionEntity aplicacio) {
		if (teText(aplicacio.getCn())) {
			return aplicacio.getCn();
		}
		if (teText(aplicacio.getNifCertificado())) {
			return aplicacio.getNifCertificado();
		}
		return getIntegracioCodi(aplicacio);
	}

	private boolean teText(String text) {
		return text != null && !text.trim().isEmpty();
	}

	@Override
	public List<SubsistemaInfo> getSubsistemesInfo() {
		return Arrays.stream(SubsistemesEnum.values())
				.map(s -> new SubsistemaInfo()
						.codi(s.name())
						.nom(s.getNom()))
				.collect(Collectors.toList());
	}

	public static String limitaText(String text, int maxCaracters) {
		if (text == null) return null;
		if (maxCaracters <= 0) return "";

		if (text.length() <= maxCaracters) {
			return text;
		}

		// Si no hi ha espai per afegir "...", retalla només al màxim possible
		if (maxCaracters <= 3) {
			return text.substring(0, maxCaracters);
		}

		return text.substring(0, maxCaracters - 3) + "...";
	}

	@Override
	public List<SubsistemaSalut> getSubsistemesSalut() {
		return SalutHelper.getSubsistemesSalut();
	}

	@Override
	public List<ContextInfo> getContextsInfo(String baseUrl) {
		return List.of(
				new ContextInfo()
						.codi("BACK")
						.nom("Backoffice")
						.path(baseUrl + "/emiservback")
						.manuals(List.of(
								new Manual().nom("Manual d'usuari").path("https://github.com/GovernIB/emiserv/raw/emiserv-dev/doc/pdf/02_emiserv_usuari.pdf"),
								new Manual().nom("Manual d'implementació de backoffices").path("https://github.com/GovernIB/emiserv/raw/emiserv-dev/doc/pdf/01_emiserv_backoffice.pdf"),
								new Manual().nom("Manual d'instal·lació").path("https://github.com/GovernIB/emiserv/raw/emiserv-dev/doc/pdf/00_emiserv_instalar.pdf")
						)),
				new ContextInfo()
						.codi("INT")
						.nom("API interna")
						.path(baseUrl + "/emiservapi/interna")
						.manuals(List.of(
								new Manual().nom("Manual d'integració").path("https://github.com/GovernIB/emiserv/raw/emiserv-dev/doc/pdf/03_emiserv_integracio.pdf")
						))
						.api(baseUrl + "/emiservapi/interna/swagger-ui/index.html"),
				new ContextInfo()
						.codi("EXT")
						.nom("API externa")
						.path(baseUrl + "/emiservapi/externa")
						.api(baseUrl + "/emiservapi/externa/swagger-ui/index.html")
		);
	}

	@Override
	public List<MissatgeSalut> getMissatgesSalut() {
		return Collections.emptyList();
	}

	public Integer measureDbLatencyMs() {

        try {
            Session session = em.unwrap(Session.class);

            final String[] sql = new String[1];

            // 1) Detectar producte de BBDD amb JDBC
            session.doWork(conn -> {
                String product = conn.getMetaData().getDatabaseProductName().toLowerCase();
                sql[0] = product.contains("oracle") ? "SELECT 1 FROM DUAL" : "SELECT 1";
            });

            // 2) Mesurar query
            long start = System.currentTimeMillis();
            em.createNativeQuery(sql[0])
                    .setHint("org.hibernate.timeout", 2) // segons
                    .getSingleResult();

            return (int) (System.currentTimeMillis() - start);
        } catch (Exception e) {
            return null;
        }
    }

	// LOGS
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static String logDir;
	private static final String APP_NOM = "emiserv";

    @Override
    public List<FitxerInfo> llistarLogFiles() {
        return LogHelper.llistarFitxers(getLogDir(), APP_NOM);
    }

	@Override
	public FitxerContingut getLogFileByNom(String nomFitxer) {
		LogHelper.setAppNom(APP_NOM);
		return LogHelper.getFitxerByNom(getLogDir(), nomFitxer);
	}

	@Override
	public LogFileStream getFileLogStreamByNom(String nomFitxer) {
		LogHelper.setAppNom(APP_NOM);
		return LogHelper.getFileStreamByNom(getLogDir(), nomFitxer);
	}

	@Override
	public List<String> readLastNLogLines(String nomFitxer, Long nLinies) {
		LogHelper.setAppNom(APP_NOM);
		return LogHelper.readLastNLines(getLogDir(), nomFitxer, nLinies);
	}

	private String getLogDir() {
		if (logDir == null) {
			logDir = environment.getProperty("es.caib.emiserv.log.dir");
		}
		return logDir;
	}

}
