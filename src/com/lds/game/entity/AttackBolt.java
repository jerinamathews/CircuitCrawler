package com.lds.game.entity;

import com.lds.Vector2f;
import com.lds.EntityManager;

public class AttackBolt extends PhysEnt
{
	Vector2f directionVec;
	
	public AttackBolt(Vector2f posVec, Vector2f directionVec, float angle)
	{
		super(20.0f, posVec.getX(), posVec.getY(), false, false, 100.0f, 100.0f, 0.0f);
		this.directionVec = directionVec;
		this.angle = angle;
		this.move(directionVec.getX() * 5.0f, directionVec.getY() * 5.0f);
		this.enableColorMode(1.0f, 0.0f, 0.0f, 1.0f);
		this.setColorInterpSpeed(0.05f);
		this.initColorInterp(0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	@Override
	public void interact(Entity ent)
	{
		ent.colList.remove(this);
		if (!(ent instanceof Player))
			EntityManager.removeEntity(this);
	}
	
	@Override
	public void update()
	{
		super.update();
		if (this.getColorA() == 0.0f)
		{
			EntityManager.removeEntity(this);
		}
	}
}
