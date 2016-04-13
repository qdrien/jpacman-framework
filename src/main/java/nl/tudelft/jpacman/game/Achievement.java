package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.level.IdentifiedPlayer;
import nl.tudelft.jpacman.level.Player;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * The list of possible achievements.
 */

@SuppressWarnings("checkstyle:magicnumber")
public enum Achievement {
    VICTOR, WON_THRICE, SPEEDY_DEATH, AMBUSHED, OVER_9000;

    /**
     * The maximum number of achievements that can be recommended to the player.
     */
    private static final int MAX_RECOMMENDATIONS = 3;

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

        VICTOR.recommended = WON_THRICE;
        WON_THRICE.recommended = OVER_9000;
        SPEEDY_DEATH.recommended = AMBUSHED;
        AMBUSHED.recommended = SPEEDY_DEATH;
        OVER_9000.recommended = WON_THRICE;
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
     * The achievement recommended to accomplish after a certain achievement was accomplished.
     */
    private Achievement recommended;

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

    /**
     * Offers the player achievements (at most MAX_RECOMMENDATIONS) to accomplish given that he has accomplished some other achievements.
     * @param player The player currently logged in.
     */
    @SuppressWarnings("checkstyle:linelength")
    public static void offerAchievements(IdentifiedPlayer player)
    {
        List<Achievement> recommendations = new ArrayList<>();
        try
        {
            List<Achievement> obtained = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(player.getProfilePath()), Charset.defaultCharset()));
            //ignoring the first line, it's not directly related to recommendations.
            String toDisplay = "", line = reader.readLine();
            while ((line = reader.readLine()) != null)
            {
                Achievement currentAchievement = valueOf(line);
                if (!obtained.contains(currentAchievement)) {
                    obtained.add(currentAchievement);
                }
            }

            for (int i = 0; i < obtained.size() && recommendations.size() <= MAX_RECOMMENDATIONS; i++)
            {
                Achievement recommended = obtained.get(i).recommended;
                if (!recommendations.contains(recommended) && !obtained.contains(recommended)) {
                    recommendations.add(recommended);
                    toDisplay +=  recommended + ": " + recommended.getDescription() + System.getProperty("line.separator");
                }
            }
            //VICTOR is the default recommended achievement.
            if (toDisplay.equals("") && !obtained.contains(VICTOR)) {
                toDisplay = VICTOR + ": " + VICTOR.getDescription();
            }
            reader.close();
            JOptionPane.showMessageDialog(null, toDisplay, "Recommended achievements.", JOptionPane.PLAIN_MESSAGE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
