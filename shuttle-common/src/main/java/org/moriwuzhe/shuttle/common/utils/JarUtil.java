package org.moriwuzhe.shuttle.common.utils;

import sun.net.www.protocol.jar.URLJarFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: moriwuzhe
 * @Date: 2021-04-14 00:32
 * @Version: 1.0
 */
public final class JarUtil {

    private static final Method ADDURL_METHOD;

    private static final String JAR_PROTOCOL = "jar";
    private static final String FILE_PROTOCOL = "file";

    static {
        try {
            ADDURL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("Agent Source initialize fail", e);
        }
    }

    public static Set<URL> findSources(ClassLoader loader, final String parentDir, final String suffix) throws Exception {
        URL rootUrl = loader.getResource(parentDir);
        Set<URL> result = new HashSet<>();
        if (rootUrl == null) {
            return result;
        }

        if (JAR_PROTOCOL.equals(rootUrl.getProtocol())) {
            JarURLConnection jarURLConnection = (JarURLConnection) rootUrl.openConnection();
            List<JarEntry> jarEntries = jarURLConnection.getJarFile().stream()
                    .filter(jarEntry -> jarEntry.getName().startsWith(parentDir) && jarEntry.getName().endsWith(suffix)).collect(Collectors.toList());
            for (JarEntry jarEntry : jarEntries) {
                result.add(new URL(rootUrl.getPath() + jarEntry.getName().substring(parentDir.length())));
            }
            return result;
        }

        if (FILE_PROTOCOL.equals(rootUrl.getProtocol())) {
            File[] list = new File(rootUrl.getPath())
                    .listFiles(pathname -> pathname.getName().endsWith(suffix));
            for (File file : list) {
                result.add(file.toURI().toURL());
            }
            return result;
        }

        new Exception(String.format("findSources not suport by %s url Protocol", rootUrl.getProtocol())).printStackTrace();
        return result;
    }

    public static JarFile retrieve(URL url) throws IOException, PrivilegedActionException {
        final InputStream inputStream = url.openConnection().getInputStream();
        JarFile result = (JarFile) AccessController.doPrivileged((PrivilegedExceptionAction) () -> {
            Path tempPath = Files.createTempFile("jar_cache", (String) null, new FileAttribute[0]);
            try {
                Files.copy(inputStream, tempPath, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                URLJarFile jarFile = new URLJarFile(tempPath.toFile(), null);
                tempPath.toFile().deleteOnExit();
                return jarFile;
            } catch (Throwable throwable) {
                try {
                    Files.delete(tempPath);
                } catch (IOException e) {
                    throwable.addSuppressed(e);
                }
                throw throwable;
            }
        });
        return result;
    }

    public static File toTempFile(URL url) throws IOException, PrivilegedActionException {
        final InputStream inputStream = url.openConnection().getInputStream();
        File result = (File) AccessController.doPrivileged((PrivilegedExceptionAction<File>) () -> {
            Path tempPath = Files.createTempFile("jar_cache", (String) null, new FileAttribute[0]);
            try {
                Files.copy(inputStream, tempPath, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                tempPath.toFile().deleteOnExit();
                return tempPath.toFile();
            } catch (Throwable throwable) {
                try {
                    Files.delete(tempPath);
                } catch (IOException e) {
                    throwable.addSuppressed(e);
                }
                throw throwable;
            }
        });
        return result;
    }

    public static void addUrl(final URL url, final URLClassLoader loader) throws PrivilegedActionException, IOException {
        AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
            ADDURL_METHOD.setAccessible(true);
            if (FILE_PROTOCOL.equals(url.getProtocol())) {
                File f = new File(url.toURI());
                if (!f.exists()) {
                    throw new IOException("not found " + url);
                }
                ADDURL_METHOD.invoke(loader, url);
            } else {
                File tempFile = toTempFile(url);
                ADDURL_METHOD.invoke(loader, tempFile.toURI().toURL());
            }
            return null;
        });

    }

}
