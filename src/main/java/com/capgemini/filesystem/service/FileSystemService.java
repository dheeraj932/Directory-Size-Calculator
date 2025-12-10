package com.capgemini.filesystem.service;

import com.capgemini.filesystem.exception.DirectoryAlreadyExistsException;
import com.capgemini.filesystem.exception.DirectoryNotFoundException;
import com.capgemini.filesystem.exception.InvalidPathException;
import com.capgemini.filesystem.model.Directory;
import com.capgemini.filesystem.model.FileSystemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for file system operations
 * Demonstrates business logic and exception handling
 */
@Service
public class FileSystemService {
    
    @Autowired
    private FileSystemManager fileSystemManager;
    
    /**
     * Change directory - supports relative and absolute paths
     */
    public Directory changeDirectory(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new InvalidPathException("Path cannot be empty");
        }
        
        Directory target = resolvePath(path);
        if (target == null) {
            throw new DirectoryNotFoundException("Directory not found: " + path);
        }
        
        fileSystemManager.setCurrentDirectory(target);
        return target;
    }
    
    /**
     * List directory contents
     */
    public Map<String, Object> listDirectory() {
        Directory current = fileSystemManager.getCurrentDirectory();
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> directories = new ArrayList<>();
        List<Map<String, Object>> files = new ArrayList<>();
        
        for (FileSystemEntity child : current.getChildren()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", child.getName());
            item.put("type", child.getType());
            item.put("size", child.calculateSize());
            item.put("path", child.getPath());
            
            if (child instanceof Directory) {
                directories.add(item);
            } else {
                files.add(item);
            }
        }
        
        result.put("currentPath", current.getPath());
        result.put("directories", directories);
        result.put("files", files);
        result.put("totalItems", directories.size() + files.size());
        
        return result;
    }
    
    /**
     * Calculate size of current directory (recursive)
     */
    public Map<String, Object> getDirectorySize() {
        Directory current = fileSystemManager.getCurrentDirectory();
        long size = current.calculateSize(); // Uses recursive calculation
        
        Map<String, Object> result = new HashMap<>();
        result.put("path", current.getPath());
        result.put("size", size);
        result.put("sizeInKB", size / 1024.0);
        result.put("sizeInMB", size / (1024.0 * 1024.0));
        
        return result;
    }
    
    /**
     * Create a new directory
     */
    public Directory createDirectory(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory name cannot be empty");
        }
        
        if (name.contains("/") || name.contains("\\")) {
            throw new InvalidPathException("Directory name cannot contain path separators");
        }
        
        Directory current = fileSystemManager.getCurrentDirectory();
        
        // Check if directory already exists
        FileSystemEntity existing = current.getChild(name);
        if (existing != null) {
            throw new DirectoryAlreadyExistsException("Directory already exists: " + name);
        }
        
        Directory newDirectory = new Directory(name);
        current.addChild(newDirectory);
        return newDirectory;
    }
    
    /**
     * Remove a directory
     */
    public void removeDirectory(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory name cannot be empty");
        }
        
        Directory current = fileSystemManager.getCurrentDirectory();
        
        FileSystemEntity entity = current.getChild(name);
        if (entity == null) {
            throw new DirectoryNotFoundException("Directory not found: " + name);
        }
        
        if (!(entity instanceof Directory)) {
            throw new IllegalArgumentException("Entity is not a directory: " + name);
        }
        
        // Prevent removing current directory or root
        if (entity == fileSystemManager.getCurrentDirectory()) {
            throw new IllegalArgumentException("Cannot remove current directory");
        }
        
        if (entity == fileSystemManager.getRoot()) {
            throw new IllegalArgumentException("Cannot remove root directory");
        }
        
        current.removeChild(name);
    }
    
    /**
     * Get current directory path
     */
    public Map<String, Object> getCurrentPath() {
        Directory current = fileSystemManager.getCurrentDirectory();
        Map<String, Object> result = new HashMap<>();
        result.put("path", current.getPath());
        result.put("name", current.getName());
        return result;
    }
    
    /**
     * Get directory tree structure
     */
    public FileSystemEntity.TreeRepresentation getDirectoryTree(String path) {
        Directory target;
        if (path == null || path.trim().isEmpty() || path.equals("/")) {
            target = fileSystemManager.getRoot();
        } else {
            target = resolvePath(path);
            if (target == null) {
                throw new DirectoryNotFoundException("Directory not found: " + path);
            }
        }
        
        return target.getTreeRepresentation();
    }
    
    /**
     * Resolve a path (absolute or relative) to a Directory
     */
    private Directory resolvePath(String path) {
        path = path.trim();
        
        // Handle absolute paths
        if (path.startsWith("/")) {
            return resolveAbsolutePath(path);
        }
        
        // Handle relative paths
        return resolveRelativePath(path);
    }
    
    /**
     * Resolve absolute path
     */
    private Directory resolveAbsolutePath(String path) {
        if (path.equals("/")) {
            return fileSystemManager.getRoot();
        }
        
        String[] parts = path.substring(1).split("/");
        Directory current = fileSystemManager.getRoot();
        
        for (String part : parts) {
            if (part.isEmpty()) continue;
            
            FileSystemEntity entity = current.getChild(part);
            if (entity == null || !(entity instanceof Directory)) {
                return null;
            }
            current = (Directory) entity;
        }
        
        return current;
    }
    
    /**
     * Resolve relative path
     */
    private Directory resolveRelativePath(String path) {
        String[] parts = path.split("/");
        Directory current = fileSystemManager.getCurrentDirectory();
        
        for (String part : parts) {
            if (part.isEmpty() || part.equals(".")) {
                continue;
            }
            
            if (part.equals("..")) {
                if (current.getParent() != null) {
                    current = (Directory) current.getParent();
                }
                continue;
            }
            
            FileSystemEntity entity = current.getChild(part);
            if (entity == null || !(entity instanceof Directory)) {
                return null;
            }
            current = (Directory) entity;
        }
        
        return current;
    }
}

