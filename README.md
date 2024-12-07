# Multithreaded Inverted Index

## Project Overview
This project implements a **multithreaded inverted index** data structure, designed to support efficient text processing and search operations. Although the front-end user interface was not implemented, the back-end features a robust, thread-safe architecture capable of handling concurrent file parsing, index construction, and query execution.

## Features
- **Inverted Index:** A data structure that maps terms to the documents where they appear, supporting efficient search operations.
- **Multithreading:** Utilizes a thread-safe `WorkQueue` to parallelize tasks such as:
  - Index construction from multiple files.
  - Query execution against the built index.
- **Extensibility:** Modular design allows for easy integration with a front-end or additional back-end features.
- **JSON Output:** Supports exporting the index and query results in JSON format for easy integration with other systems.

## Key Components
### Core Classes
1. **`InvertedIndex`**: Manages the mapping of terms to document locations.
2. **`MultiThreadedInvertedIndex`**: Extends `InvertedIndex` with thread-safe functionality.
3. **`WorkQueue`**: Manages a pool of worker threads to parallelize tasks.

### Utilities
- **`InvertedIndexBuilder` & `MultiThreadedInvertedIndexBuilder`**: Build the inverted index from a collection of files.
- **`QueryBuilder` & `MultiThreadedQueryBuilder`**: Execute queries on the index.
- **`JsonWriter`**: Exports data to JSON format.

### Other Support Classes
- **`ArgumentParser`**: Parses command-line arguments for easier execution.
- **`FileStemmer`**: Processes text files to extract and normalize terms.

## Setup and Usage
### Prerequisites
- Java Development Kit (JDK) 8 or higher.
- Apache Maven (for dependency management).

## Future Enhancements
- **Front-End Integration:** Develop a web-based or GUI front-end for user-friendly interaction.
- **Search Optimization:** Implement ranking algorithms like TF-IDF for more relevant results.
- **Expanded File Formats:** Add support for parsing and indexing additional file types (e.g., PDFs).

## License
This project is licensed under the [MIT License](LICENSE).

---

### Project Status
This project currently serves as a backend-only multithreaded search engine.

