package com.lds.parser;

import java.util.ArrayList;
import java.util.HashMap;

import com.lds.Texture;
import com.lds.game.Game;
import com.lds.game.entity.Entity;

/*import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.lds.game.R;
import com.lds.Enums.RenderMode;

import android.content.Context;
import android.content.res.XmlResourceParser;*/

public abstract class EntityData
{
	private boolean isSolid;
	protected boolean circular;
	protected boolean willCollide;
	protected boolean textureModeEnabled, tilesetModeEnabled;
	private String tileCoordsStr, id;
	protected float size, xPos, yPos, xScl, yScl, angle;	
	protected float[] color, gradient, texture;
	protected int tileX, tileY;
	protected Texture tex;
	
	public EntityData(HashMap<String, String> entHM)
	{
		if(entHM.get("size") != null)
			size = Float.parseFloat(entHM.get("size"));
		if(entHM.get("xPos") != null)
			xPos = Float.parseFloat(entHM.get("xPos"));
		if(entHM.get("yPos") != null)
			yPos = Float.parseFloat(entHM.get("yPos"));
		if(entHM.get("xScl") != null)
			xScl = Float.parseFloat(entHM.get("xScl"));
		if(entHM.get("yScl") != null)
			yScl = Float.parseFloat(entHM.get("yScl"));
		if(entHM.get("angle") != null)
			angle = Float.parseFloat(entHM.get("angle"));
		if(entHM.get("id") != null)
			id = entHM.get("id");
		
		isSolid = Boolean.parseBoolean(entHM.get("isSolid"));
		circular = Boolean.parseBoolean(entHM.get("circular"));
		willCollide = Boolean.parseBoolean(entHM.get("willCollide"));
		
		//COLOR
		if(entHM.get("color") != null)
		{
			String[] colorsStr = entHM.get("color").split(",");
			color = new float[colorsStr.length];
			for(int i = 0; i < colorsStr.length; i++)
				color[i] = Float.parseFloat(colorsStr[i]);
		}
		
		//GRADIENT
		if (entHM.get("gradient") != null)
		{
			String[] gradientStr = entHM.get("gradient").split(",");
			gradient = new float[gradientStr.length];
			for (int i = 0; i < gradientStr.length; i++)
				gradient[i] = Float.parseFloat(gradientStr[i]);
		}
		
		//TEXTURE
		if (entHM.get("texture") != null)
		{
			String texID = entHM.get("texID");
			if(texID.equalsIgnoreCase("tilesetcolors"))
				tex = Game.tilesetcolors;
			else if(texID.equalsIgnoreCase("tilesetwire"))
				tex = Game.tilesetwire;
			else if(texID.equalsIgnoreCase("randomthings"))
				tex = Game.randomthings;
			else if(texID.equalsIgnoreCase("text"))
				tex = Game.text;
			
			String[] textureStr = entHM.get("coords").split(",");
			texture = new float[textureStr.length];
			for (int i = 0; i < textureStr.length; i++)
				texture[i] = Float.parseFloat(textureStr[i]);
			
			textureModeEnabled = true;
		}
		
		//TILESET
		if (entHM.get("tileset") != null)
		{
			//TODO move textures to a different XML file, load dynamically
			String texID = entHM.get("texID");
			if(texID.equalsIgnoreCase("tilesetcolors"))
				tex = Game.tilesetcolors;
			else if(texID.equalsIgnoreCase("tilesetwire"))
				tex = Game.tilesetwire;
			else if(texID.equalsIgnoreCase("randomthings"))
				tex = Game.randomthings;
			else if(texID.equalsIgnoreCase("text"))
				tex = Game.text;
			
			tileX = Integer.parseInt(entHM.get("x"));
			tileY = Integer.parseInt(entHM.get("y"));
			
			tilesetModeEnabled = true;
		}
	}
	
	
	//float setters/getters
	public void setSize(float newSize) 			{size = newSize;}
	public void setXPos (float newXPos)			{xPos = newXPos;}
	public void setYPos (float newYPos)			{yPos = newYPos;}
	public void setXScl(float newXScl)			{xScl = newXScl;}
	public void setYScl(float newYScl)			{yScl = newYScl;}
	public void setAngle(float newAngle)		{angle = newAngle;}
	public void setIsSolid(boolean newIsSolid) 	{isSolid = newIsSolid;}
	public void setCircular(boolean newCircular){circular = newCircular;}
	
	public float getSize()		{return size;}
	public float getXPos() 		{return xPos;}
	public float getYPos()		{return yPos;}
	public float getXScl()		{return xScl;}
	public float getYScl()		{return yScl;}
	public float getAngle()		{return angle;}
	public boolean getIsSolid()	{return isSolid;}
	public boolean getCircular(){return circular;}
		
	public void createInst(ArrayList<Entity> entData)
	{
	}
}
