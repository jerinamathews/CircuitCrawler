package com.lds.game.entity;

import javax.microedition.khronos.opengles.GL11;

import com.lds.EntityManager;
import com.lds.math.Vector2;
import com.lds.physics.Circle;

public abstract class Pickup extends Entity //pickup objects are picked up, such as keys, powerups, or batteries
{	
    private int value;
    
	public Pickup(Vector2 position, int value)
	{
	    super(new Circle(20, position, false));
	    this.value = value;
	}
	
	@Override
	public void update(GL11 gl)
	{
		//TODO scaling
	}
	
	@Override
	public void interact(Entity ent)
	{
		if (ent instanceof Player)
		{
			EntityManager.removeEntity(this);
		}
	}
	
	public int getValue()
    {
        return value;
    }
    
    public void setEvergyValue(int value)
    {
        this.value = value;
    }
}
