package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.LevelFactory;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        player = mock(Player.class);
        BoardFactory boardFactory = mock(BoardFactory.class);
        LevelFactory levelFactory = mock(LevelFactory.class);
        Map<Direction, Sprite> spriteMap = new HashMap<>();
        player = new Player(spriteMap, mock(AnimatedSprite.class));
        Level level = mock(Level.class);
        game = new SinglePlayerGame(player, level, boardFactory, levelFactory);
    }

    /**
     * Tests that the score and the number of lives a player has are effectively reset to default
     * (when #Game.reset is called)
     */
    @Test
    public void resetTest(){
        player.addPoints(100);
        player.addLife();
        int score = player.getScore();
        int lives = player.getLives();
        game.reset();
        assertNotEquals(player.getScore(), score);
        assertNotEquals(player.getLives(), lives);
        assertEquals(0, player.getScore());
        assertEquals(3, player.getLives());
    }


    @Test
    public void testNextLevel() throws Exception {
        int initialLevel = game.getCurrentLevel();
        game.nextLevel();
        assertEquals(initialLevel + 1, game.getCurrentLevel());
    }

}
