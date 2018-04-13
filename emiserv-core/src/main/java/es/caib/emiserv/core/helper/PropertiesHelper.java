/**
 * 
 */
package es.caib.emiserv.core.helper;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitat per accedir a les entrades del fitxer de properties.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PropertiesHelper extends Properties {

	private static final String APPSERV_PROPS_PATH = "es.caib.emiserv.properties.path";

	private static PropertiesHelper instance = null;

	private boolean llegirSystem = true;


	public static PropertiesHelper getProperties() {
		return getProperties(null);
	}
	public static PropertiesHelper getProperties(String path) {
		String propertiesPath = path;
		if (propertiesPath == null) {
			propertiesPath = System.getProperty(APPSERV_PROPS_PATH);
		}
		if (instance == null) {
			instance = new PropertiesHelper();
			if (propertiesPath != null) {
				instance.llegirSystem = false;
				logger.info("Llegint les propietats de l'aplicació del path: " + propertiesPath);
				try {
					if (propertiesPath.startsWith("classpath:")) {
						instance.load(
								PropertiesHelper.class.getClassLoader().getResourceAsStream(
										propertiesPath.substring("classpath:".length())));
					} else if (propertiesPath.startsWith("file://")) {
						FileInputStream fis = new FileInputStream(
								propertiesPath.substring("file://".length()));
						instance.load(fis);
					} else {
						FileInputStream fis = new FileInputStream(propertiesPath);
						instance.load(fis);
					}
				} catch (Exception ex) {
					logger.error("No s'han pogut llegir els properties", ex);
				}
			}
		}
		return instance;
	}

	public String getProperty(String key) {
		if (llegirSystem)
			return System.getProperty(key);
		else
			return super.getProperty(key);
	}
	public String getProperty(String key, String defaultValue) {
		String val = getProperty(key);
        return (val == null) ? defaultValue : val;
	}

	public boolean getAsBoolean(String key) {
		String value = getProperty(key);
		if (value != null)
			return new Boolean(getProperty(key)).booleanValue();
		else
			return false;
	}
	public int getAsInt(String key) {
		return new Integer(getProperty(key)).intValue();
	}
	public long getAsLong(String key) {
		return new Long(getProperty(key)).longValue();
	}
	public float getAsFloat(String key) {
		return new Float(getProperty(key)).floatValue();
	}
	public double getAsDouble(String key) {
		return new Double(getProperty(key)).doubleValue();
	}

	public Map<String, String> findByPrefix(String prefix) {
		Map<String, String> properties = new HashMap<String, String>();
		if (llegirSystem) {
			for (Object key: System.getProperties().keySet()) {
				if (key instanceof String) {
					String keystr = (String)key;
					if (keystr.startsWith(prefix)) {
						properties.put(
								keystr,
								System.getProperty(keystr));
					}
				}
			}
		} else {
			for (Object key: this.keySet()) {
				if (key instanceof String) {
					String keystr = (String)key;
					if (keystr.startsWith(prefix)) {
						properties.put(
								keystr,
								getProperty(keystr));
					}
				}
			}
		}
		return properties;
	}

	private static final Logger logger = LoggerFactory.getLogger(PropertiesHelper.class);
	private static final long serialVersionUID = 1L;

}
