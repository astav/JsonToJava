package com.astav.jsontojava;

import com.astav.jsontojava.classmanager.GeneratedClassManager;
import com.astav.jsontojava.classmanager.ImportClassManager;
import com.astav.jsontojava.regex.RegexFilter;
import com.astav.jsontojava.template.JavaTemplate;
import com.astav.jsontojava.util.MergeMapCollectionHelper;
import com.astav.jsontojava.util.PrimitiveClassHelper;
import com.astav.jsontojava.util.StringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * User: Astav
 * Date: 10/21/12
 */
public class Generator {
    public static final String DEFAULT_TYPE = "Object";
    public static final String ENTRY_POSTFIX_NAME = "Entry";
    public static final String LIST_TYPE_GENERIC = "List<%s>";
    public static final String MAP_TYPE_GENERIC = "Map<%s, %s>";
    public static final String MAP_DEFAULT_KEY_TYPE = "String";

    private final String packageName;
    private final String outputDirectory;
    private final ClassFiles classFiles = new ClassFiles();
    private final MergeMapCollectionHelper mergeMapCollectionHelper = new MergeMapCollectionHelper();
    private final PrimitiveClassHelper primitiveClassHelper = new PrimitiveClassHelper();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JavaTemplate javaTemplate = new JavaTemplate();
    private final GeneratedClassManager generatedClassManager;
    private final Optional<ImportClassManager> importClassManager;
    private final Optional<RegexFilter> regexFilter;
    private int generatedFileCount = 0;

    public Generator(String outputDirectory, String packageName, String regexFilename, String importDirectory, List<String> importPackages) throws IOException, ClassNotFoundException {
        this.packageName = packageName;
        System.out.println("Package name is '" + this.packageName + "'");
        this.outputDirectory = outputDirectory;
        System.out.println("Output directory is '" + this.outputDirectory + "'");
        this.generatedClassManager = new GeneratedClassManager(outputDirectory, packageName);
        File regexFile = new File(regexFilename);
        if (regexFile.exists()) {
            System.out.println(String.format("Using regex file '%s'", regexFilename));
            RegexFilter regexFilter = objectMapper.readValue(regexFile, new TypeReference<RegexFilter>() {
            });
            this.regexFilter = Optional.of(regexFilter);
        } else {
            System.out.println(String.format("Regex file not supplied or not found '%s'.", regexFilename));
            this.regexFilter = Optional.absent();
        }
        if (importDirectory == null || importPackages == null) {
            System.out.println("Not importing existing classes.");
            this.importClassManager = Optional.absent();
        } else {
            System.out.println(String.format("Importing classes from '%s' with specified packages.", importDirectory));
            this.importClassManager = Optional.of(new ImportClassManager(importDirectory, importPackages));
        }
    }

    public void generateClasses(String key, Map<String, Object> classData) throws IOException, ClassNotFoundException {
        String className = StringHelper.capFirstLetter(key);
        ClassFileData classFileData = classFiles.get(className);
        for (Map.Entry<String, Object> entry : classData.entrySet()) {
            Object value = entry.getValue();
            String entryKey = entry.getKey();
            Class<?> aClass = value == null ? Object.class : value.getClass();
            ClassMetaData vcMetaData = getClassMetaData(entryKey, value, aClass);

            classFileData.getMapOfVariablesToTypes().put(vcMetaData.getVariableName(), vcMetaData.getVariableType());
            classFileData.addImportPackages(vcMetaData.getImportPackages());

            Optional<GenerateClassForMap> generateClassForMapOptional = vcMetaData.getGenerateClass();
            if (generateClassForMapOptional.isPresent()) {
                GenerateClassForMap generateClassForMap = generateClassForMapOptional.get();
                //noinspection unchecked
                generateClasses(generateClassForMap.getClassName(), generateClassForMap.getData());
            }
        }
        File javaSourceFile = javaTemplate.writeOutJavaFile(className, outputDirectory, packageName, classFiles);
        generatedFileCount++;
        generatedClassManager.compileAndLoadClass(className, javaSourceFile, outputDirectory);
    }

