package ie.atu.sw;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The driver class.
 *
 * Uses the producer-consumer pattern in its approach to solving the problem
 * of simultaneously displaying the interactive menu and executing menu actions
 * that correspond to selected menu options.
 *
 * The producer thread (the first virtual thread) adds a chosen menu option to the
 * pendingOptions thread-safe queue, while the consumer thread (the second virtual thread)
 * polls the available options from the queue, and executes the corresponding menu action.
 *
 * Launches the menu, analyses the tweets, writes the scores to output file.
 */
public class Runner {
    /**
     * Is used for writing score results to a file.
     */
    private static final FileManager fileManager = new FileManager();
    /**
     * Is used to communicate with the user - retrieves the menu option choice,
     * the file names from the user.
     */
    private static final MenuManager menuManager = new MenuManager();

    /**
     * It is volatile so that any changes to the value of this variable
     * are immediately visible to all virtual threads - once this variable
     * is false, each virtual threads finishes
     */
    private static volatile boolean shouldRun = true;

    /**
     * Is used to create two virtual thread: the first thread is responsible
     * for displaying the menu and getting user's menu option choice
     * (which then is added to the pendingOptions queue),
     * while the second thread is used to retrieve the available pending menu option from
     * the queue, and execute the appropriate menu action.
     */
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * Is used to create virtual threads on which performSentimentAnalysis would be run.
     * For each call of menu option (4) a new virtual thread is launched.
     */
    private static final ExecutorService analyzerExecutor = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * Stores the name of the input file with the tweets.
     */
    private static String inputFilePath = "";
    /**
     * Stores the name of the input file with the sentiment map (lexicon).
     */
    private static String sentimentMapFilePath = "";
    /**
     * Stores the name of the input file with the stopwords.
     */
    private static String stopwordsFilePath = "";

    /**
     * Contains pending options - the options entered by the user earlier,
     * waiting to be processed by the corresponding virtual thread.
     *
     * A thread-safe collection is used.
     */
    private static final LinkedBlockingQueue<Integer> pendingOptions = new LinkedBlockingQueue<>();

    /**
     * The driver method.
     *
     * @param args not used.
     */
    public static void main(String[] args) {
        try (executor) {
            // create the menu virtual thread
            executor.submit(() -> {
                try {
                    while (shouldRun) {
                        // show the menu and get the user's choice option
                        int option = menuManager.menu();

                        // if the user wants to run the analysis, add the option to the queue
                        if (option == 4) {
                            /*
                             this new pending option will be picked up
                             and executed by the second virtual thread
                             */
                            pendingOptions.add(option);
                        } else {
                            /*
                             otherwise, the user wants to update the file name (or quit),
                             because it's part  of interaction with the menu, it belongs
                             to this current thread
                             */
                            menuAction(option);
                        }
                    }

                    System.out.println("Done - Menu Thread");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // create a virtual thread for polling pending menu options
            executor.submit(() -> {
                try {
                    while (shouldRun) {
                        if (!pendingOptions.isEmpty()) {
                            int nextOption = pendingOptions.poll();
                            menuAction(nextOption);
                        }
                    }

                    System.out.println("Done - Pending Options Thread");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Executes the given menu option.
     *
     * @param option the menu option.
     */
    public static void menuAction(int option) {
        switch (option) {
            case 1:
                System.out.println("\nOption 1 Selected: Specify a Text File");
                inputFilePath = menuManager.getFileName("Enter the path of the text file: ");

                break;
            case 2:
                System.out.println("\nOption 2 Selected: Specify a Sentiment Map File");
                sentimentMapFilePath = menuManager.getFileName("Enter the path of the sentiment map file: ");

                break;
            case 3:
                System.out.println("\nOption 3 Selected: Specify a Stopwords File");
                stopwordsFilePath = menuManager.getFileName("Enter the path of the stopwords file: ");

                break;
            case 4:
                System.out.println("\nOption 4 Selected: Perform Sentiment Analysis");

                /*
                 don't let the user change the file names until the current
                 file names have been passed to the method
                 */
                synchronized (menuManager.getUserInputLock()) {
                    // each user's analysis is executed on its own thread
                    analyzerExecutor.execute(() -> {
                        performSentimentAnalysis(inputFilePath, sentimentMapFilePath, stopwordsFilePath);
                    });
                }

                break;
            case 5:
                System.out.println("Quitting the application");
                // update the volatile variable
                shouldRun = false;

                break;
            default:
                System.out.println("Invalid option selected. Please try again.");

                break;
        }
    }

    /**
     * This method creates a new parser, and a new twitter analyzer.
     *
     * Uses JOptionPane to display the results, so that the output from
     * this method does not interfere with menu output.
     *
     * @param inputFilePath        the name of the file with the tweets.
     * @param sentimentMapFilePath the name of the file with the lexicon.
     * @param stopwordsFilePath    the name of the file with the stopwords.
     */
    private static void performSentimentAnalysis(String inputFilePath, String sentimentMapFilePath, String stopwordsFilePath) {
        try {
            // create a brand-new parser
            VirtualThreadFileParser parser = new VirtualThreadFileParser();
            parser.clear();

            if (!inputFilePath.isEmpty() && !sentimentMapFilePath.isEmpty() && !stopwordsFilePath.isEmpty()) {
                parser.go(inputFilePath);

                // Assuming getWords() returns List<String> of tweets
                List<String> tweets = parser.getWords();

                // create a brand-new analyzer
                TweetSentimentAnalyzer analyzer = new TweetSentimentAnalyzer(sentimentMapFilePath, stopwordsFilePath);
                float totalScore = analyzer.analyzeTweets(tweets);

                // only display the score when the user is not typing a file name
                String scoreData = String.format("Total score: %,.2f (%s, %s, %s)", totalScore,
                        inputFilePath, sentimentMapFilePath, stopwordsFilePath);

                JOptionPane.showMessageDialog(null, scoreData);

                // is executed on a brand new virtual thread
                fileManager.writeScoreToFile(scoreData);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Please ensure all file paths (text, sentiment map, stopwords) are specified.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading files: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}