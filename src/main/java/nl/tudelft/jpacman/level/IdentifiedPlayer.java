package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.FileChecker;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.game.Achievement;
import nl.tudelft.jpacman.npc.ghost.GhostColor;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;

import javax.swing.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

/**
 * An identified player - i.e. a player that has been logged in and therefore has a profile.
 */
@SuppressWarnings({"checkstyle:linelength", "PMD.AvoidDuplciateLiterals"})
public class IdentifiedPlayer extends Player {

    /**
     * The maximum size of the player's username and password.
     */
    private static final int MAX_LOGIN_LENGTH = 25, MAX_PASS_LENGTH = 15;

    /**
     * The path of the file containing usernames and passwords.
     */
    private static final String LOGIN_PATH = new File("").getAbsolutePath() + "/src/main/resources/login.txt";

    /**
     * Whether the application is running or whether it's being tested.
     */
    private static boolean isNotATest = true;

    /**
     * Path of the player's profile file.
     */
    private String profilePath;

    /**
     * The name of the current player and the path to the file storing the player's stats.
     */
    private String playerName;

    /**
     * Creates a new player.
     *
     * @param spriteMap      A map containing a sprite for this player for every direction.
     * @param deathAnimation The sprite to be shown when this player dies.
     */
    public IdentifiedPlayer(final Map<Direction, Sprite> spriteMap, final AnimatedSprite deathAnimation) {
        super(spriteMap, deathAnimation);
    }

    /**
     * Sets whether the application is running or being tested.
     */
    public static void setIsNotATest() {
        isNotATest = false;
    }

