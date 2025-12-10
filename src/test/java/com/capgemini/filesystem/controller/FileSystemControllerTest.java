package com.capgemini.filesystem.controller;

import com.capgemini.filesystem.service.FileSystemManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for FileSystemController
 * Tests all API endpoints using MockMvc
 */
@SpringBootTest
@AutoConfigureMockMvc
class FileSystemControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private FileSystemManager fileSystemManager;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        // Reset to root directory before each test
        fileSystemManager.setCurrentDirectory(fileSystemManager.getRoot());
    }
    
    @Test
    void testGetCurrentPath() throws Exception {
        mockMvc.perform(get("/api/filesystem/pwd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.path").value("/"))
                .andExpect(jsonPath("$.name").value("root"));
    }
    
    @Test
    void testListDirectory() throws Exception {
        mockMvc.perform(get("/api/filesystem/ls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.currentPath").value("/"))
                .andExpect(jsonPath("$.directories").isArray())
                .andExpect(jsonPath("$.files").isArray())
                .andExpect(jsonPath("$.totalItems").exists());
    }
    
    @Test
    void testGetDirectorySize() throws Exception {
        mockMvc.perform(get("/api/filesystem/size"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.path").value("/"))
                .andExpect(jsonPath("$.size").exists())
                .andExpect(jsonPath("$.sizeInKB").exists())
                .andExpect(jsonPath("$.sizeInMB").exists());
    }
    
    @Test
    void testChangeDirectory_RelativePath() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("path", "documents");
        
        mockMvc.perform(post("/api/filesystem/cd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Directory changed successfully"))
                .andExpect(jsonPath("$.currentPath").value("/documents"))
                .andExpect(jsonPath("$.directoryName").value("documents"));
    }
    
    @Test
    void testChangeDirectory_AbsolutePath() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("path", "/documents/work");
        
        mockMvc.perform(post("/api/filesystem/cd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.currentPath").value("/documents/work"))
                .andExpect(jsonPath("$.directoryName").value("work"));
    }
    
    @Test
    void testChangeDirectory_NotFound() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("path", "nonexistent");
        
        mockMvc.perform(post("/api/filesystem/cd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Directory Not Found"));
    }
    
    @Test
    void testCreateDirectory() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("name", "test-directory");
        
        mockMvc.perform(post("/api/filesystem/mkdir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Directory created successfully"))
                .andExpect(jsonPath("$.directoryName").value("test-directory"))
                .andExpect(jsonPath("$.path").value("/test-directory"));
    }
    
    @Test
    void testCreateDirectory_Duplicate() throws Exception {
        // Create directory first
        Map<String, String> createRequest = new HashMap<>();
        createRequest.put("name", "duplicate-test");
        
        mockMvc.perform(post("/api/filesystem/mkdir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());
        
        // Try to create again
        mockMvc.perform(post("/api/filesystem/mkdir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Directory Already Exists"));
    }
    
    @Test
    void testRemoveDirectory() throws Exception {
        // Create directory first
        Map<String, String> createRequest = new HashMap<>();
        createRequest.put("name", "temp-remove");
        
        mockMvc.perform(post("/api/filesystem/mkdir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());
        
        // Remove it
        mockMvc.perform(delete("/api/filesystem/rmdir")
                        .param("name", "temp-remove"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Directory removed successfully"))
                .andExpect(jsonPath("$.removedDirectory").value("temp-remove"));
    }
    
    @Test
    void testRemoveDirectory_NotFound() throws Exception {
        mockMvc.perform(delete("/api/filesystem/rmdir")
                        .param("name", "nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Directory Not Found"));
    }
    
    @Test
    void testGetDirectoryTree_Root() throws Exception {
        mockMvc.perform(get("/api/filesystem/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.tree.name").value("root"))
                .andExpect(jsonPath("$.tree.type").value("DIRECTORY"))
                .andExpect(jsonPath("$.tree.children").isArray());
    }
    
    @Test
    void testGetDirectoryTree_WithPath() throws Exception {
        mockMvc.perform(get("/api/filesystem/tree")
                        .param("path", "/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.tree.name").value("documents"))
                .andExpect(jsonPath("$.tree.type").value("DIRECTORY"));
    }
    
    @Test
    void testGetDirectoryTree_InvalidPath() throws Exception {
        mockMvc.perform(get("/api/filesystem/tree")
                        .param("path", "/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Directory Not Found"));
    }
}