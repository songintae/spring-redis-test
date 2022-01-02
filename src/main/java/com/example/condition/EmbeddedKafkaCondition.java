package com.example.condition;

import com.example.EmbeddedRedisServer;
import com.example.context.EmbeddedRedis;
import com.example.support.EmbeddedRedisServerFactory;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Optional;

public class EmbeddedKafkaCondition implements ExecutionCondition, AfterAllCallback, ParameterResolver {
    private static final Logger logger = LoggerFactory.getLogger(EmbeddedKafkaCondition.class);
    private static final String EMBEDDED_REDIS = "embedded-redis";
    private static final ThreadLocal<EmbeddedRedisServer> REDIS = new ThreadLocal<>();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {

        if (REDIS.get() == null) {
            return false;
        } else {
            return parameterContext.getParameter().getType().equals(EmbeddedRedisServer.class);
        }
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext context)
            throws ParameterResolutionException {

        EmbeddedRedisServer redis = getRedisFromStore(context);
        Assert.state(redis != null, "Could not find embedded redis instance");
        return redis;
    }

    @Override
    public void afterAll(ExtensionContext context) {
        EmbeddedRedisServer redis = REDIS.get();
        if (redis != null) {
            try {
                redis.destroy();
            } catch (Exception e) {
                logger.error("Could not destroy redis server", e);
            }
            REDIS.remove();
        }
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        Optional<AnnotatedElement> element = context.getElement();
        if (element.isPresent() && !springTestContext(element.get())) {

            EmbeddedRedis embedded = AnnotatedElementUtils.findMergedAnnotation(element.get(), EmbeddedRedis.class);
            // When running in a spring test context, the EmbeddedKafkaContextCustomizer will create the broker.
            if (embedded != null) {
                EmbeddedRedisServer redis = getRedisFromStore(context);
                if (redis == null) {
                    redis = createRedisServer(embedded);
                    REDIS.set(redis);
                    getStore(context).put(EMBEDDED_REDIS, redis);
                }
            }
        }
        return ConditionEvaluationResult.enabled("");
    }

    private boolean springTestContext(AnnotatedElement annotatedElement) {
        return AnnotatedElementUtils.findAllMergedAnnotations(annotatedElement, ExtendWith.class)
                .stream()
                .anyMatch(extended -> Arrays.asList(extended.value()).contains(SpringExtension.class));
    }

    @SuppressWarnings("unchecked")
    private EmbeddedRedisServer createRedisServer(EmbeddedRedis embedded) {
        EmbeddedRedisServer embeddedRedisServer = EmbeddedRedisServerFactory.getEmbeddedRedis(embedded);

        try {
            embeddedRedisServer.afterPropertiesSet();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not create redis server", e);
        }

        return embeddedRedisServer;
    }

    private EmbeddedRedisServer getRedisFromStore(ExtensionContext context) {
        return getParentStore(context).get(EMBEDDED_REDIS, EmbeddedRedisServer.class) == null
                ? getStore(context).get(EMBEDDED_REDIS, EmbeddedRedisServer.class)
                : getParentStore(context).get(EMBEDDED_REDIS, EmbeddedRedisServer.class);
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context));
    }

    private ExtensionContext.Store getParentStore(ExtensionContext context) {
        ExtensionContext parent = context.getParent().get();
        return parent.getStore(ExtensionContext.Namespace.create(getClass(), parent));
    }


    public static EmbeddedRedisServer getRedisServer() {
        return REDIS.get();
    }

}
