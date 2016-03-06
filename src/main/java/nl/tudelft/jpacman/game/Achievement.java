package nl.tudelft.jpacman.game;

public enum Achievement
{
    VICTOR, WON_THRICE, SPEEDY_DEATH, AMBUSHED;

    private String description;
    private int bonusScore;

    static
    {
        VICTOR.description = "Won a level!";
        WON_THRICE.description = "Won the game three times!";
        SPEEDY_DEATH.description = "Killed by Blinky";
        AMBUSHED.description = "Killed by Pinky";

        VICTOR.bonusScore = 1500;
        WON_THRICE.bonusScore = 5000;
        SPEEDY_DEATH.bonusScore = 100;
        AMBUSHED.bonusScore = 150;
    }

    public static Achievement parseAchievement(String s)
    {
        return valueOf(s);
    }

    public String getDescription()
    {
        return description;
    }

    public int getBonusScore()
    {
        return bonusScore;
    }
}
