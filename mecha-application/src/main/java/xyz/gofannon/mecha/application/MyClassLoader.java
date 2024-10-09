package xyz.gofannon.mecha.application;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class MyClassLoader extends ClassLoader {

    public MyClassLoader(ClassLoader parent) {
        super(parent);
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.startsWith("xyz.gofannon.mecha.impl.")) {
            return doLoadClass(name);
        }

        return super.findClass(name);
    }

    private Class<?> doLoadClass(String name) throws ClassNotFoundException {
        System.out.println("=== Try loading "+name);

        try (InputStream in = MyClassLoader.class.getResourceAsStream("/tmp/mecha-impl-1.0-SNAPSHOT.jar");
             JarInputStream jarInputStream = new JarInputStream(in)
        ) {

            String clazzName = name.replace('.', '/') + ".class";
            JarEntry entry;
            while ((entry = jarInputStream.getNextJarEntry()) != null) {
                if (entry.getName().equals(clazzName)) {
                    byte[] content = IOUtils.toByteArray(jarInputStream);
                    return defineClass(name, content, 0, content.length);
                }
            }
            throw new ClassNotFoundException(name);

        } catch (IOException ex) {
            throw new ClassNotFoundException(name);
        }
    }

}
