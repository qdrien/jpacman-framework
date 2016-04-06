package nl.tudelft.jpacman.board;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.sprite.PacManSprites;
import nl.tudelft.jpacman.sprite.Sprite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A factory that creates {@link Board} objects from 2-dimensional arrays of
 * {@link Square}s.
 *
 * @author Jeroen Roosen
 */
public class BoardFactory {

    /**
     * The sprite store providing the sprites for the background.
     */
    private final PacManSprites sprites;

    /**
     * Creates a new BoardFactory that will create a board with the provided
     * background sprites.
     *
     * @param spriteStore The sprite store providing the sprites for the background.
     */
    public BoardFactory(PacManSprites spriteStore) {
        this.sprites = spriteStore;
        generateLevels();
    }

    /**
     * Creates a new board from a grid of cells and connects it.
     *
     * @param grid The square grid of cells, in which grid[x][y] corresponds to
     *             the square at position x,y.
     * @return A new board, wrapping a grid of connected cells.
     */
    public Board createBoard(Square[][] grid) {
        assert grid != null;

        final Board board = new Board(grid);

        final int width = board.getWidth(), height = board.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Square square = grid[x][y];
                square.setX(x);
                square.setY(y);
                for (Direction dir : Direction.values()) {
                    int dirX = (width + x + dir.getDeltaX()) % width;
                    int dirY = (height + y + dir.getDeltaY()) % height;
                    Square neighbour = grid[dirX][dirY];
                    square.link(neighbour, dir);
                }
            }
        }

        return board;
    }

    /**
     * Creates a new square that can be occupied by any unit.
     *
     * @return A new square that can be occupied by any unit.
     */
    public Square createGround() {
        return new Ground(sprites.getGroundSprite());
    }

    /**
     * Creates a new square that cannot be occupied by any unit.
     *
     * @return A new square that cannot be occupied by any unit.
     */
    public Square createWall() {
        return new Wall(sprites.getWallSprite());
    }

    /**
     * Checks level files and generate text files from images if needed
     * (will stop when no file is found for the next level i.e. no text/image file)
     */
    private void generateLevels() {
        final String path =
                Launcher.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        final File dir = new File(path);
        final File[] files = dir.listFiles();

        int levelCount = 0;
        if (files != null) {
            File currentFile = levelFileFor(1, files);
            while (currentFile != null) {
                levelCount++;
                if (currentFile.getName().endsWith(".png")) {
                    try {
                        createBoardFileFromImage(currentFile, levelCount);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                currentFile = levelFileFor(levelCount + 1, files);
            }
        }
    }

    /**
     * Creates a text file for a level from a given image file.
     *
     * @param file  The image file to generate the level from
     * @param level The level id of this level
     * @throws IOException
     */
    private void createBoardFileFromImage(final File file, final int level) throws IOException {
        final BufferedImage img = ImageIO.read(file);
        if (img == null) {
            System.err.println("Error loading image " + file.getName());
            return;
        }

        final List<String> lines = convertImageToTxt(img);
        final Path newFile = Paths.get(file.getParent() + File.separator
                + "board" + level + ".txt");
        System.err.println("Creating file for level " + level + " from an image.");
        Files.write(newFile, lines, Charset.forName("UTF-8"));
    }

    /**
     * Converts a given image to a list of "pacman level" lines (Strings).
     *
     * @param img The image used for generating the level
     * @return a List of Strings containing lines in pacman's format
     */
    private List<String> convertImageToTxt(final BufferedImage img) {
        final List<String> lines = new ArrayList<>();
        //For each line of pixels
        for (int y = 0; y < img.getHeight(); y++) {
            StringBuilder line = new StringBuilder();
            //For each pixel in this line
            for (int x = 0; x < img.getWidth(); x++) {
                //Get the corresponding letter
                Character c = ItemsColor.getLetterByRGBValue(img.getRGB(x, y));
                if (c != null) {
                    line.append(c);
                }
            }
            lines.add(line.toString());
        }
        return lines;
    }

    /**
     * Returns the file matching the given level in the given list of files.
     *
     * @param level The level we are searching for
     * @param files The list of files to search in
     * @return If available, a text file (preferably) or an image file; null otherwise
     */
    private File levelFileFor(final int level, final File... files) {
        File output = null;
        for (final File file : files) {
            if (file.getName().equals("board" + level + ".txt")) {
                return file;
            } else if (file.getName().equals(level + ".png")) {
                output = file;
            }
        }
        return output;
    }

    /**
     * A wall is a square that is inaccessible to anyone.
     *
     * @author Jeroen Roosen
     */
    private static final class Wall extends Square {

        /**
         * The background for this square.
         */
        private final Sprite background;

        /**
         * Creates a new wall square.
         *
         * @param sprite The background for the square.
         */
        private Wall(Sprite sprite) {
            this.background = sprite;
        }

        @Override
        public boolean isAccessibleTo(Unit unit) {
            return false;
        }

        @Override
        public Sprite getSprite() {
            return background;
        }
    }

    /**
     * A wall is a square that is accessible to anyone.
     *
     * @author Jeroen Roosen
     */
    private static final class Ground extends Square {

        /**
         * The background for this square.
         */
        private final Sprite background;

        /**
         * Creates a new ground square.
         *
         * @param sprite The background for the square.
         */
        private Ground(Sprite sprite) {
            this.background = sprite;
        }

        @Override
        public boolean isAccessibleTo(Unit unit) {
            return true;
        }

        @Override
        public Sprite getSprite() {
            return background;
        }
    }
}
