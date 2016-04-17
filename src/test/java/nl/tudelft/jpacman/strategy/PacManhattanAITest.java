package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.IdentifiedPlayer;
import nl.tudelft.jpacman.level.Pellet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

/**
 * AI test in the game.
 */
@SuppressWarnings("checkstyle:magicnumber")
public class PacManhattanAITest {
    /**
     * Launcher in the test.
     */
    private Launcher launcher;

    /**
     * Launch the user interface.
     */
    @Before
    public void setUpPacman() {
        launcher = new Launcher();
        launcher.launch(true);
    }

    /**
     * Quit the user interface when we're done.
     */
    @After
    public void tearDown() {
        launcher.dispose();
    }

    /**
     * Test to know if the square is safe or not for the player.
     * A square is safe to more thant 14 squares about the nearest ghost
     */
    @SuppressWarnings("methodlength")
    @Test
    public void safetySquareTest() {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);
        final PacManhattanAI aiStrategy = new PacManhattanAI(game);
        assertFalse("The square should be dangerous",
                aiStrategy.isSafetySquare(player.getSquare()));
        assertFalse("The square should be dangerous",
                aiStrategy.isSafetySquare(player.getSquare().getSquareAt(Direction.SOUTH)));
        assertFalse("The square should be dangerous",
                aiStrategy.isSafetySquare(player.getSquare().getSquareAt(Direction.SOUTH)
                .getSquareAt(Direction.SOUTH)));
        assertFalse("The square should be dangerous",
                aiStrategy.isSafetySquare(player.getSquare().getSquareAt(Direction.SOUTH)
                .getSquareAt(Direction.SOUTH).getSquareAt(Direction.SOUTH)));

