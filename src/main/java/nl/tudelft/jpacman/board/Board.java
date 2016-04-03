package nl.tudelft.jpacman.board;

import nl.tudelft.jpacman.level.IdentifiedPlayer;
import nl.tudelft.jpacman.npc.ghost.Ghost;

import java.util.ArrayList;
import java.util.List;

/**
 * A top-down view of a matrix of {@link Square}s.
 *
 * @author Jeroen Roosen
 */
public class Board {

    /**
     * The "radius" around a ghost that prevents squares in it to be considered "safe".
     */
    private static final int UNSAFE_RANGE = 4;
    /**
     * The grid of squares with board[x][y] being the square at column x, row y.
     */
    private final Square[][] board;

    /**
     * Creates a new board.
     *
     * @param grid The grid of squares with grid[x][y] being the square at column.
     *             x, row y.
     */
    public Board(Square[][] grid) {
        assert grid != null;
        this.board = grid;
        assert invariant() : "Initial grid cannot contain null squares";
    }

    /**
     * Returns the Manhattan distance between two points p0 and p1 given by their coordinates.
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

    /**
     * Whatever happens, the squares on the board can't be null.
     *
     * @return false if any square on the board is null.
     */
    public boolean invariant() {
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                if (board[x][y] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the width of this board, i.e. the amount of columns.
     *
     * @return The width of this board.
     */
    public int getWidth() {
        return board.length;
    }

    /**
     * Returns the height of this board, i.e. the amount of rows.
     *
     * @return The height of this board.
     */
    public int getHeight() {
        return board[0].length;
    }

    /**
     * Returns the square at the given <code>x,y</code> position.
     *
     * @param x The <code>x</code> position (column) of the requested square.
     * @param y The <code>y</code> position (row) of the requested square.
     * @return The square at the given <code>x,y</code> position (never null).
     */
    public Square squareAt(int x, int y) {
        assert withinBorders(x, y);
        final Square result = board[x][y];
        assert result != null : "Follows from invariant.";
        return result;
    }

    /**
     * Determines whether the given <code>x,y</code> position is on this board.
     *
     * @param x The <code>x</code> position (row) to test.
     * @param y The <code>y</code> position (column) to test.
     * @return <code>true</code> iff the position is on this board.
     */
    public boolean withinBorders(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    /**
     * Retrieves possible target squares for the given player by looking at ghosts positions.
     *
     * @param player The player we want to retrieve possible squares for
     * @return An ArrayList of Squares the player can move on
     */
    public List<Square> getPossibleSquares(IdentifiedPlayer player) {
        final List<Square> possibleSquares = new ArrayList<>();
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                final Square square = squareAt(x, y);
                if (square.isAccessibleTo(player) && isSafe(x, y)) possibleSquares.add(square);
            }
        }
        return possibleSquares;
    }

    /**
     * Determine whether a Square (given by its coordinates) is safe for the player
     * (i.e. no ghosts are too close).
     *
     * @param x The given horizontal coordinate of the Square we want to check
     * @param y The given vertical coordinate of the Square we want to check
     * @return true if it is safe, false otherwise
     */
    boolean isSafe(final int x, final int y) {
        int minX, minY, maxX, maxY;

        //Clamp values so that we stay within borders (between 0 and "width or height")
        minX = Math.max(0, Math.min(getWidth() - 1, x - UNSAFE_RANGE));
        maxX = Math.max(0, Math.min(getWidth() - 1, x + UNSAFE_RANGE));
        minY = Math.max(0, Math.min(getHeight() - 1, y - UNSAFE_RANGE));
        maxY = Math.max(0, Math.min(getHeight() - 1, y + UNSAFE_RANGE));

        //For each position in the rectangle produced by (minX,minY) and (maxX,maxY)
        for (int currentX = minX; currentX < maxX; currentX++) {
            for (int currentY = minY; currentY < maxY; currentY++) {
                //as we have a rectangle around the target square that contains too much squares,
                // we need to filter (ignore) "out of range" neighbors (using manhattan distance)
                if (manhattanDistance(x, y, currentX, currentY) > UNSAFE_RANGE) continue;
                //If is is in "Manhattan range", we need to check whether there is a ghost on it
                final long count = squareAt(currentX, currentY).getOccupants().stream()
                        .filter(p -> p instanceof Ghost)
                        .count();
                //If there is at least one Ghost on that square,
                // the original square at (x,y) is not safe
                if (count != 0) return false;
            }
        }
        return true;
    }
}
