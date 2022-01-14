package es.caib.emiserv.logic.intf.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
@Component
public class BackofficeClassloader extends ClassLoader {

    @Getter @Setter
    @Value("${es.caib.emiserv.backoffice.jar.path:}")
    public String backofficeJarPath;

    public BackofficeClassloader() {
        super(getContextClassloader());
        if (backofficeJarPath == null || backofficeJarPath.isEmpty()) {
            backofficeJarPath = BackofficeClassloader.class.getClassLoader().getResource("").getPath().substring(0, BackofficeClassloader.class.getClassLoader().getResource("").getPath().indexOf("/modules/")) + "/backoffice/backoffice.jar";
        }

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
//                ClassLoader cl = getLocalClassLoader();
                ClassLoader cl = getContextClassloader();
                cp.appendClassPath(new LoaderClassPath(cl));
                CtClass ctClass = cp.makeClass(new ByteArrayInputStream(b));
                clazz = ctClass.toClass();
            } catch (Throwable ex) {
                throwable = ex;
            }
        }

//        if (b != null && b.length > 0) {
//            clazz = defineClass(name, b, 0, b.length);
//        }

        if (clazz == null) {
            clazz = loadClassFromContext(name);
        }

        return clazz;
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

//    public void loadBackofficeClasses() throws IOException {
//        File backofficeFile = new File(backofficeJarPath);
//
//        if (backofficeFile.exists()) {
//            // Obtenim les classes del backoffice.jar
//            try (JarFile jarFile = new JarFile(backofficeJarPath)) {
//                Enumeration<JarEntry> e = jarFile.entries();
//                while (e.hasMoreElements()) {
//                    JarEntry jarEntry = e.nextElement();
//                    if (jarEntry.getName().endsWith(".class")) {
//                        String className = jarEntry.getName()
//                                .replace("/", ".")
//                                .replace(".class", "");
//                        try {
//                            InputStream in = jarFile.getInputStream(jarEntry);
//                            if (in != null) {
//                                byte[] classBytes = toByteArray(in);
//                                defineClass(className, classBytes, 0, classBytes.length);
//                            }
//                        } catch (IOException ex) {
//                            log.error("No ha estat possible carregar la classe {} del backoffice.jar", className, ex);
//                        }
//                    }
//                }
//            }
//        }
//    }

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

    private Set<URL> getBackofficeJarResourceUrls() throws IOException {

        Set<URL> urls = new HashSet<>();

        File backofficeFile = new File(backofficeJarPath);
        if (backofficeFile.exists()) {
            urls.add(new URL("jar:file:" + backofficeJarPath + "!/"));
        }

        return urls;
    }

//    private Set<URL> getBackofficeJarResourceUrls(String name) throws IOException {
//        Set<URL> urls = new HashSet<>();
//        if (name.equals("META-INF/spring.components")) {
//            return urls;
//        }
//        Set<String> classNamesFromBackofficeJarFile = getClassNamesFromBackofficeJarFile();
//        if (!classNamesFromBackofficeJarFile.isEmpty()) {
//            classNamesFromBackofficeJarFile.stream()
//                    .filter(c -> c.startsWith(name.replace("/", ".")))
//                    .forEach(c -> {
//                        try {
//                            urls.add(new URL("jar:file:" + backofficeJarPath + "!/" + c.replace(".", "/") + ".class"));
//                        } catch (MalformedURLException e) {
//                            log.error("URL de classe backoffice incorrecte", e);
//                        }
//                    });
//        }
//        return urls;
//    }

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

//        if (clazz == null) {
//            try {
//                localClassloader = ClassLoaderUtil.getClassloader(ReflectionUtil.class);
//                log.debug("Attempting to load class '{}' with {}: {}", className, "local classloader", localClassloader);
//                clazz = Class.forName(className, true, localClassloader);
//            } catch (Throwable ex) {
//                if (throwable == null) {
//                    throwable = ex;
//                }
//            }
//        }

        if (clazz == null) {
            throw new RuntimeException("No s'ha pogut obtenir la classe " + className + " al Custom classloader", throwable);
        } else {
            return clazz;
        }
    }



//    private static ClassLoader getLocalClassLoader() {
//        ClassLoader localClassloader;
//        localClassloader = ClassLoaderUtil.getContextClassloader();
//        if (localClassloader == null) {
//            localClassloader = ClassLoaderUtil.getClassloader(ReflectionUtil.class);
//        }
//        return localClassloader;
//    }

    private static ClassLoader getContextClassloader() {
        return System.getSecurityManager() != null ?
                AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader()) :
                Thread.currentThread().getContextClassLoader();
    }

//    private static ClassLoader getClassloader(final Class<?> clazz) {
//        return System.getSecurityManager() != null ?
//                AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> clazz.getClassLoader()) :
//                clazz.getClassLoader();
//    }

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
