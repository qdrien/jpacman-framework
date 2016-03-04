package nl.tudelft.jpacman;

import nl.tudelft.jpacman.game.HallOfFame;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.*;

public class HallOfFameTest
{
    private HallOfFame HOF;
    private String path;
    private BufferedReader reader;

    @Before
    public void init() throws IOException
    {
        HOF = new HallOfFame();
        path = HOF.getHOFPath();
        reader = new BufferedReader(new FileReader(path));
    }

    @After
    public void cleanup() throws IOException
    {
        reader.close();
    }

    @Test
    public void hallOfFameUpdateTest()
    {
        File hallOfFameFile = new File(path);
        HOF.handleHOF(Integer.MAX_VALUE);
        //Testing whether the Hall of Fame file was modified within the last few seconds, as it should' ve been, given the score.
        assertTrue(hallOfFameFile.lastModified() / 10000 == System.currentTimeMillis() / 10000);
    }

/* Commented because the Hall of Fame shouldn't be reset every time the tests are run.
    @Test
    public void hallOfFameResetTest() throws IOException
    {
        HOF.resetHOF();
        BufferedReader defaultHOFFileReader = new BufferedReader(new FileReader(HOF.getDefaultHofPath()));
        String current = reader.readLine(), base = defaultHOFFileReader.readLine();
        while(current != null && base != null)
        {
            current += reader.readLine();
            base += defaultHOFFileReader.readLine();
        }
        assertEquals(current, base);
    }
*/
}
