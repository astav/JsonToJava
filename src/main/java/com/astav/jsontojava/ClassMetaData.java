package com.astav.jsontojava;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * User: Astav
 * Date: 10/21/12
 */
public class ClassMetaData {
    private String variableName;
    private String variableType;
    private List<String> importPackages = Lists.newArrayList();
    private Optional<GenerateClassForMap> generateClass = Optional.absent();

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableType() {
        return variableType;
    }

    public void setVariableType(String variableType) {
        this.variableType = variableType;
    }

    public Optional<GenerateClassForMap> getGenerateClass() {
        return generateClass;
    }

    public void setGenerateClass(Optional<GenerateClassForMap> generateClass) {
        this.generateClass = generateClass;
    }
    
    public void addImportPackage(String packageName) {
        importPackages.add(packageName);
    }

    public List<String> getImportPackages() {
        return ImmutableList.copyOf(importPackages);
    }
}

