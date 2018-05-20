package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.core.klass.manager.AbstractClassManager;

/**
 * Interface for persistence manager
 */
public interface PersistenceManager {

    /**
     * Persists given object
     * @param object object
     */
    Long persist(Object object);

    /**
     * Updates given object.
     * @param object object to update
     */
    void update(Object object) ;

    /**
     * Flushes changes.
     */
    void flush();

    /**
     * Loads object based on given objectId and return it as an instance of given class.
     * @param objectId objectId of object
     * @param klazz object's  class
     * @param <T> object's class parameter
     * @return object
     */
    <T> T load(Long objectId, Class<T> klazz);

    /**
     * Saves modified class manager for future flush.
     * @param classManager modified class manager
     */
    void addModifiedClassManager(AbstractClassManager classManager);

    /**
     * Getter for persistence context.
     * @return persistence context
     */
    PersistenceContext getContext();

}
