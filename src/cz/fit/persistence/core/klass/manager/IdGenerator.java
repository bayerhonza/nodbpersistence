package cz.fit.persistence.core.klass.manager;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

    private static final Integer GENERATOR_STEP = 1;

    private final AtomicInteger currentId = new AtomicInteger();

    public IdGenerator() {
        currentId.set(1);
    }

    public IdGenerator(Integer seed) {
        currentId.set(seed);
    }

    public Integer getNextId() {
        return currentId.getAndAdd(GENERATOR_STEP);
    }


}
