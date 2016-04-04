package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.Launcher;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@SuppressWarnings("checkstyle:magicnumber")
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
