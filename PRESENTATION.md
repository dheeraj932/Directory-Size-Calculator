# Directory Size Calculator Application - Presentation

## Overview

This document provides a comprehensive overview of the Directory Size Calculator Application, including design approach, key components, and how to run and test the application.

## Approach and Design

### Design Philosophy

The application follows **Object-Oriented Programming (OOP)** principles with a focus on:

1. **Inheritance**: Abstract base class `FileSystemEntity` with concrete implementations `Directory` and `File`
2. **Polymorphism**: Method overriding for size calculation and tree representation
3. **Encapsulation**: Private fields with controlled access through methods
4. **Separation of Concerns**: Clear separation between model, service, and controller layers

### Architecture Pattern

The application uses a **layered architecture**:

```
┌─────────────────────────────────┐
│   REST Controller Layer         │  ← API Endpoints
├─────────────────────────────────┤
│   Service Layer                 │  ← Business Logic
├─────────────────────────────────┤
│   Model Layer                   │  ← Domain Entities
└─────────────────────────────────┘
```

### Key Design Decisions

1. **In-Memory Storage**: All file system data is stored in memory for simplicity and performance
2. **Recursive Size Calculation**: Uses recursion to calculate directory sizes, as requested
3. **Path Resolution**: Supports both absolute (`/documents/work`) and relative (`documents/work`, `..`) paths
4. **Exception Handling**: Comprehensive exception handling with custom exceptions and global handler
5. **RESTful API**: RESTful design for easy integration and testing

## Key Files and Folders

### Domain Model (`src/main/java/com/capgemini/filesystem/model/`)

- **FileSystemEntity.java**: Abstract base class defining common behavior
  - Abstract methods: `calculateSize()`, `getType()`, `getTreeRepresentation()`
  - Common methods: `getPath()`
  
- **Directory.java**: Represents a directory (folder)
  - Contains list of children (files and subdirectories)
  - Recursive size calculation
  - Tree representation with children
  
- **File.java**: Represents a file
  - Has a fixed size attribute
  - Direct size calculation (no recursion needed)

### Service Layer (`src/main/java/com/capgemini/filesystem/service/`)

- **FileSystemService.java**: Core business logic
  - `changeDirectory()`: Navigate through file system
  - `listDirectory()`: List current directory contents
  - `getDirectorySize()`: Calculate directory size recursively
  - `createDirectory()`: Create new directories
  - `removeDirectory()`: Delete directories
  - `getDirectoryTree()`: Get complete tree structure
  - Path resolution logic (absolute and relative)

- **FileSystemManager.java**: Manages file system state
  - Maintains root directory
  - Tracks current directory
  - Initializes 3-level directory structure with seed data

### Controller Layer (`src/main/java/com/capgemini/filesystem/controller/`)

- **FileSystemController.java**: REST API endpoints
  - `POST /api/filesystem/cd`: Change directory
  - `GET /api/filesystem/ls`: List directory
  - `GET /api/filesystem/size`: Get directory size
  - `POST /api/filesystem/mkdir`: Create directory
  - `DELETE /api/filesystem/rmdir`: Remove directory
  - `GET /api/filesystem/pwd`: Get current path
  - `GET /api/filesystem/tree`: Get directory tree

### Exception Handling (`src/main/java/com/capgemini/filesystem/exception/`)

- **DirectoryNotFoundException.java**: When directory doesn't exist
- **InvalidPathException.java**: When path is invalid
- **DirectoryAlreadyExistsException.java**: When creating duplicate directory
- **GlobalExceptionHandler.java**: Centralized exception handling with proper HTTP status codes

### Test Files (`src/test/java/com/capgemini/filesystem/`)

- **FileSystemServiceTest.java**: Unit tests for service layer
  - Tests all service methods
  - Tests exception scenarios
  - Tests edge cases

- **FileSystemControllerTest.java**: Integration tests for controller
  - Tests all API endpoints
  - Uses MockMvc for HTTP testing

### Configuration Files

- **build.gradle**: Gradle build configuration with Spring Boot dependencies
- **settings.gradle**: Project settings
- **application.properties**: Application configuration (port, logging)
- **Postman_Collection.json**: Postman collection for API testing

## Process to Run, Test, and Verify

### Prerequisites

1. **Java 17+** installed
2. **Gradle 8.5+** (or use Gradle wrapper)

### Step 1: Build the Project

```bash
cd "Capgemini Final"
./gradlew build
```

This will:
- Download dependencies
- Compile source code
- Run tests
- Create JAR file

### Step 2: Run the Application

```bash
./gradlew bootRun
```

Or run the JAR directly:
```bash
java -jar build/libs/filesystem-calculator-1.0.0.jar
```

The application will start on `http://localhost:8080`

