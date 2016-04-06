package nl.tudelft.jpacman.board;

import java.awt.*;

/**
 * Contains the mapping between pacman items and their corresponding color/letter.
 */
public enum ItemsColor {
    PACMAN(Color.YELLOW, 'P'),
    GHOST(Color.RED, 'G'),
    WALL(Color.BLUE, '#'),
    SQUARE(Color.BLACK, ' '),
    PELLET(Color.WHITE, '.');

    private final int color;
    private final char character;

    /**
     * Constructor for the ItemsColor enum.
     *
     * @param color The colour of the game object in the "image" map format
     * @param character The character corresponding to the game object in the map parser format
     */
    ItemsColor(final Color color, char character) {
        this.color = color.getRGB();
        this.character = character;
    }

    /**
     * Returns the letter that corresponds to the item whose colour matches the given RGB value.
     *
     * @param rgbValue The int representing a RGB value
     * @return A letter representing the game object that matches the given RGB value
     *          or null if none matches
     */
    public static Character getLetterByRGBValue(final int rgbValue) {
        for (final ItemsColor c : ItemsColor.values()) {
            if (rgbValue == c.color) {
                return c.character;
            }
        }
        System.err.println("Unknown color: " + rgbValue);
        return null;
    }
}
