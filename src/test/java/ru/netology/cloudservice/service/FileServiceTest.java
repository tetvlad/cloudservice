package ru.netology.cloudservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.netology.cloudservice.dto.FileResponse;
import ru.netology.cloudservice.model.File;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.FileRepository;
import ru.netology.cloudservice.repository.UserRepository;

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

    @Mock
    private UserRepository userRepository;

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
        when(userRepository.findById(testUser.getLogin())).thenReturn(Optional.of(testUser));
        when(fileRepository.findAllByUser(testUser)).thenReturn(List.of(testFile));

        List<FileResponse> result = fileService.getFiles(testUser.getLogin(), 3);

        assertEquals(1, result.size());
        assertEquals("test-image.jpg", result.get(0).filename());
        assertEquals(1024L, result.get(0).size());
    }

    @Test
    void uploadFile_Success() {
        when(userRepository.findById(testUser.getLogin())).thenReturn(Optional.of(testUser));
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "dummy-content".getBytes()
        );

        fileService.uploadFile(testUser.getLogin(), "test-image.jpg", mockMultipartFile);

        verify(fileRepository, times(1)).save(any(File.class));
    }

    @Test
    void deleteFile_Success() {
        when(userRepository.findById(testUser.getLogin())).thenReturn(Optional.of(testUser));

        fileService.deleteFile(testUser.getLogin(), "test-image.jpg");

        verify(fileRepository, times(1)).deleteByFilenameAndUser("test-image.jpg", testUser);
    }

    @Test
    void downloadFile_Success() {
        when(userRepository.findById(testUser.getLogin())).thenReturn(Optional.of(testUser));
        when(fileRepository.findByFilenameAndUser("test-image.jpg", testUser)).thenReturn(Optional.of(testFile));

        File result = fileService.downloadFile(testUser.getLogin(), "test-image.jpg");

        assertNotNull(result);
        assertEquals("test-image.jpg", result.getFilename());
    }

    @Test
    void renameFile_Success() {
        when(userRepository.findById(testUser.getLogin())).thenReturn(Optional.of(testUser));
        when(fileRepository.findByFilenameAndUser("test-image.jpg", testUser)).thenReturn(Optional.of(testFile));

        fileService.renameFile(testUser.getLogin(), "test-image.jpg", "new-name.jpg");

        assertEquals("new-name.jpg", testFile.getFilename());
        verify(fileRepository, times(1)).save(testFile);
    }
}