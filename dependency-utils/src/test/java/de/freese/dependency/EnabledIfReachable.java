// Created: 09 März 2025
package de.freese.dependency;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author Thomas Freese
 */
@Target({METHOD, TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@ExtendWith(EnabledIfReachableCondition.class)
public @interface EnabledIfReachable {
    int timeoutMillis();

    String uri();
}
