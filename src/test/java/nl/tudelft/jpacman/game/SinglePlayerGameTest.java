package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;

/**
 * Tests various aspects of SinglePlayerGame.
 *
 * @author Adrien Coppens
 */
public class SinglePlayerGameTest {

    private SinglePlayerGame game;
    private Player player;

    /**
     * Sets up the Game and its components
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        player = mock(Player.class);
        player = new Player(new HashMap<>(), mock(AnimatedSprite.class));
        game = new SinglePlayerGame(player);
    }

    /**
     * Tests that the score and the number of lives a player has are effectively reset to default
     * (when #Game.reset is called)
     */
    @Test
    public void resetTest() {
        player.addPoints(100);
        player.addLife();
        final int score = player.getScore(), lives = player.getLives();
        game.reset();
        assertNotEquals(player.getScore(), score);
        assertNotEquals(player.getLives(), lives);
        assertEquals(0, player.getScore());
        assertEquals(3, player.getLives());
    }


    @Test
    public void testNextLevel() throws Exception {
        final int initialLevel = game.getCurrentLevel();
        game.nextLevel();
        assertEquals(initialLevel + 1, game.getCurrentLevel());
    }

}
