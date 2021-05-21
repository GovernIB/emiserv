/**
 * 
 */
package es.caib.emiserv.back.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Helper per a mostrar missatges d'alerta o informació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MissatgeHelper {

	public static final String SESSION_ATTRIBUTE_ERROR = "MissatgeHelper.Error";
	public static final String SESSION_ATTRIBUTE_WARNING = "MissatgeHelper.Warning";
	public static final String SESSION_ATTRIBUTE_SUCCESS = "MissatgeHelper.Success";
	public static final String SESSION_ATTRIBUTE_INFO = "MissatgeHelper.Info";

	public static void error(
			HttpServletRequest request,
			String text) {
		newMissatge(
				request,
				SESSION_ATTRIBUTE_ERROR,
				text);
	}
	public static void warning(
			HttpServletRequest request,
			String text) {
		newMissatge(
				request,
				SESSION_ATTRIBUTE_WARNING,
				text);
	}
	public static void success(
			HttpServletRequest request,
			String text) {
		newMissatge(
				request,
				SESSION_ATTRIBUTE_SUCCESS,
				text);
	}
	public static void info(
			HttpServletRequest request,
			String text) {
		newMissatge(
				request,
				SESSION_ATTRIBUTE_INFO,
				text);
	}

	public List<String> getErrors(
			HttpServletRequest request,
			boolean delete) {
		return getMissatges(
				request,
				SESSION_ATTRIBUTE_ERROR,
				delete);
	}
	public List<String> getWarnings(
			HttpServletRequest request,
			boolean delete) {
		return getMissatges(
				request,
				SESSION_ATTRIBUTE_WARNING,
				delete);
	}
	public List<String> getSuccesses(
			HttpServletRequest request,
			boolean delete) {
		return getMissatges(
				request,
				SESSION_ATTRIBUTE_SUCCESS,
				delete);
	}
	public List<String> getInfos(
			HttpServletRequest request,
			boolean delete) {
		return getMissatges(
				request,
				SESSION_ATTRIBUTE_INFO,
				delete);
	}


	@SuppressWarnings("unchecked")
	private static void newMissatge(
			HttpServletRequest request,
			String attributeName,
			String text) {
		HttpSession session = request.getSession();
		List<String> alerts = (List<String>)session.getAttribute(attributeName);
		if (alerts == null) {
			alerts = new ArrayList<String>();
			session.setAttribute(attributeName, alerts);
		}
		alerts.add(text);
	}

	@SuppressWarnings("unchecked")
	private static List<String> getMissatges(
			HttpServletRequest request,
			String attributeName,
			boolean delete) {
		HttpSession session = request.getSession();
		List<String> alerts = (List<String>)session.getAttribute(attributeName);
		if (delete)
			session.removeAttribute(attributeName);
		return alerts;
	}

}
