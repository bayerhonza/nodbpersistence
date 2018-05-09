package cz.vutbr.fit.nodbpersistence.core.helpers;

import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

/**
 * Utility for class handling.
 */
public class ClassHelper {
    /**
     * Checks if class is primitive type or its wrapper, or other simple type.
     * Simple types:
     * <ul>
     * <li>{@link Enum}</li>
     * <li>{@link CharSequence}</li>
     * <li>{@link Number}</li>
     * <li>{@link Date}</li>
     * <li>{@link URI}</li>
     * <li>{@link Locale}</li>
     * </ul>
     * <p>
     * Copied from <a href="https://github.com/spring-projects/spring-framework/blob/master/spring-beans/src/main/java/org/springframework/beans/BeanUtils.java">Spring project GitHub</a>
     *
     * @param clazz type to check
     * @return {@code true} if is type is simple, else {@code false}
     */
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
