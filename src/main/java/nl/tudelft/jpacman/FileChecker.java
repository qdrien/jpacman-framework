package nl.tudelft.jpacman;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public final class FileChecker {
    /**
     * The path of the file containing usernames and passwords.
     */
    private static final String LOGIN_PATH = new File("").getAbsolutePath()
            + "/src/main/resources/login.txt";

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
        final BufferedReader reader = new BufferedReader(new FileReader(LOGIN_PATH));
        while ((line = reader.readLine()) != null) {
            if (name.equals(line.split(" ")[0])) {
                JOptionPane.showMessageDialog(null, "Profile already exists", "Error",
                        JOptionPane.PLAIN_MESSAGE);
                return true;
            }
        }
        reader.close();
        return false;
    }

    /**
     * Checks whether the player correctly identified himself.
     *
     * @param playerName The name of the player
     * @param passEntered the password entered by the player.
     * @return Whether the identifying info is correct or not.
     */
    public static boolean checkLoginInfo(final String playerName, final char... passEntered) {
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(LOGIN_PATH));
            String line = reader.readLine();
            while (line != null) {
                final String[] split = line.split(" ");
                if (split[0].equals(playerName)
                        && Arrays.hashCode(passEntered) == Integer.parseInt(split[1]))
                    return true;
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error whilst reading login.txt " + e.getMessage());
        }
        JOptionPane.showMessageDialog(null, "Username and/or password is erroneous", "Error",
                JOptionPane.PLAIN_MESSAGE);
        return false;
    }
}
