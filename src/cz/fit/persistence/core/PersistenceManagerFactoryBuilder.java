package cz.fit.persistence.core;

import cz.fit.persistence.exceptions.PersistenceCoreException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PersistenceManagerFactoryBuilder {

    private final PersistenceContext persistenceContext;

    public PersistenceManagerFactoryBuilder(PersistenceSettings persistenceConfiguration) throws PersistenceCoreException {
        this.persistenceContext = new PersistenceContext(persistenceConfiguration);
        persistenceContext.init();
    }

    public PersistenceManagerFactory buildPersistenceManagerFactory() {
        return new PersistenceManagerFactory(persistenceContext);
    }

    private boolean isRootFsPresent(String rootFsDir) {
        Path rooFsPath = Paths.get(rootFsDir);
        return rooFsPath.toFile().exists();
    }
}
