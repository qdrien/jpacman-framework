package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.ui.PacManUiBuilder;


/**
 * Created by Nicolas Leemans on 1/03/16.
 */
public interface BehaviourStrategy
{
    public int defineBehaviour(PacManUiBuilder builder, Game game);
    public String toString();
}
