package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.game.Achievement;
import nl.tudelft.jpacman.npc.ghost.GhostColor;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class PlayerTest
{
    private Player testPlayer;
    private static final String PATH = new File("").getAbsolutePath()+"/src/test/resources/Testy.prf";

    @Before
    public void init() throws IOException
    {
        Sprite sprites[] = new Sprite[1];
        testPlayer = new Player(null, new AnimatedSprite(sprites, 1, false));
        testPlayer.setProfilePath(PATH);
        testPlayer.setPlayerName("Testy");

        BufferedWriter writer = new BufferedWriter(new FileWriter(PATH));
        writer.write("0 0 0 0 0 0 0 0" + System.getProperty("line.separator"));
        writer.close();
    }

    @AfterClass
    public static void cleanup()
    {
        new File(PATH).delete();
    }

    @Test
    public void testPlayerCreation()
    {
        testPlayer.createNewPlayer();
        String profilePath = new File("").getAbsolutePath()+"/src/main/resources/profiles/" + testPlayer.getPlayerName() + ".prf";
        assertTrue("Profile file hasn't been created.", new File(profilePath).exists());
        assertTrue("Login file hasn't been updated.", new File(testPlayer.getLoginPath()).lastModified() / 10000 == System.currentTimeMillis() / 10000);
    }

    @Test
    public void testAuthentification()
    {
        testPlayer.authenticate();
        assertNotEquals("The player name is  empty.", testPlayer.getPlayerName(), "");
    }

    @Test
    public void testAchievementAddition()
    {
        testPlayer.addAchievement(Achievement.WON_THRICE);
        try
        {
            String line;
            boolean found = false;
            BufferedReader reader = new BufferedReader(new FileReader(PATH));
            while((line = reader.readLine()) != null)
            {
                if (line.equals(Achievement.WON_THRICE.toString())) found = true;
            }
            assertTrue(found);
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testAchievements()
    {
        long before = System.currentTimeMillis();
        testPlayer.killedBy(GhostColor.RED);
        //An achievement has been added means the profile file of the player has been modified (added a line).
        assertTrue("Achievement not added to file.", new File(PATH).lastModified() > before);
        before = System.currentTimeMillis();
        testPlayer.levelCompleted();
        assertTrue("Achievement not added to file.", new File(PATH).lastModified() > before);
    }
}
