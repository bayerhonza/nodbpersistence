package cz.fit.persistence.core.helpers;

import java.lang.reflect.Type;

public class ConvertStringToType {
    /**
     * https://stackoverflow.com/questions/13943550/how-to-convert-from-string-to-a-primitive-type-or-standard-java-wrapper-types
     *
     * @param type  type of result
     * @param value source value to be converted
     * @return object of desired type or null
     */
    public static Object convertStringToType(Type type, String value) {
        if (type.equals(Boolean.class) || type.equals(boolean.class)) return Boolean.parseBoolean(value);
        if (type.equals(Byte.class) || type.equals(byte.class)) return Byte.parseByte(value);
        if (type.equals(Short.class) || type.equals(short.class)) return Short.parseShort(value);
        if (type.equals(Integer.class) || type.equals(int.class)) return Integer.parseInt(value);
        if (type.equals(Long.class) || type.equals(long.class)) return Long.parseLong(value);
        if (type.equals(Float.class) || type.equals(float.class)) return Float.parseFloat(value);
        if (type.equals(Double.class) || type.equals(double.class)) return Double.parseDouble(value);
        return value;
    }
}
