package nl.tudelft.jpacman;


import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.game.GameFactory;
import nl.tudelft.jpacman.level.IdentifiedPlayer;
import nl.tudelft.jpacman.level.LevelFactory;
import nl.tudelft.jpacman.level.PlayerFactory;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.sprite.PacManSprites;
import nl.tudelft.jpacman.ui.MyJDialogStrategy;
import nl.tudelft.jpacman.ui.PacManUI;
import nl.tudelft.jpacman.ui.PacManUiBuilder;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * Creates and launches the JPacMan UI.
 *
 * @author Jeroen Roosen
 */
public class Launcher {
    public static final int DIALOG_WIDTH = 400;
    public static final int DIALOG_HEIGHT = 200;
    private static final PacManSprites SPRITE_STORE = new PacManSprites();
    private PacManUI pacManUI;
    private Game game;

    /**
     * @return A new board factory using the sprite store from
     * {@link #getSpriteStore()}.
     */
    public static BoardFactory getBoardFactory() {
        return new BoardFactory(getSpriteStore());
    }

    /**
     * @return The default {@link PacManSprites}.
     */
    public static PacManSprites getSpriteStore() {
        return SPRITE_STORE;
    }

    /**
     * @return A new factory using the sprites from {@link #getSpriteStore()}
     * and the ghosts from {@link #getGhostFactory()}.
     */
    public static LevelFactory getLevelFactory() {
        return new LevelFactory(getSpriteStore(), getGhostFactory());
    }

    /**
     * @return A new factory using the sprites from {@link #getSpriteStore()}.
     */
    public static GhostFactory getGhostFactory() {
        return new GhostFactory(getSpriteStore());
    }

    /**
     * Main execution method for the Launcher.
     *
     * @param args The command line arguments - which are ignored.
     * @throws IOException When a resource could not be read.
     */
    public static void main(String[] args) throws IOException {
        new Launcher().launch();
    }

    /**
     * @return The game object this launcher will start when {@link #launch()}
     * is called.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Creates a new game using the level from {@link Game#makeLevel(int)}.
     *
     * @return a new Game.
     */
    public Game makeGame() {
        return getGameFactory().createSinglePlayerGame();
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
     * @param builder The {@link PacManUiBuilder} that will provide the UI.
     * @param game    The game that will process the events.
     */
    protected void addSinglePlayerKeys(final PacManUiBuilder builder,
                                       final Game game) {
        final IdentifiedPlayer p1 = game.getPlayers().get(0);

        builder.addKey(KeyEvent.VK_UP, () -> game.move(p1, Direction.NORTH))
                .addKey(KeyEvent.VK_DOWN, () -> game.move(p1, Direction.SOUTH))
                .addKey(KeyEvent.VK_LEFT, () -> game.move(p1, Direction.WEST))
                .addKey(KeyEvent.VK_RIGHT, () -> game.move(p1, Direction.EAST));
    }

    /**
     * Creates and starts a JPac-Man game.
     */
    public void launch() {
        game = makeGame();
        PacManUiBuilder builder = new PacManUiBuilder().withDefaultButtons();
        builder.addButton("Identification", () ->
        {
            final IdentifiedPlayer player = game.getPlayers().get(0);
            if (player.authenticate()) {
                try {
                    player.displayAchievements();
                    pacManUI.refreshLevelChoices(player.getMaxLevelReached());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        builder.addButton("New player", () -> game.getPlayers().get(0).createNewPlayer());
        builder.addButton("Stats", () -> game.getPlayers().get(0).displayProfileStats());
        addSinglePlayerKeys(builder, game);
        pacManUI = builder.build(game);
        final MyJDialogStrategy dialog =
                new MyJDialogStrategy(new JFrame(), builder, game, pacManUI);
        dialog.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        //prevents the user from closing the dialog via the upper-right corner "X"
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Disposes of the UI. For more information see {@link javax.swing.JFrame#dispose()}.
     */
    public void dispose() {
        pacManUI.dispose();
    }
}
