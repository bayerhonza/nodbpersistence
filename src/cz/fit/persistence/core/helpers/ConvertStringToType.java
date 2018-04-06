package cz.fit.persistence.core.helpers;

import java.lang.reflect.Type;

public class ConvertStringToType {
    /**
     * https://stackoverflow.com/questions/13943550/how-to-convert-from-string-to-a-primitive-type-or-standard-java-wrapper-types
     *
     * @param type  type of result
     * @param value source value to be converted
     * @param <T>   generic for result type
     * @return object of desired type or null
     */
    public static Object convertStringToType(Type type, String value) {
        if (type.equals(Boolean.class)) return Boolean.parseBoolean(value);
        if (type.equals(Byte.class)) return Byte.parseByte(value);
        if (type.equals(Short.class)) return Short.parseShort(value);
        if (type.equals(Integer.class)) return Integer.parseInt(value);
        if (type.equals(Long.class)) return Long.parseLong(value);
        if (type.equals(Float.class)) return Float.parseFloat(value);
        if (type.equals(Double.class)) return Double.parseDouble(value);
        return value;
    }
}
