package es.scsp.backoffice;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BackofficeClassloader extends ClassLoader {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BackofficeClassloader.class);

    private String backofficeJarPath;

    public BackofficeClassloader() {
        super(getContextClassloader());
        backofficeJarPath = getDefaultBackofficeJarPath();

        File backofficeFile = new File(backofficeJarPath);
        if (!backofficeFile.exists()) {
            log.warn("BKO - La llibreria de backoffice '{}' no existeix.", backofficeJarPath);
        } else {
            log.info("BKO - Configurat el classloader amb la llibreria de backoffice: {}", backofficeJarPath);
        }
    }

    public void setBackofficeJarPath(String backofficeJarPath) {

        File backofficeFile = new File(backofficeJarPath);
        if (backofficeFile.exists()) {
            this.backofficeJarPath = backofficeJarPath;
            log.info("BKO - Configurat el classloader amb la llibreria de backoffice: {}", backofficeJarPath);
        } else {
            log.warn("BKO - La llibreria de backoffice '{}' no existeix.", backofficeJarPath);
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

    private Set<URL> getBackofficeJarResourceUrls() throws IOException {

        Set<URL> urls = new HashSet<>();

        File backofficeFile = new File(backofficeJarPath);
        if (backofficeFile.exists()) {
            urls.add(new URL("jar:file:" + backofficeJarPath + "!/"));
        }

        return urls;
    }

    @Override
    public Class findClass(String name) throws ClassNotFoundException {
        Class<?> clazz = null;
        Throwable throwable = null;
        byte[] b = null;

        try {
            b = loadClassFromBackofficeJar(name);
        } catch (Throwable ex) {
            throwable = ex;
        }

        if (b != null && b.length > 0) {
            try {
                ClassPool cp = ClassPool.getDefault();
                ClassLoader cl = getContextClassloader();
                cp.appendClassPath(new LoaderClassPath(cl));
                CtClass ctClass = cp.makeClass(new ByteArrayInputStream(b));
                clazz = ctClass.toClass();
            } catch (Throwable ex) {
                throwable = ex;
            }
        }

        if (clazz == null) {
            clazz = loadClassFromContext(name);
        }

        if (clazz == null) {
            log.error("Classe {} no trobada per el BackofficeClassLoader", name, throwable);
        }
        return clazz;
    }

    public Set<String> getClassNamesFromBackofficeJarFile() throws IOException {
        Set<String> classNames = new HashSet<>();

        // Comprovam si existeix el fitxer backoffice.jar
        File backofficeFile = new File(backofficeJarPath);
        if (backofficeFile.exists()) {
            // Obtenim les classes del backoffice.jar
            try (JarFile jarFile = new JarFile(backofficeJarPath)) {
                Enumeration<JarEntry> e = jarFile.entries();
                while (e.hasMoreElements()) {
                    JarEntry jarEntry = e.nextElement();
                    if (jarEntry.getName().endsWith(".class")) {
                        String className = jarEntry.getName()
                                .replace("/", ".")
                                .replace(".class", "");
                        classNames.add(className);
                    }
                }
            }
        }

        return classNames;
    }

    private byte[] loadClassFromBackofficeJar(String name) throws IOException {

        File backofficeFile = new File(backofficeJarPath);

        if (backofficeFile.exists()) {
            // Obtenim les classes del backoffice.jar
            try (JarFile jarFile = new JarFile(backofficeJarPath)) {
                Enumeration<JarEntry> e = jarFile.entries();
                while (e.hasMoreElements()) {
                    JarEntry jarEntry = e.nextElement();
                    if (jarEntry.getName().endsWith(".class")) {
                        String className = jarEntry.getName()
                                .replace("/", ".")
                                .replace(".class", "");
                        if (className.equals(name)) {
                            InputStream in = jarFile.getInputStream(jarEntry);
                            if (in != null) {
                                return toByteArray(in);
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private Class<?> loadClassFromContext(String className) {
        Class<?> clazz = null;
        ClassLoader localClassloader;
        Throwable throwable = null;
        try {
            localClassloader = getContextClassloader();
            if (localClassloader != null) {
                log.debug("Attempting to load class '{}' with {}: {}", className, "current thread context classloader", localClassloader);
                clazz = Class.forName(className, true, localClassloader);
            }
        } catch (Throwable ex) {
            if (throwable == null) {
                throwable = ex;
            }
        }

        if (clazz == null) {
            throw new RuntimeException("No s'ha pogut obtenir la classe " + className + " al Custom classloader", throwable);
        } else {
            return clazz;
        }
    }

    private static ClassLoader getContextClassloader() {
        return System.getSecurityManager() != null ?
                AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader()) :
                Thread.currentThread().getContextClassLoader();
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

    private static byte[] toByteArray(InputStream in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (in.available() > 0) {
                int data = in.read(buffer);
                out.write(buffer, 0, data);
            }

            in.close();
            out.close();
            return out.toByteArray();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new IllegalStateException(ioe);
        }
    }
}
