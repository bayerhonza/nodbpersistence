package cz.vutbr.fit.nodbpersistence.core.helpers;

import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

    /**
     * Checks if the given class is a primitive type or its wrapper.
     *
     * @param clazz class to check
     * @return true if is primitive, else false
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) return true;
        if (clazz.equals(Byte.class) || clazz.equals(byte.class)) return true;
        if (clazz.equals(Short.class) || clazz.equals(short.class)) return true;
        if (clazz.equals(Integer.class) || clazz.equals(int.class)) return true;
        if (clazz.equals(Long.class) || clazz.equals(long.class)) return true;
        if (clazz.equals(Float.class) || clazz.equals(float.class)) return true;
        return clazz.equals(Double.class) || clazz.equals(double.class);
    }

    public static void setFieldValue(Field field, Object object, Object newValue) {
        try {
            boolean isFinal = false;
            boolean finalFieldAccess = false;
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            Object localObject;
            if (Modifier.isFinal(field.getModifiers())) {
                finalFieldAccess = modifiersField.canAccess(field);
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                isFinal = true;
            }
            if (Modifier.isStatic(field.getModifiers())) {
                localObject = null;
            } else {
                localObject = object;
            }
            boolean fieldAccess = field.canAccess(localObject);
            field.setAccessible(true);
            field.set(localObject, newValue);
            field.setAccessible(fieldAccess);

            if (isFinal) {
                modifiersField.setInt(field, field.getModifiers() & Modifier.FINAL);
                modifiersField.setAccessible(finalFieldAccess);
            }
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    public static Object getFieldValue(Field field, Object object) {
        try {
            Object result;
            boolean isFinal = false;
            boolean finalFieldAccess = false;
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            Object localObject;
            if (Modifier.isFinal(field.getModifiers())) {
                finalFieldAccess = modifiersField.canAccess(field);
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                isFinal = true;
            }
            if (Modifier.isStatic(field.getModifiers())) {
                localObject = null;
            } else {
                localObject = object;
            }
            boolean fieldAccess = field.canAccess(localObject);
            field.setAccessible(true);
            result = field.get(localObject);
            field.setAccessible(fieldAccess);

            if (isFinal) {
                modifiersField.setInt(field, field.getModifiers() & Modifier.FINAL);
                modifiersField.setAccessible(finalFieldAccess);
            }
            return result;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }
}
