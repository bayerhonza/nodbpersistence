package cz.fit.persistence.annotations;

import java.lang.annotation.*;

/**
 * <p>Annotation of ObjectId</p>
 *
 * <p></p>Currently supported types are:
 * <ul>
 * <li><code>int</code> and its wrapper {@link Integer}</li>
 *
 * </ul>
 * </p>
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ObjectId {

}
