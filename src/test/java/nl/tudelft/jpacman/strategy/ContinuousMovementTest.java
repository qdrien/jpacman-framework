package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.IdentifiedPlayer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Class to test the continuousMovement method for the continuous movement of the player.
 */
@SuppressWarnings("checkstyle:magicnumber")
public class ContinuousMovementTest {
    private Launcher launcher;

    /**
     * Launch the user interface.
     */
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
     * Launch the game, and imitate what would happen in a typical game.
     * The test is only a smoke test, and not a focused small test.
     * Therefore it is OK that the method is a bit too long.
     *
     * @throws InterruptedException Since we're sleeping in this test.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void continuousMovementTest1() throws InterruptedException {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);

        // start cleanly.
        assertFalse(game.isInProgress());
        game.start();
        assertTrue(game.isInProgress());
        assertEquals(0, player.getScore());

        //Test continuous movement
        game.continousMovement(player, Direction.EAST);
        Thread.sleep(2000);
        assertEquals(60, player.getScore());
    }

    /**
     * A continuous movement test.
     *
     * @throws InterruptedException if the thread is interrupted
     */
    @SuppressWarnings("methodlength")
    @Test
    public void continuousMovementTest2() throws InterruptedException {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);

        // start cleanly.
        assertFalse(game.isInProgress());
        game.start();
        assertTrue(game.isInProgress());
        assertEquals(0, player.getScore());

        //Test continuous movement
        game.continousMovement(player, Direction.WEST);
        Thread.sleep(2000);
        assertEquals(60, player.getScore());
    }

    /**
     * A continuous movement test.
     *
     * @throws InterruptedException if the thread is interrupted
     */
    @SuppressWarnings("methodlength")
    @Test
    public void continuousMovementTest3() throws InterruptedException {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);

        // start cleanly.
        assertFalse(game.isInProgress());
        game.start();
        assertTrue(game.isInProgress());
        assertEquals(0, player.getScore());

        //Test continuous movement
        game.continousMovement(player, Direction.WEST);
        Thread.sleep(2000);
        assertEquals(60, player.getScore());

        game.continousMovement(player, Direction.SOUTH);
        //wait 600 ms
        Thread.sleep(800);
        assertEquals(80, player.getScore());

    }

    /**
     * A continuous move test.
     *
     * @throws InterruptedException if the thread is interrupted
     */
    @SuppressWarnings("methodlength")
    @Test
    public void continuousMovementTest4() throws InterruptedException {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);

        // start cleanly.
        assertFalse(game.isInProgress());
        game.start();
        assertTrue(game.isInProgress());
        assertEquals(0, player.getScore());

        //Test continuous movement
        game.continousMovement(player, Direction.EAST);
        //wait 2000 ms
        Thread.sleep(2000);
        assertEquals(60, player.getScore());

        game.continousMovement(player, Direction.SOUTH);
        //wait 1000 ms
        Thread.sleep(800);
        assertEquals(80, player.getScore());

    }

    /**
     * A continuous move test.
     *
     * @throws InterruptedException if the thread is interrupted
     */
    @SuppressWarnings("methodlength")
    @Test
    public void continuousMovementTest5() throws InterruptedException {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);

        // start cleanly.
        assertFalse(game.isInProgress());
        game.start();
        assertTrue(game.isInProgress());
        assertEquals(0, player.getScore());

        //Test continuous movement
        game.continousMovement(player, Direction.EAST);
        //wait 2000 ms
        Thread.sleep(2000);
        assertEquals(60, player.getScore());

        //No pellet remaining, the score stay the same
        Thread.sleep(200);
        assertEquals(60, player.getScore());

        //Go back and no pellet remaining
        game.continousMovement(player, Direction.WEST);
        //wait 1000 ms
        Thread.sleep(1000);
        assertEquals(60, player.getScore());
    }
}
