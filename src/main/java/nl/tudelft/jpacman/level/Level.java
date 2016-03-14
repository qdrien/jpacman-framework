package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.PacmanConfigurationException;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.npc.NPC;
import nl.tudelft.jpacman.npc.ghost.Ghost;

import java.util.*;
import nl.tudelft.jpacman.strategy.AIStrategy;
import nl.tudelft.jpacman.strategy.PacmanStrategy;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A level of Pac-Man. A level consists of the board with the players and the
 * AIs on it.
 *
 * @author Jeroen Roosen
 */
public class Level implements PlayerListener {

    private static final int UNSAFE_RANGE = 4;
    private static final boolean QUICK_WIN = false; //todo
    /**
     * The service for the thread
     */
    private ScheduledExecutorService serviceAI;
    /**
     * The board of this level.
     */
    private final Board board;

    /**
     * The lock that ensures moves are executed sequential.
     */
    private final Object moveLock = new Object();

    /**
     * The lock that ensures starting and stopping can't interfere with each
     * other.
     */
    private final Object startStopLock = new Object();

    /**
     * The NPCs of this level and, if they are running, their schedules.
     */
    private final Map<NPC, ScheduledExecutorService> npcs;

    /**
     * <code>true</code> iff this level is currently in progress, i.e. players
     * and NPCs can move.
     */
    private boolean inProgress;

    /**
     * The squares from which players can start this game.
     */
    private final List<Square> startSquares;

    /**
     * The start current selected starting square.
     */
    private int startSquareIndex;

    /**
     * The players on this level.
     */
    private final List<Player> players;

    /**
     * The table of possible collisions between units.
     */
    private final CollisionMap collisions;

    /**
     * The objects observing this level.
     */
    private final List<LevelObserver> observers;
    /**
     * The chosen strategy by the player
     */
    private PacmanStrategy strategy;
    /**
     * The list of the ghosts in the game
     */
    private final ArrayList<Ghost> ghostList;
    /**
     * The amount of pellets there were when the level started
     */
    private int initialPelletCount = -1;
	private int index;

	/**
     * Creates a new level for the board.
     *
     * @param b
     *            The board for the level.
     * @param ghosts
     *            The ghosts on the board.
     * @param startPositions
     *            The squares on which players start on this board.
     * @param collisionMap
     *            The collection of collisions that should be handled.
     */
    public Level(Board b, List<NPC> ghosts, List<Square> startPositions,
                 CollisionMap collisionMap) {
        assert b != null;
        assert ghosts != null;
        assert startPositions != null;

        this.board = b;
        this.inProgress = false;
        this.ghostList = new ArrayList<>();
        this.npcs = new HashMap<>();
        for (NPC g : ghosts) {
            npcs.put(g, null);
            if(g instanceof Ghost)
            {
                ghostList.add((Ghost) g);
            }
        }
        this.startSquares = startPositions;
        this.startSquareIndex = 0;
        this.players = new ArrayList<>();
        this.collisions = collisionMap;
        this.observers = new ArrayList<>();
		if(QUICK_WIN)
			System.out.println("Warning: QUICK_WIN mode activated, the level will be considered complete if 13 pellets are picked up.\n" +
					"Disable this by setting QUICK_WIN to false in 'nl.tudelft.jpacman.level.Level.java'");
    }

    /**
     * Adds an observer that will be notified when the level is won or lost.
     *
     * @param observer
     *            The observer that will be notified.
     */
    public void addObserver(LevelObserver observer) {
        if (observers.contains(observer)) {
            return;
        }
        observers.add(observer);
    }

    /**
     * Removes an observer if it was listed.
     *
     * @param observer
     *            The observer to be removed.
     */
    public void removeObserver(LevelObserver observer) {
        observers.remove(observer);
    }

