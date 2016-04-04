package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.level.IdentifiedPlayer;
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
@SuppressWarnings("checkstyle:magicnumber")
public class SinglePlayerGameTest {

    private SinglePlayerGame game;
    private IdentifiedPlayer player;

    /**
     * Sets up the Game and its components.
     */
    @Before
    public void setUp() {
        player = mock(IdentifiedPlayer.class);
        player = new IdentifiedPlayer(new HashMap<>(), mock(AnimatedSprite.class));
        game = new SinglePlayerGame(player);
    }

    /**
     * Tests that the score and the number of lives a player has are effectively reset to default
     * (when #Game.reset is called).
     */
    @Test
    public void resetTest() {
        player.addPoints(100);
        player.addLife();
        final int score = player.getScore(), lives = player.getLives();
        game.reset();
        assertNotEquals("Additional score hasn't been granted", player.getScore(), score);
        assertNotEquals("Additional life hasn't been granted", player.getLives(), lives);
        assertEquals("The player's score hasn't been reset properly", 0, player.getScore());
        assertEquals("The player does not have the correct amount of lives after the reset",
                SinglePlayerGame.STARTING_LIVES, player.getLives());
    }


    @Test
    public void testNextLevel() throws Exception {
        final int initialLevel = game.getCurrentLevel();
        game.nextLevel();
        assertEquals("current level hasn't been correctly updated",
                initialLevel + 1, game.getCurrentLevel());
    }

}
