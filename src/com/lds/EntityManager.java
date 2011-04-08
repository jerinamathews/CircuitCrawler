package com.lds;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import com.lds.game.entity.Entity;

public class EntityManager 
{
	private static ArrayList<Entity> trashList;
	private static ArrayList<Entity> addList;
	
	public EntityManager()
	{
		trashList = new ArrayList<Entity>();
		addList = new ArrayList<Entity>();
	}
	
	public static void removeEntity(Entity ent)
	{
		ent.setExists(false);
		if (!trashList.contains(ent))
			trashList.add(ent);
	}
	
	public static void addEntity(Entity ent)
	{
		if (!addList.contains(ent))
			addList.add(ent);
	}
	
	public void update(ArrayList<Entity> entList, GL10 gl)
	{
		for (Entity ent : trashList)
		{
			ent.freeHardwareBuffers(gl);
			for (Entity cEnt : entList)
			{
				if (cEnt.colList.contains(ent))
					cEnt.colList.remove(ent);
			}
			entList.remove(ent);
			ent = null;
		}
		
		for (Entity ent : addList)
		{
			ent.genHardwareBuffers(gl);
			entList.add(0, ent);
		}
		
		trashList.clear();
		addList.clear();
	}
}
