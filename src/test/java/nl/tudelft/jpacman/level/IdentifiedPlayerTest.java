package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.game.Achievement;
import nl.tudelft.jpacman.npc.ghost.Ghost;
import nl.tudelft.jpacman.npc.ghost.GhostColor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests methods that have been added or modified in the Player class since v6.3.0.
 *
 * @author Adrien Coppens
 */
public class IdentifiedPlayerTest
{
    /**
     * The path of the file that will be used to test the testPlayer's profile.
     */
    private static final String PATH = new File("").getAbsolutePath()
            + "/src/test/resources/Testy.prf";
    /**
     * The player we are making tests on.
     */
    private static IdentifiedPlayer player;

    /**
     * Deletes the file used to test profiles after all tests have run.
     */
    @AfterClass
    public static void cleanup() {
        new File(PATH).delete();
    }

    @BeforeClass
    public static void setUp() throws IOException {
        final Launcher launcher = new Launcher();
        launcher.launch();
        player = launcher.getGame().getPlayers().get(0);
        IdentifiedPlayer.setIsNotATest();
        player.setProfilePath(PATH);
        player.setPlayerName();

        final BufferedWriter writer = new BufferedWriter(new FileWriter(PATH));
        writer.write("0 0 0 0 0 0 0 0" + System.getProperty("line.separator"));
        writer.close();
    }

    /**
     * Tests whether adding achievements works correctly or not.
     */
    @SuppressWarnings("PMD.DataFlowAnomalyAnalysis") //the initialisations are required.
    @Test
    public void testAchievementAddition() {
        player.addAchievement(Achievement.WON_THRICE);
        try {
            String line;
            boolean found = false;
            final BufferedReader reader = new BufferedReader(new FileReader(PATH));
            while ((line = reader.readLine()) != null) {
                if (line.equals(Achievement.WON_THRICE.toString())) found = true;
            }
            assertTrue("WON_THRICE wasn't found in the test testPlayer's achievement file", found);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests whether some methods triggered by the obtention
     * of specific achievements work correctly.
     *
     * @throws IOException if a problem happens while reading the file
     */
    @Test
    public void testAchievements() throws IOException {
        player.killedBy(GhostColor.RED);
        player.levelCompleted(1);

        String line;
        boolean speedyFound = false, victorFound = false;
        final BufferedReader reader = new BufferedReader(new FileReader(PATH));
        while ((line = reader.readLine()) != null) {
            if (line.equals(Achievement.SPEEDY_DEATH.toString())) speedyFound = true;
            if (line.equals(Achievement.VICTOR.toString())) victorFound = true;
        }
        reader.close();

        assertTrue("Achievement not added to file: SPEEDY_DEATH.", speedyFound);
        assertTrue("Achievement not added to file: VICTOR.", victorFound);
    }
}