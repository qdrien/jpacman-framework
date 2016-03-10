package nl.tudelft.jpacman.Strategie;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.strategy.AStarPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Nicolas Leemans on 7/03/16.
 */
public class AStarPathTest
{
    private Launcher launcher;


    @Before
    public void setUpPacman()
    {
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


    @SuppressWarnings("methodlength")
    @Test
    public void ConstructorTest()
    {
        Game game = launcher.getGame();
        assertFalse(game.isInProgress());
        assertNotNull(game);

        AStarPath aStarPath = new AStarPath(game);

        assertNotNull(aStarPath);
        Square square = game.getPlayers().get(0).getSquare();
        aStarPath.setGoal(square);
        assertNotNull(aStarPath.isGoal(square));
        assertTrue(aStarPath.isGoal(square));

        Square square1 = square.getSquareAt(Direction.EAST);

        assertNotNull(aStarPath.isGoal(square1));
        assertFalse(aStarPath.isGoal(square1));

        assertNotNull(aStarPath.getCost());

    }
    @SuppressWarnings("methodlength")
    @Test
    public void ManhattanDistanceTest()
    {

        assertEquals(AStarPath.ManhattanDistance(0,0,0,0), 0.0);
        assertEquals(AStarPath.ManhattanDistance(0,1,1,1), 1.0);
        assertEquals(AStarPath.ManhattanDistance(5,3,2,1), 5.0);
        assertEquals(AStarPath.ManhattanDistance(1,4,2,0), 5.0);
        assertEquals(AStarPath.ManhattanDistance(-1,-8,-3,-1), 9.0);
        assertEquals(AStarPath.ManhattanDistance(-0,-0,-0,-0), 0.0);
        assertEquals(AStarPath.ManhattanDistance(-2,-2,-1,-1), 2.0);
    }

    @SuppressWarnings("methodlength")
    @Test
    public void ConstanteTest()
    {
        Game game = launcher.getGame();
        assertFalse(game.isInProgress());
        assertNotNull(game);

        AStarPath aStarPath = new AStarPath(game);

        assertNotNull(aStarPath);
        Player player = game.getPlayers().get(0);
        Square square = player.getSquare();

        assertNotNull(player);
        assertNotNull(square);

        assertEquals(aStarPath.PELLET_COST, 1.0);
        assertEquals(aStarPath.EMPTY_COST, 5.0);
        assertEquals(aStarPath.GHOST_COST, 1000000.0);
        assertEquals(aStarPath.NEAREST_GHOST_COST, 500.0);
        assertEquals(aStarPath.DST_THRESHOLD, 3);

    }

    @SuppressWarnings("methodlength")
    @Test
    public void ValidNeighborsTest()
    {
        Game game = launcher.getGame();
        assertFalse(game.isInProgress());
        assertNotNull(game);

        AStarPath aStarPath = new AStarPath(game);

        assertNotNull(aStarPath);
        Player player = game.getPlayers().get(0);
        Square square = player.getSquare();

        assertNotNull(player);
        assertNotNull(square);

        List<Square> neigborList = aStarPath.getValidNeighborsTest(square,player);

        //The accessible square is to East and West. (North and south are walls)
        assertNotNull(neigborList);
        assertTrue(neigborList.contains(player.getSquare().getSquareAt(Direction.EAST)));
        assertTrue(neigborList.contains(player.getSquare().getSquareAt(Direction.WEST)));

        assertFalse(neigborList.contains(player.getSquare().getSquareAt(Direction.NORTH)));
        assertFalse(neigborList.contains(player.getSquare().getSquareAt(Direction.SOUTH)));

        //Player move to have others neighbors
        game.start();
        assertTrue(game.isInProgress());
        game.move(player, Direction.EAST);
        Square square2 = player.getSquare();

        assertFalse(square == square2);

        List<Square> neigborList2 = aStarPath.getValidNeighborsTest(square2,player);

        //The accessible square is to East and West and North (south is a wall)

        assertNotNull(neigborList2);
        assertTrue(neigborList2.contains(square2.getSquareAt(Direction.EAST)));
        assertTrue(neigborList2.contains(square2.getSquareAt(Direction.WEST)));
        assertTrue(neigborList2.contains(square2.getSquareAt(Direction.NORTH)));

        assertFalse(neigborList2.contains(square2.getSquareAt(Direction.SOUTH)));

    }

    @SuppressWarnings("methodlength")
    @Test
    public void ValidNeighborsTest2()
    {
        Game game = launcher.getGame();
        assertFalse(game.isInProgress());
        assertNotNull(game);

        AStarPath aStarPath = new AStarPath(game);

        assertNotNull(aStarPath);
        Player player = game.getPlayers().get(0);
        Square square = player.getSquare();

        assertNotNull(player);
        assertNotNull(square);

        List<Square> neigborList = aStarPath.getValidNeighborsTest(square,player);

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
        Square square2 = player.getSquare();

        assertFalse(square == square2);

        List<Square> neigborList2 = aStarPath.getValidNeighborsTest(square2,player);

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

        Square square3 = player.getSquare();

        assertFalse(square == square3);
        assertFalse(square2 == square3);


        List<Square> neigborList3 = aStarPath.getValidNeighborsTest(square3,player);

        assertNotNull(neigborList3);
        //All move is possible except to left (West)
        assertTrue(neigborList3.contains(square3.getSquareAt(Direction.EAST)));
        assertFalse(neigborList3.contains(square3.getSquareAt(Direction.WEST)));
        assertTrue(neigborList3.contains(square3.getSquareAt(Direction.NORTH)));
        assertTrue(neigborList3.contains(square3.getSquareAt(Direction.SOUTH)));


    }
    @SuppressWarnings("methodlength")
    @Test
    public void nearestGhostTest()
    {
        Game game = launcher.getGame();
        assertFalse(game.isInProgress());
        assertNotNull(game);

        AStarPath aStarPath = new AStarPath(game);

        assertNotNull(aStarPath);
        Player player = game.getPlayers().get(0);
        Square square = player.getSquare();

        assertNotNull(player);
        assertNotNull(square);

        assertFalse(aStarPath.nearestGhostsTest(player.getSquare()));
        assertFalse(aStarPath.nearestGhostsTest(player.getSquare().getSquareAt(Direction.NORTH)));
        assertFalse(aStarPath.nearestGhostsTest(player.getSquare().getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)));
        assertFalse(aStarPath.nearestGhostsTest(player.getSquare().getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)));


