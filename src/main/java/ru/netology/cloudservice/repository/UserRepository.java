package ru.netology.cloudservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudservice.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}