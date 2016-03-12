package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Pellet;
import nl.tudelft.jpacman.npc.ghost.Ghost;

import java.util.*;


/**
 * Created by Nicolas Leemans on 8/03/16.
 */
public class PacManhattanAI extends AIStrategy
{
    private Deque<Direction> directionQueue;//Queue containing the potential directions to follow
    private AStarPath pathAStar; //The path calculates with AStar
    private boolean[][] visitedCase;//List to know if the square is yet visited or not
    private final static int GHOST_DST_THRESHOLD = 14;//The threshold distance to move the player
    /**
     * The default constructor
     *
     * @param game the current game
     */
    public PacManhattanAI(final Game game)
    {
        super(game);
        init(game);
    }

    /**
     * Initialise the data used to calculate the best movement to apply
     * @param game
     *      The current game
     */
    public void init(Game game)
    {
        visitedCase = new boolean[getBoard().getHeight()][getBoard().getWidth()];
        pathAStar = new AStarPath(game);
        directionQueue = new ArrayDeque<>();
    }

    /**
     * Calculates the best move to apply according the data of the game (ghosts position, pellet nearest,...)
     * @return the best move
     */
    @Override
    public Direction nextMove()
    {
        Square square;
        for(Ghost ghost : getGhostsList())
        {
            double distance = AStarPath.ManhattanDistance(getPlayer().getSquare().getX(), getPlayer().getSquare().getY(), ghost.getSquare().getX(), ghost.getSquare().getY());
            if (distance < GHOST_DST_THRESHOLD)
            {
                square = BFSNearestSafetySquare();
                computePath(square);
            }
        }
        //If there is no safety path found (or ghosts are far), calculate the nearest safety square where there is a pellet
        if (directionQueue.isEmpty())
        {
            computePath(BFSNearestSafetyPelletSquare());

            if (!directionQueue.isEmpty())
            {
                return directionQueue.removeFirst();

            }
            else
            {
                return getPlayer().getDirection();
            }
        }
        else
        {
            return directionQueue.removeFirst();
        }
    }

    /**
     * Compute a path
     * @param square the goal square
     */
    public void computePath(Square square)
    {
        if (square == null)
        {
            directionQueue = null;
        }

        pathAStar = new AStarPath(game);
        pathAStar.setGoal(square);

        List<Square> path = pathAStar.compute(getPlayer().getSquare());

        //Check if the path is safe
        if (pathAStar.getCost() > AStarPath.GHOST_COST)
        {
            path = null;
        }
        directionQueue = convertPathToDirection(path);
    }

    /**
     * Search the nearest safe square where there is a pellet on the board.
     * The search used is the Breadth First Search
     * @return the nearest safe square where there is a pellet, null if no safe pellet found
     */
    public Square BFSNearestSafetyPelletSquare()
    {
        for (int i = 0; i < getBoard().getHeight(); i++)
        {
            for(int j = 0; j < getBoard().getWidth(); j++)
            {
                visitedCase[i][j] = false;
            }
        }

        Queue<Square> squareQueue = new ArrayDeque<>();
        squareQueue.add(getPlayer().getSquare());
        if(getPlayer().getSquare() != null)
        {
            visitedCase[getPlayer().getSquare().getY()][getPlayer().getSquare().getX()] = true;
        }
        while (squareQueue.isEmpty()==false)
        {
            Square square = squareQueue.remove();
            if (square.getOccupants().size() > 0 && square.getOccupants().get(0) instanceof Pellet)
            {
                return square;
            }
            else
            {
                List<Square> neighborsList = getValidNeighbors(square);
                for(Square neighborSquare : neighborsList)
                {
                    if(neighborSquare != null)
                    {
                        if(visitedCase[neighborSquare.getY()][neighborSquare.getX()] == false)
                        {
                            squareQueue.add(neighborSquare);
                        }
                        visitedCase[neighborSquare.getY()][neighborSquare.getX()] = true;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Convert a square's list to know which direction the player must follow to get the nearest safe square
     * @param squaresList the square's list determining the path
     * @return a queue with each direction at each step
     */
    public Deque<Direction> convertPathToDirection(List<Square> squaresList)
    {

        Deque<Direction> directions = new ArrayDeque<>();

        //Direction can't calculate if path has not at least 2 squares
        if(squaresList == null || squaresList.size() < 2)
        {
            return directions;
        }
        for(int i = 1; i < squaresList.size(); ++i)
        {
            Square originSquare = squaresList.get(i-1);
            Square destinationSquare = squaresList.get(i);
            int x = destinationSquare.getX() - originSquare.getX();
            int y = destinationSquare.getY() - originSquare.getY();

            if(x == 0)
            {
                //Vertical situation
                if(y == 1 || y < -1)
                {
                    directions.add(Direction.SOUTH);
                }
                else
                {
                    directions.add(Direction.NORTH);
                }
            }
            else
            {
                //Horizontal situation
                if(x == 1 || x < -1)
                {
                    directions.add(Direction.EAST);
                }
                else
                {
                    directions.add(Direction.WEST);
                }
            }
        }
        return directions;
    }

    /**
     * Compute a BFS to know the nearest safety square
     * @return the nearest safety square, null if there is'nt
     */
    public Square BFSNearestSafetySquare()
    {
        for (int i = 0; i < getBoard().getHeight(); ++i)
        {
            for (int j = 0; j < getBoard().getWidth(); ++j)
            {
                visitedCase[i][j] = false;
            }
        }

        Queue<Square> squaresQueue = new ArrayDeque<>();
        squaresQueue.add(getPlayer().getSquare());

        while (!squaresQueue.isEmpty())
        {
            Square square = squaresQueue.remove();
            visitedCase[square.getY()][square.getX()] = true;
            if (isSafetySquare(square))
            {
                return square;
            }
            else
            {
                List<Square> neighborsList = getValidNeighbors(square);
                for (Square neighbor : neighborsList)
                {
                    if (!visitedCase[neighbor.getY()][neighbor.getX()])
                        squaresQueue.add(neighbor);
                }
            }
        }
        return null;
    }

    /**
     * Get valid neighbors to visit
     * @param square A determined square
     * @return a new list with valid neighbor
     */
    public List<Square> getValidNeighbors(Square square)
    {
        List<Square> neighbors = square.getNeighbours();

        List<Square> validNeighbors = new ArrayList<>(neighbors);
        Iterator<Square> iterator = validNeighbors.iterator();

        while(iterator.hasNext())
        {
            Square neighbor = iterator.next();
            boolean invalidNeighbor = false;
            if(neighbor.isAccessibleTo(getPlayer()))
            {
                if(neighbor.getOccupants().size()==2)
                {
                    invalidNeighbor = neighbor.getOccupants().get(1) instanceof Ghost;
                }
                if(neighbor.getOccupants().size()==1)
                {
                    invalidNeighbor = neighbor.getOccupants().get(0) instanceof Ghost;
                }
            }
            else
            {
                invalidNeighbor = true;
            }
            if(invalidNeighbor)
            {
                iterator.remove();
            }
        }
        return validNeighbors;
    }
    /**
     * Determines if the square is safe
     * @param square the current square
     * @return true if the square is safe
     */
    public boolean isSafetySquare(Square square)
    {
        for (Ghost ghost : getGhostsList())
        {
            double distance = AStarPath.ManhattanDistance(square.getX(), square.getY(), ghost.getSquare().getX(), ghost.getSquare().getY());
            if (distance < GHOST_DST_THRESHOLD)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * No Strategy to execute for the AI
     */
    @Override
    public void executeStrategy() {}

}

