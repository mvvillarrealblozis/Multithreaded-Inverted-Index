package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Represents an inverted index for indexing documents and their words.
 * An inverted index associates each word with a list of its occurrences in various documents.
 * This implementation uses a nested TreeMap structure to store
 * word occurrences and document word counts efficiently, allowing for fast retrieval and sorted order of words.
 */
public class InvertedIndex {
	/**
	 * Stores the inverted index. Each word is mapped to another TreeMap, which in turn maps document identifiers
	 * to a TreeSet of integers representing the positions of the word in that document.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;


	/**
	 * Stores the count of words in each document. Maps document identifiers to an integer count of words.
	 */
	private final TreeMap<String, Integer> counts;

	/**
	 * Constructs an empty InvertedIndex. Initializes the underlying data structures for storing the inverted index
	 * and word counts.
	 */
	public InvertedIndex() {
		index = new TreeMap<>();
		counts = new TreeMap<>();
	}

	/**
	 * Adds a word, its file location, and position within that file to the inverted index.
	 * If the word does not exist in the index, it creates a new entry for it.
	 * If the word exists but not in the specified file, it adds the file and position.
	 * If the word and file both exist, it just adds the position to the existing set.
	 *
	 * @param word the word to add to the index
	 * @param location the file in which the word was found
	 * @param position the position of the word within the file
	 */
	public void add(String word, String location, int position) {
		// Check if the inverted index map already has the word
		TreeMap<String, TreeSet<Integer>> locations = index.get(word);
		// If not, create a new TreeMap for the word and add it to the inverted index
		if (locations == null) {
			locations = new TreeMap<>();
			index.put(word, locations);
		}

		TreeSet<Integer> positions = locations.get(location);
		// If the location is not present, create a new TreeSet for positions
		if (positions == null) {
			positions = new TreeSet<>();
			locations.put(location, positions);
		}

		positions.add(position);

		Integer currentMax = counts.get(location);
		if (currentMax == null || position > currentMax) {
			counts.put(location, position);
		}
	}

	/**
	 * Adds all words with their positions to the inverted index for a given file.
	 *
	 * @param words an ArrayList of words to add to the index
	 * @param location the file in which the words were found
	 * @param position the starting position for the first word in the list
	 */
	public void addAll(ArrayList<String> words, String location, int position) {
		for (String word : words) {
			add(word, location, position++);
		}
	}


	/**
	 * Merges another inverted index into this index.
	 *
	 * @param other the inverted index to merge into this one
	 */
	public void combine(InvertedIndex other) {
		for (var otherEntry : other.index.entrySet()) {
			var word = otherEntry.getKey();
			var otherLocations = otherEntry.getValue();
			var thisLocations = this.index.get(word);

			if (thisLocations == null) {
				this.index.put(word, otherLocations);
			}
			else {
				for (var otherLocationEntry : otherLocations.entrySet()) {
					String location = otherLocationEntry.getKey();
					var otherPositions = otherLocationEntry.getValue();
					var thisPositions = thisLocations.get(location);

					if (thisPositions == null) {
						thisLocations.put(location, otherPositions);
					} else {
						thisPositions.addAll(otherPositions);
					}
				}
			}
		}
		for (var otherCountEntry : other.counts.entrySet()) {
			String location = otherCountEntry.getKey();
			Integer otherCount = otherCountEntry.getValue();
			Integer thisCount = this.counts.get(location);

			if (thisCount == null || otherCount > thisCount) {
				this.counts.put(location, otherCount);
			}
		}
	}


	/**
	 * Writes the inverted index to a JSON file.
	 * This method provides a controlled way to serialize the internal state without exposing it.
	 *
	 * @param path The path to the file where the index should be written.
	 * @throws IOException if theres in error with the input
	 */
	public void writeIndex(Path path) throws IOException{
		JsonWriter.writeIndex(index, path);
	}


	/**
	 * This method returns a TreeMap where each key is a file path and each value is the count of unique word stems in that file.
	 *
	 * @return the word count map
	 */
	public Map<String, Integer> getWordCount() {
		return Collections.unmodifiableMap(counts);
	}

	/**
	 * Returns a string representation of the inverted index.
	 *
	 * @return a string representation of the inverted index
	 */
	@Override
	public String toString() {
		return index.toString();
	}

	/**
	 * Returns the number of unique words in the inverted index.
	 *
	 * @return the number of unique words in the inverted index
	 */
	public int numWords() {
		return index.size();
	}

