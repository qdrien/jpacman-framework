package nl.tudelft.jpacman.board;

import org.junit.Test;

import static org.junit.Assert.*;

public class ItemsColorTest {

    @Test
    public void testGetItemByRGBValue() throws Exception {
        assertEquals(ItemsColor.WALL, ItemsColor.getItemByRGBValue(-16776961));
        assertEquals(ItemsColor.PACMAN, ItemsColor.getItemByRGBValue(-256));
    }
}