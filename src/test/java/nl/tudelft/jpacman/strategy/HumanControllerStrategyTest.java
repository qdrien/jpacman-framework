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
    private Launcher launcher;


    @Before
    public void setUpPacman() {
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
     * Attribution of keys for the player to move the Pacman in the game.
     *
     * @throws AWTException if Abstract Window Toolkit has occurred
     * @throws InterruptedException if the thread is interrupted
     */
    @SuppressWarnings("methodlength")
    @Test
    public void strategyTest() throws AWTException, InterruptedException {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);
        assertFalse(game.isInProgress());
        assertNotNull(game);
        final PacManUiBuilder builder = new PacManUiBuilder().withDefaultButtons();
        final PacmanStrategy strategy = new HumanControllerStrategy(game, builder);

        assertEquals(strategy.getTypeStrategy(), PacmanStrategy.Type.PLAYER);
        assertNotNull(strategy);

        strategy.executeStrategy();

        assertFalse(game.isInProgress());
        game.start();
        assertTrue(game.isInProgress());

        //Score initial
        assertEquals(0, player.getScore());
    }
}
