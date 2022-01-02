package com.example.context;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

import java.util.List;

public class EmbeddedRedisContextCustomizerFactory implements ContextCustomizerFactory {
    @Override
    public ContextCustomizer createContextCustomizer(Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
        EmbeddedRedis embeddedRedis = AnnotatedElementUtils.findMergedAnnotation(testClass, EmbeddedRedis.class);
        return embeddedRedis != null ? new EmbeddedRedisCustomizer(embeddedRedis) : null;
    }
}
