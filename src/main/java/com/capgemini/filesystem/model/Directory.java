package com.capgemini.filesystem.model;

import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Directory entity - inherits from FileSystemEntity
 * Demonstrates inheritance and contains files and subdirectories
 */
@EqualsAndHashCode(callSuper = true)
public class Directory extends FileSystemEntity {
    private List<FileSystemEntity> children;
    
    public Directory(String name) {
        super(name);
        this.children = new ArrayList<>();
    }
    
    /**
     * Polymorphic implementation - directories calculate size recursively
     * This demonstrates recursion as requested
     */
    @Override
    public long calculateSize() {
        return children.stream()
                .mapToLong(FileSystemEntity::calculateSize)
                .sum();
    }
    
    @Override
    public String getType() {
        return "DIRECTORY";
    }
    
    /**
     * Add a child entity (file or directory)
     */
    public void addChild(FileSystemEntity entity) {
        entity.setParent(this);
        children.add(entity);
    }
    
    /**
     * Remove a child entity by name
     */
    public boolean removeChild(String name) {
        return children.removeIf(child -> child.getName().equals(name));
    }
    
    /**
     * Get child by name
     */
    public FileSystemEntity getChild(String name) {
        return children.stream()
                .filter(child -> child.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get all children
     */
    public List<FileSystemEntity> getChildren() {
        return new ArrayList<>(children);
    }
    
    /**
     * Get only directories
     */
    public List<Directory> getDirectories() {
        return children.stream()
                .filter(child -> child instanceof Directory)
                .map(child -> (Directory) child)
                .collect(Collectors.toList());
    }
    
    /**
     * Get only files
     */
    public List<File> getFiles() {
        return children.stream()
                .filter(child -> child instanceof File)
                .map(child -> (File) child)
                .collect(Collectors.toList());
    }
    
    /**
     * Polymorphic implementation - directories build tree recursively
     */
    @Override
    public TreeRepresentation getTreeRepresentation() {
        TreeRepresentation tree = new TreeRepresentation(name, getType(), calculateSize());
        for (FileSystemEntity child : children) {
            tree.getChildren().add(child.getTreeRepresentation());
        }
        return tree;
    }
}

