package nl.tudelft.jpacman.npc.ghost;

import com.google.common.collect.Lists;
import nl.tudelft.jpacman.board.*;
import nl.tudelft.jpacman.level.LevelFactory;
import nl.tudelft.jpacman.level.MapParser;
import nl.tudelft.jpacman.level.Pellet;
import nl.tudelft.jpacman.sprite.PacManSprites;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Tests the various methods provided by the {@link Navigation} class.
 *
 * @author Jeroen Roosen
 */
@SuppressWarnings({"magicnumber", "PMD.AvoidDuplicateLiterals"})
public class NavigationTest {

    /**
     * Map parser used to construct boards.
     */
    private MapParser parser;

    /**
     * Set up the map parser.
     */
    @Before
    public void setUp() {
        final PacManSprites sprites = new PacManSprites();
        parser = new MapParser(new LevelFactory(sprites, new GhostFactory(
                sprites)), new BoardFactory(sprites));
    }

    /**
     * Verifies that the path to the same square is empty.
     */
    @Test
    public void testShortestPathEmpty() {
        final Board b = parser.parseMap(Lists.newArrayList(" ")).getBoard();
        final Square s1 = b.squareAt(0, 0), s2 = b.squareAt(0, 0);
        List<Direction> path = Navigation
                .shortestPath(s1, s2, mock(Unit.class));
        assertEquals(0, path.size());
    }

    /**
     * Verifies that if no path exists, the result is <code>null</code>.
     */
    @Test
    public void testNoShortestPath() {
        final Board b = parser
                .parseMap(Lists.newArrayList("#####", "# # #", "#####"))
                .getBoard();
        final Square s1 = b.squareAt(1, 1), s2 = b.squareAt(3, 1);
        List<Direction> path = Navigation
                .shortestPath(s1, s2, mock(Unit.class));
        assertNull(path);
    }

    /**
     * Verifies that having no traveller ignores terrain.
     */
    @Test
    public void testNoTraveller() {
        final Board b = parser
                .parseMap(Lists.newArrayList("#####", "# # #", "#####"))
                .getBoard();
        final Square s1 = b.squareAt(1, 1), s2 = b.squareAt(3, 1);
        List<Direction> path = Navigation.shortestPath(s1, s2, null);
        assertArrayEquals(new Direction[]{Direction.EAST, Direction.EAST},
                path.toArray(new Direction[]{}));
    }

    /**
     * Tests if the algorithm can find a path in a straight line.
     */
    @Test
    public void testSimplePath() {
        final Board b = parser.parseMap(Lists.newArrayList("####", "#  #", "####"))
                .getBoard();
        final Square s1 = b.squareAt(1, 1), s2 = b.squareAt(2, 1);
        List<Direction> path = Navigation
                .shortestPath(s1, s2, mock(Unit.class));
        assertArrayEquals(new Direction[]{Direction.EAST},
                path.toArray(new Direction[]{}));
    }

    /**
     * Verifies that the algorithm can find a path when it has to take corners.
     */
    @Test
    public void testCornerPath() {
        final Board b = parser.parseMap(
                Lists.newArrayList("####", "#  #", "## #", "####")).getBoard();
        final Square s1 = b.squareAt(1, 1), s2 = b.squareAt(2, 2);
        List<Direction> path = Navigation
                .shortestPath(s1, s2, mock(Unit.class));
        assertArrayEquals(new Direction[]{Direction.EAST, Direction.SOUTH},
                path.toArray(new Direction[]{}));
    }

    /**
     * Verifies that the nearest object is detected.
     */
    @Test
    public void testNearestUnit() {
        final Board b = parser
                .parseMap(Lists.newArrayList("#####", "# ..#", "#####"))
                .getBoard();
        final Square s1 = b.squareAt(1, 1), s2 = b.squareAt(2, 1), result = Navigation.findNearest(Pellet.class, s1).getSquare();
        assertEquals(s2, result);
    }

    /**
     * Verifies that there is no such location if there is no nearest object.
     */
    @Test
    public void testNoNearestUnit() {
        assertNull(Navigation.findNearest(Pellet.class, parser.parseMap(Lists.newArrayList(" ")).getBoard().squareAt(0, 0)));
    }

    /**
     * Verifies that there is ghost on the default board
     * next to cell [1, 1].
     *
     * @throws IOException if board reading fails.
     */
    @Test
    public void testFullSizedLevel() throws IOException {
        assertNotNull(Navigation.findNearest(Ghost.class, parser.parseMap(getClass().getResourceAsStream("/board1.txt")).getBoard().squareAt(1, 1)));
    }
}
