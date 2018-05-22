package cz.vutbr.fit.nodbpersistence.core.helpers;

import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

public class ConvertStringToType {
    /**
     * Converts string value into desired type. The source code was copied from <a href="https://stackoverflow.com/questions/13943550/how-to-convert-from-string-to-a-primitive-type-or-standard-java-wrapper-types">Stack Overflow</a>
     *
     * @param type  type of result
     * @param value source value to be converted
     * @return object of desired type or null
     */
    public static Object convertStringToType(Class<?> type, String value) {
        if (ClassHelper.isPrimitiveOrWrapper(type)) {
            return convertStringToPrimitiveType(type, value);
        }
        if (type.isEnum()) {
            Object[] enumConstants = type.getEnumConstants();
            for (Object enumValue : enumConstants) {
                if (value.equals(enumValue.toString())) {
                    return enumValue;
                }
            }
            throw new PersistenceException("Unknown value " + value + "of enum " + type.getName());
        }
        if (CharSequence.class.isAssignableFrom(type)) {
            return value;
        }
        if (Date.class.isAssignableFrom(type)) {
            return Date.from(Instant.ofEpochSecond(Long.valueOf(value)));
        }
        if (URI.class.isAssignableFrom(type)) {
            return URI.create(value);
        }
        if (Locale.class.isAssignableFrom(type)) {
            return new Locale(value);
        }
        return value;
    }

    public static Object convertStringToPrimitiveType(Class<?> type, String value) {
        if (type.equals(Boolean.class) || type.equals(boolean.class)) return Boolean.parseBoolean(value);
        if (type.equals(Byte.class) || type.equals(byte.class)) return Byte.parseByte(value);
        if (type.equals(Short.class) || type.equals(short.class)) return Short.parseShort(value);
        if (type.equals(Integer.class) || type.equals(int.class)) return Integer.parseInt(value);
        if (type.equals(Long.class) || type.equals(long.class)) return Long.parseLong(value);
        if (type.equals(Float.class) || type.equals(float.class)) return Float.parseFloat(value);
        if (type.equals(Double.class) || type.equals(double.class)) return Double.parseDouble(value);
        throw new PersistenceException("Unknown primitive type.");
    }
}
