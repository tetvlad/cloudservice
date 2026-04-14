package ru.netology.cloudservice.dto;

/**
 * DTO для приема учетных данных от клиента.
 * Используется при обработке POST-запроса на эндпоинт /login.
 */
public record LoginRequest(String login, String password) {
}