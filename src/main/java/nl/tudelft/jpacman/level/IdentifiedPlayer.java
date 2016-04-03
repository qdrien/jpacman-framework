package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.game.Achievement;
import nl.tudelft.jpacman.npc.ghost.GhostColor;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;
import java.util.Map;

/**
 * A player operated unit in our game.
 *
 * @author Jeroen Roosen
 */
public class IdentifiedPlayer extends Player {

    /**
     * The maximum size of the player's username and password.
     */
    private static final int MAX_LOGIN_LENGTH = 25, MAX_PASS_LENGTH = 15;
    /**
     * The path of the file containing usernames and passwords.
     */
    private static final String LOGIN_PATH = new File("").getAbsolutePath()
            + "/src/main/resources/login.txt";
    /**
     * Whether the application is running or whether it's being tested.
     */
    private static boolean isNotATest = true;
    protected String profilePath;

    /**
     * Creates a new player.
     *
     * @param spriteMap      A map containing a sprite for this player for every direction.
     * @param deathAnimation The sprite to be shown when this player dies.
     */
    public IdentifiedPlayer(Map<Direction,
            Sprite> spriteMap, AnimatedSprite deathAnimation) {
        super(spriteMap, deathAnimation);
    }

    /**
     * Sets whether the application is running or being tested.
     */
    public static void setIsNotATest() {
        isNotATest = false;
    }

    /**
     * Authenticates existing player profile.
     *
     * @return Whether the identification was carried through or cancelled.
     */
    public boolean authenticate() {
        final String options[] = {"Ok", "Cancel"};
        final JPanel panel = new JPanel();
        final JLabel loginLabel = new JLabel("Login: "), passLabel = new JLabel("Password: ");
        final JTextField loginEntered = new JTextField(MAX_LOGIN_LENGTH);
        final JPasswordField passEntered = new JPasswordField(MAX_PASS_LENGTH);
        panel.add(loginLabel);
        panel.add(loginEntered);
        panel.add(passLabel);
        panel.add(passEntered);
        do {
            final int choice = JOptionPane.showOptionDialog(null, panel, "Identification",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    options, options[0]);
            if (choice != 0) return false;
            playerName = loginEntered.getText();
        } while (!checkLoginInfo(passEntered.getPassword()));
        setProfilePath();
        JOptionPane.showMessageDialog(null, "You are now logged in as " + playerName,
                "Login successful", JOptionPane.PLAIN_MESSAGE);
        //Security precaution
        Arrays.fill(passEntered.getPassword(), '0');
        return true;
    }

    /**
     * Sets the path to the file storing the player's stats. (default version)
     */
    private void setProfilePath() {
        profilePath = new File("").getAbsolutePath()
                + "/src/main/resources/profiles/" + playerName + ".prf";
    }

    /**
     * Sets the path to the file storing the player's stats.
     *
     * @param s The path to set.
     */
    public void setProfilePath(final String s) {
        profilePath = s;
    }

