class Item extends Entity {
    
	protected static String[] SPRITES_ITEM = {"itemUpgrade.png", "itemHeal.png"};

    public Item(int r, int c, int typeChoice) {
        super(r, c, typeChoice);
        setSprite(SPRITES_ITEM[getType()]);
    }
    
    public Item(Location loc, int typeChoice) {
        this(loc.getRow(), loc.getCol(), typeChoice);
    }
    
}