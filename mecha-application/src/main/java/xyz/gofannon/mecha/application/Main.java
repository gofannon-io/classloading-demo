package xyz.gofannon.mecha.application;

import xyz.gofannon.mecha.api.Platform;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        loadAndPrint("default", Main.class.getClassLoader());
        ClassLoader childFirstClassLoader = new LocalClassLoader(Main.class.getClassLoader());
        loadAndPrint("home made", childFirstClassLoader);

        //ClassLoader classLoader = Main.class.getClassLoader();
//        Platform platform = (Platform)classLoader.loadClass("xyz.gofannon.mecha.impl.PlatformImpl").getConstructor().newInstance();
//
//        System.out.println("Platform type: " + platform.getPlatformType());
//        System.out.println("Engine serial number: " + platform.getEngine().getSerialNumber());
//        System.out.println("-----------------------");
//        System.out.println("Platform classloader: " + platform.getClass().getClassLoader());
//        System.out.println("Engine serial number: " + platform.getEngine().getClass().getClassLoader());
//        System.out.println("-----------------------");
//        System.out.println("Platform String classloader: " + platform.getPlatformType().getClass().getClassLoader());
//        System.out.println("Engine String classloader: " + platform.getEngine().getSerialNumber().getClass().getClassLoader());
    }

    private static void loadAndPrint(String title, ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {


        Platform platform = (Platform)classLoader.loadClass("xyz.gofannon.mecha.impl.PlatformImpl").getConstructor().newInstance();

        System.out.println("--- "+title+" --------------------------------------------------------");
        System.out.println("Platform type: " + platform.getPlatformType());
        System.out.println("Engine serial number: " + platform.getEngine().getSerialNumber());
        System.out.println();
    }
}