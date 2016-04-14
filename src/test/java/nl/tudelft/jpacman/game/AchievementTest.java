package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.level.IdentifiedPlayer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("checkstyle:linelength")
public class AchievementTest
{
    private static final String PROFILE_PATH = new File("").getAbsolutePath() + "/src/test/resources/Testy.prf";

    @Before
    public void init() throws IOException
    {
        final BufferedWriter writer = new BufferedWriter(new FileWriter(PROFILE_PATH));
        writer.write("0 0 0 0 0 0 0 0" + System.getProperty("line.separator"));
        writer.close();
    }

    @After
    public void cleanup() {new File(PROFILE_PATH).delete();}

    @Test
    public void testDefaultRecommendation()
    {
        final IdentifiedPlayer player = mock(IdentifiedPlayer.class);
        when(player.getProfilePath()).thenReturn(PROFILE_PATH);
        final String recommendation = Achievement.offerAchievements(player);
        assertEquals("VICTOR: Won a level!", recommendation);
    }

    @Test
    public void testNonDefaultRecommendation() throws IOException
    {
        final BufferedWriter writer = new BufferedWriter(new FileWriter(PROFILE_PATH, true));
        writer.write("SPEEDY_DEATH");
        writer.close();

        final IdentifiedPlayer player = mock(IdentifiedPlayer.class);
        when(player.getProfilePath()).thenReturn(PROFILE_PATH);
        String recommendation = Achievement.offerAchievements(player);
        assertEquals("AMBUSHED: Killed by Pinky." + System.getProperty("line.separator"), recommendation);
    }
}
