import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.Random;
import java.util.ArrayList;

public class AScrollingGame extends GameCore {
    
    //---------------- Class Variables and Constants -----------------//
    
    // Constants for the various keyboard controls
    // use Java constant names for key presses
    // http://docs.oracle.com/javase/7/docs/api/constant-values.html#java.awt.event.KeyEvent.VK_DOWN   
    protected static final int KEY_SPEED_UP = KeyEvent.VK_9;
    protected static final int KEY_SLOW_DOWN = KeyEvent.VK_0;
    protected static final int KEY_RESET_SPEED = KeyEvent.VK_R;
    
    protected static final int KEY_DEBUG = KeyEvent.VK_D;
    
    protected static final int KEY_MOVE_DOWN = KeyEvent.VK_DOWN;
    
    // ADD more
    
    // if needed use the following
    //   public static final int[] MOVE_KEYS = { KEY_MOVE_DOWN, 
    //                                         ... };
    
    // Default row location of player at beginning of the game
    private static final int DEFAULT_PLAYER_ROW = 4;
    
    
    public static final int KEY_SCREENSHOT = KeyEvent.VK_S;
    
    
    private static final String INTRO_SCREEN = "ink.png";
    
    protected static String PLAYER_IMG = "user.gif";    // specify user image file
    
    // ADD others for Avoid/Get items 
    // USE ArrayList when you have many similar items
    
    // use if you are interested in click interation
    protected Location clickCoord;
    
    // make sure to update it
    protected Location playerCoord;
    
    protected int score;
    protected int hits;
    protected boolean gameOver = false;
    protected boolean debug = false;
    
  
    // ADD constructor(s) if needed
    
    public AScrollingGame(int grid_h, int grid_w){
        this(grid_h, grid_w, DEFAULT_TIMER_DELAY);
    }
    
    public AScrollingGame(int hdim, int wdim, int init_delay_ms) {
        super(hdim, wdim, init_delay_ms);        
    }
    
    
    // Perform the tasks of beginning the game
    //   - display still screen (until "Enter")
    //   - reset game params for game start
    protected void startGame(){
        updateTitle("Welcome to my game...");  
        displayStillScreen(INTRO_SCREEN);
        
        // Consider below instead if you want multiple still screen
        /*
        ArrayList<String> splashImages = new ArrayList<String>();
        splashImages.add(INTRO_SCREEN);    
        splashImages.add(..); // another intro screen...
        
        for (String screen: splashImages)
        displayStillScreen(screen);
        */
        
        resetGamePlayParam();
    }
    
    
    
     protected void resetGamePlayParam() {
        score = 0; 
        hits = 0;
        updateTitle("Scrolling Game --- SCORE " + score + " ;hits " + hits);
        
        // store and initialize user position
        playerCoord = new Location(DEFAULT_PLAYER_ROW, 0);
        setGridImage(playerCoord, PLAYER_IMG);
        
        // Try the lines below
        setGridColor(playerCoord, Color.PINK);
        
        debug = true;
        displayGridLines();
        System.out.println("debug mode" + debug + " grid lines shown");

    }
    
    //Call methods that check for user input
    //   key press and mouse click
    protected int performGameUpdates() {
        clickCoord = handleMouseClick();
        if (clickCoord != null)
            System.out.println("Mouse clicked at : " + clickCoord);
        
        return handleKeyPress();
    }
    
    
    /****************** Methods to Implement Part I ******************/

    //Call methods that modify assets at each "render ticks"
    //Some assets move each "render ticks", new ones are created
    protected void performRenderUpdates(){
         // To be completed    
        
    }
    
    
    //Update game state with new assets 
    // such as adding in A/G images in the right-most column
    protected void populate() {
        
    }
    
    //Update game state with motions
    // such as scrolling items from left to right by one column
    protected void scroll() {
        
    }
    
    //Check for collision between user and assets
    protected void handleCollision() {
        
        
    }
    
    
    //ADD helpers methods as you need them
    
    
    
    
    /****************** Methods to Complete Part I ******************/
    
    //Handle game player key press for game play
    protected int handleKeyPress() {
        
        int key = super.handleKeyPress(); // delegate to parent window level keys
        
        if (key != GameGrid.NO_KEY) {
            System.out.println("A key has been pressed ");
        }
        
        if (key == KeyEvent.VK_S) {
            System.out.println("could save the screen: add the call");
            
            // move these lines (not in the correct conditional)
            debug = !debug;
            hideGridLines();
        }
        
        return key;
    }
    
    //contains all of the tasks that need to be done each time a game ends
    protected void endGame(){
    	//potentially display screen as in startGame
    	
    	//minimum
        //updateTitle("GAME OVER ...
    }
    
    
    // return true if the game is finished, false otherwise
    protected boolean isGameOver() {
        return gameOver;
    }
    
  
    

    
}
