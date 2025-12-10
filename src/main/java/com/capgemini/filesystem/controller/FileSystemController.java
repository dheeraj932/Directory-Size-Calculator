package com.capgemini.filesystem.controller;

import com.capgemini.filesystem.model.Directory;
import com.capgemini.filesystem.model.FileSystemEntity;
import com.capgemini.filesystem.service.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for file system operations
 * Provides endpoints for cd, ls, size, mkdir, rmdir, pwd, and tree commands
 */
@RestController
@RequestMapping("/api/filesystem")
@CrossOrigin(origins = "*")
public class FileSystemController {
    
    @Autowired
    private FileSystemService fileSystemService;
    
    /**
     * Change directory
     * POST /api/filesystem/cd
     */
    @PostMapping("/cd")
    public ResponseEntity<Map<String, Object>> changeDirectory(@RequestBody Map<String, String> request) {
        String path = request.get("path");
        Directory directory = fileSystemService.changeDirectory(path);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Directory changed successfully");
        response.put("currentPath", directory.getPath());
        response.put("directoryName", directory.getName());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * List directory contents
     * GET /api/filesystem/ls
     */
    @GetMapping("/ls")
    public ResponseEntity<Map<String, Object>> listDirectory() {
        Map<String, Object> result = fileSystemService.listDirectory();
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get directory size (recursive)
     * GET /api/filesystem/size
     */
    @GetMapping("/size")
    public ResponseEntity<Map<String, Object>> getDirectorySize() {
        Map<String, Object> result = fileSystemService.getDirectorySize();
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Create directory
     * POST /api/filesystem/mkdir
     */
    @PostMapping("/mkdir")
    public ResponseEntity<Map<String, Object>> createDirectory(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        Directory directory = fileSystemService.createDirectory(name);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Directory created successfully");
        response.put("directoryName", directory.getName());
        response.put("path", directory.getPath());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Remove directory
     * DELETE /api/filesystem/rmdir
     */
    @DeleteMapping("/rmdir")
    public ResponseEntity<Map<String, Object>> removeDirectory(@RequestParam String name) {
        fileSystemService.removeDirectory(name);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Directory removed successfully");
        response.put("removedDirectory", name);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get current directory path
     * GET /api/filesystem/pwd
     */
    @GetMapping("/pwd")
    public ResponseEntity<Map<String, Object>> getCurrentPath() {
        Map<String, Object> result = fileSystemService.getCurrentPath();
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get directory tree structure
     * GET /api/filesystem/tree
     */
    @GetMapping("/tree")
    public ResponseEntity<Map<String, Object>> getDirectoryTree(@RequestParam(required = false) String path) {
        FileSystemEntity.TreeRepresentation tree = fileSystemService.getDirectoryTree(path);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("tree", tree);
        
        return ResponseEntity.ok(response);
    }
}

