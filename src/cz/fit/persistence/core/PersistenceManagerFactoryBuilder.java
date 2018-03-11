package cz.fit.persistence.core;

public class PersistenceManagerFactoryBuilder {

    private PersistenceContext persistenceContext;

    public PersistenceManagerFactoryBuilder(PersistenceSettings persistenceConfiguration) {
        this.persistenceContext = new PersistenceContext(persistenceConfiguration);
        persistenceContext.init();
    }

    public PersistenceManagerFactory buildPersistenceManagerFactory() {
        return new PersistenceManagerFactory(persistenceContext);
    }
}
