package nl.tudelft.jpacman.level;

import java.util.Map;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;

/**
 * A player operated unit in our game.
 *
 * @author Jeroen Roosen
 */
public class Player extends Unit {

    /**
     * The amount of points the player needs to get an additional life
     * (he gets one each time he reaches a multiple of this value)
     */
    public static final int NEW_LIFE_THRESHOLD = 10000;
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
	 * The number of lives left
	 */
	private int lives = 3;

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
        checkNewLifeThreshold(points);
		score += points;
    }

    /**
     * Checks whether a player has reached the "new life threshold" allowing him to get an additional life
     * @param points The amount of points that are going to be added
     */
    private void checkNewLifeThreshold(int points) {
        //Simply uses integer division (if we have different results, a threshold has been reached)
        //Note that this can only work if the amount of points a player can get in one go is < the threshold
        if (score / NEW_LIFE_THRESHOLD != (score + points) / NEW_LIFE_THRESHOLD) addLife();
    }

    /**
	 * Removes one life from the player and check if he has none left afterwards
	 */
	public void loseLife() {
		lives--;
		if (lives == 0) setAlive(false);
        else {
            //TODO: teleport the player
        }
	}

    /**
     * Simply adds one life to the player
     */
    public void addLife() {
        lives++;
    }

    /**
     * Simple getter for the lives field
     * @return The amount of lives left
     */
    public int getLives() {
        return lives;
    }

    /**
     * Simple setter for the lives field
     * @param lives The amount of lives the player will have after the method call
     */
    public void setLives(int lives) {
        this.lives = lives;
    }
}
