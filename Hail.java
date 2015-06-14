//********************************************************
//Brandon Grant
//Weather Forecast
//Hail.java
//********************************************************

import java.awt.*;
import java.util.*;

class Hail
{
    private int x, y, depth;
    private int weight, size, speed;
    private int windSpeed, windDir;
    private int boundX, boundY;
    private boolean alive;

    public Hail(int width, int height)
    {
        boundX = width;
        boundY = height;
        speed = 1;
        windSpeed = 0;
        windDir = 1;
        alive = false;
    }

    public boolean isAlive()
    { 
        return alive;
    }

    public void createHail()
    {
        if( windSpeed > 0 && (int)(Math.random() * 2) == 0 )
        {
            if( windDir == 1 )
                x = -5;
            else
                x = boundX + 5;

            y = (int)(Math.random() * boundY);
        }
        else
        {
            x = (int)(Math.random() * boundX);
            y = 0;
        }

        depth = (int)(Math.random() * 4);
        size = 4 - depth;
        weight = (int)(Math.random() * size) + 30;
        alive = true;
    }

    public void move()
    {
        x += windDir * windSpeed;
        y += weight / 2 * speed;

        if( y > boundY)
            alive = false;

        if( windSpeed > 0 )
        {
            if( windDir == -1 && x < 0 )
                alive = false;
            if( windDir == 1 && x > boundX )
                alive = false;
        }
    }

    public void setSpeed( int speed )
    {
        this.speed = speed;
    }

    public void startWind( int speed, int dir )
    {
        windSpeed = speed;

        if( dir > 0 )
            windDir = -1;
        else
            windDir = 1;
    }

    public void stopWind()
    {
        windSpeed = 0;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getDepth()
    {
        return depth;
    }

    public int getWeight()
    {
        return weight;
    }

    public int getSize()
    {
        return size;
    }
}