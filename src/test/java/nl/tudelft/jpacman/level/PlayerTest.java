package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.Launcher;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PlayerTest {
    /**
     * Player with which to run tests.
     */
    private static Player player;

    @BeforeClass
    public static void setUp() throws Exception {
        final Launcher launcher = new Launcher();
        launcher.launch();
        player = launcher.getGame().getPlayers().get(0);
    }

    /**
     * Tests that Player::addPoints(int) effectively adds points
     * and that a life is added when the threshold is reached.
     */
    @Test
    public void addPoints() {
        final int score = player.getScore(), lives = player.getLives();
        player.addPoints(10);
        assertEquals(score + 10, player.getScore());
        //using MAX_VALUE to ensure it is bigger than the "add life threshold"
        player.addPoints(Integer.MAX_VALUE);
        assertEquals(lives + 1, player.getLives());
    }

    /**
     * Tests that Player::loseLife() effectively removes one life from the testPlayer when called.
     */
    @Test
    public void loseLife() {
        final int lives = player.getLives();
        player.loseLife();
        assertEquals(lives - 1, player.getLives());
    }

    /**
     * Test that a testPlayer can die "for good" when Player::loseLife() removes its last life.
     */
    @Test
    public void dies() {
        player.setLives(1);
        assert player.isAlive();
        player.loseLife();
        assertFalse(player.isAlive());
    }

    /**
     * Tests that Player::addLife() effectively adds one life to the testPlayer when called.
     */
    @Test
    public void addLife() {
        final int lives = player.getLives();
        player.addLife();
        assertEquals(lives + 1, player.getLives());
    }
}
