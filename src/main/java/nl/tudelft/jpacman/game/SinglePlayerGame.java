package nl.tudelft.jpacman.game;

import com.google.common.collect.ImmutableList;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.AILevel;
import nl.tudelft.jpacman.level.IdentifiedPlayer;

import java.io.IOException;
import java.util.List;

/**
 * A game with one player and a single level.
 *
 * @author Jeroen Roosen
 */
public class SinglePlayerGame extends Game {

    public static final int STARTING_LIVES = 3;
    /**
     * The player of this game.
     */
    private final IdentifiedPlayer player;

    /**
     * The level of this game.
     */
    private AILevel level;


    /**
     * Create a new single player game for the provided level and player.
     *
     * @param p The player.
     */
    protected SinglePlayerGame(IdentifiedPlayer p) {
        assert p != null;

        this.player = p;
        this.level = makeLevel(1);
        level.registerPlayer(p);
    }

    @Override
    public List<IdentifiedPlayer> getPlayers() {
        return ImmutableList.of(player);
    }

    /**
     * Simple getter for current Level.
     *
     * @return The current Level
     */
    @Override
    public AILevel getLevel() {
        return level;
    }

    /**
     * Sets the current level to the given one.
     *
     * @param level The Level that is to be played
     */
    @Override
    public void setLevel(final AILevel level) {
        player.unregister(level);
        level.registerPlayer(player);
        this.level = level;
    }

    /**
     * This method deals with levelWon event by bringing the user to the next level.
     */
    @Override
    public void levelWon() {
        super.levelWon();
        System.out.println("Just won level: " + getCurrentLevel());
        try {
            player.levelCompleted(getCurrentLevel());
        } catch (IOException e) {
            e.printStackTrace();
        }
        setLevel(nextLevel());
    }

    /**
     * Moves the player one square to the north if possible.
     */
    public void moveUp() {
        move(player, Direction.NORTH);
    }

    /**
     * Moves the player one square to the south if possible.
     */
    public void moveDown() {
        move(player, Direction.SOUTH);
    }

    /**
     * Moves the player one square to the west if possible.
     */
    public void moveLeft() {
        move(player, Direction.WEST);
    }

    /**
     * Moves the player one square to the east if possible.
     */
    public void moveRight() {
        move(player, Direction.EAST);
    }

    /**
     * Resets the score (to 0) and the number of player lives (to 3)
     * (should be called when a new level is set "manually" i.e. using "level choice" radio buttons)
     */
    @Override
    public void reset() {
        stop();
        player.resetScore();
        player.setLives(STARTING_LIVES);
    }
}
