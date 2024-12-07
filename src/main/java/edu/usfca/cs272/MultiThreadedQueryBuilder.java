package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Processes search queries and generates search results based on an inverted index with multiple threads
 */
public class MultiThreadedQueryBuilder implements QueryInterface{
	/**
	 * Task for processing a single query string using a multi-threaded approach.
	 */
	public class QueryTask implements Runnable {
		/**
		 * Query to process
		 */
		private final String query;
		/**
		 * Query builder used to process queries
		 */
		private final MultiThreadedQueryBuilder builder; // TODO Remove

		/**
		 * Constructs a new QueryTask for processing a specific query.
		 *
		 * @param query the search query string
		 * @param builder the instance of MultiThreadedQueryBuilder this task should use to process queries
		 */
		public QueryTask(String query, MultiThreadedQueryBuilder builder) {
			this.query = query;
			this.builder = builder;
		}

		@Override
		public void run() {
			builder.processQuery(query);
		}
	}

	/**
	 * Logger to use
	 */
	private static final Logger log = LogManager.getLogger(); // TODO Remove or make public

	/**
	 * The work queue to use for managing concurrent tasks
	 */
	private final WorkQueue workQueue;

	/**
	 * A map of search query strings to lists of search results, each key represents a unique search query
	 * that has been processed, and its corresponding value is a list of search results sorted by relevance.
	 */
	private final TreeMap<String, List<InvertedIndex.SearchResult>> results;

	/**
	 * The inverted index used for searching documents.
	 */
	private final MultiThreadedInvertedIndex index;

	/**
	 * Indicates whether the search should be conducted as a partial text match or an exact text match.
	 * When set to true, the search will include partial matches for the query terms.
	 */
	private final boolean partialSearch;

	/**
	 * Constructs a new QueryBuilder instance configured to use a specific inverted index and search type.
	 *
	 * @param index the inverted index to use for generating search results
	 * @param partialSearch true to enable partial match searches, false for exact match searches
	 * @param workQueue the work queue to use for managing concurrent tasks
	 */
	public MultiThreadedQueryBuilder(MultiThreadedInvertedIndex index, boolean partialSearch, WorkQueue workQueue) {
		this.results = new TreeMap<>();
		this.index = index;
		this.partialSearch = partialSearch;
		this.workQueue = workQueue;
	}

	/**
	 * Returns the map of search results.
	 *
	 * @return the map containing the results of processed search queries
	 */
	public Set<String> getQueries() {
		// TODO synchronized (results) {
		return Collections.unmodifiableSet(results.keySet());
	}

	/**
	 * Retrieves an immutable list of search results for a given query.
	 *
	 * @param query the search query string
	 * @return an immutable list of search results corresponding to the query, or null if the query is not found
	 */
	public List<InvertedIndex.SearchResult> getResults(String query) {
		// TODO Re-stem and join here too (do not reuse a stemmer object)
		// TODO synchronized (results) {
		List<MultiThreadedInvertedIndex.SearchResult> resultsList = results.get(query);
		if (resultsList != null) {
			return Collections.unmodifiableList(resultsList);
		}
		return Collections.emptyList();
	}

	/**
	 * Returns a string representation of the query builder
	 *
	 * @return a string representation of the query builder
	 */
	@Override
	public String toString() {
		return index.toString();
	}

	/**
	 * Writes the search results to a specified file.
	 *
	 * @param path The path of the file where search results will be written
	 * @throws IOException If an error occurs during writing to the file
	 */
	public void writeResults(Path path) throws IOException{
		synchronized (results) {
			JsonWriter.writeSearchResults(results, path);
		}
	}

	/**
	 * Processes a single query line and updates the results map.
	 *
	 * @param line The query line to process
	 */
	public void processQuery(String line) {
		/* TODO 
		workQueue.execute(new QueryTask(line));
		
		move the code below into the run method of the task...
		*/
		TreeSet<String> queryWords = FileStemmer.uniqueStems(line);
		String key = String.join(" ", queryWords);

		synchronized (results) {
			if (queryWords.isEmpty() || results.containsKey(key)) {
				return;
			}
		}

		List<MultiThreadedInvertedIndex.SearchResult> searchResults = index.search(queryWords, partialSearch);

		synchronized (results) {
			this.results.put(key, searchResults);
		}
	}

	/**
	 * Processes the queries from a file and generates search results for each query
	 * based on the provided inverted index. Each line in the query file is treated
	 * as a separate query.
	 *
	 * @param queryFile the file containing the queries
	 * @throws IOException if an IO error occurs reading from the query file
	 */
	public void processQueries(Path queryFile) throws IOException {
		try (var lines = Files.newBufferedReader(queryFile)) {
			String line;
			while ((line = lines.readLine()) != null) {
				workQueue.execute(new QueryTask(line, this));
			}
		}
		
		// TODO Do this instead: QueryInterface.super.processQueries(queryFile);
		workQueue.finish();
	}
}
