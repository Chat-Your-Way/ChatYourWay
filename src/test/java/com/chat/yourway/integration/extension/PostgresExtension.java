package com.chat.yourway.integration.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresExtension implements Extension, BeforeAllCallback, AfterAllCallback {

  private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>(
      DockerImageName.parse("postgres:14.6"))
      .withEnv("TZ", "UTC");

  @Override
  public void beforeAll(ExtensionContext extensionContext) {
    container.start();
    System.setProperty("JDBC_URL", container.getJdbcUrl());
    System.setProperty("POSTGRES_USERNAME", container.getUsername());
    System.setProperty("POSTGRES_PASSWORD", container.getPassword());
  }

  @Override
  public void afterAll(ExtensionContext extensionContext) {
    //should do nothing, testcontainers will shut down the container
  }
}
