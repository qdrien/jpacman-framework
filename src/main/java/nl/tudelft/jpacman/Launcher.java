package nl.tudelft.jpacman;


import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.game.Achievement;
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
import java.io.IOException;

/**
 * Creates and launches the JPacMan UI.
 *
 * @author Jeroen Roosen
 */
public class Launcher {
    public static final int DIALOG_HEIGHT = 200;
    private static final int DIALOG_WIDTH = 400;
    private static final PacManSprites SPRITE_STORE = new PacManSprites();
    private static PacManUI pacManUI;
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
        new Launcher().launch(false);
    }

    /**
     * Returns the UI.
     *
     * @return The UI.
     */
    public static PacManUI getPacManUI() {
        return pacManUI;
    }

    /**
     * @return The game object this launcher will start when launch
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
     * Creates and starts a JPac-Man game.
     *
     * @param test a boolean set to true if called by unit test, false otherwise
     */
    @SuppressWarnings("checkstyle:linelength")
    public void launch(boolean test) {
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
        builder.addButton("Stats", () ->
        {
            try {
                final IdentifiedPlayer player = game.getPlayers().get(0);
                if (player.displayProfileStats()) {
                    JOptionPane.showMessageDialog(null, Achievement.offerAchievements(player), "Recommended achievements.", JOptionPane.PLAIN_MESSAGE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pacManUI = builder.build(game);
        if (!test) {
            final MyJDialogStrategy dialog =
                    new MyJDialogStrategy(new JFrame(), builder, game, pacManUI);
            dialog.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
            //prevents the user from closing the dialog via the upper-right corner "X"
            dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        }
    }

    /**
     * Disposes of the UI. For more information see {@link javax.swing.JFrame#dispose()}.
     */
    public void dispose() {
        pacManUI.dispose();
    }
}
