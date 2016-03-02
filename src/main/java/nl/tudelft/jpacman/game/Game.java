package nl.tudelft.jpacman.game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.Level.LevelObserver;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.ui.PacManUiBuilder;

import javax.swing.*;
import java.awt.event.ActionListener;
/**
 * A basic implementation of a Pac-Man game.
 * 
 * @author Jeroen Roosen 
 */
public abstract class Game extends JFrame implements LevelObserver, ActionListener {

	/**
	 * <code>true</code> if the game is in progress.
	 */
	private boolean inProgress;
    private Square oldDestination;
    private Context context= null;
    private PacManUiBuilder builder;

	/**
	 * Object that locks the start and stop methods.
	 */
	private final Object progressLock = new Object();
    private PlayerMoveTask currentMoveTask;

    /**
	 * Creates a new game.
	 */
	protected Game() {
		inProgress = false;
	}
    private ScheduledExecutorService service;
	/**
	 * Starts or resumes the game.
	 */
	public void start()
    {
        synchronized (progressLock)
        {
			if (isInProgress()) {
				return;
			}
			if (getLevel().isAnyPlayerAlive()
					&& getLevel().remainingPellets() > 0) {
				inProgress = true;
				getLevel().addObserver(this);
				getLevel().start();
			}
            if(service != null)
            {
                this.service = Executors.newSingleThreadScheduledExecutor();
            }
		}
	}

	/**
	 * Pauses the game.
	 */
	public void stop()
    {
		synchronized (progressLock) {
			if (!isInProgress()) {
				return;
			}
            if(service != null)
            {
                this.currentMoveTask.setFinished(true);
                service.shutdownNow();
            }
			inProgress = false;
			getLevel().stop();
		}
	}

	/**
     *
	 * @return <code>true</code> iff the game is started and in progress.
	 */
	public boolean isInProgress() {
		return inProgress;
	}

	/**
	 * @return An immutable list of the participants of this game.
	 */
	public abstract List<Player> getPlayers();

	/**
	 * @return The level currently being played.
	 */
	public abstract Level getLevel();

	/**
	 * Moves the specified player one square in the given direction.
	 * 
	 * @param player
	 *            The player to move.
	 * @param direction
	 *            The direction to move in.
	 */
	public void moveContinu(Player player, Direction direction)
    {
        if (isInProgress())
        {
            Square location = player.getSquare();
            Square destination = location.getSquareAt(direction);
            if(destination.isAccessibleTo(player))// && destination != oldDestination)
            {
                //oldDestination = destination;
                if(service == null)
                {
                    this.service = Executors.newSingleThreadScheduledExecutor();
                }
                else
                {
                    this.currentMoveTask.setFinished(true);
                }
                this.currentMoveTask = new PlayerMoveTask(service, player, direction);
                service.schedule(currentMoveTask, player.getInterval(), TimeUnit.MILLISECONDS);
            }
        }
    }
    /**
     * Moves the specified player one square in the given direction.
     *
     * @param player
     *            The player to move.
     * @param direction
     *            The direction to move in.
     */
    public void move(Player player, Direction direction)
    {
        if(isInProgress())
        {
            getLevel().move(player,direction);
        }
    }

	
	@Override
	public void levelWon() {
		stop();
	}
	
	@Override
	public void levelLost() {
		stop();
	}

    public PacManUiBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(PacManUiBuilder builder) {
        this.builder = builder;
    }


    private final class PlayerMoveTask implements Runnable
    {

        /**
         * The service executing the task.
         */
        private final ScheduledExecutorService s;

        /**
         * The player to move.
         */
        private final Player player;
        private final Direction dir;
        private boolean finished = false;

        /**
         * Creates a new task.
         *
         * @param s
         *            The service that executes the task.
         * @param p
         *            The player to move.
         */
        private PlayerMoveTask(ScheduledExecutorService s, Player p, Direction direction)
        {
            this.s = s;
            this.player = p;
            this.dir = direction;
        }

        @Override
        public void run()
        {
            long interval = player.getInterval();
            if(!finished)
            {
                getLevel().move(player, dir);
                s.schedule(this, interval, TimeUnit.MILLISECONDS);
            }
        }

        public void setFinished(boolean finished)
        {
            this.finished = finished;
        }
        public boolean getFinished()
        {
            return finished;
        }
    }

    private JButton HumanController, AIController;
    private JLabel label;
    private JPanel panel;


    public void buildWindow()
    {
        setTitle("Strategy selection");
        setSize(320, 120);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel(new BorderLayout(40,50));
        HumanController = new JButton("Control Pacman");
        AIController = new JButton("Be spectator");
        HumanController.addActionListener(this);
        AIController.addActionListener(this);
        label = new JLabel("Choose a game mode and then click to start");
        panel.add(label,BorderLayout.NORTH);
        panel.add(AIController,BorderLayout.EAST);
        panel.add(HumanController, BorderLayout.WEST);
        add(panel);
        setVisible(true);
    }


    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if(source == HumanController)
        {
            context = new Context(new StrategyHumanController());
            System.out.println(context.getStrategy());

        }
        else if(source == AIController)
        {
            System.out.println("La stratégie adopté est le controle par une IA");
            context = new Context(new StrategyAIController());
            System.out.println(context.getStrategy());
        }
        context.executeStrategy(builder,this);
        setVisible(false);
    }

}
