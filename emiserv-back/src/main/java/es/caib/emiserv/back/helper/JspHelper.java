/**
 * 
 */
package es.caib.emiserv.back.helper;

import es.caib.emiserv.logic.intf.keycloak.KeycloakHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Helper per a obtenir informació des de les pàgines JSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class JspHelper {

	public static String getApplicationVersion(HttpServletRequest request) throws IOException {
		return getManifestAttribute(request, "Implementation-Version");
	}

	public static String getBuildTimestamp(HttpServletRequest request) throws IOException {
		return getManifestAttribute(request, "Build-Timestamp");
	}

	public static String getCommitId(HttpServletRequest request) throws IOException {
		return getManifestAttribute(request, "Implementation-SCM-Revision");
	}

	public static String getCurrentUserName() {
		return KeycloakHelper.getCurrentUserFullName();
	}

	private static String getManifestAttribute(
			HttpServletRequest request,
			String attributeName) throws IOException {
		InputStream is = request.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF");
		if (is != null) {
			Manifest manifest = new Manifest(is);
			Attributes attributes = manifest.getMainAttributes();
			return attributes.getValue(attributeName);
		} else {
			return "";
		}
	}

}
