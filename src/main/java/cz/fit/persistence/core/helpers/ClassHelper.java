package cz.fit.persistence.core.helpers;

import cz.fit.persistence.exceptions.PersistenceException;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
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
     *     <li>{@link Enum}</li>
     *     <li>{@link CharSequence}</li>
     *     <li>{@link Number}</li>
     *     <li>{@link Date}</li>
     *     <li>{@link URI}</li>
     *     <li>{@link Locale}</li>
     * </ul>
     *
     * Copied from <a href="https://github.com/spring-projects/spring-framework/blob/master/spring-beans/src/main/java/org/springframework/beans/BeanUtils.java">Spring project GitHub</a>
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

    /**
     * Instantiate a new object of desired class by using no-argument constructor.
     *
     * It is crucial that object be POJO!.
     * @param klass desired class
     * @param <T> paramater of class
     * @return object of {@code T} instance
     */
    public static <T> Object instantiateClass(Class<T> klass) {
        Objenesis objenesis = new ObjenesisStd(true);

        try {
            ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
            Constructor noArgConstrutor = rf.newConstructorForSerialization(klass);
            return noArgConstrutor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException  e) {
            throw new PersistenceException(e);
        }
    }

    public static String createReferenceString(Object object, Long objectId) {
        return object.getClass().getCanonicalName() + "#" + objectId;
    }
}
