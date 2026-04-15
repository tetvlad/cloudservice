package ru.netology.cloudservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.dto.FileResponse;
import ru.netology.cloudservice.model.File;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.FileRepository;
import ru.netology.cloudservice.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для бизнес-логики работы с файлами в облачном хранилище.
 */
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public FileService(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void uploadFile(String login, String filename, MultipartFile multipartFile) {
        User user = getUserByLogin(login);

        try {
            File file = new File();
            file.setFilename(filename);
            file.setSize(multipartFile.getSize());
            file.setFileContent(multipartFile.getBytes());
            file.setUser(user);

            fileRepository.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла", e);
        }
    }

    @Transactional
    public void deleteFile(String login, String filename) {
        User user = getUserByLogin(login);
        fileRepository.deleteByFilenameAndUser(filename, user);
    }

    @Transactional
    public File downloadFile(String login, String filename) {
        User user = getUserByLogin(login);
        return fileRepository.findByFilenameAndUser(filename, user)
                .orElseThrow(() -> new RuntimeException("Файл не найден"));
    }

    @Transactional
    public void renameFile(String login, String filename, String newName) {
        User user = getUserByLogin(login);
        File file = fileRepository.findByFilenameAndUser(filename, user)
                .orElseThrow(() -> new RuntimeException("Файл не найден"));

        file.setFilename(newName);
        fileRepository.save(file);
    }

    @Transactional(readOnly = true)
    public List<FileResponse> getFiles(String login, int limit) {
        User user = getUserByLogin(login);

        return fileRepository.findAllByUser(user).stream()
                .limit(limit)
                .map(file -> new FileResponse(file.getFilename(), file.getSize()))
                .collect(Collectors.toList());
    }

    /**
     * Вспомогательный метод для поиска пользователя.
     */
    private User getUserByLogin(String login) {
        return userRepository.findById(login)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
}