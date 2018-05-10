package cz.vutbr.fit.nodbpersistence.annotations;

import java.lang.annotation.*;

/**
 * Attributes annotated by {@link Transient} will not be persisted and are ignored by the system.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Transient {

}
