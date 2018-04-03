package cz.fit.persistence.core.helpers;

import cz.fit.persistence.exceptions.PersistenceException;

import java.io.*;

public class ReflectionHelper {

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
