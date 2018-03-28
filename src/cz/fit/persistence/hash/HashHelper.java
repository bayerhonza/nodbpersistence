package cz.fit.persistence.hash;

class HashHelper {

    public static Integer getHashFromClass(Class<?> klass) {
        if (klass == null) {
            return null;
        }
        return klass.getCanonicalName().hashCode();
    }
}
