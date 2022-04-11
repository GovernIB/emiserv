package es.caib.emiserv.back.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API EMISERV")
                        .description("API de Consulta d'EMISERV")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Limit Tecnologies")
                                .url("http://limit.es")
                                .email("limit@limit.es"))
                        .termsOfService("TOC")
                        .license(new License().name("Llicència").url("#"))
                );
    }
}
