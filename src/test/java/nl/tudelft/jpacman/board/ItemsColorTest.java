package nl.tudelft.jpacman.board;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;

/**
 * Tests methods that deal with translating colours to game objects or their corresponding letters
 */
public class ItemsColorTest {

    /**
     * Tests that getLetterByRGBValue returns correct characters for some colours.
     * Note that this test depends on {@link ItemsColor#correspondingLetter(ItemsColor)}
     * which means it isn't really a unit test (but one cannot mock an enum apparently)
     */
    @Test
    public void testGetLetterByRGBValue(){
        assertEquals("A blue colour should be a wall and thus have returned the '#' character",
                Character.valueOf('#'), ItemsColor.getLetterByRGBValue(Color.BLUE.getRGB()));
        assertEquals("A yellow colour should have returned the letter 'P' for pacman",
                Character.valueOf('P'), ItemsColor.getLetterByRGBValue(Color.YELLOW.getRGB()));
    }

    /**
     * Tests that correspondingLetters returns the corresponding characters.
     */
    @Test
    public void testCorrespondingLetter() {
        assertEquals("A ghost item should have returned the letter 'G'",
                Character.valueOf('G'), ItemsColor.correspondingLetter(ItemsColor.GHOST));
        assertEquals("A square item should have returned a whitespace",
                Character.valueOf(' '), ItemsColor.correspondingLetter(ItemsColor.SQUARE));
    }
}