package nl.tudelft.jpacman.game;

import nl.tudelft.jpacman.level.PlayerFactory;

/**
 * Factory that provides Game objects.
 *
 * @author Jeroen Roosen
 */
public class GameFactory {

    /**
     * The factory providing the player objects.
     */
    private final PlayerFactory playerFact;

    /**
     * Creates a new game factory.
     *
     * @param playerFactory The factory providing the player objects.
     */
    public GameFactory(PlayerFactory playerFactory) {
        this.playerFact = playerFactory;
    }

    /**
     * Creates a game for a single level with one player.
     *
     * @return A new single player game.
     */
    public Game createSinglePlayerGame() {
        return new SinglePlayerGame(playerFact.createPacMan());
    }

}
