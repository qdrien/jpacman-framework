package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.IdentifiedPlayer;
import nl.tudelft.jpacman.npc.ghost.Ghost;

import java.util.List;

/**
 * Abstract class for each Artificial Intelligence strategy.
 */
public abstract class AIStrategy extends PacmanStrategy {

    /**
     * The player of the game.
     */
    private final IdentifiedPlayer player;
    /**
     * The board game.
     */
    private final Board board;
    /**
     * The ghosts list.
     */
    private final List<Ghost> ghosts;

    /**
     * Default constructor.
     *
     * @param game the current game.
     */
    public AIStrategy(final Game game) {
        super(game);
        this.board = game.getLevel().getBoard();
        this.player = game.getLevel().getPlayer();
        this.ghosts = game.getLevel().getGhostList();
    }

    /**
     * Get the type of the strategy.
     *
     * @return a AI type strategy.
     */
    @Override
    public final PacmanStrategy.Type getTypeStrategy() {
        return Type.AI;
    }

    /**
     * Get the player of the game.
     *
     * @return the player.
     */
    IdentifiedPlayer getPlayer() {
        return this.player;
    }

    /**
     * Get the board of the game.
     *
     * @return the board.
     */
    final Board getBoard() {
        return this.board;
    }

    /**
     * Get the ghost's list of the game.
     *
     * @return the ghost's list.
     */
    List<Ghost> getGhostsList() {
        return this.ghosts;
    }
}
