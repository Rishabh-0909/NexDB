# NexDB

**A lightweight embedded database engine written in Java.**

NexDB is an experimental embedded database engine developed to explore the internal workings of database systems. It focuses on fundamental database concepts such as indexing, disk-based storage, serialization, caching, concurrency control, and query execution.

Rather than serving as a replacement for production database systems, NexDB is a learning-oriented project aimed at understanding how database engines manage, organize, retrieve, and modify data internally.

## Overview

NexDB provides a collection-based data storage model that allows Java applications to store and retrieve structured objects.

The engine explores several core components of database architecture:

- **Indexing:** B+ Trees, bitmap indexes, and non-unique indexes.
- **Storage management:** Page-based disk storage, page buffers, and index storage.
- **Caching:** LRU-based caching for index managers.
- **Serialization:** Conversion of Java objects into a format suitable for persistent storage.
- **Concurrency:** Reader-writer locking and concurrent collection operations.
- **Query execution:** Index-based querying and comparison operations.
- **Database operations:** Insertion, selection, updating, and deletion.

## Core Features

### 1. Indexing Engine

NexDB implements multiple indexing mechanisms to explore efficient data retrieval.

- B+ Tree-based unique indexes.
- Cluster indexing using B+ Trees.
- Non-unique indexes using B+ Trees and array-based structures.
- Bitmap-based indexing.
- LRU caching for cluster index managers.
- Support for query operations on indexed fields.

### 2. Disk-Based Storage

The storage layer explores how database records and indexes can be persisted beyond application memory.

- Page-based disk storage.
- Page buffer management.
- Database storage and index storage abstractions.
- Compact and organized file-based index storage implementations.
- Removed-object tracking.
- Asynchronous file operations.

### 3. Serialization

The serialization layer converts Java data into a representation suitable for database storage.

- Primitive-type serializers for supported data types.
- Model-to-schema conversion.
- Object serialization and deserialization.
- Nullable-field support.
- Schema-based collection and field representation.

### 4. Caching

NexDB uses caching mechanisms to reduce repeated storage access.

- LRU cache implementation for unique index managers.
- Cache support for cluster index managers.
- Decorator-based index management.

### 5. Concurrency and Database Operations

The engine incorporates mechanisms for managing concurrent access and modifying stored data.

- Reader-writer locking at collection level.
- Insert operations.
- Select operations.
- Update operations.
- Delete operations.
- Graceful shutdown mechanism.

### 6. Querying

The query layer provides index-oriented lookup operations, including:

- Less than (`LT`)
- Less than or equal (`LTE`)
- Greater than (`GT`)
- Greater than or equal (`GTE`)
- Equal (`EQ`)

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Core implementation |
| Maven | Build and dependency management |
| JUnit 5 | Unit and integration testing |
| Gson | JSON serialization |
| Guava | Utility and data structure support |
| SLF4J | Logging abstraction |

## Project Architecture

The project is organized into logical layers responsible for different database-engine components.

| Component | Responsibility |
|---|---|
| `storage` | Disk storage, page management, and file operations |
| `index` | B+ Tree, bitmap, and index management |
| `scheme` | Database schema and collection definitions |
| `serialization` | Object serialization and deserialization |
| `operation` | Insert, select, update, and delete operations |
| `cache` | Caching and cache decorators |
| `query` | Query interfaces and execution logic |
| `exception` | Internal exception handling |

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 21 or later.
- Apache Maven 3.x.
- Git.

### Clone the repository

```bash
git clone <YOUR_NEXDB_REPOSITORY_URL>
cd nexDB
```

### Build the project

```bash
mvn clean install
```

### Run the tests

```bash
mvn test
```

## Development Roadmap

NexDB is an evolving project. The following areas represent ongoing development and potential improvements.

- [ ] Complete default cluster-index integration across collections.
- [ ] Improve schema and field-index manager coordination.
- [ ] Implement asynchronous write queues for disk operations.
- [ ] Improve bitmap storage efficiency and support for larger identifiers.
- [ ] Extend caching to additional index types.
- [ ] Improve transaction and index I/O session handling.
- [ ] Expand logging with configurable information and debug levels.
- [ ] Improve query execution for non-indexed fields.
- [ ] Optimize storage operations and reduce unnecessary disk access.
- [ ] Expand automated testing, particularly for concurrent operations.

## Current Limitations

NexDB is an experimental database engine and is not intended for production use.

Some areas still require further development:

- Queries on non-indexed fields may require loading objects into memory.
- Bitmap indexing currently has limitations on the maximum supported identifier.
- Transaction support is not yet fully implemented.
- Performance and concurrency behavior require further validation.

