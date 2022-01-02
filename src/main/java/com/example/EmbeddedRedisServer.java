package com.example;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import redis.embedded.RedisServer;

import java.util.List;
import java.util.Objects;

public class EmbeddedRedisServer implements InitializingBean, DisposableBean {
    public static final String BEAN_NAME = "embeddedRedis";
    private final RedisServer delegate;

    public EmbeddedRedisServer(RedisServer delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    public boolean isActive() {
        return delegate.isActive();
    }


    public List<Integer> ports() {
        return delegate.ports();
    }

    @Override
    public void destroy() throws Exception {
        delegate.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        delegate.start();
    }
}
