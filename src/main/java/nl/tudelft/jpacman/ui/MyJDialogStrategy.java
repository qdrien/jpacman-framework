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
 * Created by Nicolas Leemans on 10/03/16.
 */
public class MyJDialogStrategy extends JDialog
{

    private static final long serialVersionUID = 1L;
    private JButton HumanController, AIController;
    private PacmanStrategy strategy;
    private Game game;
    private PacManUiBuilder builder;
    private PacManUI pacManUI;

    public MyJDialogStrategy(JFrame parent, String title, String message, PacManUiBuilder builder, Game game, PacmanStrategy strategy, PacManUI pacManUI)
    {
        super(parent, title);
        this.builder = builder;
        this.game = game;
        this.strategy = strategy;
        this.pacManUI = pacManUI;
        // set the position of the window
        Point p = new Point(100, 100);
        setLocation(p.x, p.y);

        // Create a message
        JPanel messagePane = new JPanel();
        messagePane.add(new JLabel(message));
        // get content pane, which is usually the
        // Container of all the dialog's components.
        getContentPane().add(messagePane);

        // Create a button
        JPanel buttonPane = new JPanel();
        HumanController = new JButton("Control Pacman");
        AIController = new JButton("Be spectator");
        buttonPane.add(HumanController);
        buttonPane.add(AIController);
        // set action listener on the button
        HumanController.addActionListener(new MyActionListener());
        AIController.addActionListener(new MyActionListener());

        getContentPane().add(buttonPane, BorderLayout.PAGE_END);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }

    // override the createRootPane inherited by the JDialog, to create the rootPane.
    // create functionality to close the window when "Escape" button is pressed
    public JRootPane createRootPane()
    {
        JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        AbstractAction action = new AbstractAction() {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        };
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", action);
        return rootPane;
    }

    // an action listener to be used when an action is performed
    // (e.g. button is pressed)
    class MyActionListener implements ActionListener
    {

        //close and dispose of the window.
        public void actionPerformed(ActionEvent e)
        {
            Object source = e.getSource();
            if(source == HumanController)
            {
                strategy= new HumanControllerStrategy(game,builder);
                System.out.println("The chosen strategy is : " + strategy.getTypeStrategy());
            }
            else if(source == AIController)
            {
                strategy = new PacManhattanAI(game);
                System.out.println("The chosen strategy is : " + strategy.getTypeStrategy());
            }
            game.setStrategy(strategy);
            setVisible(false);
            dispose();
            pacManUI.start();

        }
    }

}
