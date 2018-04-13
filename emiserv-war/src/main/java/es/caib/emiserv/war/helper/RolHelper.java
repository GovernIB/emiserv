/**
 * 
 */
package es.caib.emiserv.war.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitat per a gestionar el canvi de rol de l'usuari actual.
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

	public static String getRequestParameterCanviRol() {
		return REQUEST_PARAMETER_CANVI_ROL;
	}



	private static final Logger LOGGER = LoggerFactory.getLogger(RolHelper.class);

}
