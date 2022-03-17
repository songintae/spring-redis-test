package com.example.context;

import com.example.condition.EmbeddedRedisCondition;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@ExtendWith(EmbeddedRedisCondition.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EmbeddedRedis {
    int port() default -1;

    String[] settings() default {};
}
