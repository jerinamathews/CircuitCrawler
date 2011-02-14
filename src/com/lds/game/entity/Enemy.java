package com.lds.game.entity;

import com.lds.EntityManager;
import com.lds.game.SoundPlayer;
import com.lds.Enums.AIType;
import com.lds.Stopwatch;
import com.lds.game.ai.NodePath;
import com.lds.game.ai.Node;

public abstract class Enemy extends Character //enemies will fall under this class
{
	private static int enemyCount = 0;
	protected AIType type;
	protected boolean agressive;
	protected int lastTime, randomTime;
	//Patrol stuff
	protected NodePath patrolPath;
	protected int pathLocation;
	protected boolean onPatrol, doneRotating;

	public Enemy(float size, float xPos, float yPos, boolean circular, int health, AIType type)
	{
		this(size, xPos, yPos, 0.0f, 1.0f, 1.0f, circular, health, type);
	}
	
	public Enemy(float size, float xPos, float yPos, float angle, float xScl, float yScl, boolean circular, int health, AIType type)
	{
		super(size, xPos, yPos, angle, xScl, yScl, circular, health, 0.0f);
		this.type = type;
		lastTime = Stopwatch.elapsedTimeMs();
		randomTime = 500;
		enemyCount++;
		agressive = false;
		onPatrol = false;
		doneRotating = false;
	}
	
	@Override
	public void update()
	{
		super.update();
	}
	
	@Override
	public void die()
	{
		enemyCount--;
		EntityManager.removeEntity(this);
		SoundPlayer.getInstance().playSound(3);
	}
	
	@Override
	public void interact(Entity ent)
	{
		if (ent instanceof AttackBolt)
		{
			takeDamage(25);
			agressive = true;
		}
	}
	
	//Patrol stuff
	
	public void setPatrolPath(NodePath patrolPath)
	{
		type = AIType.PATROL;
		this.patrolPath = patrolPath;
		pathLocation = 0;
	}
	
	public NodePath getPatrolPath()
	{
		return patrolPath;
	}
	
	public Node getPatrolPathNode(int index)
	{
		return patrolPath.getNodeList().get(index);
	}
	
	public int getClosestPatrolPathNodeIndex()
	{
		return patrolPath.getNodeList().indexOf(patrolPath.getClosestNode(this));
	}
	
	public Node getClosestPatrolPathNode()
	{
		return patrolPath.getClosestNode(this);
	}
	
	public int getPathLocation()
	{
		return pathLocation;
	}
	
	public boolean isOnPatrol()
	{
		return onPatrol;
	}
	
	public void setPathLocation(int pathLocation)
	{
		this.pathLocation = pathLocation;
	}
	
	public void setOnPatrol(boolean onPatrol)
	{
		this.onPatrol = onPatrol;
	}
	
	public boolean isDoneRotating()
	{
		return doneRotating;
	}
	
	public void setDoneRotating(boolean doneRotating)
	{
		this.doneRotating = doneRotating;
	}
	
	//General Stuff
	
	public AIType getType()
	{
		return type;
	}
	
	public boolean isAgressive()
	{
		return agressive;
	}
	
	public static int getEnemyCount ()
	{
		return enemyCount;
	}
	
	public int getRandomTime()
	{
		return randomTime;
	}
	
	public int getLastTime()
	{
		return lastTime;
	}
	
	public void setAgressive(boolean agressive)
	{
		this.agressive = agressive;
	}
	
	public void setRandomTime(int randomTime)
	{
		this.randomTime = randomTime;
	}
	
	public void setLastTime(int lastTime)
	{
		this.lastTime = lastTime;
	}
}