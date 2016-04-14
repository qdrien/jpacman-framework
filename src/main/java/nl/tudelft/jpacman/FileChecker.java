package nl.tudelft.jpacman;

import nl.tudelft.jpacman.game.Achievement;

import javax.swing.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Utility class to check various values found in various files.
 */
@SuppressWarnings("checkstyle:linelength") // because that's just completely silly. We're not coding on phone screens.
public final class FileChecker {
    /**
     * The path of the file containing usernames and passwords.
     */
    private static final String LOGIN_PATH = new File("").getAbsolutePath() + "/src/main/resources/login.txt";

    /**
     * Forces the compiler to not generate default constructor, making this a true Utility Class.
     */
    private FileChecker() {
    }

    /**
     * Checks whether a user already exists with the username desired by the player.
     *
     * @param name the name to check.
     * @return Whether the name is already in use or not.
     * @throws IOException If the login file cannot be found or read.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    public static boolean checkUsername(final String name) throws IOException {
        String line;
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(LOGIN_PATH), Charset.defaultCharset()));
        while ((line = reader.readLine()) != null) {
            if (name.equals(line.split(" ")[0])) {
                JOptionPane.showMessageDialog(null, "Profile already exists", "Error", JOptionPane.PLAIN_MESSAGE);
                reader.close();
                return true;
            }
        }
        reader.close();
        return false;
    }

    /**
     * Checks whether the player correctly identified himself.
     *
     * @param playerName  The name of the player
     * @param passEntered the password entered by the player.
     * @return Whether the identifying info is correct or not.
     */
    public static boolean checkLoginInfo(final String playerName, final char... passEntered) {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(LOGIN_PATH), Charset.defaultCharset()));
            String line = reader.readLine();
            while (line != null) {
                final String[] split = line.split(" ");
                if (split[0].equals(playerName) && Arrays.hashCode(passEntered) == Integer.parseInt(split[1])) {
                    reader.close();
                    return true;
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error whilst reading login.txt " + e.getMessage());
        }
        JOptionPane.showMessageDialog(null, "Username and/or password is erroneous", "Error", JOptionPane.PLAIN_MESSAGE);
        return false;
    }

    /**
     * Checks whether an achievement has already been earned by the player.
     *
     * @param achievement the achievement to check.
     * @param profilePath The path to the profile file
     * @return Whether the achievement has already been earned or not.
     * @throws IOException If the file was not found or is not readable.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    public static boolean checkAchievement(final String profilePath, final Achievement achievement) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(profilePath), Charset.defaultCharset()));
        //the first line is ignored, since it contains other information.
        String line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            //Removing whitespace just in case the file has been manually edited.
            line = line.replaceAll("\\s+", "");
            if (line.equals(achievement.toString())) {
                reader.close();
                return true;
            }
        }
        reader.close();
        return false;
    }

    /**
     * Reads the player's achievements from file.
     *
     * @param achievements the list of achievements.
     * @param profilePath The path to the profile file
     * @return The updated list of achievements.
     * @throws IOException If the file was not found or is not readable.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    public static String parseAchievements(final String profilePath, String achievements) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(profilePath), Charset.defaultCharset()));
        //first line ignored, it contains other other information
        String achievementName = reader.readLine();
        StringBuilder builder = new StringBuilder(50);
        builder.append(achievements).append("<br>Achievements: <br>");
        while ((achievementName = reader.readLine()) != null) {
            builder.append(achievementName).append(": ").append(Achievement.parseAchievement(achievementName).getDescription()).append("<br>");
        }
        reader.close();
        return builder.toString();
    }
}
