package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Extends {@link InvertedIndex} to provide a thread-safe inverted index.
 * This implementation uses concurrent data structures and synchronization
 * mechanisms to ensure that multiple threads can access and modify the index safely and efficiently.
 */
public class MultiThreadedInvertedIndex extends InvertedIndex {
	/**
	 * Lock used for managing concurrent read and exclusive write access to the inverted index
	 */
	private final MultiReaderLock lock;

	/**
	 * Constructs a new MultiThreadedInvertedIndex.
	 * Initializes a lock used for managing concurrent read and exclusive write access.
	 */
	public MultiThreadedInvertedIndex () {
		this.lock = new MultiReaderLock();
	}

	@Override
	public void add(String word, String location, int position) {
		lock.writeLock().lock();
		try {
			super.add(word, location, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void addAll(ArrayList<String> words, String location, int position) {
		lock.writeLock().lock();
		try {
			super.addAll(words, location, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void writeIndex(Path path) throws IOException {
		lock.readLock().lock();
		try {
			super.writeIndex(path);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Map<String, Integer> getWordCount() {
		lock.readLock().lock();
		try {
			return Collections.unmodifiableMap(super.getWordCount());
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numWords() {
		lock.readLock().lock();
		try {
			return super.numWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasWord(String word) {
		lock.readLock().lock();
		try {
			return super.hasWord(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasLocation(String word, String location) {
		lock.readLock().lock();
		try {
			return super.hasLocation(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> viewLocations(String word) {
		lock.readLock().lock();
		try {
			// TODO return super.viewLocations(word);
			return Collections.unmodifiableSet(super.viewLocations(word));
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasPosition(String word, String location, int position) {
		lock.readLock().lock();
		try {
			return super.hasPosition(word, location, position);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<Integer> viewPositions(String word, String location) {
		lock.readLock().lock();
		try {
			// TODO Just super call
			return Collections.unmodifiableSet(super.viewPositions(word, location));
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasCount(String location) {
		lock.readLock().lock();
		try {
			return super.hasCount(location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Map<String, Integer> viewCount() {
		lock.readLock().lock();
		try {
			// TODO Return super
			return Collections.unmodifiableMap(super.viewCount());
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int getCount(String location) {
		lock.readLock().lock();
		try {
			return super.getCount(location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<SearchResult> exactSearch(Set<String> queryWords) {
		lock.readLock().lock();
		try {
			return super.exactSearch(queryWords);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<SearchResult> partialSearch(Set<String> queryWords) {
		lock.readLock().lock();
		try {
			return super.partialSearch(queryWords);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> viewWords() {
		lock.readLock().lock();
		try {
			return super.viewWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Merges another inverted index into this index.
	 *
	 * @param other the inverted index to merge into this one
	 */
	@Override
	public void combine(InvertedIndex other) {
		lock.writeLock().lock();
		try {
			super.combine(other);
		} finally {
			lock.writeLock().unlock();
		}
	}
}