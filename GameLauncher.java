public class GameLauncher{ 
    /**
    * Only CHANGE constant values as appropriate
    *
    * THERE SHOULD NOT B ANYTHING SUBSTANTIAL TO **ADD** TO THIS CLASS
    * 
    * The Scrolling Game is described the Project Handout. 
    *  
    * @author Elodie Fourquet 
    * @date October, 2020
    */
    
    private static final int BASE = 0;
    private static final int CREATIVE = 1;
    private static final int NUM_ROW = 27;
    private static final int NUM_COL = 9;
    
    private static final int RUNNING = CREATIVE;
    
    public static void main(String[] args) {
        GameCore game = null;
        
        switch (RUNNING) {
        case BASE:
            // Construct sized version of the base game you've written
            //game = new AScrollingGame(NUM_ROW, NUM_COL);
            // Test me too (and more)
           
            //System.out.println("From GameLauncher main: Running the BaseGame version");
            break;
        case CREATIVE:
            // Construct sized version of the creative game you've written
            game = new CreativeGame(NUM_ROW, NUM_COL);
            System.out.println("From GameLauncher main: Running the CreativeGame version ");
            break;
        default:
            System.out.println("Not sure which version you meant...");
            
        }
        
        // Launch the instantiated game version
        // Make sure to trace the run method in GameCore
        if (game != null)
            game.run();
        else 
            System.out.println("Check that a Game is instantiated in GameLauncher");
        
    }
    
}
