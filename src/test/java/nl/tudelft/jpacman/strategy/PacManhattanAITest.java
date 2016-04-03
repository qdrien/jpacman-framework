package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Pellet;
import nl.tudelft.jpacman.level.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

public class PacManhattanAITest {
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
     * Test to know if the square is safe or not for the player.
     * A square is safe to more thant 14 squares about the nearest ghost
     */
    @SuppressWarnings("methodlength")
    @Test
    public void safetySquareTest() {
        final Game game = launcher.getGame();
        final Player player = game.getPlayers().get(0);
        final PacManhattanAI AI = new PacManhattanAI(game);
        assertFalse(AI.isSafetySquare(player.getSquare()));
        assertFalse(AI.isSafetySquare(player.getSquare().getSquareAt(Direction.SOUTH)));
        assertFalse(AI.isSafetySquare(player.getSquare().getSquareAt(Direction.SOUTH).getSquareAt(Direction.SOUTH)));
        assertFalse(AI.isSafetySquare(player.getSquare().getSquareAt(Direction.SOUTH).getSquareAt(Direction.SOUTH).getSquareAt(Direction.SOUTH)));

        //Safety Square more than 14 squares about the nearest ghost
        assertTrue(AI.isSafetySquare(player.getSquare().getSquareAt(Direction.SOUTH).getSquareAt(Direction.SOUTH).getSquareAt(Direction.SOUTH).getSquareAt(Direction.SOUTH).getSquareAt(Direction.EAST).getSquareAt(Direction.EAST).getSquareAt(Direction.EAST).getSquareAt(Direction.EAST).getSquareAt(Direction.EAST).getSquareAt(Direction.EAST).getSquareAt(Direction.EAST).getSquareAt(Direction.EAST)));
    }

