package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Pellet;
import nl.tudelft.jpacman.npc.ghost.Ghost;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements a strategy
 * to clean the board and avoid the ghosts with AI.
 */
public class PacManhattanAI extends AIStrategy {

    /**
     * The remaining pellet threshold to change
     * the behavior of the AI.
     */
    private static final int HIGH_PELLET_COUNT = 30;
    /**
     * The remaining pellet threshold to change
     * the behavior of the AI.
     */
    private static final int LOW_PELLET_COUNT = 7;
    /**
     * The safe distance between the Pacman and ghosts.
     */
    private static final int INITIAL_GHOST_DST = 14;
    /**
     * The medium safe distance between the Pacman and ghosts.
     */
    private static final int MEDIUM_GHOST_DST = 6;
    /**
     * The low safe distance between the Pacman and ghosts.
     */
    private static final int LOW_GHOST_DST = 3;
    /**
     * The threshold distance between the player and a ghost.
     */
    private int ghostDstThreshold = INITIAL_GHOST_DST;
    /**
     * The pacman game.
     */
    private final Game game;
    /**
     * Queue containing the potential directions to follow.
     */
    private Deque<Direction> directionQueue;
    /**
     * The path calculates with AStar.
     */
    private AStarPath pathAStar;
    /**
     * List to know if the square is yet visited or not.
     */
    private boolean[][] visitedSquare;

    /**
     * The default constructor.
     *
     * @param game the current game
     */
    public PacManhattanAI(final Game game) {
        super(game);
        this.game = game;
        init(game);
    }

    /**
     * Initialise the data used to calculate the best movement to apply.
     *
     * @param game The current game
     */
    private void init(final Game game) {
        visitedSquare = new boolean[getBoard().getHeight()][getBoard().getWidth()];
        pathAStar = new AStarPath(game);
    }

    /**
     * Calculates the best move to apply according the data of the game
     * (ghosts position, pellet nearest,...).
     *
     * @return the best move
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Override
    public Direction nextMove() {
        directionQueue = new ArrayDeque<>(); //Initialisation of the queue containing best moves
        boolean warning = false; //Boolean to know if a ghost is near of the player or not
        updatePacmanBehaviour(game.getLevel().remainingPellets()); //Accelerate the endgame

        for (final Ghost ghost : getGhostsList()) {
            //Test if a ghost is near of the player
            final double distance = AStarPath.manhattanDistance(
                    getPlayer().getSquare().getX(), getPlayer().getSquare().getY(),
                    ghost.getSquare().getX(), ghost.getSquare().getY());
            if (distance < ghostDstThreshold) {
                computePath(bfsNearestSafetySquare());
                warning = true;
                break;
            }
        }
        if (!warning) {
            //There is no near ghost, thus find the nearest pellet
            computePath(bfsNearestSafetyPelletSquare());
        }
        if (directionQueue.isEmpty()) {
            if (warning) {
                //No safe square found, find the nearest pellet
                computePath(bfsNearestSafetyPelletSquare());
                if (directionQueue.isEmpty()) {
                    //No path found, find a other direction
                    return hurryMove();
                } else {
                    return directionQueue.removeFirst();
                }
            } else {
                //No path found to a nearest pellet, find a safe square
                computePath(bfsNearestSafetySquare());
                if (directionQueue.isEmpty()) {
                    //No path found, find a other direction
                    return hurryMove();
                } else {
                    return directionQueue.removeFirst();
                }
            }
        } else {
            //Apply the best move
            return directionQueue.removeFirst();
        }
    }

    /**
     * Compute a path.
     *
     * @param square the goal square
     */
    private void computePath(final Square square) {
        pathAStar = new AStarPath(game);
        pathAStar.setGoal(square);

        List<Square> path = pathAStar.compute(getPlayer().getSquare());

        //Check if the path is safe
        if (pathAStar.getCost() > AStarPath.GHOST_COST) {
            path = null;
        }
        directionQueue = convertPathToDirection(path);
    }

    /**
     * Search the nearest safe square where there
     * is a pellet on the board.
     * The search used is the Breadth First Search.
     *
     * @return the nearest safe square where
     * there is a pellet, null if no safe pellet found.
     */
    @SuppressWarnings("checkstyle:methodlength")
    public Square bfsNearestSafetyPelletSquare() {
        for (int i = 0; i < getBoard().getHeight(); ++i) {
            Arrays.fill(visitedSquare[i], false);
        }

        final Queue<Square> squareQueue = new ArrayDeque<>();
        squareQueue.add(getPlayer().getSquare());
        if (getPlayer().getSquare() != null) {
            visitedSquare[getPlayer().getSquare().getY()][getPlayer().getSquare().getX()] = true;
        }
        while (!squareQueue.isEmpty()) {
            final Square square = squareQueue.remove();

            if (square.getOccupants().size() > 0
                    && square.getOccupants().get(0) instanceof Pellet
                    && !square.equals(getPlayer().getSquare())) {
                return square;
            }
            else {
                final List<Square> neighborsList = AStarPath.getValidNeighbors(square, getPlayer());
                neighborsList.stream().filter(neighborSquare -> neighborSquare != null)
                        .forEach(neighborSquare -> {
                            if (!visitedSquare[neighborSquare.getY()][neighborSquare.getX()]) {
                                squareQueue.add(neighborSquare);
                            }
                            visitedSquare[neighborSquare.getY()][neighborSquare.getX()] = true;
                        });
            }
        }
        return getPlayer().getSquare();
    }

