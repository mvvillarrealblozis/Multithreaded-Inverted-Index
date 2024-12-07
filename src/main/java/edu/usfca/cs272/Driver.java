package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Maximiliano Villarreal-Blozis
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class Driver {
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		ArgumentParser parser = new ArgumentParser(args);
		Boolean partialSearch = parser.hasFlag("-partial");
		InvertedIndex invertedIndex ;
		WorkQueue workQueue = null;
		MultiThreadedInvertedIndex threadSafe = null;
		QueryInterface queryBuilder = null; 


		if (parser.hasFlag("-threads")) {
			int numThreads = parser.getInteger("-threads", WorkQueue.DEFAULT);
			if (numThreads < 1) {
				numThreads = WorkQueue.DEFAULT;
			}
			workQueue = new WorkQueue(numThreads);
			threadSafe = new MultiThreadedInvertedIndex();
			invertedIndex = threadSafe;
			queryBuilder = new MultiThreadedQueryBuilder(threadSafe, partialSearch, workQueue);
		} else {
			invertedIndex = new InvertedIndex();
			queryBuilder = new QueryBuilder(invertedIndex, partialSearch);
		}
		
		if (parser.hasFlag("-text")) {
			Path input = parser.getPath("-text");

			if (input != null) {
				try {
					if (workQueue != null && threadSafe != null) {
						MultiThreadedInvertedIndexBuilder.buildIndex(input, threadSafe, workQueue);
					} else {
						InvertedIndexBuilder.buildIndex(input, invertedIndex);
					}
				} catch (IOException e) {
					System.err.println("Error processing input path: " + input);
				}
			} else {
				System.err.println("Warning: input text is null");
				return;
			}
		}

		if (parser.hasFlag("-query")) {
			Path queryFile = parser.getPath("-query");
			if (queryFile != null && Files.isRegularFile(queryFile)) {
				try {
					queryBuilder.processQueries(queryFile);
				} catch (IOException e) {
					System.err.println("Error processing query file: " + queryFile);
				}
			} else {
				System.err.println("Query file is not readable or does not exist: " + queryFile);
			}
		}

		if (workQueue != null) {
			workQueue.shutdown();
		}

		if (parser.hasFlag("-counts")) {
			Path output = parser.getPath("-counts", Path.of("counts.json"));
			try {
				JsonWriter.writeObject(invertedIndex.getWordCount(), output);
			}
			catch (Exception e) {
				System.out.println("Unable to write the counts to JSON file at: " + output);
			}
		}

		if (parser.hasFlag("-index")) {
			Path indexPath = parser.getPath("-index", Path.of("index.json"));
			try {
				invertedIndex.writeIndex(indexPath);
			} catch (IOException e) {
				System.out.println("Error writing words to JSON file with flag -index.");
			}
		}

		if (parser.hasFlag("-results")) {
			Path resultsPath = parser.getPath("-results", Path.of("results.json"));
			try {
				queryBuilder.writeResults(resultsPath);
			} catch (IOException e) {
				System.err.println("Error writing search results to JSON file: " + resultsPath);
			}
		}
	}
}