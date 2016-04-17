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
 * Test for the astarPath class containing methods
 * to calculate the best move to apply by the AI.
 */
@SuppressWarnings("checkstyle:magicnumber")
public class AStarPathTest {
    /**
     * Epsilon value to test.
     */
    private static final double EPSILON = .1;
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
     * Test for the constructor of a astarPath object.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void constructorTest() {
        final Game game = launcher.getGame();
        assertFalse("Game is in progress", game.isInProgress());
        assertNotNull("Game hasn't been instantiated", game);

        final AStarPath aStarPath = new AStarPath(game);

        assertNotNull("The aStar Path hasn't been instantiated", aStarPath);
        final Square square = game.getPlayers().get(0).getSquare();
        aStarPath.setGoal(square);
        assertNotNull("The goal case hasn't been instantiated", aStarPath.isGoal(square));
        assertTrue("The case goal should be correct", aStarPath.isGoal(square));

        final Square square1 = square.getSquareAt(Direction.EAST);

        assertNotNull("The goal case hasn't been instantiated", aStarPath.isGoal(square1));
        assertFalse("The case goal shouldn't be correct", aStarPath.isGoal(square1));

        assertNotNull("The cost hasn't been instantiated", aStarPath.getCost());

    }

    /**
     * Test to compute the manhattan distance between two points.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void manhattanDistanceTest() {

        assertEquals("Manhattan distance is not correct",
                AStarPath.manhattanDistance(0, 0, 0, 0), 0.0, EPSILON);
        assertEquals("Manhattan distance is not correct",
                AStarPath.manhattanDistance(0, 1, 1, 1), 1.0, EPSILON);
        assertEquals("Manhattan distance is not correct",
                AStarPath.manhattanDistance(5, 3, 2, 1), 5.0, EPSILON);
        assertEquals("Manhattan distance is not correct",
                AStarPath.manhattanDistance(1, 4, 2, 0), 5.0, EPSILON);
        assertEquals("Manhattan distance is not correct",
                AStarPath.manhattanDistance(-1, -8, -3, -1), 9.0, EPSILON);
        assertEquals("Manhattan distance is not correct",
                AStarPath.manhattanDistance(-0, -0, -0, -0), 0.0, EPSILON);
        assertEquals("Manhattan distance is not correct",
                AStarPath.manhattanDistance(-2, -2, -1, -1), 2.0, EPSILON);
    }

    /**
     * Test to the initialisation of constants in the game.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void constantTest() {
        final Game game = launcher.getGame();
        assertFalse("Game is in progress", game.isInProgress());
        assertNotNull("Game hasn't been instantiated", game);

        final AStarPath aStarPath = new AStarPath(game);

        assertNotNull("The aStar Path hasn't been instantiated", aStarPath);
        final IdentifiedPlayer player = game.getPlayers().get(0);

        assertNotNull("The player hasn't been instantiated", player);
        assertNotNull("The player's square hasn't been instantiated", player.getSquare());

        assertEquals("The constant is not correct", AStarPath.PELLET_COST, 1.0, EPSILON);
        assertEquals("The constant is not correct", AStarPath.EMPTY_COST, 5.0, EPSILON);
        assertEquals("The constant is not correct", AStarPath.GHOST_COST, 1000000.0, EPSILON);
        assertEquals("The constant is not correct", AStarPath.NEAREST_GHOST_COST, 500.0, EPSILON);
        assertEquals("The constant is not correct", AStarPath.DST_THRESHOLD, 3, EPSILON);

    }

    /**
     * Test the list of valid neighbors.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void validNeighboursTest() {
        final Game game = launcher.getGame();
        assertFalse("Game is in progress", game.isInProgress());
        assertNotNull("Game hasn't been instantiated", game);

        final AStarPath aStarPath = new AStarPath(game);

        assertNotNull("The aStar Path hasn't been instantiated", aStarPath);
        final IdentifiedPlayer player = game.getPlayers().get(0);
        final Square square = player.getSquare();

        assertNotNull("The player hasn't been instantiated", player);
        assertNotNull("The player's square hasn't been instantiated", square);

        final List<Square> neighbourList = AStarPath.getValidNeighbors(square);

        //The accessible square is to East and West. (North and south are walls)
        assertNotNull("The neighbors list hasn't been computed", neighbourList);
        assertTrue("The neighbors list should contain this neighbour",
                neighbourList.contains(player.getSquare().getSquareAt(Direction.EAST)));
        assertTrue("The neighbors list should contain this neighbour",
                neighbourList.contains(player.getSquare().getSquareAt(Direction.WEST)));

        assertFalse("The neighbors list shouldn't contain this neighbour",
                neighbourList.contains(player.getSquare().getSquareAt(Direction.NORTH)));
        assertFalse("The neighbors list shouldn't contain this neighbour",
                neighbourList.contains(player.getSquare().getSquareAt(Direction.SOUTH)));

        //Player move to have others neighbors
        game.start();
        assertTrue("Game is not in progress", game.isInProgress());
        game.move(player, Direction.EAST);
        final Square square2 = player.getSquare();

        assertFalse("The player doesn't move", square.isSameSquare(square2));

        final List<Square> neigborList2 = AStarPath.getValidNeighbors(square2);

        //The accessible square is to East and West and North (south is a wall)

        assertNotNull("The neighbors list hasn't been computed", neigborList2);
        assertTrue("The neighbors list should contain this neighbour",
                neigborList2.contains(square2.getSquareAt(Direction.EAST)));
        assertTrue("The neighbors list should contain this neighbour",
                neigborList2.contains(square2.getSquareAt(Direction.WEST)));
        assertTrue("The neighbors list should contain this neighbour",
                neigborList2.contains(square2.getSquareAt(Direction.NORTH)));

        assertFalse("The neighbors list shouldn't contain this neighbour",
                neigborList2.contains(square2.getSquareAt(Direction.SOUTH)));

    }

    /**
     * Test the list of valid neighbors.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void validNeighborsTest2() {
        final Game game = launcher.getGame();
        assertFalse("Game is in progress", game.isInProgress());
        assertNotNull("Game hasn't been instantiated", game);

        final AStarPath aStarPath = new AStarPath(game);

        assertNotNull("The aStar Path hasn't been instantiated", aStarPath);
        final IdentifiedPlayer player = game.getPlayers().get(0);
        final Square square = player.getSquare();

        assertNotNull("The player hasn't been instantiated", player);
        assertNotNull("The player's square hasn't been instantiated", square);

        final List<Square> neigborList = AStarPath.getValidNeighbors(square);

        //The accessible square is to East and West. (North and south are walls)
        assertNotNull("The neighbors list hasn't been computed", neigborList);
        assertTrue("The neighbors list should contain this neighbour",
                neigborList.contains(player.getSquare().getSquareAt(Direction.EAST)));
        assertTrue("The neighbors list should contain this neighbour",
                neigborList.contains(player.getSquare().getSquareAt(Direction.WEST)));

        assertFalse("The neighbors list shouldn't contain this neighbour",
                neigborList.contains(player.getSquare().getSquareAt(Direction.NORTH)));
        assertFalse("The neighbors list shouldn't contain this neighbour",
                neigborList.contains(player.getSquare().getSquareAt(Direction.SOUTH)));

        //Player move to have others neighbors
        game.start();
        assertTrue("The game should be in progress", game.isInProgress());
        game.move(player, Direction.WEST);
        final Square square2 = player.getSquare();

        assertFalse("The player doesn't move", square.isSameSquare(square2));


        final List<Square> neigborList2 = AStarPath.getValidNeighbors(square2);

        //The accessible square is to East and West and North (south is a wall)

        assertNotNull("The neighbors list hasn't been computed", neigborList2);
        assertTrue("The neighbors list should contain this neighbour",
                neigborList2.contains(square2.getSquareAt(Direction.EAST)));
        assertTrue("The neighbors list should contain this neighbour",
                neigborList2.contains(square2.getSquareAt(Direction.WEST)));
        assertTrue("The neighbors list should contain this neighbour",
                neigborList2.contains(square2.getSquareAt(Direction.NORTH)));

        assertFalse("The neighbors list shouldn't contain this neighbour",
                neigborList2.contains(square2.getSquareAt(Direction.SOUTH)));

        //Player move to have others neighbors
        game.move(player, Direction.WEST);
        game.move(player, Direction.WEST);
        game.move(player, Direction.WEST);
        game.move(player, Direction.WEST);
        game.move(player, Direction.WEST);
        game.move(player, Direction.WEST);

        final Square square3 = player.getSquare();

        assertFalse("The player doesn't move", square.isSameSquare(square3));
        assertFalse("The player doesn't move", square2.isSameSquare(square3));

        final List<Square> neigborList3 = AStarPath.getValidNeighbors(square3);

        assertNotNull("The neighbors list hasn't been computed", neigborList3);
        //All move is possible except to left (West)
        assertTrue("The neighbors list should contain this neighbour",
                neigborList3.contains(square3.getSquareAt(Direction.EAST)));
        assertFalse("The neighbors list shouldn't contain this neighbour",
                neigborList3.contains(square3.getSquareAt(Direction.WEST)));
        assertTrue("The neighbors list should contain this neighbour",
                neigborList3.contains(square3.getSquareAt(Direction.NORTH)));
        assertTrue("The neighbors list should contain this neighbour",
                neigborList3.contains(square3.getSquareAt(Direction.SOUTH)));
    }

    /**
     * test to know if the square is near of a ghost or not.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void nearestGhostTest() {
        final Game game = launcher.getGame();
        assertFalse("Game is in progress", game.isInProgress());
        assertNotNull("Game hasn't been instantiated", game);

        final AStarPath aStarPath = new AStarPath(game);

        assertNotNull("The aStar Path hasn't been instantiated", aStarPath);
        final IdentifiedPlayer player = game.getPlayers().get(0);
        final Square square = player.getSquare();

        assertNotNull("The player hasn't been instantiated", player);
        assertNotNull("The player's square hasn't been instantiated", square);

        assertFalse("The square should be safe", aStarPath.nearestGhosts(player.getSquare()));
        assertFalse("The square should be safe",
                aStarPath.nearestGhosts(player.getSquare().getSquareAt(Direction.NORTH)));
        assertFalse("The square should be safe", aStarPath.nearestGhosts(player.getSquare()
                .getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)));
        assertFalse("The square should be safe", aStarPath.nearestGhosts(player.getSquare()
                .getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)
                .getSquareAt(Direction.NORTH)));


        //The next north case is dangerous.
        assertTrue("The square shouldn't be safe", aStarPath.nearestGhosts(player.getSquare()
                .getSquareAt(Direction.NORTH)
                .getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)
                .getSquareAt(Direction.NORTH)));
        assertTrue("The square shouldn't be safe", aStarPath.nearestGhosts(player.getSquare()
                .getSquareAt(Direction.NORTH)
                .getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)
                .getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)));

        assertFalse("The square should be safe", aStarPath.nearestGhosts(player.getSquare()
                .getSquareAt(Direction.EAST)));
        assertFalse("The square should be safe", aStarPath.nearestGhosts(player.getSquare()
                .getSquareAt(Direction.EAST)
                .getSquareAt(Direction.EAST)));

        assertFalse("The square should be safe", aStarPath.nearestGhosts(player.getSquare()
                .getSquareAt(Direction.WEST)));
        assertFalse("The square should be safe", aStarPath.nearestGhosts(player
                .getSquare().getSquareAt(Direction.WEST)
                .getSquareAt(Direction.WEST)));
    }

    /**
     * Test the AStar method.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void ghTest() {
        final Game game = launcher.getGame();
        assertFalse("Game is in progress", game.isInProgress());
        assertNotNull("Game hasn't been instantiated", game);

        final AStarPath aStarPath = new AStarPath(game);

        assertNotNull("The aStar Path hasn't been instantiated", aStarPath);
        final IdentifiedPlayer player = game.getPlayers().get(0);
        final Square square = player.getSquare();

        assertNotNull("The player hasn't been instantiated", player);
        assertNotNull("The player's square hasn't been instantiated", square);

        final Square origin = player.getSquare();
        final Square destination = player.getSquare().getSquareAt(Direction.EAST);
        final Square destination2 = player.getSquare().getSquareAt(Direction.EAST)
                .getSquareAt(Direction.EAST);

        final Square destination3 = player.getSquare().getSquareAt(Direction.WEST);
        final Square destination4 = player.getSquare().getSquareAt(Direction.WEST)
                .getSquareAt(Direction.WEST);

        //There is only Pellet
        assertEquals("The g method is not correct",
                aStarPath.g(origin, destination), Double.valueOf(1.0));
        assertEquals("The g method is not correct",
                aStarPath.g(origin, destination2), Double.valueOf(1.0));
        assertEquals("The g method is not correct",
                aStarPath.g(origin, destination3), Double.valueOf(1.0));
        assertEquals("The g method is not correct",
                aStarPath.g(origin, destination4), Double.valueOf(1.0));
        assertEquals("The g method is not correct",
                aStarPath.g(destination, destination4), Double.valueOf(1.0));
        assertEquals("The g method is not correct",
                aStarPath.g(destination3, destination4), Double.valueOf(1.0));

        final Square finalDestination = player.getSquare().getSquareAt(Direction.NORTH)
                .getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)
                .getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH);
        assertEquals("The h method is not correct",
                aStarPath.g(origin, finalDestination), Double.valueOf(500.0));

        assertEquals("The h method is not correct",
                aStarPath.h(origin, destination), Double.valueOf(1.0));
        assertEquals("The h method is not correct",
                aStarPath.h(origin, destination2), Double.valueOf(2.0));

        assertEquals("The h method is not correct",
                aStarPath.h(origin, destination3), Double.valueOf(1.0));
        assertEquals("The h method is not correct",
                aStarPath.h(origin, destination4), Double.valueOf(2.0));

        assertEquals("The h method is not correct",
                aStarPath.h(destination, destination4), Double.valueOf(3.0));
        assertEquals("The h method is not correct",
                aStarPath.h(destination3, destination4), Double.valueOf(1.0));

    }

    /**
     * Test the AStar method.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void hTest() {
        final Game game = launcher.getGame();
        assertFalse("Game is in progress", game.isInProgress());
        assertNotNull("Game hasn't been instantiated", game);

        final AStarPath aStarPath = new AStarPath(game);

        assertNotNull("The aStar Path hasn't been instantiated", aStarPath);
        final IdentifiedPlayer player = game.getPlayers().get(0);
        final Square square = player.getSquare();

        assertNotNull("The player hasn't been instantiated", player);
        assertNotNull("The player's square hasn't been instantiated", square);

        final Square origin = player.getSquare();
        final Square destination = player.getSquare().
                getSquareAt(Direction.SOUTH);

        assertEquals("The h method is not correct",
                aStarPath.h(origin, destination), Double.valueOf(1.0));
    }
}
