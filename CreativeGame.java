import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.*;

public class CreativeGame extends GameCore {
    
    //---------------- Class Variables and Constants -----------------//
    
    // Constants for the various keyboard controls
    // use Java constant names for key presses
    // http://docs.oracle.com/javase/7/docs/api/constant-values.html#java.awt.event.KeyEvent.VK_DOWN   
    protected static final int KEY_SPEED_UP = KeyEvent.VK_9;
    protected static final int KEY_SLOW_DOWN = KeyEvent.VK_0;
    protected static final int KEY_RESET_SPEED = KeyEvent.VK_R;
    protected static final int KEY_SCREENSHOT = KeyEvent.VK_S;    
    protected static final int KEY_DEBUG = KeyEvent.VK_D;   
    protected static final int KEY_MOVE_UP = KeyEvent.VK_UP;
    protected static final int KEY_MOVE_DOWN = KeyEvent.VK_DOWN;
    protected static final int KEY_MOVE_LEFT = KeyEvent.VK_LEFT;
    protected static final int KEY_MOVE_RIGHT = KeyEvent.VK_RIGHT;
    protected static final int KEY_ATTACK = KeyEvent.VK_SPACE;   
    public static final ArrayList<Integer> KEY_MOVE_LIST = new ArrayList<>(List.of(KEY_MOVE_UP, KEY_MOVE_DOWN, KEY_MOVE_LEFT, KEY_MOVE_RIGHT));
    
    private static final int FACTOR_ENTITY_DEFAULT = 18; // Default game speed
    private static final int[] SCORE_THRESHOLDS = {1000, 2000, 3000, 5000, 7500};
    private static final int SHIP_PLAYER = 0; // Used in Ship class's constructor to create the player's ship   
    private static final int SPEED_MODIFIER = 3; // How much the game's speed changes per speedUp() or slowDown() call
    
    // Used in (DICE.nextInt(PERCENT)) < chance) calls to simulate percentage odds for some event to happen (such as item spawns) 
    // chance = % likelihood for some event to happen
    private static final int PERCENT = 100;  
    
    private static final String DEFAULT_SCREENSHOT_OUTPUT = "output.jpg";
    private static final String BACKGROUND = "background.jpg";     
    private static final String INTRO_SCREEN_1 = "intro1.jpg";  
    private static final String INTRO_SCREEN_2 = "intro2.jpg";
    private static final String INTRO_SCREEN_3 = "intro3.jpg";
    
    private static final String VICTORY_SCREEN = "victory.jpg";
    private static final String DEFEAT_SCREEN = "defeat.jpg";
        
    private static final int ITEM_UPGRADE = 0;
    private static final int ITEM_HEAL = 1;
    private static final int ITEM_TYPES = 2;   
    private static final int ITEM_HEAL_AMOUNT = 5;

    protected ArrayList<Entity> entityList = new ArrayList<>(); // Stores items and enemy ships
    protected ArrayList<Projectile> projList = new ArrayList<>(); // Stores projectiles fired
    
    // use if you are interested in click interation
    protected Location clickCoord;
    
    protected Ship playerShip;
    
    protected int difficulty = 0; 
    protected int minShipIdx = 1; 
    protected int maxShipIdx = 2;
    protected int spawnLimit = 3; // Max number of entities spawned per populate() call
    protected int itemSpawnChance = 5; // % chance to spawn an item (in place of an enemy), used in populate()     
   
    protected int score;
    protected int playerHP;
    protected int playerMaxHP;   
    protected boolean gameOver = false;
    protected boolean debug = false;
  
    // ADD constructor(s) if needed
    
    public CreativeGame(int grid_h, int grid_w){
        this(grid_h, grid_w, FACTOR_ENTITY_DEFAULT);
    }
    
    public CreativeGame(int hdim, int wdim, int init_delay_ms) {
        super(hdim, wdim, init_delay_ms);        
    }
        
    // Perform the tasks of beginning the game
    //   - display still screen (until "Enter")
    //   - reset game params for game start
    protected void startGame() {
        updateTitle("NEBULA - A JAVA SPACE SHOOTER");  

        ArrayList<String> splashImages = new ArrayList<String>();
        splashImages.add(INTRO_SCREEN_1);    
        splashImages.add(INTRO_SCREEN_2); 
        splashImages.add(INTRO_SCREEN_3);        
        
        for (String screen: splashImages)
        displayStillScreen(screen);    
        
        resetGamePlayParam();
    }
 
