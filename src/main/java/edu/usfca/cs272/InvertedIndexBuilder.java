package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class for building an inverted index from text files.
 * This class provides methods for processing individual files and directories
 * to extract words and their positions within those files, adding them to an inverted index.
 */
public class InvertedIndexBuilder {
	/**
	 * Builds an index from the specified path. This method determines whether the path is a file or a directory.
	 * If the path is a file, it processes the file directly. If the path is a directory, it recursively processes all files within it.
	 *
	 * @param path The path to the file or directory to be indexed.
	 * @param invertedIndex The inverted index object to which the extracted data will be added.
	 * @throws IOException If an I/O error occurs while reading from the file or directory.
	 */
	public static void buildIndex(Path path, InvertedIndex invertedIndex) throws IOException {
		if (Files.isRegularFile(path)) {
			processFile(path, invertedIndex);
		} else {
			processDirectory(path, invertedIndex);
		}
	}

	/**
	 * Recursively processes directories to index all eligible files. For each entry in the directory,
	 * this method checks if it is a directory itself and recursively processes it. If it is a file,
	 * and it meets the criteria determined by {@code isTextFile}, it is then processed.
	 *
	 * @param directory The directory path to process.
	 * @param invertedIndex The inverted index to which the data is added.
	 * @throws IOException If an I/O error occurs while accessing the directory or its files.
	 */
	public static void processDirectory(Path directory, InvertedIndex invertedIndex) throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					processDirectory(entry, invertedIndex);
				} else if (isTextFile(entry)) {
					processFile(entry, invertedIndex);
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
	public static void processFile(Path file, InvertedIndex invertedIndex) throws IOException {
		Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		int position = 1;
		String filePath = file.toString();
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parsedWords = FileStemmer.parse(line);
				for (String word : parsedWords) {
					String stemmed = stemmer.stem(word).toString();
					invertedIndex.add(stemmed, filePath, position++);
				}
			}
		}
	}

	/**
	 * Determines if the specified path points to a text file based on its file extension.
	 * Currently, only files with ".text" or ".txt" extensions are considered as text files.
	 *
	 * @param path The file path to check.
	 * @return {@code true} if the file is a text file; {@code false} otherwise.
	 */
	public static boolean isTextFile(Path path) {
		String pathString = path.toString().toLowerCase();
		return pathString.endsWith(".text") || pathString.endsWith(".txt");
	}

}