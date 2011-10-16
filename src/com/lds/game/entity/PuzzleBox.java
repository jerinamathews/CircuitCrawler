package com.lds.game.entity;

import com.lds.game.event.PuzzleActivatedListener;

public class PuzzleBox extends StaticEnt
{
	private PuzzleActivatedListener listener;
	
	public PuzzleBox(float size, float xPos, float yPos, boolean circular, boolean willCollide)
	{
		super(size, xPos, yPos, circular, willCollide);
	}
	
	public PuzzleBox(float size, float xPos, float yPos, float angle, float xScl, float yScl, boolean isSolid, boolean circular, boolean willCollide)
	{
		super(size, xPos, yPos, angle, xScl, yScl, isSolid, circular, willCollide);
	}
	
	public void setPuzzleInitListener(PuzzleActivatedListener listener)
	{
		this.listener = listener;
	}
	
	public void run()
	{
		if (listener != null)
			listener.onPuzzleActivated();
	}
}
