package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Unit;
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
public class Player extends Unit {

	/**
	 * The amount of points accumulated by this player.
	 */
	private int score;

	/**
	 * The animations for every direction.
	 */
	private final Map<Direction, Sprite> sprites;

	/**
	 * The animation that is to be played when Pac-Man dies.
	 */
	private final AnimatedSprite deathSprite;

	/**
	 * <code>true</code> iff this player is alive.
	 */
	private boolean alive;

    /**
     * The maximum size of the player's username and password.
     */
    private static final int MAX_LOGIN_LENGTH = 25, MAX_PASS_LENGTH = 15;

    /**
     * The path of the file containing usernames and passwords.
     */
    private static final String LOGIN_PATH = new File("").getAbsolutePath() + "/src/main/resources/login.txt";

    /**
     * The name of the current player and the path to the file storing the player's stats.
     */
    private String playerName, profilePath;

    /**
     * Whether the player has eaten a superpellet or not. (for future use, superpellet not implemented)
     */
    private boolean poweredUp; //booleans are initialised to false by default.

    /**
     * Whether the application is running or whether it's being tested.
     */
    private static boolean isNotATest = true;

    /**
	 * Creates a new player with a score of 0 points.
	 * 
	 * @param spriteMap
	 *            A map containing a sprite for this player for every direction.
	 * @param deathAnimation
	 *            The sprite to be shown when this player dies.
	 */
	Player(Map<Direction, Sprite> spriteMap, AnimatedSprite deathAnimation) {
		this.score = 0;
		this.alive = true;
		this.sprites = spriteMap;
		this.deathSprite = deathAnimation;
		deathSprite.setAnimating(false);
	}

    /**
     * Returns the path of the file containing usernames and passwords.
     * @return The path of the file containing usernames and passwords.
     */
    public String getLoginPath()
    {
        return LOGIN_PATH;
    }

