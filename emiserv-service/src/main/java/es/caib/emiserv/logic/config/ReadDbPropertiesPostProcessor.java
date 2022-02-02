package es.caib.emiserv.logic.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ReadDbPropertiesPostProcessor implements EnvironmentPostProcessor {

    private static final String DBAPP_PROPERTIES = "es.caib.emiserv.db.properties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        Map<String, Object> propertySource = new HashMap<>();

        try {
            log.debug("Obtenint dataSource per a carregar les propietats de la BBDD...");
            JndiDataSourceLookup lookup = new JndiDataSourceLookup();
            String datasourceJndi = environment.getProperty("spring.datasource.jndi-name", "java:jboss/datasources/emiservDS");
            DataSource dataSource = lookup.getDataSource(datasourceJndi);
            log.debug("... Datasource carregat correctament.");

            log.debug("Carregant les propietats...");
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT key, value FROM ems_config WHERE jboss_property = 0");
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                String clau = resultSet.getString("key");
                String valor = resultSet.getString("value");
                propertySource.put(clau, valor);
                log.debug("   ... carregada la propietat: {}={}", clau, valor);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
            log.debug("... Finalitzada la càrega de propietats");

            log.debug("Afegint les propietats carregades de base de dades al entorn...");
            environment.getPropertySources().addFirst(new MapPropertySource(DBAPP_PROPERTIES, propertySource));
            log.debug("...Propietats afegides");

        } catch (Throwable ex) {
            log.error("No s'han pogut carregar les propietats de la BBDD", ex);
        }

    }
}
