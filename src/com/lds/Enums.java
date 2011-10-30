package com.lds;

/**
 * A static helper class that stores all the enums.
 * \todo all these enums should be moved into their respective classes.
 * @author Lightning Development Studios
 */
public final class Enums 
{
    
	public static enum Direction 
	{
	    LEFT,
	    RIGHT,
	    UP,
	    DOWN
	}
	
	public static enum DiagDir
	{
	    TOPLEFT,
	    TOPRIGHT,
	    BOTTOMLEFT,
	    BOTTOMRIGHT
	}
	
	public static enum RenderMode
	{
	    COLOR,
	    GRADIENT,
	    TEXTURE,
	    TILESET
	}
	
	public static enum TileState
	{
	    FLOOR,
	    WALL,
	    PIT,
	    BRIDGE,
	    SlipperyTile 
	}
	
	public static enum AIType
	{
	    STALKER,
	    PATROL,
	    TURRET
	}
	
	public static enum UIPosition
	{
	    TOP(0),
	    LEFT(1),
	    BOTTOM(2),
	    RIGHT(3),
	    CENTER(4),
	    TOPLEFT(5),
	    TOPRIGHT(6),
	    BOTTOMLEFT(7),
	    BOTTOMRIGHT(8);
	    
		private int value;
		
		private UIPosition(int value)
		{
		    this.value = value;
		}
		
		public int getValue()
		{
		    return value;
		}
	  }
	
	public static enum GateType
	{
	    NONE,
	    AND,
	    OR,
	    XOR,
	    NOT,
	    NAND,
	    NOR,
	    XNOR
	}
	
	private Enums() 
	{

	}
}


