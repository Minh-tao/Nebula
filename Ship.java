class Ship extends Entity { // Represents enemy and player ships
    
	protected static String[] SPRITES_SHIP = {"player.png", "enemyDummy.png", "enemyBasic.png", "enemyKamikaze.png", "enemyTank.png", "enemyElite.png"};
	private static final int PLAYER = 0;
	private static final int ENEMY_DUMMY = 1;
	private static final int ENEMY_BASIC = 2;
	private static final int ENEMY_KAMIKAZE = 3;
	private static final int ENEMY_TANK = 4;	
	private static final int ENEMY_ELITE = 5;	
	private static final int DEFAULT_PLAYER_HP = 10; // Player's initial health
	
	private int projType;
	private int health;
    private int score; // Number of points awarded when destroying an enemy
    private int itemDropChance; // % chance for an enemy to drop an item on death
    private int fireRate; // % chance for an enemy to attack each "entity render tick" 
    private boolean canAttack;
    private boolean isEnemy;
       
    public Ship(int r, int c, int typeChoice) {
        super(r, c, typeChoice);
        setSprite(SPRITES_SHIP[getType()]);    
        if (getType() == PLAYER) { // creates the player's ship
        		health = DEFAULT_PLAYER_HP;   
        		isEnemy = false;           		
        	} else if (getType() == ENEMY_DUMMY) {
        		health = 1;
        		setSpeed(1);
        		score = 50;   
        		itemDropChance = 0;
        		canAttack = false;
        		isEnemy = true;     		      	
        	} else if (getType() == ENEMY_BASIC) {
        		health = 1;
        		setSpeed(1);
        		score = 75; 
        		itemDropChance = 5;
        		fireRate = 20;
        		projType = 0;
        		canAttack = true;
        		isEnemy = true;        		
        	} else if (getType() == ENEMY_KAMIKAZE) {
        		health = 2;
        		setSpeed(2);
        		score = 125; 
        		itemDropChance = 5;
        		canAttack = false;
        		isEnemy = true;
        	} else if (getType() == ENEMY_TANK) {
        		health = 4;
        		setSpeed(1);
        		score = 300; 
        		itemDropChance = 15;
        		fireRate = 5;
        		projType = 1;
        		canAttack = true;
       		isEnemy = true;        		
        	} else if (getType() == ENEMY_ELITE) {
        		health = 2;
        		setSpeed(2);
        		score = 500; 
        		itemDropChance = 20;
        		fireRate = 30;
        		projType = 2;
        		canAttack = true;
        		isEnemy = true;      		
        	}
    }
    
    // Creates a "clone" of the argument ship, which retains all of its fields
    // Used when moving ships to new locations
    
    public Ship(Ship clone) { 
        this(clone.getRow(), clone.getCol(), clone.getType());
        health = clone.getHealth();
        projType = clone.getProjType();
    } 
 
    public Ship(Location loc, int typeChoice) {
        this(loc.getRow(), loc.getCol(), typeChoice);
    } 
    
  
    public boolean canAttack() { return canAttack; }   
    public boolean isEnemy() { return isEnemy; }
    public boolean isDead() { return health <= 0; }    
    public int getProjType() { return projType; }
    public int getDropChance() { return itemDropChance; }
    public int getHealth() { return health; }
    public int getScore() { return score; }
    public int getFireRate() { return fireRate; }
    
    public void upgradeProjType() { 
    		if (projType < 2) {
    			projType++; 
    		}
    	} 		
    	public void setHealth(int hp) { health = hp; }
    public void hurt(int damage) { health -= damage; }
    public void heal(int amount) { health += amount; }
    
}