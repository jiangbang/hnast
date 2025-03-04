package com.glface.log;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoggerMonitor {
    String value() default "";
}
