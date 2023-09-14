package es.scsp.backoffice;

import es.caib.emiserv.logic.intf.service.ScspService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Classe que al instanciar-se carrega totes les classes que implementen la interfície es.scsp.common.backoffice.BackOffice
 * de la llibreria backoffice.jar
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@PropertySource(ignoreResourceNotFound = true, value = {"file://${" + ScspService.APP_SYSTEM_PROPERTIES + "}"})
public class BackofficeInitialization {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BackofficeInitialization.class);

    @Autowired
    private Environment env;

    private String backofficeJarPath;

    BackofficeClassloader backofficeClassloader;

    @Scheduled(fixedDelay = 300000, initialDelay = 5000)
    @PostConstruct
    public void loadBackofficeClasses() throws IOException {
        log.debug("BKO - Inicialitzant classes del backoffice...");

        String currentBackofficeJarPath = getBackofficeJarPath();
        if (currentBackofficeJarPath != null && !currentBackofficeJarPath.isBlank() && !currentBackofficeJarPath.equals(backofficeJarPath)) {
            log.info("BKO - Carregant classes del backoffice...");
            backofficeJarPath = currentBackofficeJarPath;
            backofficeClassloader = new BackofficeClassloader();
            if (backofficeJarPath != null && !backofficeJarPath.isEmpty())
                backofficeClassloader.setBackofficeJarPath(backofficeJarPath);
            List<String> backofficeClassNames = getBackofficeClasses();
            Set<String> backofficeJarClassNames = backofficeClassloader.getClassNamesFromBackofficeJarFile();
            backofficeClassNames.retainAll(backofficeJarClassNames);
            backofficeClassNames.forEach(c -> {
                try {
                    backofficeClassloader.findClass(c);
                    log.info("BKO - Classe de backoffice {} carregada correctament", c);
                } catch (ClassNotFoundException e) {
                    log.error("BKO - No ha estat possible carregar la classe '{}' del backoffice", e);
                }
            });
            log.info("BKO - Carrega finalitzada");
        } else {
            log.debug("BKO - No ha estat necessari inicialitzar les classes del backoffice.");
        }
        log.debug("BKO - Inicialització finalitzada");
    }

    private String getBackofficeJarPath() {
        log.debug("Obtenint dataSource per a consultar la ruta del jar de backoffice de la BBDD...");
        String backofficeJarPath = null;
        try {
            JndiDataSourceLookup lookup = new JndiDataSourceLookup();
            String datasourceJndi = env.getProperty("spring.datasource.jndi-name", "java:jboss/datasources/emiservDS");
            DataSource dataSource = lookup.getDataSource(datasourceJndi);
            log.debug("... Datasource carregat correctament.");

            log.debug("Carregant la ruta...");
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT value FROM ems_config WHERE key = 'es.caib.emiserv.backoffice.jar.path'");
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        backofficeJarPath = resultSet.getString("value");
                        log.debug("   ... carregada la ruta: {}", backofficeJarPath);
                    }
                }
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            log.debug("... Finalitzada la càrega de la ruta del jar de backoffice");
        } catch (Throwable ex) {
            log.error("No s'han pogut carregar les propietats de la BBDD", ex);
        }
        return backofficeJarPath;
    }

    private List<String> getBackofficeClasses() {

        List<String> resposta = new ArrayList<>();
        try {
            ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
            provider.addIncludeFilter(new AbstractClassTestingTypeFilter() {
                @Override
                protected boolean match(ClassMetadata metadata) {
                    for (String interfaceName : metadata.getInterfaceNames()) {
                        if ("es.scsp.common.backoffice.BackOffice".equals(interfaceName))
                            return true;
                    }
                    return false;
                }
            });
            provider.setResourceLoader(new PathMatchingResourcePatternResolver(backofficeClassloader));
            Set<BeanDefinition> components = provider.findCandidateComponents("es/caib/emiserv/backoffice");
            for (BeanDefinition component : components) {
                resposta.add(component.getBeanClassName());
            }
        } catch (Exception e) {
            log.error("No ha estat possible carregar les classes de backoffice", e);
        }
        return resposta;
    }
}
