package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.ui.PacManUiBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;

/**
 * Class to test the choice of the strategy for the game mode
 */
public class ChosenStrategyTest
{
    private Launcher launcher;

    @Before
    public void setUpPacman()
    {
        launcher = new Launcher();
        launcher.launch();
    }

    /**
     * Quit the user interface when we're done.
     */
    @After
    public void tearDown() {
        launcher.dispose();
    }

    /**
     * Human strategy test
     */
    @SuppressWarnings("methodlength")
    @Test
    public void humanStrategyTest1()
    {
        final Game game = launcher.getGame();
        final PacManUiBuilder  builder = new PacManUiBuilder().withDefaultButtons();
        // start cleanly.

        assertFalse(game.isInProgress());
        assertNotNull(game);
        assertNotNull(builder);


        final PacmanStrategy strategy1 = new HumanControllerStrategy(game,builder);
        assertNotNull(strategy1.getTypeStrategy());
        assertEquals(strategy1.getTypeStrategy(), PacmanStrategy.Type.PLAYER);

        //The mode depends on a click by the player => no move in nextMove()
        assertNull(strategy1.nextMove());

    }

    /**
     * AI strategy test
     */
    @SuppressWarnings("methodlength")
    @Test
    public void AIStrategyTest1()
    {
        final Game game = launcher.getGame();
        final PacManUiBuilder  builder = new PacManUiBuilder().withDefaultButtons();
        // start cleanly.
        assertFalse(game.isInProgress());
        assertNotNull(game);
        assertNotNull(builder);

        final PacmanStrategy strategy2 = new PacManhattanAI(game);
        assertNotNull(strategy2.getTypeStrategy());
        assertEquals(strategy2.getTypeStrategy(), PacmanStrategy.Type.AI);

        assertNotNull(strategy2.nextMove());


    }

    /**
     * Other strategy test (for a new implementation of strategy)
     * @throws InterruptedException
     */
    @SuppressWarnings("methodlength")
    @Test
    public void OtherStrategyAITest1() throws InterruptedException {
        final Game game = launcher.getGame();
        // start cleanly.
        assertFalse(game.isInProgress());
        assertNotNull(game);

        //Creation of a new AI strategy
        final AIStrategy strategy3 = new AIStrategy(game)
        {
            @Override
            public Direction nextMove() {
                return Direction.NORTH;
            }

            @Override
            public void executeStrategy()
            {
                game.move(game.getPlayers().get(0), nextMove());

            }
        };
        assertNotNull(strategy3.getTypeStrategy());
        assertEquals(strategy3.getTypeStrategy(), PacmanStrategy.Type.AI);


        //Launch AI
        assertFalse(game.isInProgress());
        assertNotNull(game.getPlayers().get(0));

        game.start();
        assertTrue(game.isInProgress());
        assertEquals(0, game.getPlayers().get(0).getScore());

    }

}