    /**
     * Returns the path of the player's profile file.
     *
     * @return The path of the player's profile file.
     */
    public String getProfilePath() {
        return profilePath;
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
     * Authenticates existing player profile.
     *
     * @return Whether the identification was carried through or cancelled.
     */
    public boolean authenticate() {
        final String[] options = {"Ok", "Cancel"};
        final JPanel panel = new JPanel();
        final JLabel loginLabel = new JLabel("Login: "), passLabel = new JLabel("Password: ");
        final JTextField loginEntered = new JTextField(MAX_LOGIN_LENGTH);
        final JPasswordField passEntered = new JPasswordField(MAX_PASS_LENGTH);
        panel.add(loginLabel);
        panel.add(loginEntered);
        panel.add(passLabel);
        panel.add(passEntered);
        do {
            if (buttonChoice(options, panel, loginEntered, "Identification") != 0) {
                return false;
            }
        } while (!FileChecker.checkLoginInfo(getPlayerName(), passEntered.getPassword()));
        setProfilePath();
        if (isNotATest) {
            JOptionPane.showMessageDialog(null, "You are now logged in as " + getPlayerName(), "Login successful", JOptionPane.PLAIN_MESSAGE);
        }
        //Security precaution
        Arrays.fill(passEntered.getPassword(), '0');
        return true;
    }

    /**
     * Helper method to avoid code duplication.
     *
     * @param options      The buttons that can be clicked.
     * @param panel        The JPanel displaying the choice.
     * @param loginEntered The name entered by the player.
     * @param title        The title of the dialog box.
     * @return The identifier of the button clicked.
     */
    private int buttonChoice(final String[] options, JPanel panel, final JTextField loginEntered, final String title) {
        int choice = 0;
        if (isNotATest) {
            choice = JOptionPane.showOptionDialog(null, panel, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            setPlayerName(loginEntered.getText());
        }
        return choice;
    }

    /**
     * Sets the path to the file storing the player's stats. (default version)
     */
    private void setProfilePath() {
        profilePath = new File("").getAbsolutePath() + "/src/main/resources/profiles/" + getPlayerName() + ".prf";
    }

    /**
     * Displays the player's achievements, if any were obtained.
     *
     * @throws IOException If the file was not found or is not readable.
     */
    public void displayAchievements() throws IOException {
        final String[] options = new String[]{"Yes", "No"};
        final JPanel panel = new JPanel();
        panel.add(new JLabel("Display Achievements?"));
        if (JOptionPane.showOptionDialog(null, panel, "Query", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]) != 0) {
            return;
        }
        String toDisplay = "<html>";
        toDisplay = FileChecker.parseAchievements(profilePath, toDisplay);
        toDisplay += "</html>";
        if ("<html><br>Achievements: <br></html>".equals(toDisplay)) {
            JOptionPane.showMessageDialog(null, "No achievements earned yet.", "Awww", JOptionPane.PLAIN_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, toDisplay, "Achievements", JOptionPane.PLAIN_MESSAGE);
        }
    }

    /**
     * Adds an achievement to the player's profile file.
     *
     * @param achievement The achievement to add.
     * @throws IOException If the file was not found or is not readable.
     */
    public void addAchievement(final Achievement achievement) throws IOException {
        //If the achievement has already been obtained by this player
        // (or the player isn't logged in), don't add it.
        if (getPlayerName() == null || FileChecker.checkAchievement(profilePath, achievement)) {
            return;
        }
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getProfilePath(), true), Charset.defaultCharset()));
        writer.write(achievement + System.getProperty("line.separator"));
        writer.close();
        final int bonus = achievement.getBonusScore();
        setScore(getScore() + bonus);
        if (isNotATest) {
            JOptionPane.showMessageDialog(null, "Achievement unlocked: " + achievement + ", gained " + bonus + " points.", "Congratulations", JOptionPane.PLAIN_MESSAGE);
        }
    }

    /**
     * Creates new player profile.
     */
    @SuppressWarnings({"PMD.DataFlowAnomalyAnalysis", "checkstyle:braces"}) //the initialisations are required.
    public void createNewPlayer() {
        final String[] options = {"Ok", "Cancel"};
        final JPanel panel = new JPanel();
        final JLabel loginLabel = new JLabel("Login: "), passLabel = new JLabel("Password: ");
        final JTextField loginEntered = new JTextField(MAX_LOGIN_LENGTH);
        final JPasswordField passEntered = new JPasswordField(MAX_PASS_LENGTH);
        panel.add(loginLabel);
        panel.add(loginEntered);
        panel.add(passLabel);
        panel.add(passEntered);
        try {
            do {
                if (buttonChoice(options, panel, loginEntered, "Profile creation") != 0) return;
            } while (FileChecker.checkUsername(getPlayerName()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(LOGIN_PATH, true), Charset.defaultCharset()));
            writer.write(getPlayerName() + " " + Arrays.hashCode(passEntered.getPassword()) + "\n");
            writer.close();
            new File(new File("").getAbsolutePath() + "/src/main/resources/profiles").mkdir();
            if (isNotATest) {
                setProfilePath();
                JOptionPane.showMessageDialog(null, "Profile created", "Success", JOptionPane.PLAIN_MESSAGE);
            }
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(profilePath), Charset.defaultCharset()));
            //0 levels completed, 0 high score achieved, 0 fruits eaten, 0 ghosts killed, 0 times killed by Blinky, 0 times killed by Pinky, 0 times killed by Inky, 0 times killed by Clyde.
            writer.write("0 0 0 0 0 0 0 0" + System.getProperty("line.separator"));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Triggered whenever the player completes a game.
     *
     * @param level The id of the level that has been completed.
     * @throws IOException If the file was not found or is not readable.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public void levelCompleted(final int level) throws IOException {
        if (getPlayerName() == null) {
            return;
        }
        final String[] split = getInfoLine();
        final int levelsCompleted = Integer.parseInt(split[0]);
        addAchievement(Achievement.VICTOR);
        if (level > levelsCompleted) {
            final StringBuilder result = new StringBuilder();
            for (int i = 1; i < split.length; i++) {
                result.append(split[i]).append(' ');
            }
            setInfoLine(level + " " + result.toString());
            if (levelsCompleted >= 3) {
                addAchievement(Achievement.WON_THRICE);
            }
        }
    }

    /**
     * Reads the first line of the player's profile file, which contains various information.
     *
     * @return The line, split into its components.
     * @throws IOException If the file cannot be found or read.
     */
    private String[] getInfoLine() throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(profilePath), Charset.defaultCharset()));
        final String[] split = reader.readLine().split(" ");
        reader.close();
        return split;
    }

    /**
     * Updates all the player's info.
     *
     * @param toWrite The info to write to the player's profile file.
     * @throws IOException If the file cannot be written to.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    private void setInfoLine(final String toWrite) throws IOException {
        final StringBuilder builder = new StringBuilder().append(toWrite).append(System.getProperty("line.separator"));
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(profilePath), Charset.defaultCharset()));
        String line = reader.readLine(); //ignore first line, it's already included.
        while ((line = reader.readLine()) != null) {
            builder.append(line).append(System.getProperty("line.separator"));
        }
        reader.close();
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(profilePath), Charset.defaultCharset()));
        writer.write(builder.toString());
        writer.close();
    }

    /**
     * Triggered whenever the player dies.
     *
     * @param killer The ghost that killed pacman.
     * @throws IOException If the file was not found or is not readable.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the DU anomaly warning makes no sense.
    public void killedBy(final GhostColor killer) throws IOException {
        if (getPlayerName() == null) {
            return;
        }
        final String[] split = getInfoLine();
        final StringBuilder toWrite = new StringBuilder();
        final Achievement toGrant = killer.getAchievementGranted();
        for (int i = 0; i < split.length; i++) {
            if (i == killer.getIndex()) {
                toWrite.append(Integer.parseInt(split[i]) + 1).append(' ');
            } else {
                toWrite.append(split[i]).append(' ');
            }
        }
        setInfoLine(toWrite.toString());
        if (toGrant != null) {
            addAchievement(toGrant);
        }
    }

    /**
     * Saves the player's highest scores and checks.
     * whether it's high enough to earn him an achievement.
     *
     * @throws IOException If the file was not found or is not readable.
     */
    @SuppressWarnings({"PMD.DataFlowAnomalyAnalysis", "checkstyle:magicnumber"})
    //the initialisations are required.
    public void saveScore() throws IOException {
        if (getPlayerName() == null) {
            return;
        }
        final String[] split = getInfoLine();
        final StringBuilder toUpdate = new StringBuilder();
        int highScore = Integer.parseInt(split[1]);
        if (getScore() > 9000) {
            addAchievement(Achievement.OVER_9000);
        }
        if (getScore() > highScore) {
            highScore = getScore();
        }
        for (int i = 0; i < split.length; i++) {
            if (i == 1) {
                toUpdate.append(highScore).append(' ');
            } else {
                toUpdate.append(split[i]).append(' ');
            }
        }
        setInfoLine(toUpdate.toString());
    }

    /**
     * Displays the player's stats.
     *
     * @return Whether the player is logged in or not.
     * @throws IOException If the profile file cannot be found or read.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public boolean displayProfileStats() throws IOException {
        if (getPlayerName() == null) {
            JOptionPane.showMessageDialog(null, "You are not logged in.", "Error", JOptionPane.PLAIN_MESSAGE);
            return false;
        }
        String toDisplay = "<html>";

        final String[] split = getInfoLine();
        toDisplay += "Levels completed: " + split[0];
        toDisplay += "<br>High score: " + split[1];
        toDisplay += "<br>Ghosts killed: " + split[2];
        toDisplay += "<br>Fruits eaten: " + split[3];
        toDisplay += "<br>Times killed by Blinky: " + split[4];
        toDisplay += "<br>Times killed by Pinky: " + split[5];
        toDisplay += "<br>Times killed by Inky: " + split[6];
        toDisplay += "<br>Times killed by Clyde: " + split[7];
        toDisplay = FileChecker.parseAchievements(profilePath, toDisplay);
        toDisplay += "</html>";
        JOptionPane.showMessageDialog(null, toDisplay, "Statistics", JOptionPane.PLAIN_MESSAGE);

        return true;
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
     *
     * @param s The name we want the player to have.
     */
    public void setPlayerName(final String s) {
        playerName = s;
    }

    /**
     * Returns the highest level ever reached by the player.
     *
     * @return said level.
     * @throws IOException If the file was not found or is not readable.
     */
    public int getMaxLevelReached() throws IOException {
        return Integer.parseInt(getInfoLine()[0]);
    }

    /**
     * Returns the path of the login file.
     *
     * @return The path of the login file.
     */
    public String getLoginPath() {
        return LOGIN_PATH;
    }
}
