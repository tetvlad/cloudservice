package ru.netology.cloudservice.controller;

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
import ru.netology.cloudservice.model.File;
import ru.netology.cloudservice.service.FileService;

import java.util.List;

/**
 * REST-контроллер для работы с файлами.
 * Реализует эндпоинты /file и /list.
 */
@RestController
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Вспомогательный метод для получения логина текущего авторизованного пользователя.
     */
    private String getCurrentUserLogin() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("filename") String filename,
                                        @RequestPart("file") MultipartFile file) {
        fileService.uploadFile(getCurrentUserLogin(), filename, file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam("filename") String filename) {
        fileService.deleteFile(getCurrentUserLogin(), filename);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename) {
        File file = fileService.downloadFile(getCurrentUserLogin(), filename);
        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(new ByteArrayResource(file.getFileContent()));
    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(@RequestParam("filename") String filename,
                                        @RequestBody RenameFileRequest request) {
        fileService.renameFile(getCurrentUserLogin(), filename, request.name());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public List<FileResponse> getFiles(@RequestParam("limit") int limit) {
        return fileService.getFiles(getCurrentUserLogin(), limit);
    }
}
