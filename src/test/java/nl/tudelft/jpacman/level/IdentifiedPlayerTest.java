package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.game.Achievement;
import nl.tudelft.jpacman.npc.ghost.GhostColor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertTrue;

/**
 * Testing the "IdentifiedPlayer" class.
 */
public class IdentifiedPlayerTest {
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
    @After
    public void cleanup() {
        new File(PATH).delete();
    }

    /**
     * Resets the test profile and load resources for this test suite
     *
     * @throws IOException If the file was not found or is not readable.
     */
    @Before
    public void setUp() throws IOException {
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
        try {
            String line;
            boolean found = false;
            player.addAchievement(Achievement.WON_THRICE);
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

    /**
     * Tests player creation.
     */
    @Test
    public void testCreatePlayer() throws IOException {
        player.createNewPlayer();
        //Checking that the profile file was created.
        assertTrue(new File(PATH).exists());
        //Checking that the login file contains the test player's name (Testy).
        assertTrue(cleanLoginFile());

    }

    /**
     * Helper method to locate the test player within the login file and then remove it.
     *
     * @return Whether the test player's info was found within the login file.
     * @throws IOException todo: damien
     */
    private Boolean cleanLoginFile() {
        Boolean found = false;
        String loginPath = player.getLoginPath(), line, toWrite = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(loginPath));
            while ((line = reader.readLine()) != null) {
                if (line.split(" ")[0].equals("Testy")) found = true;
                else toWrite += line + System.getProperty("line.separator");
            }
            reader.close();
            //Putting the login file back in order.
            BufferedWriter writer = new BufferedWriter(new FileWriter(loginPath));
            writer.write(toWrite);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return found;
    }

    /**
     * Tests identification of an existing player.
     */
    @Test
    public void authenticationTest() {
        //Create the player to authenticate.
        player.createNewPlayer();
        //Check that the authentication worked.
        assertTrue(player.authenticate());
        /*
        This might seem like a cheap/meaningless test at first glance, but it DOES check if "Testy" is located in the login file.
        If it wasn't, it would trigger an endless loop.
        */
        //Putting the login file back in order.
        cleanLoginFile();
    }
}