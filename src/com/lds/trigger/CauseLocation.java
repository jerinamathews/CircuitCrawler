package com.lds.trigger;

import com.lds.game.Player;

public class CauseLocation extends Cause
{
	private Player player;
	private int minX, maxX, minY, maxY;
	
	public CauseLocation (Player player, int minX, int maxX, int minY, int maxY)
	{
		this.player = player;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}
	
	@Override
	public void update()
	{
		if (player.getXPos() >= minX && player.getXPos() <= maxX && player.getYPos() >= minY && player.getYPos() <= maxY)
		{
			trigger();
		}
		else
		{
			untrigger();
		}
	}
}
