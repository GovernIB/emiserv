package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.base.controller.BaseUtilsController;
import es.caib.emiserv.back.helper.ReactAppHelper;
import es.caib.emiserv.logic.intf.base.config.PropertyConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

/**
 * Serveix el SPA de la interficie REACT ({@code /reactapp/**}) i exposa els endpoints de
 * {@link BaseUtilsController} ({@code /sysenv}, {@code /manifest}, ...) que l'index.html
 * llegeix com a scripts.
 * <p>
 * Tots aquests camins requereixen sessio (l'SPA nomes s'obre despres del login), aixi que no
 * cal exposar la configuracio abans d'autenticar-se. {@code /sysenv} nomes publica les
 * propietats de {@link PropertyConfig#REACT_APP_PROPS_MAP} i {@link PropertyConfig#VITE_PROPS_MAP}.
 * <p>
 * Els fitxers del SPA es cerquen amb {@link ReactAppHelper} (aplicacio web i, si no hi son,
 * classpath). Si l'aplicacio s'ha construit sense el SPA, les rutes del SPA redirigeixen a la
 * interficie JSP en lloc de donar un 404.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Controller
public class ReactController extends BaseUtilsController {

	public static final String REACT_APP_PATH = ReactAppHelper.REACT_APP_PATH;
	/** Pantalla de la interficie JSP on es va si no hi ha SPA (la mateixa que IndexController). */
	static final String JSP_INICI_PATH = "/servei";

	@Autowired
	private ServletContext servletContext;
	@Autowired
	private ReactAppHelper reactAppHelper;
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
			InputStream resource = path.endsWith("/") ? null : reactAppHelper.obrirRecurs(path);
			if (resource != null) {
				String mimeType = servletContext.getMimeType(path);
				MediaType mediaType = mimeType != null ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;
				return ResponseEntity.ok().contentType(mediaType).body(new InputStreamResource(resource));
			}
			// Recurs estatic inexistent: 404
			if (uri.matches(".*\\.(js|css|ico|png|jpg|svg|woff2?|map)$") || uri.endsWith("index.html")) {
				return ResponseEntity.notFound().build();
			}
			// Ruta del SPA: retornam index.html
			InputStream indexHtml = reactAppHelper.isDisponible() ? reactAppHelper.obrirRecurs(ReactAppHelper.INDEX_HTML) : null;
			if (indexHtml == null) {
				// Aplicacio construida sense el SPA (isDisponible ja ho registra al log): a la JSP
				return ResponseEntity.status(HttpStatus.FOUND).
						location(URI.create(request.getContextPath() + JSP_INICI_PATH)).
						build();
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
