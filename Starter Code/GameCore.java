import java.util.Random;
import java.awt.event.KeyEvent;
import java.awt.Color;


//public abstract class AbstractGame {
public abstract class GameCore {

	//---------------- Class Variables and Constants -----------------//
    protected static final int KEY_QUIT_GAME = KeyEvent.VK_Q;
    protected static final int KEY_PAUSE_GAME = KeyEvent.VK_P;    
    
    
    //the keyboard key used to advance past a still screen
    protected static int KEY_DEFAULT_ADVANCING_SCREEN = KeyEvent.VK_ENTER;
    
    // default number of vertical/horizontal cells: height/width of grid
    protected static final int DEFAULT_GRID_H = 5;
    protected static final int DEFAULT_GRID_W = 10;
    
    
    //The initial timer delay -- the number of milliseconds between each
    //"tick" in the game loop.  See gameLoop() and sleep() for more info.
    protected static final int DEFAULT_TIMER_DELAY = 150;
    protected static final int MIN_TIMER_DELAY = 20;
    
    private static final int FACTOR = 3;

    
    //USE THIS! Don't create additional Random objects, just reuse this one!
    protected static final Random DICE = new Random();   
    
    //---------------- Instance Variables -----------------// 
    
    //Keep track number of "ticks" since gameLoop() started 
    private int ticksElapsed;  
    
    //Control game speed (ms between game "ticks"): permit to 
    //speed up/slow down the game 
    private int currentTimerDelay;  
    //Retain a default speed in case of reset 
    private int defaultTimerDelay;
    
    //Determines if the game is paused or not
    protected boolean isPaused;
    
    private boolean stillScreen;
    //Set the key to advance still screen
    private int defaultStillScreenKey;
    
    //Grid for the game play "board" (Java window of the game, which main
    //content is a matrix of cells that is rendered on the screen)
    private GameGrid grid;
   
    //---------------- Constructor -----------------//
    
    public GameCore(int hdim, int wdim) {
    	this(hdim, wdim, DEFAULT_TIMER_DELAY);
    }
    
    //Initializes general game properties
    //hdim/wdim: determines number of rows/columns on board, respectively
    //timerDelay: determines initial speed of game -- time between render "ticks" (see gameLoo())
    public GameCore(int hdim, int wdim, int timerDelay) {
        
    	//allocate the memory for the grid
        this.grid = new GameGrid(hdim, wdim);
        init(timerDelay);
    }    
    
    private void init(int timerDelay){	
    	//Initialize attributes and counters
        this.currentTimerDelay = timerDelay;
        this.defaultTimerDelay = timerDelay;
        this.ticksElapsed = 0;
        
        this.stillScreen = false;
        this.isPaused = false;
        
        defaultStillScreenKey = KEY_DEFAULT_ADVANCING_SCREEN;
    }
    
    
    //----------------------- Instance Methods --------------------------//
    
    //Runs the game, including pre and post game tasks
    public void run(){     
        startGame(); 
        gameLoop();  
        endGame();   
    }
    
    
    //-------------------- Reset & Timing Methods ------------------------//
    
    protected void slowDown(int msDelay){
    	currentTimerDelay += msDelay;
    }
    
    protected void speedUp(int msDelay){
    	System.out.println("BEFORE delay " + currentTimerDelay + " " + msDelay);

    	currentTimerDelay = Math.max( 
    		currentTimerDelay - msDelay, MIN_TIMER_DELAY);
    	System.out.println("delay " + currentTimerDelay);
    }
    
    protected void resetSpeed() {
    	currentTimerDelay = defaultTimerDelay;
    }
    
    //-------------------- Display/Hide Game Methods ---------------------//
    
    protected void displayStillScreen(String screen){
    	runStillScreen(screen);
    }  

    protected void displayStillScreen(String screen, int advanceKey){
    	defaultStillScreenKey = advanceKey;
    	runStillScreen(screen);
    }
    
    // Turn on grid lines (useful for debugging)
    protected void displayGridLines(){
        grid.setLineColor(Color.RED); 
    }
    
    // Turn off grid lines 
    protected void hideGridLines(){
    	grid.setLineColor(null); 
    }
        
    // Display the provided img as the background during the game play
    protected void displayGameBackground(String img){
        grid.setGridBackgroundImage(img);        
    }
    
    // Remove the background of the game play
    protected void hideGameBackground(){
        grid.setGridBackgroundImage(null);        
    }
    
    // Display the provided color as the background during the game play 
    //    null for no color (default BLACK used)
    protected void setGameBackgroundColor(Color color){
        grid.setGridBackgroundColor(color);    
    }
    
        // Take a screenshot of the content of the GameGrid
    protected void takeScreenShot(String fileName){
        grid.save(fileName);        
    }
    
    // Update the title bar of the game window 
    protected void updateTitle(String title) {
        grid.setTitle(title);
    }
    
