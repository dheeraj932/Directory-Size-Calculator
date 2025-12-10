package com.capgemini.filesystem.service;

import com.capgemini.filesystem.model.Directory;
import org.springframework.stereotype.Component;

/**
 * Manages the file system state and current directory
 */
@Component
public class FileSystemManager {
    private Directory root;
    private Directory currentDirectory;
    
    public FileSystemManager() {
        initializeFileSystem();
    }
    
    /**
     * Initialize file system with a 3-level directory structure
     */
    private void initializeFileSystem() {
        root = new Directory("root");
        currentDirectory = root;
        
        // Level 1: Create main directories
        Directory documents = new Directory("documents");
        Directory projects = new Directory("projects");
        Directory downloads = new Directory("downloads");
        
        root.addChild(documents);
        root.addChild(projects);
        root.addChild(downloads);
        
        // Level 2: Create subdirectories
        Directory work = new Directory("work");
        Directory personal = new Directory("personal");
        documents.addChild(work);
        documents.addChild(personal);
        
        Directory javaProject = new Directory("java-project");
        Directory springProject = new Directory("spring-project");
        projects.addChild(javaProject);
        projects.addChild(springProject);
        
        Directory images = new Directory("images");
        Directory videos = new Directory("videos");
        downloads.addChild(images);
        downloads.addChild(videos);
        
        // Level 3: Create sub-subdirectories
        Directory reports = new Directory("reports");
        Directory invoices = new Directory("invoices");
        work.addChild(reports);
        work.addChild(invoices);
        
        Directory photos = new Directory("photos");
        Directory screenshots = new Directory("screenshots");
        images.addChild(photos);
        images.addChild(screenshots);
        
        // Add some files with sizes
        documents.addChild(new com.capgemini.filesystem.model.File("readme.txt", 1024));
        work.addChild(new com.capgemini.filesystem.model.File("report1.pdf", 2048));
        work.addChild(new com.capgemini.filesystem.model.File("report2.pdf", 3072));
        reports.addChild(new com.capgemini.filesystem.model.File("annual-report.pdf", 5120));
        invoices.addChild(new com.capgemini.filesystem.model.File("invoice-001.pdf", 1536));
        
        javaProject.addChild(new com.capgemini.filesystem.model.File("Main.java", 512));
        javaProject.addChild(new com.capgemini.filesystem.model.File("Utils.java", 768));
        springProject.addChild(new com.capgemini.filesystem.model.File("Application.java", 1024));
        springProject.addChild(new com.capgemini.filesystem.model.File("Controller.java", 1280));
        
        photos.addChild(new com.capgemini.filesystem.model.File("vacation.jpg", 4096));
        photos.addChild(new com.capgemini.filesystem.model.File("family.jpg", 3584));
        screenshots.addChild(new com.capgemini.filesystem.model.File("screen1.png", 2048));
    }
    
    public Directory getRoot() {
        return root;
    }
    
    public Directory getCurrentDirectory() {
        return currentDirectory;
    }
    
    public void setCurrentDirectory(Directory directory) {
        this.currentDirectory = directory;
    }
}

