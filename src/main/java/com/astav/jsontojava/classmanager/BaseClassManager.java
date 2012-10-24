package com.astav.jsontojava.classmanager;

import com.google.common.collect.Lists;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * User: Astav
 * Date: 10/21/12
 */
public abstract class BaseClassManager {
    private ObjectMapper mapper = new ObjectMapper();
    private List<Class<?>> classes = Lists.newArrayList();

    protected void addClass(Class<?> aClass) {
        classes.add(aClass);
    }

    public List<Class<?>> getMatchingClasses(Object data) throws IOException {
        List<Class<?>> matchingClasses = Lists.newArrayList();

        if (data == null) return matchingClasses;

        String jsonData = mapper.writeValueAsString(data);

        if(jsonData.equals("{}")) return matchingClasses;

        for (Class<?> aClass : classes) {
            try {
                mapper.readValue(jsonData, aClass);
                if (!matchingClasses.contains(aClass)) {
                    matchingClasses.add(aClass);
                }
            } catch (Throwable e) { // TODO: Change...
            }
        }
        return matchingClasses;
    }
}
