package com.astav.jsontojava.util;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

/**
 * User: Astav
 * Date: 10/21/12
 */
public class MergeMapCollectionHelper {
    public Map<String, Object> merge(Collection<Map<String, Object>> listMapValue) {
        Map<String, Object> allEntryMap = Maps.newHashMap();
        for (Map<String, Object> map : listMapValue) {
            allEntryMap.putAll(map);
        }
        return allEntryMap;
    }

    /* wip     private Map<String, Object> merge(Collection<Map<String, Object>> listMapValue) {
                Map<String, Object> allEntryMap = Maps.newHashMap();
                Map<String, Collection<Object>> valuesCollectionByKey = Maps.newHashMap();
                for (Map<String, Object> map : listMapValue) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry == null) continue;
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        if (value == null) continue;

                        Collection<Object> collection = valuesCollectionByKey.get(key);
                        if (collection == null) {
                            collection = Lists.newArrayList();
                            valuesCollectionByKey.put(key, collection);
                        }
                        collection.add(value);
                    }
                }

                for (Map.Entry<String, Collection<Object>> entry : valuesCollectionByKey.entrySet()) {
                    String key = entry.getKey();
                    Collection<Object> valuesCollection = entry.getValue();
                    if (valuesCollection == null || valuesCollection.size() == 0) continue;
                    Object aValue = valuesCollection.iterator().next();
                    Class<? extends Object> valueClass = aValue.getClass();
                    if (Map.class.isAssignableFrom(valueClass)) {
                        allEntryMap.put(key, merge((Collection<Map<String, Object>>) valuesCollection));
                    } else if (List.class.isAssignableFrom(valueClass)) {

                    } else {
                        allEntryMap.put(key, valuesCollection);
                    }

                }
                return allEntryMap;
            }
    */
}
