package cz.vutbr.fit.nodbpersistence.core;

import org.junit.rules.ExternalResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NoDbRule extends ExternalResource {

    public PersistenceManager pm;
    public PersistenceSettings settings;

    @Override
    protected void before() throws Throwable {
        this.settings = new PersistenceSettings();
        this.settings.loadFromPropertiesFile("src/test/resources/nodbpersistence.xml");

        PersistenceManagerFactoryBuilder builder = new PersistenceManagerFactoryBuilder(this.settings);
        PersistenceManagerFactory pmFactory = builder.buildPersistenceManagerFactory();
        this.pm = pmFactory.getPersistenceManager();
    }

    @Override
    protected void after() {
        Path rootPath = Paths.get(this.settings.getRootPath());
        try {
            Files.walk(rootPath)
                    .map(Path::toFile)
                    .forEach(File::delete);
            Files.deleteIfExists(rootPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PersistenceManager getPersistenceManager() {
        return this.pm;
    }

}