     protected void resetGamePlayParam() {
        score = 0;       
        // store and initialize user position
        playerShip = new Ship(getTotalGridRows() - 1, getTotalGridCols() / 2, SHIP_PLAYER);
        playerHP = playerShip.getHealth();
        playerMaxHP = playerShip.getHealth();
        
        setGridImage(playerShip, playerShip.getSprite());    
        updateTitle("SCORE: " + score + " | HEALTH: " + playerHP + "/" + playerMaxHP);        
        displayGameBackground(BACKGROUND);
     
        // Try the lines below
        
        //displayGridLines();
        System.out.println("debug mode" + debug);

    }
    
    //Call methods that check for user input
    //   key press and mouse click
    protected int performGameUpdates() {
        clickCoord = handleMouseClick();
        if (clickCoord != null)
            System.out.println("Mouse clicked at : " + clickCoord);
        
        return handleKeyPress();
    }
    

    // Call methods that modify assets during each "render tick"
    // Only updates projectile/player movement and calls methods to handle/check for collisions
    protected void performRenderUpdates() { 
  	 	scrollProj();
        checkAllProjCollision();
        checkAllPlayerCollision();
        setGridImage(playerShip, playerShip.getSprite());
    }
    
    // Call methods that modify assets during each "entity render tick"
    // Updates entity (enemy ships and items) movement, creates new entities
    protected void performEntityUpdates() { 
        scrollEntities(); 
        checkAllProjCollision();
        enemyAttack();
        checkAllProjCollision();
        populate();
        checkAllPlayerCollision();
    }

    
    // Spawns a random number of entities at the top row of the grid
    protected void populate() {  		
    		Location spawnLoc;
    		int spawnCount = DICE.nextInt(spawnLimit); // random number of get/avoid entities created per populate() call
    		for (int i = 0; i < spawnCount; i++) {
    			spawnLoc = new Location(0, DICE.nextInt(getTotalGridCols()));   			
    			if (getGridImage(spawnLoc) == null) {	   				
    				if (DICE.nextInt(PERCENT) < itemSpawnChance) {
    					spawnItem(spawnLoc, PERCENT);	
    				} else {
    					spawnShip(spawnLoc);
    				}   			
    			}
    		}
    	}
   	// Has a % chance to spawn an item at the argument location; chance determined by the argument integer    
    protected void spawnItem(Location loc, int chance) {  
    		if (DICE.nextInt(PERCENT) < chance) { 
    			Item spawn = new Item(loc, DICE.nextInt(ITEM_TYPES));    			    			
    			entityList.add(spawn);
    			setGridImage(spawn, spawn.getSprite());
    		}    		
    }
    
    // Spawns an enemy ship at the argument location
    protected void spawnShip(Location loc) {    				
    		Ship spawn = new Ship(loc, DICE.nextInt(maxShipIdx) + minShipIdx);
    		entityList.add(spawn);
    		setGridImage(spawn, spawn.getSprite());   		
    }
    		  
    // Moves items and enemy ships on the grid
    protected void scrollEntities() { 
    		Ship oldShip; Ship newShip; Item oldItem; Item newItem; 
    		boolean isEntityIntact;
    		int i = 0;
    		while (i < entityList.size()) {  
    			isEntityIntact = true;
    			for (int j = 0; j < entityList.get(i).getSpeed(); j++) { // Entity's speed determines how many cells it moves each "entity render tick"
    				if (entityList.get(i) instanceof Ship) {   
    					oldShip = (Ship) entityList.get(i);
    					newShip = new Ship(oldShip);    					
    					newShip.set(oldShip.getRow() + 1, oldShip.getCol());
    					if (isLocValid(newShip)) {
    						if (isLocClear(newShip)) {
    							setGridImage(oldShip, null);
    							setGridImage(newShip, newShip.getSprite()); 
    							entityList.set(i, newShip);  
    							// Checks if ship has been destroyed by colliding with player/a projectile
    							if (checkEnemyPlayerCollision() || projDestroyedShip(i, newShip)) { 
    								isEntityIntact = false;  
    								break;   								
    							}   							
    						}
    					} else { 
    						removeEntity(i);
    						isEntityIntact = false;
    						break;
    					}   					
    				} else if (entityList.get(i) instanceof Item) {  
    					oldItem = (Item) entityList.get(i);
    					newItem = new Item(oldItem, oldItem.getType());
    					newItem.set(oldItem.getRow() + 1, oldItem.getCol());
    					if (isLocValid(newItem)) {
    						if (isLocClear(newItem)) {
    							setGridImage(oldItem, null);
    							setGridImage(newItem, newItem.getSprite()); 
    							entityList.set(i, newItem);  
    							if (checkItemPlayerCollision()) { 
    								isEntityIntact = false; 
    								break;
    							}
    						}
    					} else { 
    						removeEntity(i);  
    						isEntityIntact = false;
    						break;
    					}
    				}					   			
    			}
    			// isEntityIntact tracks an entity; if isEntityIntact is false, it has been destroyed (by colliding with player/projectile
    			// or going outside the grid) and removed from entityList
    			if (isEntityIntact) { i++; }
    		}
    	}
    	  	    
