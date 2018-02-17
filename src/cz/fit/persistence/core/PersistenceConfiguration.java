package cz.fit.persistence.core;

public class PersistenceConfiguration {

    private String path;
    private CompilationMethod ZIPMethod;

    public PersistenceConfiguration(String path, CompilationMethod ZIPMethod) {
        this.path = path;
        this.ZIPMethod = ZIPMethod;
    }

    public PersistenceConfiguration(String path) {
        this.path = path;
        this.ZIPMethod = null;
    }

}
