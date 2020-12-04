package com.zhu.gradleproject.annotation;

import java.lang.annotation.*;

/**
 * <pre>
 *     @author zwy
 *     @date 1/13/2020
 *     email    1092478224@qq.com
 *     desc
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface EsId {
}
