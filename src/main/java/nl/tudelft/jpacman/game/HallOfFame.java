package nl.tudelft.jpacman.game;

import javax.swing.*;
import java.io.*;
import java.nio.channels.FileChannel;

public class HallOfFame
{
    /**
     * Number of best scores stored in the hall of fame.
     */
    private static final int NUMBER_OF_RECORDS_KEPT = 10;

    /**
     * Longest names allowed in hall of fame.
     */
    private static final int NAME_LENGTH = 25;

    /**
     * Relative path of the default Hall of Fame.
     */
    private static final String DEFAULT_HOF_PATH = new File("").getAbsolutePath() + "/src/main/resources/DefaultHoF.txt";

    /**
     * Relative path of the running Hall of Fame.
     */
    private static final String HOF_PATH = new File("").getAbsolutePath() + "/src/main/resources/HoF.txt";

    /**
     * The points scored in the game.
     */
    private int score;

    /**
     * Get relative path of the running Hall of Fame.
     */
    public String getHOFPath()
    {
        return HOF_PATH;
    }

    /**
     * Get relative path of the running Hall of Fame.
     */
    public String getDefaultHofPath()
    {
        return DEFAULT_HOF_PATH;
    }

    public int getNumberOfRecordsKept()
    {
        return NUMBER_OF_RECORDS_KEPT;
    }

    public void handleHOF(int pointsScored)
    {
        int bestScores[] = new int[10];
        String bestPlayers[] = new String[10];
        score = pointsScored;

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(HOF_PATH));
            for (int i = 0; i < NUMBER_OF_RECORDS_KEPT; i++)
            {
                String tmp[] = reader.readLine().split(" ");
                bestPlayers[i] = tmp[0];
                bestScores[i] = Integer.parseInt(tmp[1]);
            }
            reader.close();
        }
        catch (IOException e)
        {
            System.out.println("Error whilst reading HoF.txt "+e.getMessage());
        }

        //Inserting eventual better score into Hall of Fame.
        updateHOF(score, bestScores, bestPlayers);
        //Displaying the HOF, regardless of whether it has been updated or not.
        displayHOF(bestScores, bestPlayers);
    }

    private void updateHOF(int score, int[] bestScores, String[] bestPlayers)
    {
        for (int i = 0; i < NUMBER_OF_RECORDS_KEPT; i++)
        {
            if(score > bestScores[i])
            {
                for (int j = NUMBER_OF_RECORDS_KEPT - 1; j > i; j--)
                {
                    bestScores[j] = bestScores[j - 1];
                    bestPlayers[j] = bestPlayers[j - 1];
                }
                String options[] = {"Ok"};
                JPanel panel = new JPanel();
                JLabel label = new JLabel("Enter your name: ");
                JTextField userInput = new JTextField(NAME_LENGTH);
                panel.add(label);
                panel.add(userInput);
                JOptionPane.showOptionDialog(null, panel, "New High Score!", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                String tmp = userInput.getText();
                bestPlayers[i] = tmp;
                bestScores[i] = score;
                //Saving new Hall of Fame to file.
                saveUpdatedHOF(bestScores, bestPlayers);
                break;
            }
        }
    }

    private void displayHOF(int bestScores[], String bestPlayers[])
    {
        String text = "";
        Object options[] = {"Leave", "Reset"};
        for (int i = 0; i < NUMBER_OF_RECORDS_KEPT; i++) text += formatDisplay(bestScores[i], bestPlayers[i]);
        int buttonPressed = JOptionPane.showOptionDialog(null, text, "Hall of Fame", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (buttonPressed == 1) resetHOF();
    }

    //Trying to align the displays in 2 columns.
    private String formatDisplay(int score, String player)
    {
        String padding = "";
        for (int i = NAME_LENGTH; i > player.length(); i--) padding += " ";
        return player + padding + score + "\n\n";
    }

    private void saveUpdatedHOF(int bestScores[], String bestPlayers[])
    {
        try
        {
            BufferedWriter w = new BufferedWriter(new FileWriter(HOF_PATH));
            for (int i = 0; i < NUMBER_OF_RECORDS_KEPT; i++) w.write(bestPlayers[i] + " " + bestScores[i]+"\n");
            w.close();
        }
        catch (IOException e)
        {
            System.out.println("Error whilst writing to HoF.txt "+e.getMessage());
        }
    }

    public void resetHOF()
    {
        Object options[] = {"Yes", "No"};
        int buttonPressed = JOptionPane.showOptionDialog(null, "Do you really want to erase the Hall of Fame?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
        if (buttonPressed == 0)
        {
            try
            {
                FileChannel src = new FileInputStream(DEFAULT_HOF_PATH).getChannel(), dest = new FileOutputStream(HOF_PATH).getChannel();
                dest.transferFrom(src, 0, src.size());
                JOptionPane.showMessageDialog(null, "Hall of Fame reset!", "Reset", JOptionPane.PLAIN_MESSAGE);
            }
            catch (IOException e)
            {
                System.out.println("Error whilst writing to HoF.txt "+e.getMessage());
            }
        }
    }
}
