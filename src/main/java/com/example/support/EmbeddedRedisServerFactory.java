package com.example.support;

import com.example.EmbeddedRedisServer;
import com.example.context.EmbeddedRedis;
import org.springframework.util.SocketUtils;
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;
import redis.embedded.util.Architecture;
import redis.embedded.util.OS;

public class EmbeddedRedisServerFactory {
    private static final int MIN_PORT = 10000;

    private EmbeddedRedisServerFactory() {
    }

    public static EmbeddedRedisServer getEmbeddedRedis(EmbeddedRedis embeddedRedis) {
        RedisExecProvider customProvider = RedisExecProvider.defaultProvider()
                .override(OS.UNIX, Architecture.x86_64, "/redis-bin/redis-server-3.2.12-linux-local")
                .override(OS.MAC_OS_X, Architecture.x86_64, "/redis-bin/redis-server-3.2.12-macOS-local");

        RedisServerBuilder builder = RedisServer.builder()
                .redisExecProvider(customProvider);

        int port = embeddedRedis.port() != -1 ? embeddedRedis.port() : SocketUtils.findAvailableTcpPort(MIN_PORT);
        builder.port(port);

        if (embeddedRedis.settings() != null) {
            for (String setting : embeddedRedis.settings()) {
                builder.setting(setting);
            }
        }

        return new EmbeddedRedisServer(builder.build());

    }
}
