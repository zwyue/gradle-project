package com.zhu.gradleproject.annotation;

import java.lang.annotation.*;

/**
 * copyright     <a href="http://ditop.tech/>德拓科技</a>
 * <pre>
 * </pre>
 *
 * @author zwy
 * @date 12/7/2020
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface SearchField {
}
