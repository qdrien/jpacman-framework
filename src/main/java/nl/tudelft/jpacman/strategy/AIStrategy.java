package nl.tudelft.jpacman.strategy;

/**
 * Created by Nicolas Leemans on 2/03/16.
 */

import nl.tudelft.jpacman.game.Game;

/**
 * Abstract class for each Artificial Intelligence strategy
 */
public abstract class AIStrategy extends PacmanStrategy
{
    /**
     * Default constructor
     * @param game the current game
     */
    public AIStrategy(Game game)
    {
        super(game);
    }


    /**
     * Get the type of the strategy
     * @return a AI type strategy
     */
    @Override
    public PacmanStrategy.Type getTypeStrategy()
    {
        return Type.AI;
    }
}
