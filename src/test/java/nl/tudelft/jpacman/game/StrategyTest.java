package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.ui.PacManUiBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.net.www.content.text.plain;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by nico on 2/03/16.
 */
public class StrategyTest
{
    private Context context;
    private StrategyHumanController strategy1;
    private StrategyAIController strategy2;
    private Game game;
    private Launcher launcher;
    private Player player;
    private PacManUiBuilder builder;

    /**
     * Launch the user interface.
     */
    @Before
    public void setUpPacman()
    {
        launcher = new Launcher();
        launcher.launch();
        game = launcher.getGame();
        player = game.getPlayers().get(0);
        builder = new PacManUiBuilder().withDefaultButtons();
    }

    /**
     * Quit the user interface when we're done.
     */
    @After
    public void tearDown() {
        launcher.dispose();
    }
    @Test
    public void HumanStrategy()
    {
        strategy1 = new StrategyHumanController();
        context = new Context(strategy1);
        assertNotNull(strategy1);
        assertNotNull(context);
        assertTrue(strategy1.getClass() == StrategyHumanController.class);
        assertTrue("The player is a human" == strategy1.toString());
        int numStrategy = context.executeStrategy(builder,game);
        assertTrue(numStrategy==1);
    }
    @Test
    public void AIStrategy()
    {
        strategy2 = new StrategyAIController();
        context = new Context(strategy2);
        assertNotNull(strategy2);
        assertNotNull(context);
        assertTrue(strategy2.getClass() == StrategyAIController.class);
        assertTrue("The player is a AI" == strategy2.toString());
        int numStrategy = context.executeStrategy(builder,game);
        assertTrue(numStrategy==2);
    }

}
