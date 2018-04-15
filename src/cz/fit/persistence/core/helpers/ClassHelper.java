package cz.fit.persistence.core.helpers;

import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

public class ClassHelper {

    public static boolean isSimpleValueType(Class<?> clazz) {
        return (isPrimitiveOrWrapper(clazz) ||
                Enum.class.isAssignableFrom(clazz) ||
                CharSequence.class.isAssignableFrom(clazz) ||
                Number.class.isAssignableFrom(clazz) ||
                Date.class.isAssignableFrom(clazz) ||
                URI.class == clazz || URL.class == clazz ||
                Locale.class == clazz || Class.class == clazz);
    }

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) return true;
        if (clazz.equals(Byte.class) || clazz.equals(byte.class)) return true;
        if (clazz.equals(Short.class) || clazz.equals(short.class)) return true;
        if (clazz.equals(Integer.class) || clazz.equals(int.class)) return true;
        if (clazz.equals(Long.class) || clazz.equals(long.class)) return true;
        if (clazz.equals(Float.class) || clazz.equals(float.class)) return true;
        if (clazz.equals(Double.class) || clazz.equals(double.class)) return true;
        return false;
    }
}
