package ru.netology.cloudservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционный тест с использованием Testcontainers.
 * Поднимает реальную базу данных PostgreSQL в Docker-контейнере для проверки
 * успешного запуска полного контекста приложения.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CloudserviceApplicationTests {

	@Container
	private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		// Отключил data.sql, чтобы не было конфликтов
		registry.add("spring.sql.init.mode", () -> "never");
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
	}

	@Test
	void contextLoads() {
		assertTrue(postgres.isRunning());
	}
}