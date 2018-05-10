package cz.vutbr.fit.nodbpersistence.core;

/**
 * Factory for  {@link PersistenceManager}
 */
public class PersistenceManagerFactory {

    private final PersistenceContext context;

    PersistenceManagerFactory(PersistenceContext persistenceContext) {
        this.context = persistenceContext;
    }

    /**
     * Creates new instance of persistence manager.
     * @return persistence manager
     */
    public PersistenceManager getPersistenceManager() {
        return new PersistenceManagerImpl(this);
    }

    /**
     * Getter for persistence context.
     * @return persistence context
     */
    PersistenceContext getContext() {
        return context;
    }
}
