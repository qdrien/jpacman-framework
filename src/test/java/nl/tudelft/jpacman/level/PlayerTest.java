package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.game.Achievement;
import nl.tudelft.jpacman.npc.ghost.GhostColor;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

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
        final Sprite sprites[] = new Sprite[1];
        testPlayer = new Player(null, new AnimatedSprite(sprites, 1, false));
        testPlayer.setProfilePath(PATH);
        testPlayer.setPlayerName("Testy");

        final BufferedWriter writer = new BufferedWriter(new FileWriter(PATH));
        writer.write("0 0 0 0 0 0 0 0" + System.getProperty("line.separator"));
        writer.close();
    }

    /**
     * Deletes the file used to test profiles after all tests have run.
     */
    @After
    public void cleanup()
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
            final BufferedReader reader = new BufferedReader(new FileReader(PATH));
            while((line = reader.readLine()) != null)
            {
                if (line.equals(Achievement.WON_THRICE.toString())) found = true;
            }
            assertTrue("WON_THRICE wasn't found in the test player's achievement file", found);
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

    /**
     * Tests player creation.
     */
    @Test
    public void testCreatePlayer() throws IOException
    {
        testPlayer.createNewPlayer();
        //Checking that the profile file was created.
        assertTrue(new File(PATH).exists());
        //Checking that the login file contains the test player's name (Testy).
        assertTrue(cleanLoginFile());

    }

    /**
     * Helper method to locate the test player within the login file and then remove it.
     * @return Whether the test player's info was found within the login file.
     */
    private Boolean cleanLoginFile()
    {
        Boolean found = false;
        String loginPath = testPlayer.getLoginPath(), line, toWrite = "";
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(loginPath));
            while ((line = reader.readLine()) != null)
            {
                if (line.split(" ")[0].equals("Testy")) found = true;
                else toWrite += line + System.getProperty("line.separator");
            }
            reader.close();
            //Putting the login file back in order.
            BufferedWriter writer = new BufferedWriter(new FileWriter(loginPath));
            writer.write(toWrite);
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return found;
    }

    /**
     * Tests identification of an existing player.
     */
    @Test
    public void authenticationTest()
    {
        //Create the player to authenticate.
        testPlayer.createNewPlayer();
        //Check that the authentication worked.
        assertTrue(testPlayer.authenticate());
        /*
        This might seem like a cheap/meaningless test at first glance, but it DOES check if "Testy" is located in the login file.
        If it wasn't, it would trigger an endless loop.
        */
        //Putting the login file back in order.
        cleanLoginFile();
    }
}
