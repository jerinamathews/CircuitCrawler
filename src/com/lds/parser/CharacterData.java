package com.lds.parser;

import java.util.HashMap;

public class CharacterData extends PhysEntData
{
	private int health;
	public CharacterData(HashMap<String, String> characterHM)
	{
		super(characterHM);
		
		if(characterHM.get("health") != null)
			health = Integer.parseInt(characterHM.get("health"));
		else
			health = 100;
	}
		
	public int getHealth()			{return health;}

	
	public void setHealth(int newHealth)		{health = newHealth;}

}
