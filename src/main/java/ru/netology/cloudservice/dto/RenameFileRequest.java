package ru.netology.cloudservice.dto;

/**
 * DTO для приема нового имени файла при запросе на переименование (PUT /file).
 */
public record RenameFileRequest(String name) {
}