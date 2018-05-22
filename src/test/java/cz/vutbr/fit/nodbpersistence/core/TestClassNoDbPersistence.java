package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceCoreException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestClassNoDbPersistence {

    public static Long objectId;

    public static String TEST_PATH = "D:\\Documents\\FIT\\3BIT\\IBP\\nodb_tests";

    public PersistenceManager pm;
    public PersistenceSettings settings;


    @Before
    public void beforeAllTests() throws PersistenceCoreException {
        settings = new PersistenceSettings();
        settings.setRootPath(TEST_PATH);

        PersistenceManagerFactoryBuilder builder = new PersistenceManagerFactoryBuilder(settings);
        PersistenceManagerFactory pmFactory = builder.buildPersistenceManagerFactory();
        pm = pmFactory.getPersistenceManager();
    }

    @AfterClass
    public static void afterAllTests() {
        Path rootPath = Paths.get(TEST_PATH);
        try {
            Files.walk(rootPath)
                    .map(Path::toFile)
                    .forEach(File::delete);
            Files.deleteIfExists(rootPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
