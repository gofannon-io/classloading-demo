package xyz.gofannon.mecha.application;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class LocalClassLoader extends ClassLoader {

    private final Map<String, String> resourceIndex = new HashMap<>();
    private final Map<String, LocalResource> resourceCache = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();


    public LocalClassLoader(ClassLoader parent) {
        super(parent);
        loadResourceIndex();
    }

    private void loadResourceIndex() {
        try (InputStream in = LocalClassLoader.class.getResourceAsStream("/embedded/jar-index.txt")) {
            assert in != null;
            List<String> entry = IOUtils.readLines(in, StandardCharsets.UTF_8);
            entry.forEach(it -> {
                String[] parts = it.split(":");
                String resourcePath = parts[0];
                String jarFileName = parts[1];
                resourceIndex.put(resourcePath, jarFileName);
            });

        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return loadInnerClass(name);
        } catch (ClassNotFoundException e) {
            return super.findClass(name);
        }
    }

//    @Override
//    public Class<?> loadClass(String name) throws ClassNotFoundException {
//        return loadClass(name, false);
//    }


    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        String clazzName = name.replace('.', '/') + ".class";
        Class<?> clazz = findFromCache(clazzName);

        if (clazz != null)
            return clazz;

        try {
            return loadInnerClass(name);
        } catch (ClassNotFoundException e) {
            //ignored
        }


        return super.loadClass(name.replace('/', '.'), resolve);
    }

    private Class<?> loadInnerClass(String clazzName) throws ClassNotFoundException {
        String resourceName = clazzName.replace('.', '/') + ".class";

        if (!resourceIndex.containsKey(resourceName)) {
            throw new ClassNotFoundException(clazzName);
        }

        Class<?> clazz = findFromCache(resourceName);
        if (clazz != null)
            return clazz;

        String jarFilename = resourceIndex.get(resourceName);
        return loadFromEmbeddedJar(jarFilename,clazzName, resourceName);

//        try (InputStream in = LocalClassLoader.class.getResourceAsStream("/embedded/jar-index.txt")) {
//            assert in != null;
//            List<String> jarFilenames = IOUtils.readLines(in, StandardCharsets.UTF_8);
//            for (String filename : jarFilenames) {
//                try {
//                    Class<?> innerClazz = doLoad2(filename, name, clazzName);
//                    if (innerClazz != null)
//                        return innerClazz;
//                } catch (ClassNotFoundException e) {
//                    // ignore
//                }
//            }
//            throw new ClassNotFoundException(name);
//
//        } catch (IOException ex) {
//            throw new ClassNotFoundException(name);
//        }
//        return loadFromInnerJar("mecha-impl-2-2.0-SNAPSHOT.jar", name);
    }


    private Class<?> loadFromEmbeddedJar(String jarName, String clazzName, String resourceName) throws ClassNotFoundException {
        try (InputStream in = LocalClassLoader.class.getResourceAsStream("/embedded/" + jarName);
             JarInputStream jarInputStream = new JarInputStream(in)
        ) {

            JarEntry entry;
            while ((entry = jarInputStream.getNextJarEntry()) != null) {
                if (entry.getName().equals(resourceName)) {
                    byte[] content = IOUtils.toByteArray(jarInputStream);
                    return defineInnerClass(clazzName, content);
                }
            }
            //throw new ClassNotFoundException(name);
            return null;

        } catch (IOException ex) {
            throw new ClassNotFoundException(clazzName);
        }
    }

//    private Class<?> loadFromInnerJar(String jarName, String clazzName) throws ClassNotFoundException {
//        try (InputStream in = LocalClassLoader.class.getResourceAsStream("/embedded/" + jarName);
//             JarInputStream jarInputStream = new JarInputStream(in)
//        ) {
//
//            JarEntry entry;
//            while ((entry = jarInputStream.getNextJarEntry()) != null) {
//                if (entry.getName().equals(clazzName)) {
//                    byte[] content = IOUtils.toByteArray(jarInputStream);
//                    return defineInnerClass(clazzName, content);
//                }
//            }
//            throw new ClassNotFoundException(clazzName);
//
//        } catch (IOException ex) {
//            throw new ClassNotFoundException(clazzName);
//        }
//    }

    private Class<?> findFromCache(String name) {
        lock.readLock().lock();
        try {

            LocalResource resource = resourceCache.get(name);
            return resource != null ? resource.clazz : null;

        } finally {
            lock.readLock().unlock();
        }
    }


    private Class<?> defineInnerClass(String clazzName, byte[] content) {
        Class<?> clazz = defineClass(clazzName, content, 0, content.length);
        lock.writeLock().lock();
        try {
            resourceCache.put(clazzName, new LocalResource(clazzName, clazz));
            return clazz;
        } finally {
            lock.writeLock().unlock();
        }
    }

}
