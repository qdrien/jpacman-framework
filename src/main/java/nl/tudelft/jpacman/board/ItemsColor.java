package nl.tudelft.jpacman.board;

import java.awt.*;

/**
 * Contains the mapping between pacman items and their corresponding color.
 */
public enum ItemsColor {
    PACMAN(Color.YELLOW),
    GHOST(Color.RED),
    WALL(Color.BLUE),
    SQUARE(Color.BLACK),
    PELLET(Color.WHITE);

    private final int value;

    ItemsColor(final Color color) {
        this.value = color.getRGB();
    }

    /**
     * Returns the type of item that matches the given RGB value.
     *
     * @param rgbValue The int representing a RGB value
     * @return The corresponding "pacman object" enum type or null if none matches
     */
    public static ItemsColor getItemByRGBValue(final int rgbValue) {
        for (final ItemsColor c : ItemsColor.values()) {
            if (rgbValue == c.value) return c;
        }
        System.err.println("Unknown color " + rgbValue);
        return null;
    }
}
