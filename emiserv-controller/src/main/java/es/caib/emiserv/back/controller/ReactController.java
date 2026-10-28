package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.base.controller.BaseUtilsController;
import es.caib.emiserv.logic.intf.base.config.PropertyConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;

/**
 * Serveix el SPA de la interficie REACT ({@code /reactapp/**}) i exposa els endpoints de
 * {@link BaseUtilsController} ({@code /sysenv}, {@code /manifest}, ...) que l'index.html
 * llegeix com a scripts.
 * <p>
 * Tots aquests camins requereixen sessio (l'SPA nomes s'obre despres del login), aixi que no
 * cal exposar la configuracio abans d'autenticar-se. {@code /sysenv} nomes publica les
 * propietats de {@link PropertyConfig#REACT_APP_PROPS_MAP} i {@link PropertyConfig#VITE_PROPS_MAP}.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Controller
public class ReactController extends BaseUtilsController {

	public static final String REACT_APP_PATH = "/reactapp";

	@Autowired
	private ServletContext servletContext;
	@Value("${es.caib.emiserv.security.mappableRoles:EMS_ADMIN,EMS_RESP}")
	private String mappableRoles;

	@RequestMapping(REACT_APP_PATH + "/**")
	public ResponseEntity<?> serveReact(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String path = uri.substring(request.getContextPath().length());
		try {
			if (path.contains("..")) {
				return ResponseEntity.notFound().build();
			}
			InputStream resource = servletContext.getResourceAsStream(path);
			if (resource != null && !path.endsWith("/")) {
				String mimeType = servletContext.getMimeType(path);
				MediaType mediaType = mimeType != null ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;
				return ResponseEntity.ok().contentType(mediaType).body(new InputStreamResource(resource));
			}
			// Recurs estatic inexistent: 404
			if (uri.matches(".*\\.(js|css|ico|png|jpg|svg|woff2?|map)$") || uri.endsWith("index.html")) {
				return ResponseEntity.notFound().build();
			}
			// Ruta del SPA: retornam index.html
			InputStream indexHtml = servletContext.getResourceAsStream(REACT_APP_PATH + "/index.html");
			if (indexHtml == null) {
				log.error("No s'ha trobat {}/index.html a l'aplicacio web ({}): el SPA no esta construit",
						REACT_APP_PATH,
						servletContext.getRealPath("/"));
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(new InputStreamResource(indexHtml));
		} catch (Exception ex) {
			log.error("Error carregant recurs", ex);
			return ResponseEntity.internalServerError().body("Error carregant recurs");
		}
	}

	@Override
	protected String getAuthToken() {
		// Autenticacio per sessio: no hi ha token al navegador.
		return null;
	}

	@Override
	protected String[] getAuthRoles() {
		List<String> rols = SessioUsuariController.parseRols(mappableRoles);
		return rols.toArray(new String[0]);
	}

	@Override
	protected boolean isReactAppMappedFrontProperty(String propertyName) {
		return PropertyConfig.REACT_APP_PROPS_MAP.containsKey(propertyName);
	}

	@Override
	protected String getReactAppMappedFrontProperty(String propertyName) {
		return PropertyConfig.REACT_APP_PROPS_MAP.get(propertyName);
	}

	@Override
	protected boolean isViteMappedFrontProperty(String propertyName) {
		return PropertyConfig.VITE_PROPS_MAP.containsKey(propertyName);
	}

	@Override
	protected String getViteMappedFrontProperty(String propertyName) {
		return PropertyConfig.VITE_PROPS_MAP.get(propertyName);
	}

}
