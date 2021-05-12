package org.moriwuzhe.shuttle.common.classloaders;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;

/**
 * @Description:
 * @Author: xjp
 * @Date: 2021-04-11 23:20
 * @Version: 1.0
 */
public class ShuttleClassloader extends URLClassLoader {

    private URL[] urls;

    private LinkedHashSet<ClassLoader> classLoadersChan = new LinkedHashSet<>();

    public ShuttleClassloader(URL[] urls) {
        super(urls, ClassLoader.getSystemClassLoader().getParent());
        this.urls = urls;
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        for (ClassLoader classLoader : classLoadersChan) {
            try {
                Class<?> clazz = classLoader.loadClass(name);
                if (clazz != null) {
                    return clazz;
                }
            } catch (Exception e) {
                //ignore
            }
        }

        final Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        // 优先从parent（SystemClassLoader）里加载系统类，避免抛出ClassNotFoundException
        if (name != null && (name.startsWith("sun.") || name.startsWith("java."))) {
            return super.loadClass(name, resolve);
        }
        try {
            Class<?> clazz = findClass(name);
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        } catch (Exception e) {
            // ignore
        }
        return super.loadClass(name, resolve);
    }

    public ShuttleClassloader addClassLoader(ClassLoader loader) {
        if (loader != null) {
            classLoadersChan.add(loader);
        }
        return this;
    }

    public ShuttleClassloader loadJars() throws Exception {
        loadJars(this);
        return this;
    }

    public ShuttleClassloader loadJars(ClassLoader loader) throws Exception {
        Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        addURL.setAccessible(true);
        for (URL url : urls) {
            addURL.invoke(loader, url);
        }
        return this;
    }
}
