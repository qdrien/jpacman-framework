package nl.tudelft.jpacman.level;

import com.google.common.collect.Lists;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.npc.NPC;
import nl.tudelft.jpacman.npc.ghost.Ghost;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests various aspects of level.
 *
 * @author Jeroen Roosen
 */
public class LevelTest {

	/**
	 * The level under test.
	 */
	private Level level;

	/**
	 * An NPC on this level.
	 */
	private final NPC ghost = mock(NPC.class);

	/**
	 * Starting position 1.
	 */
	private final Square square1 = mock(Square.class);

	/**
	 * Starting position 2.
	 */
	private final Square square2 = mock(Square.class);

	/**
	 * The board for this level.
	 */
	private final Board board = mock(Board.class);

	/**
	 * The collision map.
	 */
	private final CollisionMap collisions = mock(CollisionMap.class);

	/**
	 * Sets up the level with the default board, a single NPC and a starting
	 * square.
	 */
	@Before
	public void setUp() {
		final long defaultInterval = 100L;
		level = new Level(board, Lists.newArrayList(ghost), Lists.newArrayList(
				square1, square2), collisions);
		when(ghost.getInterval()).thenReturn(defaultInterval);
	}

	/**
	 * Validates the state of the level when it isn't started yet.
	 */
	@Test
	public void noStart() {
		assertFalse(level.isInProgress());
	}

	/**
	 * Validates the state of the level when it is stopped without starting.
	 */
	@Test
	public void stop() {
		level.stop();
		assertFalse(level.isInProgress());
	}

	/**
	 * Validates the state of the level when it is started.
	 */
	@Test
	public void start() {
		level.start();
		assertTrue(level.isInProgress());
	}

	/**
	 * Validates the state of the level when it is started then stopped.
	 */
	@Test
	public void startStop() {
		level.start();
		level.stop();
		assertFalse(level.isInProgress());
	}

	/**
	 * Verifies registering a player puts the player on the correct starting
	 * square.
	 */
	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	public void registerPlayer() {
		Player p = mock(Player.class);
		level.registerPlayer(p);
		verify(p).occupy(square1);
	}

	/**
	 * Verifies registering a player twice does not do anything.
	 */
	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	public void registerPlayerTwice() {
		Player p = mock(Player.class);
		level.registerPlayer(p);
		level.registerPlayer(p);
		verify(p, times(1)).occupy(square1);
	}

	/**
	 * Verifies registering a second player puts that player on the correct
	 * starting square.
	 */
	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	public void registerSecondPlayer() {
		Player p1 = mock(Player.class);
		Player p2 = mock(Player.class);
		level.registerPlayer(p1);
		level.registerPlayer(p2);
		verify(p2).occupy(square2);
	}

	/**
	 * Verifies registering a third player puts the player on the correct
	 * starting square.
	 */
	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	public void registerThirdPlayer() {
		Player p1 = mock(Player.class);
		Player p2 = mock(Player.class);
		Player p3 = mock(Player.class);
		level.registerPlayer(p1);
		level.registerPlayer(p2);
		level.registerPlayer(p3);
		verify(p3).occupy(square1);
	}

    /**
     * Tests manhattan distance computation
     */
    @Test
    public void manhattanDistanceTest() {
		assertEquals(1, Level.manhattanDistance(0, 0, 0, 1));
		assertEquals(2, Level.manhattanDistance(0, 0, 1, 1));
    }

    /**
     * Tests if a square is effectively considered safe when no ghosts are occupying "neighbouring squares"
     */
    @Test
    public void isSafeTrue() {
        Square[][] grid = new Square[2][2];
        Square x0y0 = mock(Square.class);
        Square x0y1 = mock(Square.class);
        Square x1y0 = mock(Square.class);
        Square x1y1 = mock(Square.class);

        ArrayList<Unit> units = new ArrayList<>();
        when(x0y0.getOccupants()).thenReturn(units);

        when(board.squareAt(0, 0)).thenReturn(x0y0);
        when(board.squareAt(0, 1)).thenReturn(x0y1);
        when(board.squareAt(1, 0)).thenReturn(x1y0);
        when(board.squareAt(1, 1)).thenReturn(x1y1);

        when(board.getWidth()).thenReturn(2);
        when(board.getHeight()).thenReturn(2);

        grid[0][0] = x0y0;
        grid[0][1] = x0y1;
        grid[1][0] = x1y0;
        grid[1][1] = x1y1;
        Board board = new Board(grid);

        assertTrue(level.isSafe(0, 0));
    }

    /**
     * Tests if a square is effectively considered unsafe when a ghosts is occupying a "neighbouring square"
     */
    @Test
    public void isSafeFalse() {
        Square[][] grid = new Square[2][2];
        Square x0y0 = mock(Square.class);
        Square x0y1 = mock(Square.class);
        Square x1y0 = mock(Square.class);
        Square x1y1 = mock(Square.class);

        Ghost ghost = mock(Ghost.class);
        ArrayList<Unit> units = new ArrayList<>();
        units.add(ghost);

        when(x0y0.getOccupants()).thenReturn(units);

        when(board.squareAt(0, 0)).thenReturn(x0y0);
        when(board.squareAt(0, 1)).thenReturn(x0y1);
        when(board.squareAt(1, 0)).thenReturn(x1y0);
        when(board.squareAt(1, 1)).thenReturn(x1y1);

        when(board.getWidth()).thenReturn(2);
        when(board.getHeight()).thenReturn(2);

        grid[0][0] = x0y0;
        grid[0][1] = x0y1;
        grid[1][0] = x1y0;
        grid[1][1] = x1y1;
        Board board = new Board(grid);

        assertFalse(level.isSafe(0, 1));
    }
}
