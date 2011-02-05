package com.lds.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.lds.Graphics;
import com.lds.game.event.*;
import com.lds.game.menu.MainMenu;

public class Run extends Activity
{
	public Graphics glSurface;
	public GameRenderer gameR;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
				
		//Grab screen information to initialize local entity ArrayList
		DisplayMetrics screen = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(screen);
		float screenX = (float)screen.widthPixels;
		float screenY = (float)screen.heightPixels;
		
		//Enable fullscreen
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//set up OpenGL rendering
		Object syncObj = new Object();
		gameR = new GameRenderer(screenX, screenY, this, syncObj);
		glSurface = new Graphics(this, gameR, syncObj);
		setContentView(glSurface);
		final OnGameOverListener l = new OnGameOverListener()
		{
			public void onGameOver()
			{
				Intent i = new Intent(Run.this, MainMenu.class);
				startActivity(i);
			}
		};
		glSurface.setGameInitializedEvent(new OnGameInitializedListener()
		{
			public void onGameInitialized() 
			{
				glSurface.setGameOverEvent(l);
			}
		});
	}
	
	@Override
	protected void onResume ()
	{
		super.onResume();
		glSurface.onResume();
	}
	
	@Override
	protected void onPause ()
	{
		super.onPause();
		glSurface.onPause();
		finish();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		finish();
	}
}
