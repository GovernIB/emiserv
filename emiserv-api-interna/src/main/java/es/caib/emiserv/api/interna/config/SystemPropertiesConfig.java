package es.caib.emiserv.api.interna.config;

import es.caib.emiserv.logic.intf.service.ScspService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(ignoreResourceNotFound = true, value = {
		"file://${" + ScspService.APP_PROPERTIES + "}",
		"file://${" + ScspService.APP_SYSTEM_PROPERTIES + "}"})
public class SystemPropertiesConfig {
}
