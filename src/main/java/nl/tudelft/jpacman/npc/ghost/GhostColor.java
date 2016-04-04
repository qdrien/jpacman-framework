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

    private int index;
    private Achievement achievementGranted;

    public int getIndex() {
        return index;
    }

    public Achievement getAchievementGranted() {
        return achievementGranted;
    }
}
