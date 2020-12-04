package com.zhu.gradleproject.annotation;

import java.lang.annotation.*;

/**
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 10/29/2020
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface AttributeValue {

    String value() default "";
}
