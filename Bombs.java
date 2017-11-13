import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Bombs extends JFrame implements ActionListener
{
	//private JPanel buttons;
    private JMenuBar menuBar;
    private JMenu gameModes, help;
    private JMenuItem beginner, intermediate, expert, custom, quit, instructions;
    private Board gameBoard;
    private Tile tiles;
    private Timer gameTimer;

    private ActionListener timeListen;

    private JLabel timerLabel, outcome;

    private int total, score;

    private int rows = 5;
    private int columns = 5;
    private int bombs = 5;
    private int gameTime = 0;
    private boolean timeStart = true;

    private ClassLoader loader = getClass().getClassLoader();

    private JPanel boardView, labelView;

	private Bombs()
	{
		super("Minesweeper");

        beginner = new JMenuItem("Beginner");
        intermediate = new JMenuItem("Intermediate");
        expert = new JMenuItem("Expert");
        custom = new JMenuItem("Custom");
        quit = new JMenuItem("Quit");
        menuBar = new JMenuBar();
        gameModes = new JMenu("Game");
        help = new JMenu("Help");
        instructions = new JMenuItem("Instructions");
        timerLabel = new JLabel("Timer: 0");
        outcome = new JLabel("");

        menuBar.add(gameModes);
        menuBar.add(help);
        gameModes.add(beginner);
        gameModes.add(intermediate);
        gameModes.add(expert);
        gameModes.add(custom);
        gameModes.add(quit);
        help.add(instructions);
        setJMenuBar(menuBar);

        instructions.addActionListener(this);
        beginner.addActionListener(this);
        intermediate.addActionListener(this);
        expert.addActionListener(this);
        custom.addActionListener(this);

        quit.addActionListener(e ->
        {
            System.exit(0);
        });

        // Add time to clock after first tile is clicked
        timeListen = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                gameTime += 1;
                timerLabel.setText("Timer: " + gameTime);
            }
        };

        boardView = new JPanel();
        labelView = new JPanel();

		Container c = getContentPane();

        gameBoard = new Board(columns, rows, bombs, this);

        boardView.setLayout(new GridLayout(columns, rows, 0, 0));
        labelView.setLayout(new GridLayout(1, 2, 1, 1));
        gameBoard.fillBoardView(boardView);
        gameBoard.setNumbers();
        gameBoard.displayNumbers();
        total = gameBoard.getTotal();
        labelView.add(timerLabel);
        labelView.add(outcome);

        c.add(labelView, BorderLayout.NORTH);
        c.add(boardView, BorderLayout.CENTER);

		setSize(250, 290);
        setVisible(true);
    }

    // Restart the board with the given specifications
    public void restart()
    {
        boardView.removeAll();

        gameBoard = new Board(columns, rows, bombs, this);
        boardView.setLayout(new GridLayout(columns, rows, 0, 0));
        labelView.setLayout(new GridLayout(1, 2, 1, 1));
        gameBoard.fillBoardView(boardView);
        gameBoard.setNumbers();
        gameBoard.displayNumbers();
        total = gameBoard.getTotal();

        timeStart = true;
        gameTime = 0;
        timerLabel.setText("Timer: 0");
        outcome.setText("");
        labelView.repaint();
    }

    public void actionPerformed(ActionEvent e)
    {
        // Change the board to the specified difficulty
        if(e.getActionCommand().equals("Beginner"))
        {
            rows = 5;
            columns = 5;
            bombs = 5;
            restart();

            setSize(250, 290);
            setVisible(true);
        }
        else if(e.getActionCommand().equals("Intermediate"))
        {
            rows = 8;
            columns = 8;
            bombs = 15;
            restart();

            setSize(400, 430);
            setVisible(true);
        }
        else if(e.getActionCommand().equals("Expert"))
        {
            rows = 10;
            columns = 10;
            bombs = 30;
            restart();

            setSize(500, 530);
            setVisible(true);
        }
        else if(e.getActionCommand().equals("Custom"))
        {
            boolean done = false;

            // Ask user for input and require correct input for each
            while(!done)
            {
                String rowString = JOptionPane.showInputDialog("Enter number of rows (from 5-10):");

                // Validate input
                try
                {
                    rows = Integer.valueOf(rowString);

                } catch(NumberFormatException n)
                {
                    continue;
                }

                // Validate number
                if(rows >= 5 && rows <= 10)
                {
                    done = true;
                }
            }

            done = false;

            while(!done)
            {
                String columnString = JOptionPane.showInputDialog("Enter number of columns (from 5-10):");

                try
                {
                    columns = Integer.valueOf(columnString);

                } catch(NumberFormatException n)
                {
                    continue;
                }

                if(columns >= 5 && columns <= 10)
                {
                    done = true;
                }
            }

            done = false;

            while(!done)
            {
                String bombString = JOptionPane.showInputDialog("Enter number of bombs (from 1 - half of size):");

                try
                {
                    bombs = Integer.valueOf(bombString);

                } catch(NumberFormatException n)
                {
                    continue;
                }

                if(bombs >= 1 && bombs <= ((rows * columns) / 2))
                {
                    done = true;
                }
            }

            restart();

            setSize(rows * 50, (columns * 50) + 30);
            setVisible(true);
        }
        else if(e.getActionCommand().equals("Instructions"))
        {
            // Display instructions to user
            JOptionPane.showMessageDialog(this, " - Click tiles to reveal values \n - Avoid bombs \n - Numbers indicate number of bombs surrounding respective tile", "Instructions", JOptionPane.PLAIN_MESSAGE);
        }
        else
        {
            // Start timer on first button click
            if(timeStart)
            {
                gameTimer = new Timer(1000, timeListen);
                gameTimer.setRepeats(true);
                gameTimer.setInitialDelay(0);
                gameTimer.start();
                timeStart = false;
            }

            Tile currentTile = (Tile)e.getSource();

            // If bomb is clicked user loses
            if(currentTile.id() == 9)
            {
                ImageIcon img = new ImageIcon(loader.getResource("res/bombTileExplode.png"));
                currentTile.setBackImage(img);
                currentTile.showBack();

                outcome.setText("You Lose");
                gameTimer.stop();
                gameBoard.userLoss(this);
            }
            // User clicked on blank tile
            else if(currentTile.id() == 0)
            {
                currentTile.showBack();

                int x, y;
                x = currentTile.posX();
                y = currentTile.posY();
                gameBoard.clearBlanks(x, y, this);
            }
            // User clicked on number tile
            else
            {
                currentTile.showBack();
            }

            score = gameBoard.getScore();

            // User won tell them
            if(score == total)
            {
                outcome.setText("You Win!");
                gameTimer.stop();
                gameBoard.userWin(this);
            }

            currentTile.removeActionListener(this);
        }
    }

    public static void main(String args[])
    {
        Bombs B = new Bombs();
        B.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
    }
}