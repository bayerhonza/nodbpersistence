package cz.fit.persistence.hash;

public class HashHelper {

    public static Integer getHashFromClass(Class<?> klass) {
        if (klass == null) {
            return null;
        }
        return klass.getCanonicalName().hashCode();
    }
}
