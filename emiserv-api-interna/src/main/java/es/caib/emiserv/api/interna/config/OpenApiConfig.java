package es.caib.emiserv.api.interna.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Interna EMISERV",
                description = "API Interna de Consulta d'EMISERV",
                version = "v1.0",
                contact = @Contact(name = "Limit Tecnologies", url = "http://limit.es", email = "limit@limit.es")
        ),
        security = @SecurityRequirement(name = OpenApiConfig.SECURITY_NAME),
        servers = {
                @Server(url = "/emiservapi/interna", description = "Servidor per defecte"),
                @Server(url = "https://proves.caib.es/emiservapi/interna", description = "Servidor de l'entorn de PROVES")
        }
)
@SecurityScheme(name = OpenApiConfig.SECURITY_NAME, scheme = OpenApiConfig.SECURITY_SCHEME, type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {

        public static final String SECURITY_NAME = "basicAuth";
        public static final String SECURITY_SCHEME = "basic";

}
