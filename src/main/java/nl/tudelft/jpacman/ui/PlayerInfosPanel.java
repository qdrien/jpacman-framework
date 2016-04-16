package nl.tudelft.jpacman.ui;

import nl.tudelft.jpacman.level.IdentifiedPlayer;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A panel consisting of a column for each player, with the numbered players on
 * top and their respective scores/lives underneath.
 *
 * @author Jeroen Roosen
 */
public class PlayerInfosPanel extends JPanel {


    /**
     * The default way in which the score is shown.
     */
    private static final ScoreFormatter DEFAULT_SCORE_FORMATTER =
            // this lambda breaks cobertura 2.7 ...
            // player) -> String.format("Score: %3d", player.getScore());
            new ScoreFormatter() {
                public String format(IdentifiedPlayer p) {
                    return String.format("Score: %3d", p.getScore());
                }
            };
    /**
     * The map of players and the labels their scores are on.
     */
    private final Map<IdentifiedPlayer, JLabel> scoreLabels;
    /**
     * The map of players and the labels their lives are on.
     */
    private final Map<IdentifiedPlayer, JLabel> livesLabels;
    /**
     * The way to format the score information.
     */
    private ScoreFormatter scoreFormatter = DEFAULT_SCORE_FORMATTER;

    /**
     * Creates a new player infos panel with a column for each player.
     *
     * @param players The players to display the infos of.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public PlayerInfosPanel(List<IdentifiedPlayer> players) {
        super();
        assert players != null;

        setLayout(new GridLayout(3, players.size()));

        for (int i = 1; i <= players.size(); i++) {
            add(new JLabel("Player " + i, JLabel.CENTER));
        }
        scoreLabels = new LinkedHashMap<>();
        livesLabels = new LinkedHashMap<>();
        for (IdentifiedPlayer p : players) {
            final JLabel scoreLabel = new JLabel("0", JLabel.CENTER);
            scoreLabels.put(p, scoreLabel);
            add(scoreLabel);
            final JLabel livesLabel = new JLabel("Lives: 3", JLabel.CENTER);
            livesLabels.put(p, livesLabel);
            add(livesLabel);
        }
    }

    /**
     * Refreshes the scores and the lives of the players.
     */
    protected void refresh() {
        for (IdentifiedPlayer p : scoreLabels.keySet()) {
            String score = "";
            if (!p.isAlive()) {
                score = "You died. ";
            }
            score += scoreFormatter.format(p);
            scoreLabels.get(p).setText(score);
            livesLabels.get(p).setText("Lives: " + p.getLives());
        }
    }

    /**
     * Let the player infos panel use a dedicated score formatter.
     *
     * @param sf Score formatter to be used.
     */
    public void setScoreFormatter(ScoreFormatter sf) {
        assert sf != null;
        scoreFormatter = sf;
    }

    /**
     * Provide means to format the score for a given player.
     */
    public interface ScoreFormatter {

        /**
         * Format the score of a given player.
         *
         * @param p The player and its score
         * @return Formatted score.
         */
        String format(IdentifiedPlayer p);
    }
}
