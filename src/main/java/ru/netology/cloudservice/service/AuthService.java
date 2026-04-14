package ru.netology.cloudservice.service;

import org.springframework.stereotype.Service;
import ru.netology.cloudservice.dto.LoginRequest;
import ru.netology.cloudservice.dto.LoginResponse;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.UserRepository;
import ru.netology.cloudservice.security.JwtProvider;

/**
 * Сервис, реализующий бизнес-логику авторизации пользователей.
 * Взаимодействует с базой данных для проверки существования пользователя и корректности пароля,
 * а также управляет процессом выдачи токенов.
 */
@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public AuthService(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findById(request.login())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!user.getPassword().equals(request.password())) {
            throw new RuntimeException("Неверный пароль");
        }

        String token = jwtProvider.generateToken(user.getLogin());
        return new LoginResponse(token);
    }

    public void logout(String authToken) {
    }
}