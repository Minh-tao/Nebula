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
    protected static final int DEFAULT_GRID_H = 20;
    protected static final int DEFAULT_GRID_W = 10;
    
    
    //The initial timer delay -- the number of milliseconds between each
    //"tick" in the game loop. The delay is now fixed and can't be changed by constructors or speedUp()/slowDown().
    //Changing the delay afects 
    protected static final int TIMER_DELAY = 20;
    
    // Controls how often render ticks happen, and how often projectiles move
    private static final int FACTOR = 3; 
    
    private static final int FACTOR_ENTITY_DEFAULT = 18;
    private static final int FACTOR_ENTITY_MIN = 3;
    private static final int FACTOR_ENTITY_MAX = 27;

    
    //USE THIS! Don't create additional Random objects, just reuse this one!
    protected static final Random DICE = new Random();   
    
    //---------------- Instance Variables -----------------// 
    
    //Keep track number of "ticks" since gameLoop() started 
    private int ticksElapsed;  
    
    // Controls how often "entity render ticks" happen, and how often entities move; determines game speed
    // currentSpeed is now a factor number (used in isEntityRenderTick())
    // instead of the number of milliseconds between each tick in the game loop
    private int currentSpeed; 
    
    //Retain a default speed in case of reset 
    private int defaultSpeed;  
    
    //Determines if the game is paused or not
    protected boolean isPaused;
    
    private boolean stillScreen;
    //Set the key to advance still screen
    private int defaultStillScreenKey;
    
    //Grid for the game play "board" (Java window of the game, which main
    //content is a matrix of cells that is rendered on the screen)
    private GameGrid grid;
   
    //---------------- Constructor -----------------//
    
    //Since game speed is now determined by a factor number instead of the number of milliseconds between each tick in the game loop,
    //constructors are changed to accept a factor number instead
    
    public GameCore(int hdim, int wdim) {
    	this(hdim, wdim, FACTOR_ENTITY_DEFAULT);
    }
    
    //Initializes general game properties
    //hdim/wdim: determines number of rows/columns on board, respectively
    //timerDelay: determines initial speed of game -- time between entity render "ticks" (see gameLoop())
    public GameCore(int hdim, int wdim, int entityFactor) {
        
    	//allocate the memory for the grid
        this.grid = new GameGrid(hdim, wdim);
        init(entityFactor);
    }    
    
    private void init(int entityFactor){	
    	//Initialize attributes and counters
    		this.currentSpeed = entityFactor;
        this.defaultSpeed = entityFactor;
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
    
    protected void slowDown(int factor) {
    	currentSpeed = Math.min(currentSpeed + factor, FACTOR_ENTITY_MAX);
    }
    
    protected void speedUp(int factor) {
    	currentSpeed = Math.max(currentSpeed - factor, FACTOR_ENTITY_MIN);
    	//System.out.println("delay " + currentSpeed);
    }
    
    protected void resetSpeed() {
    	currentSpeed = defaultSpeed;
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
    //handles all of the tasks done only on each "entity render tick"
    protected abstract void performEntityUpdates();
    
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
    		this.sleep(TIMER_DELAY);
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
            this.sleep(TIMER_DELAY); 
            
            // Player's input is checked every tick and even when game is paused
            performGameUpdates();
            if (!isPaused) {
            	// Game is rendered/animated only if the number of ticks elapsed is divisible by FACTOR
            	if (isRenderTick())
            		performRenderUpdates();
            		// Entities are rendered only if the number of ticks elapsed is divisible by currentSpeed
            		if (isEntityRenderTick())
            			performEntityUpdates();            	
                ticksElapsed++;
            } 
        }   
    }
    
    // Determines if the current tick is a "render tick" or not
    private boolean isRenderTick(){
        return (ticksElapsed % FACTOR == 0);
    }
    
    // Determines if the current tick is an "entity render tick" or not
    private boolean isEntityRenderTick(){
        return (ticksElapsed % currentSpeed == 0);
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
