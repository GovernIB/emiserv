package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.base.config.ApiOrigenFilter;
import es.caib.emiserv.logic.intf.base.config.BaseBootConfig;
import es.caib.emiserv.back.helper.LocaleHelper;
import es.caib.emiserv.back.helper.RolHelper;
import es.caib.emiserv.logic.intf.dto.IdiomaEnumDto;
import es.caib.emiserv.logic.intf.dto.InterficieUsuariEnumDto;
import es.caib.emiserv.logic.intf.dto.TemaAplicacioEnumDto;
import es.caib.emiserv.logic.intf.keycloak.KeycloakHelper;
import es.caib.emiserv.logic.intf.service.AplicacioService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Usuari de la sessio de servidor amb que opera la interficie REACT.
 * <p>
 * La interficie REACT no gestiona cap token al navegador: s'autentica amb la mateixa sessio HTTP
 * que la interficie JSP. Aquest endpoint li dona el codi, nom, correu i rols de l'usuari i li
 * serveix per a comprovar si la sessio continua viva.
 *
 * @author Limit Tecnologies
 */
@Hidden
@RestController
public class SessioUsuariController {

	private final AplicacioService aplicacioService;
	private final List<String> rolsInterficie;
	/** Origens, a mes del propi, a on es permet tornar despres del login (p. ex. el servidor de Vite). */
	private final List<String> origensPermesos;

	@Autowired
	public SessioUsuariController(
			AplicacioService aplicacioService,
			@org.springframework.beans.factory.annotation.Value("${es.caib.emiserv.security.mappableRoles:EMS_ADMIN,EMS_RESP}") String mappableRoles,
			@org.springframework.beans.factory.annotation.Value("${" + ApiOrigenFilter.PROPIETAT_ORIGENS + ":}") String origensPermesos) {
		this.aplicacioService = aplicacioService;
		this.rolsInterficie = parseRols(mappableRoles);
		this.origensPermesos = ApiOrigenFilter.parseOrigens(origensPermesos);
	}

	/**
	 * Converteix la propietat de rols separats per comes en una llista de rols.
	 *
	 * @param mappableRoles
	 *            els rols separats per comes.
	 * @return la llista de rols sense espais.
	 */
	static List<String> parseRols(String mappableRoles) {
		return Arrays.stream(mappableRoles.split(",")).
				map(String::trim).
				filter(r -> !r.isEmpty()).
				collect(Collectors.toList());
	}

	@GetMapping(BaseBootConfig.API_PATH + "/sessioUsuari")
	public ResponseEntity<SessioUsuari> sessioUsuari(HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Set<String> autoritats = auth.getAuthorities().stream().
				map(GrantedAuthority::getAuthority).
				collect(Collectors.toSet());
		List<String> rols = rolsInterficie.stream().
				filter(autoritats::contains).
				collect(Collectors.toList());
		InterficieUsuariEnumDto efectiva = aplicacioService.getInterficieEfectiva();
		InterficieUsuariEnumDto preferida = aplicacioService.getInterficiePreferida();
		return ResponseEntity.ok(new SessioUsuari(
				auth.getName(),
				KeycloakHelper.getCurrentUserFullName(),
				KeycloakHelper.getCurrentUserEmail(),
				rols,
				efectiva != null ? efectiva.name() : null,
				preferida != null ? preferida.name() : null,
				idiomaUsuari(),
				temaUsuari(),
				RolHelper.getRolActual(request, RolHelper.getRolsAutoritats())));
	}

