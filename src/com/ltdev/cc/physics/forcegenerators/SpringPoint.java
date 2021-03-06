package com.ltdev.cc.physics.forcegenerators;

import com.ltdev.cc.physics.primitives.Shape;
import com.ltdev.math.Vector2;

public class SpringPoint implements IndivForce
{
    public Vector2 point;
    public float restLength;
    public float k;

    public SpringPoint(Vector2 point, float restLength, float k)
    {
        this.point = point;
        this.restLength = restLength;
        this.k = k;
    }

    public void UpdateForce(float frameTime, Shape s)
    {
        Vector2 v = Vector2.subtract(point, s.getPos());
        float mag = (v.length() - restLength) * k * frameTime;
        s.addImpulse(Vector2.scaleTo(v, mag));
    }
}
