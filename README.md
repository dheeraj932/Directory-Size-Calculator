# Directory Size Calculator Application

A Spring Boot application that simulates a file system with hierarchical directory structure. The application supports various file system operations through REST API endpoints.

## Features

- **Change Directory (cd)**: Navigate through the file system
- **List Directory (ls)**: View contents of current directory
- **Calculate Size**: Recursively calculate directory size
- **Make Directory (mkdir)**: Create new directories
- **Remove Directory (rmdir)**: Delete directories
- **Print Working Directory (pwd)**: Get current directory path
- **Directory Tree**: Get complete directory tree structure

## Architecture & Design

### Object-Oriented Design

The application demonstrates OOP principles:

1. **Inheritance**: 
   - `FileSystemEntity` (abstract base class)
   - `Directory` and `File` extend `FileSystemEntity`

2. **Polymorphism**:
   - `calculateSize()` method behaves differently for files vs directories
   - `getTreeRepresentation()` provides different implementations
   - Method overriding for size calculation (recursive for directories, direct for files)

3. **Encapsulation**:
   - Private fields with public getters/setters
   - Service layer encapsulates business logic

### Project Structure

```
src/
├── main/
│   ├── java/com/capgemini/filesystem/
│   │   ├── model/
│   │   │   ├── FileSystemEntity.java    # Abstract base class
│   │   │   ├── Directory.java           # Directory implementation
│   │   │   └── File.java                # File implementation
│   │   ├── service/
│   │   │   ├── FileSystemService.java   # Business logic
│   │   │   └── FileSystemManager.java   # File system state management
│   │   ├── controller/
│   │   │   └── FileSystemController.java # REST API endpoints
│   │   ├── exception/
│   │   │   ├── DirectoryNotFoundException.java
│   │   │   ├── InvalidPathException.java
│   │   │   ├── DirectoryAlreadyExistsException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   └── FileSystemApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/capgemini/filesystem/
        ├── service/
        │   └── FileSystemServiceTest.java
        └── controller/
            └── FileSystemControllerTest.java
```

## Getting Started

### Prerequisites

- Java 17
- Gradle 8.5

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd "Capgemini Final"
```

2. Build the project:
```bash
./gradlew build
```

3. Run the application:
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## API Endpoints

### 1. Change Directory
**POST** `/api/filesystem/cd`

Request Body:
```json
{
  "path": "documents"
}
```

Response:
```json
{
  "success": true,
  "message": "Directory changed successfully",
  "currentPath": "/documents",
  "directoryName": "documents"
}
```

### 2. List Directory
**GET** `/api/filesystem/ls`

Response:
```json
{
  "success": true,
  "currentPath": "/",
  "directories": [
    {
      "name": "documents",
      "type": "DIRECTORY",
      "size": 10240,
      "path": "/documents"
    }
  ],
  "files": [],
  "totalItems": 1
}
```

### 3. Get Directory Size
**GET** `/api/filesystem/size`

Response:
```json
{
  "success": true,
  "path": "/",
  "size": 25088,
  "sizeInKB": 24.5,
  "sizeInMB": 0.02392578125
}
```

### 4. Create Directory
**POST** `/api/filesystem/mkdir`

Request Body:
```json
{
  "name": "new-directory"
}
```

Response:
```json
{
  "success": true,
  "message": "Directory created successfully",
  "directoryName": "new-directory",
  "path": "/new-directory"
}
```

### 5. Remove Directory
**DELETE** `/api/filesystem/rmdir?name=directory-name`

Response:
```json
{
  "success": true,
  "message": "Directory removed successfully",
  "removedDirectory": "directory-name"
}
```

### 6. Get Current Path
**GET** `/api/filesystem/pwd`

Response:
```json
{
  "success": true,
  "path": "/documents/work",
  "name": "work"
}
```

### 7. Get Directory Tree
**GET** `/api/filesystem/tree?path=/documents`

Response:
```json
{
  "success": true,
  "tree": {
    "name": "documents",
    "type": "DIRECTORY",
    "size": 10240,
    "children": [
      {
        "name": "work",
        "type": "DIRECTORY",
        "size": 8704,
        "children": [...]
      }
    ]
  }
}
```

## Testing

### Run JUnit Tests

```bash
./gradlew test
```

### Test Coverage

The project includes comprehensive JUnit tests for:
- Service layer operations
- Controller endpoints
- Exception handling
- Edge cases

### Postman Testing

Import the `Postman_Collection.json` file into Postman to test all endpoints.

## Seed Data

The application initializes with a 3-level directory structure:

```
root/
├── documents/
│   ├── work/
│   │   ├── reports/
│   │   │   └── annual-report.pdf (5120 bytes)
│   │   ├── invoices/
│   │   │   └── invoice-001.pdf (1536 bytes)
│   │   ├── report1.pdf (2048 bytes)
│   │   └── report2.pdf (3072 bytes)
│   ├── personal/
│   └── readme.txt (1024 bytes)
├── projects/
│   ├── java-project/
│   │   ├── Main.java (512 bytes)
│   │   └── Utils.java (768 bytes)
│   └── spring-project/
│       ├── Application.java (1024 bytes)
│       └── Controller.java (1280 bytes)
└── downloads/
    ├── images/
    │   ├── photos/
    │   │   ├── vacation.jpg (4096 bytes)
    │   │   └── family.jpg (3584 bytes)
    │   └── screenshots/
    │       └── screen1.png (2048 bytes)
    └── videos/
```

## Exception Handling

The application includes comprehensive exception handling:

- `DirectoryNotFoundException`: When a directory doesn't exist
- `InvalidPathException`: When an invalid path is provided
- `DirectoryAlreadyExistsException`: When trying to create a duplicate directory
- `IllegalArgumentException`: For invalid arguments

All exceptions return appropriate HTTP status codes and error messages.

## Key Implementation Details

### Recursive Size Calculation

The `calculateSize()` method in `Directory` class uses recursion to calculate the total size:

```java
@Override
public long calculateSize() {
    return children.stream()
            .mapToLong(FileSystemEntity::calculateSize)
            .sum();
}
```

### Polymorphism in Action

- **Files**: Return their fixed size directly
- **Directories**: Recursively sum sizes of all children

### Path Resolution

The service supports both absolute (`/documents/work`) and relative (`documents/work`, `..`, `.`) paths.

## Notes

- All data is stored in memory (no database required)
- The file system persists only during application runtime
- Maximum directory depth supported: Unlimited (tested with 3+ levels)
- File sizes are in bytes

## Author

Capgemini Final Project

## License

This project is for educational purposes.

