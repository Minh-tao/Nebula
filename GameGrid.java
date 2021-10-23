import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;
import javax.swing.*;


//    ******************************************************
//    *                                                    *
//    *    !!   DO  NOT  MODIFY THIS FILE     !!       *
//    *                                                    *
//    ******************************************************
import java.awt.Color;


//Represents the 2D grid that stores the state of our "game board"
//Grid is a matrix of cells
//    - each cell contain a given color or null i.e. no color
public class GameGrid extends JComponent implements KeyListener {
    
    
    //---------------- Class Variables and Constants -----------------//
    
    //indicates no key has been pressed
    public static final int NO_KEY = -1; 
    
    //default color for a blank cell on the grid
    public static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK; 
    
    //notice not final, thus it can be changed in constructor
    //(if you don't use a background image, you can change the overall color)
    public static Color currentBackgroundColor = DEFAULT_BACKGROUND_COLOR; 
    
    
    //---------------- Instance Variables -----------------//
    
    //matrix storing state of each cell on grid
    private Cell[][] cells;
    
    //JFrame used to draw graphics in window
    private JFrame frame;
    //Color of grid lines
    private Color lineColor;
    //background image filename
    private Image background = null;
    //splash screen image filename
    private Image splash = null;
    
    //tracks the last keyboard key pressed by the user
    private int lastKeyPressed;
    //tracks last location clicked on by the user
    private Location lastLocationClicked;
    
    
    
    //---------------- Constructors -----------------//
    
    //uses provided dimensions and default background color (see 3 arg)
    public GameGrid(int numRows, int numCols) {
        init(numRows, numCols, null);
    }
    
    //uses provided dimensions and solid color background 
    public GameGrid(int numRows, int numCols, Color bcolor) {
        init(numRows, numCols, bcolor);
    }
    
    //uses provided dimensions and image file as background image
    public GameGrid(int numRows, int numCols, String backgroundImg) {        
        setGridBackgroundImage(backgroundImg);
        init(numRows, numCols, null);
    }
    
    
    
    
    //---------------- Instance Methods -----------------//
    
    //repaints contents of the game grid
    public void paintComponent(Graphics g) {
        
        if (splash!=null)
            g.drawImage(splash, 0, 0, frame.getWidth(), frame.getHeight(), null);
        else 
            drawGameGrid(g);
    }
    
    
    //updates title of the game window with specified String
    public void setTitle(String title) {
        frame.setTitle(title);
    }
    
    
    //draws the argument image in the cell at the argument location 
    public void setCellImage(Location loc, String imgName) {
        if (isValid(loc)) {
            cells[loc.getRow()][loc.getCol()].setImageFileName(imgName);
            repaint();
        } else 
        System.out.println("Can't set image for this invalid location\n" +
        	"Bad grid.setImage(loc, name) call with " + imgName);
        
    } 
    
    
    //returns the name of the image being drawn in the Cell at the arg Location
    public String getCellImage(Location loc) {
        if (isValid(loc))
            return cells[loc.getRow()][loc.getCol()].getImageFileName();
        System.out.println("Can't get image at this invalid location\n" + 
        	"Bad grid.getImage(loc) call");
        return null;
        
    }
    
    
    //returns the number of rows (i.e. height) of the grid
    public int getNumRows() { 
        return cells.length; 
    }
    
    
    //returns the number of columns (ie. width) of the grid
    public int getNumCols() { 
        return cells[0].length; 
    }
    
    
    //returns the current color of the grid lines
    public Color getLineColor() { 
        return lineColor; 
    }
    
    
    //updates the current color of the grid lines
    //Hint: can be useful for debugging scrolling/motion in your game!
    public void setLineColor(Color color) {
        lineColor = color;
        repaint();
    }
    
    
    // set the color at that valid location 
    //    null passed for color to represent no color i.e. no block present
    public void setColor(Location loc, Color color) {
        if (isValid(loc)) {  
            cells[loc.getRow()][loc.getCol()].setColor(color);
            repaint();
        }
    }
    
    // set the color at that valid coordinates (row, col int)
    //    null passed for color to represent no color i.e. no block present
    // Note: redirect to the other setColor() that takes a location parameter
    public void setColor(int r, int c, Color color) {
        setColor(new Location(r, c), color);
    }
    
    
    // return either the color at that valid location 
    //        or null (if invalid location or no color i.e. no block present)
    public Color getColor(Location loc) {
        if (isValid(loc))
            return cells[loc.getRow()][loc.getCol()].getColor();
        
        return null;
    }
    
    // return either the color at that valid coordinates (row, col ints)
    //        or null (empty color i.e. no block present)
    // Note: redirect to the other getColor() that takes a location parameter
    public Color getColor(int r, int c) {
    	return getColor(new Location(r, c));
    }
    
    //sets the background of the grid to the argument image file name; 
    //null for no background image
    public void setGridBackgroundImage(String imageFileName) {
        background = setImage(imageFileName);
        splash = null;
        repaint();
    }
    
    //sets the background of the grid to the argument color; 
    //null for no (i.e. default) color
    public void setGridBackgroundColor(Color backgroundColor) {    
        if (backgroundColor != null)
            currentBackgroundColor = backgroundColor;
        else            
            currentBackgroundColor = DEFAULT_BACKGROUND_COLOR;
    }
    
    //sets splash image drawn drawn on top of the grid to the arg image file
    public void setSplashScreen(String imageFileName) {
        splash = setImage(imageFileName);
        repaint();
    }
    
    
    //returns the last keyboard key pressed by the user
    public int checkLastKeyPressed() {
        int key = lastKeyPressed;
        lastKeyPressed = NO_KEY;
        return key;
    }
    
