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

    /**
     * Constructor for the ItemsColor enum.
     *
     * @param color The given colour that has to be mapped to its corresponding RGB int value
     */
    ItemsColor(final Color color) {
        this.value = color.getRGB();
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
            if (rgbValue == c.value) {
                return correspondingLetter(c);
            }
        }
        System.err.println("Unknown color: " + rgbValue);
        return null;
    }

    /**
     * Retrieves the letter that corresponds to the given item colour.
     * @param item The given item's colour
     * @return A Character representing the item's type for the map parser
     *          or null if the item is of unknown type
     */
    public static Character correspondingLetter(ItemsColor item) {
        switch (item) {
            case PACMAN:
                return 'P';
            case GHOST:
                return 'G';
            case WALL:
                return '#';
            case SQUARE:
                return ' ';
            case PELLET:
                return '.';
            default:
                System.err.println("Unknown item: " + item);
                return null;
        }
    }
}
