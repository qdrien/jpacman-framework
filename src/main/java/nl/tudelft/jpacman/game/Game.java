package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.Launcher;
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
    private boolean inProgress, firstPass = true;
    /**
     *  The chosen strategy by the player
     */
    private PacmanStrategy strategy;

    // --Commented out by Inspection (29/03/2016 19:09):private PacManUiBuilder builder;//The builder

    /**
     * Object that locks the start and stop methods.
     */
    private final Object progressLock = new Object();

    /**
     * The Launcher that created this Game instance
     */
    Launcher launcher;

    /**
     * Creates a new game.
     */
    protected Game() {
        inProgress = false;
    }

    /**
     * For the execution of the thread for the continuousMovement method
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
                currentMoveTask.setFinished();
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
	public void continousMovement(Player player, Direction direction)
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
                    this.currentMoveTask.setFinished();
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
            getLevel().move(player, direction);
        }
    }

	
    @Override
    public void levelWon() {
        stop();
    }

    @Override
    public void levelLost() {
        if (firstPass) {
            stop();
            final Player player = getPlayers().get(0);
            triggerHoF(player);
        }
        firstPass = false;
    }

    /**
     * Triggers the Hall of Fame handler and eventually updates of the player's score.
     *
     * @param player The player in question.
     */
    private void triggerHoF(Player player) {
        HallOfFame.setIsNotATest(true);
        final HallOfFame hallOfFame = new HallOfFame();
        player.saveScore();
        hallOfFame.handleHoF(player.getScore(), player.getPlayerName());
    }

    /**
     * Simple setter for the launcher that created the Game instance
     *
     * @param launcher The launcher that created the Game instance
     */
    public void setLauncher(final Launcher launcher) {
        this.launcher = launcher;
    }

    /**
     * Simple getter for the Launcher field
     *
     * @return The Launcher that created the Game instance
     */
    public Launcher getLauncher() {
        return launcher;
    }

    /**
     * Forces subclasses to provide a method to switch to the given Level
     *
     * @param level The Level we want to switch to
     */
    public abstract void setLevel(Level level);

// --Commented out by Inspection START (29/03/2016 19:08):
//    /**
//     * Get the builder
//     * @return the builder
//     */
//    public PacManUiBuilder getBuilder() {
//        return builder;
//    }
// --Commented out by Inspection STOP (29/03/2016 19:08)

// --Commented out by Inspection START (29/03/2016 19:09):
//    /**
//     * Set the builder
//     * @param builder the builder to set
//     */
//    public void setBuilder(PacManUiBuilder builder) {
//        this.builder = builder;
//    }
// --Commented out by Inspection STOP (29/03/2016 19:09)

// --Commented out by Inspection START (29/03/2016 19:09):
//    /**
//     * Get the chosen strategy
//     * @return the chosen strategy
//     */
//    public PacmanStrategy getStrategy()
//    {
//        return strategy;
//    }
// --Commented out by Inspection STOP (29/03/2016 19:09)

    /**
     * Set the Strategy
     * @param strategy the strategy to set
     */
    public void setStrategy(PacmanStrategy strategy)
    {
        this.strategy = strategy;
    }


    /**
     * Forces subclasses to provide a method to reset the score and the number of lives the player has
     * (should be called when a new level is set "manually")
     */
    public abstract void reset();
    
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
            if(!finished)
            {
                getLevel().move(player, dir);
                s.schedule(this, interval, TimeUnit.MILLISECONDS);
            }
        }
        /**
         * Boolean to finish the task for the thread
         */
        public void setFinished()
        {
            this.finished = true;
        }

// --Commented out by Inspection START (29/03/2016 19:09):
//        /**
//         * Get the boolean to know if the task is finish or not
//         * @return true if the task is finished, false otherwise
//         */
//        public boolean getFinished()
//        {
//            return finished;
//        }
// --Commented out by Inspection STOP (29/03/2016 19:09)
    }
}
