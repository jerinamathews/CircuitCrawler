package com.lds.game.puzzle;

import com.lds.game.event.OnPuzzleFailListener;
import com.lds.game.event.OnPuzzleInitializedListener;
import com.lds.game.event.OnPuzzleSuccessListener;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public interface IPuzzle extends GLSurfaceView.Renderer
{	
	public void onTouchEvent(MotionEvent event);
	public void setContext(Context context);
	public void setSyncObj(Object o);
	
	public void setPuzzleInitializedEvent(OnPuzzleInitializedListener listener);
	public void setPuzzleSuccessEvent(OnPuzzleSuccessListener listener);
	public void setPuzzleFailEvent(OnPuzzleFailListener listener);
}