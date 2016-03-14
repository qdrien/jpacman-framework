package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Nicolas Leemans on 22/02/16.
 */


/**
 * Class to test the moveContinu method for the continu move of the player
 */
public class MoveContinuTest
{
    private Launcher launcher;

    /**
     * Launch the user interface.
     */
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
     * Launch the game, and imitate what would happen in a typical game.
     * The test is only a smoke test, and not a focused small test.
     * Therefore it is OK that the method is a bit too long.
     *
     * @throws InterruptedException Since we're sleeping in this test.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void moveContinuTest1() throws InterruptedException
    {
        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);

        // start cleanly.
        assertFalse(game.isInProgress());
        game.start();
        assertTrue(game.isInProgress());
        assertEquals(0, player.getScore());

        //Test continu movement
        game.moveContinu(player, Direction.EAST);
        //Attendre 400 ms
        Thread.sleep(1500);
        assertEquals(60, player.getScore());
    }

    /**
     * A continu move test
     * @throws InterruptedException
     */
    @SuppressWarnings("methodlength")
    @Test
    public void moveContinuTest2() throws InterruptedException
    {
        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);

        // start cleanly.
        assertFalse(game.isInProgress());
        game.start();
        assertTrue(game.isInProgress());
        assertEquals(0, player.getScore());

        //Test continu movement
        game.moveContinu(player, Direction.WEST);
        //Attendre 400 ms
        Thread.sleep(1500);
        assertEquals(60, player.getScore());
    }

    /**
     * A continu move test
     * @throws InterruptedException
     */
    @SuppressWarnings("methodlength")
    @Test
    public void moveContinuTest3() throws InterruptedException
    {
        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);

        // start cleanly.
        assertFalse(game.isInProgress());
        game.start();
        assertTrue(game.isInProgress());
        assertEquals(0, player.getScore());

        //Test continu movement
        game.moveContinu(player, Direction.WEST);
        //Wait 400 ms
        Thread.sleep(1500);
        assertEquals(60, player.getScore());

        game.moveContinu(player, Direction.SOUTH);
        //wait 600 ms
        Thread.sleep(600);
        assertEquals(80, player.getScore());

    }

    /**
     * A continu move test
     * @throws InterruptedException
     */
    @SuppressWarnings("methodlength")
    @Test
    public void moveContinuTest4() throws InterruptedException
    {
        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);

        // start cleanly.
        assertFalse(game.isInProgress());
        game.start();
        assertTrue(game.isInProgress());
        assertEquals(0, player.getScore());

        //Test continu movement
        game.moveContinu(player, Direction.EAST);
        //wait 1500 ms
        Thread.sleep(1500);
        assertEquals(60, player.getScore());

        game.moveContinu(player, Direction.SOUTH);
        //wait 1000 ms
        Thread.sleep(600);
        assertEquals(80, player.getScore());

    }

    /**
     * A continu move test
     * @throws InterruptedException
     */
    @SuppressWarnings("methodlength")
    @Test
    public void moveContinuTest5() throws InterruptedException
    {
        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);

        // start cleanly.
        assertFalse(game.isInProgress());
        game.start();
        assertTrue(game.isInProgress());
        assertEquals(0, player.getScore());

        //Test continu movement
        game.moveContinu(player, Direction.EAST);
        //wait 1500 ms
        Thread.sleep(1500);
        assertEquals(60, player.getScore());

        //No pellet remaining, the score stay the same
        Thread.sleep(200);
        assertEquals(60, player.getScore());

        //Go back and no pellet remaining
        game.moveContinu(player, Direction.WEST);
        //wait 1000 ms
        Thread.sleep(1000);
        assertEquals(60, player.getScore());
    }
}
