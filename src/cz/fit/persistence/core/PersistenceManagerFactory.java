package cz.fit.persistence.core;

public class PersistenceManagerFactory {

    private static PersistenceManagerFactory pmfactory = null;

    protected PersistenceManagerFactory() {

    }

    public static PersistenceManagerFactory getInstance() {
        if (pmfactory == null) {
            pmfactory = new PersistenceManagerFactory();
        }
        return pmfactory;
    }

    public PersistenceManagerImpl getPersistenceManager() {
        return new PersistenceManagerImpl("/");
    }
}