        //Safety Square more than 14 squares about the nearest ghost
        assertTrue("The square should be safe",
                aiStrategy.isSafetySquare(player.getSquare().getSquareAt(Direction.SOUTH)
                .getSquareAt(Direction.SOUTH).getSquareAt(Direction.SOUTH)
                .getSquareAt(Direction.SOUTH).getSquareAt(Direction.EAST)
                .getSquareAt(Direction.EAST).getSquareAt(Direction.EAST)
                .getSquareAt(Direction.EAST).getSquareAt(Direction.EAST)
                .getSquareAt(Direction.EAST).getSquareAt(Direction.EAST)
                .getSquareAt(Direction.EAST)));
    }

    /**
     * Test to get the list of valid neighbors about the player's square.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void getValidNeighborsTest() {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);

        final List<Square> neighborsList = AStarPath.getValidNeighbors(player.getSquare());
        assertTrue("The neighbors list should contain this neighbour", neighborsList.contains(player.getSquare().getSquareAt(Direction.EAST)));
        assertTrue("The neighbors list should contain this neighbour", neighborsList.contains(player.getSquare().getSquareAt(Direction.WEST)));

        //Invalid because walls
        assertFalse("The neighbors list shouldn't contain this neighbour", neighborsList.contains(player.getSquare().getSquareAt(Direction.NORTH)));
        assertFalse("The neighbors list shouldn't contain this neighbour", neighborsList.contains(player.getSquare().getSquareAt(Direction.SOUTH)));

        game.start();
        game.move(player, Direction.EAST);
        game.move(player, Direction.EAST);
        game.move(player, Direction.EAST);
        game.move(player, Direction.EAST);
        game.move(player, Direction.EAST);
        game.move(player, Direction.EAST);

        final List<Square> neighborsList2 = AStarPath.getValidNeighbors(player.getSquare());
        assertTrue("The neighbors list should contain this neighbour", neighborsList2.contains(player.getSquare().getSquareAt(Direction.NORTH)));
        assertTrue("The neighbors list should contain this neighbour", neighborsList2.contains(player.getSquare().getSquareAt(Direction.WEST)));
        assertTrue("The neighbors list should contain this neighbour", neighborsList2.contains(player.getSquare().getSquareAt(Direction.SOUTH)));

        //Invalid because walls
        assertFalse("The neighbors list shouldn't contain this neighbour", neighborsList2.contains(player.getSquare().getSquareAt(Direction.EAST)));

    }

    /**
     * Test to get the nearest safety square about the initial player position.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void bfsNearestSafetySquareTest() {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);
        assertNotNull("The player has not been instantiated", player.getSquare());
        assertEquals("Player square incorrect", player.getSquare().getX(), 11);
        assertEquals("Player square incorrect", player.getSquare().getY(), 15);
        final PacManhattanAI aiStrategy = new PacManhattanAI(game);

        final Square square = aiStrategy.bfsNearestSafetySquare();

        assertNotNull("Square has not been instantiated", square);
        assertSame("Dimension incorrect", square.getX(), 19);
        assertSame("Dimension incorrect", square.getY(), 17);
    }

    /**
     * Test to get the next move about the initial player position.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void nextMoveTest() {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);
        assertNotNull("The player has not been instantiated", player.getSquare());
        assertEquals("Player square incorrect", player.getSquare().getX(), 11);
        assertEquals("Player square incorrect", player.getSquare().getY(), 15);
        final PacManhattanAI aiStrategy = new PacManhattanAI(game);

        final Direction nextMove = aiStrategy.nextMove();
        assertEquals("Direction of the player incorrect", nextMove, Direction.EAST);

        game.start();

        game.move(player, nextMove);

        //New position
        assertNotNull("The player's square is incorrect", player.getSquare());
        assertEquals("The player's square is incorrect", player.getSquare().getX(), 12);
        assertEquals("The player's square is incorrect", player.getSquare().getY(), 15);

        assertEquals("Direction of the player incorrect", aiStrategy.nextMove(), Direction.EAST);

        game.move(player, nextMove);

        //New position
        assertNotNull("The player's square is incorrect", player.getSquare());
        assertEquals("The player's square is incorrect", player.getSquare().getX(), 13);
        assertEquals("The player's square is incorrect", player.getSquare().getY(), 15);
    }

    /**
     * Test to get the nearest safe square where there is a pellet about
     * the initial player position.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void safetyPelletSquareTest() {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);
        assertNotNull("The player has not been instantiated", player.getSquare());
        assertEquals("The player's square is incorrect", player.getSquare().getX(), 11);
        assertEquals("The player's square is incorrect", player.getSquare().getY(), 15);

        final PacManhattanAI aiStrategy = new PacManhattanAI(game);

        final Square targetSquare = aiStrategy.bfsNearestSafetyPelletSquare();
        assertTrue("It should have a pellet", targetSquare.getOccupants().get(0) instanceof Pellet);

        assertEquals("The square is incorrect", player.getSquare().getSquareAt(Direction.EAST), targetSquare);

        game.start();

        game.move(player, Direction.EAST);

        assertEquals("The square is incorrect", player.getSquare(), targetSquare);

        //New position
        assertNotNull("The player's square is incorrect", player.getSquare());
        assertEquals("The player's square is incorrect", player.getSquare().getX(), 12);
        assertEquals("The player's square is incorrect", player.getSquare().getY(), 15);
    }

    /**
     * Test to get the direction about a path.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void convertPathToDirectionTest() {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);
        assertNotNull("The player has not been instantiated", player.getSquare());
        assertEquals("The player's square is incorrect", player.getSquare().getX(), 11);
        assertEquals("The player's square is incorrect", player.getSquare().getY(), 15);

        final Square square1 = player.getSquare();
        final Square square2 = square1.getSquareAt(Direction.WEST);
        final Square square3 = square2.getSquareAt(Direction.WEST);
        final Square square4 = square3.getSquareAt(Direction.WEST);
        final Square square5 = square4.getSquareAt(Direction.WEST);

        final ArrayList<Square> squaresList = new ArrayList<>();

        squaresList.add(square1);
        squaresList.add(square2);
        squaresList.add(square3);
        squaresList.add(square4);
        squaresList.add(square5);

        assertNotNull("The list has not been instantiated", squaresList);
        assertTrue("The computed list should contain this square", squaresList.contains(square1));
        assertTrue("The computed list should contain this square", squaresList.contains(square2));
        assertTrue("The computed list should contain this square", squaresList.contains(square3));
        assertTrue("The computed list should contain this square", squaresList.contains(square4));
        assertTrue("The computed list should contain this square", squaresList.contains(square5));

        final Deque<Direction> dir = new PacManhattanAI(game).convertPathToDirection(squaresList);

        assertEquals("Direction incorrect", dir.getFirst(), Direction.WEST);
        assertEquals("Direction incorrect", dir.getLast(), Direction.WEST);

    }

    /**
     * Test to get the direction about a path.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void convertPathToDirectionTest2() {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);
        assertNotNull("The player has not been instantiated", player.getSquare());
        assertEquals("The player's square is incorrect", player.getSquare().getX(), 11);
        assertEquals("The player's square is incorrect", player.getSquare().getY(), 15);

        final Square square1 = player.getSquare();
        final Square square2 = square1.getSquareAt(Direction.EAST);
        final Square square3 = square2.getSquareAt(Direction.WEST);
        final Square square4 = square3.getSquareAt(Direction.EAST);
        final Square square5 = square4.getSquareAt(Direction.WEST);
        final Square square6 = square4.getSquareAt(Direction.WEST);


        final ArrayList<Square> squaresList = new ArrayList<>();

        squaresList.add(square1);
        squaresList.add(square2);
        squaresList.add(square3);
        squaresList.add(square4);
        squaresList.add(square5);
        squaresList.add(square6);

        assertNotNull("The list has not been instantiated", squaresList);
        assertTrue("The computed list should contain this square", squaresList.contains(square1));
        assertTrue("The computed list should contain this square", squaresList.contains(square2));
        assertTrue("The computed list should contain this square", squaresList.contains(square3));
        assertTrue("The computed list should contain this square", squaresList.contains(square4));
        assertTrue("The computed list should contain this square", squaresList.contains(square5));
        assertTrue("The computed list should contain this square", squaresList.contains(square6));


        final Deque<Direction> dir = new PacManhattanAI(game).convertPathToDirection(squaresList);

        assertEquals("Direction incorrect", dir.getFirst(), Direction.EAST);
        assertEquals("Direction incorrect", dir.getLast(), Direction.NORTH);
    }

    /**
     * Test the choose of the direction in the last resort (No Best Direction found).
     */
    @SuppressWarnings("methodlength")
    @Test
    public void hurryMoveTest() {
        final Game game = launcher.getGame();
        final IdentifiedPlayer player = game.getPlayers().get(0);
        assertNotNull("The player has not been instantiated", player.getSquare());
        assertEquals("The player's square is incorrect", player.getSquare().getX(), 11);
        assertEquals("The player's square is incorrect", player.getSquare().getY(), 15);

        final PacManhattanAI aiStrategy = new PacManhattanAI(game);

        assertEquals("Direction incorrect", aiStrategy.hurryMove(), Direction.EAST);

        assertEquals("Distance incorrect", aiStrategy.getGhostDstThreshold(), 14);
        PacManhattanAI.setGhostDstThreshold(7);
        assertEquals("Distance incorrect", aiStrategy.getGhostDstThreshold(), 7);
    }
}
