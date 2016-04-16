package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.IdentifiedPlayer;
import nl.tudelft.jpacman.ui.PacManUiBuilder;

import java.awt.event.KeyEvent;

/**
 * Class that represents the strategy where a player controls the pacman.
 */
public class HumanControllerStrategy extends PacmanStrategy {
    /**
     * The builder.
     */
    private final PacManUiBuilder builder;


    /**
     * Default constructor.
     *
     * @param game    the current game.
     * @param builder The UI builder that will be used.
     */
    public HumanControllerStrategy(final Game game, final PacManUiBuilder builder) {
        super(game);
        this.setGame(game);
        this.builder = builder;
    }

    /**
     * Return the type of the strategy.
     *
     * @return PLAYER strategy.
     */
    @Override
    public Type getTypeStrategy() {
        return Type.PLAYER;
    }

    /**
     * Adds key events UP, DOWN, LEFT and RIGHT to a game.
     *
     * @param builder The {@link PacManUiBuilder} that will provide the UI.
     * @param game    The game that will process the events.
     */
    private void addSinglePlayerKeys(final PacManUiBuilder builder, final Game game) {
        final IdentifiedPlayer player = game.getPlayers().get(0);
        builder.addKey(KeyEvent.VK_UP, () -> game.continousMovement(player, Direction.NORTH))
                .addKey(KeyEvent.VK_DOWN, () -> game.continousMovement(player, Direction.SOUTH))
                .addKey(KeyEvent.VK_LEFT, () -> game.continousMovement(player, Direction.WEST))
                .addKey(KeyEvent.VK_RIGHT, () -> game.continousMovement(player, Direction.EAST));

    }

    /**
     * Return the next move to apply.
     *
     * @return null.
     */
    @Override
    public Direction nextMove() {
        return null;
    }


    /**
     * Add keys to the player.
     */
    public void executeStrategy() {
        addSinglePlayerKeys(builder, getGame());
    }
}
