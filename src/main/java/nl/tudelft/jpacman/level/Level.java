package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.PacmanConfigurationException;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.npc.NPC;
import nl.tudelft.jpacman.npc.ghost.Ghost;
import java.util.*;


/**
 * A level of Pac-Man. A level consists of the board with the players and the
 * AIs on it.
 *
 * @author Jeroen Roosen
 */
public abstract class Level implements PlayerListener {

    /**
     * If true, picking up 13 pellets is enough to win a level
     */
    private static final boolean QUICK_WIN = false; //todo
    /**
     * The board of this level.
     */
    private final Board board;
    /**
     * The lock that ensures moves are executed sequential.
     */
    private final Object moveLock = new Object();
    /**
     * The squares from which players can start this game.
     */
    private final List<Square> startSquares;
    /**
     * The players on this level.
     */
    protected final List<IdentifiedPlayer> players;
    /**
     * The table of possible collisions between units.
     */
    private final CollisionMap collisions;
    /**
     * The objects observing this level.
     */
    private final List<LevelObserver> observers;
    /**
     * The list of the ghosts in the game
     */
    protected final List<Ghost> ghostList;
    /**
     * <code>true</code> iff this level is currently in progress, i.e. players
     * and NPCs can move.
     */
    protected boolean inProgress;
    /**
     * The start current selected starting square.
     */
    private int startSquareIndex;

    /**
     * The amount of pellets there were when the level started
     */
    private int initialPelletCount = -1;

    /**
     * Creates a new level for the board.
     *
     * @param b              The board for the level.
     * @param ghosts         The ghosts on the board.
     * @param startPositions The squares on which players start on this board.
     * @param collisionMap   The collection of collisions that should be handled.
     */
    public Level(Board b, List<NPC> ghosts, List<Square> startPositions,
                 CollisionMap collisionMap) {
        assert b != null;
        assert ghosts != null;
        assert startPositions != null;

        this.board = b;
        this.inProgress = false;
        this.ghostList = new ArrayList<>();
        this.startSquares = startPositions;
        this.startSquareIndex = 0;
        this.players = new ArrayList<>();
        this.collisions = collisionMap;
        this.observers = new ArrayList<>();
        if (QUICK_WIN)
            System.out.println("Warning: QUICK_WIN mode activated, the level will be considered complete if 13 pellets are picked up.\n" +
                    "Disable this by setting QUICK_WIN to false in 'nl.tudelft.jpacman.level.Level.java'");
    }

    /**
     * Adds an observer that will be notified when the level is won or lost.
     *
     * @param observer The observer that will be notified.
     */
    public void addObserver(LevelObserver observer) {
        if (observers.contains(observer)) {
            return;
        }
        observers.add(observer);
    }

    /**
     * Registers a player on this level, assigning him to a starting position. A
     * player can only be registered once, registering a player again will have
     * no effect.
     *
     * @param p The player to register.
     */
    public void registerPlayer(IdentifiedPlayer p) {
        assert p != null;
        assert !startSquares.isEmpty();

        if (players.contains(p)) {
            return;
        }
        players.add(p);
        final Square square = startSquares.get(startSquareIndex);
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
     * @param unit      The unit to move.
     * @param direction The direction to move the unit in.
     */
    public void move(Unit unit, Direction direction) {
        assert unit != null;
        assert direction != null;

        if (!isInProgress()) {
            return;
        }

        synchronized (moveLock) {
            unit.setDirection(direction);
            final Square location = unit.getSquare();
            final Square destination = location.getSquareAt(direction);

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
    protected void updateObservers() {
        if (initialPelletCount == -1) initialPelletCount = remainingPellets();
        if (!isAnyPlayerAlive()) {
            observers.forEach(LevelObserver::levelLost);
        }
        if (remainingPellets() == 0 || QUICK_WIN && initialPelletCount - remainingPellets() == 13) {
            observers.forEach(LevelObserver::levelWon);
        }
    }

    /**
     * Returns <code>true</code> iff at least one of the players in this level
     * is alive.
     *
     * @return <code>true</code> if at least one of the registered players is alive.
     */
    public boolean isAnyPlayerAlive() {
        for (IdentifiedPlayer p : players) {
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
    public void onPlayerLoseLife(final IdentifiedPlayer p) {
        if (p.getLives() > 0) {
            final List<Square> possibleSquares = getPossibleSquares();
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
     *
     * @return the player
     */
    public IdentifiedPlayer getPlayer() {
        return players.get(0);
    }


    /**
     * Get the ghost's list
     *
     * @return the ghost's list
     */
    public List<Ghost> getGhostList() {
        return ghostList;
    }

    /**
     * Calls {@link Board#getPossibleSquares(IdentifiedPlayer)} to get the list of squares the player can move onto
     * (such squares are accessible & have no ghost that are too close to them)
     *
     * @return An ArrayList of Squares the player can move onto
     */
    private List<Square> getPossibleSquares() {
        assert players.get(0) != null;
        return board.getPossibleSquares(players.get(0));
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

}
