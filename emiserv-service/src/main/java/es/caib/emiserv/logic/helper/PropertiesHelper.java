/**
 * 
 */
package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.dto.ConfigSourceEnumDto;
import es.caib.emiserv.persist.entity.ConfigEntity;
import es.caib.emiserv.persist.repository.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static es.caib.emiserv.logic.config.ReadDbPropertiesPostProcessor.DBAPP_PROPERTIES;

/**
 * Utilitats per a gestionar les propietats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class PropertiesHelper {

//	private static final String SCSP_PROP_PREFIX = "es.caib.emiserv.scsp.";

	@Autowired
	private ConfigurableEnvironment environment;
//	@Autowired
//	private ScspCoreParametroConfiguracionRepository scspCoreParametroConfiguracionRepository;
	@Autowired
	private ConfigRepository configRepository;

//	public void propagateScspPropertiesToDb() {
//		log.debug("Propagant propietats SCSP a la base de dades...");
//		Properties scspProperties = new Properties();
//		loadScspPropertiesFromSystemPropertyFile(ScspService.APP_PROPERTIES, scspProperties);
//		loadScspPropertiesFromSystemPropertyFile(ScspService.APP_SYSTEM_PROPERTIES, scspProperties);
//		List<String> processedScspProperties = new ArrayList<String>();
//		for (String propertyName: scspProperties.stringPropertyNames()) {
//			String paramName = propertyName.substring(SCSP_PROP_PREFIX.length());
//			String paramValor = scspProperties.getProperty(propertyName);
//			processedScspProperties.add(paramName);
//			Optional<ScspCoreParametroConfiguracionEntity> parametroConfiguracion = scspCoreParametroConfiguracionRepository.findById(
//					paramName);
//			if (!parametroConfiguracion.isPresent()) {
//				log.debug("Creant propietat SCSP (" +
//						"nom=" + paramName + ", " +
//						"valor=" + paramValor + ")");
//				scspCoreParametroConfiguracionRepository.save(
//						ScspCoreParametroConfiguracionEntity.builder().
//						nombre(paramName).
//						valor(paramValor).
//						build());
//			} else {
//				log.debug("Modificant propietat SCSP (" +
//						"nom=" + paramName + ", " +
//						"valor=" + paramValor + ")");
//				parametroConfiguracion.get().update(
//						paramValor,
//						parametroConfiguracion.get().getDescripcion());
//			}
//		}
//		// Eliminam de la base de dades les files que no estan presents a les properties
//		// A no ser que no s'hagin trobat els fitxerrs de propietats (scspProperties es buid)
//		if (!scspProperties.isEmpty()) {
//			List<ScspCoreParametroConfiguracionEntity> parametrosConfiguracion = scspCoreParametroConfiguracionRepository.findAll();
//			for (ScspCoreParametroConfiguracionEntity parametroConfiguracion : parametrosConfiguracion) {
//				if (!processedScspProperties.contains(parametroConfiguracion.getNombre())) {
//					log.debug("Eliminant propietat SCSP (" +
//							"nom=" + parametroConfiguracion.getNombre() + ")");
//					scspCoreParametroConfiguracionRepository.delete(parametroConfiguracion);
//				}
//			}
//		}
//		log.debug("...propagació de les propietats SCSP a la base de dades finalitzada.");
//	}

//	private void loadScspPropertiesFromSystemPropertyFile(String systemProperty, Properties scspProperties) {
//		String file = System.getProperty(systemProperty);
//		if (file != null) {
//			Properties props = new Properties();
//			try {
//				props.load(new FileInputStream(file));
//			} catch (IOException ignored) {}
//			for (Object key: props.keySet()) {
//				String propName = (String)key;
//				if (propName.startsWith(SCSP_PROP_PREFIX)) {
//					scspProperties.setProperty(propName, props.getProperty(propName));
//				}
//			}
//		}
//	}

	public void reloadDbProperties() {
		Map<String, Object> propertySource = new HashMap<>();

		List<ConfigEntity> dbProperties = configRepository.findBySourceProperty(ConfigSourceEnumDto.DATABASE);
		dbProperties.forEach(p -> propertySource.put(p.getKey(), p.getValue()));
		if (environment.getPropertySources().contains(DBAPP_PROPERTIES)) {
			environment.getPropertySources().replace(DBAPP_PROPERTIES, new MapPropertySource(DBAPP_PROPERTIES, propertySource));
		} else {
			environment.getPropertySources().addFirst(new MapPropertySource(DBAPP_PROPERTIES, propertySource));
		}
	}

}
