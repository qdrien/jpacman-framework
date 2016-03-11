package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.Level.LevelObserver;
import nl.tudelft.jpacman.level.Player;

import java.util.List;

/**
 * A basic implementation of a Pac-Man game.
 * 
 * @author Jeroen Roosen 
 */
public abstract class Game implements LevelObserver {

	/**
	 * <code>true</code> if the game is in progress.
	 */
	private boolean inProgress;

	/**
	 * Object that locks the start and stop methods.
	 */
	private final Object progressLock = new Object();

	/**
	 * The Launcher that created this Game instance
	 */
	protected Launcher launcher;

	/**
	 * Creates a new game.
	 */
	protected Game() {
		inProgress = false;
	}

	/**
	 * Starts or resumes the game.
	 */
	public void start() {
		synchronized (progressLock) {
			if (isInProgress()) {
				return;
			}
			if (getLevel().isAnyPlayerAlive()
					&& getLevel().remainingPellets() > 0) {
				inProgress = true;
				getLevel().addObserver(this);
				getLevel().start();
			}
		}
	}

	/**
	 * Pauses the game.
	 */
	public void stop() {
		synchronized (progressLock) {
			if (!isInProgress()) {
				return;
			}
			inProgress = false;
			getLevel().stop();
		}
	}

	/**
	 * @return <code>true</code> iff the game is started and in progress.
	 */
	public boolean isInProgress() {
		return inProgress;
	}

	/**
	 * @return An immutable list of the participants of this game.
	 */
	public abstract List<Player> getPlayers();

	/**
	 * @return The level currently being played.
	 */
	public abstract Level getLevel();

	/**
	 * Moves the specified player one square in the given direction.
	 * 
	 * @param player
	 *            The player to move.
	 * @param direction
	 *            The direction to move in.
	 */
	public void move(Player player, Direction direction) {
		if (isInProgress()) {
			// execute player move.
			getLevel().move(player, direction);
		}
	}

	@Override
	public void levelWon() {
		stop();
	}
	
	@Override
	public void levelLost() {
		stop();
	}

    /**
     * Simple setter for the launcher that created the Game instance
     * @param launcher The launcher that created the Game instance
     */
	public void setLauncher(final Launcher launcher) {
		this.launcher = launcher;
	}

    /**
     * Simple getter for the Launcher field
     * @return The Launcher that created the Game instance
     */
	public Launcher getLauncher() {
		return launcher;
	}

    /**
     * Forces subclasses to provide a method to switch to the given Level
     * @param level The Level we want to switch to
     */
	public abstract void setLevel(Level level);

    /**
     * Forces subclasses to provide a method to reset the score and the number of lives the player has
     * (should be called when a new level is set "manually")
     */
    public abstract void reset();
}
