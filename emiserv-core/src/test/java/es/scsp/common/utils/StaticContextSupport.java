package es.scsp.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ContextLoader;

import es.caib.emiserv.core.scsp.ApplicationContextProvider;
import es.scsp.common.exceptions.ScspException;

public class StaticContextSupport {
	private static final Log LOG = LogFactory.getLog(StaticContextSupport.class);

	private static ApplicationContext instance;

	public static synchronized void setContextInstance(ApplicationContext value) throws ScspException {
		if (instance != null) {
			throw new ScspException("No se puede generar mas de una instancia del contexto.", "0504");
		}
		instance = value;
	}

	public static synchronized ApplicationContext getContextInstance() {
		if (instance != null) {
			return instance;
		}
		instance = ContextLoader.getCurrentWebApplicationContext();
		if (instance != null) {
			return instance;
		}

		LOG.debug("Creando instancia de Spring ApplicationContext a partir del recurso del classpath.");
		ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
		if (ctx == null) {
			if (StaticContextSupport.class.getResourceAsStream("/applicationContext-scsp.xml") != null) {
				ctx = new ClassPathXmlApplicationContext("applicationContext-scsp.xml");
			} else if (StaticContextSupport.class.getResourceAsStream("/applicationContext.xml") != null) {
				ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
			}
		}
		if (ctx instanceof ClassPathXmlApplicationContext) {
			ClassLoader classLoader = ApplicationContext.class.getClassLoader();
			ClassPathXmlApplicationContext cpxCtx = (ClassPathXmlApplicationContext)ctx;
			cpxCtx.setClassLoader(classLoader);
			cpxCtx.refresh();
		}
		instance = ctx;
		return instance;
	}
}
