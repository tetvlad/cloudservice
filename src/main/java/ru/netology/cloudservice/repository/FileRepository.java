package ru.netology.cloudservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudservice.model.File;
import ru.netology.cloudservice.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link File}.
 * Обеспечивает операции сохранения, поиска и удаления файлов пользователя.
 */
@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    /**
     * Поиск файла по его имени и владельцу.
     *
     * @param filename имя искомого файла
     * @param user     пользователь, которому принадлежит файл
     * @return {@link Optional} с найденным файлом или пустой Optional, если файл не найден
     */
    Optional<File> findByFilenameAndUser(String filename, User user);

    /**
     * Удаление файла по его имени и владельцу.
     *
     * @param filename имя удаляемого файла
     * @param user     пользователь, которому принадлежит файл
     */
    void deleteByFilenameAndUser(String filename, User user);

    /**
     * Получение списка всех файлов, принадлежащих конкретному пользователю.
     *
     * @param user пользователь, чьи файлы необходимо найти
     * @return список файлов пользователя
     */
    List<File> findAllByUser(User user);
}