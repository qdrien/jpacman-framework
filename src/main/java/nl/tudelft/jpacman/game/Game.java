package nl.tudelft.jpacman.game;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.Level.LevelObserver;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.strategy.PacmanStrategy;
import nl.tudelft.jpacman.ui.PacManUiBuilder;

/**
 * A basic implementation of a Pac-Man game.
 * 
 * @author Jeroen Roosen 
 */
public abstract class Game implements LevelObserver {

	/**
	 * <code>true</code> if the game is in progress.
	 */

    /**
     *  The chosen strategy by the player
     */
    private PacmanStrategy strategy;

    private boolean inProgress;
    private PacManUiBuilder builder;//The builder

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
     * For the execution of the thread for the moveContinu method
     */
    private PlayerMoveTask currentMoveTask;
    private ScheduledExecutorService service;
	/**
	 * Starts or resumes the game.
	 */
	public void start()
    {
        synchronized (progressLock)
        {
            if (isInProgress()) {
				return;
			}
            if(strategy != null)
            {
                getLevel().startStrategy(strategy);
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
	public void stop()
    {
		synchronized (progressLock) {
			if (!isInProgress()) {
				return;
			}
            if(service != null)
            {
                currentMoveTask.setFinished(true);
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
	 * Moves the specified player until the next cross in the given direction.
	 * 
	 * @param player
	 *            The player to move.
	 * @param direction
	 *            The direction to move in.
	 */
	public void moveContinu(Player player, Direction direction)
    {
        if (isInProgress())
        {
            Square location = player.getSquare();
            Square destination = location.getSquareAt(direction);
            if(destination.isAccessibleTo(player))
            {
                if(service == null)
                {
                    this.service = Executors.newSingleThreadScheduledExecutor();
                }
                else
                {
                    this.currentMoveTask.setFinished(true);
                }
                this.currentMoveTask = new PlayerMoveTask(service, player, direction);
                service.schedule(currentMoveTask, player.getInterval(), TimeUnit.MILLISECONDS);
            }
        }
    }
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
        if(isInProgress())
        {
            getLevel().move(player,direction);
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
     * Get the builder
     * @return the builder
     */
    public PacManUiBuilder getBuilder() {
        return builder;
    }

    /**
     * Set the builder
     * @param builder the builder to set
     */
    public void setBuilder(PacManUiBuilder builder) {
        this.builder = builder;
    }

    /**
     * Get the chosen strategy
     * @return the chosen strategy
     */
    public PacmanStrategy getStrategy()
    {
        return strategy;
    }

    /**
     * Set the Strategy
     * @param strategy the strategy to set
     */
    public void setStrategy(PacmanStrategy strategy)
    {
        this.strategy = strategy;
    }

    /**
     * Class representing the timer and methods to apply during the timer
     */
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
        /**
         * The direction to follow by the player
         */
        private final Direction dir;
        /**
         * A boolean to know if the current task is finished or not
         */
        private boolean finished = false;

        /**
         * Creates a new task.
         *
         * @param s
         *            The service that executes the task.
         * @param p
         *            The player to move.
         * @param direction
         *             The direction to follow
         */
        private PlayerMoveTask(ScheduledExecutorService s, Player p, Direction direction)
        {
            this.s = s;
            this.player = p;
            this.dir = direction;
        }

        /**
         * The run method to apply periodically
         */
        @Override
        public void run()
        {
            long interval = player.getInterval();
            if(finished == false)
            {
                getLevel().move(player, dir);
                s.schedule(this, interval, TimeUnit.MILLISECONDS);
            }
        }
        /**
         * Boolean to finish the task for the thread
         * @param finished true if the task is finished, false otherwise
         */
        public void setFinished(boolean finished)
        {
            this.finished = finished;
        }

        /**
         * Get the boolean to know if the task is finish or not
         * @return true if the task is finished, false otherwise
         */
        public boolean getFinished()
        {
            return finished;
        }
    }
}
