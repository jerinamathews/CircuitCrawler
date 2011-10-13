package com.lds;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.lds.game.event.*;

public class Graphics extends GLSurfaceView
{
	private Renderer renderer;
	private Object syncObj;
	
	public Graphics(Context context, Renderer r, Object syncObj) 
	{
		super(context);
		this.syncObj = syncObj;
		renderer = r;
		setRenderer(r);
	}
	
	@Override
	public boolean onTouchEvent(final MotionEvent event)
	{
		//grab touch input, pass it through to the generic renderer (in this case, com.lds.GameRenderer
		queueEvent(new Runnable() 
		{
			public void run()
			{
				renderer.onTouchInput(event);
			}
		});
		
		//sync with OpenGL thread
		synchronized (syncObj)
		{
			try
			{
				syncObj.wait(500);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public void onPuzzleWon()
	{
		queueEvent(new Runnable()
		{
			public void run()
			{
				renderer.onPuzzleWon();
			}
		});
	}
	
	public void onPuzzleFailed()
	{
		queueEvent(new Runnable()
		{
			public void run()
			{
				renderer.onPuzzleFailed();
			}
		});
	}
			
	public interface Renderer extends GLSurfaceView.Renderer
	{
		public abstract void onTouchInput(MotionEvent event);
		public abstract void setGameOverEvent(OnGameOverListener listener);
		public abstract void setGameInitializedEvent(OnGameInitializedListener listener);
		public abstract void setPuzzleActivatedEvent(OnPuzzleActivatedListener listener);
		public abstract void onPuzzleWon();
		public abstract void onPuzzleFailed();
	}
}
