/**
 * 
 */
package es.caib.emiserv.api.interna.scsp;

import org.springframework.beans.factory.FactoryBean;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * FactoryBean per a accedir a les propietats de EMISERV.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EmiservPropertiesFactoryBean implements FactoryBean<Properties> {

	public static final String APP_PROPERTIES = "es.caib.emiserv.properties";
	public static final String DATASOURCE_JNDI_PROP = "spring.datasource.jndi-name";
	public static final String DATASOURCE_JNDI_PROP2 = "spring.datasource.jndi.name";
	public static final String HIBERNATE_DIALECT_PROP = "spring.jpa.properties.hibernate.dialect";
	public static final String SCSP_PROP_PREFIX = "es.caib.emiserv.scsp.";

	@Override
	public Class<?> getObjectType() {
		return Properties.class;
	}

	@Override
	public Properties getObject() throws Exception {
		Properties props = new Properties();
		String appProperties = System.getProperty(APP_PROPERTIES);
		if (appProperties != null) {
			props.load(new FileInputStream(appProperties));
			setPropertyDefault(
					props,
					DATASOURCE_JNDI_PROP,
					"java:jboss/datasources/emiservDS");
			props.setProperty(DATASOURCE_JNDI_PROP2, props.getProperty(DATASOURCE_JNDI_PROP));
			setPropertyDefault(
					props,
					HIBERNATE_DIALECT_PROP,
					"org.hibernate.dialect.Oracle10gDialect");
			setPropertyDefault(props, "proxy.host", "");
			setPropertyDefault(props, "proxy.port", "");
			setPropertyDefault(props, "proxy.user", "");
			setPropertyDefault(props, "proxy.password", "");
			setPropertyDefault(props, "proxy.domain", "");
			setPropertyDefault(props, "proxy.non.proxy.hosts", "");
			removePrefixFromScspProperties(props);
		}
		return props;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	private void setPropertyDefault(Properties properties, String property, String value) {
		String propValue = properties.getProperty(property);
		if (propValue == null || propValue.trim().isEmpty()) {
			properties.setProperty(property, value);
		}
	}

	private void removePrefixFromScspProperties(Properties properties) {
		Map<String, String> modifiedProps = new HashMap<String, String>();
		for (Object key: properties.keySet()) {
			if (key instanceof String && ((String)key).startsWith(SCSP_PROP_PREFIX)) {
				modifiedProps.put(
						((String)key).substring(SCSP_PROP_PREFIX.length()),
						properties.getProperty((String)key));
			}
		}
		properties.putAll(modifiedProps);
		for (String key: modifiedProps.keySet()) {
			properties.remove(SCSP_PROP_PREFIX + key);
		}
	}

}
