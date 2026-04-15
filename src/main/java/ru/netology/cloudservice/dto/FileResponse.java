package ru.netology.cloudservice.dto;

/**
 * DTO для отправки информации о файле в ответ на запрос списка файлов (/list).
 */
public record FileResponse(String filename, Long size) {
}