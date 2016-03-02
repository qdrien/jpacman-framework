package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.ui.PacManUiBuilder;


/**
 * Created by Nicolas Leemans 1/03/16.
 */
public class StrategyAIController implements BehaviourStrategy
{
    private final int numStrategy = 2;
    @Override
    public int defineBehaviour(PacManUiBuilder builder, Game game)
    {
        return numStrategy;
       // System.out.println("Pacman est manipulé par une intelligence artificielle");

    }
    @Override
    public String toString()
    {
        return "The player is a AI";
    }
    public Direction nextMove()
    {
        //Programme qui calculer le prochain mouvement a appliquer
        return Direction.EAST;
    }

}
