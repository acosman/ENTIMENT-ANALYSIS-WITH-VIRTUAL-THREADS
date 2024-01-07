package ie.atu.sw;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;

/**
 * This class is responsible for parsing text files using virtual threads.
 * It reads a file line by line, splits each line into words, and stores these words in a list.
 */
public class VirtualThreadFileParser {
    private static int line = 0;
    private final List<String> words = new CopyOnWriteArrayList<>();

    /**
     * Parses the specified file and processes each line using virtual threads.
     * Each line is split into words, and these words are stored in a list.
     *
     * @param book The path to the file to be parsed.
     * @throws Exception if an I/O error occurs opening the file or if an interrupt occurs.
     */
    public void go(String book) throws Exception {
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            Files.lines(Paths.get(book)).forEach(text -> {
                pool.execute(() -> {
                    process(text);
                });
            });
        }
    }

    /**
     * Clears the list of words. This can be used to reset the parser.
     */
    public void clear() {
        this.words.clear();
    }

    /**
     * Processes a single line of text, splitting it into individual words
     * and adding them to the words list. Each call to this method increments the line count.
     *
     * @param text The line of text to process.
     */
    private void process(String text) {
        Arrays.stream(text.split("\\s+")).forEach(w -> words.add(w));
        incrementLine();
    }

    /**
     * Synchronized method to increment the line count.
     * This method is synchronized to maintain thread safety as it modifies the static line variable.
     */
    private synchronized void incrementLine() {
        line++;
    }

    /**
     * Retrieves the list of words accumulated from the parsed file.
     *
     * @return A list of words.
     */
    public List<String> getWords() {
        return words;
    }
}
