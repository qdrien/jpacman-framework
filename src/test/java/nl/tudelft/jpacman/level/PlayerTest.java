package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.game.Achievement;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.*;

public class PlayerTest
{
    private Player testPlayer;
    private static final String path = new File("").getAbsolutePath()+"/src/test/resources/Testy.prf";

    @Before
    public void init()
    {
        Sprite sprites[] = new Sprite[1];
        testPlayer = new Player(null, new AnimatedSprite(sprites, 1, false));
    }

    @AfterClass
    public static void cleanup()
    {
        new File(path).delete();
    }

    @Test
    public void testPlayerCreation()
    {
        testPlayer.createNewPlayer();
        String profilePath = new File("").getAbsolutePath()+"/src/main/resources/profiles/" + testPlayer.getPlayerName() + ".prf";
        assertTrue(new File(profilePath).exists());
        assertTrue(new File(testPlayer.getLoginPath()).lastModified() / 10000 == System.currentTimeMillis() / 10000);
    }

    @Test
    public void testAuthentification()
    {
        testPlayer.authenticate();
        assertNotEquals(testPlayer.getPlayerName(), "");
    }

    @Test
    public void testAchievementAddition()
    {
        testPlayer.setProfilePath(path);
        //Note: Throws a FileNotFoundException, that's normal and wouldn't occur during normal execution.
        testPlayer.addAchievement(Achievement.WON_THRICE);
        try
        {
            String line;
            //INSANITY-INDUCING: this does not work even though the tested code creates the file in the exact same place (path)
            //System.out.println(new File(path).canRead()); //This returns "true"
            BufferedReader reader = new BufferedReader(new FileReader(path)); //This throws a "FileNotFoundException"
            line = reader.readLine();
            assertEquals(line, "" + Achievement.WON_THRICE);
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