    	// Checks all enemies in entityList that can attack; has a chance to have them attack 
    protected void enemyAttack() { 
    		for (int i = 0; i < entityList.size(); i++) {   
    			if (entityList.get(i) instanceof Ship && ((Ship)entityList.get(i)).canAttack()) {  
     			Ship attacking = (Ship)entityList.get(i);
    				if (DICE.nextInt(PERCENT) < attacking.getFireRate()) { 
    					Projectile proj = new Projectile(attacking); 
    					proj.set(proj.getRow() + 1, proj.getCol());
    					if (isLocValid(proj) && getGridImage(proj) == null) {
    						setGridImage(proj, proj.getSprite());
    						projList.add(proj);					   			
    					}
    				}
    			}
    		}
    	}
       
    // Creates a projectile in front of player; allows the player to attack
    protected void playerAttack() {    	
    		Projectile proj = new Projectile(playerShip); 
    		proj.set(proj.getRow() - 1, proj.getCol());  		
    		if (isLocValid(proj) && getGridImage(proj) == null) {
    			setGridImage(proj, proj.getSprite());
    			projList.add(proj);
    		} 
    }
    	
    	// Moves active projectiles on the grid
    	protected void scrollProj() {
    		Projectile oldProj; Projectile newProj; int i = 0; boolean isProjIntact;
    		while (i < projList.size()) {
    			isProjIntact = true;
    			for (int j = 0; j < projList.get(i).getSpeed(); j++) {
    				oldProj = projList.get(i);    				
    				newProj = new Projectile(oldProj);      				
    				if (newProj.isEnemy()) { newProj.set(oldProj.getRow() + 1, oldProj.getCol()); }
    				else { newProj.set(oldProj.getRow() - 1, oldProj.getCol()); }   				
    				setGridImage(oldProj, null);
    				if (isLocValid(newProj)) {    	
    					projList.set(i, newProj);  				  
    				} else {
    					projList.remove(i);
    					isProjIntact = false;
    					break;
    				}		
    				if (projCollided(i, newProj)) { 
    					isProjIntact = false;
    					break;
    				}
    				if (getGridImage(newProj) == null) {
    					setGridImage(newProj, newProj.getSprite());
    				}
    			}
    			if (isProjIntact) { 
    				i++; 
    			}    			
    		}
    	}
    	
    	// Checks if projectile at the argument index in projList has collided with a ship at the argument location   	
    	protected boolean projCollided(int projIdx, Projectile proj) {  
    		int shipIdx = shipOverlap(proj);      		
    		if (shipIdx == -1) { return false; }	
    		if (shipIdx == -2 && proj.isEnemy()) { // if projectile hit player
    			projCollidedWithPlayer(projIdx, proj);
    			return true;
    		} 		
    		if (isSameSide((Ship)entityList.get(shipIdx), proj)) { // if the colliding ship and projectile are of the same side
    			return false; 
    		}     		 		 		    				
    		projCollidedWithEnemy(shipIdx, projIdx, proj);
    		return true;   		
    }
    
    // Handles a projectile colliding with player
    protected void projCollidedWithPlayer(int projIdx, Projectile proj) {
    		playerShip.hurt(proj.getDamage()); // Reduces the player's health depending on the projectile's damage
    		playerHP = playerShip.getHealth();
    		projList.remove(projIdx);   
    		updateGameState();
    		setGridImage(playerShip, playerShip.getSprite());  
    	}
    	
    	// Handles a projectile colliding with enemy
    	protected void projCollidedWithEnemy(int shipIdx, int projIdx, Projectile proj) {
    		((Ship)entityList.get(shipIdx)).hurt(proj.getDamage()); 
    		Ship hit = (Ship)entityList.get(shipIdx);    
	  	projList.remove(projIdx);  		  	
    		if (hit.isDead()) {    		
    			spawnItem(hit, hit.getDropChance());    			
    			score += hit.getScore();
    			removeEntity(shipIdx);
    			updateGameState();
    			return;
    		}   		
    		setGridImage(hit, hit.getSprite());
    	}
    
    // Helper method for scrollEntity, used to handle enemy ships being destroyed by projectiles mid-flight
    // Checks if enemy ship at the argument index in entityList has collided with a projectile at the argument location
    	// Returns true and removes the ship from entityList and the grid if it is destroyed
    
