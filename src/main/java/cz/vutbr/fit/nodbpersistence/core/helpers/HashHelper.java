package cz.vutbr.fit.nodbpersistence.core.helpers;

/**
 * Hashing class
 */
public class HashHelper {

    /**
     * Creates hash cde from class canonical name.
     * @param klass class to be hashed
     * @return hash code of class
     */
    public static Integer getHashFromClass(Class<?> klass) {
        return klass.getCanonicalName().hashCode();
    }
}
