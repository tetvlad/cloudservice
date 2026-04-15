package ru.netology.cloudservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.cloudservice.dto.LoginRequest;
import ru.netology.cloudservice.dto.LoginResponse;
import ru.netology.cloudservice.exception.BadCredentialsException;
import ru.netology.cloudservice.exception.UserNotFoundException;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.UserRepository;
import ru.netology.cloudservice.security.JwtProvider;

/**
 * Сервис, реализующий бизнес-логику авторизации пользователей.
 * Взаимодействует с базой данных для проверки существования пользователя и корректности пароля,
 * а также управляет процессом выдачи токенов.
 */
@Slf4j
@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public AuthService(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {

        log.info("Попытка авторизации пользователя: {}", request.login());

        User user = userRepository.findById(request.login())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (!user.getPassword().equals(request.password())) {
            throw new BadCredentialsException("Неверный пароль");
        }

        log.info("Пользователь {} успешно авторизован", request.login());

        String token = jwtProvider.generateToken(user.getLogin());
        return new LoginResponse(token);
    }

    public void logout(String authToken) {
        log.info("Получен запрос на выход из системы");
    }
}