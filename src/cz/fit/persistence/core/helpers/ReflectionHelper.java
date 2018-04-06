package cz.fit.persistence.core.helpers;

import cz.fit.persistence.exceptions.PersistenceException;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ReflectionHelper {

    public static Object deepCopyReflection(Object object) {
        Object copy;
        try {
            Class<?> klass = object.getClass();
            Constructor<?> constructor = object.getClass().getConstructor(null);
            copy = constructor.newInstance(null);

            Field[] sourceFields = object.getClass().getDeclaredFields();

            return copy;

        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object deepCopy(Object object) throws PersistenceException {
        DeepCopyWrapper wrappedObject = new DeepCopyWrapper(object);
        Object copiedObject;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(wrappedObject);
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(bos.toByteArray()));
            copiedObject = in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenceException(e);
        }
        if (copiedObject instanceof DeepCopyWrapper) {
            return ((DeepCopyWrapper) copiedObject).getObject();
        } else {
            return null;
        }

    }
}
