package cz.fit.persistence.core;

public class PersistenceManagerFactory {

    private final PersistenceContext context;

    PersistenceManagerFactory(PersistenceContext persistenceContext) {
        this.context = persistenceContext;
    }

    public PersistenceManager getPersistenceManager() {
        return new PersistenceManagerImpl(this);
    }

    public PersistenceContext getContext() {
        return context;
    }
}
