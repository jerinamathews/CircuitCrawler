/**
 * Copyright (c) 2010-2012 Lightning Development Studios <lightningdevelopmentstudios@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ltdev.cc.entity;

import com.ltdev.EntityManager;
import com.ltdev.Stopwatch;
import com.ltdev.cc.physics.primitives.Rectangle;
import com.ltdev.graphics.TextureManager;
import com.ltdev.math.Vector2;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL11;

/**
 * A Cannon is an object that fires blocks at the player to push them.
 * @author Lightning Development Studios
 */
public class Cannon extends Entity
{
	private float shotVelocity, stupidity, time;
	private Entity target;
	private float size;
	private ArrayList<CannonShell> shells;
	
	/**
	 * Initializes a new instance of the Cannon class.
	 * \todo Create comments for the last 3 variables.
	 * @param size The Cannon's size.
	 * @param position The Cannon's position.
	 * @param angle The Cannon's angle.
	 * @param stupidity A value that changes the cannon's accuracy.
	 * @param shotVelocity The velocity that cannons are shot at.
	 * @param p A reference to the target entity.
	 */
	public Cannon(float size, Vector2 position, float angle, float stupidity, float shotVelocity, Entity t)
    {
        super(new Rectangle(new Vector2(size, size), position, angle, true));
        
        this.shotVelocity = shotVelocity;
        this.size = size;
        
        shape.setStatic(true);
        target = t;
        shells = new ArrayList<CannonShell>();
        
        this.tex = TextureManager.getTexture("tilesetentities");
        this.tilesetX = 3;
        this.tilesetY = 0;
    }
	
	/**
	 * Force the Cannon to face the player.
	 */
	public void facePlayer()
	{
	    Vector2 distance = Vector2.subtract(shape.getPos(), target.getPos());
	    shape.setAngle(distance.angleDeg() + 180 + (float)(Math.random() * 2 - 1) * stupidity);
	}

	@Override
	public void update(GL11 gl)
	{
		super.update(gl);
		facePlayer();
		
		time += Stopwatch.getFrameTime();
		
		if (time > 3000)
		{
		    if (shells.size() > 2)
		    {
		        EntityManager.removeEntity(shells.get(0));
		        shells.remove(0);
		    }
		    
		    CannonShell shell = new CannonShell(Vector2.add(getPos(), new Vector2((float)Math.cos(shape.getAngle()) * (size / 2 + 10), (float)Math.sin(shape.getAngle()) * (size / 2 + 10))), shape.getAngle());
            shell.setTexture(TextureManager.getTexture("tilesetentities"));      
            EntityManager.addEntity(shell);
            
		    shells.add(shell);
		    shell.shape.addImpulse(new Vector2((float)Math.cos(shape.getAngle()) * shotVelocity, (float)Math.sin(shape.getAngle()) * shotVelocity));
		    
		    time = 0;
		}
	}
}
