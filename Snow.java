//********************************************************
//Brandon Grant
//Weather Forecast
//Snow.java
//********************************************************

import java.awt.*;
import java.util.*;

class Snow
{
    private int x, y, depth;
    private int weight, size, speed;
    private int amtDrifted, driftDir;
    private int windSpeed, windDir;
    private int boundX, boundY;
    private boolean alive;

    public Snow(int width, int height)
    {
        boundX = width;
        boundY = height;
        speed = 1;
        driftDir = 1;
        windSpeed = 0;
        windDir = 1;
        alive = false;
    }

    public boolean isAlive()
    { 
        return alive;
    }

    public void createSnowFlake()
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
        weight = (int)(Math.random() * size) + 5;
        alive = true;

        if( (int)(Math.random() * 2) == 0 )
            driftDir = 1;
        else
            driftDir = -1;
    }

    public void move()
    {
        if( y % 2 == 0 )
        {
            if( (int)(Math.random() * 10) == 0 )
                driftDir *= -1;

            x += driftDir * speed;
            x += windDir * windSpeed;
            amtDrifted++;

            if( amtDrifted >= weight )
            {
                driftDir *= -1;
                amtDrifted = 0;
            }
        }

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