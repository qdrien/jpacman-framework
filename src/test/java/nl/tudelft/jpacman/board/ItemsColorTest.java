package nl.tudelft.jpacman.board;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests methods that deal with translating colours to game objects.
 */
public class ItemsColorTest {

    /**
     * Tests that getLetterByRGBValue returns correct characters for some colours
     * and null for an 'unknown' colour.
     */
    @Test
    public void testGetLetterByRGBValue() {
        assertEquals("A blue colour should be a wall and thus have returned the '#' character",
                Character.valueOf('#'), ItemsColor.getLetterByRGBValue(Color.BLUE.getRGB()));
        assertEquals("A yellow colour should have returned the letter 'P' for pacman",
                Character.valueOf('P'), ItemsColor.getLetterByRGBValue(Color.YELLOW.getRGB()));
        assertNull("A yellow colour should have returned the letter 'P' for pacman",
                ItemsColor.getLetterByRGBValue(Color.CYAN.getRGB()));
    }
}