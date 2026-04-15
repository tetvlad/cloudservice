package ru.netology.cloudservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.exception.CloudFileNotFoundException;
import ru.netology.cloudservice.exception.FileProcessingException;
import ru.netology.cloudservice.model.File;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.FileRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для бизнес-логики работы с файлами в облачном хранилище.
 */
@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Transactional
    public void uploadFile(User user, String filename, MultipartFile multipartFile) {

        try {
            File file = new File();
            file.setFilename(filename);
            file.setSize(multipartFile.getSize());
            file.setFileContent(multipartFile.getBytes());
            file.setUser(user);

            fileRepository.save(file);
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка при чтении файла", e);
        }
    }

    @Transactional
    public void deleteFile(User user, String filename) {
        fileRepository.deleteByFilenameAndUser(filename, user);
    }

    @Transactional
    public File downloadFile(User user, String filename) {
        return fileRepository.findByFilenameAndUser(filename, user)
                .orElseThrow(() -> new CloudFileNotFoundException("Файл не найден"));
    }

    @Transactional
    public void renameFile(User user, String filename, String newName) {
        File file = fileRepository.findByFilenameAndUser(filename, user)
                .orElseThrow(() -> new CloudFileNotFoundException("Файл не найден"));

        file.setFilename(newName);
        fileRepository.save(file);
    }

    @Transactional(readOnly = true)
    public List<File> getFiles(User user, int limit) {

        return fileRepository.findAllByUser(user).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}