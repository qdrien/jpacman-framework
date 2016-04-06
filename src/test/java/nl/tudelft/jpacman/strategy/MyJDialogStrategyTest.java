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
public class MyJDialogStrategyTest
{
    @Test
    public void chosenStrategyDialogTest() {
        final MyJDialogStrategy dialog =
                new MyJDialogStrategy(new JFrame(), null, null, null);
        dialog.setSize(400, 200);

        assertNotNull(dialog);
        assertEquals(dialog.getY(), 100);
        assertEquals(dialog.getX(), 100);


    }

}
