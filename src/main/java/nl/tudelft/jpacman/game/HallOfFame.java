package nl.tudelft.jpacman.game;

import javax.swing.*;
import java.io.*;
import java.nio.channels.FileChannel;

/**
 * The Hall of Fame.
 */
public class HallOfFame {
    /**
     * Number of best scores stored in the hall of fame and longest names allowed in hall of fame.
     */
    private static final int NUMBER_OF_RECORDS = 10, NAME_LENGTH = 25;

    /**
     * Relative path of the default Hall of Fame.
     */
    private static final String DEFAULT_HOF_PATH = new File("").getAbsolutePath() + "/src/main/resources/DefaultHoF.txt";

    /**
     * Relative path of the running Hall of Fame.
     */
    private static final String HOF_PATH = new File("").getAbsolutePath() + "/src/main/resources/HoF.txt";

    /**
     * Whether the application is running or whether it's being tested. (ham for the launcherSmokeTest)
     */
    private static boolean isNotATest, ham; //booleans are initialised to false by default, so these can stay uninitialised.

    /**
     * The points scored in the game.
     */
    private int score;

    /**
     * Sets whether the application is running or being test.
     *
     * @param noTest Whether the application is running or being test.
     */
    public static void setIsNotATest(final boolean noTest) {
        isNotATest = noTest;
    }

    /**
     * Determines if the Hall of Fame is a ham. (whether the smoke test is running or the application)
     */
    public static void setHam() {
        ham = true;
    }

    /**
     * Get relative path of the running Hall of Fame.
     *
     * @return The String corresponding to the path where the HoF file is stored
     */
    public String getHoFPath() {
        return HOF_PATH;
    }

    /**
     * Get relative path of the running Hall of Fame.
     *
     * @return The String corresponding to the path where the default HoF file is stored
     */
    public String getDefaultHoFPath() {
        return DEFAULT_HOF_PATH;
    }

    /**
     * Returns the number of records kept in the Hall of Fame.
     *
     * @return The number of records kept in the Hall of Fame.
     */
    public int getNumberOfRecordsKept() {
        return NUMBER_OF_RECORDS;
    }

    /**
     * Handles the Hall of Fame.
     *
     * @param pointsScored The points scored by the current player in the elapsed game.
     * @param playerName   The name of the current player.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    public void handleHoF(final int pointsScored, final String playerName) {
        int bestScores[] = new int[NUMBER_OF_RECORDS];
        String bestPlayers[] = new String[NUMBER_OF_RECORDS];
        score = pointsScored;

        try {
            final BufferedReader reader = new BufferedReader(new FileReader(HOF_PATH));
            for (int i = 0; i < NUMBER_OF_RECORDS; i++) {
                final String split[] = reader.readLine().split(" ");
                bestPlayers[i] = split[0];
                bestScores[i] = Integer.parseInt(split[1]);
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error whilst reading HoF.txt " + e.getMessage());
        }

        //Inserting eventual better score into Hall of Fame.
        updateHoF(bestScores, bestPlayers, playerName);
        //Displaying the HOF, regardless of whether it has been updated or not.
        if (isNotATest && !ham) displayHoF(bestScores, bestPlayers);
    }

    /**
     * Updates the Hall of Fame when needs be.
     *
     * @param bestScores  The list of high scores to store.
     * @param bestPlayers The list of players associated to the high scores to store.
     * @param playerName  The name of the current player.
     */
    private void updateHoF(int[] bestScores, String[] bestPlayers, final String playerName) {
        for (int i = 0; i < NUMBER_OF_RECORDS; i++) {
            if (score > bestScores[i]) {
                for (int j = NUMBER_OF_RECORDS - 1; j > i; j--) {
                    bestScores[j] = bestScores[j - 1];
                    bestPlayers[j] = bestPlayers[j - 1];
                }
                //In case the player isn't logged in.
                if (playerName == null && !ham) {
                    bestPlayers[i] = askName();
                } else {
                    bestPlayers[i] = playerName;
                }
                bestScores[i] = score;
                //Saving new Hall of Fame to file.
                saveUpdatedHoF(bestScores, bestPlayers);
                break;
            }
        }
    }

    /**
     * Asks the player's name to store in the Hall of Fame is the player is not logged in.
     *
     * @return The name.
     */
    private String askName() {
        final String options[] = {"Ok"};
        final JPanel panel = new JPanel();
        final JLabel label = new JLabel("Enter your name: ");
        final JTextField userInput = new JTextField(NAME_LENGTH);
        panel.add(label);
        panel.add(userInput);
        JOptionPane.showOptionDialog(null, panel, "New High Score!", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        return userInput.getText();
    }

    /**
     * Displays the Hall of Fame.
     *
     * @param bestScores  The list of high scores to display.
     * @param bestPlayers The list of players to display.
     */
    private void displayHoF(final int bestScores[], final String... bestPlayers) {
        String text = "";
        final String options[] = {"Leave", "Reset"};
        for (int i = 0; i < NUMBER_OF_RECORDS; i++) text += formatDisplay(bestScores[i], bestPlayers[i]);
        final int buttonPressed = JOptionPane.showOptionDialog(null, text, "Hall of Fame", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (buttonPressed == 1) resetHoF();
    }

    /**
     * Formats the Hall of Fame's display.
     *
     * @param score  A score.
     * @param player A player name.
     * @return A formatted display.
     */
    //Trying to align the displays in 2 columns.
    private String formatDisplay(final int score, final String player) {
        String padding = "";
        for (int i = NAME_LENGTH; i > player.length(); i--) padding += " ";
        return player + padding + score + "\n\n";
    }

    /**
     * Saves the updated Hall of Fame to file.
     *
     * @param bestScores  The list of high scores to store.
     * @param bestPlayers The list of players associated to the high scores to store.
     */
    private void saveUpdatedHoF(final int bestScores[], final String... bestPlayers) {
        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(HOF_PATH));
            for (int i = 0; i < NUMBER_OF_RECORDS; i++) writer.write(bestPlayers[i] + " " + bestScores[i] + "\n");
            writer.close();
        } catch (IOException e) {
            System.err.println("Error whilst writing to HoF.txt " + e.getMessage());
        }
    }

    /**
     * Resets the Hall of Fame to its default values.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    public void resetHoF() {
        int buttonPressed = 0;
        if (isNotATest) {
            final Object options[] = {"Yes", "No"};
            buttonPressed = JOptionPane.showOptionDialog(null, "Do you really want to erase the Hall of Fame?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
        }
        if (buttonPressed == 0) {
            try {
                final FileChannel src = new FileInputStream(DEFAULT_HOF_PATH).getChannel(), dest = new FileOutputStream(HOF_PATH).getChannel();
                dest.transferFrom(src, 0, src.size());
                if (isNotATest)
                    JOptionPane.showMessageDialog(null, "Hall of Fame reset!", "Reset", JOptionPane.PLAIN_MESSAGE);
            } catch (IOException e) {
                System.err.println("Error whilst writing to HoF.txt " + e.getMessage());
            }
        }
    }
}
