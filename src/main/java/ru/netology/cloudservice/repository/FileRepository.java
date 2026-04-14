package ru.netology.cloudservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudservice.model.File;
import ru.netology.cloudservice.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByFilenameAndUser(String filename, User user);

    void deleteByFilenameAndUser(String filename, User user);

    List<File> findAllByUser(User user);
}