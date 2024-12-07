package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Interface for query processing
 */
public interface QueryInterface {

	/**
	 * Processes a single query string and updates the search results.
	 * @param line The query string to process
	 */
	void processQuery(String line);

	/**
	 * Processes multiple queries from a file.
	 * @param queryFile the file containing the queries
	 * @throws IOException if an error occurs while reading the file
	 */
	void processQueries(Path queryFile) throws IOException;

	/**
	 * Retrieves an immutable list of search results for a given query.
	 * @param query the query string
	 * @return a list of search results, or an empty list if the query is not found
	 */
	List<InvertedIndex.SearchResult> getResults(String query);

	/**
	 * Returns the set of queries that have been processed.
	 * @return a set of query strings
	 */
	Set<String> getQueries();

	/**
	 * Writes the search results to a specified file.
	 * @param path the path of the file where the search results will be written
	 * @throws IOException if an error occurs during writing to the file
	 */
	void writeResults(Path path) throws IOException;

	/**
	 * Returns a string representation of the query builder.
	 * @return a string representation of the query builder.
	 */
	@Override
	String toString();
}
