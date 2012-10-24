package com.astav.jsontojava;

import java.util.HashMap;

/**
 * User: Astav
 * Date: 10/21/12
 */
public class ClassFiles extends HashMap<String, ClassFileData> {
    @Override
    public ClassFileData get(Object key) {
        ClassFileData classFileData = super.get(key);
        if (classFileData == null) {
            classFileData = new ClassFileData();
            put(key.toString(), classFileData);
        }
        return classFileData;
    }
}

