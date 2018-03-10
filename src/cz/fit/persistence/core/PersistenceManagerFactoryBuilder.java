package cz.fit.persistence.core;

public class PersistenceManagerFactoryBuilder {

    private PersistenceContext persistenceContext;

    PersistenceManagerFactoryBuilder() {
        this(null);
    }

    public PersistenceManagerFactoryBuilder(PersistenceSettings persistenceConfiguration) {
        this.persistenceContext = new PersistenceContext(persistenceConfiguration);
    }

    public PersistenceManagerFactory buildPersistenceManagerFactory() {
        return new PersistenceManagerFactory(persistenceContext);
    }
}