    private ClassMetaData getClassMetaData(String entryKey, Object value, Class<?> aClass) throws IOException {
        ClassMetaData vcMetaData = new ClassMetaData();

        Optional<String> postfixClass = regexFilter.isPresent() ?
                regexFilter.get().getTypeForKey(entryKey) : Optional.<String>absent();
        if (postfixClass.isPresent()) { // match regex
            String type = postfixClass.get();
            System.out.println(String.format("  Using %s for json field '%s'", type, entryKey));
            vcMetaData.setVariableType(StringHelper.capFirstLetter(type));
            vcMetaData.setVariableName(entryKey);
            return vcMetaData;
        }

        Optional<Class> isAPrimitiveClass = primitiveClassHelper.getPrimitiveClass(aClass, value);
        if (isAPrimitiveClass.isPresent()) { // primitive class match
            Class<?> primitiveClass = isAPrimitiveClass.get();
            vcMetaData.setVariableType(StringHelper.capFirstLetter(primitiveClass.getSimpleName()));
            vcMetaData.setVariableName(entryKey);
            return vcMetaData;
        }

        List<Class<?>> matchingClasses = importClassManager.isPresent() ?
                importClassManager.get().getMatchingClasses(value) : Lists.<Class<?>>newArrayList();
        matchingClasses.addAll(generatedClassManager.getMatchingClasses(value));

        if (!matchingClasses.isEmpty()) { // one of the loaded classes works, use that instead..
            int useIndex = askUserToPickOneIfMultipleClasses(entryKey, matchingClasses);
            if (useIndex >= 0) {
                Class<?> chosenClass = matchingClasses.get(useIndex);
                System.out.println(String.format("  Using %s for json field '%s'", chosenClass.getName(), entryKey));
                vcMetaData.setVariableType(StringHelper.capFirstLetter(chosenClass.getSimpleName()));
                vcMetaData.setVariableName(entryKey);
                String name = chosenClass.getPackage().getName();
                if (!name.equals(packageName)) {
                    vcMetaData.addImportPackage(name + "." + chosenClass.getSimpleName());
                }
                return vcMetaData;
            }
        }

        if (Map.class.isAssignableFrom(aClass)) { // map type match
            boolean stayAsMap = false;
            Map mapValue = (Map) value;
            if (!mapValue.isEmpty()) {
                Optional<Class<?>> valuesPrimitiveType = primitiveClassHelper.getValuePrimitiveClassIfPossible(mapValue);

                Object aKey = mapValue.keySet().iterator().next();
                Optional<Class> mapKeyPrimitiveClass = primitiveClassHelper.getPrimitiveClass(aKey.getClass(), aKey);
                boolean mapKeyIsANumber = Number.class.isAssignableFrom(mapKeyPrimitiveClass.get());
                if (mapKeyIsANumber) { // if the key is a number stay as a map
                    stayAsMap = true;
                } else if (valuesPrimitiveType.isPresent()) {
                    stayAsMap = !askUserIfNewClassIsRequired(entryKey, mapValue, valuesPrimitiveType.get());
                }

                if (!stayAsMap) {
                    vcMetaData.setVariableType(StringHelper.capFirstLetter(entryKey));
                    vcMetaData.setVariableName(entryKey);
                    vcMetaData.setGenerateClass(Optional.of(new GenerateClassForMap(vcMetaData.getVariableType(), mapValue)));
                } else {
                    if (!valuesPrimitiveType.isPresent()) {
                        @SuppressWarnings("unchecked") Map<String, Object> allEntryMap = mergeMapCollectionHelper.merge(mapValue.values());

                        String entryName = entryKey + ENTRY_POSTFIX_NAME;
                        vcMetaData.setVariableType(String.format(MAP_TYPE_GENERIC, MAP_DEFAULT_KEY_TYPE, StringHelper.capFirstLetter(entryName)));
                        vcMetaData.setVariableName(entryName);

                        vcMetaData.setGenerateClass(Optional.of(new GenerateClassForMap(entryName, allEntryMap)));
                    } else {
                        @SuppressWarnings("SuspiciousMethodCalls") Object aValue = mapValue.get(aKey);
                        ClassMetaData mapValueMetaData = getClassMetaData(entryKey, aValue, aValue.getClass());

                        vcMetaData.setVariableType(String.format(MAP_TYPE_GENERIC, MAP_DEFAULT_KEY_TYPE, mapValueMetaData.getVariableType()));
                        vcMetaData.setVariableName(entryKey);

                        vcMetaData.setGenerateClass(mapValueMetaData.getGenerateClass());
                    }
                }
            } else {
                vcMetaData.setVariableType(String.format(MAP_TYPE_GENERIC, MAP_DEFAULT_KEY_TYPE, DEFAULT_TYPE));
                vcMetaData.setVariableName(entryKey);
            }
        } else if (List.class.isAssignableFrom(aClass)) { // list type match
            List listValue = (List) value;
            if (listValue.size() > 0) {
                Object listEntryValue = listValue.get(0); // get the first entry?
                ClassMetaData listEntryMetaData = getClassMetaData(entryKey, listEntryValue, listEntryValue.getClass());
                if (listEntryMetaData.getGenerateClass().isPresent()) {
                    @SuppressWarnings("unchecked") List<Map<String, Object>> collectionMap = (List<Map<String, Object>>) listValue;
                    Map<String, Object> allEntryMap = mergeMapCollectionHelper.merge(collectionMap);

                    String entryName = entryKey + ENTRY_POSTFIX_NAME;
                    vcMetaData.setVariableType(String.format(LIST_TYPE_GENERIC, StringHelper.capFirstLetter(entryName)));
                    vcMetaData.setVariableName(entryName);

                    vcMetaData.setGenerateClass(Optional.of(new GenerateClassForMap(entryName, allEntryMap)));

                } else {
                    vcMetaData.setVariableType(String.format(LIST_TYPE_GENERIC, listEntryMetaData.getVariableType()));
                    vcMetaData.setVariableName(entryKey);

                    vcMetaData.setGenerateClass(listEntryMetaData.getGenerateClass());
                }
            } else {
                vcMetaData.setVariableType(String.format(LIST_TYPE_GENERIC, DEFAULT_TYPE));
                vcMetaData.setVariableName(entryKey);
            }
        } else {
            System.out.println(String.format(" *Warning*: Chosen type 'Object' for json field '%s' with null value", entryKey));
            vcMetaData.setVariableType(DEFAULT_TYPE);
            vcMetaData.setVariableName(entryKey);
        }
        return vcMetaData;
    }

