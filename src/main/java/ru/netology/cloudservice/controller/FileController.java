package ru.netology.cloudservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.dto.FileResponse;
import ru.netology.cloudservice.dto.RenameFileRequest;
import ru.netology.cloudservice.exception.UserNotFoundException;
import ru.netology.cloudservice.model.File;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.UserRepository;
import ru.netology.cloudservice.service.FileService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST-контроллер для работы с файлами.
 * Реализует эндпоинты /file и /list.
 */
@Slf4j
@RestController
public class FileController {

    private final FileService fileService;
    private final UserRepository userRepository;

    public FileController(FileService fileService, UserRepository userRepository) {
        this.fileService = fileService;
        this.userRepository = userRepository;
    }

    /**
     * Вспомогательный метод для получения текущего авторизованного пользователя из БД.
     */
    private User getCurrentUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findById(login)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("filename") String filename,
                                        @RequestPart("file") MultipartFile file) {

        log.info("Получен запрос на загрузку файла: {}", filename);

        fileService.uploadFile(getCurrentUser(), filename, file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam("filename") String filename) {

        log.info("Получен запрос на удаление файла: {}", filename);

        fileService.deleteFile(getCurrentUser(), filename);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename) {
        File file = fileService.downloadFile(getCurrentUser(), filename);
        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(new ByteArrayResource(file.getFileContent()));
    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(@RequestParam("filename") String filename,
                                        @RequestBody RenameFileRequest request) {
        fileService.renameFile(getCurrentUser(), filename, request.name());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public List<FileResponse> getFiles(@RequestParam("limit") int limit) {

        List<File> files = fileService.getFiles(getCurrentUser(), limit);

        return files.stream()
                .map(file -> new FileResponse(file.getFilename(), file.getSize()))
                .collect(Collectors.toList());
    }
}