    /**
     * Authenticates existing player profile.
     * @return Whether the identification was carried through or cancelled.
     */
    public boolean authenticate()
    {
        String options[] = {"Ok", "Cancel"};
        JPanel panel = new JPanel();
        JLabel loginLabel = new JLabel("Login: "), passLabel = new JLabel("Password: ");
        JTextField loginEntered = new JTextField(MAX_LOGIN_LENGTH);
        JPasswordField passEntered = new JPasswordField(MAX_PASS_LENGTH);
        panel.add(loginLabel);
        panel.add(loginEntered);
        panel.add(passLabel);
        panel.add(passEntered);
        do
        {
            int choice = JOptionPane.showOptionDialog(null, panel, "Identification", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (choice != 0) return false;
            playerName = loginEntered.getText();
        }while(!checkLoginInfo(passEntered.getPassword()));
        setProfilePath();
        JOptionPane.showMessageDialog(null, "You are now logged in as " + playerName, "Login successful", JOptionPane.PLAIN_MESSAGE);
        //Security precaution
        Arrays.fill(passEntered.getPassword(), '0');
        return true;
    }

    /**
     * Sets the path to the file storing the player's stats. (default version)
     */
    private void setProfilePath()
    {
        profilePath = new File("").getAbsolutePath()+"/src/main/resources/profiles/" + playerName + ".prf";
    }

    /**
     * Sets the path to the file storing the player's stats.
     * @param s The path to set.
     */
    public void setProfilePath(String s)
    {
        profilePath = s;
    }

    /**
     * Displays the player's achievements, if any were obtained.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    public void displayAchievements()
    {
        if (displayChoiceBox(new String[]{"Yes", "No"}, "Display Achievements?", "Query") != 0) return;
        String toDisplay = "<html>";
        try
        {
            toDisplay = parseAchievements(toDisplay);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        toDisplay += "</html>";
        if ("<html></html>".equals(toDisplay)) JOptionPane.showMessageDialog(null, "No achievements earned yet.", "Awww", JOptionPane.PLAIN_MESSAGE);
        else displayChoiceBox(new String[]{"Ok"}, toDisplay, "Achievements");

    }

    /**
     * Reads the player's achievements from file.
     * @param achievements the list of achievements.
     * @return  The updated list of achievements.
     * @throws IOException If the file was not found or is not readable.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    private String parseAchievements(String achievements) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(profilePath));
        String achievementName = reader.readLine(); //first line ignored, it contains other other information
        achievements += "<br>Achievements: <br>";
        while ((achievementName = reader.readLine()) != null)
        {
            achievements += achievementName + ": " + Achievement.parseAchievement(achievementName).getDescription() + "<br>";
        }
        reader.close();
        return achievements;
    }

    /**
     * Displays an option dialog.
     * @param options The buttons that can be clicked.
     * @param label The text that will be displayed.
     * @param title The title of the dialog.
     * @return The index of the button that was clicked.
     */
    private int displayChoiceBox(String[] options, String label, String title)
    {
        JPanel panel = new JPanel();
        JLabel text = new JLabel(label);
        panel.add(text);
        return JOptionPane.showOptionDialog(null, panel, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    }

    /**
     * Adds an achievement to the player's profile file.
     * @param achievement The achievement to add.
     */
    public void addAchievement(Achievement achievement)
    {
        //If the achievement has already been obtained by this player (or the player isn't logged in), don't add it.
        if (playerName == null || checkAchievement(achievement)) return;
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(profilePath, true));
            writer.write(achievement + System.getProperty("line.separator"));
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        int bonus = achievement.getBonusScore();
        score += bonus;
        if (isNotATest) JOptionPane.showMessageDialog(null, "Achievement unlocked: " + achievement + ", gained " + bonus + " points.", "Congratulations", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Checks whether an achievement has already been earned by the player.
     * @param achievement the achievement to check.
     * @return Whether the achievement has already been earned or not.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    private boolean checkAchievement(Achievement achievement)
    {
        boolean found = false;
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(profilePath));
            String line = reader.readLine(); //the first line is ignored, since it contains other information.
            while ((line = reader.readLine()) != null)
            {
                //Removing whitespace just in case the file has been manually edited.
                line = line.replaceAll("\\s+", "");
                if (line.equals(achievement.toString())) found = true;
            }
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return found;
    }

    /**
     * Creates new player profile.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    public void createNewPlayer()
    {
        String options[] = {"Ok", "Cancel"};
        JPanel panel = new JPanel();
        JLabel loginLabel = new JLabel("Login: "), passLabel = new JLabel("Password: ");
        JTextField loginEntered = new JTextField(MAX_LOGIN_LENGTH);
        JPasswordField passEntered = new JPasswordField(MAX_PASS_LENGTH);
        panel.add(loginLabel);
        panel.add(loginEntered);
        panel.add(passLabel);
        panel.add(passEntered);
        try
        {
            int choice = 0;
            do
            {
                if (isNotATest) choice = JOptionPane.showOptionDialog(null, panel, "Profile creation", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if (choice != 0) return;
                playerName = loginEntered.getText();
            }while (checkUsername(playerName));
            char pass[] = passEntered.getPassword();
            createProfile(pass);
            JOptionPane.showMessageDialog(null, "Profile created", "Success", JOptionPane.PLAIN_MESSAGE);
            //Security precaution
            Arrays.fill(pass, '0');
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Creates the player's profile file, and initialises it. Also adds the player's identifying information to the login file.
     * @param pass the player's desired password.
     * @throws IOException If the login file cannot be written to or the player's profile file cannot be created.
     */
    private void createProfile(char pass[]) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(LOGIN_PATH, true));
        writer.write(playerName + " " + Arrays.hashCode(pass) + "\n");
        writer.close();
        //Creating "profiles" subdirectory if necessary.
        new File(new File("").getAbsolutePath()+"/src/main/resources/profiles").mkdir();
        //Creating the profile file for the new user.
        setProfilePath();
        writer = new BufferedWriter(new FileWriter(profilePath));
        //0 levels completed, 0 high score achieved, 0 fruits eaten, 0 ghosts killed, 0 times killed by Blinky, 0 times killed by Pinky, 0 times killed by Inky, 0 times killed by Clyde.
        writer.write("0 0 0 0 0 0 0 0" + System.getProperty("line.separator"));
        writer.close();
    }

