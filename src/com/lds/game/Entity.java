package com.lds.game;

import javax.microedition.khronos.opengles.GL10;

import com.lds.EntityCleaner;
import com.lds.Point;
import com.lds.TilesetHelper;
import com.lds.Enums.RenderMode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import java.util.ArrayList;

//Highest level abstract class in game

public abstract class Entity 
{
	public static final float DEFAULT_SIZE = 69.0f;
	
	//behavior data
	protected boolean isSolid;
	protected boolean isRendered;
	
	//graphics data
	protected float angle, size, xPos, yPos, xScl, yScl, halfSize;
	protected float colorR, colorG, colorB, colorA;
	protected int texturePtr;
	protected RenderMode renderMode;
	
	protected float[] vertices;
	protected float[] texture;
	protected float[] color;
	private byte[] indices;
	
	protected FloatBuffer vertexBuffer;
	protected FloatBuffer textureBuffer;
	protected FloatBuffer colorBuffer;
	private ByteBuffer indexBuffer;
	
	//debug data
	private int entID;
	private static int entCount = 0;
	
	//collision data
	public Point[] colPoints;
	public float[] colSlopes;
	public double diagonal, rad, diagAngle;
	
	public ArrayList<Entity> colList = new ArrayList<Entity>();
	public ArrayList<Entity> colIgnoreList = new ArrayList<Entity>();
	
	
	public Entity (float size, float xPos, float yPos, RenderMode renderMode)
	{
		this(size, xPos, yPos, 0.0f, 1.0f, 1.0f, true, renderMode);
	}
	
	public  Entity (float size, float xPos, float yPos, float angle, float xScl, float yScl, boolean isSolid, RenderMode renderMode)
	{
		//initialize debug data
		entID = entCount;
		entCount++;
		
		//initialize behavior variables
		this.isSolid = isSolid;
		
		//initializes graphics variables
		this.size = size;
		this.xPos = xPos;
		this.yPos = yPos;
		this.angle = angle;
		this.xScl = xScl;
		this.yScl = yScl;
		this.renderMode = renderMode;
		
		halfSize = size / 2;
		
		//initializes collision variables
		rad = Math.toRadians((double)(angle + 90.0f));
		diagonal = Math.sqrt(Math.pow(halfSize * xScl, 2) + Math.pow(halfSize * yScl, 2)); //distance from center to corner
		diagAngle = Math.asin((halfSize * xScl) / diagonal); //angle between vertical line and diagonal to top left corner
		colSlopes = new float[4];
		
		colPoints = new Point[4]; //0: top left, 1: bottom left, 2:top right, 3: bottom right
		colPoints[0] = new Point();
		colPoints[1] = new Point();
		colPoints[2] = new Point();
		colPoints[3] = new Point();
		
		//makes it so x/yPos are in center of box - Robert
		float[] initVerts = {	halfSize, halfSize, 	//top left
								halfSize, -halfSize, 	//bottom left
								-halfSize, halfSize, 	//top right
								-halfSize, -halfSize }; //bottom right

		vertices = initVerts;
				
		byte[] initIndices = {	0, 1, 2, 3 };
		
		indices = initIndices;
		
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuf.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
				
		indexBuffer = ByteBuffer.allocateDirect(indices.length);
		indexBuffer.put(indices);
		indexBuffer.position(0);
	}
	
	public void draw(GL10 gl)
	{
		//Enable texturing and bind the current texture pointer (texturePtr) to GL_TEXTURE_2D
		if (renderMode == RenderMode.TEXTURE || renderMode == RenderMode.TILESET)
		{
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturePtr);
		}
		
		//Sets the front face of a polygon based on rotation (Clockwise - GL_CW, Counter-clockwise - GL_CCW)
		gl.glFrontFace(GL10.GL_CW);
		
