/**
 * 
 */
package es.caib.emiserv.back.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper per a gestionar el canvi de rol de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolHelper {

	private static final String ROLE_ADMIN = "EMS_ADMIN";
	private static final String ROLE_RESP = "EMS_RESP";

	private static final String REQUEST_PARAMETER_CANVI_ROL = "canviRol";
	private static final String SESSION_ATTRIBUTE_ROL_ACTUAL = "RolHelper.rol.actual";

	public static void processarCanviRols(
			HttpServletRequest request) {
		String canviRol = request.getParameter(REQUEST_PARAMETER_CANVI_ROL);
		if (canviRol != null && canviRol.length() > 0) {
			LOGGER.debug("Processant canvi rol (rol=" + canviRol + ")");
			if (request.isUserInRole(canviRol)) {
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_ROL_ACTUAL,
						canviRol);
			}
		}
	}

	public static String getRolActual(HttpServletRequest request) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		List<String> rolsDisponibles = getRolsUsuariActual(request);
		if (rolActual == null || !rolsDisponibles.contains(rolActual)) {
			if (request.isUserInRole(ROLE_ADMIN) && rolsDisponibles.contains(ROLE_ADMIN)) {
				rolActual = ROLE_ADMIN;
			} else if (request.isUserInRole(ROLE_RESP) && rolsDisponibles.contains(ROLE_RESP)) {
				rolActual = ROLE_RESP;
			}
			if (rolActual != null)
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_ROL_ACTUAL,
						rolActual);
		}
		LOGGER.debug("Obtenint rol actual (rol=" + rolActual + ")");
		return rolActual;
	}

	public static boolean isUsuariActualAdministrador(HttpServletRequest request) {
		return ROLE_ADMIN.equals(getRolActual(request));
	}
	public static boolean isUsuariActualResponsable(HttpServletRequest request) {
		return ROLE_RESP.equals(getRolActual(request));
	}

	public static List<String> getRolsUsuariActual(HttpServletRequest request) {
		LOGGER.debug("Obtenint rols disponibles per a l'usuari actual");
		List<String> rols = new ArrayList<String>();
		if (request.isUserInRole(ROLE_ADMIN))
			rols.add(ROLE_ADMIN);
		if (request.isUserInRole(ROLE_RESP))
			rols.add(ROLE_RESP);
		return rols;
	}

	public static void esborrarRolActual(HttpServletRequest request) {
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
	}

	/**
	 * Rol actual per a la interfície REACT, a partir dels rols de l'usuari a l'API (les autoritats de
	 * Spring Security, que també funcionen en arrencar des de l'IDE). Fa servir el mateix atribut de
	 * sessió que la JSP: canviar el rol en una interfície el canvia a l'altra. Sense rol a la sessió (o
	 * si ja no el té), l'administrador i, si no, el responsable.
	 */
	public static String getRolActual(HttpServletRequest request, List<String> rolsDisponibles) {
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		if (rolActual == null || !rolsDisponibles.contains(rolActual)) {
			rolActual = rolsDisponibles.contains(ROLE_ADMIN) ? ROLE_ADMIN : rolsDisponibles.contains(ROLE_RESP) ? ROLE_RESP : null;
			if (rolActual != null) {
				request.getSession().setAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL, rolActual);
			}
		}
		return rolActual;
	}

	/** Rols d'EMISERV (administrador i responsable) de l'usuari autenticat, segons les seves autoritats. */
	public static List<String> getRolsAutoritats() {
		List<String> rols = new ArrayList<String>();
		org.springframework.security.core.Authentication auth =
				org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			java.util.Set<String> autoritats = new java.util.HashSet<String>();
			for (org.springframework.security.core.GrantedAuthority autoritat: auth.getAuthorities()) {
				autoritats.add(autoritat.getAuthority());
			}
			if (autoritats.contains(ROLE_ADMIN))
				rols.add(ROLE_ADMIN);
			if (autoritats.contains(ROLE_RESP))
				rols.add(ROLE_RESP);
		}
		return rols;
	}

	/** Canvia el rol actual (interfície REACT); només si l'usuari el té. */
	public static boolean setRolActual(HttpServletRequest request, List<String> rolsDisponibles, String rol) {
		if (rol == null || !rolsDisponibles.contains(rol)) {
			return false;
		}
		request.getSession().setAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL, rol);
		return true;
	}

	public static String getRequestParameterCanviRol() {
		return REQUEST_PARAMETER_CANVI_ROL;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RolHelper.class);

}