    /**
     * Checks whether a user already exists with the username desired by the player.
     * @param name the name to check.
     * @return Whether the name is already in use or not.
     * @throws IOException If the login file cannot be found or read.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    private boolean checkUsername(String name) throws IOException
    {
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(LOGIN_PATH));
        while ((line = reader.readLine()) != null)
        {
            if (name.equals(line.split(" ")[0]))
            {
                JOptionPane.showMessageDialog(null, "Profile already exists", "Error", JOptionPane.PLAIN_MESSAGE);
                return true;
            }
        }
        reader.close();
        return false;
    }

    /**
     * Checks whether the player correctly identified himself.
     * @param passEntered the password entered by the player.
     * @return Whether the identifying info is correct or not.
     */
    private boolean checkLoginInfo(char passEntered[])
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(LOGIN_PATH));
            String line = reader.readLine();
            while (line != null)
            {
                String split[] = line.split(" ");
                String login = split[0];
                if (login.equals(playerName) && Arrays.hashCode(passEntered) == Integer.parseInt(split[1])) return true;
                line = reader.readLine();
            }
            reader.close();
        }
        catch (IOException e)
        {
            System.err.println("Error whilst reading login.txt " + e.getMessage());
        }
        JOptionPane.showMessageDialog(null, "Username and/or password is erroneous", "Error", JOptionPane.PLAIN_MESSAGE);
        return false;
    }

    /**
     * Triggered whenever the player completes a game.
     */
    public void levelCompleted()
    {
        if (playerName == null) return;
        try
        {
            String split[] = readInfoLine();
            int levelsCompleted = Integer.parseInt(split[0]) + 1;
            String result = "";
            for (int i = 1; i < split.length; i++) result += split[i] + " ";
            updateInfoLine(levelsCompleted + result);
            if (levelsCompleted >= 3) addAchievement(Achievement.WON_THRICE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Reads the first line of the player's profile file, which contains various information.
     * @return The line, split into its components.
     * @throws IOException If the file cannot be found or read.
     */
    private String[] readInfoLine() throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(profilePath));
        String split[] = reader.readLine().split(" ");
        reader.close();
        return split;
    }

    /**
     * Triggered whenever the player dies.
     * @param killer The ghost that killed pacman.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    public void killedBy(GhostColor killer)
    {
        if (playerName == null) return;
        int toAlter;
        switch (killer)
        {
            case RED:
                toAlter = 4;
                addAchievement(Achievement.SPEEDY_DEATH);
                break;
            case PINK:
                toAlter = 5;
                addAchievement(Achievement.AMBUSHED);
                break;
            case CYAN:
                toAlter = 6;
                break;
            case ORANGE:
                toAlter = 7;
                break;
            default:
                return;
        }
         try
        {
            String split[] = readInfoLine(), toWrite = "";
            for (int i = 0; i < split.length; i++)
            {
                if (i == toAlter) toWrite += Integer.parseInt(split[i]) + 1 + " ";
                else toWrite += split[i] + " ";
            }
            updateInfoLine(toWrite);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Saves the player's highest scores and checks whether it's high enough to earn him an achievement.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    public void saveScore()
    {
        if (playerName == null) return;
        try
        {
            String split[] = readInfoLine(), toWrite = "";
            int highScore = Integer.parseInt(split[1]);
            if (score > 9000) addAchievement(Achievement.OVER_9000);
            if (score > highScore) highScore = score;
            for (int i = 0; i < split.length; i++)
            {
                if (i == 1) toWrite += highScore + " ";
                else toWrite += split[i] + " ";
            }
            updateInfoLine(toWrite);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Updates all the player's info.
     * @param toWrite The info to write to the player's profile file.
     * @throws IOException If the file cannot be written to.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    private void updateInfoLine(String toWrite) throws IOException
    {
        toWrite += System.getProperty("line.separator");
        BufferedReader reader = new BufferedReader(new FileReader(profilePath));
        String line = reader.readLine(); //ignore first line, it's already included.
        while ((line = reader.readLine()) != null)
        {
            toWrite += line + System.getProperty("line.separator");
        }
        reader.close();
        BufferedWriter writer = new BufferedWriter(new FileWriter(profilePath));
        writer.write(toWrite);
        writer.close();
    }

    /**
     * Displays the player's stats.
     */
    public void displayProfileStats()
    {
        if (playerName == null)
        {
            JOptionPane.showMessageDialog(null, "You are not logged in.", "Error", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        String toDisplay = "<html>";
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(profilePath));
            String split[] = reader.readLine().split(" ");
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

            JOptionPane.showMessageDialog(null, toDisplay, "Statistics", JOptionPane.PLAIN_MESSAGE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Returns the current player's username.
     * @return The current player's username.
     */
    public String getPlayerName()
    {
        return playerName;
    }

	/**
	 * Returns whether this player is alive or not.
	 * 
	 * @return <code>true</code> iff the player is alive.
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Sets whether this player is alive or not.
	 * 
	 * @param isAlive
	 *            <code>true</code> iff this player is alive.
	 */
	public void setAlive(boolean isAlive) {
		if (isAlive) {
			deathSprite.setAnimating(false);
		}
		if (!isAlive) {
			deathSprite.restart();
		}
		this.alive = isAlive;
	}

    /**
     * Sets whether the application is running or being test.
     * @param noTest Whether the application is running or being test.
     */
    public static void setIsNotATest(boolean noTest)
    {
        isNotATest = noTest;
    }

    /**
	 * Returns the amount of points accumulated by this player.
	 * 
	 * @return The amount of points accumulated by this player.
	 */
	public int getScore() {
		return score;
	}

	@Override
	public Sprite getSprite() {
		if (isAlive()) {
			return sprites.get(getDirection());
		}
		return deathSprite;
	}

	/**
	 * Adds points to the score of this player.
	 * 
	 * @param points
	 *            The amount of points to add to the points this player already
	 *            has.
	 */
	public void addPoints(int points) {
		score += points;
	}

    /**
     * Returns whether pacman has eaten a superpellet or not.
     * @return Whether pacman has eaten a superpellet or not.
     */
    public boolean isPoweredUp()
    {//PoweredUp is always false for now, since the "superpellet" isn't implemented.
        return poweredUp;
    }

    /**
     * Sets the player's name.
     * @param s The name to set.
     */
    public void setPlayerName(String s)
    {
        playerName = s;
    }
}
