package es.caib.emiserv.back.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Accés als fitxers del SPA de la interfície REACT.
 * <p>
 * Els fitxers es cerquen primer a l'aplicació web ({@link ServletContext}: carpeta
 * {@code reactapp} del WAR) i, si no hi són, al classpath sota {@value #CLASSPATH_PREFIX} (el JAR
 * {@code emiserv-back-*-reactapp.jar} que inclou {@code emiserv-boot}).
 * <p>
 * Un build sense el perfil {@code front} genera un WAR/JAR sense el SPA. En aquest cas
 * {@link #isDisponible()} retorna false perquè les entrades a la interfície REACT portin a la JSP
 * en lloc d'acabar en un 404.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Component
public class ReactAppHelper {

	public static final String REACT_APP_PATH = "/reactapp";
	public static final String INDEX_HTML = REACT_APP_PATH + "/index.html";
	static final String CLASSPATH_PREFIX = "META-INF/resources";

	private final ServletContext servletContext;
	private final String classpathPrefix;
	private final AtomicBoolean errorRegistrat = new AtomicBoolean();

	@Autowired
	public ReactAppHelper(ServletContext servletContext) {
		this(servletContext, CLASSPATH_PREFIX);
	}

	/** Amb un prefix del classpath diferent (proves). */
	public ReactAppHelper(ServletContext servletContext, String classpathPrefix) {
		this.servletContext = servletContext;
		this.classpathPrefix = classpathPrefix;
	}

	/**
	 * Obre un fitxer del SPA: de l'aplicació web o, si no hi és, del classpath.
	 *
	 * @param path
	 *            el camí del fitxer dins l'aplicació (p. ex. {@code /reactapp/index.html}).
	 * @return el contingut o null si no existeix (o si és una carpeta).
	 * @throws IOException
	 *             si no es pot llegir.
	 */
	public InputStream obrirRecurs(String path) throws IOException {
		InputStream resource = servletContext.getResourceAsStream(path);
		if (resource != null) {
			return resource;
		}
		ClassPathResource classPathResource = new ClassPathResource(classpathPrefix + path);
		// isReadable() és false per a les carpetes (també dins d'un JAR)
		return classPathResource.isReadable() ? classPathResource.getInputStream() : null;
	}

	/**
	 * Indica si el SPA està inclòs a l'aplicació (hi ha {@code index.html}). Si no hi és, ho
	 * registra al log com a error un sol cop.
	 *
	 * @return true si el SPA està disponible.
	 */
	public boolean isDisponible() {
		try (InputStream index = obrirRecurs(INDEX_HTML)) {
			if (index != null) {
				return true;
			}
		} catch (IOException ex) {
			log.warn("Error comprovant {}", INDEX_HTML, ex);
		}
		if (errorRegistrat.compareAndSet(false, true)) {
			log.error("No s'ha trobat {} ni a l'aplicació web ({}) ni al classpath ({}): l'aplicació s'ha " +
					"construït sense la interfície REACT (perfil Maven 'front'). Es redirigeix a la interfície JSP.",
					INDEX_HTML,
					servletContext.getRealPath("/"),
					classpathPrefix + REACT_APP_PATH);
		}
		return false;
	}

}
