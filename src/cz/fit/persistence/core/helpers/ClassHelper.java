package cz.fit.persistence.core.helpers;

import cz.fit.persistence.core.PersistenceContext;
import cz.fit.persistence.exceptions.PersistenceException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    /**
     * Instantiate a new object of desired class by using no-argument constructor.
     *
     * It is crucial that object be POJO!.
     * @param klass desired class
     * @param <T> paramater of class
     * @return object of {@code T} instance
     */
    public static <T> T instantiateClass(Class<T> klass) {
        try {
            Constructor<T> constructor = klass.getConstructor();
            boolean accs = constructor.canAccess(null);
            constructor.setAccessible(true);
            T newObj = constructor.newInstance();
            constructor.setAccessible(accs);
            return newObj;
        } catch (Exception e) {
            new PersistenceException(e);
        }
        return null;
    }
}
