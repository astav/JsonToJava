package com.astav.jsontojava;

import java.util.Map;

/**
 * User: Astav
 * Date: 10/21/12
 */
public class GenerateClassForMap {
    private String className;
    private Map data;

    public GenerateClassForMap(String className, Map data) {
        this.setClassName(className);
        this.setData(data);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }
}
