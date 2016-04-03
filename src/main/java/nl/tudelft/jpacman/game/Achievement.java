package nl.tudelft.jpacman.game;

/**
 * The list of possible achievements.
 */
public enum Achievement {
    VICTOR, WON_THRICE, SPEEDY_DEATH, AMBUSHED, OVER_9000;

    static {
        VICTOR.description = "Won a level!";
        WON_THRICE.description = "Won the game three times!";
        SPEEDY_DEATH.description = "Killed by Blinky.";
        AMBUSHED.description = "Killed by Pinky.";
        OVER_9000.description = "Scored more than 9000 points, impressing Vegeta in the process.";

        VICTOR.bonusScore = 1500;
        WON_THRICE.bonusScore = 5000;
        SPEEDY_DEATH.bonusScore = 100;
        AMBUSHED.bonusScore = 150;
        OVER_9000.bonusScore = 999;
    }

    /**
     * A text description associated with the Achievement.
     */
    private String description;
    /**
     * The amount of points the Achievement grants to the player when obtained.
     */
    private int bonusScore;

    /**
     * Returns the Achievement corresponding to the given String.
     *
     * @param s The String.
     * @return The Achievement.
     */
    public static Achievement parseAchievement(final String s) {
        return valueOf(s);
    }

    /**
     * Returns the textual description corresponding with the Achievement.
     *
     * @return The textual description corresponding with the Achievement.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the amount of points the Achievement grants to the player when obtained.
     *
     * @return The amount of points the Achievement grants to the player when obtained.
     */
    public int getBonusScore() {
        return bonusScore;
    }
}
