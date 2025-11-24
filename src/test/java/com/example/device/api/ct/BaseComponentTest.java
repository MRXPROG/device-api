package com.example.device.api.ct;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@ActiveProfiles("ct")
public abstract class BaseComponentTest {

    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("device_db_test")
                    .withUsername("postgres")
                    .withPassword("postgres");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void register(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");

        registry.add("spring.liquibase.enabled", () -> true);
    }
}