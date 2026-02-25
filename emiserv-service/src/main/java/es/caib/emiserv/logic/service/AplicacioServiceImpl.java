/**
 * 
 */
package es.caib.emiserv.logic.service;

import es.caib.comanda.model.server.monitoring.ContextInfo;
import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.IntegracioSalut;
import es.caib.comanda.model.server.monitoring.Manual;
import es.caib.comanda.model.server.monitoring.MissatgeSalut;
import es.caib.comanda.model.server.monitoring.SubsistemaInfo;
import es.caib.comanda.model.server.monitoring.SubsistemaSalut;
import es.caib.comanda.ms.log.helper.LogFileStream;
import es.caib.comanda.ms.log.helper.LogHelper;
import es.caib.emiserv.logic.helper.PropertiesHelper;
import es.caib.emiserv.logic.helper.SalutHelper;
import es.caib.emiserv.logic.intf.dto.SubsistemesEnum;
import es.caib.emiserv.logic.intf.service.AplicacioService;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.UsuariEntity;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.UsuariRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
	private Environment environment;

    @PersistenceContext
    private EntityManager em;

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
	public List<IntegracioInfo> getIntegracionsInfo() {
		return Collections.emptyList();
	}

	@Override
	public List<IntegracioSalut> getIntegracionsSalut() {
		return Collections.emptyList();
	}

	@Override
	public List<SubsistemaInfo> getSubsistemesInfo() {

		// Subsistemes per consultes a backoffice i enrutador
		List<SubsistemaInfo> subsistemes = Arrays.stream(SubsistemesEnum.values())
				.map(s -> new SubsistemaInfo()
						.codi(s.name())
						.nom(s.getNom()))
				.collect(Collectors.toCollection(ArrayList::new));

		// Un subsistema per servei actiu
		List<ServeiEntity> serveisActius = serveiRepository.findByActiuTrue();
		for (ServeiEntity servei : serveisActius) {
			subsistemes.add(new SubsistemaInfo().codi(servei.getCodi()).nom(servei.getNom()));
		}
		return subsistemes;
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
						.manuals(List.of(
								new Manual().nom("Manual d'integració").path("https://github.com/GovernIB/emiserv/raw/emiserv-dev/doc/pdf/03_emiserv_integracio.pdf")
								))
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

//	@Override
//	public Map<String, String> readProperties() {
//		Map<String, String> properties = new HashMap<>();
//
//		properties.put("es.caib.emiserv.backoffice.caib.auth.password", environment.getProperty("es.caib.emiserv.backoffice.caib.auth.password"));
//		properties.put("es.caib.emiserv.backoffice.caib.auth.username", environment.getProperty("es.caib.emiserv.backoffice.caib.auth.username"));
//		properties.put("es.caib.emiserv.backoffice.caib.soap.action", environment.getProperty("es.caib.emiserv.backoffice.caib.soap.action"));
//		properties.put("es.caib.emiserv.backoffice.jar.path", environment.getProperty("es.caib.emiserv.backoffice.jar.path"));
//		properties.put("es.caib.emiserv.backoffice.mock", environment.getProperty("es.caib.emiserv.backoffice.mock"));
//		properties.put("es.caib.emiserv.backoffice.processar.datos.especificos.peticio", environment.getProperty("es.caib.emiserv.backoffice.processar.datos.especificos.peticio"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.auth.password", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.auth.password"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.auth.username", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.auth.username"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.soap.action", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.soap.action"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPASWS01.processar.datos.especificos.peticio", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPASWS01.processar.datos.especificos.peticio"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.auth.password", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.auth.password"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.auth.username", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.auth.username"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.soap.action", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.soap.action"));
//		properties.put("es.caib.emiserv.backoffice.SVDCCAACPCWS01.processar.datos.especificos.peticio", environment.getProperty("es.caib.emiserv.backoffice.SVDCCAACPCWS01.processar.datos.especificos.peticio"));
//		properties.put("es.caib.emiserv.backoffice.SVDSCDDWS01.caib.auth.password", environment.getProperty("es.caib.emiserv.backoffice.SVDSCDDWS01.caib.auth.password"));
//		properties.put("es.caib.emiserv.backoffice.SVDSCDDWS01.caib.auth.username", environment.getProperty("es.caib.emiserv.backoffice.SVDSCDDWS01.caib.auth.username"));
//		properties.put("es.caib.emiserv.backoffice.SVDSCDDWS01.caib.soap.action", environment.getProperty("es.caib.emiserv.backoffice.SVDSCDDWS01.caib.soap.action"));
//		properties.put("es.caib.emiserv.backoffice.SVDSCDDWS01.processar.datos.especificos.peticio", environment.getProperty("es.caib.emiserv.backoffice.SVDSCDDWS01.processar.datos.especificos.peticio"));
//		properties.put("es.caib.emiserv.default.auditor", environment.getProperty("es.caib.emiserv.default.auditor"));
//		properties.put("es.caib.emiserv.security.mappableRoles", environment.getProperty("es.caib.emiserv.security.mappableRoles"));
//		properties.put("es.caib.emiserv.security.useResourceRoleMappings", environment.getProperty("es.caib.emiserv.security.useResourceRoleMappings"));
//		properties.put("es.caib.emiserv.tasca.backoffice.async.processar.pendents", environment.getProperty("es.caib.emiserv.tasca.backoffice.async.processar.pendents"));
//		properties.put("es.caib.emiserv.xsd.base.path", environment.getProperty("es.caib.emiserv.xsd.base.path"));
//
//		return properties;
//	}

}
