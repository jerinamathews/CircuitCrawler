package com.lds.game.entity;

import com.lds.EntityCleaner;
import com.lds.Enums.RenderMode;
import com.lds.game.Inventory;
import com.lds.Vector2f;

public class Player extends Character //your character, protagonist
{
	private int energy;
	private boolean holdingObject;
	private HoldObject hObj;
	private boolean controlled;
	
	public Player (float xPos, float yPos, float angle, RenderMode renderMode)
	{
		//initialize Character and Entity data
		super(Entity.DEFAULT_SIZE, xPos, yPos, angle, 1.0f, 1.0f, false, renderMode, 100, 100, 0.5f);
		
		//initialize Player data
		energy = 100;
	}
	
	@Override
	public void update()
	{
		super.update();
	}
	
	public void die ()
	{
		EntityCleaner.queueEntityForRemoval(this);
	}
	
	public void attack ()
	{
		energy -= 5;
	}
	
	@Override
	public void interact (Entity ent)
	{
		if (ent instanceof StaticBlock || ent instanceof HoldObject || ent instanceof Door)
		{
			colList.remove(ent);
		}
		else if (ent instanceof InvenPickup)
		{
			Inventory.add(((InvenPickup)ent).getName());
			EntityCleaner.queueEntityForRemoval(ent);
			colList.remove(ent);
		}
		else if (ent instanceof Health)
		{
			health += ((Powerup)ent).getValue();
		}
		else if (ent instanceof Energy)
		{
			energy += ((Powerup)ent).getValue();
		}
	}
	
	public HoldObject getHeldObject()
	{
		return hObj;
	}

	public int getEnergy()
	{
		return energy;
	}

	public boolean isHoldingObject()
	{
		return holdingObject;
	}
	
	public void holdObject(HoldObject hObj)
	{
		holdingObject = true;
		this.hObj = hObj;
		hObj.hold();
		updateHeldObjectPosition();
		colIgnoreList.add(hObj);
		hObj.colIgnoreList.add(this);
	}
	
	public void dropObject()
	{
		holdingObject = false;
		colIgnoreList.remove(hObj);
		hObj.colIgnoreList.remove(this);
		hObj.drop();
		hObj = new PhysBlock(0.0f, 0.0f, 0.0f, RenderMode.BLANK);
		hObj = null;
	}

	public void updateHeldObjectPosition()
	{
		//TODO: FIND OUT WHY THE FUCK THIS WORKS
		float heldDistance = hObj.size + this.halfSize + 10.0f;
		double rad = Math.toRadians(angle + 90.0);
		Vector2f directionVec = new Vector2f((float)Math.cos(rad), (float)Math.sin(rad));
		directionVec.scale(heldDistance);
		directionVec.add(posVec);
		
		hObj.setPos(directionVec.getX(), directionVec.getY());
		hObj.setAngle(angle);
	}
	
	public void disableUserControl()
	{
		controlled = false;
	}
	
	public void enableUserControl()
	{
		controlled = true;
	}
	
	public boolean userHasControl()
	{
		return controlled;
	}
}