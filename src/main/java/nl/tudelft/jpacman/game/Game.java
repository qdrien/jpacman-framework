package nl.tudelft.jpacman.game;

import java.util.List;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.Level.LevelObserver;
import nl.tudelft.jpacman.level.Player;

/**
 * A basic implementation of a Pac-Man game.
 * 
 * @author Jeroen Roosen 
 */
public abstract class Game implements LevelObserver {
    /**
	 * <code>true</code> if the game is in progress.
	 */
	private boolean inProgress, firstPass = true;

	/**
	 * Object that locks the start and stop methods.
	 */
	private final Object progressLock = new Object();

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
        if (firstPass)
        {
            stop();
            final Player player = getPlayers().get(0);
            player.addAchievement(Achievement.VICTOR);
            player.levelCompleted();
            triggerHoF(player);
        }
        firstPass = false;
    }
	
	@Override
	public void levelLost() {
        if (firstPass)
        {
            stop();
            final Player player = getPlayers().get(0);
            triggerHoF(player);
        }
        firstPass = false;
	}

    /**
     * Triggers the Hall of Fame handler and eventually updates of the player's score.
     * @param player The player in question.
     */
    private void triggerHoF(Player player)
    {
        HallOfFame.setIsNotATest(true);
        final HallOfFame hallOfFame = new HallOfFame();
        player.saveScore();
        hallOfFame.handleHoF(player.getScore(), player.getPlayerName());
    }
}