    //returns location clicked since last call
    //    null otherwise 
    public Location checkLastLocationClicked() {
    	Location loc = lastLocationClicked;
    	lastLocationClicked = null;
    	return loc;
    }
    
    
    //saves a screenshot of the grid's  state to a img file of the arg name
    public void save(String imgFileName) {
        try {
            BufferedImage bi = new BufferedImage(getWidth(), getHeight(), 
            	BufferedImage.TYPE_INT_RGB);
            
            paintComponent(bi.getGraphics());
            int index = imgFileName.lastIndexOf('.');
            if (index == -1)
                throw new RuntimeException("invalid file name:  " + imgFileName);
            ImageIO.write(bi, imgFileName.substring(index + 1), new File(imgFileName));
            
        } catch(IOException e) {
            throw new RuntimeException("unable to save to file:  " + imgFileName);
        }
    }
    
    
    
    //Initilizes a single cell on the grid
    private int initCell(int numRows, int numCols, Color backgroundColor) {
        //set the cell color to the provided bcolor or to the default BLACK 
        this.setGridBackgroundColor(backgroundColor);
        
        cells = new Cell[numRows][numCols];
        
        for (int row = 0; row < numRows; row++)
            for (int col = 0; col < numCols; col++)
            cells[row][col] = new Cell();
        
        //initial cellSize for determining Window size  
        return Math.max(Math.min(1000 / numRows, 1000 / numCols), 1);    
    }
    
    
    //initializes the GameGrid with the argument attributes
    private void init(int numRows, int numCols, Color bcolor) {
        
        int cellSize = initCell(numRows, numCols, bcolor);
        
        setPreferredSize(new Dimension(cellSize * numCols, cellSize * numRows));
        
        frame = new JFrame("Grid");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(this);
        frame.getContentPane().add(this);
        frame.pack();
        frame.setVisible(true);
        
        lastKeyPressed = NO_KEY;
    }
    
    
    
    //(re)paints the contents of the grid
    private void drawGameGrid(Graphics g) {
        
        if (background!=null) {
            g.drawImage(background, 0, 0, 
            	frame.getWidth(), frame.getHeight(), null);
        }
        
        for (int row = 0; row < getNumRows(); row++) {
            for (int col = 0; col < getNumCols(); col++) {
                
                int cellSize = getCellSize();
                
                int x = col * cellSize;
                int y = row * cellSize; 
                
                drawCell(g, x, y, new Location(row, col), cellSize);
                
                //draw boundary if lineColor has been set
                if (lineColor != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    g.setColor(lineColor);
                    g2.setStroke(new BasicStroke(3));
                    g.drawRect(x, y, cellSize, cellSize);
                }
            }  
        }
    }
    
    
    //repaints a single cell on the GameGrid
    private void drawCell(Graphics g, int x, int y, Location loc, int cellSize) {
        
        Cell cell = cells[loc.getRow()][loc.getCol()];
        
        Image image = setImage(cell.getImageFileName());
        
        cell.draw(g, x,  y, image, cellSize, background);
    }
    
    
    //helper function used to determine the height/width of each individual Cell
    private int getCellSize() {
        int cellWidth = getWidth() / getNumCols();
        int cellHeight = getHeight() / getNumRows();
        return Math.min(cellWidth, cellHeight);
    }
    
    
    
    //determines if the arg Location is valid (ie. not out of bounds)
    private boolean isValid(Location loc) {
        
        int row = loc.getRow();
        int col = loc.getCol();
        if (0 <= row && row < getNumRows() && 0 <= col && col < getNumCols())
            return true;
        else 
            throw new RuntimeException("Invalid " + loc);
        
    }
    
    
    //opens and returns the image of the argument filename
    private Image setImage(String imageFileName) {
        if (imageFileName == null)
            return null;
        URL url = getClass().getResource(imageFileName);
        if (url == null)
            throw new RuntimeException("cannot find file:  " + imageFileName);
        return new ImageIcon(url).getImage();
    }
    
    
    //updates the last keyboard key pressed by the user
    public void keyPressed(KeyEvent e) {
        lastKeyPressed = e.getKeyCode();
    }
    
    
    //required by key listener, not implemented
    public void keyReleased(KeyEvent e) { }  
    public void keyTyped(KeyEvent e) { }
    
    
    //-----   Inner class Cell: DO NOT MODIFY!  ----- 
    class Cell {
        private Color color = null;
        private String imageFileName;
        
        public Cell() {
            imageFileName = null;
        }
        
        
        public void setColor(Color c) {
            color = c;
        }
        
        public Color getColor() {
            return color;
        }
        
        public String getImageFileName() {
            return imageFileName;
        }
        
        public void setImageFileName(String fileName) {
            imageFileName = fileName;
        }
        
        public void draw(Graphics g, int x, int y, Image image, int cellSize, Image background) {
            
            if (background == null || color != null) {
                
                if (background == null) 
                    g.setColor(GameGrid.currentBackgroundColor);       
                
                if (color != null)
                    g.setColor(color);      
                
                g.fillRect(x, y, cellSize, cellSize);   
            }
            
            if (image!= null) {
                int width = image.getWidth(null);
                int height = image.getHeight(null);
                
                if (width > height) {
                    int drawHeight = cellSize * height / width;
                    
                    g.drawImage(image, x, y + (cellSize - drawHeight) / 2,
                    	cellSize, drawHeight, null);
                } else {
                    int drawWidth = cellSize * width / height;
                    
                    g.drawImage(image, x + (cellSize - drawWidth) / 2, y, 
                    	drawWidth, cellSize, null);
                }
            }
            
        }
        
    }
    
    
}