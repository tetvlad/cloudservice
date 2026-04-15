package ru.netology.cloudservice.dto;

/**
 * DTO для отправки информации об ошибке в формате, ожидаемом FRONT-ом.
 */
public record ErrorResponse(String message, Integer id) {
}