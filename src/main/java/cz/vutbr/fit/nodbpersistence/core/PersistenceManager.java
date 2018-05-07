package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;

public interface PersistenceManager {

    void persist(Object object) throws PersistenceException;

    void update(Object object) throws PersistenceException;

    <T> T load(Long objectId, Class<T> klazz) throws PersistenceException;

    PersistenceContext getContext();

}
