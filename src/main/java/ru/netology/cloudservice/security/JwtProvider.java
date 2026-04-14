package ru.netology.cloudservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Класс для работы с JSON Web Tokens.
 * Отвечает за генерацию новых токенов при успешной авторизации,
 * их криптографическую валидацию и извлечение данных (логина) из токена,
 * Keys.secretKeyFor(SignatureAlgorithm.HS256) - генерируем секретный ключ для подписи токенов.
 */
@Component
public class JwtProvider {

    private final Key jwtAccessSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final long expirationTime = 3600000;

    /**
     * Метод для генерации токена после успешного логина.
     * setSubject(login) - вшиваем логин пользователя в токен,
     * signWith(jwtAccessSecret) - подписываем нашим секретным ключом.
     */
    public String generateToken(String login) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(login) //
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(jwtAccessSecret)
                .compact();
    }

    /**
     * Метод для проверки валидности токена (не подделан ли он и не истек ли срок).
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtAccessSecret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Метод для извлечения логина из токена.
     */
    public String getLoginFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtAccessSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}