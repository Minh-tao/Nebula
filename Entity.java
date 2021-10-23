// Represents either an item or a ship

class Entity extends Location { 
    	
	private String sprite;
	private int type;
	private int speed; // Determines how many cells the entity moves each "entity render tick"
        
    public Entity(int r, int c, int typeChoice) {
        super(r, c);
        type = typeChoice;  
        speed = 1;
    }
 
    public Entity(Location loc, int typeChoice) {
        this(loc.getRow(), loc.getCol(), typeChoice);
    }
    
    
    public String getSprite() { return sprite; }
    public int getType() { return type; }
    public int getSpeed() { return speed; }
    public void setSprite(String spr) { sprite = spr; }         
    public void setType(int typeChoice) { typeChoice = type; }
    public void setSpeed(int speedInput) { speed = speedInput; }
}