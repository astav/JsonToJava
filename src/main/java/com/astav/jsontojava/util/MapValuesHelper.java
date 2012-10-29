package com.astav.jsontojava.util;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * User: Astav
 * Date: 10/21/12
 */
public class MapValuesHelper {
    @SuppressWarnings("unchecked")
    private static List<Class<?>> primitiveTypeClasses = new ArrayList<Class<?>>(
            Arrays.asList(Boolean.class, Double.class, Integer.class, Long.class, String.class));

    public Optional<Class> getPrimitiveClass(Class<?> aClass, Object value) {
        // TODO: Doesn't support byte, short and char yet...
        for (Class primitiveClass : primitiveTypeClasses) {
            if (primitiveClass == String.class
                    && primitiveClass.isAssignableFrom(aClass)) {
                String strValue = (String) value;
                try {
                    Integer.valueOf(strValue);
                    Class reference = Integer.class;
                    return Optional.of(reference);
                } catch (NumberFormatException ignored) {
                }

                try {
                    Long.valueOf(strValue);
                    Class reference = Long.class;
                    return Optional.of(reference);
                } catch (NumberFormatException ignored) {
                }

                try {
                    Float.valueOf(strValue);
                    Class reference = Float.class;
                    return Optional.of(reference);
                } catch (NumberFormatException ignored) {
                }

                try {
                    Double.valueOf(strValue);
                    Class reference = Double.class;
                    return Optional.of(reference);
                } catch (NumberFormatException ignored) {
                }

                String lowerCaseStrValue = strValue.toLowerCase();
                if (lowerCaseStrValue.equals("true") || lowerCaseStrValue.equals("false")) {
                    Class reference = Boolean.class;
                    return Optional.of(reference);
                }

                Class reference = String.class;
                return Optional.of(reference);
            } else if (primitiveClass.isAssignableFrom(aClass)) {
                return Optional.of(primitiveClass);
            }
        }
        return Optional.absent();
    }

    public Optional<Class<?>> areAllValuesTheSamePrimitiveType(Map mapValue) {
        Class<?> valuePrimitiveClass = null;
        for (Object aValue : mapValue.values()) {
            if (aValue == null) {
                // TODO: Print warning of null value
                return Optional.absent();
            }
            Optional<Class> thisPrimitiveClass = getPrimitiveClass(aValue.getClass(), aValue);
            if (!thisPrimitiveClass.isPresent()
                    || (valuePrimitiveClass != null && thisPrimitiveClass.get() != valuePrimitiveClass)) {
                return Optional.absent();
            } else if (valuePrimitiveClass == null) {
                valuePrimitiveClass = thisPrimitiveClass.get();
            }
        }
        return Optional.<Class<?>>of(valuePrimitiveClass);
    }

    public boolean areAllValuesComplexTypes(Map mapValue) {
        for (Object aValue : mapValue.values()) {
            if (aValue == null) {
                // TODO: Print warning of null value
                return false;
            }
            if (!Map.class.isAssignableFrom(aValue.getClass())) {
                return false;
            }
        }
        return true;
    }
}
