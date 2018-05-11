package cz.vutbr.fit.nodbpersistence.annotations;

import java.lang.annotation.*;

/**
 * Annotation is obligatory for each persisted class. It guarantees the unique identifier of
 * object in the persistence system.
 * Currently supported types are <code>int</code> and its wrapper {@link Long}
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ObjectId {
}
