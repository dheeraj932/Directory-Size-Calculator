package com.capgemini.filesystem.model;

import lombok.EqualsAndHashCode;

/**
 * File entity - inherits from FileSystemEntity
 * Demonstrates inheritance
 */
@EqualsAndHashCode(callSuper = true)
public class File extends FileSystemEntity {
    private long size;
    
    public File(String name, long size) {
        super(name);
        this.size = size;
    }
    
    /**
     * Polymorphic implementation - files have a fixed size
     */
    @Override
    public long calculateSize() {
        return size;
    }
    
    @Override
    public String getType() {
        return "FILE";
    }
    
    /**
     * Polymorphic implementation - files have no children
     */
    @Override
    public TreeRepresentation getTreeRepresentation() {
        return new TreeRepresentation(name, getType(), size);
    }
}