    /**
     * Convert a square's list to know which direction the player must follow
     * to get the nearest safe square.
     *
     * @param squaresList the square's list determining the path
     * @return a queue with each direction at each step
     */
    @SuppressWarnings("checkstyle:methodlength")
    public Deque<Direction> convertPathToDirection(final List<Square> squaresList) {
        final Deque<Direction> directions = new ArrayDeque<>();

        //Direction can't calculate if path has not at least 2 squares
        if (squaresList == null || squaresList.size() < 2) {
            return directions;
        }
        for (int i = 1; i < squaresList.size(); ++i) {
            final Square originSquare = squaresList.get(i - 1);
            final Square destinationSquare = squaresList.get(i);
            final int x = destinationSquare.getX() - originSquare.getX(),
                    y = destinationSquare.getY() - originSquare.getY();
            if (x == 0) {
                //Vertical situation
                if (y == 1 || y < -1) {
                    directions.add(Direction.SOUTH);
                }
                else {
                    directions.add(Direction.NORTH);
                }
            } else {
                //Horizontal situation
                if (x == 1 || x < -1) {
                    directions.add(Direction.EAST);
                }
                else {
                    directions.add(Direction.WEST);
                }
            }
        }
        return directions;
    }

    /**
     * Compute a BFS to know the nearest safety square.
     *
     * @return the nearest safety square, null if there isn't
     */
    public Square bfsNearestSafetySquare() {
        for (int i = 0; i < getBoard().getHeight(); ++i) {
            Arrays.fill(visitedSquare[i], false);
        }

        final Queue<Square> squaresQueue = new ArrayDeque<>();
        squaresQueue.add(getPlayer().getSquare());
        while (!squaresQueue.isEmpty()) {
            final Square square = squaresQueue.remove();
            visitedSquare[square.getY()][square.getX()] = true;
            if (isSafetySquare(square) && !square.equals(getPlayer().getSquare())) {
                return square;
            }
            else {
                final List<Square> neighborsList = AStarPath.getValidNeighbors(square, getPlayer());
                squaresQueue.addAll(neighborsList.stream()
                        .filter(neighbor -> !visitedSquare[neighbor.getY()][neighbor.getX()])
                        .collect(Collectors.toList()));
            }
        }
        return getPlayer().getSquare();
    }

    /**
     * Determines if the square is safe.
     *
     * @param square the current square
     * @return true if the square is safe
     */
    public boolean isSafetySquare(final Square square) {
        for (final Ghost ghost : getGhostsList()) {
            final double distance = AStarPath.manhattanDistance(square.getX(), square.getY(),
                    ghost.getSquare().getX(), ghost.getSquare().getY());
            if (distance < ghostDstThreshold) {
                return false;
            }
        }
        return true;
    }

    /**
     * FInd a not optimised direction in last resort (No best move found).
     *
     * @return a not optimised direction
     */
    public Direction hurryMove() {
        if (getPlayer().getSquare().getSquareAt(getPlayer().getDirection())
                .isAccessibleTo(getPlayer())) {
            return getPlayer().getDirection();
        } else {
            if (getPlayer().getDirection() == Direction.WEST
                    || getPlayer().getDirection() == Direction.EAST) {
                return Direction.SOUTH;
            } else {
                return Direction.EAST;
            }
        }
    }

    /**
     * Define the Pacman Behaviour in the game.
     *
     * @param pelletNbr the pellets number remaining in the game
     */
    private void updatePacmanBehaviour(final int pelletNbr) {
        if (pelletNbr <= HIGH_PELLET_COUNT) {
            //Pacman must recover the last pellets to finish
            if (pelletNbr <= LOW_PELLET_COUNT) {
                setGhostDstThreshold(LOW_GHOST_DST);
            }
                //The pacman play safety and recover pellets if he can
            else {
                setGhostDstThreshold(MEDIUM_GHOST_DST);
            }
        } else {
            setGhostDstThreshold(INITIAL_GHOST_DST); //Pacman play safety
        }
    }

    /**
     * No Strategy to execute for the AI.
     */
    @Override
    public void executeStrategy() { /**/ }

    /**
     * Get the ghostDstThreshold.
     *
     * @return ghostDstThreshold
     */
    public int getGhostDstThreshold() {
        return ghostDstThreshold;
    }

    /**
     * Set the ghostDstThreshold.
     *
     * @param ghostDst the new ghostDstThreshold
     */
    public void setGhostDstThreshold(final int ghostDst) {
        ghostDstThreshold = ghostDst;
    }
}

