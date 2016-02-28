package nl.tudelft.jpacman.game;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
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
	private boolean inProgress;

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
    private ScheduledExecutorService service;
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
            if(service != null)
            {
                service.shutdownNow();
            }
			inProgress = false;
			getLevel().stop();
		}
	}

	/**
     *
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
	public void move(Player player, Direction direction)
    {
		if (isInProgress())
        {
            Square location = player.getSquare();
            Square destination = location.getSquareAt(direction);
            if(destination.isAccessibleTo(player))
            {
                if(service != null)
                {
                    service.shutdownNow();
                }
                this.service = Executors.newSingleThreadScheduledExecutor();
                service.schedule(new PlayerMoveTask(service, player, direction), player.getInterval(), TimeUnit.MILLISECONDS);
            }
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


    private final class PlayerMoveTask implements Runnable
    {

        /**
         * The service executing the task.
         */
        private final ScheduledExecutorService s;

        /**
         * The player to move.
         */
        private final Player player;
        private final Direction dir;
        /**
         * Creates a new task.
         *
         * @param s
         *            The service that executes the task.
         * @param p
         *            The player to move.
         */
        private PlayerMoveTask(ScheduledExecutorService s, Player p, Direction direction) {
            this.s = s;
            this.player = p;
            this.dir = direction;
        }

        @Override
        public void run()
        {
            long interval = player.getInterval();
            s.schedule(this, interval, TimeUnit.MILLISECONDS);
            getLevel().move(player, dir);


        }
    }

}
