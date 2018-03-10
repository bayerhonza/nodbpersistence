package cz.fit.persistence.core;


import cz.fit.persistence.exceptions.PersistenceException;
import cz.fit.persistence.hash.HashHelper;


public class PersistenceManagerImpl implements PersistenceManager {

    @Override
    public void persist(Object ob) throws PersistenceException {
        if (ob == null) {
            throw new PersistenceException(new NullPointerException());
        }
        Integer obClassHashCode = HashHelper.getHashFromClass(ob.getClass());
    }

    @Override
    public void update(Object object) throws PersistenceException {

    }

    @Override
    public void find(Object object) throws PersistenceException {

    }

}
