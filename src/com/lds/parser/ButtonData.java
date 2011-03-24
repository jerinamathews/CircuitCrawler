package com.lds.parser;

import java.util.ArrayList;
import java.util.HashMap;
import com.lds.game.entity.Button;
import com.lds.game.entity.Entity;

public class ButtonData extends StaticEntData
{
	private Button buttonRef;
	public ButtonData(HashMap<String, String> buttonHM)
	{
		super(buttonHM);
	}
	
	public void createInst(ArrayList<Entity> entData)
	{
		buttonRef = new Button(xPos, yPos);
		
		//COLOR
		if (color != null)
			buttonRef.enableColorMode(color[0],color[1],color[2],color[3]);
		
		//GRADIENT
		if (gradient != null)
			buttonRef.enableGradientMode(gradient);
		
		//TEXTURE
		if (textureModeEnabled)
			buttonRef.enableTextureMode(tex, texture);
		
		//TILESET
		if (tilesetModeEnabled)
			buttonRef.enableTilesetMode(tex, tileX, tileY);
		
		entData.add(buttonRef);
		ent = buttonRef;
	}
}