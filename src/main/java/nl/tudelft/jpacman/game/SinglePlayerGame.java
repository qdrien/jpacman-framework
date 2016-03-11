package nl.tudelft.jpacman.game;

import com.google.common.collect.ImmutableList;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.Player;

import java.util.List;

/**
 * A game with one player and a single level.
 * 
 * @author Jeroen Roosen 
 */
public class SinglePlayerGame extends Game {

	/**
	 * The player of this game.
	 */
	private final Player player;

	/**
	 * The level of this game.
	 */
	private Level level;

    /**
	 * Create a new single player game for the provided level and player.
	 * 
	 * @param p
	 *            The player.
	 * @param l
	 *            The level.
	 */
	protected SinglePlayerGame(Player p, Level l) {
		assert p != null;
		assert l != null;

		this.player = p;
		this.level = l;
		level.registerPlayer(p);
	}

	@Override
	public List<Player> getPlayers() {
		return ImmutableList.of(player);
	}

    /**
     * Simple getter for current Level
     * @return The current Level
     */
	@Override
	public Level getLevel() {
		return level;
	}

    /**
     * This method deals with levelWon event by bringing the user to the next level
     */
    @Override
    public void levelWon() {
        super.levelWon();
        final Level level = launcher.nextLevel();
        setLevel(level);
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
     * Sets the current level to the given one
     * @param level The Level that is to be played
     */
    @Override
    public void setLevel(final Level level) {
        player.unregister(level);
        level.registerPlayer(player);
        this.level = level;
    }

	/**
	 * Resets the score (to 0) and the number of player lives (to 3)
     * (should be called when a new level is set "manually" i.e. using "level choice" radio buttons)
	 */
    @Override
    public void reset(){
        stop();
        player.resetScore();
        player.setLives(3);
    }
}
