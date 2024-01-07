package ie.atu.sw;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executors;

/**
 * This class is used to write to an output file.
 *
 * It's thread-safe.
 */
public class FileManager {
    /**
     * The name of the output file.
     */
    private static final String OUTPUT_PATH = "Output/out.txt";
    /**
     * Ensures that only one thread at a time writes to a file.
     */
    private static final Object fileOutputLock = new Object();

    /**
     * Writes the given score message to the output file.
     *
     * @param scoreData the score message.
     */
    public void writeScoreToFile(String scoreData) {
        /*
         only one thread at a time can access this method - because the
         output file is a shared resource
         */
        synchronized (fileOutputLock) {
            try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
                pool.execute(() -> {
                    try (PrintWriter out = new PrintWriter(new BufferedWriter(
                            new FileWriter(OUTPUT_PATH, true)))) {
                        out.println(scoreData);
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}