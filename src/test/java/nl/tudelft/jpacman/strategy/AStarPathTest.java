package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.IdentifiedPlayer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test for the astarPath class containing methods to calculate the best move to apply by the AI.
 */
@SuppressWarnings("checkstyle:magicnumber")
public class AStarPathTest {
    private static final double EPSILON = .1;
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
     * Test for the constructor of a astarPath object.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void constructorTest() {
        final Game game = launcher.getGame();
        assertFalse(game.isInProgress());
        assertNotNull(game);

        final AStarPath aStarPath = new AStarPath(game);

        assertNotNull(aStarPath);
        final Square square = game.getPlayers().get(0).getSquare();
        aStarPath.setGoal(square);
        assertNotNull(aStarPath.isGoal(square));
        assertTrue(aStarPath.isGoal(square));

        final Square square1 = square.getSquareAt(Direction.EAST);

        assertNotNull(aStarPath.isGoal(square1));
        assertFalse(aStarPath.isGoal(square1));

        assertNotNull(aStarPath.getCost());

    }

    /**
     * Test to compute the manhattan distance between two points.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void manhattanDistanceTest() {

        assertEquals(AStarPath.manhattanDistance(0, 0, 0, 0), 0.0, EPSILON);
        assertEquals(AStarPath.manhattanDistance(0, 1, 1, 1), 1.0, EPSILON);
        assertEquals(AStarPath.manhattanDistance(5, 3, 2, 1), 5.0, EPSILON);
        assertEquals(AStarPath.manhattanDistance(1, 4, 2, 0), 5.0, EPSILON);
        assertEquals(AStarPath.manhattanDistance(-1, -8, -3, -1), 9.0, EPSILON);
        assertEquals(AStarPath.manhattanDistance(-0, -0, -0, -0), 0.0, EPSILON);
        assertEquals(AStarPath.manhattanDistance(-2, -2, -1, -1), 2.0, EPSILON);
    }

    /**
     * Test to the initialisation of constants in the game.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void constantTest() {
        final Game game = launcher.getGame();
        assertFalse(game.isInProgress());
        assertNotNull(game);

        final AStarPath aStarPath = new AStarPath(game);

        assertNotNull(aStarPath);
        final IdentifiedPlayer player = game.getPlayers().get(0);

        assertNotNull(player);
        assertNotNull(player.getSquare());

        assertEquals(AStarPath.PELLET_COST, 1.0, EPSILON);
        assertEquals(AStarPath.EMPTY_COST, 5.0, EPSILON);
        assertEquals(AStarPath.GHOST_COST, 1000000.0, EPSILON);
        assertEquals(AStarPath.NEAREST_GHOST_COST, 500.0, EPSILON);
        assertEquals(AStarPath.DST_THRESHOLD, 3, EPSILON);

    }

    /**
     * Test the list of valid neighbors.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void validNeighboursTest() {
        final Game game = launcher.getGame();
        assertFalse(game.isInProgress());
        assertNotNull(game);

        final AStarPath aStarPath = new AStarPath(game);

        assertNotNull(aStarPath);
        final IdentifiedPlayer player = game.getPlayers().get(0);
        final Square square = player.getSquare();

        assertNotNull(player);
        assertNotNull(square);

        List<Square> neighbourList = aStarPath.getValidNeighbors(square, player);

        //The accessible square is to East and West. (North and south are walls)
        assertNotNull(neighbourList);
        assertTrue(neighbourList.contains(player.getSquare().getSquareAt(Direction.EAST)));
        assertTrue(neighbourList.contains(player.getSquare().getSquareAt(Direction.WEST)));

        assertFalse(neighbourList.contains(player.getSquare().getSquareAt(Direction.NORTH)));
        assertFalse(neighbourList.contains(player.getSquare().getSquareAt(Direction.SOUTH)));

        //Player move to have others neighbors
        game.start();
        assertTrue(game.isInProgress());
        game.move(player, Direction.EAST);
        Square square2 = player.getSquare();

        assertFalse(square.equals(square2));

        List<Square> neigborList2 = aStarPath.getValidNeighbors(square2, player);

        //The accessible square is to East and West and North (south is a wall)

        assertNotNull(neigborList2);
        assertTrue(neigborList2.contains(square2.getSquareAt(Direction.EAST)));
        assertTrue(neigborList2.contains(square2.getSquareAt(Direction.WEST)));
        assertTrue(neigborList2.contains(square2.getSquareAt(Direction.NORTH)));

        assertFalse(neigborList2.contains(square2.getSquareAt(Direction.SOUTH)));

    }

    /**
     * Test the list of valid neighbors.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void validNeighborsTest2() {
        final Game game = launcher.getGame();
        assertFalse(game.isInProgress());
        assertNotNull(game);

        AStarPath aStarPath = new AStarPath(game);

        assertNotNull(aStarPath);
        final IdentifiedPlayer player = game.getPlayers().get(0);
        final Square square = player.getSquare();

        assertNotNull(player);
        assertNotNull(square);

        List<Square> neigborList = aStarPath.getValidNeighbors(square, player);

        //The accessible square is to East and West. (North and south are walls)
        assertNotNull(neigborList);
        assertTrue(neigborList.contains(player.getSquare().getSquareAt(Direction.EAST)));
        assertTrue(neigborList.contains(player.getSquare().getSquareAt(Direction.WEST)));

        assertFalse(neigborList.contains(player.getSquare().getSquareAt(Direction.NORTH)));
        assertFalse(neigborList.contains(player.getSquare().getSquareAt(Direction.SOUTH)));

        //Player move to have others neighbors
        game.start();
        assertTrue(game.isInProgress());
        game.move(player, Direction.WEST);
        final Square square2 = player.getSquare();

        assertFalse(square.equals(square2));


        List<Square> neigborList2 = aStarPath.getValidNeighbors(square2, player);

        //The accessible square is to East and West and North (south is a wall)

        assertNotNull(neigborList2);
        assertTrue(neigborList2.contains(square2.getSquareAt(Direction.EAST)));
        assertTrue(neigborList2.contains(square2.getSquareAt(Direction.WEST)));
        assertTrue(neigborList2.contains(square2.getSquareAt(Direction.NORTH)));

        assertFalse(neigborList2.contains(square2.getSquareAt(Direction.SOUTH)));

        //Player move to have others neighbors
        game.move(player, Direction.WEST);
        game.move(player, Direction.WEST);
        game.move(player, Direction.WEST);
        game.move(player, Direction.WEST);
        game.move(player, Direction.WEST);
        game.move(player, Direction.WEST);

        final Square square3 = player.getSquare();

        assertFalse(square.equals(square3));
        assertFalse(square2.equals(square3));

        List<Square> neigborList3 = aStarPath.getValidNeighbors(square3, player);

        assertNotNull(neigborList3);
        //All move is possible except to left (West)
        assertTrue(neigborList3.contains(square3.getSquareAt(Direction.EAST)));
        assertFalse(neigborList3.contains(square3.getSquareAt(Direction.WEST)));
        assertTrue(neigborList3.contains(square3.getSquareAt(Direction.NORTH)));
        assertTrue(neigborList3.contains(square3.getSquareAt(Direction.SOUTH)));
    }

    /**
     * test to know if the square is near of a ghost or not.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void nearestGhostTest() {
        final Game game = launcher.getGame();
        assertFalse(game.isInProgress());
        assertNotNull(game);

        AStarPath aStarPath = new AStarPath(game);

        assertNotNull(aStarPath);
        final IdentifiedPlayer player = game.getPlayers().get(0);
        final Square square = player.getSquare();

        assertNotNull(player);
        assertNotNull(square);

        assertFalse(aStarPath.nearestGhosts(player.getSquare()));
        assertFalse(aStarPath.nearestGhosts(player.getSquare().getSquareAt(Direction.NORTH)));
        assertFalse(aStarPath.nearestGhosts(player.getSquare()
                .getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)));
        assertFalse(aStarPath.nearestGhosts(player.getSquare()
                .getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)
                .getSquareAt(Direction.NORTH)));


        //The next north case is dangerous.
        assertTrue(aStarPath.nearestGhosts(player.getSquare().getSquareAt(Direction.NORTH)
                .getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)
                .getSquareAt(Direction.NORTH)));
        assertTrue(aStarPath.nearestGhosts(player.getSquare().getSquareAt(Direction.NORTH)
                .getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)
                .getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)));

        assertFalse(aStarPath.nearestGhosts(player.getSquare().getSquareAt(Direction.EAST)));
        assertFalse(aStarPath.nearestGhosts(player.getSquare().getSquareAt(Direction.EAST)
                .getSquareAt(Direction.EAST)));

        assertFalse(aStarPath.nearestGhosts(player.getSquare().getSquareAt(Direction.WEST)));
        assertFalse(aStarPath.nearestGhosts(player.getSquare().getSquareAt(Direction.WEST)
                .getSquareAt(Direction.WEST)));
    }

    /**
     * Test the AStar method.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void gTest() {
        final Game game = launcher.getGame();
        assertFalse(game.isInProgress());
        assertNotNull(game);

        AStarPath aStarPath = new AStarPath(game);

        assertNotNull(aStarPath);
        final IdentifiedPlayer player = game.getPlayers().get(0);
        final Square square = player.getSquare();

        assertNotNull(player);
        assertNotNull(square);

        final Square origin = player.getSquare();
        final Square destination = player.getSquare().getSquareAt(Direction.EAST);
        final Square destination2 = player.getSquare().getSquareAt(Direction.EAST)
                .getSquareAt(Direction.EAST);

        final Square destination3 = player.getSquare().getSquareAt(Direction.WEST);
        final Square destination4 = player.getSquare().getSquareAt(Direction.WEST)
                .getSquareAt(Direction.WEST);

        //There is only Pellet
        assertEquals(aStarPath.g(origin, destination), Double.valueOf(1.0));
        assertEquals(aStarPath.g(origin, destination2), Double.valueOf(1.0));
        assertEquals(aStarPath.g(origin, destination3), Double.valueOf(1.0));
        assertEquals(aStarPath.g(origin, destination4), Double.valueOf(1.0));
        assertEquals(aStarPath.g(destination, destination4), Double.valueOf(1.0));
        assertEquals(aStarPath.g(destination3, destination4), Double.valueOf(1.0));

        Square destinationNearestGhost = player.getSquare().getSquareAt(Direction.NORTH)
                .getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)
                .getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH);
        assertEquals(aStarPath.g(origin, destinationNearestGhost), Double.valueOf(500.0));

    }

    /**
     * Test the AStar method.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void hTest() {
        final Game game = launcher.getGame();
        assertFalse(game.isInProgress());
        assertNotNull(game);

        final AStarPath aStarPath = new AStarPath(game);

        assertNotNull(aStarPath);
        final IdentifiedPlayer player = game.getPlayers().get(0);
        final Square square = player.getSquare();

        assertNotNull(player);
        assertNotNull(square);

        final Square origin = player.getSquare();
        final Square destination = player.getSquare().getSquareAt(Direction.EAST);
        final Square destination2 = player.getSquare().getSquareAt(Direction.EAST)
                .getSquareAt(Direction.EAST);

        final Square destination3 = player.getSquare().getSquareAt(Direction.WEST);
        final Square destination4 = player.getSquare().getSquareAt(Direction.WEST)
                .getSquareAt(Direction.WEST);

        assertEquals(aStarPath.h(origin, destination), Double.valueOf(1.0));
        assertEquals(aStarPath.h(origin, destination2), Double.valueOf(2.0));

        assertEquals(aStarPath.h(origin, destination3), Double.valueOf(1.0));
        assertEquals(aStarPath.h(origin, destination4), Double.valueOf(2.0));

        assertEquals(aStarPath.h(destination, destination4), Double.valueOf(3.0));
        assertEquals(aStarPath.h(destination3, destination4), Double.valueOf(1.0));
    }
}
