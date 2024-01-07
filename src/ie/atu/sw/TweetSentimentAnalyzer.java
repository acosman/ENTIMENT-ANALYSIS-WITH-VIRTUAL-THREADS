package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is responsible for analyzing the sentiment of tweets.
 * It uses a sentiment map and a set of stopwords to compute sentiment scores.
 */
public class TweetSentimentAnalyzer {

    private final Map<String, Integer> sentimentMap;
    private final Set<String> stopwords;

    /**
     * Constructs a TweetSentimentAnalyzer with specified files for sentiment map and stopwords.
     * Each instance of TweetSentimentAnalyzer is intended to be used within a single thread.
     *
     * @param sentimentMapFile The file path for the sentiment map.
     * @param stopwordsFile The file path for the stopwords.
     * @throws IOException If there is an error reading the files.
     */
    public TweetSentimentAnalyzer(String sentimentMapFile, String stopwordsFile) throws IOException {
        this.sentimentMap = SentimentScoreMapLoader.loadSentimentScoreMap(sentimentMapFile);
        this.stopwords = loadStopwords(stopwordsFile);
    }

    /**
     * Loads stopwords from a given file.
     *
     * @param fileName The name of the file containing stopwords.
     * @return A set of stopwords.
     * @throws IOException If there is an error reading the file.
     */
    private Set<String> loadStopwords(String fileName) throws IOException {
        Set<String> stopwords = new HashSet<>();
        try (BufferedReader stop = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = stop.readLine()) != null) {
                stopwords.add(line.toLowerCase());
            }
        }
        return stopwords;
    }

    /**
     * Analyzes a list of tweets and calculates their total sentiment score.
     *
     * @param tweets The list of tweets to analyze.
     * @return The total sentiment score of the list of tweets.
     */
    public float analyzeTweets(List<String> tweets) {
        float total = 0;
        for (String tweet : tweets) {
            total += processTweet(tweet);
        }

        return total;
    }

    /**
     * Processes a single tweet and calculates its sentiment score.
     * It splits the tweet into words, and for each word not in the stopwords set,
     * it calculates the sentiment score based on the sentiment map.
     *
     * @param tweet The tweet to be processed.
     * @return The sentiment score of the tweet.
     */
    private float processTweet(String tweet) {
        String[] words = tweet.split(" ");
        float tweetScore = 0;

        for (String word : words) {
            word = word.toLowerCase();
            if (!stopwords.contains(word) && sentimentMap.containsKey(word)) {
                tweetScore += sentimentMap.get(word);

                // simulate a longer execution
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return tweetScore;
    }
}
