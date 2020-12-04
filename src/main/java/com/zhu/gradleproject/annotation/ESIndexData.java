package com.zhu.gradleproject.annotation;

import java.lang.annotation.*;


/**
 * @author zwy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ESIndexData {
    String indexName();
}
