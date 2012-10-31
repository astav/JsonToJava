package com.astav.jsontojava;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * User: Astav
 * Date: 10/18/12
 * <p/>
 * A very crude JsonToJava source file generator that tries to discover the schema and
 * generate the necessary java classes.
 * <p/>
 * Tips:
 * Specify all values for json fields with non-null values. By doing so,
 * the generator will try to re-use classes it already generated in other structures as long
 * as the class can be parsed back from json into the generated class.
 * <p/>
 * Limitations:
 * - Can't discover and create abstract types
 * - Can't collate unspecified fields across different structures into the same class when missing information
 * - Doesn't support byte, short and char types yet
 */
public class JsonToJava {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("JsonToJava v0.1");
        System.out.println();
        if (args.length < 6) {
            System.out.println("com.astav.jsontojava.JsonToJava <json-file> <output-directory> <package-name> <main-class> <regex-file> <import-base-dir> <import-package-names>");
            System.out.println("  <json-file> (required): json file to use");
            System.out.println("  <output-directory> (required): directory where the output should go");
            System.out.println("  <package-name> (required): package name to use");
            System.out.println("  <main-class> (required): name of the main class to use");
            System.out.println("  <regeex-file> (required): regular expressions to use (if file doesn't exist, it will be skipped)");
            System.out.println("  <prompt-for-complex-value-types> (required - 'true' or 'false'): when encountering a map of maps, instead of generating a new class for every entry, prompt instead");
            System.out.println("  <import-base-dir> (optional): import base directory");
            System.out.println("  <import-package-names> (optional): import package names");
            return;
        }
        String importBaseDir = args.length > 6 ? args[6] : null;
        List<String> importPackageNames = args.length > 7 ? Lists.newArrayList(args).subList(7, args.length) : null;
        @SuppressWarnings("unchecked") Map<String, Object> map = mapper.readValue(FileUtils.readFileToString(new File(args[0])), Map.class);
        Generator generator = new Generator(args[1], args[2], args[4], importBaseDir, importPackageNames, args[5].equals("true"));
        System.out.println("Generating classes....");
        generator.generateClasses(args[3], map);
        System.out.println();
        System.out.println(String.format("Generated %s files. All done.", generator.getGeneratedFileCount()));
    }
}
