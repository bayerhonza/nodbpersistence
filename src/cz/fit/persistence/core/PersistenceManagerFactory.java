package cz.fit.persistence.core;

public class PersistenceManagerFactory {

    private PersistenceContext context;

    PersistenceManagerFactory(PersistenceContext persistenceContext) {
        this.context = persistenceContext;
    }

    public PersistenceManager getPersistenceManager() {
        //TODO implement persistence manager
        return new PersistenceManagerImpl(this);
    }

    public PersistenceContext getContext() {
        return context;
    }
}
