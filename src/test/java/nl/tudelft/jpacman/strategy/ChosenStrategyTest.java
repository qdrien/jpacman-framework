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
 * Class to test the choice of the strategy for the game mode.
 */
public class ChosenStrategyTest {
    private Launcher launcher;

    /**
     * Launch the user interface.
     */
    @Before
    public void setUpPacman() {
        launcher = new Launcher();
        launcher.launch(true);
    }

    /**
     * Quit the user interface when we're done.
     */
    @After
    public void tearDown() {
        launcher.dispose();
    }

    /**
     * Human strategy test.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void humanStrategyTest1() {
        final Game game = launcher.getGame();
        final PacManUiBuilder builder = new PacManUiBuilder().withDefaultButtons();
        // start cleanly.

        assertFalse("Game is in progress", game.isInProgress());
        assertNotNull("Game hasn't been instantiated", game);
        assertNotNull("Builder hasn't been instantiated", builder);


        final PacmanStrategy strategy1 = new HumanControllerStrategy(game, builder);
        assertNotNull("The strategy hasn't been instantiated", strategy1.getTypeStrategy());
        assertEquals("The strategy should be player", strategy1.getTypeStrategy(), PacmanStrategy.Type.PLAYER);

        //The mode depends on a click by the player => no move in nextMove()
        assertNull("The move should be null in the player strategy", strategy1.nextMove());

    }

    /**
     * AI strategy test.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void aiStrategyTest1() {
        final Game game = launcher.getGame();
        final PacManUiBuilder builder = new PacManUiBuilder().withDefaultButtons();
        // start cleanly.
        assertFalse("Game is in progress", game.isInProgress());
        assertNotNull("Game hasn't been instantiated", game);
        assertNotNull("Builder hasn't been instantiated", builder);

        final PacmanStrategy strategy2 = new PacManhattanAI(game);
        assertNotNull("The strategy hasn't been instantiated", strategy2.getTypeStrategy());
        assertEquals("The strategy should be AI", strategy2.getTypeStrategy(), PacmanStrategy.Type.AI);

        assertNotNull("It should have a move in a AI strategy", strategy2.nextMove());


    }

    /**
     * Other strategy test (for a new implementation of strategy).
     *
     * @throws InterruptedException if the thread is interrupted
     */
    @SuppressWarnings("methodlength")
    @Test
    public void otherStrategyAITest1() throws InterruptedException {
        final Game game = launcher.getGame();
        // start cleanly.
        assertFalse("Game is in progress", game.isInProgress());
        assertNotNull("Game hasn't been instantiated", game);

        //Creation of a new AI strategy
        final AIStrategy strategy3 = new AIStrategy(game) {
            @Override
            public Direction nextMove() {
                return Direction.NORTH;
            }

            @Override
            public void executeStrategy() {
                game.move(game.getPlayers().get(0), nextMove());

            }
        };
        assertNotNull("The strategy hasn't been instantiated",
                strategy3.getTypeStrategy());
        assertEquals("The strategy should be AI", strategy3.getTypeStrategy(),
                PacmanStrategy.Type.AI);


        //Launch AI
        assertFalse("Game is in progress", game.isInProgress());
        assertNotNull("The player hasn't been instantiated", game.getPlayers().get(0));

        game.start();
        assertTrue("Game should be in progress", game.isInProgress());
        assertEquals("The score should be 0 because not defined", 0, game.getPlayers().get(0).getScore());

    }

}
