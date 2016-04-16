package nl.tudelft.jpacman.board;

import com.google.common.collect.ImmutableList;
import nl.tudelft.jpacman.sprite.Sprite;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A square on a {@link Board}, which can (or cannot, depending on the type) be
 * occupied by units.
 *
 * @author Jeroen Roosen
 */
public abstract class Square {
    /**
     * The units occupying this square, in order of appearance.
     */
    private final List<Unit> occupants;
    /**
     * The collection of squares adjacent to this square.
     */
    private final Map<Direction, Square> neighbours;
    /**
     * x-coordinate of a square.
     */
    private int x;
    /**
     * y-coordinate of a square.
     */
    private int y;

    /**
     * Creates a new, empty square.
     */
    protected Square() {
        this.occupants = new ArrayList<>();
        this.neighbours = new EnumMap<>(Direction.class);
    }

    /**
     * Returns the square adjacent to this square.
     *
     * @param direction The direction of the adjacent square.
     * @return The adjacent square in the given direction.
     */
    public Square getSquareAt(Direction direction) {
        return neighbours.get(direction);
    }

    /**
     * Links this square to a neighbour in the given direction. Note that this
     * is a one-way connection.
     *
     * @param neighbour The neighbour to link.
     * @param direction The direction the new neighbour is in, as seen from this cell.
     */
    public void link(Square neighbour, Direction direction) {
        neighbours.put(direction, neighbour);
    }

    /**
     * Returns an immutable list of units occupying this square, in the order in
     * which they occupied this square (i.e. oldest first.).
     *
     * @return An immutable list of units occupying this square, in the order in
     * which they occupied this square (i.e. oldest first.)
     */
    public List<Unit> getOccupants() {
        return ImmutableList.copyOf(occupants);
    }

    /**
     * Adds a new occupant to this square. If the occupant was already present,
     * nothing changed.
     *
     * @param occupant The unit to occupy this square.
     */
    void put(Unit occupant) {
        assert occupant != null;
        if (!occupants.contains(occupant)) {
            occupants.add(occupant);
        }
    }

    /**
     * Removes the unit from this square if it was present.
     *
     * @param occupant The unit to be removed from this square.
     */
    void remove(Unit occupant) {
        assert occupant != null;
        occupants.remove(occupant);
    }

    /**
     * Determines whether the unit is allowed to occupy this square.
     *
     * @return <code>true</code> iff the unit is allowed to occupy this square.
     */
    public abstract boolean isAccessibleTo();

    /**
     * Returns the sprite of this square.
     *
     * @return The sprite of this square.
     */
    public abstract Sprite getSprite();

    /**
     * Get the x-coordinate of a square.
     *
     * @return the x-coordinate.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Set the x-coordinate.
     *
     * @param x the x-coordinate to set.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Get the y-coordinate of a square.
     *
     * @return the y-coordinate.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Set the y-coordinate.
     *
     * @param y the x-coordinate to set.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Get the neighbors list of a square.
     *
     * @return the neighbors list.
     */
    public List<Square> getNeighbours() {
        List<Square> neighboursList = new ArrayList<>();
        if (getSquareAt(Direction.NORTH) != null) {
            neighboursList.add(getSquareAt(Direction.NORTH));
        }
        if (getSquareAt(Direction.SOUTH) != null) {
            neighboursList.add(getSquareAt(Direction.SOUTH));
        }
        if (getSquareAt(Direction.EAST) != null) {
            neighboursList.add(getSquareAt(Direction.EAST));
        }
        if (getSquareAt(Direction.WEST) != null) {
            neighboursList.add(getSquareAt(Direction.WEST));
        }
        return neighboursList;
    }

    /**
     * Equality test between two squares.
     *
     * @param square The test whose equality with this one is being tested.
     * @return true if squares are equals, false otherwise.
     */
     public boolean isSameSquare(Square square) {
        return this.getX() == square.getX() && this.getY() == square.getY();
    }
}
