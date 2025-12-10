package com.capgemini.filesystem.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for file system entities (files and directories)
 * Demonstrates inheritance in OOP design
 */
@Data
public abstract class FileSystemEntity {
    protected String name;
    protected FileSystemEntity parent;
    
    public FileSystemEntity(String name) {
        this.name = name;
    }
    
    /**
     * Abstract method to calculate size - demonstrates polymorphism
     * Each subclass implements this differently
     */
    public abstract long calculateSize();
    
    /**
     * Abstract method to get the type of entity
     */
    public abstract String getType();
    
    /**
     * Get the full path of this entity
     */
    public String getPath() {
        if (parent == null) {
            return name.equals("root") ? "/" : "/" + name;
        }
        String parentPath = parent.getPath();
        if (parentPath.equals("/")) {
            return "/" + name;
        }
        return parentPath + "/" + name;
    }
    
    /**
     * Get tree representation - demonstrates polymorphism
     */
    public abstract TreeRepresentation getTreeRepresentation();
    
    /**
     * Inner class for tree representation
     */
    @Data
    public static class TreeRepresentation {
        private String name;
        private String type;
        private long size;
        private List<TreeRepresentation> children;
        
        public TreeRepresentation(String name, String type, long size) {
            this.name = name;
            this.type = type;
            this.size = size;
            this.children = new ArrayList<>();
        }
    }
}

