package ru.netology.cloudservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.netology.cloudservice.model.File;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.FileRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для FileService с использованием Mockito.
 */
@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    private User testUser;
    private File testFile;

    @BeforeEach
    void setUp() {
        testUser = new User("admin@mail.ru", "12345");
        testFile = new File(1L, "test-image.jpg", 1024L, "dummy-content".getBytes(), testUser);
    }

    @Test
    void getFiles_Success() {
        // Только fileRepository
        when(fileRepository.findAllByUser(testUser)).thenReturn(List.of(testFile));

        // Теперь метод будет возвращать List<File>, а не DTO
        List<File> result = fileService.getFiles(testUser, 3);

        assertEquals(1, result.size());
        assertEquals("test-image.jpg", result.get(0).getFilename());
        assertEquals(1024L, result.get(0).getSize());
    }

    @Test
    void uploadFile_Success() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "dummy-content".getBytes()
        );

        // Передаем testUser напрямую
        fileService.uploadFile(testUser, "test-image.jpg", mockMultipartFile);

        verify(fileRepository, times(1)).save(any(File.class));
    }

    @Test
    void deleteFile_Success() {
        fileService.deleteFile(testUser, "test-image.jpg");

        verify(fileRepository, times(1)).deleteByFilenameAndUser("test-image.jpg", testUser);
    }

    @Test
    void downloadFile_Success() {
        when(fileRepository.findByFilenameAndUser("test-image.jpg", testUser)).thenReturn(Optional.of(testFile));

        File result = fileService.downloadFile(testUser, "test-image.jpg");

        assertNotNull(result);
        assertEquals("test-image.jpg", result.getFilename());
    }

    @Test
    void renameFile_Success() {
        when(fileRepository.findByFilenameAndUser("test-image.jpg", testUser)).thenReturn(Optional.of(testFile));

        fileService.renameFile(testUser, "test-image.jpg", "new-name.jpg");

        assertEquals("new-name.jpg", testFile.getFilename());
        verify(fileRepository, times(1)).save(testFile);
    }
}