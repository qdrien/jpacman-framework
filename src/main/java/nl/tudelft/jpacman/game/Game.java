package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.PacmanConfigurationException;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.level.AILevel;
import nl.tudelft.jpacman.level.IdentifiedPlayer;
import nl.tudelft.jpacman.level.Level.LevelObserver;
import nl.tudelft.jpacman.level.MapParser;
import nl.tudelft.jpacman.strategy.PacmanStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A basic implementation of a Pac-Man game.
 *
 * @author Jeroen Roosen
 */
public abstract class Game implements LevelObserver {
    /**
     * Object that locks the start and stop methods.
     */
    private final Object progressLock = new Object();
    /**
     * The current level id.
     */
    private int currentLevel;
    /**
     * <code>true</code> if the game is in progress.
     */
    private boolean inProgress, firstPass = true;
    /**
     * The chosen strategy by the player.
     */
    private PacmanStrategy strategy;
    /**
     * For the execution of the thread for the continuousMovement method.
     */
    private PlayerMoveTask currentMoveTask;
    private ScheduledExecutorService service;

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
            if (strategy != null) {
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
    public void stop() {
        synchronized (progressLock) {
            if (!isInProgress()) {
                return;
            }
            if (service != null) {
                currentMoveTask.setFinished();
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
    public abstract List<IdentifiedPlayer> getPlayers();

    /**
     * @return The level currently being played.
     */
    public abstract AILevel getLevel();

    /**
     * Forces subclasses to provide a method to switch to the given Level.
     *
     * @param level The Level we want to switch to
     */
    protected abstract void setLevel(AILevel level);

    /**
     * Sets the level to the one that has the given id (calls #Game.setLevel).
     *
     * @param levelIndex The id of the level we want to switch to
     */
    public void setLevel(final int levelIndex) {
        final AILevel level = makeLevel(levelIndex);
        assert level != null;
        setLevel(level);
        currentLevel = levelIndex;
    }

    /**
     * Moves the specified player until the next cross in the given direction.
     *
     * @param player    The player to move.
     * @param direction The direction to move in.
     */
    public void continousMovement(IdentifiedPlayer player, Direction direction) {
        if (isInProgress()) {
            final Square location = player.getSquare();
            final Square destination = location.getSquareAt(direction);
            if (destination.isAccessibleTo(player)) {
                if (service == null) {
                    this.service = Executors.newSingleThreadScheduledExecutor();
                } else {
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
     * @param player    The player to move.
     * @param direction The direction to move in.
     */
    public void move(IdentifiedPlayer player, Direction direction) {
        if (isInProgress()) {
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
            final IdentifiedPlayer player = getPlayers().get(0);
            HallOfFame.setIsNotATest(true);
            try {
                player.saveScore();
            } catch (IOException e) {
                e.printStackTrace();
            }
            new HallOfFame().handleHoF(player.getScore(), player.getPlayerName());
        }
        firstPass = false;
    }

    /**
     * Set the Strategy.
     *
     * @param strategy the strategy to set
     */
    public void setStrategy(PacmanStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Forces subclasses to provide a method allowing to
     * reset the score and the number of lives the player has
     * (should be called when a new level is set "manually").
     */
    public abstract void reset();

    /**
     * Creates a new level. Uses the map parser to
     * parse the desired board file.
     *
     * @param id The id of the level we want to load
     * @return A new level.
     */
    protected AILevel makeLevel(final int id) {
        final MapParser parser = getMapParser();
        final String file = "/board" + id + ".txt";
        try (InputStream boardStream = Launcher.class
                .getResourceAsStream(file)) {
            if (boardStream == null) return null;
            currentLevel = id;
            return parser.parseMap(boardStream);
        } catch (IOException e) {
            throw new PacmanConfigurationException("Unable to create level.", e);
        }
    }

    /**
     * @return A new map parser object using the factories from
     * {@link Launcher#getLevelFactory()} and {@link Launcher#getBoardFactory()}.
     */
    private MapParser getMapParser() {
        return new MapParser(Launcher.getLevelFactory(), Launcher.getBoardFactory());
    }

    /**
     * Returns the next level and increments the currentLevel field.
     * If this was already the last level, simply restart it.
     *
     * @return The new(and next) level
     */
    public AILevel nextLevel() {
        AILevel level = makeLevel(++currentLevel);
        if (level == null) {
            //the level could not be loaded, this means that the previous one was the final level
            //restart this last level (loop until player dies)
            //(this level can't be finished without loosing at least one life
            // so that there will be an end)
            level = makeLevel(--currentLevel);
        }
        assert level != null;
        return level;
    }

    /**
     * Simple getter for currentLevel.
     *
     * @return The id of the current level
     */
    protected int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Class representing the timer and methods to apply during the timer.
     */
    private final class PlayerMoveTask implements Runnable {

        /**
         * The service executing the task.
         */
        private final ScheduledExecutorService scheduledExecutorService;


        /**
         * The player to move.
         */
        private final IdentifiedPlayer player;
        /**
         * The direction to follow by the player.
         */
        private final Direction dir;
        /**
         * A boolean to know if the current task is finished or not.
         */
        private boolean finished;

        /**
         * Creates a new task.
         *
         * @param s         The service that executes the task.
         * @param p         The player to move.
         * @param direction The direction to follow
         */
        PlayerMoveTask(ScheduledExecutorService s, IdentifiedPlayer p, Direction direction) {
            this.scheduledExecutorService = s;
            this.player = p;
            this.dir = direction;
        }

        /**
         * The run method to apply periodically.
         */
        @Override
        public void run() {
            if (!isFinished()) {
                getLevel().move(player, dir);
                scheduledExecutorService.schedule(this,
                        player.getInterval(), TimeUnit.MILLISECONDS);
            }
        }

        /**
         * Boolean to finish the task for the thread.
         */
        public void setFinished() {
            this.finished = true;
        }


        /**
         * Get the boolean to know if the task is finish or not.
         *
         * @return true if the task is finished, false otherwise
         */
        public boolean isFinished() {
            return finished;
        }

    }
}
