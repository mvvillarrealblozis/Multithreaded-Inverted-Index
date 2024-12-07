package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using spaces.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class JsonWriter {
	/**
	 * Formatter used to write search results to JSON file
	 */
	private static DecimalFormat FORMATTER = new DecimalFormat("0.00000000");

	/**
	 * Indents the writer by the specified number of times. Does nothing if the
	 * indentation level is 0 or less.
	 *
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(Writer writer, int indent) throws IOException {
		while (indent-- > 0) {
			writer.write("  ");
		}
	}

	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeQuote(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Writer writer, int indent) throws IOException {
		var iterator = elements.iterator();

		writer.write("[");
		if (iterator.hasNext()) {
			writer.write("\n");
			Number firstElement = iterator.next();
			writeIndent(writer, indent + 1);
			writer.write(firstElement.toString());
		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			Number element = iterator.next();
			writeIndent(writer, indent + 1);
			writer.write(element.toString());
		}

		writer.write("\n");
		writeIndent(writer, indent);
		writer.write("]");
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static String writeArray(Collection<? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes a key-value pair to a writer, formatting it as JSON. This method is intended to help in constructing JSON objects
	 * by writing a string key and a number value, followed by formatting for readability.
	 *
	 * @param key the key of the JSON object to write
	 * @param val the value associated with the key
	 * @param writer the writer to which the JSON object is written
	 * @param indent the indentation level for the current line (not used in this simplified version, but included for potential future enhancements)
	 * @throws IOException if an I/O error occurs during writing
	 */
	public static void writeObjectHelper(String key, Number val, Writer writer, int indent) throws IOException {
		writer.write("\"");
		writer.write(key);
		writer.write("\": ");
		writer.write(String.valueOf(val));
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	// Map.Entry and .entrySet() borrowed from Sophie's lecture code
	public static void writeObject(Map<String, ? extends Number> elements, Writer writer, int indent) throws IOException {
		var iterator = elements.entrySet().iterator();

		writer.write("{");
		if (iterator.hasNext()) {
			writer.write("\n");
			Map.Entry<String, ? extends Number> firstEntry = iterator.next();
			writeIndent(writer, indent + 1);
			writeObjectHelper(firstEntry.getKey(), firstEntry.getValue(), writer, indent);
		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			Map.Entry<String, ? extends Number> entry = iterator.next();
			writeIndent(writer, indent + 1);
			writeObjectHelper(entry.getKey(), entry.getValue(), writer, indent);
		}

		writer.write("\n");
		writeIndent(writer, indent);
		writer.write("}");
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObject(Map, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObject(Map, Writer, int)
	 */
	public static String writeObject(Map<String, ? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}


	/**
	 * Prepares a writer to begin writing a JSON array by writing the key and the start of the array structure.
	 * This helper method is designed to start the process of writing an array value associated with a specific key
	 * in a JSON object. It writes the key followed by a colon (:) to indicate the start of the array value in JSON format.
	 *
	 * @param key the key under which the array will be written in the JSON object
	 * @param writer the writer object used to write the JSON structure
	 * @throws IOException if an I/O error occurs during writing
	 */
	public static void writeObjectArraysHelper(String key, 	Writer writer) throws IOException {
		writer.write("\"");
		writer.write(key);
		writer.write("\": ");
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of number objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeArray(Collection)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Writer writer, int indent) throws IOException {
		writer.write("{");
		var iterator = elements.entrySet().iterator();

		if (iterator.hasNext()) {
			writer.write("\n");
			var firstEntry = iterator.next();
			writeIndent(writer, indent + 1);
			writeObjectArraysHelper(firstEntry.getKey(), writer);
			writeArray(firstEntry.getValue(), writer, indent + 1);
		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			var entry = iterator.next();
			writeIndent(writer, indent + 1);
			writeObjectArraysHelper(entry.getKey(), writer);
			writeArray(entry.getValue(), writer, indent + 1);
		}

		writer.write("\n"); // Ensure the closing bracket is on a new line
		writeIndent(writer, indent);
		writer.write("}");
	}


	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObjectArrays(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static String writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObjectArrays(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects. The generic
	 * notation used allows this method to be used for any type of collection with
	 * any type of nested map of String keys to number objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeObject(Map)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Writer writer, int indent) throws IOException {
		writer.write("[");
		var iterator = elements.iterator();

		if (iterator.hasNext()) {
			writer.write("\n");
			Map<String, ? extends Number> firstElement = iterator.next();
			writeIndent(writer, indent + 1);
			writeObject(firstElement, writer, indent + 1);
		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			Map<String, ? extends Number> nextElement = iterator.next();
			writeIndent(writer, indent + 1);
			writeObject(nextElement, writer, indent + 1);
		}

		writeIndent(writer, indent);
		writer.write("]");
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArrayObjects(Collection)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArrayObjects(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array with nested objects.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArrayObjects(Collection)
	 */
	public static String writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArrayObjects(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Serializes a single inverted index entry as JSON. Writes the word and its associated document positions,
	 * indented according to the provided level. Relies on {@code writeObjectArrays} to format the nested map of
	 * documents to positions.
	 *
	 * @param wordEntry An entry pairing a word with its document and position mappings.
	 * @param writer    The output writer for serializing the entry.
	 * @param indent    The indentation level for pretty-printing the JSON output.
	 * @throws IOException If writing to the writer fails.
	 */
	private static void writeIndexHelper(Map.Entry<String, ? extends Map<String, ? extends Collection<? extends Number>>> wordEntry, Writer writer, int indent) throws IOException {
		writer.write("\"" + wordEntry.getKey() + "\": ");
		writeObjectArrays(wordEntry.getValue(), writer, indent);
	}

	/**
	 * Writes the inverted index structure containing words, files, and positions to a JSON file.
	 * This method iterates over the inverted index map, where each key is a word and each value is a TreeMap
	 * containing files associated with positions of that word. It writes each word along with its associated
	 * files and positions in a structured JSON format to the specified output file.
	 *
	 * @param index the InvertedIndex containing the word, file, and position mappings
	 * @param writer the writer to use
	 * @param indent the starting indent level
	 * @throws IOException if an I/O error occurs during writing
	 */
	public static void writeIndex(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> index, Writer writer, int indent) throws IOException {
		writer.write("{");

		var iterator = index.entrySet().iterator();

		if (iterator.hasNext()) {
			writer.write("\n");
			Map.Entry<String, ? extends Map<String, ? extends Collection<? extends Number>>> entry = iterator.next();
			writeIndent(writer, indent + 1);
			writeIndexHelper(entry, writer, indent + 1);
		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			Map.Entry<String, ? extends Map<String, ? extends Collection<? extends Number>>> entry = iterator.next();
			writeIndent(writer, indent + 1);
			writeIndexHelper(entry, writer, indent + 1);
		}

		writer.write("\n");

		writeIndent(writer, indent);
		writer.write("}");
	}

	/**
	 * Saves a list of words and their locations in documents to a file in JSON format.
	 *
	 * @param index The inverted index containing words, the documents they are found in, and their positions within those documents.
	 * @param outPath The path to the file where the JSON representation of the inverted index will be saved.
	 * @throws IOException If an error occurs while writing to the file.
	 *
	 */
	public static void writeIndex(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> index, Path outPath) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(outPath, UTF_8)) {
			writeIndex(index, writer, 0);
		}
	}

	/**
	 * Converts an inverted index into a JSON string.
	 *
	 * @param index The inverted index to serialize, mapping words to documents and their respective positions.
	 * @return A JSON string representation of the index, or {@code null} if an IOException occurs.
	 */
	public static String writeIndex(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> index) {
		try {
			StringWriter writer = new StringWriter();
			writeIndex(index, writer, 1);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes a single search result in JSON format.
	 *
	 * @param result the search result to write
	 * @param writer the writer to use for output
	 * @param indent the indentation level for formatting
	 * @throws IOException if an IO error occurs during writing
	 */
	public static void writeSearchResultsHelper(InvertedIndex.SearchResult result, Writer writer, int indent) throws IOException {
		writeIndent("{\n", writer, indent);

		writeIndent("\"count\": ", writer, indent + 1);
		writer.write(result.getCount() + ",\n");

		writeIndent("\"score\": ", writer, indent + 1);
		writer.write(FORMATTER.format(result.getScore()) + ",\n");

		writeIndent("\"where\": ", writer, indent + 1);
		writer.write("\"" + result.getLocation() + "\"\n");

		writeIndent("}", writer, indent);
	}

	/**
	 * Writes a collection of search result objects with writer and indent.
	 *
	 * @param searchResults the collection of search results to write
	 * @param writer the writer to use for writing
	 * @param indent the indentation level
	 * @throws IOException if an IO error occurs during writing
	 */
	private static void writeSearchResultsCollection(List<InvertedIndex.SearchResult> searchResults, Writer writer, int indent) throws IOException {
		var iterator = searchResults.iterator();

		if (iterator.hasNext()) {
			InvertedIndex.SearchResult firstResult = iterator.next();
			writeSearchResultsHelper(firstResult, writer, indent);
		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			InvertedIndex.SearchResult nextResult = iterator.next();
			writeSearchResultsHelper(nextResult, writer, indent);
		}

		if (!searchResults.isEmpty()) {
			writer.write("\n");
		}
	}

	/**
	 * Writes the search results for each query in JSON format.
	 *
	 * @param results A map where each key is a query string and the value is a list of SearchResult objects associated with that query
	 * @param writer The writer used to output the JSON format results
	 * @param indent The initial indentation level to use for formatting the JSON output
	 * @throws IOException if an IO error occurs during writing
	 */
	public static void writeSearchResults(Map<String, List<InvertedIndex.SearchResult>> results, Writer writer, int indent) throws IOException {
		writer.write("{\n");
		var iterator = results.entrySet().iterator();

		if (iterator.hasNext()) {
			var entry = iterator.next();
			String query = entry.getKey();
			List<InvertedIndex.SearchResult> searchResults = entry.getValue();

			writeQuote(query, writer, indent + 1);
			writer.write(": [\n");

			writeSearchResultsCollection(searchResults, writer, indent + 2);

			writeIndent("]", writer, indent + 1);

		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			var entry = iterator.next();
			String query = entry.getKey();
			List<InvertedIndex.SearchResult> searchResults = entry.getValue();

			writeQuote(query, writer, indent + 1);
			writer.write(": [\n");

			writeSearchResultsCollection(searchResults, writer, indent + 2);

			writeIndent("]", writer, indent + 1);

		}

		writer.write("\n}");
	}

	/**
	 * Saves a list of words and their locations in documents to a file in JSON format.
	 *
	 * @param results A map where each key is a query string and the value is a list of SearchResult objects associated with that query
	 * @param outPath The path to the file where the JSON representation of the inverted index will be saved
	 * @throws IOException If an error occurs while writing to the file
	 *
	 */
	public static void writeSearchResults(Map<String, List<InvertedIndex.SearchResult>> results, Path outPath) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(outPath, UTF_8)) {
			writeSearchResults(results, writer, 0);
		}
	}

	/**
	 * Converts an inverted index into a JSON string.
	 *
	 * @param results A map where each key is a query string and the value is a list of SearchResult objects associated with that query
	 * @return A JSON string representation of the index, or {@code null} if an IOException occurs
	 */
	public static String writeSearchResults(Map<String, List<InvertedIndex.SearchResult>> results) {
		try {
			StringWriter writer = new StringWriter();
			writeSearchResults(results, writer, 1);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}


	/** Prevent instantiating this class of static methods. */
	private JsonWriter() {
	}
}