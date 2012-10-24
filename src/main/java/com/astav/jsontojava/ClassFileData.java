package com.astav.jsontojava;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Astav
 * Date: 10/21/12
 */
public class ClassFileData {
    private Map<String, String> mapOfVariablesToTypes = Maps.newHashMap();
    private Set<String> importPackages = Sets.newHashSet();

    public Map<String, String> getMapOfVariablesToTypes() {
        return mapOfVariablesToTypes;
    }

    public void addImportPackages(List<String> packages) {
        importPackages.addAll(packages);
    }

    public List<String> getImportPackages() {
        return ImmutableList.copyOf(importPackages);
    }

}

