package nl.tudelft.jpacman.board;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * todo: Nicolas
 */
public class ItemsColorTest {

    /**
     * todo: Nicolas
     * @throws Exception todo : Nicolas
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testGetItemByRGBValue() throws Exception {
        assertEquals(ItemsColor.WALL, ItemsColor.getItemByRGBValue(-16776961));
        assertEquals(ItemsColor.PACMAN, ItemsColor.getItemByRGBValue(-256));
    }
}