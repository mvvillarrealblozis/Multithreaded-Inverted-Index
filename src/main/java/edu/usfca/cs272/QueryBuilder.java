package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;


// TODO Use the @Override annotation

/**
 * Processes search queries and generates search results based on an inverted index.
 */
public class QueryBuilder implements QueryInterface{

	/**
	 * A map of search query strings to lists of search results, each key represents a unique search query
	 * that has been processed, and its corresponding value is a list of search results sorted by relevance.
	 */
	private final TreeMap<String, List<InvertedIndex.SearchResult>> results;

	/**
	 * The stemmer used to process query strings into their base or root forms.
	 */
	private final Stemmer stemmer;

	/**
	 * The inverted index used for searching documents.
	 */
	private final InvertedIndex index;

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
	 */
	public QueryBuilder(InvertedIndex index, boolean partialSearch) {
		this.results = new TreeMap<>();
		this.stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
		this.index = index;
		this.partialSearch = partialSearch;
	}

	/**
	 * Returns the map of search results.
	 *
	 * @return the map containing the results of processed search queries
	 */
	public Set<String> getQueries() {
		return Collections.unmodifiableSet(results.keySet());
	}

	/**
	 * Retrieves an immutable list of search results for a given query.
	 *
	 * @param query the search query string
	 * @return an immutable list of search results corresponding to the query, or null if the query is not found
	 */
	public List<InvertedIndex.SearchResult> getResults(String query) {
		// TODO re-stem and join before doing the get
		List<InvertedIndex.SearchResult> resultsList = results.get(query);
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
		JsonWriter.writeSearchResults(results, path);
	}

	/**
	 * Processes a single query line and updates the results map.
	 *
	 * @param line The query line to process
	 */
	public void processQuery(String line) {
		TreeSet<String> queryWords = FileStemmer.uniqueStems(line, stemmer);
		String key = String.join(" ", queryWords);

		if (!queryWords.isEmpty() && !results.containsKey(key)) {
			List<InvertedIndex.SearchResult> searchResults = index.search(queryWords, partialSearch);
			this.results.put(key, searchResults);
		}
	}

	// TODO Move this into the interface as a default implementation instead
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
				processQuery(line);
			}
		}
	}
}