        //The next north case is dangerous.
        assertTrue(aStarPath.nearestGhostsTest(player.getSquare().getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)));
        assertTrue(aStarPath.nearestGhostsTest(player.getSquare().getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH)));

        assertFalse(aStarPath.nearestGhostsTest(player.getSquare().getSquareAt(Direction.EAST)));
        assertFalse(aStarPath.nearestGhostsTest(player.getSquare().getSquareAt(Direction.EAST).getSquareAt(Direction.EAST)));

        assertFalse(aStarPath.nearestGhostsTest(player.getSquare().getSquareAt(Direction.WEST)));
        assertFalse(aStarPath.nearestGhostsTest(player.getSquare().getSquareAt(Direction.WEST).getSquareAt(Direction.WEST)));

    }
    @SuppressWarnings("methodlength")
    @Test
    public void gTest()
    {
        Game game = launcher.getGame();
        assertFalse(game.isInProgress());
        assertNotNull(game);

        AStarPath aStarPath = new AStarPath(game);

        assertNotNull(aStarPath);
        Player player = game.getPlayers().get(0);
        Square square = player.getSquare();

        assertNotNull(player);
        assertNotNull(square);

        Square origin = player.getSquare();
        Square destination = player.getSquare().getSquareAt(Direction.EAST);
        Square destination2 = player.getSquare().getSquareAt(Direction.EAST).getSquareAt(Direction.EAST);

        Square destination3 = player.getSquare().getSquareAt(Direction.WEST);
        Square destination4 = player.getSquare().getSquareAt(Direction.WEST).getSquareAt(Direction.WEST);

        //There is only Pellet
        assertEquals(aStarPath.gTest(origin,destination), 1.0);
        assertEquals(aStarPath.gTest(origin,destination2), 1.0);
        assertEquals(aStarPath.gTest(origin,destination3), 1.0);
        assertEquals(aStarPath.gTest(origin,destination4), 1.0);
        assertEquals(aStarPath.gTest(destination,destination4), 1.0);
        assertEquals(aStarPath.gTest(destination3,destination4), 1.0);

        Square destinationNearestGhost = player.getSquare().getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH).getSquareAt(Direction.NORTH);
        assertEquals(aStarPath.gTest(origin,destinationNearestGhost), 500.0);

    }

    @SuppressWarnings("methodlength")
    @Test
    public void hTest()
    {
        Game game = launcher.getGame();
        assertFalse(game.isInProgress());
        assertNotNull(game);

        AStarPath aStarPath = new AStarPath(game);

        assertNotNull(aStarPath);
        Player player = game.getPlayers().get(0);
        Square square = player.getSquare();

        assertNotNull(player);
        assertNotNull(square);

        Square origin = player.getSquare();
        Square destination = player.getSquare().getSquareAt(Direction.EAST);
        Square destination2 = player.getSquare().getSquareAt(Direction.EAST).getSquareAt(Direction.EAST);

        Square destination3 = player.getSquare().getSquareAt(Direction.WEST);
        Square destination4 = player.getSquare().getSquareAt(Direction.WEST).getSquareAt(Direction.WEST);

        assertTrue(aStarPath.hTest(origin,destination) == 1);
        assertTrue(aStarPath.hTest(origin,destination2) == 2);

        assertTrue(aStarPath.hTest(origin,destination3) == 1);
        assertTrue(aStarPath.hTest(origin,destination4) == 2);

        assertTrue(aStarPath.hTest(destination,destination4) == 3);
        assertTrue(aStarPath.hTest(destination3,destination4) == 1);

    }

}