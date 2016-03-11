package nl.tudelft.jpacman.strategy;

/**
 * Created by Nicolas Leemans on 2/03/16.
 */

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.npc.ghost.Ghost;

import java.util.ArrayList;

/**
 * Abstract class for each Artificial Intelligence strategy
 */
public abstract class AIStrategy extends PacmanStrategy
{
    /**
     * Game data used by AI's to calculate the next move to apply
     */
    private final Player player;//The player of the game
    private final Board board; // The board game
    private final ArrayList<Ghost> ghosts; //The ghosts list
    /**
     * Default constructor
     * @param game the current game
     */
    public AIStrategy(final Game game)
    {
        super(game);
        this.board = game.getLevel().getBoard();
        this.player = game.getLevel().getPlayer();
        this.ghosts = game.getLevel().getGhostList();
    }

    /**
     * Get the type of the strategy
     * @return a AI type strategy
     */
    @Override
    public final PacmanStrategy.Type getTypeStrategy()
    {
        return Type.AI;
    }

    /**
     * Get the player of the game
     * @return the player
     */
    public final Player getPlayer()
    {
        return this.player;
    }
    /**
     * Get the board of the game
     * @return the board
     */
    public final Board getBoard()
    {
        return this.board;
    }
    /**
     * Get the ghost's list of the game
     * @return the ghost's list
     */
    public final ArrayList<Ghost> getGhostsList()
    {
        return this.ghosts;
    }
}
