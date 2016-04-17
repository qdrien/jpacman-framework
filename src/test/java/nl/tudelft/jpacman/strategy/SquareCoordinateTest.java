package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Class to test coordinates of a square in the board.
 */
@SuppressWarnings("checkstyle:magicnumber")
public class SquareCoordinateTest {
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
     * Launch the game, and imitate what would happen in a typical game.
     * The test is only a smoke test, and not a focused small test.
     * Therefore it is OK that the method is a bit too long.
     */
    @SuppressWarnings("methodlength")
    @Test
    public void squareCoordinateTest1() {
        final Square square = launcher.getGame().getPlayers().get(0).getSquare();

        assertEquals("Incorrect square", square.getX(), 11);
        assertEquals("Incorrect square", square.getY(), 15);

        assertEquals("Incorrect square", square.getSquareAt(Direction.NORTH).getX(), 11);
        assertEquals("Incorrect square", square.getSquareAt(Direction.NORTH).getY(), 14);

        assertEquals("Incorrect square", square.getSquareAt(Direction.WEST).getX(), 10);
        assertEquals("Incorrect square", square.getSquareAt(Direction.WEST).getY(), 15);

        assertEquals("Incorrect square", square.getSquareAt(Direction.SOUTH).getX(), 11);
        assertEquals("Incorrect square", square.getSquareAt(Direction.SOUTH).getY(), 16);

        assertEquals("Incorrect square", square.getSquareAt(Direction.EAST).getX(), 12);
        assertEquals("Incorrect square", square.getSquareAt(Direction.EAST).getY(), 15);

        assertEquals("Incorrect square", square.getX(), 11);
        assertEquals("Incorrect square", square.getY(), 15);

        square.setX(20);
        square.setY(25);

        assertEquals("Incorrect square dimension", square.getX(), 20);
        assertEquals("Incorrect square dimension", square.getY(), 25);
    }
}
