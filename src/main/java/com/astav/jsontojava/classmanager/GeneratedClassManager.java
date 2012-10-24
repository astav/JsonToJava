package com.astav.jsontojava.classmanager;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * User: Astav
 * Date: 10/21/12
 */
public class GeneratedClassManager extends BaseClassManager {

    public static final String PACKAGE_SEPERATOR = ".";

    private final ClassLoader classLoader;
    private final String packageName;

    public GeneratedClassManager(String base, String packageName) throws IOException, ClassNotFoundException {
        File folder = new File(base);
        if (!folder.exists()) {
            if (!folder.mkdirs())
                throw new RuntimeException("Couldn't create necessary folders in " + base);
        }
        classLoader = new URLClassLoader(new URL[]{folder.toURI().toURL()});

        if (!packageName.endsWith(PACKAGE_SEPERATOR)) packageName = packageName + PACKAGE_SEPERATOR;

        this.packageName = packageName;

        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                if (!file.isFile()) continue;
                if (!file.getName().endsWith(".class")) continue;
                Class<?> aClass = classLoader.loadClass(this.packageName + file.getName());
                addClass(aClass);
            }
        }
    }

    public void compileAndLoadClass(String className, File outputFile, String outputDirectory) throws ClassNotFoundException {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager standardFileManager = javaCompiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());
        Iterable<? extends JavaFileObject> compilationUnits = standardFileManager.getJavaFileObjects(outputFile);
        List<String> options = Arrays.asList(
                "-d", outputDirectory,
                "-sourcepath", outputDirectory,
                "-classpath", System.getProperty("java.class.path"));

        JavaCompiler.CompilationTask task = javaCompiler.getTask(
                null,
                standardFileManager,
                diagnostics,
                options,
                null,
                compilationUnits);

        Boolean success = task.call();

        if (success) {
            loadClass(className);
        } else {
            System.out.println("! Failed to compile " + className);
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                System.out.println("Error : " + diagnostic.toString());
            }
        }
    }

    private void loadClass(String className) throws ClassNotFoundException {
        Class<?> aClass = classLoader.loadClass(this.packageName + className);
        addClass(aClass);
    }
}
