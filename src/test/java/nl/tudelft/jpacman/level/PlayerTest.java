package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.Launcher;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests methods that have been added or modified in the Player class since v6.3.0.
 * (points/lives-related methods)
 */
@SuppressWarnings("checkstyle:magicnumber")
public class PlayerTest {
    /**
     * Player with which to run tests.
     */
    private static Player player;

    /**
     * Instantiate a launcher and gets a reference to the player
     * on which the test will be executed.
     */
    @BeforeClass
    public static void init() {
        final Launcher launcher = new Launcher();
        launcher.launch(true);
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
        assertEquals("Score wasn't properly added", score + 10, player.getScore());
        //using MAX_VALUE to ensure it is bigger than the "add life threshold"
        player.addPoints(Integer.MAX_VALUE);
        assertEquals("incorrect amount of lives, should have received a free life",
                lives + 1, player.getLives());
    }

    /**
     * Tests that Player::loseLife() effectively removes one life from the testPlayer when called.
     */
    @Test
    public void loseLife() {
        final int lives = player.getLives();
        player.loseLife();
        assertEquals("Incorrect amount of lives after removing one",
                lives - 1, player.getLives());
    }

    /**
     * Test that a testPlayer can die "for good" when Player::loseLife() removes its last life.
     */
    @Test
    public void dies() {
        player.setLives(1);
        assert player.isAlive();
        player.loseLife();
        assertFalse("The player did not properly die after loosing its last life",
                player.isAlive());
    }

    /**
     * Tests that Player::addLife() effectively adds one life to the testPlayer when called.
     */
    @Test
    public void addLife() {
        final int lives = player.getLives();
        player.addLife();
        assertEquals("Additional life was not properly granted", lives + 1, player.getLives());
    }
}