		//Backface culling
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		
		//Enable settings for this polygon
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		if (renderMode == RenderMode.TEXTURE || renderMode == RenderMode.TILESET) {gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);}
		if (renderMode == RenderMode.GRADIENT) {gl.glEnableClientState(GL10.GL_COLOR_ARRAY);}
		
		//Bind vertices, texture coordinates, and/or color coordinates to the OpenGL system
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
		if (renderMode == RenderMode.TEXTURE || renderMode == RenderMode.TILESET) {gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);}
		if (renderMode == RenderMode.GRADIENT) {gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);}
		
		//Sets color
		if (renderMode == RenderMode.COLOR) {gl.glColor4f(colorR, colorG, colorB, colorA);}
		
		//Draw the vertices
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);		
		
		//Disable things for next polygon
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		if(renderMode == RenderMode.GRADIENT) {gl.glDisableClientState(GL10.GL_COLOR_ARRAY);}
		gl.glDisable(GL10.GL_CULL_FACE);
		
		//Disable texturing for next polygon
		if (renderMode == RenderMode.TEXTURE || renderMode == RenderMode.TILESET) 
		{
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}
		
		//Reset color for next polygon.
		if (renderMode == RenderMode.COLOR || renderMode == RenderMode.GRADIENT) {gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);}
	}
		
	public void remove()
	{
		EntityCleaner.queueEntityForRemoval(this);
	}
		
	/*********************
	 * Collision Methods *
	 *********************/
	
	//reinitialize colllision variables
	public void initializeCollisionVariables ()
	{
		rad = Math.toRadians((double)(angle + 90.0f));
		diagonal = Math.sqrt(Math.pow(halfSize * xScl, 2) + Math.pow(halfSize * yScl, 2));
		diagAngle = Math.atan2((halfSize * xScl) , (halfSize * yScl));
	}
	
	//used to get the absolute, not relative, positions of the entity's 4 points in the XY Plane
	public void updateAbsolutePointLocations ()
	{
		initializeCollisionVariables();
		
		colPoints[0].setX((float)(Math.cos(this.rad + diagAngle) * diagonal) + xPos); //top left
		colPoints[0].setY((float)(Math.sin(this.rad + diagAngle) * diagonal) + yPos); //top left
		
		colPoints[1].setX((float)(Math.cos(this.rad + Math.PI - diagAngle) * diagonal) + xPos); //bottom left
		colPoints[1].setY((float)(Math.sin(this.rad + Math.PI - diagAngle) * diagonal) + yPos); //bottom left
		
		colPoints[2].setX((float)(Math.cos(this.rad - diagAngle) * diagonal) + xPos); //
		colPoints[2].setY((float)(Math.sin(this.rad - diagAngle) * diagonal) + yPos);
		
		colPoints[3].setX((float)(Math.cos(this.rad - Math.PI + diagAngle) * diagonal) + xPos);
		colPoints[3].setY((float)(Math.sin(this.rad - Math.PI + diagAngle) * diagonal) + yPos);
	}
	
	public boolean closeEnough (Entity ent)
	{
		if (Math.sqrt(Math.pow(xPos - ent.xPos, 2) + Math.pow(yPos - ent.yPos, 2)) < (float)((diagonal) + ent.diagonal))
			return true;
		
		else
			return false;
	}
	
	public boolean isFacing (Entity ent)
	{
		float m = (this.colPoints[0].getY() - this.colPoints[1].getY()) / (this.colPoints[0].getX() - this.colPoints[1].getX());
		float b1 = (colPoints[0].getY() - m * colPoints[0].getX());
		float b2 = (colPoints[3].getY() - m * colPoints[3].getX());
		float entB = (ent.yPos - m * ent.xPos);
		Point frontPoint = new Point(halfSize * (float)Math.cos(rad) + xPos, halfSize * (float)Math.sin(rad) + yPos);
		Point backPoint = new Point(halfSize * (float)Math.cos(rad + Math.PI) + xPos, halfSize * (float)Math.sin(rad + Math.PI) + yPos);
		double frontDist = Math.sqrt((double)Math.pow(ent.xPos - frontPoint.getX(), 2) + Math.pow(ent.yPos - frontPoint.getY(), 2));
		double backDist = Math.sqrt((double)Math.pow(ent.xPos - backPoint.getX(), 2) + Math.pow(ent.yPos - backPoint.getY(), 2));
		
		if (backDist < frontDist)
		{
			return false;
		}
		
		if (entB < b1 && entB > b2 || entB > b1 && entB < b2)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//This tests for collision between two entities (no shit) - Devin
	public boolean isColliding (Entity ent)
	{	
		//checks to see if either object is not solid
		if (this.isSolid == false || ent.isSolid == false)
			return false;
		
		if (colIgnoreList.contains(ent))
			return false;
		
		//update values
		updateAbsolutePointLocations();
		ent.updateAbsolutePointLocations();
		
		//makes sure the entities are close enough so that collision testing is actually neccessary
		if (!closeEnough(ent))
		{
			return false;
		}
		
		//used to determine the number of collisions with respect to the axes. if it is 4 after the check, method returns true
		int colCount = 0;
		float ent1Low, ent1High, ent2Low, ent2High;
		
		//calculates 4 slopes to use with the SAT

		colSlopes[0] = ((this.colPoints[0].getY() - this.colPoints[1].getY()) / (this.colPoints[0].getX() - this.colPoints[1].getX()));
		colSlopes[1] = -1 / colSlopes[0];
		colSlopes[2] = ((ent.colPoints[0].getY() - ent.colPoints[1].getY()) / (ent.colPoints[0].getX() - ent.colPoints[1].getX()));
		colSlopes[3] = -1 / colSlopes[2];
		
		//checks for collision on each of the 4 slopes
		
		for (float slope : colSlopes)
		{
			
			this.colPoints[0].setColC((colPoints[0].getY() - slope * colPoints[0].getX()));
			ent1Low = this.colPoints[0].getColC();
			ent1High = this.colPoints[0].getColC();
			//iterates through each point on ent1 to get high and low c values
			for (int i = 1; i < this.colPoints.length; i++)
			{
				//finds c (as in y = mx + c) for the line going through each point
				colPoints[i].setColC((colPoints[i].getY() - slope * colPoints[i].getX()));
				if (ent1Low > colPoints[i].getColC())
				{
					ent1Low = colPoints[i].getColC();
				}
				if (ent1High < colPoints[i].getColC())
				{
					ent1High = colPoints[i].getColC();
				}
			}
			
			//same for ent2
			ent.colPoints[0].setColC((ent.colPoints[0].getY() - slope * ent.colPoints[0].getX()));
			ent2Low = ent.colPoints[0].getColC();
			ent2High = ent.colPoints[0].getColC();
			//iterates through each point on ent1 to get high and low c values
			for (int i = 1; i < ent.colPoints.length; i++)
			{
				//finds c (as in y = mx + c) for the line going through each point
				ent.colPoints[i].setColC((ent.colPoints[i].getY() - slope * ent.colPoints[i].getX()));
				if (ent2Low > ent.colPoints[i].getColC())
				{
					ent2Low = ent.colPoints[i].getColC();
				}
				if (ent2High < ent.colPoints[i].getColC())
				{
					ent2High = ent.colPoints[i].getColC();
				}
			}
			
			//checks for collision with respect to the current axis. adds one to colCount if the collision is true, and returns false if not
			if ((ent1High >= ent2Low && ent1High <= ent2High) || (ent2High >= ent1Low && ent2High <= ent1High))
			{
				colCount++;
			}
			else
			{
				return false;
			}
		}
		
		//if the objects are colliding with respect to all 4 axes, return true
		if (colCount == 4)
		{
			colList.add(ent);
			return true;
		}
		else
		{
			return false;
		}
	}
			
	//this is a blank method, to be overriden by subclasses
	//it determines how each object interacts with other objects and performs the action
	public void interact (Entity ent)
	{
		
	}
	
	//this is a blank method ot be overriden similat to interact
	//it performs the action to occur when an object stops colliding with another
	public void uninteract (Entity ent)
	{
		
	}
	
	//this is a blank method to be overriden by PickupObj
	//TODO: get rid of this shell, run pickupScale directly from GameRenderer
	public void pickupScale ()
	{
		
	}
	
	/**********************
	 * RenderMode methods *
	 **********************/
	
	//BLANK
	public void setBlankMode()
	{
		renderMode = RenderMode.BLANK;
	}
	
	//COLOR
	public void setColorMode(float r, float g, float b, float a)
	{
		renderMode = RenderMode.COLOR;
		updateColor(r, g, b, a);
	}
	
	public void setColorMode(int r, int b, int g, int a)
	{
		renderMode = RenderMode.COLOR;
		updateColor(r, g, b, a);
	}
	
	public void updateColor(float r, float g, float b, float a)
	{
		if (renderMode == RenderMode.COLOR)
		{
			colorR = r;
			colorG = g;
			colorB = b;
			colorA = a;
		}
	}
	
	public void updateColor(int r, int g, int b, int a)
	{
		if (renderMode == RenderMode.COLOR)
		{
			colorR = (float) r / 255.0f;
			colorG = (float) g / 255.0f;
			colorB = (float) b / 255.0f;
			colorA = (float) a / 255.0f;
		}
	}
	
	//GRADIENT
	public void setGradientMode(float[] color)
	{
		renderMode = RenderMode.GRADIENT;
		updateGradient(color);
	}
	
	public void updateGradient(float[] color)
	{
		if (renderMode == RenderMode.GRADIENT)
		{
			this.color = color;
			
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(color.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			colorBuffer = byteBuf.asFloatBuffer();
			colorBuffer.put(color);
			colorBuffer.position(0);
		}
	}
	
	//TEXTURE
	public void setTextureMode(int texturePtr)
	{
		renderMode = RenderMode.TEXTURE;
		updateTexture(texturePtr);
	}
	
	public void setTextureMode(int texturePtr, float[] texture)
	{
		renderMode = RenderMode.TEXTURE;
		updateTexture(texturePtr, texture);
	}
		
	public void updateTexture(int texturePtr)
	{
		if (renderMode == RenderMode.TEXTURE)
		{
			this.texturePtr = texturePtr;
			float[] initTexture = { 1.0f, 0.0f,
									1.0f, 1.0f,
									0.0f, 0.0f,
									0.0f, 1.0f};
			texture = initTexture;
			
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			textureBuffer = byteBuf.asFloatBuffer();
			textureBuffer.put(texture);
			textureBuffer.position(0);
		}
	}
	
	public void updateTexture(int texturePtr, float[] texture)
	{
		if (renderMode == RenderMode.TEXTURE)
		{
			this.texturePtr = texturePtr;
			this.texture = texture;
			
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			textureBuffer = byteBuf.asFloatBuffer();
			textureBuffer.put(texture);
			textureBuffer.position(0);
		}
	}
	
	//TILESET
	public void setTilesetMode(int texturePtr, int x, int y, int min, int max)
	{
		renderMode = RenderMode.TILESET;
		updateTileset(texturePtr, x, y, min, max);
	}
	
	public void setTilesetMode (int texturePtr, int tileID)
	{
		renderMode = RenderMode.TILESET;
		updateTileset(texturePtr, tileID);
	}
	
	public void updateTileset(int texturePtr, int x, int y, int min, int max)
	{
		if (renderMode == RenderMode.TILESET)
		{
			this.texturePtr = texturePtr;
			texture = TilesetHelper.getTextureVertices(x, y, min, max);
			
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			textureBuffer = byteBuf.asFloatBuffer();
			textureBuffer.put(texture);
			textureBuffer.position(0);
		}
	}
	
	public void updateTileset(int texturePtr, int tileID)
	{
		if (renderMode == RenderMode.TILESET)
		{
			this.texturePtr = texturePtr;
			texture = TilesetHelper.getTextureVertices(tileID);
			
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			textureBuffer = byteBuf.asFloatBuffer();
			textureBuffer.put(texture);
			textureBuffer.position(0);
		}
	}
	
	public void updateTileset(int x, int y, int min, int max)
	{
		if (renderMode == RenderMode.TILESET)
		{
			texture = TilesetHelper.getTextureVertices(x, y, min, max);
		
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			textureBuffer = byteBuf.asFloatBuffer();
			textureBuffer.put(texture);
			textureBuffer.position(0);
		}
	}
	
	public void updateTileset(int tileID)
	{
		if (renderMode == RenderMode.TILESET)
		{
			texture = TilesetHelper.getTextureVertices(tileID);
			
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			textureBuffer = byteBuf.asFloatBuffer();
			textureBuffer.put(texture);
			textureBuffer.position(0);
		}
	}
	
	/**************************
	 * Accessors and Mutators *
	 **************************/
	
	public float getSize()				{ return size; }
	public float getXPos()				{ return xPos; }
	public float getYPos()				{ return yPos; }
	public float getAngle()				{ return angle; }
	public float getXScl()				{ return xScl; }
	public float getYScl()				{ return yScl; }
	public float getColorR()			{ return colorR; }
	public float getColorG()			{ return colorG; }
	public float getColorB()			{ return colorB; }
	public float getColorA()			{ return colorA; }
	public int getTexturePtr()			{ return texturePtr; }
	public RenderMode getRenderMode()	{ return renderMode; }
	public float[] getVertices()		{ return vertices; }
	public float[] getColorCoords()		{ return color; }
	public float[] getTextureCoords()	{ return texture; }
	public int getEntID()				{ return entID; }
	public static int getEntCount()		{ return entCount; }
	
	public void setSize(float size)		{ this.size = size; }
	public void setXPos(float xPos)		{ this.xPos = xPos; }
	public void setYPos(float yPos)		{ this.yPos = yPos; }
	public void setAngle(float angle)	{ this.angle = angle; }
	public void setXScl(float xScl)		{ this.xScl = xScl; }
	public void setYScl(float yScl)		{ this.yScl	= yScl; }
}
