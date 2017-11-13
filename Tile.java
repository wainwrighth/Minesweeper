import javax.swing.*;

class Tile extends JButton
{
	// Resource loader initializer
	private ClassLoader loader = getClass().getClassLoader();

	// Values to initialize
	private int id;
	private String customName;
	private int posX, posY;
	private boolean visited;

	private Icon back;
	private Icon cover = new ImageIcon(loader.getResource("res/coverTile.png"));

	// Default constructor
    public Tile()
    { 
    	super();
    	visited = false;
    	customName = "covered";
    }

    // Constructor with parameters for tile
	public Tile(ImageIcon backImage)
	{
		visited = false;
		customName = "covered";
		back = backImage;
		super.setIcon(cover);
		super.setSize(50, 50);
	}

	// Metadata: position number
    public int posX() { return posX; }
    public int posY() { return posY; }
    public void setPos(int px, int py)
    { 
    	posX = px;
    	posY = py;
    }

    // Change visited value of tile
    public void setVisited(boolean b) { visited = b; }
    public boolean getVisited() { return visited; }

	// Metadata: ID number
    public int id() { return id; }
    public void setID(int i) { id = i; }

    // Tile flipping functions
    public void showBack() 
    {
    	super.setIcon(back);
    	customName = "revealed";
    }
   	public void hideBack(){super.setIcon(cover);}

    // // Metadata: Custom name
	public String customName() { return customName; }
    // public void setCustomName(String s) { customName = s; }

    // Set the back number under the tile
    public void setBackImage(ImageIcon backImage) { back = backImage; }

}