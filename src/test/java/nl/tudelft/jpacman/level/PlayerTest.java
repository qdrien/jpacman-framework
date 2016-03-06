package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PlayerTest
{
    private Player testPlayer;

    @Before
    public void init()
    {
        Sprite sprites[] = new Sprite[1];
        testPlayer = new Player(null, new AnimatedSprite(sprites, 1, false));
    }

    @Test
    public void testPlayerCreation()
    {
        testPlayer.createNewPlayer();
        assertTrue(new File(testPlayer.getLoginPath()).lastModified() / 10000 == System.currentTimeMillis() / 10000);
    }

    @Test
    public void testAuthentification()
    {
        testPlayer.authenticate();
        assertNotEquals(testPlayer.getPlayerName(), "");
    }
}
