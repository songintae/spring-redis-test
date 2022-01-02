package com.example.context;

import com.example.EmbeddedRedisServer;
import com.example.support.EmbeddedRedisServerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.util.Assert;

public class EmbeddedRedisCustomizer implements ContextCustomizer {
    private final EmbeddedRedis embeddedRedis;

    public EmbeddedRedisCustomizer(EmbeddedRedis embeddedRedis) {
        this.embeddedRedis = embeddedRedis;
    }

    @Override
    public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        Assert.isInstanceOf(DefaultSingletonBeanRegistry.class, beanFactory);

        EmbeddedRedisServer embeddedRedisServer = EmbeddedRedisServerFactory.getEmbeddedRedis(embeddedRedis);

        beanFactory.initializeBean(embeddedRedisServer, EmbeddedRedisServer.BEAN_NAME);
        beanFactory.registerSingleton(EmbeddedRedisServer.BEAN_NAME, embeddedRedisServer);
        ((DefaultSingletonBeanRegistry) beanFactory).registerDisposableBean(EmbeddedRedisServer.BEAN_NAME, embeddedRedisServer);
    }
}
