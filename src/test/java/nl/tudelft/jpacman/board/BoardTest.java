package nl.tudelft.jpacman.board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import nl.tudelft.jpacman.npc.ghost.Ghost;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Test various aspects of board.
 * 
 * @author Jeroen Roosen 
 */
public class BoardTest {

	private Board board;
	
	private Square x0y0 = mock(Square.class);
	private Square x0y1 = mock(Square.class);
	private Square x0y2 = mock(Square.class);
	private Square x1y0 = mock(Square.class);
	private Square x1y1 = mock(Square.class);
	private Square x1y2 = mock(Square.class);
	
	private final int maxWidth = 2;
	private final int maxHeight = 3;
	
	/**
	 * Setup a board that can be used for testing.
	 */
	@Before
	public void setUp() {
		Square[][] grid = new Square[maxWidth][maxHeight];
		grid[0][0] = x0y0;
		grid[0][1] = x0y1;
		grid[0][2] = x0y2;
		grid[1][0] = x1y0;
		grid[1][1] = x1y1;
		grid[1][2] = x1y2;
		board = new Board(grid);
	}
	
	/**
	 * Verifies the board has the correct width.
	 */
	@Test
	public void verifyWidth() {
		assertEquals(maxWidth, board.getWidth());
	}
	
	/**
	 * Verifies the board has the correct height.
	 */
	@Test
	public void verifyHeight() {
		assertEquals(maxHeight, board.getHeight());
	}
	
	/**
	 * Verifies the square at x0y0 is indeed the right square.
	 */
	@Test
	public void verifyX0Y0() {
		assertEquals(x0y0, board.squareAt(0, 0));
	}
	
	/**
	 * Verifies the square at x1y2 is indeed the right square.
	 */
	@Test
	public void verifyX1Y2() {
		assertEquals(x1y2, board.squareAt(1, 2));
	}
	
	/**
	 * Verifies the square at x0y1 is indeed the right square.
	 */
	@Test
	public void verifyX0Y1() {
		assertEquals(x0y1, board.squareAt(0, 1));
	}

	/**
	 * Tests manhattan distance computation
	 */
	@Test
	public void manhattanDistanceTest() {
		assertEquals(1, Board.manhattanDistance(0, 0, 0, 1));
		assertEquals(2, Board.manhattanDistance(0, 0, 1, 1));
	}

	/**
	 * Tests if a square is effectively considered safe when no ghosts are occupying "neighbouring squares"
	 */
	@Test
	public void isSafeTrue() {
		ArrayList<Unit> units = new ArrayList<>();
		when(x0y0.getOccupants()).thenReturn(units);

		assertTrue(board.isSafe(0, 0));
	}

	/**
	 * Tests if a square is effectively considered unsafe when a ghosts is occupying a "neighbouring square"
	 */
	@Test
	public void isSafeFalse() {
		Ghost ghost = mock(Ghost.class);
		ArrayList<Unit> units = new ArrayList<>();
		units.add(ghost);

		when(x0y0.getOccupants()).thenReturn(units);

		assertFalse(board.isSafe(0, 1));
	}
}