	/**
	 * Checks if the inverted index contains the specified word.
	 *
	 * @param word the word to check for
	 * @return true if the inverted index contains the word, false otherwise
	 */
	public boolean hasWord(String word) {
		return index.containsKey(word);
	}

	/**
	 * Returns all the words contained in the inverted index.
	 *
	 * @return an unmodifiable Set of all words in the inverted index.
	 */
	public Set<String> viewWords() {
		return Collections.unmodifiableSet(index.keySet());
	}

	/**
	 * Checks if the specified word in the inverted index has the specified location (file).
	 *
	 * @param word the word to check for
	 * @param location the location (file) to check for
	 * @return true if the word has the location, false otherwise
	 */
	public boolean hasLocation(String word, String location) {
		TreeMap<String, TreeSet<Integer>> locations = index.get(word);
		return locations != null && locations.containsKey(location);
	}

	/**
	 * Returns all locations (files) where the specified word appears.
	 *
	 * @param word the word to get locations for
	 * @return a Set of strings representing the locations (files) where the word is found
	 */
	public Set<String> viewLocations(String word){
		TreeMap<String, TreeSet<Integer>> locations = index.get(word);
		if (locations == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(locations.keySet());
	}

	/**
	 * Checks if the specified word in the inverted index has the specified location (file) and position.
	 *
	 * @param word the word to check for
	 * @param location the location (file) to check for
	 * @param position the position to check for
	 * @return true if the word has the location and position, false otherwise
	 */
	public boolean hasPosition(String word, String location, int position) {
		TreeMap<String, TreeSet<Integer>> locations = index.get(word);
		if (locations != null) {
			TreeSet<Integer> positions = locations.get(location);
			return positions != null && positions.contains(position);
		}
		return false;
	}

	/**
	 * Returns all positions of the specified word in the specified location (file).
	 *
	 * @param word the word to get positions for
	 * @param location the location (file) to get positions in
	 * @return a TreeSet of integers representing the positions of the word in the specified location
	 */
	public Set<Integer> viewPositions(String word, String location) {
		TreeMap<String, TreeSet<Integer>> locations = index.get(word);
		if (locations != null) {
			TreeSet<Integer> positions = locations.get(location);
			if (positions != null) {
				return Collections.unmodifiableSet(positions);
			}
		}
		return Collections.emptySet();
	}

	/**
	 * Checks if the word count map contains the specified location (file).
	 *
	 * @param location the location (file) to check for
	 * @return true if the word count map contains the location, false otherwise
	 */
	public boolean hasCount(String location) {
		return counts.containsKey(location);
	}

	/**
	 * Returns a map of all locations (files) and their respective counts of unique word stems.
	 *
	 * @return a map of all locations and their counts of unique word stems
	 */
	public Map<String, Integer> viewCount() {
		return Collections.unmodifiableMap(counts);
	}

	/**
	 * Returns the count of unique word stems in the specified location (file) from the word count map.
	 * If the location is not found in the word count map, it returns 0.
	 *
	 * @param location the location (file) to get the count for
	 * @return the count of unique word stems in the specified location, or 0 if the location is not found
	 */
	public int getCount(String location) {
		return counts.getOrDefault(location, 0);
	}

	/**
	 * Represents a search result with a file's location, term count, total words, and relevance score.
	 * Implements {@link Comparable} for sorting by score, count, and location.
	 */
	public class SearchResult implements Comparable<SearchResult> {
		/**
		 * The number of occurrences of the search query within the file.
		 */
		private int count;

		/**
		 * The path or identifier for the file where the search query was found.
		 */
		private final String location;

		/**
		 * The calculated relevance score of the file based on the search query.
		 */
		private double score;

		/**
		 * Constructs a new {@link SearchResult} instance with specified parameters.
		 * Initializes the location, count, and total words for a search result,
		 * and calculates the score as the ratio of count to total words.
		 *
		 * @param location the path or identifier for the file where the search query was found
		 */
		public SearchResult(String location) {
			this.location = location;
			this.count = 0;
			this.score = 0;
		}

		@Override
		public String toString() {
			return String.format("Location: %s, Count: %d, Score: %.8f", this.location, this.count, this.score);
		}

		@Override
		public int compareTo(SearchResult o) {
			int scoreRes = Double.compare(o.score, this.score);
			if (scoreRes != 0) {
				return scoreRes;
			}

			int countRes = Integer.compare(o.count, this.count);
			if (countRes != 0) {
				return countRes;
			}

			return this.location.compareToIgnoreCase(o.location);
		}

		/**
		 * Increments the count of occurrences by a specified amount and updates the score accordingly.
		 * The score is recalculated as the ratio of the updated count to the total number of words.
		 *
		 * @param num the amount to increment the count of occurrences
		 */
		private void incrementCount(int num) {
			this.count += num;
			calculateScore();
		}

		/**
		 * Void method that calculates the score of the given file based off the count and total words of the file.
		 */
		private void calculateScore() {
			this.score = this.count / (double) InvertedIndex.this.getCount(this.location);
		}

		/**
		 * Returns the location associated with this search result.
		 * The location typically represents the file path of the file where the search words were found.
		 *
		 * @return the location of the file as a {@link String}
		 */
		public String getLocation() {
			return this.location;
		}

		/**
		 * Returns the total count of occurrences of the search words found in the file associated with this search result.
		 *
		 * @return the count of occurrences as an integer
		 */
		public int getCount() {
			return this.count;
		}

		/**
		 * Returns the score of the file associated with this search result.
		 * The score is calculated based on the total occurrences of the search words in relation to the total number of words in the file.
		 *
		 * @return the score as a double
		 */
		public double getScore() {
			return this.score;
		}
	}

	/**
	 * Processes search locations and updates results and sorted results lists.
	 * For each location, if it doesn't already exist in results, a new SearchResult is created.
	 * Each SearchResult's word count is incremented by the number of words at that location.
	 *
	 * @param results A map of location identifiers to SearchResult objects
	 * @param sortedResults A list of SearchResult objects, sorted by the order they are processed.
	 * @param locations A map of location identifiers to sets of word counts
	 */
	private void searchHelper(Map<String, SearchResult> results, List<SearchResult> sortedResults, TreeMap<String, TreeSet<Integer>> locations) {
		for (var entry : locations.entrySet()) {
			String location = entry.getKey();
			int wordCount = entry.getValue().size();

			var searchResult = results.get(location);

			if (searchResult == null) {
				searchResult = new SearchResult(location);
				results.put(location, searchResult);
				sortedResults.add(searchResult);
			}

			searchResult.incrementCount(wordCount);
		}
	}

	/**
	 * Generates a list of exact search results for a given list of query words using an inverted index.
	 * For each query word, it finds matching files and calculates the total number of occurrences
	 * of the query words in each file, as well as the score for each file based on these occurrences.
	 *
	 * @param queryWords the list of words to query in the inverted index
	 * @return a sorted list of {@link SearchResult} objects representing the search results
	 */
	public List<SearchResult> exactSearch(Set<String> queryWords) {
		HashMap<String, SearchResult> results = new HashMap<>();
		List<SearchResult> sortedResults = new ArrayList<>();

		for (String word : queryWords) {
			var locations = index.get(word);

			if (locations != null) {
				searchHelper(results, sortedResults, locations);
			}
		}

		Collections.sort(sortedResults);
		return sortedResults;
	}

	/**
	 * Generates a list of partial search results for a given list of query words using an inverted index.
	 * For each query word, it finds matching files and calculates the total number of occurrences
	 * of the query words in each file, as well as the score for each file based on these occurrences.
	 *
	 * @param queryWords the list of words to query in the inverted index
	 * @return a sorted list of {@link SearchResult} objects representing the search results
	 */
	public List<SearchResult> partialSearch(Set<String> queryWords) {
		HashMap<String, SearchResult> results = new HashMap<>();
		List<SearchResult> sortedResults = new ArrayList<>();

		for (String prefix : queryWords) {
			Map<String, TreeMap<String, TreeSet<Integer>>> subMap = index.tailMap(prefix, true);
			for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> wordEntry : subMap.entrySet()) {
				String word = wordEntry.getKey();
				if (!word.startsWith(prefix)) {
					break;
				}
				TreeMap<String, TreeSet<Integer>> locations = wordEntry.getValue();
				searchHelper(results, sortedResults, locations);
			}
		}

		Collections.sort(sortedResults);
		return sortedResults;
	}

	/**
	 * Performs a search for the given query words using either exact or partial search criteria.
	 *
	 * @param queryWords A set of words to search for
	 * @param partialSearch A boolean flag indicating whether to perform a partial search (true) or an exact search (false)
	 * @return A list of SearchResult objects that match the search criteria
	 */
	public List<SearchResult> search(Set<String> queryWords, boolean partialSearch) {
		return partialSearch ? partialSearch(queryWords) : exactSearch(queryWords);
	}
}