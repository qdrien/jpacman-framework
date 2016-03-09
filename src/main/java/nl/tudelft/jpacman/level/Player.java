package nl.tudelft.jpacman.level;

import java.io.*;
import java.util.Arrays;
import java.util.Map;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.game.Achievement;
import nl.tudelft.jpacman.npc.ghost.GhostColor;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;

import javax.swing.*;

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

    private static final int MAX_LOGIN_LENGTH = 25, MAX_PASS_LENGTH = 15;
    private static final String LOGIN_PATH = new File("").getAbsolutePath() + "/src/main/resources/login.txt";

    private String playerName, profilePath;

    private boolean poweredUp = false;

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

    private void setProfilePath()
    {
        profilePath = new File("").getAbsolutePath()+"/src/main/resources/profiles/" + playerName + ".prf";
    }

    public void setProfilePath(String s)
    {
        profilePath = s;
    }

    public void getAchievements()
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
        if (toDisplay.equals("<html></html>")) JOptionPane.showMessageDialog(null, "No achievements obtained yet.", "Awww", JOptionPane.PLAIN_MESSAGE);
        else displayChoiceBox(new String[]{"Ok"}, toDisplay, "Achievements");

    }

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

    private int displayChoiceBox(String[] options, String label, String title)
    {
        JPanel panel = new JPanel();
        JLabel text = new JLabel(label);
        panel.add(text);
        return JOptionPane.showOptionDialog(null, panel, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    }

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
        JOptionPane.showMessageDialog(null, "Achievement unlocked: " + achievement + ", gained " + bonus + " points.", "Congratulations", JOptionPane.PLAIN_MESSAGE);
    }

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
            do
            {
                int choice = JOptionPane.showOptionDialog(null, panel, "Profile creation", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
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

    private void newPassword()
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(LOGIN_PATH));
            String line = reader.readLine(), toWrite = "";
            while (line != null)
            {
                String login[] = line.split(" ");
                if (playerName.equals(login[0]))
                {
                    String newPwd = JOptionPane.showInputDialog(null, "Enter new password", "Password change", JOptionPane.PLAIN_MESSAGE);
                    toWrite += playerName + " " + Arrays.hashCode(newPwd.toCharArray()) + "\n";
                }
                else
                {
                    toWrite += line + "\n";
                }
                line = reader.readLine();
            }
            reader.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter(LOGIN_PATH));
            writer.write(toWrite);
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void levelCompleted()
    {
        if (playerName == null) return;
        try
        {
            String split[] = readInfoLine();
            int levelsCompleted = Integer.parseInt(split[0]) + 1;
            updateInfoLine(levelsCompleted + Arrays.toString(Arrays.copyOfRange(split, 1, split.length)));
            if (levelsCompleted >= 3) addAchievement(Achievement.WON_THRICE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String[] readInfoLine() throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(profilePath));
        String split[] = reader.readLine().split(" ");
        reader.close();
        return split;
    }

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

    public boolean isPoweredUp()
    {//PoweredUp is always false for now, since the "superpellet" isn't implemented.
        return poweredUp;
    }

    public void setPlayerName(String s)
    {
        playerName = s;
    }
}