	/**
	 * Canvia el rol actual (administrador o responsable), com el selector de rol de la JSP i
	 * compartit amb ella. Com a la JSP, el rol actual decideix què mostra la interfície; els permisos
	 * del servidor són els dels rols de l'usuari.
	 */
	@PutMapping(BaseBootConfig.API_PATH + "/sessioUsuari/rol")
	public ResponseEntity<Void> updateRol(@RequestBody RolRequest rol, HttpServletRequest request) {
		if (!RolHelper.setRolActual(request, RolHelper.getRolsAutoritats(), rol.getRol())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.noContent().build();
	}

	/**
	 * Desa les preferències del perfil de la interfície REACT: idioma (el mateix que el perfil de la
	 * JSP, CA o ES) i tema. Un valor null esborra la preferència. L'idioma s'aplica també a la sessió
	 * (com en desar el perfil de la JSP), perquè la JSP i els missatges de l'API el facin servir.
	 */
	@PutMapping(BaseBootConfig.API_PATH + "/sessioUsuari/preferencies")
	public ResponseEntity<Void> updatePreferencies(
			@RequestBody PreferenciesRequest preferencies,
			HttpServletRequest request,
			HttpServletResponse response) {
		aplicacioService.updateIdiomaUsuariActual(preferencies.getIdioma() != null ? preferencies.getIdioma().name() : null);
		aplicacioService.updateTemaUsuariActual(preferencies.getTema());
		LocaleHelper.processarLocale(request, response, aplicacioService, true);
		return ResponseEntity.noContent().build();
	}

	/** Idioma del perfil (ca o es), o null si l'usuari no n'ha triat cap. */
	private String idiomaUsuari() {
		String idioma = aplicacioService.getIdiomaUsuariActual();
		return idioma != null && !idioma.trim().isEmpty() ? idioma.trim().toLowerCase() : null;
	}

	private String temaUsuari() {
		TemaAplicacioEnumDto tema = aplicacioService.getTemaUsuariActual();
		return tema != null ? tema.name() : null;
	}

	/**
	 * Canvia la interficie preferida de l'usuari. Un valor null recupera la per defecte.
	 */
	@PutMapping(BaseBootConfig.API_PATH + "/sessioUsuari/interficie")
	public ResponseEntity<Void> updateInterficie(@RequestBody InterficieRequest request) {
		aplicacioService.updateInterficie(request.getInterficie());
		return ResponseEntity.noContent().build();
	}

	/**
	 * Inicia la sessio i torna a la URL indicada.
	 * <p>
	 * No fa res per si mateix: com qualsevol altra URL de l'aplicacio requereix autenticacio, aixi
	 * que sense sessio el contenidor (o Spring Security) passa pel login i hi torna en acabar.
	 * No es sota {@code /api} perque alla una peticio sense sessio rep un 401 en lloc de la
	 * redireccio al login.
	 * <p>
	 * Nomes es torna a camins relatius al mateix servidor, a URLs del mateix origen o a
	 * als origens de la propietat {@code es.caib.emiserv.front.dev.origins}. Qualsevol altra (o cap) porta a {@code /reactapp}: aixi
	 * l'endpoint no es pot fer servir per a redirigir a un lloc extern.
	 */
	@GetMapping("/sessioUsuari/login")
	public ResponseEntity<Void> login(
			@RequestParam(required = false) String redirect,
			HttpServletRequest request) {
		String desti = isRedirectPermes(redirect, request) ?
				redirect :
				request.getContextPath() + ReactController.REACT_APP_PATH;
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(desti)).build();
	}

	private boolean isRedirectPermes(String redirect, HttpServletRequest request) {
		if (redirect == null || redirect.trim().isEmpty() || redirect.indexOf('\\') >= 0) {
			return false;
		}
		try {
			URI uri = new URI(redirect);
			if (!uri.isAbsolute()) {
				// Cami relatiu al mateix servidor; "//host" seria un altre servidor.
				return redirect.startsWith("/") && !redirect.startsWith("//");
			}
			if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
				return false;
			}
			String origen = uri.getScheme() + "://" + uri.getRawAuthority();
			int port = request.getServerPort();
			boolean portPerDefecte = ("http".equals(request.getScheme()) && port == 80) || ("https".equals(request.getScheme()) && port == 443);
			String origenPropi = request.getScheme() + "://" + request.getServerName() + (portPerDefecte ? "" : ":" + port);
			return origen.equalsIgnoreCase(origenPropi) || ApiOrigenFilter.isOrigenPermes(origen, origensPermesos);
		} catch (URISyntaxException ex) {
			return false;
		}
	}

	@lombok.Value
	public static class SessioUsuari {
		String codi;
		String nom;
		String email;
		List<String> rols;
		String interficie;
		String interficiePreferida;
		/** Idioma del perfil (ca o es); null: el del navegador. */
		String idioma;
		/** Tema de la interfície REACT (CLAR, OBSCUR, DRACULA o SISTEMA); null: el del sistema. */
		String tema;
		/** Rol actual (EMS_ADMIN o EMS_RESP), el mateix que el selector de rol de la JSP. */
		String rolActual;
	}

	@Data
	public static class RolRequest {
		private String rol;
	}

	@Data
	public static class PreferenciesRequest {
		private IdiomaEnumDto idioma;
		private TemaAplicacioEnumDto tema;
	}

	@Data
	public static class InterficieRequest {
		private InterficieUsuariEnumDto interficie;
	}

}
