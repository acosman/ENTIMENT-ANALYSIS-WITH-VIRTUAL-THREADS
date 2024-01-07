package ie.atu.sw;

import java.util.Scanner;

/**
 * Is used to display the menu and get the menu choice
 * from the user.
 */
public class MenuManager {
    /**
     * Is made static to ensure that no matter how many instances
     * of MenuManager are created, all would share the same Scanner,
     * because otherwise instance Scanner objects could interfere
     * with one another due to sharing the same stream (System.in).
     */
    private static final Scanner scanner = new Scanner(System.in);
    /**
     * Is used to ensure that file the user is typing file names,
     * the analysis can't be started, and vice versa.
     */
    private static final Object userInputLock = new Object();

    /**
     * Returns the lock responsible for locking the
     * process of retrieving the file names from the user.
     *
     * While this class runs on a single thread, the menuAction method from
     * the Runner class is executed from the second thread, and we want to ensure
     * that as we retrieve the file name data that is updated by this class,
     * that data is not in a process of being changed.
     *
     * @return the lock.
     */
    public Object getUserInputLock() {
        return userInputLock;
    }

    /**
     * Returns the menu option chosen by the user.
     *
     * @return the menu option.
     */
    public int menu() {
        String prompt = "************************************************************\n" +
                "*     ATU - Dept. of Computer Science & Applied Physics    *\n" +
                "*                                                          *\n" +
                "*             Virtual Threaded Sentiment Analyser          *\n" +
                "*                                                          *\n" +
                "************************************************************\n" +
                "(1) Specify a Text File\n" +
                "(2) Specify a Sentiment Map File\n" +
                "(3) Specify a Stopwords File\n" +
                "(4) Perform Sentiment Analysis\n" +
                "(5) Quit\n" +
                "Select Option [1-5]>";

        String input;
        boolean isValidOption;
        do {
            input = getNotEmptyInput(prompt);
            System.out.println();

            isValidOption = isValidOption(input);

            if (!isValidOption) {
                System.out.println("Is not a valid option!");
            }
        } while (!isValidOption);

        // by this point the option is guaranteed to be a valid number
        return Integer.parseInt(input);
    }

    /**
     * Gets a file name from the user.
     *
     * @param prompt the prompt message.
     *
     * @return the file name.
     */
    public String getFileName(String prompt) {
        /*
        require this lock, because this lock could be in use by the second thread
        that is running option 4 of menuAction
         */
        synchronized (userInputLock) {
            return getNotEmptyInput(prompt);
        }
    }

    /**
     * Checks whether the given option is valid.
     *
     * An option is valid if it's a number in 1 - 5 range.
     *
     * @param option the option String.
     * @return true, if valid, false otherwise.
     */
    private boolean isValidOption(String option) {
        switch (option) {
            // is a valid option
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
                return true;
            // is invalid option
            default:
                return false;
        }
    }

    /**
     * Gets non-empty input from the user.
     *
     * @param prompt the prompt message.
     * @return the input String.
     */
    private String getNotEmptyInput(String prompt) {
        synchronized (scanner) {
            boolean isEmpty;
            String input;

            do {
                System.out.print(prompt);
                input = scanner.nextLine().trim();
                isEmpty = input.isEmpty();
            } while (isEmpty);

            return input;
        }
    }
}