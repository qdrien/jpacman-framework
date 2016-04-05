package nl.tudelft.jpacman.ui;

import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.strategy.HumanControllerStrategy;
import nl.tudelft.jpacman.strategy.PacManhattanAI;
import nl.tudelft.jpacman.strategy.PacmanStrategy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class that creates a selection window to chose the strategy to apply in game.
 */
public class MyJDialogStrategy extends JDialog {
    /**
     * Button of the window.
     */
    private final JButton humanController, aiController;
    /**
     * The game.
     */
    private final Game game;
    /**
     * The builder.
     */
    private final PacManUiBuilder builder;
    private final PacManUI pacManUI;
    /**
     * The strategy chosen by the player.
     */
    private PacmanStrategy strategy;

    /**
     * Create a new window to chose the game mode (strategy,...).
     *
     * @param parent   a JFrame parent
     * @param builder  the builder
     * @param game     the game
     * @param pacManUI the pacManUI
     */
    public MyJDialogStrategy(JFrame parent, PacManUiBuilder builder, Game game, PacManUI pacManUI) {
        super(parent, "Strategy selection");
        this.builder = builder;
        this.game = game;
        this.pacManUI = pacManUI;
        final Point p = new Point(100, 100);
        setLocation(p.x, p.y);// set the position of the window
        // Create a message
        final JPanel messagePane = new JPanel();
        messagePane.add(new JLabel("Choose a game mode and then click to start"));
        // get content pane, which is usually the
        // Container of all the dialog's components.
        getContentPane().add(messagePane);
        // Create a button
        final JPanel buttonPane = new JPanel();
        humanController = new JButton("Control Pacman");
        aiController = new JButton("Be spectator");
        buttonPane.add(humanController);
        buttonPane.add(aiController);
        // set action listener on the button
        humanController.addActionListener(new MyActionListener());
        aiController.addActionListener(new MyActionListener());
        getContentPane().add(buttonPane, BorderLayout.PAGE_END);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }

    /**
     * override the createRootPane inherited by the JDialog, to create the rootPane.
     * create functionality to close the window when "Escape" button is pressed
     *
     * @return the root pane
     */
    public JRootPane createRootPane() {
        final JRootPane rootPane = new JRootPane();
        final AbstractAction action = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        };
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", action);
        return rootPane;
    }

    /**
     * An action listener to be used when an action is performed.
     * (e.g. button is pressed)
     */
    private class MyActionListener implements ActionListener {
        //close and dispose of the window.
        public void actionPerformed(ActionEvent e) {
            final Object source = e.getSource();
            if (source == humanController) {
                strategy = new HumanControllerStrategy(game, builder);
            }
            else if (source == aiController) {
                strategy = new PacManhattanAI(game);
            }
            game.setStrategy(strategy);
            setVisible(false);
            dispose();
            pacManUI.start();
        }
    }
}
