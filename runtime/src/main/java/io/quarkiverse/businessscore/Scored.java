package io.quarkiverse.businessscore;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;

import io.quarkiverse.businessscore.BusinessScore.Score;

/**
 * The business score is increased when an anotated business method is invoked.
 */
@InterceptorBinding
@Retention(RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Scored {

    /**
     * @see Score#value()
     */
    @Nonbinding
    int value() default 1;

    /**
     * @see Score#labels()
     */
    @Nonbinding
    String[] labels() default {};

    /**
     * If set to {@code true} then the score is increased even if the intercepted method throws an exception.
     */
    @Nonbinding
    boolean scoreOnException() default false;

}