    protected boolean projDestroyedShip(int shipIdx, Ship hit) {  
    		int projIdx = projOverlap(hit);
    		if (projIdx == -1) { return false; }
    		if (isSameSide(hit, projList.get(projIdx))) { return false; }
    		((Ship)entityList.get(shipIdx)).hurt(projList.get(projIdx).getDamage()); 	
    		hit = (Ship)entityList.get(shipIdx);    
	  	projList.remove(projIdx);  	
    		if (hit.isDead()) {    		
    			spawnItem(hit, hit.getDropChance());
    			score += hit.getScore();
    			removeEntity(shipIdx);
    			updateGameState();
    			return true;
    		}   		
    		return false;   		
    } 

    // Checks if an enemy has collided with player; if yes, returns true and removes the enemy from grid and entityList
    protected boolean checkEnemyPlayerCollision() {  
    		int shipIdx = shipOverlap(playerShip);
        if (shipIdx <= -1) { return false; }         
        playerShip.hurt(((Ship)(entityList.get(shipIdx))).getHealth()); // Player is damaged depending on how much health the enemy had when colliding 
        playerHP = playerShip.getHealth();
        	entityList.remove(shipIdx);   
        	updateGameState();
        setGridImage(playerShip, playerShip.getSprite());  
        return true;
    }    
	
    // Checks if an item has collided with player; if yes, removes the item from grid and entityList
    protected boolean checkItemPlayerCollision() {  
    		int itemIdx = itemOverlap(playerShip);
        if (itemIdx == -1) { return false; }  
        	processItem((Item)entityList.get(itemIdx));
        	entityList.remove(itemIdx);   
        	updateGameState();
        setGridImage(playerShip, playerShip.getSprite());  
        return true;
    }
    
    // Apply the argument item's effects
    protected void processItem(Item pickup) {     	 
  		int itemType = pickup.getType();
    		if (itemType == ITEM_UPGRADE) {
    			playerShip.upgradeProjType();
    		} else if (itemType == ITEM_HEAL) {
    			playerShip.heal(ITEM_HEAL_AMOUNT);
    			if (playerShip.getHealth() > playerMaxHP) { playerShip.setHealth(playerMaxHP); }
    			playerHP = playerShip.getHealth();
    			updateGameState();		
    		} 
    }
    
    // Updates the game's title and ends the game if win/lose conditions are met
    // Modifies game parameters to adjust difficulty once certain score thresholds are reached
    protected void updateGameState() {
    		updateTitle("SCORE: " + score + " | HEALTH: " + playerHP + "/" + playerMaxHP);
    		if (score >= SCORE_THRESHOLDS[4] || playerHP <= 0) { gameOver = true; 
    		} else if (score >= SCORE_THRESHOLDS[difficulty]) {
    			if (difficulty == 0) {
    				maxShipIdx++;
    			} else if (difficulty == 1) {
    				minShipIdx++; 		
    				spawnLimit++;
    				itemSpawnChance--;
    			} else if (difficulty == 2) {
    				maxShipIdx++;
    			} else if (difficulty == 3) {
    				minShipIdx++;
    				maxShipIdx--;
    			}   			
    			difficulty++;
    		}       	
    }
   
    //Handle game player key press for game play
    protected int handleKeyPress() {
        
        int key = super.handleKeyPress(); // delegate to parent window level keys
        Ship newLoc = null; // = null so java does not complain about "newLoc might not have been initialized"
        
        /*if (key != GameGrid.NO_KEY) {
            System.out.println("A key has been pressed ");
        }*/
        
        if (key == KEY_ATTACK) {
            playerAttack();
        }
        
        if (key == KEY_SCREENSHOT) {
            takeScreenShot(DEFAULT_SCREENSHOT_OUTPUT);
        }
        
        if (key == KEY_DEBUG) {            
            debug = !debug;
            if (debug) {
            		displayGridLines();
            	} else if (!debug) {
            		hideGridLines();
            	}
        }
        
        if (key == KEY_SLOW_DOWN) {
            slowDown(SPEED_MODIFIER);
        }
        
        if (key == KEY_SPEED_UP) {
            speedUp(SPEED_MODIFIER);
        }
        
        if (key == KEY_RESET_SPEED) {
            resetSpeed();
        }
        
        if (!isPaused) {
        		if (KEY_MOVE_LIST.contains(key)) {
        			newLoc = new Ship(playerShip);
        			if (key == KEY_MOVE_UP) {
        				newLoc.set(playerShip.getRow() - 1, playerShip.getCol());      				
        			} else if (key == KEY_MOVE_DOWN) {
        				newLoc.set(playerShip.getRow() + 1, playerShip.getCol());        				
        			} else if (key == KEY_MOVE_LEFT) {
        				newLoc.set(playerShip.getRow(), playerShip.getCol() - 1);        				
        			} else if (key == KEY_MOVE_RIGHT) {
        				newLoc.set(playerShip.getRow(), playerShip.getCol() + 1);      				
        			}
        			if (isLocValid(newLoc)) {     		
        				Ship oldLoc = playerShip;
        				playerShip = newLoc; 
        				checkAllPlayerCollision();
        				setGridImage(oldLoc, null);
        				setGridImage(newLoc, playerShip.getSprite());
        			}
        		}
        		
        }
        return key;
    }
    
        
    // Helper methods
        
