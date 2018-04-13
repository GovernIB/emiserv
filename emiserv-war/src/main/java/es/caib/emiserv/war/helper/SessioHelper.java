/**
 * 
 */
package es.caib.emiserv.war.helper;


/**
 * Utilitat per a gestionar accions de context de sessió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SessioHelper {

	public static final String SESSION_ATTRIBUTE_AUTH_PROCESSADA = "SessioHelper.autenticacioProcessada";
	//public static final String SESSION_ATTRIBUTE_CONTENIDOR_VISTA = "SessioHelper.contenidorVista";



	/*public static void processarAutenticacio(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		Boolean autenticacioProcessada = (Boolean)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_AUTH_PROCESSADA);
		if (autenticacioProcessada == null) {
			aplicacioService.processarAutenticacioUsuari();
			request.getSession().setAttribute(
					SESSION_ATTRIBUTE_AUTH_PROCESSADA,
					new Boolean(true));
		}
	}
	public static boolean isAutenticacioProcessada(HttpServletRequest request) {
		return request.getSession().getAttribute(SESSION_ATTRIBUTE_AUTH_PROCESSADA) != null;
	}

	public static void updateContenidorVista(
			HttpServletRequest request,
			String vista) {
		request.getSession().setAttribute(
				SESSION_ATTRIBUTE_CONTENIDOR_VISTA,
				vista);
	}
	public static String getContenidorVista(HttpServletRequest request) {
		return (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_CONTENIDOR_VISTA);
	}*/

}
