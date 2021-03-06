package com.ltdev.cc.entity;

import com.ltdev.cc.SoundPlayer;
import com.ltdev.cc.physics.primitives.Circle;
import com.ltdev.math.Vector2;

import javax.microedition.khronos.opengles.GL11;

public class Button extends Entity
{
	private boolean active, prevActive;
	
	public Button(Vector2 position)
	{
		super(new Circle(69, position, false));
		
		this.tilesetX = 0;
		this.tilesetY = 0;
	}
	
	@Override
	public void update(GL11 gl)
	{
	    super.update(gl);
	    
	    //update the texture when activated/deactivated.
	    if (prevActive != active)
	    {
	        /*if (active)
	            this.setTile(gl, 1, 0);
	        else
	            this.setTile(gl, 0, 0);*/
	        
	        prevActive = active;
	    }
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public void activate()
	{
	    prevActive = active;
		active = true;
		SoundPlayer.playSound(SoundPlayer.SOUND_TEST);
	}
	
	public void deactivate()
	{
	    prevActive = active;
		active = false;
		SoundPlayer.playSound(SoundPlayer.SOUND_TEST);
	}
	
	@Override
	public void interact(Entity ent)
	{
	    super.interact(ent);
	    
		if (ent instanceof Player || ent instanceof HoldObject)
			activate();
	}
	
	@Override
	public void uninteract(Entity ent)
	{
		if (ent instanceof Player || ent instanceof HoldObject)
			deactivate();	
	}
}
