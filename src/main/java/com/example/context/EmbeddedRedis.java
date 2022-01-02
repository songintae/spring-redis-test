package com.example.context;

import com.example.condition.EmbeddedKafkaCondition;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@ExtendWith(EmbeddedKafkaCondition.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EmbeddedRedis {
    int port() default -1;

    String[] settings() default {};
}
