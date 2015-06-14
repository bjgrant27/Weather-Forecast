//********************************************************
//Brandon Grant
//Weather Forecast
//Rain.java
//********************************************************

import java.awt.*;
import java.util.*;

class Rain
{
    private int x, y, depth, speed, visibility;
    private int boundX, boundY;
    private boolean alive;

    public Rain(int width, int height)
    {
        boundX = width;
        boundY = height;
        speed = 1;
        visibility = 200;
        alive = false;
    }

    public void createRainDrop()
    {
        depth = (int)(Math.random() * 5);
        x = (int)(Math.random() * boundX);
        y = (int)(Math.random() * boundY);
        visibility = 255 - (int)(Math.random() * 100);

        alive = true;
    }

    public void fade()
    {
        visibility -= 30 * speed;

        if( visibility <= 0 )
            alive = false;
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


    public int getVisibility()
    {
        return visibility;
    }

    public boolean isAlive()
    {
        return alive;
    }

    public void setSpeed( int speed )
    {
        this.speed = speed;
    }
}