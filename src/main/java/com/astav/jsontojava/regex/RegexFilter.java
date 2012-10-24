package com.astav.jsontojava.regex;

import com.google.common.base.Optional;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

/**
 * User: Astav
 * Date: 10/23/12
 */
public class RegexFilter {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @JsonProperty("typesByRegex")
    private Map<String, String> typesByRegex;

    public Optional<String> getTypeForKey(String key) {
        for (Map.Entry<String, String> entry : typesByRegex.entrySet()) {
            String regex = entry.getKey();
            String type = entry.getValue();
            if (key.matches(regex)) {
                return Optional.of(type);
            }
        }
        return Optional.absent();
    }
}