    /**
     * Registers a player on this level, assigning him to a starting position. A
     * player can only be registered once, registering a player again will have
     * no effect.
     *
     * @param p
     *            The player to register.
     */
    public void registerPlayer(Player p) {
        assert p != null;
        assert !startSquares.isEmpty();

        if (players.contains(p)) {
            return;
        }
        players.add(p);
        Square square = startSquares.get(startSquareIndex);
		p.register(this);
        p.occupy(square);
        startSquareIndex++;
        startSquareIndex %= startSquares.size();
    }

    /**
     * Returns the board of this level.
     *
     * @return The board of this level.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Moves the unit into the given direction if possible and handles all
     * collisions.
     *
     * @param unit
     *            The unit to move.
     * @param direction
     *            The direction to move the unit in.
     */
    public void move(Unit unit, Direction direction)
    {
        assert unit != null;
        assert direction != null;

        if (!isInProgress()) {
            return;
        }

        synchronized (moveLock)
        {
            unit.setDirection(direction);
            Square location = unit.getSquare();
            Square destination = location.getSquareAt(direction);

            if (destination.isAccessibleTo(unit)) {
                List<Unit> occupants = destination.getOccupants();
                unit.occupy(destination);
                for (Unit occupant : occupants) {
                    collisions.collide(unit, occupant);
                }
            }
            updateObservers();
        }
    }

    /**
     * Starts or resumes this level, allowing movement and (re)starting the
     * NPCs.
     */
    public void start() {
        synchronized (startStopLock) {
            if (isInProgress()) {
                return;
            }

            startNPCs();
            inProgress = true;
            if(strategy != null && strategy.getTypeStrategy() == PacmanStrategy.Type.AI)
            {
                startAIStrategy();
            }
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
            if(strategy != null && strategy.getTypeStrategy() == PacmanStrategy.Type.AI)
            {
                stopAIStrategy();
            }
            inProgress = false;
        }
    }


