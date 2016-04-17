package nl.tudelft.jpacman.strategy;

import nl.tudelft.jpacman.ui.MyJDialogStrategy;
import org.junit.Test;

import javax.swing.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Class to test the creation of window
 * to chose strategy in the game.
 */
public class MyJDialogStrategyTest {
    /**
     * Test the creation of the dialog.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    @Test
    public void chosenStrategyDialogTest() {
        final MyJDialogStrategy dialog =
                new MyJDialogStrategy(new JFrame(), null, null, null);
        dialog.setSize(400, 200);

        assertNotNull("The window hasn't been instantiated", dialog);
        assertEquals("Dimension incorrect", dialog.getY(), 100);
        assertEquals("Dimension incorrect", dialog.getX(), 100);
    }

}
