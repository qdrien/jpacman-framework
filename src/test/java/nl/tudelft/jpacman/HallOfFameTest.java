package nl.tudelft.jpacman;

import nl.tudelft.jpacman.game.HallOfFame;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test class fo the Hall of Fame.
 */
public class HallOfFameTest {
    /**
     * A Hall of Fame to test methods with.
     */
    private HallOfFame hallOfFame;

    /**
     * To read the HoF file.
     */
    private BufferedReader reader;

    /**
     * Operations to be executed prior to tests.
     * @throws IOException If the HoF file cannot be found or read.
     */
    @Before
    public void init() throws IOException {
        HallOfFame.setIsNotATest(false);
        hallOfFame = new HallOfFame();
        String path = hallOfFame.getHoFPath();
        reader = new BufferedReader(new FileReader(path));
    }

    /**
     * Closes the HoF file after a test.
     * @throws IOException If the HoF file cannot be closed.
     */
    @After
    public void cleanup() throws IOException {
        reader.close();
    }

    /**
     * Tests whether the HoF is correctly updated whenever a new high score is obtained.
     * @throws IOException If the default HoF file cannot be read or found.
     */
    @Test
    public void hallOfFameUpdateTest() throws IOException {
        hallOfFame.handleHoF(Integer.MAX_VALUE, "TESTPLAYER");
        //Testing whether the Hall of Fame file was modified within the last few seconds, as it should' ve been, given the score.
        assertEquals("The Hall of Fame hasn't been modified.", reader.readLine().split(" ")[0], "TESTPLAYER");
    }

    /**
     * Tests whether the HoF gets reset to default values when appropriate.
     * @throws IOException If the default HoF file cannot be read or found.
     */
    @Test
    public void hallOfFameResetTest() throws IOException { // Note: running tests resets the Hall of Fame.
        hallOfFame.resetHoF();
        final BufferedReader defaultHOFReader = new BufferedReader(new FileReader(hallOfFame.getDefaultHoFPath()));
        String current = reader.readLine(), base = defaultHOFReader.readLine();
        for (int i = 0; i < hallOfFame.getNumberOfRecordsKept(); i++) {
            current += reader.readLine();
            base += defaultHOFReader.readLine();
        }
        defaultHOFReader.close();
        assertEquals("Hall of Fame hasn't been reset.", current, base);
    }
}
