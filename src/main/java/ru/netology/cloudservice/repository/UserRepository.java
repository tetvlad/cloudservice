package ru.netology.cloudservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudservice.model.User;

/**
 * Репозиторий для работы с сущностью {@link User}.
 * Обеспечивает базовые операции CRUD для пользователей в базе данных.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
}