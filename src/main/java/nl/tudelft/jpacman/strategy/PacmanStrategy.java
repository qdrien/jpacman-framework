package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.game.Game;

public abstract class PacmanStrategy {
    /**
     * The current game
     */
    Game game;


    /**
     * Default constructor
     *
     * @param game the current game
     */
    PacmanStrategy(final Game game) {
        this.game = game;
    }

    /**
     * Return the type of the strategy
     *
     * @return UNKNOWN
     */
    public PacmanStrategy.Type getTypeStrategy() {
        return Type.UNKNOWN;
    }

    /**
     * Move the player to the next direction
     *
     * @return the direction takes by the player
     */
    public abstract Direction nextMove();

    /**
     * Apply the chosen strategy
     */
    public abstract void executeStrategy();

    /**
     * Different type of the strategy
     */
    public enum Type {
        UNKNOWN,
        AI,
        PLAYER,
    }
}
