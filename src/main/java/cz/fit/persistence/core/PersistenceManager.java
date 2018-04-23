package cz.fit.persistence.core;

import cz.fit.persistence.exceptions.PersistenceException;

public interface PersistenceManager {

    void persist(Object object) throws PersistenceException;

    void update(Object object) throws PersistenceException;

    <T> T load(Long objectId, Class<T> klazz) throws PersistenceException;

    PersistenceContext getContext();

}
