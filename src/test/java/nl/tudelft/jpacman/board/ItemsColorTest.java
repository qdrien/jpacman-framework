package nl.tudelft.jpacman.board;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * todo: damien
 */
public class ItemsColorTest {

    /**
     * todo: damien
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testGetItemByRGBValue() throws Exception {
        assertEquals(ItemsColor.WALL, ItemsColor.getItemByRGBValue(-16776961));
        assertEquals(ItemsColor.PACMAN, ItemsColor.getItemByRGBValue(-256));
    }
}