    /**
     * Starts or resumes the AI
     * @param strategy
     */
    public void startStrategy(PacmanStrategy strategy)
    {
        this.strategy = strategy;
        synchronized (startStopLock) {
            if (isInProgress()) {
                return;
            }
            strategy.executeStrategy();
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
        for (Entry<NPC, ScheduledExecutorService> e : npcs.entrySet()) {
            e.getValue().shutdownNow();
        }
    }

    /**
     * Start or create a thread for the AI
     */
    private void startAIStrategy()
    {
        //Start the main thread for the AI
        serviceAI = Executors.newSingleThreadScheduledExecutor();
        if(isInProgress())
        {
            serviceAI.schedule(new PlayerMoveTask(serviceAI, (AIStrategy) strategy, players.get(0)), players.get(0).getInterval(), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Shutdown the thread
     */
    private void stopAIStrategy()
    {
        if(serviceAI != null)
        {
            serviceAI.shutdown();
        }
    }


    /**
     * Returns whether this level is in progress, i.e. whether moves can be made
     * on the board.
     *
     * @return <code>true</code> iff this level is in progress.
     */
    public boolean isInProgress() {
        return inProgress;
    }

    /**
     * Updates the observers about the state of this level.
     */
    private void updateObservers() {
        if(initialPelletCount == -1) initialPelletCount = remainingPellets();
        if (!isAnyPlayerAlive()) {
            for (LevelObserver o : observers) {
                o.levelLost();
            }
        }
		if (remainingPellets() == 0 || QUICK_WIN && initialPelletCount - remainingPellets() == 13) {
            for (LevelObserver o : observers) {
                o.levelWon();
            }
        }
    }

    /**
     * Returns <code>true</code> iff at least one of the players in this level
     * is alive.
     *
     * @return <code>true</code> if at least one of the registered players is
     *         alive.
     */
    public boolean isAnyPlayerAlive() {
        for (Player p : players) {
            if (p.isAlive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts the pellets remaining on the board.
     *
     * @return The amount of pellets remaining on the board.
     */
    public int remainingPellets() {
        Board b = getBoard();
        int pellets = 0;
        for (int x = 0; x < b.getWidth(); x++) {
            for (int y = 0; y < b.getHeight(); y++) {
                for (Unit u : b.squareAt(x, y).getOccupants()) {
                    if (u instanceof Pellet) {
                        pellets++;
                    }
                }
            }
        }
        return pellets;
    }

    /**
     * Called when a player that has registered this Level loses one life.
     * If he has some more lives, he is moved on the board (away from ghosts)
     * so that he can keep on playing from a safer place.
     *
     * @param p The Player that just lost one life
     */
    @Override
    public void onPlayerLoseLife(final Player p) {
        if (p.getLives() > 0) {
            final ArrayList<Square> possibleSquares = getPossibleSquares();
            if (possibleSquares.isEmpty()) {
                throw new PacmanConfigurationException("There is no safe square.");
            } else {
                final Random r = new Random();
                final int targetSquareIndex = r.nextInt(possibleSquares.size());
                p.occupy(possibleSquares.get(targetSquareIndex));
            }
        }
    }
    
    /**
     * Get the player of the game
     * @return the player
     */
    public Player getPlayer()
    {
        return players.get(0);
    }

    /**
     * Get the ghost's list
     * @return the ghost's list
     */
    public ArrayList<Ghost> getGhostList()
    {
        return ghostList;
    }

    /**
     * Retrieves possible target squares for the player by looking at ghosts positions.
     *
     * @return An ArrayList of Squares the player can move on
     */
    private ArrayList<Square> getPossibleSquares() {
        assert players.get(0) != null;

        final int boardWidth = board.getWidth();
        final int boardHeight = board.getHeight();
        final ArrayList<Square> possibleSquares = new ArrayList<>();
        for (int x = 0; x < boardWidth; x++){
            for (int y = 0; y < boardHeight; y++){
                final Square square = board.squareAt(x, y);
                if (square.isAccessibleTo(players.get(0)) && isSafe(x, y)) possibleSquares.add(square);
            }
        }
        return possibleSquares;
    }

    /**
     * Determine whether a Square (given by its coordinates) is safe for the player (i.e. no ghosts are too close)
     *
     * @param x The given horizontal coordinate of the Square we want to check
     * @param y The given vertical coordinate of the Square we want to check
     * @return true if it is safe, false otherwise
     */
    boolean isSafe(final int x, final int y) {
        int minX, minY, maxX, maxY;

        //Clamp values so that we stay within borders (between 0 and "width or height")
		minX = Math.max(0, Math.min(board.getWidth() - 1, x - UNSAFE_RANGE));
		maxX = Math.max(0, Math.min(board.getWidth() - 1, x + UNSAFE_RANGE));
		minY = Math.max(0, Math.min(board.getHeight() - 1, y - UNSAFE_RANGE));
		maxY = Math.max(0, Math.min(board.getHeight() - 1, y + UNSAFE_RANGE));

        //For each position in the rectangle produced by (minX,minY) and (maxX,maxY)
        for(int currentX = minX; currentX < maxX; currentX++){
            for(int currentY = minY; currentY < maxY; currentY++){
                //as we considered a rectangle around the target square that contains too much squares,
                // we need to filter (ignore) "out of range" neighbors (using manhattan distance)
                if(manhattanDistance(x, y, currentX, currentY) > UNSAFE_RANGE) continue;
                //If is is in "Manhattan range", we need to check whether there is a ghost on it
                final long count = board.squareAt(currentX, currentY).getOccupants().stream()
                        .filter(p -> p instanceof Ghost)
                        .count();
                //If there is at least one Ghost on that square, the original square at (x,y) is not safe
                if(count != 0) return false;
            }
        }
        return true;
    }

    /**
     * Returns the Manhattan distance between two points p0 and p1 given by their coordinates
     *
     * @param x0 The horizontal coordinate of p0
     * @param y0 The vertical coordinate of p0
     * @param x1 The horizontal coordinate of p1
     * @param y1 The vertical coordinate of p1
     * @return The int value of the Manhattan distance between the given points
     */
    public static int manhattanDistance(final int x0, final int y0, final int x1, final int y1) {
        return Math.abs(x0 - x1) + Math.abs(y0 - y1);
    }

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
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
         * @param s
         *            The service that executes the task.
         * @param n
         *            The NPC to move.
         */
        private NpcMoveTask(ScheduledExecutorService s, NPC n) {
            this.service = s;
            this.npc = n;
        }

        @Override
        public void run() {
            Direction nextMove = npc.nextMove();
            if (nextMove != null) {
                move(npc, nextMove);
            }
            long interval = npc.getInterval();
            service.schedule(this, interval, TimeUnit.MILLISECONDS);
        }
    }


    /**
     * An observer that will be notified when the level is won or lost.
     *
     * @author Jeroen Roosen
     */
    public interface LevelObserver {

        /**
         * The level has been won. Typically the level should be stopped when
         * this event is received.
         */
        void levelWon();

        /**
         * The level has been lost. Typically the level should be stopped when
         * this event is received.
         */
        void levelLost();
    }
    /**
     * A task that moves the player used by a AI
     *
     * @author Leemans Nicolas
     */
    private final class PlayerMoveTask implements Runnable
    {
        /**
         * The service executing the task.
         */
        private final ScheduledExecutorService service;

        /**
         * The NPC to move.
         */
        private final AIStrategy strategy;
        private final Player player;
        private Direction nextMove;

        /**
         * Creates a new task.
         *
         * @param s
         *          The service that executes the task.
         * @param strategy
         *          The chosen strategy by the player
         * @param p
         *          The player of the game
         */
        private PlayerMoveTask(ScheduledExecutorService s, AIStrategy strategy, Player p)
        {
            this.service = s;
            this.strategy = strategy;
            this.player=p;
        }

        /**
         * The run method called periodically
         */
        @Override
        public void run()
        {
            //First called
            if(nextMove== null)
            {
                nextMove = strategy.nextMove();//compute a new move to apply
                move(player, nextMove);
            }
            else
            {
                if(isIntersection(player, nextMove))
                {

                    nextMove = strategy.nextMove();
                    if(nextMove != null)
                    {
                        if(player.getSquare().getSquareAt(nextMove).isAccessibleTo(player))
                        {
                            move(player, nextMove);
                        }
                    }
                }
                else
                {
                    //We can't chose a other move
                    move(player, nextMove);
                }
            }
            long interval = player.getInterval();
            service.schedule(this, interval, TimeUnit.MILLISECONDS);
        }

        /**
         * Test if the player is in a intersection in the game
         * @param player the player of the game
         * @param direction the current direction
         * @return true if the player is in a intersection, false otherwise
         */
        public boolean isIntersection(Player player, Direction direction)
        {
            if(direction.equals(Direction.NORTH))
            {
                return player.getSquare().getSquareAt(Direction.EAST).isAccessibleTo(player) ||
                        player.getSquare().getSquareAt(Direction.WEST).isAccessibleTo(player);
            }
            if(direction.equals(Direction.SOUTH))
            {
                return player.getSquare().getSquareAt(Direction.EAST).isAccessibleTo(player) ||
                        player.getSquare().getSquareAt(Direction.WEST).isAccessibleTo(player);
            }
            else if(direction.equals(Direction.EAST))
            {
                return player.getSquare().getSquareAt(Direction.NORTH).isAccessibleTo(player) ||
                        player.getSquare().getSquareAt(Direction.SOUTH).isAccessibleTo(player);
            }
            else
            {
                return player.getSquare().getSquareAt(Direction.NORTH).isAccessibleTo(player) ||
                        player.getSquare().getSquareAt(Direction.SOUTH).isAccessibleTo(player);
            }
        }
    }
}
