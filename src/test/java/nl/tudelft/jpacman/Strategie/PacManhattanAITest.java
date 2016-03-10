package nl.tudelft.jpacman.Strategie;

import junit.framework.Assert;
import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.strategy.PacManhattanAI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Nicolas Leemans on 8/03/16.
 */
public class PacManhattanAITest
{
    private Launcher launcher;

    /**
     * Launch the user interface.
     */
    @Before
    public void setUpPacman() {
        launcher = new Launcher();
        launcher.launch();
    }

    /**
     * Quit the user interface when we're done.
     */
    @After
    public void tearDown() {
        launcher.dispose();
    }

    /**
     * Launch the game, and imitate what would happen in a typical game.
     * The test is only a smoke test, and not a focused small test.
     * Therefore it is OK that the method is a bit too long.
     *
     * @throws InterruptedException Since we're sleeping in this test.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void safetySquareTest()
    {
        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);
        PacManhattanAI AI = new PacManhattanAI(game);
        assertFalse(AI.isSafetySquare(player.getSquare()));
        assertFalse(AI.isSafetySquare(player.getSquare().getSquareAt(Direction.SOUTH)));
        assertFalse(AI.isSafetySquare(player.getSquare().getSquareAt(Direction.SOUTH).getSquareAt(Direction.SOUTH)));
        assertFalse(AI.isSafetySquare(player.getSquare().getSquareAt(Direction.SOUTH).getSquareAt(Direction.SOUTH).getSquareAt(Direction.SOUTH)));

        //Safety Square more than 14 squares about the nearest ghost
        assertTrue(AI.isSafetySquare(player.getSquare().getSquareAt(Direction.SOUTH).getSquareAt(Direction.SOUTH).getSquareAt(Direction.SOUTH).getSquareAt(Direction.SOUTH).getSquareAt(Direction.EAST).getSquareAt(Direction.EAST).getSquareAt(Direction.EAST).getSquareAt(Direction.EAST).getSquareAt(Direction.EAST).getSquareAt(Direction.EAST).getSquareAt(Direction.EAST).getSquareAt(Direction.EAST)));
    }

    @SuppressWarnings("methodlength")
    @Test
    public void getValidNeighborsTest()
    {
        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);
        PacManhattanAI AI = new PacManhattanAI(game);

        List<Square> neighborsList = AI.getValidNeighbors(player.getSquare());
        assertTrue(neighborsList.contains(player.getSquare().getSquareAt(Direction.EAST)));
        assertTrue(neighborsList.contains(player.getSquare().getSquareAt(Direction.WEST)));

        //Invalid because walls
        assertFalse(neighborsList.contains(player.getSquare().getSquareAt(Direction.NORTH)));
        assertFalse(neighborsList.contains(player.getSquare().getSquareAt(Direction.SOUTH)));

        game.start();
        game.move(player,Direction.EAST);
        game.move(player,Direction.EAST);
        game.move(player,Direction.EAST);
        game.move(player,Direction.EAST);
        game.move(player,Direction.EAST);
        game.move(player,Direction.EAST);

        List<Square> neighborsList2 = AI.getValidNeighbors(player.getSquare());
        assertTrue(neighborsList2.contains(player.getSquare().getSquareAt(Direction.NORTH)));
        assertTrue(neighborsList2.contains(player.getSquare().getSquareAt(Direction.WEST)));
        assertTrue(neighborsList2.contains(player.getSquare().getSquareAt(Direction.SOUTH)));

        //Invalid because walls
        assertFalse(neighborsList2.contains(player.getSquare().getSquareAt(Direction.EAST)));

    }
    @SuppressWarnings("methodlength")
    @Test
    public void BFSNearestSafetySquareTest()
    {
        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);
        PacManhattanAI AI = new PacManhattanAI(game);

        Square square = AI.BFSNearestSafetySquare();

        assertNotNull(square);
        assertTrue(square.getX() == 19);
        assertTrue(square.getY() == 17);
    }
}
