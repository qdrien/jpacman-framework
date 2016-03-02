package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.game.Game;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

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
    public void testAddPoints() throws Exception {
        int score = player.getScore();
        int lives = player.getLives();
        player.addPoints(10);
        assert(score + 10 == player.getScore());
        player.addPoints(10000); //TODO: check with inf
        assert(lives + 1 == player.getLives());
    }

    /**
     * Tests that Player::loseLife() effectively removes one life from the player when called
     * @throws Exception
     */
    @Test
    public void testLoseLife() throws Exception {
        int lives = player.getLives();
        player.loseLife();
        assert (lives - 1 == player.getLives());
    }

    /**
     * Test that a player can die "for good" when Player::loseLife() removes its last life
     * @throws Exception
     */
    @Test
    public void testDies() throws Exception {
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
    public void testAddLife() throws Exception {
        int lives = player.getLives();
        player.addLife();
        assert (lives + 1 == player.getLives());
    }
}