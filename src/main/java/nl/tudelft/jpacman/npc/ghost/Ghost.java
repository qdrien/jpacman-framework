package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.npc.NPC;
import nl.tudelft.jpacman.sprite.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * An antagonist in the game of Pac-Man, a ghost.
 *
 * @author Jeroen Roosen
 */
public abstract class Ghost extends NPC {

    private static final int VALUE = 10;

    private final GhostColor identity;

    /**
     * The sprite map, one sprite for each direction.
     */
    private Map<Direction, Sprite> sprites;

    /**
     * Creates a new ghost.
     *
     * @param spriteMap The sprites for every direction.
     * @param type      The type of ghost to create
     */
    protected Ghost(Map<Direction, Sprite> spriteMap, GhostColor type) {
        this.sprites = spriteMap;
        identity = type;
    }

    @Override
    public Sprite getSprite() {
        return sprites.get(getDirection());
    }

    /**
     * Determines a possible move in a random direction.
     *
     * @return A direction in which the ghost can move, or <code>null</code> if
     * the ghost is shut in by inaccessible squares.
     */
    protected Direction randomMove() {
        final Square square = getSquare();
        List<Direction> directions = new ArrayList<>();
        for (Direction d : Direction.values()) {
            if (square.getSquareAt(d).isAccessibleTo(this)) {
                directions.add(d);
            }
        }
        if (directions.isEmpty()) {
            return null;
        }
        return directions.get(new Random().nextInt(directions.size()));
    }

    public int getValue() {
        return VALUE;
    }

    public GhostColor getIdentity() {
        return identity;
    }
}