    // Checks if a ship and a projectile are of the same side (player/enemy)
    protected boolean isSameSide(Ship hit, Projectile proj) {  
    		return hit.isEnemy() == proj.isEnemy();
    }
    
    	protected boolean checkAllPlayerCollision() {  
    		return checkEnemyPlayerCollision() && checkItemPlayerCollision();
    }

    // Calls projCollided() on all projectiles in projList
    protected void checkAllProjCollision() {  
    		int i = 0;   
    		while (i < projList.size()) {  
    			if (!projCollided(i, projList.get(i))) {
    				i++;
    			} 
        }
    }
    
    // Checks if argument location is in the grid
    	protected boolean isLocValid(Location loc) { 
    		int gridRow = getTotalGridRows();
    		int gridCol = getTotalGridCols();
    		return (loc.getRow() >= 0 && loc.getCol() >= 0 && loc.getRow() < gridRow && loc.getCol() < gridCol);
    }
    
    // Checks if argument location is unoccupied by an entity
    protected boolean isLocClear(Location loc) { 
    		return (entityOverlap(loc) == -1);
    }
    
    // Removes an entity from the grid and entityList 	
    	protected void removeEntity(int idx) {
    		setGridImage(entityList.get(idx), null);
    		entityList.remove(idx);
    	}
    
    	// Checks if argument location overlaps with the location of an entity, returns the entityList index of the entity, or -1 if no entity is found
    protected int entityOverlap(Location loc) { 
    		for (int i = 0; i < entityList.size(); i++) {			
    			if (loc.equals(entityList.get(i))) { 
    				return i; 
    			} 	  		
    		}
    		return -1;
    }
    
    // Checks if argument location overlaps with the location of a ship
    protected int shipOverlap(Location loc) { 
    		for (int i = 0; i < entityList.size(); i++) {
    			if (entityList.get(i) instanceof Ship) { 			
    				if (loc.equals(entityList.get(i))) { 
    					return i; 
    				}
    			}   		
    		}
    		return -1;
    }
   
    // Overloaded version of shipOverlap that only accepts projectiles as arguments
    // Same function as original, but also returns -2 if argument location overlaps with the location of a ship
    // Used as helper method for projCollided()
    protected int shipOverlap(Projectile loc) { 
    		for (int i = 0; i < entityList.size(); i++) {
    			if (entityList.get(i) instanceof Ship) { 			
    				if (loc.equals(entityList.get(i))) { 
    					return i; 
    				} else if (loc.equals(playerShip)) { 
    					return -2; 
    				}	
    			}   		
    		}
    		return -1;
    }

    // Checks if argument location overlaps with the location of an item
    protected int itemOverlap(Location loc) { 
    		for (int i = 0; i < entityList.size(); i++) {
    			if (entityList.get(i) instanceof Item) { 			
    				if (loc.equals(entityList.get(i))) { 
    					return i; 
    				}
    			}   		
    		}
    		return -1;
    }
    
    // Checks if argument location overlaps with the location of a projectile, returns the projList index of the projectile, or -1 if no projectile is found
     protected int projOverlap(Location loc) { 
    		for (int i = 0; i < projList.size(); i++) {
    			if (loc.equals(projList.get(i))) { 
    				return i; 
    			}   		
    		}
    		return -1;
    }
    
    //////
       
    //contains all of the tasks that need to be done each time a game ends
    protected void endGame() {
       	if (score >= SCORE_THRESHOLDS[4]) {
       		updateTitle("VICTORY!");
       		displayStillScreen(VICTORY_SCREEN);
        	} else if (playerHP <= 0) {
        		updateTitle("DEFEAT...");
        		displayStillScreen(DEFEAT_SCREEN);
        	}
    }
    
    
    // return true if the game is finished, false otherwise
    protected boolean isGameOver() {
        return gameOver;
    }

}