    /**
     * Displays the player's achievements, if any were obtained.
     */
    public void displayAchievements() {
        final String[] options = new String[]{"Yes", "No"};
        final JPanel panel = new JPanel();
        panel.add(new JLabel("Display Achievements?"));
        if (JOptionPane.showOptionDialog(null, panel, "Query",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                options, options[0]) != 0) return;
        String toDisplay = "<html>";
        try
        {
            toDisplay = parseAchievements(toDisplay);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        toDisplay += "</html>";
        if ("<html></html>".equals(toDisplay))
            JOptionPane.showMessageDialog(null, "No achievements earned yet.", "Awww",
                    JOptionPane.PLAIN_MESSAGE);
        else
            JOptionPane.showMessageDialog(null, toDisplay, "Achievements",
                    JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Reads the player's achievements from file.
     *
     * @param achievements the list of achievements.
     * @return The updated list of achievements.
     * @throws IOException If the file was not found or is not readable.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    private String parseAchievements(String achievements) throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(profilePath));
        //first line ignored, it contains other other information
        String achievementName = reader.readLine();
        achievements += "<br>Achievements: <br>";
        while ((achievementName = reader.readLine()) != null) {
            achievements += achievementName + ": "
                    + Achievement.parseAchievement(achievementName).getDescription() + "<br>";
        }
        reader.close();
        return achievements;
    }

    /**
     * Adds an achievement to the player's profile file.
     *
     * @param achievement The achievement to add.
     */
    public void addAchievement(final Achievement achievement) {
        //If the achievement has already been obtained by this player
        // (or the player isn't logged in), don't add it.
        try {
            if (playerName == null || checkAchievement(achievement)) return;
            final BufferedWriter writer = new BufferedWriter(new FileWriter(profilePath, true));
            writer.write(achievement + System.getProperty("line.separator"));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final int bonus = achievement.getBonusScore();
        score += bonus;
        if (isNotATest)
            JOptionPane.showMessageDialog(null,
                    "Achievement unlocked: " + achievement + ", gained " + bonus + " points.",
                    "Congratulations", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Checks whether an achievement has already been earned by the player.
     *
     * @param achievement the achievement to check.
     * @return Whether the achievement has already been earned or not.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    private boolean checkAchievement(final Achievement achievement) throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(profilePath));
        //the first line is ignored, since it contains other information.
        String line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            //Removing whitespace just in case the file has been manually edited.
            line = line.replaceAll("\\s+", "");
            if (line.equals(achievement.toString())) return true;
        }
        reader.close();
        return false;
    }

    /**
     * Creates new player profile.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    public void createNewPlayer() {
        final String options[] = {"Ok", "Cancel"};
        final JPanel panel = new JPanel();
        final JLabel loginLabel = new JLabel("Login: "), passLabel = new JLabel("Password: ");
        final JTextField loginEntered = new JTextField(MAX_LOGIN_LENGTH);
        final JPasswordField passEntered = new JPasswordField(MAX_PASS_LENGTH);
        panel.add(loginLabel);
        panel.add(loginEntered);
        panel.add(passLabel);
        panel.add(passEntered);
        try {
            int choice = 0;
            do {
                if (isNotATest)
                    choice = JOptionPane.showOptionDialog(null, panel, "Profile creation",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                            options, options[0]);
                if (choice != 0) return;
                playerName = loginEntered.getText();
            } while (checkUsername(playerName));
            final char pass[] = passEntered.getPassword();
            BufferedWriter writer = new BufferedWriter(new FileWriter(LOGIN_PATH, true));
            writer.write(playerName + " " + Arrays.hashCode(pass) + "\n");
            writer.close();
            //Creating "profiles" subdirectory if necessary.
            new File(new File("").getAbsolutePath() + "/src/main/resources/profiles").mkdir();
            //Creating the profile file for the new user.
            setProfilePath();
            writer = new BufferedWriter(new FileWriter(profilePath));
            //0 levels completed, 0 high score achieved, 0 fruits eaten,
            // 0 ghosts killed, 0 times killed by Blinky, 0 times killed by Pinky,
            // 0 times killed by Inky, 0 times killed by Clyde.
            writer.write("0 0 0 0 0 0 0 0" + System.getProperty("line.separator"));
            writer.close();
            JOptionPane.showMessageDialog(null, "Profile created", "Success",
                    JOptionPane.PLAIN_MESSAGE);
            //Security precaution
            Arrays.fill(pass, '0');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether a user already exists with the username desired by the player.
     *
     * @param name the name to check.
     * @return Whether the name is already in use or not.
     * @throws IOException If the login file cannot be found or read.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    private boolean checkUsername(final String name) throws IOException {
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
     * @param passEntered the password entered by the player.
     * @return Whether the identifying info is correct or not.
     */
    private boolean checkLoginInfo(final char... passEntered) {
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(LOGIN_PATH));
            String line = reader.readLine();
            while (line != null) {
                final String split[] = line.split(" ");
                final String login = split[0];
                if (login.equals(playerName)
                        && Arrays.hashCode(passEntered) == Integer.parseInt(split[1]))
                    return true;
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error whilst reading login.txt " + e.getMessage());
        }
        JOptionPane.showMessageDialog(null, "Username and/or password is erroneous",
                "Error", JOptionPane.PLAIN_MESSAGE);
        return false;
    }

