class Projectile extends Entity {
    
	protected static String[] SPRITES_PROJ_PLAYER = {"projPlayer0.png", "projPlayer1.png", "projPlayer2.png"};
	protected static String[] SPRITES_PROJ_ENEMY = {"projEnemy0.png", "projEnemy1.png", "projEnemy2.png"};
	private static final int[] LEVEL = {0, 1, 2};
	
    private int damage;
    private boolean isEnemy;
	
	
    public Projectile(int r, int c, int typeChoice, boolean isHostile) {
        super(r, c, typeChoice);      
        isEnemy = isHostile;
        	if (!isEnemy) { 
        		setSprite(SPRITES_PROJ_PLAYER[getType()]); 
        		if (getType() == LEVEL[0]) {
        			damage = 1;
        			setSpeed(1);
        		} else if (getType() == LEVEL[1]) {
        			damage = 1;
        			setSpeed(2);
        		}  else if (getType() == LEVEL[2]) {
        			damage = 1;
        			setSpeed(3);
       	 	} 
       	} else { 
       		setSprite(SPRITES_PROJ_ENEMY[getType()]); 	      	
        		if (getType() == LEVEL[0]) {
        			damage = 1;
        			setSpeed(1);
        		} else if (getType() == LEVEL[1]) {
        			damage = 2;
        			setSpeed(1);
        		}  else if (getType() == LEVEL[2]) {
        			damage = 1;
        			setSpeed(2);
        		}
        	}       
    }
    
    public Projectile(Location loc, int typeChoice, boolean isHostile) {
        this(loc.getRow(), loc.getCol(), typeChoice, isHostile);
    }
    
    // Creates a projectile based on data from the argument ship
    public Projectile(Ship attackingShip) {
        this(attackingShip.getRow(), attackingShip.getCol(), attackingShip.getProjType(), attackingShip.isEnemy());
    }
    
    // Creates a "clone" of the argument projectile, which retains all of its fields
    public Projectile(Projectile clone) {
        this(clone.getRow(), clone.getCol(), clone.getType(), clone.isEnemy());
        damage = clone.getDamage();
        isEnemy = clone.isEnemy();
    } 
    
    public int getDamage() { return damage; }
    public boolean isEnemy() { return isEnemy; }
    
}