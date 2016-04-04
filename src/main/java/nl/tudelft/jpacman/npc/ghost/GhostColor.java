package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.game.Achievement;

/**
 * A list of supported ghost colors.
 *
 * @author Jeroen Roosen
 */
@SuppressWarnings("checkstyle:magicnumber")
public enum GhostColor {
    /**
     * Shadow, a.k.a. Blinky.
     */
    RED,

    /**
     * Bashful, a.k.a. Inky.
     */
    CYAN,

    /**
     * Speedy, a.k.a. Pinky.
     */
    PINK,

    /**
     * Pokey, a.k.a. Clyde.
     */
    ORANGE;

    static {
        RED.index = 4;
        PINK.index = 5;
        CYAN.index = 6;
        ORANGE.index = 7;

        RED.achievementGranted = Achievement.SPEEDY_DEATH;
        PINK.achievementGranted = Achievement.AMBUSHED;
        CYAN.achievementGranted = null;
        ORANGE.achievementGranted = null;
    }

    /**
     * The index of the "player killed by specific ghost" in the profile files.
     */
    private int index;

    /**
     * The eventual achievement granted whenever the player is killed by a specific ghost.
     */
    private Achievement achievementGranted;

    /**
     * Returns the index of the "player killed by specific ghost" in the profile files.
     * @return Said index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the eventual achievement granted whenever the player is killed by a specific ghost.
     * @return The Achievement.
     */
    public Achievement getAchievementGranted() {
        return achievementGranted;
    }
}
