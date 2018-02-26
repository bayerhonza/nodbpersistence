package cz.fit.persistence.core;

public final class PersistenceConfiguration {

    private String path;
    private CompilationMethod ZIPMethod;

    PersistenceConfiguration(String path, CompilationMethod ZIPMethod) {
        this.path = path;
        this.ZIPMethod = ZIPMethod;
    }

    PersistenceConfiguration(String path) {
        this.path = path;
        this.ZIPMethod = null;
    }

    public String getPath() {
        return path;
    }
}
