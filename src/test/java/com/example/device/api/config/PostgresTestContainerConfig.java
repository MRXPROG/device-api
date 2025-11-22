package com.example.device.api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class PostgresTestContainerConfig {

    private static final PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("device_db_test")
                    .withUsername("postgres")
                    .withPassword("postgres");

    static {
        container.start();
    }

    @DynamicPropertySource
    static void register(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", container::getJdbcUrl);
        r.add("spring.datasource.username", container::getUsername);
        r.add("spring.datasource.password", container::getPassword);
        r.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }
}