    /**
     * Triggered whenever the player completes a game.
     *
     * @param level The id of the level that has been completed
     */
    public void levelCompleted(int level) {
        if (playerName == null) return;
        try {
            final String split[] = getInfoLine();
            final int levelsCompleted = Integer.parseInt(split[0]);
            addAchievement(Achievement.VICTOR);
            if (level > levelsCompleted) {
                String result = "";
                for (int i = 1; i < split.length; i++) result += split[i] + " ";
                setInfoLine(level + " " + result);
                if (levelsCompleted >= 3) addAchievement(Achievement.WON_THRICE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the first line of the player's profile file, which contains various information.
     *
     * @return The line, split into its components.
     * @throws IOException If the file cannot be found or read.
     */
    private String[] getInfoLine() throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(profilePath));
        final String split[] = reader.readLine().split(" ");
        reader.close();
        return split;
    }

    /**
     * Triggered whenever the player dies.
     *
     * @param killer The ghost that killed pacman.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the DU anomaly warning makes no sense.
    public void killedBy(final GhostColor killer) {
        if (playerName == null) return;
        try {
            final String split[] = getInfoLine();
            String toWrite = "";
            final Achievement toGrant = killer.getAchievementGranted();
            for (int i = 0; i < split.length; i++) {
                if (i == killer.getIndex()) toWrite += Integer.parseInt(split[i]) + 1 + " ";
                else toWrite += split[i] + " ";
            }
            setInfoLine(toWrite);
            if (toGrant != null) addAchievement(toGrant);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the player's highest scores and checks whether it's high enough
     * to earn him an achievement.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    public void saveScore() {
        if (playerName == null) return;
        try {
            final String split[] = getInfoLine();
            String toWrite = "";
            int highScore = Integer.parseInt(split[1]);
            if (score > 9000) addAchievement(Achievement.OVER_9000);
            if (score > highScore) highScore = score;
            for (int i = 0; i < split.length; i++) {
                if (i == 1) toWrite += highScore + " ";
                else toWrite += split[i] + " ";
            }
            setInfoLine(toWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates all the player's info.
     *
     * @param toWrite The info to write to the player's profile file.
     * @throws IOException If the file cannot be written to.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    private void setInfoLine(String toWrite) throws IOException {
        toWrite += System.getProperty("line.separator");
        final BufferedReader reader = new BufferedReader(new FileReader(profilePath));
        String line = reader.readLine(); //ignore first line, it's already included.
        while ((line = reader.readLine()) != null) {
            toWrite += line + System.getProperty("line.separator");
        }
        reader.close();
        final BufferedWriter writer = new BufferedWriter(new FileWriter(profilePath));
        writer.write(toWrite);
        writer.close();
    }

    /**
     * Displays the player's stats.
     */
    public void displayProfileStats() {
        if (playerName == null) {
            JOptionPane.showMessageDialog(null, "You are not logged in.", "Error",
                    JOptionPane.PLAIN_MESSAGE);
            return;
        }
        String toDisplay = "<html>";
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(profilePath));
            final String split[] = reader.readLine().split(" ");
            reader.close();
            toDisplay += "Levels completed: " + split[0];
            toDisplay += "<br>High score: " + split[1];
            toDisplay += "<br>Ghosts killed: " + split[2];
            toDisplay += "<br>Fruits eaten: " + split[3];
            toDisplay += "<br>Times killed by Blinky: " + split[4];
            toDisplay += "<br>Times killed by Pinky: " + split[5];
            toDisplay += "<br>Times killed by Inky: " + split[6];
            toDisplay += "<br>Times killed by Clyde: " + split[7];
            toDisplay = parseAchievements(toDisplay);
            toDisplay += "</html>";

            JOptionPane.showMessageDialog(null, toDisplay, "Statistics",
                    JOptionPane.PLAIN_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the current player's username.
     *
     * @return The current player's username.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Sets the player's name.
     */
    public void setPlayerName() {
        playerName = "Testy";
    }

    /**
     * Returns the highest level ever reached by the player.
     *
     * @return said level.
     */
    public int getMaxLevelReached() {
        int i = 0;
        try {
            i = Integer.parseInt(getInfoLine()[0]);
            System.out.println("max level reached: " + i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return i;
    }
}