### Step 3: Verify Application is Running

Open a browser or use curl:
```bash
curl http://localhost:8080/api/filesystem/pwd
```

Expected response:
```json
{
  "success": true,
  "path": "/",
  "name": "root"
}
```

### Step 4: Test with Postman

1. **Import Collection**: 
   - Open Postman
   - Click "Import"
   - Select `Postman_Collection.json`

2. **Test Endpoints**:
   - Start with "Get Current Path (pwd)"
   - Try "List Directory (ls)"
   - Test "Change Directory" with different paths
   - Verify "Get Directory Size" shows recursive calculation
   - Test "Create Directory" and "Remove Directory"
   - Explore "Get Directory Tree" to see the complete structure

### Step 5: Run JUnit Tests

```bash
./gradlew test
```

This will run all unit and integration tests and display results.

### Step 6: Verify 3-Level Directory Structure

Use the tree endpoint to verify the structure:
```bash
curl http://localhost:8080/api/filesystem/tree
```

This should show:
- Root level: documents, projects, downloads
- Second level: work, personal, java-project, spring-project, images, videos
- Third level: reports, invoices, photos, screenshots
- Files at various levels with different sizes

## Test Data and Seed Data

### Initial File System Structure

The application initializes with the following structure:

```
root/ (25,088 bytes total)
├── documents/ (10,240 bytes)
│   ├── work/ (8,704 bytes)
│   │   ├── reports/ (5,120 bytes)
│   │   │   └── annual-report.pdf (5,120 bytes)
│   │   ├── invoices/ (1,536 bytes)
│   │   │   └── invoice-001.pdf (1,536 bytes)
│   │   ├── report1.pdf (2,048 bytes)
│   │   └── report2.pdf (3,072 bytes)
│   ├── personal/ (0 bytes)
│   └── readme.txt (1,024 bytes)
├── projects/ (3,584 bytes)
│   ├── java-project/ (1,280 bytes)
│   │   ├── Main.java (512 bytes)
│   │   └── Utils.java (768 bytes)
│   └── spring-project/ (2,304 bytes)
│       ├── Application.java (1,024 bytes)
│       └── Controller.java (1,280 bytes)
└── downloads/ (11,264 bytes)
    ├── images/ (9,728 bytes)
    │   ├── photos/ (7,680 bytes)
    │   │   ├── vacation.jpg (4,096 bytes)
    │   │   └── family.jpg (3,584 bytes)
    │   └── screenshots/ (2,048 bytes)
    │       └── screen1.png (2,048 bytes)
    └── videos/ (0 bytes)
```

### Testing Scenarios

1. **Basic Operations**:
   - List root directory
   - Change to documents
   - List documents directory
   - Calculate size of documents

2. **Recursive Size Calculation**:
   - Change to root
   - Get size (should be 25,088 bytes)
   - Change to documents/work
   - Get size (should be 8,704 bytes)

3. **Directory Management**:
   - Create new directory
   - Verify it appears in list
   - Remove directory
   - Verify it's gone

4. **Path Navigation**:
   - Use absolute paths: `/documents/work`
   - Use relative paths: `documents/work`
   - Use parent navigation: `..`
   - Use current directory: `.`

5. **Tree Structure**:
   - Get tree from root
   - Get tree from specific directory
   - Verify all levels are shown

6. **Error Handling**:
   - Try to change to non-existent directory
   - Try to create duplicate directory
   - Try to remove non-existent directory
   - Verify proper error messages

## OOP Concepts Demonstrated

### Inheritance
- `FileSystemEntity` (parent) → `Directory` and `File` (children)
- Children inherit common properties and methods

### Polymorphism
- `calculateSize()`: Different behavior for files vs directories
- `getTreeRepresentation()`: Different implementations
- Method overriding in subclasses

### Encapsulation
- Private fields with public getters/setters
- Service layer encapsulates business logic
- Model classes hide internal implementation

## Verification Checklist

- Application builds successfully
- Application starts without errors
- All API endpoints respond correctly
- Recursive size calculation works
- 3-level directory structure is initialized
- Exception handling works properly
- JUnit tests pass
- Postman collection works
- Tree endpoint shows complete structure
- All CRUD operations work (create, read, update, delete)

## Additional Notes

- The application uses Spring Boot 3.2.0 with Java 17
- All data is stored in memory (resets on restart)
- The file system supports unlimited depth (tested with 3+ levels)
- File sizes are in bytes, with KB and MB conversions provided
- CORS is enabled for all origins (can be restricted in production)

## Quick Reference

- **Base URL**: `http://localhost:8080`
- **API Base**: `/api/filesystem`
- **Port**: 8080 (configurable in `application.properties`)
- **Documentation**: See `README.md` for detailed API documentation

