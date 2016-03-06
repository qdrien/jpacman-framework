package nl.tudelft.jpacman.level;

import java.io.*;
import java.util.Arrays;
import java.util.Map;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Unit;
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
     */
    public void authenticate()
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
            if (choice != 0) return;
            playerName = loginEntered.getText();
        }while(!checkLoginInfo(passEntered.getPassword()));
        JOptionPane.showMessageDialog(null, "You are now logged in as " + playerName, "Login successful", JOptionPane.PLAIN_MESSAGE);
        //Security precaution
        Arrays.fill(passEntered.getPassword(), '0');
    }

    /**
     * Creates new player profile.
     */
    public void createNewPlayer()
    {
        boolean alreadyExists;
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
                alreadyExists = checkUsername(playerName);
            }while (alreadyExists);
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
//        writer = new BufferedWriter(new FileWriter(""));
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
                char pass[] = split[1].toCharArray();
                final int a = Arrays.hashCode(passEntered), b = Integer.parseInt(split[1]);
                if (login.equals(playerName) && a == b)
                {
                    return true;
                }
                line = reader.readLine();
            }
            reader.close();
        }
        catch (IOException e)
        {
            System.out.println("Error whilst reading login.txt "+e.getMessage());
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
}
