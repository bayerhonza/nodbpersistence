package cz.fit.persistence.bootstrap;

import cz.fit.persistence.core.PersistenceContext;

public class Bootstrap {

    private static PersistenceContext context;

    public static PersistenceContext initPersistence() {
        context = new PersistenceContext();
        context.init();
        return context;
    }
}
