package com.chat.yourway.integration.extension;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.utility.DockerImageName;

public class RedisExtension implements Extension, BeforeAllCallback, AfterAllCallback {
    private static final String REDIS_PASSWORD = "12345676890";
    private static final RedisContainer container = new RedisContainer(DockerImageName.parse("redis:7.0.12"));


    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        container.start();
        System.setProperty("REDIS_HOST", container.getHost());
        System.setProperty("REDIS_PORT", String.valueOf(container.getFirstMappedPort()));
        System.setProperty("REDIS_PASSWORD", REDIS_PASSWORD);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {

    }
}
