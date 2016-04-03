package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Player extends Unit
{

    /**
     * The amount of points the player needs to get an additional life
     * (he gets one each time he reaches a multiple of this value)
     */
    private static final int NEW_LIFE_THRESHOLD = 10000;
    /**
     * The base movement interval.
     */
    private static final int MOVE_INTERVAL = 200;
    /**
     * The animations for every direction.
     */
    protected final Map<Direction, Sprite> sprites;
    /**
     * The animation that is to be played when Pac-Man dies.
     */
    protected final AnimatedSprite deathSprite;
    /**
     * The ArrayList of listeners that will be called when a player-related event occurs
     */
    protected final List<PlayerListener> listeners;
    /**
     * The amount of points accumulated by this player.
     */
    protected int score;
    /**
     * <code>true</code> iff this player is alive.
     */
    protected boolean alive;
    /**
     * The name of the current player and the path to the file storing the player's stats.
     */
    protected String playerName;
    /**
     * The number of lives left
     */
    private int lives = 3;
    /**
     * Whether the player has eaten a superpellet or not. (for future use, superpellet not implemented)
     */
    private boolean poweredUp; //booleans are initialised to false by default.

    public Player(Map<Direction, Sprite> spriteMap, AnimatedSprite deathAnimation)
    {
        this.alive = true;
        listeners = new ArrayList<>();
        this.sprites = spriteMap;
        this.score = 0;
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
     * @param isAlive <code>true</code> iff this player is alive.
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
     * @param points The amount of points to add to the points this player already
     *               has.
     */
    public void addPoints(int points) {
        //Simply uses integer division (if we have different results, a threshold has been reached)
        //Note that this can only work if the amount of points a player can get in one go is < the threshold
        if (score / NEW_LIFE_THRESHOLD != (score + points) / NEW_LIFE_THRESHOLD) addLife();
        score += points;
    }

    /**
     * Returns whether pacman has eaten a superpellet or not.
     *
     * @return Whether pacman has eaten a superpellet or not.
     */
    public boolean isPoweredUp() {//PoweredUp is always false for now, since the "superpellet" isn't implemented.
        return poweredUp;
    }

    /**
     * Get the interval to move the player periodically
     *
     * @return the player interval
     */
    public long getInterval() {
        return MOVE_INTERVAL;
    }

    /**
     * Removes one life from the player and check if he has none left afterwards
     */
    public void loseLife() {
        lives--;
        if (lives == 0) {
            setAlive(false);
        } else {
            //call the associated listeners (can only be one Level?)
            listeners.forEach(l -> l.onPlayerLoseLife((IdentifiedPlayer) this));
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
     *
     * @return The amount of lives left
     */
    public int getLives() {
        return lives;
    }

    /**
     * Simple setter for the lives field
     *
     * @param lives The amount of lives the player will have after the method call
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    /**
     * Registers the given Level to the player so that he can communicate with it when needed
     *
     * @param level The given Level that the player is playing on
     */
    public void register(Level level) {
        listeners.add(level);
    }

    /**
     * Unregisters the given Level (if he was registered) so that it is no longer called when an event occurs
     *
     * @param level The given Level that has to be removed
     */
    public void unregister(Level level) {
        listeners.remove(level);
    }

    /**
     * Resets the score (to 0)
     */
    public void resetScore() {
        score = 0;
    }
}
