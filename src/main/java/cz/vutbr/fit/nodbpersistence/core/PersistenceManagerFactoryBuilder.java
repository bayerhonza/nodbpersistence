package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceCoreException;

/**
 * Builder of {@link PersistenceManagerFactory}. It creates the {@link PersistenceContext} as a core based on
 * given configuration.
 */
public class PersistenceManagerFactoryBuilder {

    private final PersistenceContext persistenceContext;

    public PersistenceManagerFactoryBuilder(PersistenceSettings persistenceConfiguration) throws PersistenceCoreException {
        this.persistenceContext = new PersistenceContext(persistenceConfiguration);
        persistenceContext.init();
    }

    /**
     * Creates persistence manager factory
     * @return persistence manager factory
     */
    public PersistenceManagerFactory buildPersistenceManagerFactory() {
        return new PersistenceManagerFactory(persistenceContext);
    }

}
