package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.npc.ghost.Ghost;

/**
 * A simple implementation of a collision map for the JPacman player.
 * <p>
 * It uses a number of instanceof checks to implement the multiple dispatch for the
 * collisionmap. For more realistic collision maps, this approach will not scale,
 * and the recommended approach is to use a {@link CollisionInteractionMap}.
 *
 * @author Arie van Deursen, 2014
 */

public class PlayerCollisions implements CollisionMap {

    @Override
    public void collide(Unit mover, Unit collidedOn) {

        if (mover instanceof IdentifiedPlayer) {
            playerColliding((IdentifiedPlayer) mover, collidedOn);
        } else if (mover instanceof Ghost) {
            ghostColliding((Ghost) mover, collidedOn);
        }
    }

    private void playerColliding(IdentifiedPlayer player, Unit collidedOn) {
        if (collidedOn instanceof Ghost) {
            playerVersusGhost(player, (Ghost) collidedOn);
        }

        if (collidedOn instanceof Pellet) {
            playerVersusPellet(player, (Pellet) collidedOn);
        }
    }

    private void ghostColliding(Ghost ghost, Unit collidedOn) {
        if (collidedOn instanceof IdentifiedPlayer) {
            playerVersusGhost((IdentifiedPlayer) collidedOn, ghost);
        }
    }

    /**
     * Actual case of player bumping into ghost or vice versa.
     *
     * @param player The player involved in the collision.
     * @param ghost  The ghost involved in the collision.
     */
    public void playerVersusGhost(IdentifiedPlayer player, Ghost ghost) {
        if (player.isPoweredUp()) {
            player.addPoints(ghost.getValue());
        } else {
            player.killedBy(ghost.getIdentity());
            player.loseLife();
        }
    }

    /**
     * Actual case of player consuming a pellet.
     *
     * @param player The player involved in the collision.
     * @param pellet The pellet involved in the collision.
     */
    public void playerVersusPellet(IdentifiedPlayer player, Pellet pellet) {
        pellet.leaveSquare();
        player.addPoints(pellet.getValue());
    }

}
