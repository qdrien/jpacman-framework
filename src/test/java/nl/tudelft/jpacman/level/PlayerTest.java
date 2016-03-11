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

/**
 * Test class for the Player class.
 */
public class PlayerTest
{
    /**
     * A player with which to test methods.
     */
    private Player testPlayer;

    /**
     * The path of the file that will be used to test the player's profile.
     */
    private static final String PATH = new File("").getAbsolutePath()+"/src/test/resources/Testy.prf";

    /**
     * Operations to be executed prior to tests.
     * @throws IOException If the file used to test profiles cannot be created or written to.
     */
    @Before
    public void init() throws IOException
    {
        Player.setIsNotATest(false);
        Sprite sprites[] = new Sprite[1];
        testPlayer = new Player(null, new AnimatedSprite(sprites, 1, false));
        testPlayer.setProfilePath(PATH);
        testPlayer.setPlayerName("Testy");

        BufferedWriter writer = new BufferedWriter(new FileWriter(PATH));
        writer.write("0 0 0 0 0 0 0 0" + System.getProperty("line.separator"));
        writer.close();
    }

    /**
     * Deletes the file used to test profiles after all tests have run.
     */
    @AfterClass
    public static void cleanup()
    {
        new File(PATH).delete();
    }

    /**
     * Tests whether adding achievements works correctly or not.
     */
    @Test
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
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

    /**
     * Tests whether some methods triggered by the obtention of specific achievements work correctly.
     */
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
