package com.capgemini.filesystem.service;

import com.capgemini.filesystem.exception.DirectoryAlreadyExistsException;
import com.capgemini.filesystem.exception.DirectoryNotFoundException;
import com.capgemini.filesystem.exception.InvalidPathException;
import com.capgemini.filesystem.model.Directory;
import com.capgemini.filesystem.model.FileSystemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for FileSystemService
 */
@SpringBootTest
class FileSystemServiceTest {
    
    @Autowired
    private FileSystemService fileSystemService;
    
    @Autowired
    private FileSystemManager fileSystemManager;
    
    @BeforeEach
    void setUp() {
        // Reset to root directory before each test
        fileSystemManager.setCurrentDirectory(fileSystemManager.getRoot());
    }
    
    @Test
    void testChangeDirectory_ValidPath() {
        Directory result = fileSystemService.changeDirectory("documents");
        assertNotNull(result);
        assertEquals("documents", result.getName());
    }
    
    @Test
    void testChangeDirectory_AbsolutePath() {
        Directory result = fileSystemService.changeDirectory("/documents/work");
        assertNotNull(result);
        assertEquals("work", result.getName());
    }
    
    @Test
    void testChangeDirectory_InvalidPath() {
        assertThrows(DirectoryNotFoundException.class, () -> {
            fileSystemService.changeDirectory("nonexistent");
        });
    }
    
    @Test
    void testChangeDirectory_EmptyPath() {
        assertThrows(InvalidPathException.class, () -> {
            fileSystemService.changeDirectory("");
        });
    }
    
    @Test
    void testListDirectory() {
        Map<String, Object> result = fileSystemService.listDirectory();
        assertNotNull(result);
        assertTrue(result.containsKey("currentPath"));
        assertTrue(result.containsKey("directories"));
        assertTrue(result.containsKey("files"));
    }
    
    @Test
    void testGetDirectorySize() {
        Map<String, Object> result = fileSystemService.getDirectorySize();
        assertNotNull(result);
        assertTrue(result.containsKey("size"));
        assertTrue(result.containsKey("path"));
        assertTrue((Long) result.get("size") >= 0);
    }
    
    @Test
    void testGetDirectorySize_Recursive() {
        // Change to a directory with subdirectories
        fileSystemService.changeDirectory("documents");
        Map<String, Object> result = fileSystemService.getDirectorySize();
        
        assertNotNull(result);
        Long size = (Long) result.get("size");
        assertTrue(size > 0, "Size should be greater than 0 for directory with files");
    }
    
    @Test
    void testCreateDirectory() {
        Directory newDir = fileSystemService.createDirectory("test-dir");
        assertNotNull(newDir);
        assertEquals("test-dir", newDir.getName());
        
        // Verify it was added
        Map<String, Object> lsResult = fileSystemService.listDirectory();
        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> directories = 
            (java.util.List<Map<String, Object>>) lsResult.get("directories");
        
        boolean found = directories.stream()
            .anyMatch(dir -> "test-dir".equals(dir.get("name")));
        assertTrue(found, "New directory should be in the list");
    }
    
    @Test
    void testCreateDirectory_Duplicate() {
        fileSystemService.createDirectory("test-dir-2");
        
        assertThrows(DirectoryAlreadyExistsException.class, () -> {
            fileSystemService.createDirectory("test-dir-2");
        });
    }
    
    @Test
    void testCreateDirectory_EmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            fileSystemService.createDirectory("");
        });
    }
    
    @Test
    void testRemoveDirectory() {
        // Create a directory first
        fileSystemService.createDirectory("temp-dir");
        
        // Remove it
        assertDoesNotThrow(() -> {
            fileSystemService.removeDirectory("temp-dir");
        });
        
        // Verify it's gone
        Map<String, Object> lsResult = fileSystemService.listDirectory();
        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> directories = 
            (java.util.List<Map<String, Object>>) lsResult.get("directories");
        
        boolean found = directories.stream()
            .anyMatch(dir -> "temp-dir".equals(dir.get("name")));
        assertFalse(found, "Removed directory should not be in the list");
    }
    
    @Test
    void testRemoveDirectory_NotFound() {
        assertThrows(DirectoryNotFoundException.class, () -> {
            fileSystemService.removeDirectory("nonexistent-dir");
        });
    }
    
    @Test
    void testGetCurrentPath() {
        Map<String, Object> result = fileSystemService.getCurrentPath();
        assertNotNull(result);
        assertEquals("/", result.get("path"));
    }
    
    @Test
    void testGetDirectoryTree() {
        FileSystemEntity.TreeRepresentation tree = fileSystemService.getDirectoryTree(null);
        assertNotNull(tree);
        assertEquals("root", tree.getName());
        assertEquals("DIRECTORY", tree.getType());
        assertNotNull(tree.getChildren());
    }
    
    @Test
    void testGetDirectoryTree_WithPath() {
        FileSystemEntity.TreeRepresentation tree = fileSystemService.getDirectoryTree("/documents");
        assertNotNull(tree);
        assertEquals("documents", tree.getName());
    }
    
    @Test
    void testGetDirectoryTree_InvalidPath() {
        assertThrows(DirectoryNotFoundException.class, () -> {
            fileSystemService.getDirectoryTree("/invalid/path");
        });
    }
}

