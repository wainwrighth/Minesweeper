import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class Board
{
	private Tile tiles[][];

	private ClassLoader loader = getClass().getClassLoader();

	private int length, cols, bombs;

	// Randomize the locations of the cards
    public void Randomize(Tile a[][])
    {
    	Random rand = new Random();
    	Tile t = new Tile();

    	// Loop through array and swap each with random positions
    	for(int i = 0; i < a.length; i++)
    	{
    		for(int j = 0; j < a[i].length; j++)
    		{
    			int num1 = rand.nextInt(i + 1);
    			int num2 = rand.nextInt(j + 1);
	    		t = a[i][j];
	    		a[i][j] = a[num1][num2];
	    		a[num1][num2] = t;
    		}
    	}
    }

    // Constructor for Board
	public Board(int column, int row, int bombCount, ActionListener AL)
	{
		tiles = new Tile[column][row];
		String imgPath;
		ImageIcon img;
		length = row;
		cols = column;
		bombs = bombCount;
		int count = 0;

		// Loop through and insert tiles
	 	for(int i = 0; i < column; i += 1)
	 	{
	 		for(int j = 0; j < row; j += 1)
	 		{
	 			Tile t;

	 			// Enter the specified amount of bombs
		 		if(count < bombCount)
		 		{
		 			imgPath = "res/bombTile.png";
		 			img = new ImageIcon(loader.getResource(imgPath));
		 			t = new Tile(img);
		 			t.setID(9);
		 			count += 1;
		 		}
		 		// Enter blank tiles for rest of set
		 		else
		 		{
		 			imgPath = "res/blankTile.png";
		 			img = new ImageIcon(loader.getResource(imgPath));
		 			t = new Tile(img);
		 			t.setID(0);
		 		}

		 		tiles[i][j] = t;
		 		t.addActionListener(AL);
	 		}
	 	}

	 	// Randomize tiles to create shuffled board
	 	Randomize(tiles);

	 	int posValX = 0;
	 	int posValY = 0;

	 	// Set position values for each tile for future reference
	 	for(int k = 0; k < column; k += 1)
	 	{
	 		for(int l = 0; l < row; l += 1)
	 		{
	 			tiles[k][l].setPos(k, l);
	 			posValX += 1;
	 		}

	 		posValX = 0;
	 		posValY += 1;
	 	}
	}

	// Function that clears surrounding blanks when clicked
	public void clearBlanks(int xVal, int yVal, ActionListener AL)
	{
		int vals[][] = {{-1, 1}, {1, 0}, {1, 0}, {0, -1}, {0, -1}, {-1, 0}, {-1, 0}, {0, 1}};

		int x = xVal;
		int y = yVal;

		// If the tile has been explored dont expand
		if(tiles[xVal][yVal].getVisited() == true)
		{
			return;
		}

		// Go through surrounding tiles and show if possible
		for(int k = 0; k < vals.length; k += 1)
		{
		    x += vals[k][0];
		    y += vals[k][1];

			if(!(x < 0 || x >= length || y < 0 || y >= length))
			{
				tiles[x][y].showBack();
				tiles[x][y].removeActionListener(AL);
			}
		}

		// Set appropriate tiles to visited to prevent infinite recursion
		tiles[xVal][yVal].setVisited(true);
		tiles[xVal][yVal].removeActionListener(AL);

		// If the surrounding tile is a blank expand it
		for(int i = 0; i < vals.length; i += 1)
		{
			x += vals[i][0];
		    y += vals[i][1];

			if(!(x < 0 || x >= length || y < 0 || y >= length))
			{
				if(tiles[x][y].id() == 0)
				{
					clearBlanks(x, y, AL);
				}
			}
		}
	}

	// Set the number marker for a blank tile
	public void setNumbers()
	{
		int vals[][] = {{-1, 1}, {1, 0}, {1, 0}, {0, -1}, {0, -1}, {-1, 0}, {-1, 0}, {0, 1}};

		// Loop through board and look for bombs
		for(int i = 0; i < length; i += 1)
		{
			for(int j = 0; j < tiles[i].length; j += 1)
			{
				// If bomb is found add 1 to every surrounding it
				if(tiles[i][j].id() == 9)
				{
					int x = i;
					int y = j;

					for(int k = 0; k < vals.length; k += 1)
					{
					    x += vals[k][0];
					    y += vals[k][1];

						if(!(x < 0 || x >= length || y < 0 || y >= tiles[i].length || tiles[x][y].id() == 9))
						{
							tiles[x][y].setID(tiles[x][y].id() + 1);
						}
					}
				}
			}
		}
	}

	// Add numnbers to faces of tiles
	public void displayNumbers()
	{
		for(int i = 0; i < tiles.length; i += 1)
		{
			for(int j = 0; j < tiles[i].length; j += 1)
			{
				if(tiles[i][j].id() != 0 && tiles[i][j].id() != 9)
				{
					// Add the specified number to the tile if it isnt a 0 or 9
					String imgPath = "res/tile" + tiles[i][j].id() + ".png";
            		ImageIcon img = new ImageIcon(loader.getResource(imgPath));

            		tiles[i][j].setBackImage(img);
            		tiles[i][j].setIcon(img);
            		tiles[i][j].hideBack();
				}
			}
		}
	}

	// Return total number of free tiles on the board
	public int getTotal()
	{
		int total = length * cols;
		total = total - bombs;
		return total;
	}

	// Get the current score of the board
	public int getScore()
	{
		int score = 0;

		for(int i = 0; i < tiles.length; i += 1)
		{
			for(int j = 0; j < tiles[i].length; j += 1)
			{
				if(tiles[i][j].customName().equals("revealed"))
				{
					score += 1;
				}
			}
		}

		return score;
	}

	// If user loses display bombs and disable board
	public void userLoss(ActionListener AL)
	{
		for(int i = 0; i < tiles.length; i += 1)
		{
			for(int j = 0; j < tiles[i].length; j += 1)
			{
				if(tiles[i][j].id() == 9)
				{
					tiles[i][j].showBack();
				}

				tiles[i][j].removeActionListener(AL);
			}
		}
	}

	// If user wins disable board
	public void userWin(ActionListener AL)
	{
		for(int i = 0; i < tiles.length; i += 1)
		{
			for(int j = 0; j < tiles[i].length; j += 1)
			{
				tiles[i][j].removeActionListener(AL);
			}
		}
	}

	// Properly fill board
	public void fillBoardView(JPanel view)
    {
    	for(int i = 0; i < tiles.length; i += 1)
		{
			for(int j = 0; j < tiles[i].length; j += 1)
			{
				view.add(tiles[i][j]);
			}
		}
    }
}