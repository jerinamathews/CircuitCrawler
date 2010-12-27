package com.lds.game;

import javax.microedition.khronos.opengles.GL10;

import com.lds.EntityCleaner;
import com.lds.Point;
import com.lds.TextureLoader;
import com.lds.TilesetHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import java.util.ArrayList;

//Highest level abstract class in game

public abstract class Entity 
{
	public static final float DEFAULT_SIZE = 30.0f;
	
	//behavior data
	public boolean isSolid, isTextured;
	
	//graphics data
	public int texturePtr;
	public float angle, size, xPos, yPos, xScl, yScl, speed, halfSize;
	
	public float[] vertices;
	public float[] texture;
	public byte[] indices;
	
	public FloatBuffer vertexBuffer;
	public FloatBuffer textureBuffer;
	public ByteBuffer indexBuffer;
	
	//debug data
	public int entID;
	public boolean isRendered;
	public static int entCount;
	
	//collision data
	public Point[] colPoints;
	public float[] colSlopes;
	public double diagonal, rad, diagAngle;
	
	public ArrayList<Entity> colList = new ArrayList<Entity>();
	
	public  Entity (float _size, float _xPos, float _yPos, float _angle, float _xScl, float _yScl, boolean _isSolid)
	{
		//initialize behavior variables
		isSolid = _isSolid;
		isTextured = false;
		
		//initializes graphics variables
		size = _size;
		xPos = _xPos;
		yPos = _yPos;
		angle = _angle;
		xScl = _xScl;
		yScl = _yScl;
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
	
	public Entity (float _size, float _xPos, float _yPos)
	{
		this(_size, _xPos, _yPos, 0.0f, 1.0f, 1.0f, true);
	}
	
	public void draw(GL10 gl)
	{
		if (isTextured)	{gl.glBindTexture(GL10.GL_TEXTURE_2D, texturePtr);}
		
		gl.glFrontFace(GL10.GL_CW);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		if (isTextured)	{gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);}
		
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
		if (isTextured) {gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);}
		
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		if (isTextured) {gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);}
	}
		
	public void remove()
	{
		EntityCleaner.queueEntityForRemoval(this);
	}
	
	
	
	
	
	/*********************
	 * Collision Methods *
	 *********************/
	
	//used to get the absolute, not relative, positions of the entity's 4 points in the XY Plane
	public void updateAbsolutePointLocations ()
	{
		//keeps the angle not exactly 0, so slope is never undefined. Still works within unnoticeable margin of error
		if (angle % 90 == 0.0f)
		{
			angle += 0.1f;
		}
		//reinitialize colllision variables
		rad = Math.toRadians((double)(angle + 90.0f));
		diagonal = Math.sqrt(Math.pow(halfSize * xScl, 2) + Math.pow(halfSize * yScl, 2));
		diagAngle = Math.atan((halfSize * xScl) / (halfSize * yScl));
		
		colPoints[0].setX((float)(Math.cos(this.rad + diagAngle) * diagonal) + xPos);
		colPoints[0].setY((float)(Math.sin(this.rad + diagAngle) * diagonal) + yPos);
		colPoints[1].setX((float)(Math.cos(this.rad + Math.PI - diagAngle) * diagonal) + xPos);
		colPoints[1].setY((float)(Math.sin(this.rad + Math.PI - diagAngle) * diagonal) + yPos);
		colPoints[2].setX((float)(Math.cos(this.rad - diagAngle) * diagonal) + xPos);
		colPoints[2].setY((float)(Math.sin(this.rad - diagAngle) * diagonal) + yPos);
		colPoints[3].setX((float)(Math.cos(this.rad - Math.PI + diagAngle) * diagonal) + xPos);
		colPoints[3].setY((float)(Math.sin(this.rad - Math.PI + diagAngle) * diagonal) + yPos);
	}
	
	//This tests for collision between two entities (no shit) - Devin
	public boolean isColliding (Entity ent)
	{	
		//checks to see if either object is not solid
		if (this.isSolid == false || ent.isSolid == false)
		{
			return false;
		}
		
		//hurr durr
		updateAbsolutePointLocations();
		ent.updateAbsolutePointLocations();
		
		//makes sure the entities are close enough so that collision testing is actually neccessary
		if (Math.sqrt(Math.pow(xPos - ent.xPos, 2) + Math.pow(yPos - ent.yPos, 2)) > ((diagonal) + ent.diagonal))
		{
			return false;
		}
		
		//used to determine the number of collisions with respect to the axes. if it is 4 after the check, method returns true
		int colCount = 0;
		float ent1High, ent1Low, ent2High, ent2Low;
		
		//calculates 4 slopes to use with the SAT
		colSlopes[0] = ((this.colPoints[0].getY() - this.colPoints[1].getY()) / (this.colPoints[0].getX() - this.colPoints[1].getX()));
		colSlopes[1] = -1 / colSlopes[0];
		colSlopes[2] = ((ent.colPoints[0].getY() - ent.colPoints[1].getY()) / (ent.colPoints[0].getX() - ent.colPoints[1].getX()));
		colSlopes[3] = -1 / colSlopes[2];
		
		//checks for collision on each of the 4 slopes
		for (float slope : colSlopes)
		{
			ent1High = -999999.0f;
			ent2High = -999999.0f;
			ent1Low = 999999.0f;
			ent2Low = 999999.0f;
			//iterates through each point on ent1 to get high and low c values
			for (Point point : this.colPoints)
			{
				//finds c (as in y = mx + c) for the line going through each point
				point.setColC((point.getY() - slope * point.getX()));
				if (ent1Low > point.getColC())
				{
					ent1Low = point.getColC();
				}
				if (ent1High < point.getColC())
				{
					ent1High = point.getColC();
				}
			}
			
			//same as above for ent2
			for (Point point : ent.colPoints)
			{
				point.setColC(point.getY() - slope * point.getX());
				if (ent2Low > point.getColC())
				{
					ent2Low = point.getColC();
				}
				if (ent2High < point.getColC())
				{
					ent2High = point.getColC();
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

	public void toggleTexturing(boolean value)
	{
		isTextured = value;
	}
	
	public void setTexture (int ptr)
	{
		texturePtr = ptr;
	}
	
	public void setTilesetCoords (int x, int y)
	{
		texture = TilesetHelper.getTextureVertices(x, y, 0, 7);
		allocTextureBuffer();
	}
	
	public void setTilesetCoords (int tileID)
	{
		texture = TilesetHelper.getTextureVertices(tileID);
		allocTextureBuffer();
	}
	
	private void allocTextureBuffer()
	{
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
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
}
