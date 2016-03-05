package nl.tudelft.jpacman.level;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PlayerTest
{
    @Test
    public void testPlayerCreation()
    {
        PlayerInfo testInfo = new PlayerInfo(true);
        assertTrue(new File(testInfo.getLoginPath()).lastModified() / 10000 == System.currentTimeMillis() / 10000);
    }

    @Test
    public void testAuthentification()
    {
        PlayerInfo testInfo = new PlayerInfo(false);
        assertNotEquals(testInfo.getPlayerName(), "");
    }
}
