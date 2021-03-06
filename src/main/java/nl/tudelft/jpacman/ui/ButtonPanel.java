package nl.tudelft.jpacman.ui;

import javax.swing.*;
import java.util.Map;

/**
 * A panel containing a button for every registered action.
 *
 * @author Jeroen Roosen
 */
class ButtonPanel extends JPanel {

    /**
     * Create a new button panel with a button for every action.
     *
     * @param buttons The map of caption - action for each button.
     * @param parent  The parent frame, used to return window focus.
     */
    ButtonPanel(final Map<String, Action> buttons, final JFrame parent) {
        super();
        assert buttons != null;
        assert parent != null;

        for (final String caption : buttons.keySet()) {
            final JButton button = new JButton(caption);
            button.addActionListener(e -> {
                buttons.get(caption).doAction();
                parent.requestFocusInWindow();
            });
            add(button);
        }
    }
}
