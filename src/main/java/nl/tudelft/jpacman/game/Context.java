package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.ui.PacManUiBuilder;

/**
 * Created by Nicolas Leemans on 1/03/16.
 */
public class Context
{
    private BehaviourStrategy strategy;

    public Context(BehaviourStrategy strategy){
        this.strategy = strategy;
    }

    public int executeStrategy(PacManUiBuilder builder, Game game)
    {
        return strategy.defineBehaviour(builder,game);
    }
    public String getStrategy()
    {
        return this.strategy.toString();
    }
}
