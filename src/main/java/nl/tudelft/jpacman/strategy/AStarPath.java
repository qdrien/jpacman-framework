package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Pellet;
import nl.tudelft.jpacman.npc.ghost.Ghost;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A* algorithm implementation to compute the shortest path.
 */
public class AStarPath extends AStar<Square> {
    /**
     * Initialization of constants used
     * to select the best move based on the current game.
     */
    /**
     * The cost when you go to a square where there is a ghost.
     */
    public static final double GHOST_COST = 1000000;
    /**
     * The cost when you go to a square nearest to a ghost.
     */
    public static final double NEAREST_GHOST_COST = 500;
    /**
     * The cost when you go to a square where there is a pellet.
     */
    public static final double PELLET_COST = 1;
    /**
     * The cost when you go to empty square (no pellet).
     */
    public static final double EMPTY_COST = 5;
    /**
     * Distance used to calculate a square cost near a ghost.
     */
    public static final int DST_THRESHOLD = 3;
    /**
     * The board game.
     */
    private final Board board;
    /**
     * The list of the ghosts.
     */
    private final List<Ghost> ghosts;
    /**
     * The game's data.
     * The square goal.
     */
    private Square goalSquare;


    /**
     * Default Constructor.
     *
     * @param game The game.
     */
    public AStarPath(final Game game) {
        this.board = game.getLevel().getBoard();
        this.ghosts = game.getLevel().getGhostList();
    }

    /**
     * Compute the manhattan distance
     * between a point (x,y) and a point (a,b).
     *
     * @param x the x coordinate from first point.
     * @param y the y coordinate from first point.
     * @param a the x coordinate from second point.
     * @param b the y coordinate from second point.
     * @return the value of the distance.
     */
    @SuppressWarnings("checkstyle:linelength")
    public static double manhattanDistance(final double x, final double y, final double a, final double b) {
        return Math.abs(a - x) + Math.abs(b - y);
    }

    /**
     * Get the list of valid neighbors (accessible to a player).
     *
     * @param square the player square.
     * @return the neighbor's list.
     */
    @SuppressWarnings("checkstyle:linelength")
    public static List<Square> getValidNeighbors(final Square square) {
        final List<Square> neighborsList = square.getNeighbours();
        final List<Square> validNeighbors = new ArrayList<>(neighborsList);
        final Iterator<Square> iterator = validNeighbors.iterator();

        while (iterator.hasNext()) {
            final Square neighborSquare = iterator.next();
            boolean invalidNeighbor = false;
            if (neighborSquare.isAccessibleTo()) {
                if (neighborSquare.getOccupants().size() == 2) {
                    invalidNeighbor = neighborSquare.getOccupants().get(1) instanceof Ghost;
                }
                if (neighborSquare.getOccupants().size() == 1) {
                    invalidNeighbor = neighborSquare.getOccupants().get(0) instanceof Ghost;
                }
            } else {
                //Wall case
                invalidNeighbor = true;
            }
            if (invalidNeighbor) {
                iterator.remove();
            }
        }
        return validNeighbors;
    }

    /**
     * Test to know if the square is the goal or not.
     *
     * @param square The node to check.
     * @return true if it's the goal square0
     */
    @Override
    public final boolean isGoal(final Square square) {
        return square.getX() == goalSquare.getX() && square.getY() == goalSquare.getY();
    }

    /**
     * Determines the goal square.
     *
     * @param goal the goal square0
     */
    public void setGoal(final Square goal) {
        this.goalSquare = goal;
    }

    /**
     * Determines the cost of the square (used to define the best square).
     *
     * @param originSquare      The square to leave.
     * @param destinationSquare The square to reach.
     * @return the cost of the square.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Override
    public final Double g(final Square originSquare, final Square destinationSquare) {
        if (originSquare.getX() == destinationSquare.getX()
                && originSquare.getY() == destinationSquare.getY()) {
            return 0.0;
        } else {
            if (nearestGhosts(destinationSquare)) {
                return NEAREST_GHOST_COST;
            }
            final Square square =
                    board.squareAt(destinationSquare.getX(), destinationSquare.getY());

            if (square.getOccupants().size() == 0) {
                return EMPTY_COST;
            } else {
                if (square.getOccupants().size() == 2) {
                    if (square.getOccupants().get(1) instanceof Ghost) {
                        return GHOST_COST;
                    }
                } else {
                    if (square.getOccupants().get(0) instanceof Pellet) {
                        return PELLET_COST;
                    } else if (square.getOccupants().get(0) instanceof Ghost) {
                        return GHOST_COST;
                    } else {
                        return 0.0;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Determines the distance between from and to square.
     *
     * @param originSquare      The square to leave.
     * @param destinationSquare The square to reach.
     * @return the manhattan distance between two squares.
     */
    @Override
    public final Double h(final Square originSquare, final Square destinationSquare) {
        return manhattanDistance(originSquare.getX(), originSquare.getY(),
                destinationSquare.getX(), destinationSquare.getY());
    }

    /**
     * Check if the square is nearest to a square with a ghost.
     *
     * @param destinationSquare the square to check.
     * @return true if the square is nearest to a square with a ghost.
     */
    public final boolean nearestGhosts(final Square destinationSquare) {

        for (final Ghost ghost : ghosts) {
            final double dst =
                    manhattanDistance(destinationSquare.getX(), destinationSquare.getY(),
                            ghost.getSquare().getX(), ghost.getSquare().getY());
            if (dst < DST_THRESHOLD) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines the square neighbors.
     *
     * @param square The current square.
     * @return the neighbor's list of the square.
     */
    @Override
    protected final List<Square> generateSuccessors(final Square square) {
        return getValidNeighbors(square);
    }

}

