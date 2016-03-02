package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.ui.Action;
import nl.tudelft.jpacman.ui.PacManUiBuilder;
import java.awt.event.KeyEvent;


/**
 * Created by Nicolas Leemans on 1/03/16.
 */
public class StrategyHumanController implements BehaviourStrategy
{
    private final int numStrategy = 1;
    /**
     * Adds key events UP, DOWN, LEFT and RIGHT to a game.
     *
     * @param builder
     *            The {@link PacManUiBuilder} that will provide the UI.
     * @param game
     *            The game that will process the events.
     */
    protected void addSinglePlayerKeys(final PacManUiBuilder builder, final Game game)
    {
        final Player p1 = game.getPlayers().get(0);
        builder.addKey(KeyEvent.VK_UP, new Action()
        {

            @Override
            public void doAction()
            {
                game.moveContinu(p1, Direction.NORTH);

            }
        }).addKey(KeyEvent.VK_DOWN, new Action()
        {

            @Override
            public void doAction()
            {
                game.moveContinu(p1, Direction.SOUTH);
            }
        }).addKey(KeyEvent.VK_LEFT, new Action()
        {

            @Override
            public void doAction()
            {
                game.moveContinu(p1, Direction.WEST);
            }
        }).addKey(KeyEvent.VK_RIGHT, new Action()
        {

            @Override
            public void doAction()
            {
                game.moveContinu(p1, Direction.EAST);
            }
        });

    }

    /**
     * Strategy allowing the player to control the Pacman
     *
     * @param builder The {@link PacManUiBuilder} that will provide the UI.
     * @param game  The game that will process the events.
     */
    @Override
    public int defineBehaviour(PacManUiBuilder builder, Game game)
    {
        addSinglePlayerKeys(builder, game);
        return numStrategy;
    }

    @Override
    public String toString()
    {
        return "The player is a human";
    }

}
