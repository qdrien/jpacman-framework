package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.game.Game;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests methods that have been added or modified in the Player class since v6.3.0
 * @author Adrien Coppens
 */
public class PlayerTest {

    /**
     * The player we are making tests on
     */
    Player player;

    @Before
    public void setUp() throws Exception {
        Launcher launcher = new Launcher();
        launcher.launch();
        Game game = launcher.getGame();
        player = game.getPlayers().get(0);
    }

    /**
     * Tests that Player::addPoints(int) effectively adds points and that a life is added when the threshold is reached
     * @throws Exception
     */
    @Test
    public void addPoints() throws Exception {
        int score = player.getScore();
        int lives = player.getLives();
        player.addPoints(10);
        assertEquals(score + 10, player.getScore());
        //using MAX_VALUE to ensure it is bigger than the "add life threshold"
        player.addPoints(Integer.MAX_VALUE);
        assertEquals(lives + 1, player.getLives());
    }

    /**
     * Tests that Player::loseLife() effectively removes one life from the player when called
     * @throws Exception
     */
    @Test
    public void loseLife() throws Exception {
        final int lives = player.getLives();
        player.loseLife();
        assertEquals(lives - 1, player.getLives());
    }

    /**
     * Test that a player can die "for good" when Player::loseLife() removes its last life
     * @throws Exception
     */
    @Test
    public void dies() throws Exception {
        player.setLives(1);
        assert player.isAlive();
        player.loseLife();
        assertFalse(player.isAlive());
    }

    /**
     * Tests that Player::addLife() effectively adds one life to the player when called
     * @throws Exception
     */
    @Test
    public void addLife() throws Exception {
        final int lives = player.getLives();
        player.addLife();
        assertEquals(lives + 1, player.getLives());
    }
}