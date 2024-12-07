package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Builds a multi-threaded inverted index from text files. This class processes
 * directories and individual files using a work queue to distribute tasks across multiple threads
 */
public class MultiThreadedInvertedIndexBuilder {
	/**
	 * Processes and indexes the contents of a file into a multi-threaded inverted index
	 */
	public static class FileTask implements Runnable {
		/**
		 * path The path to the directory or file to index
		 */
		private final Path file;

		/**
		 * index The multi-threaded inverted index to which the indexed words are added
		 */
		private final MultiThreadedInvertedIndex index;

		/**
		 * Constructs a new FileTask for processing a specific file.
		 *
		 * @param file the path to the file that will be processed by this task
		 * @param index the MultiThreadedInvertedIndex instance where the processed data will be added
		 */
		public FileTask(Path file, MultiThreadedInvertedIndex index) {
			this.file = file;
			this.index = index;
		}

		@Override
		public void run() {
			try {
				processFile(file, index);
			} catch (IOException e) {
				// TODO throw new UncheckedIOException(e);
				System.err.println("Error processing file: " + file);
			}
		}
	}


	/**
	 * Recursively builds an index from the specified path using the provided multi-threaded index.
	 * If the path is a directory, it will recursively index all files within that directory.
	 * If the path is a file, it will be indexed directly.
	 *
	 * @param path The path to the directory or file to index.
	 * @param index The multi-threaded inverted index to which the indexed words are added.
	 * @param workQueue The work queue used for managing concurrent tasks.
	 * @throws IOException If an I/O error occurs accessing the directory or files.
	 */
	public static void buildIndex(Path path, MultiThreadedInvertedIndex index, WorkQueue workQueue) throws IOException {
		if (Files.isDirectory(path)) {
			processDirectory(path, index, workQueue);
		} else if (InvertedIndexBuilder.isTextFile(path)) {
			workQueue.execute(new FileTask(path, index));
		}
		workQueue.finish();
	}

	/**
	 * Recursively processes directories to index all eligible files using a multi-threaded approach.
	 * Each directory entry is checked; if it's a directory, this method calls itself recursively.
	 *
	 * @param directory The directory path to process.
	 * @param index The multi-threaded inverted index to which the data is added.
	 * @param workQueue The work queue used for managing concurrent tasks.
	 * @throws IOException If an I/O error occurs while accessing the directory or its files.
	 */
	public static void processDirectory(Path directory, MultiThreadedInvertedIndex index, WorkQueue workQueue) throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					processDirectory(entry, index, workQueue);
				} else if (InvertedIndexBuilder.isTextFile(entry)) {
					workQueue.execute(new FileTask(entry, index));
				}
			}
		}
	}

	/**
	 * Processes a single file by indexing its content. It extracts words using a stemming process and adds them to the inverted index,
	 * along with the file's path and a starting position for each word.
	 *
	 * @param file The file to be processed.
	 * @param invertedIndex The inverted index to which the extracted words and their positions are added.
	 * @throws IOException If an I/O error occurs while reading from the file.
	 */
	public static void processFile(Path file, MultiThreadedInvertedIndex invertedIndex) throws IOException {
		InvertedIndex local = new InvertedIndex();
		InvertedIndexBuilder.processFile(file, local);
		invertedIndex.combine(local);
	}
}
