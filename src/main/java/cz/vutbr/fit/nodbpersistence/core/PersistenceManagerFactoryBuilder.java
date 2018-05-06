package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceCoreException;

public class PersistenceManagerFactoryBuilder {

    private final PersistenceContext persistenceContext;

    public PersistenceManagerFactoryBuilder(PersistenceSettings persistenceConfiguration) throws PersistenceCoreException {
        this.persistenceContext = new PersistenceContext(persistenceConfiguration);
        persistenceContext.init();
    }

    public PersistenceManagerFactory buildPersistenceManagerFactory() {
        return new PersistenceManagerFactory(persistenceContext);
    }

}