    /**
     * Test to get the list of valid neighbors about the player's square.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void getValidNeighborsTest() {
        final Game game = launcher.getGame();
        final Player player = game.getPlayers().get(0);
        final PacManhattanAI AI = new PacManhattanAI(game);

        List<Square> neighborsList = AI.getValidNeighbors(player.getSquare());
        assertTrue(neighborsList.contains(player.getSquare().getSquareAt(Direction.EAST)));
        assertTrue(neighborsList.contains(player.getSquare().getSquareAt(Direction.WEST)));

        //Invalid because walls
        assertFalse(neighborsList.contains(player.getSquare().getSquareAt(Direction.NORTH)));
        assertFalse(neighborsList.contains(player.getSquare().getSquareAt(Direction.SOUTH)));

        game.start();
        game.move(player, Direction.EAST);
        game.move(player, Direction.EAST);
        game.move(player, Direction.EAST);
        game.move(player, Direction.EAST);
        game.move(player, Direction.EAST);
        game.move(player, Direction.EAST);

        List<Square> neighborsList2 = AI.getValidNeighbors(player.getSquare());
        assertTrue(neighborsList2.contains(player.getSquare().getSquareAt(Direction.NORTH)));
        assertTrue(neighborsList2.contains(player.getSquare().getSquareAt(Direction.WEST)));
        assertTrue(neighborsList2.contains(player.getSquare().getSquareAt(Direction.SOUTH)));

        //Invalid because walls
        assertFalse(neighborsList2.contains(player.getSquare().getSquareAt(Direction.EAST)));

    }

    /**
     * Test to get the nearest safety square about the initial player position.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void BFSNearestSafetySquareTest() {
        final Game game = launcher.getGame();
        final Player player = game.getPlayers().get(0);
        assertNotNull(player.getSquare());
        assertEquals(player.getSquare().getX(), 11);
        assertEquals(player.getSquare().getY(), 15);
        final PacManhattanAI AI = new PacManhattanAI(game);

        final Square square = AI.BFSNearestSafetySquare();

        assertNotNull(square);
        assertSame(square.getX(), 19);
        assertSame(square.getY(), 17);
    }

    /**
     * Test to get the next move about the initial player position.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void nextMoveTest() {
        final Game game = launcher.getGame();
        final Player player = game.getPlayers().get(0);
        assertNotNull(player.getSquare());
        assertEquals(player.getSquare().getX(), 11);
        assertEquals(player.getSquare().getY(), 15);
        final PacManhattanAI AI = new PacManhattanAI(game);

        Direction nextMove = AI.nextMove();
        assertEquals(nextMove, Direction.EAST);

        game.start();

        game.move(player, nextMove);

        //New position
        assertNotNull(player.getSquare());
        assertEquals(player.getSquare().getX(), 12);
        assertEquals(player.getSquare().getY(), 15);

        assertEquals(AI.nextMove(), Direction.EAST);

        game.move(player, nextMove);

        //New position
        assertNotNull(player.getSquare());
        assertEquals(player.getSquare().getX(), 13);
        assertEquals(player.getSquare().getY(), 15);
    }

    /**
     * Test to get the nearest safe square where there is a pellet about the initial player position.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void safetyPelletSquareTest() {
        final Game game = launcher.getGame();
        final Player player = game.getPlayers().get(0);
        assertNotNull(player.getSquare());
        assertEquals(player.getSquare().getX(), 11);
        assertEquals(player.getSquare().getY(), 15);
        final PacManhattanAI AI = new PacManhattanAI(game);

        Square safetyPelletSquare = AI.BFSNearestSafetyPelletSquare();
        assertTrue(safetyPelletSquare.getOccupants().get(0) instanceof Pellet);

        assertEquals(player.getSquare().getSquareAt(Direction.EAST), safetyPelletSquare);

        game.start();

        game.move(player, Direction.EAST);

        assertEquals(player.getSquare(), safetyPelletSquare);

        //New position
        assertNotNull(player.getSquare());
        assertEquals(player.getSquare().getX(), 12);
        assertEquals(player.getSquare().getY(), 15);
    }

    /**
     * Test to get the direction about a path.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void convertPathToDirectionTest() {
        final Game game = launcher.getGame();
        final Player player = game.getPlayers().get(0);
        assertNotNull(player.getSquare());
        assertEquals(player.getSquare().getX(), 11);
        assertEquals(player.getSquare().getY(), 15);

        Square square1 = player.getSquare();
        Square square2 = square1.getSquareAt(Direction.WEST);
        Square square3 = square2.getSquareAt(Direction.WEST);
        Square square4 = square3.getSquareAt(Direction.WEST);
        Square square5 = square4.getSquareAt(Direction.WEST);

        ArrayList<Square> squaresList = new ArrayList<>();

        squaresList.add(square1);
        squaresList.add(square2);
        squaresList.add(square3);
        squaresList.add(square4);
        squaresList.add(square5);

        assertNotNull(squaresList);
        assertTrue(squaresList.contains(square1));
        assertTrue(squaresList.contains(square2));
        assertTrue(squaresList.contains(square3));
        assertTrue(squaresList.contains(square4));
        assertTrue(squaresList.contains(square5));

        Deque<Direction> dir = new PacManhattanAI(game).convertPathToDirection(squaresList);

        assertEquals(dir.getFirst(), Direction.WEST);
        assertEquals(dir.getLast(), Direction.WEST);

    }

    /**
     * Test to get the direction about a path.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void convertPathToDirectionTest2() {
        final Game game = launcher.getGame();
        final Player player = game.getPlayers().get(0);
        assertNotNull(player.getSquare());
        assertEquals(player.getSquare().getX(), 11);
        assertEquals(player.getSquare().getY(), 15);

        Square square1 = player.getSquare();
        Square square2 = square1.getSquareAt(Direction.EAST);
        Square square3 = square2.getSquareAt(Direction.WEST);
        Square square4 = square3.getSquareAt(Direction.EAST);
        Square square5 = square4.getSquareAt(Direction.WEST);
        Square square6 = square4.getSquareAt(Direction.WEST);


        ArrayList<Square> squaresList = new ArrayList<>();

        squaresList.add(square1);
        squaresList.add(square2);
        squaresList.add(square3);
        squaresList.add(square4);
        squaresList.add(square5);
        squaresList.add(square6);

        assertNotNull(squaresList);
        assertTrue(squaresList.contains(square1));
        assertTrue(squaresList.contains(square2));
        assertTrue(squaresList.contains(square3));
        assertTrue(squaresList.contains(square4));
        assertTrue(squaresList.contains(square5));
        assertTrue(squaresList.contains(square6));


        Deque<Direction> dir = new PacManhattanAI(game).convertPathToDirection(squaresList);

        assertEquals(dir.getFirst(), Direction.EAST);
        assertEquals(dir.getLast(), Direction.NORTH);
    }

    /**
     * Test the choose of the direction in the last resort (No Best Direction found).
     */
    @SuppressWarnings("methodlength")
    @Test
    public void hurryMoveTest() {
        final Game game = launcher.getGame();
        final Player player = game.getPlayers().get(0);
        assertNotNull(player.getSquare());
        assertEquals(player.getSquare().getX(), 11);
        assertEquals(player.getSquare().getY(), 15);

        final PacManhattanAI AI = new PacManhattanAI(game);

        assertEquals(AI.hurryMove(), Direction.EAST);

        assertEquals(AI.getGhostDstThreshold(), 14);
        AI.setGhostDstThreshold(7);
        assertEquals(AI.getGhostDstThreshold(), 7);
    }
}
