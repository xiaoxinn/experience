package com.xiaoxin.experience.system.log;

import java.lang.annotation.*;

/**
 * create by xiaoxin on 2021/3/13
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogExecute {
    String value() default "";

    boolean record() default false;
}
