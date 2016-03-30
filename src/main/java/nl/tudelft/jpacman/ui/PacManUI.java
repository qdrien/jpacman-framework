package nl.tudelft.jpacman.ui;

import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.ui.ScorePanel.ScoreFormatter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The default JPacMan UI frame. The PacManUI consists of the following
 * elements:
 * 
 * <ul>
 * <li>A score panel at the top, displaying the score of the player(s).
 * <li>A board panel, displaying the current level, i.e. the board and all units
 * on it.
 * <li>A button panel, containing all buttons provided upon creation.
 * </ul>
 * 
 * @author Jeroen Roosen 
 * 
 */
public class PacManUI extends JFrame {


	/**
	 * Default serialisation UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The desired frame rate interval for the graphics in milliseconds, 40
	 * being 25 fps.
	 */
	private static final int FRAME_INTERVAL = 40;

	/**
	 * The panel displaying the player scores.
	 */
	private final ScorePanel scorePanel;

	/**
	 * The panel displaying the game.
	 */
	private final BoardPanel boardPanel;
    private final ButtonPanel buttonPanel;
    private ButtonGroup choiceLevelGroup;
    private List<JRadioButton> buttonsList;

    /**
	 * Creates a new UI for a JPac-Man game.
	 * 
	 * @param game
	 *            The game to play.
	 * @param buttons
	 *            The map of caption-to-action entries that will appear as
	 *            buttons on the interface.
	 * @param keyMappings
	 *            The map of keyCode-to-action entries that will be added as key
	 *            listeners to the interface.
	 * @param sf
	 *            The formatter used to display the current score. 
	 */
	public PacManUI(final Game game, final Map<String, Action> buttons,
			final Map<Integer, Action> keyMappings, ScoreFormatter sf) {
		super("JPac-Man");
		assert game != null;
		assert buttons != null;
		assert keyMappings != null;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		PacKeyListener keys = new PacKeyListener(keyMappings);
		addKeyListener(keys);

		buttonPanel = new ButtonPanel(buttons, this);
        addLevelChoiceButtons(game);

		scorePanel = new ScorePanel(game.getPlayers());
		if (sf != null) {
			scorePanel.setScoreFormatter(sf);
		}

		boardPanel = new BoardPanel(game);
		
		Container contentPanel = getContentPane();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);
		contentPanel.add(scorePanel, BorderLayout.NORTH);
		contentPanel.add(boardPanel, BorderLayout.CENTER);

		pack();
	}

    /**
     * Adds buttons that allows the user to choose the level he wants to play in
     * @param game The Game instance we are playing on
     */
    private void addLevelChoiceButtons(Game game) {
        buttonsList = new ArrayList<>();
        final JLabel choiceLevelLabel = new JLabel("Choose the level:");
        choiceLevelGroup = new ButtonGroup();
        refreshLevelChoices(0);
        final JButton loadButton = new JButton("Load");
        loadButton.addActionListener(e -> {
			final Enumeration<AbstractButton> elements = choiceLevelGroup.getElements();
            while (elements.hasMoreElements()){
                final AbstractButton button = elements.nextElement();
                if(button.isSelected()) {
					Integer index = Integer.valueOf(button.getText());
					game.stop();
					game.setLevel(index);
                    game.reset();
                    game.getPlayers().get(0).setAlive(true);
                    //sometimes ghost keep on moving after death, its not a but, its a FEATURE
                }
            }
        });
        buttonPanel.add(choiceLevelLabel);
        buttonPanel.add(loadButton);
    }

    /**
	 * Starts the "engine", the thread that redraws the interface at set
	 * intervals.
	 */
	public void start() {
		setVisible(true);

		ScheduledExecutorService service = Executors
				.newSingleThreadScheduledExecutor();

		service.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				nextFrame();
			}
		}, 0, FRAME_INTERVAL, TimeUnit.MILLISECONDS);

	}

	/**
	 * Draws the next frame, i.e. refreshes the scores and game.
	 */
	private void nextFrame() {
		boardPanel.repaint();
		scorePanel.refresh();
	}

    /**
     * Removes all "level selection" radio buttons and adds those that are needed (up to @maxLevelReached)
     * @param maxLevelReached The max level the player has reached
     */
    public void refreshLevelChoices(int maxLevelReached){
        for (final JRadioButton button : buttonsList) {
            choiceLevelGroup.remove(button);
            buttonPanel.remove(button);
        }
        buttonsList.clear();
        final JRadioButton level1 = new JRadioButton("1", true);
        choiceLevelGroup.add(level1);
        buttonPanel.add(level1);
        buttonsList.add(level1);
        for(int i = 1; i < maxLevelReached + 1; i++){
            final JRadioButton button = new JRadioButton(String.valueOf(i + 1), false);
            choiceLevelGroup.add(button);
            buttonPanel.add(button);
            buttonsList.add(button);
        }
        pack();
    }
}
