/**
 * 
 */
package es.caib.emiserv.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import es.caib.emiserv.logic.intf.service.ScspService;

/**
 * Configuració de les propietats de l'aplicació a partir de la configuració
 * de les propietats de sistema (System.getProperty).
 * 
 * @author Limit Tecnologies
 */
@Configuration
@PropertySource(ignoreResourceNotFound = true, value = {
	"${" + ScspService.APP_PROPERTIES + "}",
	"${" + ScspService.APP_SYSTEM_PROPERTIES + "}"})
public class SystemPropertiesConfig {

	/*public static class ScspPropertiesEventListener implements ApplicationListener<ApplicationPreparedEvent> {
		@Autowired
		private ScspService scspService;
		@Override
		public void onApplicationEvent(ApplicationPreparedEvent event) {
			scspService.propagateScspPropertiesToDb();
		}
    }*/

}
