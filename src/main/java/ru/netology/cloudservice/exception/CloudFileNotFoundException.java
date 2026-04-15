package ru.netology.cloudservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CloudFileNotFoundException extends RuntimeException {
    public CloudFileNotFoundException(String message) {
        super(message);
    }
}