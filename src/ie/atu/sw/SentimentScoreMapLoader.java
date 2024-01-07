package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Is used to load the sentiment score map (lexicon) from the input file.
 */
public class SentimentScoreMapLoader {
    /**
     * Loads the sentiment map from the input file.
     *
     * @param fileName the name of the input file.
     *
     * @return the sentiment map.
     *
     * @throws IOException if the map couldn't be read.
     */
    public static Map<String, Integer> loadSentimentScoreMap(String fileName) throws IOException {
        Map<String, Integer> map = new HashMap<>();

        // using try with resources to ensure the resource is closed after its use
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length >= 2) {
                    map.put(parts[0].toLowerCase(), Integer.parseInt(parts[1]));
                } else {
                    System.out.println("Invalid line format: " + line);
                }
            }
        }

        return map;
    }
}