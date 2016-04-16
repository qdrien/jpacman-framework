package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.game.Game;

/**
 * Abstract class for pacman strategy.
 * A strategy is a game mode.
 */
public abstract class PacmanStrategy {
    /**
     * The current game.
     */
    private Game game;


    /**
     * Default constructor.
     *
     * @param game the current game.
     */
    PacmanStrategy(final Game game) {
        this.game = game;
    }

    /**
     * Return the type of the strategy.
     *
     * @return UNKNOWN.
     */
    public PacmanStrategy.Type getTypeStrategy() {
        return Type.UNKNOWN;
    }

    /**
     * Move the player to the next direction.
     *
     * @return the direction takes by the player.
     */
    public abstract Direction nextMove();

    /**
     * Apply the chosen strategy.
     */
    public abstract void executeStrategy();

    /**
     * Getter for the game field.
     *
     * @return The Game.
     */
    Game getGame() {
        return game;
    }

    /**
     * Setter for the game field.
     *
     * @param game The Game.
     */
    void setGame(final Game game) {
        this.game = game;
    }

    /**
     * Different type of the strategy.
     */
    public enum Type {
        UNKNOWN,
        AI,
        PLAYER,
    }
}
