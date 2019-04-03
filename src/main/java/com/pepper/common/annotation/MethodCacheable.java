package com.pepper.common.annotation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface MethodCacheable {

    /**
     * 缓存时间
     * @return
     */
    int time() default 60;


    /**
     * key
     * @return
     */
    String key() default "";



}
