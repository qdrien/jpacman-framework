package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.npc.ghost.Blinky;
import nl.tudelft.jpacman.npc.ghost.Ghost;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import nl.tudelft.jpacman.game.Achievement;
import nl.tudelft.jpacman.npc.ghost.GhostColor;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

/**
 * Tests methods that have been added or modified in the Player class since v6.3.0
 * @author Adrien Coppens
 */
public class PlayerTest {

    /**
     * The player we are making tests on
     */
    Player player;
    
    /**
     * A player with which to test methods.
     */
    private Player testPlayer;

    /**
     * The path of the file that will be used to test the player's profile.
     */
    private static final String PATH = new File("").getAbsolutePath()+"/src/test/resources/Testy.prf";

    @Before
    public void setUp() throws Exception {
        Launcher launcher = new Launcher();
        launcher.launch();
        Game game = launcher.getGame();
        player = game.getPlayers().get(0);
    }

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
    @AfterClass
    public static void cleanup()
    {
        new File(PATH).delete();
    }

     /**
     * Tests that Player::addPoints(int) effectively adds points and that a life is added when the threshold is reached
     * @throws Exception
     */
    @Test
    public void addPoints() throws Exception {
        int score = player.getScore();
        int lives = player.getLives();
        player.addPoints(10);
        assertEquals(score + 10, player.getScore());
        //using MAX_VALUE to ensure it is bigger than the "add life threshold"
        player.addPoints(Integer.MAX_VALUE);
        assertEquals(lives + 1, player.getLives());
    }

    /**
     * Tests that Player::loseLife() effectively removes one life from the player when called
     * @throws Exception
     */
    @Test
    public void loseLife() throws Exception {
        Ghost ghost = mock(Ghost.class);
        final int lives = player.getLives();
        player.loseLife(ghost);
        assertEquals(lives - 1, player.getLives());
    }

    /**
     * Test that a player can die "for good" when Player::loseLife() removes its last life
     * @throws Exception
     */
    @Test
    public void dies() throws Exception {
        Ghost ghost = mock(Ghost.class);
        player.setLives(1);
        assert player.isAlive();
        player.loseLife(ghost);
        assertFalse(player.isAlive());
    }

    /**
     * Tests that Player::addLife() effectively adds one life to the player when called
     * @throws Exception
     */
    @Test
    public void addLife() throws Exception {
        final int lives = player.getLives();
        player.addLife();
        assertEquals(lives + 1, player.getLives());
    }
    /**
     * Tests whether adding achievements works correctly or not.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    @Test
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
    public void testAchievements() throws IOException
    {
        testPlayer.killedBy(GhostColor.RED);
        testPlayer.levelCompleted(1);

        String line;
        boolean speedyFound = false, victorFound = false;
        BufferedReader reader = new BufferedReader(new FileReader(PATH));
        while ((line = reader.readLine()) !=null)
        {
            if (line.equals(Achievement.SPEEDY_DEATH.toString())) speedyFound = true;
            if (line.equals(Achievement.VICTOR.toString())) victorFound = true;
        }
        reader.close();

        assertTrue("Achievement not added to file: SPEEDY_DEATH.", speedyFound);
        assertTrue("Achievement not added to file: VICTOR.", victorFound);
    }
}