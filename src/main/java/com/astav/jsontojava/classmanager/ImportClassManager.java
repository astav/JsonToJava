package com.astav.jsontojava.classmanager;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

/**
 * User: Astav
 * Date: 10/21/12
 */
public class ImportClassManager extends BaseClassManager {
    public static final String PACKAGE_SEPERATOR = ".";
    public static final String REPLACE_PACKAGE_SEPERATOR = "\\" + PACKAGE_SEPERATOR;

    public ImportClassManager(String baseDir, List<String> basePackageName) throws ClassNotFoundException, MalformedURLException {
        ClassLoader classLoader = new URLClassLoader(
                new URL[]{
                        new File(baseDir).toURI().toURL()
                },
                ClassLoader.getSystemClassLoader());

        Collection<File> files = findClassFilesList(baseDir, basePackageName);

        System.out.println("Import class manager found " + files.size() + " : " + files);
        System.out.println();

        for (File file : files) {
            if (!file.isFile()) continue;
            String packageFilePath = file.getPath().substring(baseDir.length() + 1);
            String fullyQualifiedClassName = packageFilePath
                    .replaceAll(File.separator, PACKAGE_SEPERATOR)
                    .replaceAll(".class", "");
            addClass(classLoader.loadClass(fullyQualifiedClassName));
        }
    }

    private Collection<File> findClassFilesList(String baseDir, List<String> basePackageNames) {
        Collection<File> files = Lists.newArrayList();
        for (String packageName : basePackageNames) {
            files.addAll(findClassFiles(baseDir, packageName));
        }
        return files;
    }

    private Collection<File> findClassFiles(String baseDir, String basePackageName) {
        String packagePath = basePackageName.replaceAll(REPLACE_PACKAGE_SEPERATOR, File.separator);
        String path = baseDir + File.separator + packagePath;

        if (!path.endsWith(File.separator)) path = path + File.separator;
        File directory = new File(path);
        if (!directory.isDirectory()) {
            System.out.println(String.format("ERROR: %s isn't a valid directory.", directory));
        }
        //noinspection unchecked
        return FileUtils.listFiles(
                directory,
                new WildcardFileFilter("*.class"),
                DirectoryFileFilter.DIRECTORY
        );
    }
}
