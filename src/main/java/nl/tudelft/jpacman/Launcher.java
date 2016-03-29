package nl.tudelft.jpacman;


import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.game.*;
import nl.tudelft.jpacman.level.*;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.sprite.PacManSprites;
import nl.tudelft.jpacman.ui.Action;
import nl.tudelft.jpacman.ui.MyJDialogStrategy;
import nl.tudelft.jpacman.ui.PacManUI;
import nl.tudelft.jpacman.ui.PacManUiBuilder;

import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.*;

import java.io.InputStream;
import java.util.List;

/**
 * Creates and launches the JPacMan UI.
 * 
 * @author Jeroen Roosen 
 */
public class Launcher
{
	private static final PacManSprites SPRITE_STORE = new PacManSprites();

	private PacManUI pacManUI;
	private Game game;//The game
	private PacManUiBuilder builder;//The builder

    /**
     * The current level id
     */
    private int currentLevel;

    /**
	 * @return The game object this launcher will start when {@link #launch()}
	 *         is called.
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Creates a new game using the level from {@link #makeLevel()}.
	 * 
	 * @return a new Game.
	 */
	public Game makeGame()
    {
        GameFactory gf = getGameFactory();
		Level level = makeLevel();
        return gf.createSinglePlayerGame(level);
	}

	/**
	 * Creates a new level. By default this method will use the map parser to
	 * parse the default board stored in the <code>board1.txt</code> resource (=level 1).
	 * 
	 * @return level 1.
	 */
	public Level makeLevel() {
		return makeLevel(1);
	}

    /**
     * Creates a new level. Uses the map parser to
     * parse the desired board file.
     *
     * @return A new level.
     */
	public Level makeLevel(final int id) {
		MapParser parser = getMapParser();
		String file = "/board" + id + ".txt";
        System.out.println("Loading " + file);
        try (InputStream boardStream = Launcher.class
				.getResourceAsStream(file)) {
            if(boardStream == null) return null;
            currentLevel = id;
            Level level = parser.parseMap(boardStream);
            level.setIndex(id);
            return level;
		} catch (IOException e) {
			throw new PacmanConfigurationException("Unable to create level.", e);
		}
	}

	/**
	 * @return A new map parser object using the factories from
	 *         {@link #getLevelFactory()} and {@link #getBoardFactory()}.
	 */
	protected MapParser getMapParser() {
		return new MapParser(getLevelFactory(), getBoardFactory());
	}

	/**
	 * @return A new board factory using the sprite store from
	 *         {@link #getSpriteStore()}.
	 */
	protected BoardFactory getBoardFactory() {
		return new BoardFactory(getSpriteStore());
	}

	/**
	 * @return The default {@link PacManSprites}.
	 */
	protected PacManSprites getSpriteStore() {
		return SPRITE_STORE;
	}

	/**
	 * @return A new factory using the sprites from {@link #getSpriteStore()}
	 *         and the ghosts from {@link #getGhostFactory()}.
	 */
	protected LevelFactory getLevelFactory() {
		return new LevelFactory(getSpriteStore(), getGhostFactory());
	}

	/**
	 * @return A new factory using the sprites from {@link #getSpriteStore()}.
	 */
	protected GhostFactory getGhostFactory() {
		return new GhostFactory(getSpriteStore());
	}

	/**
	 * @return A new factory using the players from {@link #getPlayerFactory()}.
	 */
	protected GameFactory getGameFactory() {
		return new GameFactory(getPlayerFactory());
	}


	/**
	 * @return A new factory using the sprites from {@link #getSpriteStore()}.
	 */
	protected PlayerFactory getPlayerFactory() {
		return new PlayerFactory(getSpriteStore());
	}

	/**
	 * Adds key events UP, DOWN, LEFT and RIGHT to a game.
	 * 
	 * @param builder
	 *            The {@link PacManUiBuilder} that will provide the UI.
	 * @param game
	 *            The game that will process the events.
	 */
	protected void addSinglePlayerKeys(final PacManUiBuilder builder,
			final Game game)
    {
		final Player p1 = getSinglePlayer(game);

		builder.addKey(KeyEvent.VK_UP, new Action() {

			@Override
			public void doAction()
            {
                game.move(p1, Direction.NORTH);

			}
		}).addKey(KeyEvent.VK_DOWN, new Action() {

			@Override
			public void doAction() {
				game.move(p1, Direction.SOUTH);
			}
		}).addKey(KeyEvent.VK_LEFT, new Action() {

			@Override
			public void doAction() {
				game.move(p1, Direction.WEST);
			}
		}).addKey(KeyEvent.VK_RIGHT, new Action() {

            @Override
            public void doAction() {
                game.move(p1, Direction.EAST);
            }
        });

	}

	private Player getSinglePlayer(final Game game) {
		List<Player> players = game.getPlayers();
		if (players.isEmpty()) {
			throw new IllegalArgumentException("Game has 0 players.");
		}
		final Player p1 = players.get(0);
		return p1;
	}

	/**
	 * Creates and starts a JPac-Man game.
	 */
	public void launch() {
		game = makeGame();
		game.setLauncher(this);
		builder = new PacManUiBuilder().withDefaultButtons();
		builder.addButton("Identification", new Action()
        {
            @Override
            public void doAction() {
				Player player = game.getPlayers().get(0);
				boolean loggedIn = player.authenticate();
				if (loggedIn) {
					player.displayAchievements();
					pacManUI.refreshLevelChoices(player.getMaxLevelReached());
				}
			}
        });
        builder.addButton("New player", new Action()
        {
            @Override
            public void doAction()
            {
                game.getPlayers().get(0).createNewPlayer();
            }
        });
        builder.addButton("Stats", new Action()
        {
            @Override
            public void doAction()
            {
                game.getPlayers().get(0).displayProfileStats();
            }
        });
        addSinglePlayerKeys(builder, game);
        pacManUI = builder.build(game);
	buildWindow();
    }


    /**
	 * Disposes of the UI. For more information see {@link javax.swing.JFrame#dispose()}.
	 */
	public void dispose() {
		pacManUI.dispose();
	}

	/**
	 * Main execution method for the Launcher.
	 * 
	 * @param args
	 *            The command line arguments - which are ignored.
	 * @throws IOException
	 *             When a resource could not be read.
	 */
	public static void main(String[] args) throws IOException {
		new Launcher().launch();
	}

	/**
	 * Construct the window to choose the game mode (Spectator or control)
	 */
	public void buildWindow()
	{
		MyJDialogStrategy dialog = new MyJDialogStrategy(new JFrame(), "Strategy selection", "Choose a game mode and then click to start", builder,game, pacManUI);
		dialog.setSize(400, 200);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

    /**
     * Returns the next level and increments the currentLevel field.
     * If this was already the last level, simply restart it.
     * @return The new(and next) level
     */
    public Level nextLevel() {
        Level level = makeLevel(++currentLevel);
        if (level == null) {
            //the level could not be loaded, this means that the previous one was the final level
			//restart this last level and loop until player dies
            //(this level can't be finished without loosing at least one life so there will be an end)
            level = makeLevel(--currentLevel);
        }
        level.setIndex(currentLevel);
        return level;
    }

    /**
     * Simple getter for currentLevel
     * @return The id of the current level
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Sets the level to the one that has the given id (calls #Game.setLevel)
     * @param levelIndex The id of the level we want to switch to
     */
    public void setLevel(final int levelIndex) {
        Level level = makeLevel(levelIndex);
        level.setIndex(levelIndex);
        game.setLevel(level);
    }
}
