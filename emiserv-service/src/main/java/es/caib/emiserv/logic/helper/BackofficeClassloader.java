package es.caib.emiserv.logic.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class BackofficeClassloader extends ClassLoader {

    @Value("${es.caib.emiserv.backoffice.jar.path:}")
    public String backofficeJarPath;

    public BackofficeClassloader() {
        super(getContextClassloader());
        if (backofficeJarPath == null || backofficeJarPath.isEmpty()) {
            backofficeJarPath = getDefaultBackofficeJarPath();
        }
    }

    @Override
    public Enumeration<URL> getResources(String resourceName) throws IOException {

        Set<URL> urls = new HashSet<>();
        if (!resourceName.equals("META-INF/spring.components")) {
            urls = getBackofficeJarResourceUrls();
        }

        Enumeration<URL> resources = getContextClassloader().getResources(resourceName);
        urls.addAll(Collections.list(resources));
        return Collections.enumeration(urls);

    }

    private String getDefaultBackofficeJarPath() {
        String serverResourcePath = BackofficeClassloader.class.getClassLoader().getResource("").getPath();
        if (serverResourcePath != null && !serverResourcePath.isEmpty()) {
            if (serverResourcePath.contains("/modules/"))
                return BackofficeClassloader.class.getClassLoader().getResource("").getPath().substring(0, BackofficeClassloader.class.getClassLoader().getResource("").getPath().indexOf("/modules/")) + "/backoffice/backoffice.jar";
            else
                return serverResourcePath + "/backoffice.jar";
        } else {
            return new File("").getAbsolutePath() + "/backoffice.jar";
        }
    }

    private Set<URL> getBackofficeJarResourceUrls() throws IOException {

        Set<URL> urls = new HashSet<>();

        File backofficeFile = new File(backofficeJarPath);
        if (backofficeFile.exists()) {
            urls.add(new URL("jar:file:" + backofficeJarPath + "!/"));
        }

        return urls;
    }

    private static ClassLoader getContextClassloader() {
        return System.getSecurityManager() != null ?
                AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader()) :
                Thread.currentThread().getContextClassLoader();
    }

}
