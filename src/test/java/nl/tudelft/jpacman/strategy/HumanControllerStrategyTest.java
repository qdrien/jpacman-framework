package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.IdentifiedPlayer;
import nl.tudelft.jpacman.ui.PacManUiBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

/**
 * Human strategy test in the game.
 */
public class HumanControllerStrategyTest {
    /**
     * Launcher in the test.
     */
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
     * Attribution of keys for the player to move the Pacman in the game.
     *
     * @throws AWTException         if Abstract Window Toolkit has occurred
     * @throws InterruptedException if the thread is interrupted
     */
    @SuppressWarnings("methodlength")
    @Test
    public void strategyTest() throws AWTException, InterruptedException {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);
        assertFalse("Game is in progress", game.isInProgress());
        assertNotNull("Game hasn't been instantiated", game);
        final PacManUiBuilder builder = new PacManUiBuilder().withDefaultButtons();
        final PacmanStrategy strategy = new HumanControllerStrategy(game, builder);

        assertEquals("The strategy should be player",
                strategy.getTypeStrategy(), PacmanStrategy.Type.PLAYER);
        assertNotNull("The strategy hasn't been instantiated", strategy);

        strategy.executeStrategy();

        assertFalse("Game is in progress", game.isInProgress());
        game.start();
        assertTrue("Game should be in progress", game.isInProgress());

        //Score initial
        assertEquals("The score should be at the minimum", 0, player.getScore());
    }
}
