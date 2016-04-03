package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.level.CollisionInteractionMap.CollisionHandler;
import nl.tudelft.jpacman.npc.ghost.Ghost;

/**
 * An extensible default interaction map for collisions caused by the player.
 * <p>
 * The implementation makes use of the interactionmap, and as such can be easily
 * and declaratively extended when new types of units (ghosts, players, ...) are
 * added.
 *
 * @author Arie van Deursen
 * @author Jeroen Roosen
 */
public class DefaultPlayerInteractionMap implements CollisionMap {

    private CollisionMap collisions = defaultCollisions();

    /**
     * Creates the default collisions Player-Ghost and Player-Pellet.
     *
     * @return The collision map containing collisions for Player-Ghost and
     * Player-Pellet.
     */
    private static CollisionInteractionMap defaultCollisions() {
        CollisionInteractionMap collisionMap = new CollisionInteractionMap();

        collisionMap.onCollision(IdentifiedPlayer.class, Ghost.class,
                new CollisionHandler<IdentifiedPlayer, Ghost>() {

                    @Override
                    public void handleCollision(IdentifiedPlayer player, Ghost ghost) {
                        player.setAlive(false);
                    }
                });

        collisionMap.onCollision(IdentifiedPlayer.class, Pellet.class,
                new CollisionHandler<IdentifiedPlayer, Pellet>() {

                    @Override
                    public void handleCollision(IdentifiedPlayer player, Pellet pellet) {
                        pellet.leaveSquare();
                        player.addPoints(pellet.getValue());
                    }
                });
        return collisionMap;
    }

    @Override
    public void collide(Unit mover, Unit movedInto) {
        collisions.collide(mover, movedInto);
    }
}
