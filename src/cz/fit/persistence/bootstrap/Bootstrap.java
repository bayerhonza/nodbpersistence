package cz.fit.persistence.bootstrap;

import cz.fit.persistence.core.PersistenceContext;
import cz.fit.persistence.exceptions.PersistenceException;

public class Bootstrap {

    private static PersistenceContext context;

    public static PersistenceContext initPersistence() throws PersistenceException {
        context = new PersistenceContext();
        context.init();
        return context;
    }
}