    private int askUserToPickOneIfMultipleClasses(String entryKey, List<Class<?>> matchingClasses) throws IOException {
        int useIndex;
        int defaultIndex = matchingClasses.size() == 1 ? 2 : 1;

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println();
        System.out.println(String.format("What would you like to use for '%s'...", entryKey));
        System.out.println(String.format(" 1. Generate a new class '%s'", StringHelper.capFirstLetter(entryKey)));
        int i = 2;
        for (Class<?> matchingClass : matchingClasses) {
            System.out.println(" " + i++ + ". " + matchingClass.getSimpleName() + " in package (" + matchingClass.getPackage().getName() + ")");
        }
        Integer valueChosen = null;
        while (valueChosen == null) {
            System.out.print(String.format("Pick an option for json field '%s' [default: %s]: ", entryKey, defaultIndex));
            String inputStr = br.readLine();
            if (inputStr.isEmpty()) {
                valueChosen = defaultIndex;
            } else {
                valueChosen = Integer.valueOf(inputStr);
            }
            if (valueChosen < 1 || (valueChosen - 1) > matchingClasses.size()) valueChosen = null;
        }
        System.out.println();
        switch (valueChosen) {
            case 1:
                useIndex = -1;
                break;
            default:
                useIndex = valueChosen - 2;
                break;
        }
        return useIndex;
    }

    private boolean askUserIfNewClassIsRequired(String entryKey, Map mapValue, Class<?> primitiveClass) throws IOException {
        System.out.println();
        System.out.println(String.format("What would you like to use for json field '%s'\n%s\n...", entryKey, objectMapper.writeValueAsString(mapValue)));
        System.out.println(String.format(" 1. Use a 'Map<String, %s>' (default)", primitiveClass.getSimpleName()));
        System.out.println(String.format(" 2. Generate a new class called '%s'", StringHelper.capFirstLetter(entryKey)));
        Integer valueChosen = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (valueChosen == null
                || (valueChosen != 1
                && valueChosen != 2)) {
            System.out.print(String.format("Pick a class for json field '%s' [default: 1]: ", entryKey));
            String inputStr = br.readLine();
            if (inputStr.isEmpty()) {
                valueChosen = 1;
            } else {
                valueChosen = Integer.valueOf(inputStr);
            }
        }
        return valueChosen == 2;
    }

    public int getGeneratedFileCount() {
        return generatedFileCount;
    }
}