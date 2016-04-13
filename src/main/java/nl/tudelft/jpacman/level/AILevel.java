package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.npc.NPC;
import nl.tudelft.jpacman.npc.ghost.Ghost;
import nl.tudelft.jpacman.strategy.AIStrategy;
import nl.tudelft.jpacman.strategy.PacmanStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A level of Pac-Man. A level consists of the board with the players and the
 * AIs on it. Enables management of AI during a level (Pacman and ghosts)
 */
public class AILevel extends Level {
    /**
     * The NPCs of this level and, if they are running, their schedules.
     */
    private final Map<NPC, ScheduledExecutorService> npcs;
    /**
     * The lock that ensures starting and stopping can't interfere with each
     * other.
     */
    private final Object startStopLock = new Object();
    /**
     * The service for the thread.
     */
    private ScheduledExecutorService serviceAI;
    /**
     * The chosen strategy by the player.
     */
    private PacmanStrategy strategy;

    /**
     * Creates a new level for the board.
     *
     * @param b              The board for the level.
     * @param ghosts         The ghosts on the board.
     * @param startPositions The squares on which players start on this board.
     * @param collisionMap   The collection of collisions that should be handled.
     */
    public AILevel(Board b, List<NPC> ghosts,
                   List<Square> startPositions, CollisionMap collisionMap) {
        super(b, ghosts, startPositions, collisionMap);
        this.npcs = new HashMap<>();
        for (NPC g : ghosts) {
            npcs.put(g, null);
            if (g instanceof Ghost) {
                addToGhostList((Ghost) g);
            }
        }
    }

    /**
     * Starts all NPC movement scheduling.
     */
    private void startNPCs() {
        for (final NPC npc : npcs.keySet()) {
            ScheduledExecutorService service = Executors
                    .newSingleThreadScheduledExecutor();
            service.schedule(new NpcMoveTask(service, npc),
                    npc.getInterval() / 2, TimeUnit.MILLISECONDS);
            npcs.put(npc, service);
        }
    }

    /**
     * Stops all NPC movement scheduling and interrupts any movements being
     * executed.
     */
    private void stopNPCs() {
        for (Map.Entry<NPC, ScheduledExecutorService> e : npcs.entrySet()) {
            e.getValue().shutdownNow();
        }
    }


    /**
     * Start or create a thread for the AI.
     */
    private void startAIStrategy() {
        //Start the main thread for the AI
        serviceAI = Executors.newSingleThreadScheduledExecutor();
        serviceAI.schedule(new PlayerMoveTask(serviceAI, (AIStrategy) strategy, getPlayer()),
                getPlayer().getInterval() / 2, TimeUnit.MILLISECONDS);
    }

    /**
     * Shutdown the thread.
     */
    private void stopAIStrategy() {
        if (serviceAI != null) {
            serviceAI.shutdown();
        }

    }

    /**
     * Starts or resumes the level.
     * Start NPC and AI.
     */
    public void start()
    {
        synchronized (startStopLock) {
            if (isInProgress()) {
                return;
            }
            if (strategy != null && strategy.getTypeStrategy() == PacmanStrategy.Type.AI)
            {
                startAIStrategy();
            }
            else
            {
                if(strategy != null) {
                    strategy.executeStrategy();
                }
            }
            startNPCs();
            setInProgress(true);
            updateObservers();
        }
    }

    /**
     * Stops or pauses this level, no longer allowing any movement on the board
     * and stopping all NPCs.
     */
    public void stop() {
        synchronized (startStopLock) {
            if (!isInProgress()) {
                return;
            }
            stopNPCs();
            if (strategy != null && strategy.getTypeStrategy() == PacmanStrategy.Type.AI) {
                stopAIStrategy();
            }
            setInProgress(false);
        }
    }
    /**
     * Set the strategy.
     * @param strategy chosen by the player
     */
    public void setStrategy(PacmanStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * A task that moves the player used by a AI.
     */
    private final class PlayerMoveTask implements Runnable {
        /**
         * The service executing the task.
         */
        private final ScheduledExecutorService service;

        /**
         * The Player to move.
         */
        private final AIStrategy strategy;
        private final IdentifiedPlayer player;
        private Direction nextMove;

        /**
         * Creates a new task.
         *
         * @param s        The service that executes the task.
         * @param strategy The chosen strategy by the player
         * @param p        The player of the game
         */
        PlayerMoveTask(ScheduledExecutorService s, AIStrategy strategy, IdentifiedPlayer p) {
            this.service = s;
            this.strategy = strategy;
            this.player = p;
        }

        /**
         * The run method called periodically.
         */
        @Override
        public void run() {
            if (nextMove == null || isIntersection(player, nextMove)) {
                nextMove = strategy.nextMove();
            }
            if (player.getSquare().getSquareAt(nextMove).isAccessibleTo(player)) {
                move(player, nextMove);
            }
            service.schedule(this, player.getInterval(), TimeUnit.MILLISECONDS);
        }

        /**
         * Test if the player is at an intersection in the game.
         *
         * @param player    the player of the game
         * @param direction the current direction
         * @return true if the player is in a intersection, false otherwise
         */
        public boolean isIntersection(IdentifiedPlayer player, Direction direction) {
            if (direction.equals(Direction.NORTH) || direction.equals(Direction.SOUTH)) {
                return player.getSquare().getSquareAt(Direction.EAST).isAccessibleTo(player)
                        || player.getSquare().getSquareAt(Direction.WEST).isAccessibleTo(player);
            } else {
                return player.getSquare().getSquareAt(Direction.NORTH).isAccessibleTo(player)
                        || player.getSquare().getSquareAt(Direction.SOUTH).isAccessibleTo(player);
            }
        }
    }

    /**
     * A task that moves an NPC and reschedules itself after it finished.
     *
     * @author Jeroen Roosen
     */
    private final class NpcMoveTask implements Runnable {

        /**
         * The service executing the task.
         */
        private final ScheduledExecutorService service;

        /**
         * The NPC to move.
         */
        private final NPC npc;

        /**
         * Creates a new task.
         *
         * @param s The service that executes the task.
         * @param n The NPC to move.
         */
        private NpcMoveTask(ScheduledExecutorService s, NPC n) {
            this.service = s;
            this.npc = n;
        }

        /**
         * The run method called periodically.
         */
        @Override
        public void run() {
            Direction nextMove = npc.nextMove();
            if (nextMove != null) {
                move(npc, nextMove);
            }
            service.schedule(this, npc.getInterval(), TimeUnit.MILLISECONDS);
        }
    }
}
