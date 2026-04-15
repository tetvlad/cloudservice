package ru.netology.cloudservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.cloudservice.dto.LoginRequest;
import ru.netology.cloudservice.dto.LoginResponse;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.UserRepository;
import ru.netology.cloudservice.security.JwtProvider;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для AuthService с использованием Mockito.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_Success() {
        String login = "test@mail.ru";
        String password = "password";
        String expectedToken = "mocked-jwt-token";

        LoginRequest request = new LoginRequest(login, password);
        User mockUser = new User(login, password);

        when(userRepository.findById(login)).thenReturn(Optional.of(mockUser));
        when(jwtProvider.generateToken(login)).thenReturn(expectedToken);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(expectedToken, response.authToken());

        verify(userRepository, times(1)).findById(login);
        verify(jwtProvider, times(1)).generateToken(login);
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        String login = "unknown@mail.ru";
        LoginRequest request = new LoginRequest(login, "password");

        when(userRepository.findById(login)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(jwtProvider, never()).generateToken(anyString());
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        String login = "test@mail.ru";
        LoginRequest request = new LoginRequest(login, "wrong-password");
        User mockUser = new User(login, "correct-password");

        when(userRepository.findById(login)).thenReturn(Optional.of(mockUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });

        assertEquals("Неверный пароль", exception.getMessage());
        verify(jwtProvider, never()).generateToken(anyString());
    }
}