    //--------------------- Grid Methods acting on a cell --------------------
    // --------------------- at a particular location ------------------------
    // -----      Necessary for the children game Child logic/brain ----------
    
    
    // Set at the passed location the argument string Image
    // blank if null passed
    protected void setGridImage(Location loc, String imgName) {
    	grid.setCellImage(loc, imgName);
    }
    
    
    // Return the name of the image stored at the location
    // null for empty
    protected String getGridImage(Location loc) {
    	return grid.getCellImage(loc);
    }
    
    // Move the content: "from" --> "to" Locations 
    // image, and color, and null if the "from" location is blank 
    // returns the image previously stored at the "to" location
    protected String moveGridImage(Location from, Location to) {
    	String img = getGridImage(from);
    	String eraseImg = getGridImage(to);
    	Color color = getGridColor(from);	
    	setGridImage(from, null);
    	setGridColor(from, null);
    	setGridImage(to, img);
    	setGridColor(to, color);
    	return eraseImg;
    }
    
    // Return the total number of rows of the game board
    protected int getTotalGridRows() {
    	return grid.getNumRows();
    }
    
    // Return the total number of columns of the game board
    protected int getTotalGridCols() {
    	return grid.getNumCols();
    }
    
    
    // To consider if you wanted a background color at a particular location
    protected void setGridColor(Location loc, Color color) {
    	grid.setColor(loc, color);
    }
    
    protected Color getGridColor(Location loc) {
    	return grid.getColor(loc);
    }
    
    //-------------------- User Interface Methods ------------------------//
    
    // Check KEY INPUT from the user at the window level, such as
    // Exit, pause or advance still screen
    // Return the pressed key code or GameGrid.NO_KEY if no key is pressed 
    protected int handleKeyPress() {
    	int key = grid.checkLastKeyPressed();
    	//System.out.println("abstract game key pressed");
    	
    	if (key == KEY_QUIT_GAME)
            System.exit(0);
        else if (key == KEY_PAUSE_GAME)
            isPaused = !isPaused;  
        
        else if (key == defaultStillScreenKey)
        	stillScreen = false;
        
        return key;
    }
    
    // Check for MOUSE CLICK in game window
    // Return the Location of the GameGrid in which the cursor click occured;
    //         or null if mouse isn't clicked 
    protected Location handleMouseClick() {
        
        Location loc = grid.checkLastLocationClicked();
        
        if (loc != null) 
            System.out.println("You clicked on a square " + loc);
        
        return loc;
    }
    

    
    
    
    //-------------------------- Abstract methods ---------------------------//
    //                    (to be implemented in the child!)
    
    //checks to see if the game is over
    protected abstract boolean isGameOver();
    //handles all of the tasks done on each tick
    protected abstract int performGameUpdates();
    //handles all of the tasks done only on each "render tick"
    protected abstract void performRenderUpdates();
    
    //contains all of the tasks that need to be done when game starts/ends
    protected abstract void startGame();
    protected abstract void endGame();
    
    
    //---------------------- PRIVATE helper methods -----------------------//
    
    // Display a still screen until the defaultStillScreenKey is pressed
    // Similar while structure with sleep than in gameLoop()
    // sleep is required to not consume all the CPU; going too fast freezes app 
    private void runStillScreen(String screen){
    	
    	showStillScreen(screen);
    	stillScreen = true;
    	
    	while (stillScreen) {
    		this.sleep(currentTimerDelay);
    		// Listen to keep press to break out of intro 
    		// as of now --> ENTER necessary
    		handleKeyPress();
    	}
    	
    	System.out.println("done with a still screen");
    	hideStillScreen();
    }
    
    // Display and manage the actual game
    //  - update and render the game until the game is over
    //  - note that rendering (animation) occurs less frequently than
    //    game player update are taken into account
    //       player's input is checked twice more often than animation occurs
    private void gameLoop() {
        // Loop until the game is over: each iteration is a game "tick"
        System.out.println("gameLoop");
        
        while (!isGameOver()){            
            this.sleep(currentTimerDelay); 
            
            // Player's input is checked every tick and even when game is paused
            performGameUpdates();
            if (!isPaused) {
            	// Game is rendered/animated only half the time (check FACTOR)
            	if (isRenderTick())
            		performRenderUpdates();
            	
                ticksElapsed++;
            } 
        }   
    }
    
    // Determines if the current tick is a "render tick" or not...
    private boolean isRenderTick(){
        return (ticksElapsed % FACTOR == 0);
    }
    
    // Pauses the execution of the code for the argument number of milliseconds
    // Essential to not consumed all the CPU bandwidth (other applications need to run)
    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } 
        catch(Exception e) { 
            //shouldn't ever reach here, but try/catch is necessary due to 
            //Java's implementation of Thread.sleep function
        }
    }
    
      
    private void showStillScreen(String img){
    	grid.setSplashScreen(img);
    }
    
    
    private void hideStillScreen(){
        grid.setSplashScreen(null); 
    }
    
    
}
