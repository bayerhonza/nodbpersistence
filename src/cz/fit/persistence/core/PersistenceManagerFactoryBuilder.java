package cz.fit.persistence.core;

import cz.fit.persistence.exceptions.PersistenceCoreException;

public class PersistenceManagerFactoryBuilder {

    private PersistenceContext persistenceContext;

    public PersistenceManagerFactoryBuilder(PersistenceSettings persistenceConfiguration) throws PersistenceCoreException {
        this.persistenceContext = new PersistenceContext(persistenceConfiguration);
        persistenceContext.init();
    }

    public PersistenceManagerFactory buildPersistenceManagerFactory() {
        return new PersistenceManagerFactory(persistenceContext);
    }
}
