package ru.netology.cloudservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO для отправки ответа клиенту после успешной авторизации.
 * Содержит сгенерированный JWT токен, который фронтенд должен использовать для последующих запросов.
 *
 * @param authToken JWT токен доступа. Аннотация JsonProperty меняет имя поля в JSON на "auth-token".
 */
public record LoginResponse(@JsonProperty("auth-token") String authToken) {
}