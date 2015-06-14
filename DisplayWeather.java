//********************************************************
//Brandon Grant
//Weather Forecast
//DisplayWeather.java
//********************************************************

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;

public class DisplayWeather
{
    private Image bg, tree, cloud, fog, sun, shade, bolt, haze, smoke, weatherIcon[];

    private String weatherData = "";
    private String date = "";

    private int width = 0, height = 0;

    private int type, view, dateIndex;
    private int tWidth, tAscent;

    private WeatherForecast parent;

    private MediaTracker mt;

    private FontMetrics fm;

    private Snow snowFlake[];
    private Rain rainDrop[];
    private Hail hail[];

    private int flakeNum = 1000;
    private int snowDensity = 20;
    private int snowSpeed = 2;

    private int rainNum = 100;
    private int rainDensity = 4;
    private int rainSpeed = 2;

    private int hailNum = 200;
    private int hailDensity = 4;
    private int hailSpeed = 4;

    private int windDir = 0;

    private int cloudDensity = 1;

    private float boltVisibility = 0;
    private int boltPos = 0;

    private boolean snowing, raining, sunny, day, cloudy, foggy, windy, lightning, hailing, hazy, smoky;
    private boolean unavailable;

    public DisplayWeather( String weatherData, int type, int view, int dateIndex, WeatherForecast parent )
    {
        this.weatherData = weatherData;
        this.type = type;
        this.view = view;
        this.dateIndex = dateIndex;
        this.parent = parent;

        setWeather();
    }

    public void display( Graphics2D g )
    {
        fm = g.getFontMetrics( parent.getFont() );
        int tAscent = fm.getAscent();
        int nextY, nextX;

        if( type == 0 && view == 0 )
        {
            String data[] = weatherData.split( "&" );

            g.setColor( Color.black );
            g.drawRect( width / 2 - 100, 10, 200, 62 + tAscent );
            g.drawImage( weatherIcon[ 0 ], width / 2 - 52 / 2, 15, 52, 52, parent );

            nextY = 82 + 2 * tAscent;

            for( int i = 0; i < 3; i++ )
            {
                if( data[ i ].indexOf( "°" ) < 0 && data[ i ].toLowerCase().indexOf( ".gif" ) < 0 )
                {
                    g.setColor( Color.blue );
                    g.drawString( data[ i ], width / 2 - fm.stringWidth( data[ i ] ) / 2, 67 + tAscent );
                }
                else if( data[ i ].indexOf( "°" ) > 0 )
                {
                    g.setColor( Color.black );
                    g.drawString( "Temperature:  ", 10, nextY );
                    g.setColor( Color.blue );
                    g.drawString( data[ i ], width / 2 - 40, nextY );
                }
            }

            nextY += tAscent;

            for( int i = 3; i < data.length; i++ )
            {
                if( data[ i ].toLowerCase().indexOf( "feels like" ) >= 0 )
                {
                    if( data[ i + 1 ].indexOf( "°" ) > 0 )
                        g.drawString( data[ i ] + " " + data[ i + 1 ], width / 2 - 40, nextY );
                    else
                        g.drawString( data[ i ], width / 2 - 40, nextY );

                    nextY += tAscent;
                }
                else if( data[ i ].indexOf( ":" ) == data[ i ].length() - 1 )
                {
                    g.setColor( Color.black );
                    g.drawString( data[ i ], 10, nextY );

                    if( i + 1 < data.length && data[ i + 1 ].indexOf( ":" ) < 0 )
                    {
                        i++;
                        g.setColor( Color.blue );
                        g.drawString( data[ i ], width / 2 - 40, nextY );
                    }

                    nextY += tAscent;

                    if( data[ i - 1 ].toLowerCase().indexOf( "wind:" ) >= 0 && i + 1 < data.length && data[ i + 1 ].indexOf( ":" ) < 0 )
                    {
                        i++;
                        g.drawString( data[ i ], width / 2 - 40, nextY );
                        nextY += tAscent;
                    }
                }
                else if( data[ i ].indexOf( ":" ) > 0 )
                {
                    g.setColor( Color.black );
                    g.drawString( data[ i ], width / 2 - fm.stringWidth( data[ i ] ) / 2, height - 5 - tAscent );
                }
            }
        }
        else if( type == 1 && view == 0 )
        {
            String data[] = weatherData.split( "&" );
            String data2[];
            String temp;
            int cols = 6;
            int index, k;

            g.setColor( Color.blue );
            temp = "High /";
            g.drawString( temp, 7 * width / 10 - fm.stringWidth( temp ) / 2, tAscent + 12 );
            temp = "Low (°F)";
            g.drawString( temp, 7 * width / 10 - fm.stringWidth( temp ) / 2, 2 * tAscent + 12 );
            temp = "Precip.";
            g.drawString( temp, 9 * width / 10 - fm.stringWidth( temp ) / 2, tAscent + 12 );
            temp = "%";
            g.drawString( temp, 9 * width / 10 - fm.stringWidth( temp ) / 2, 2 * tAscent + 12 );

            nextY = 30 + 2 * tAscent;

            //if( weatherIcon == null )
                //cols = 5;

            g.setColor( Color.black );

            for( int i = 0; i < data.length / cols; i++ )
            {
                for( int j = 0; j < cols; j++ )
                {
                    k = cols * i + j;
                    nextX = (j * 2 - 1) * width / 10;

                    if( j == 0 )
                        g.drawString( data[ k ], width / 10 - fm.stringWidth( data[ k ] ) / 2, nextY );
                    else if( j == 1 )
                        g.drawString( data[ k ], width / 10 - fm.stringWidth( data[ k ] ) / 2, nextY + tAscent );
                    else if( data[ k ].toLowerCase().indexOf( ".gif" ) > 0 )
                        g.drawImage( weatherIcon[ i ], 3 * width / 10 - 31 / 2, nextY - tAscent, tAscent * 2, tAscent * 2, null );
                    else
                    {
                        if( fm.stringWidth( data[ k ] ) > width / 10 )
                        {
                            index = data[ k ].indexOf( "/" );

                            if( index > 0 )
                            {
                                temp = data[ k ].substring( 0, index ).trim();
                                g.drawString( temp + "/", nextX - fm.stringWidth( temp + "/" ) / 2, nextY );
                                temp = data[ k ].substring( index + 1 ).trim();
                                g.drawString( temp, nextX - fm.stringWidth( temp ) / 2, nextY + tAscent );
                            }
                            else
                            {
                                index = data[ k ].indexOf( " " );

                                if( index > 0 )
                                {
                                    temp = data[ k ].substring( 0, index );
                                    g.drawString( temp, nextX - fm.stringWidth( temp ) / 2, nextY );
                                    temp = data[ k ].substring( index + 1 );
                                    g.drawString( temp, nextX - fm.stringWidth( temp ) / 2, nextY + tAscent );
                                }
                                else
                                    g.drawString( data[ k ], nextX - fm.stringWidth( data[ k ] ) / 2, nextY + tAscent / 2 );
                            }
                        }
                        else
                            g.drawString( data[ k ], nextX - fm.stringWidth( data[ k ] ) / 2, nextY + tAscent / 2 );
                    }
                }

                nextY += 5 + 2 * tAscent;
            }
        }
        else
        {
            if( unavailable )
            {
                g.setColor( Color.red );
                g.drawLine( 0, 0, width, height );
                g.drawLine( width, 0, 0, height );
                g.setColor( Color.black );
                return;
            }

            g.setColor( Color.white );
            g.drawImage( bg, 0, 0, parent );
            g.drawImage( tree, 75, 80, parent );

            if( snowing )
            {
                g.setColor( Color.white );

                for( int i = 0; i < flakeNum; i++ )
                {
                    if( snowFlake[ i ].isAlive() )
                    {
                        g.fillOval( snowFlake[ i ].getX(), snowFlake[ i ].getY(), snowFlake[ i ].getSize(), snowFlake[ i ].getSize() );
                        snowFlake[ i ].setSpeed( snowSpeed );
                        snowFlake[ i ].move();
                    }
                }

                for( int i = 0; i < snowDensity; i++ )
                {
                    for( int j = 0; j < flakeNum; j++ )
                    {
                        if( snowFlake[ j ].isAlive() == false )
                        {
                            snowFlake[ j ].stopWind();
                            if( windy ) snowFlake[ j ].startWind( 10, windDir );
                            snowFlake[ j ].createSnowFlake();
                            break;
                        }
                    }
                }
            }

            if( hailing )
            {
                g.setColor( Color.white );

                for( int i = 0; i < hailNum; i++ )
                {
                    if( hail[ i ].isAlive() )
                    {
                        g.fillOval( hail[ i ].getX(), hail[ i ].getY(), hail[ i ].getSize(), hail[ i ].getSize() );
                        hail[ i ].setSpeed( hailSpeed );
                        hail[ i ].move();
                    }
                }

                for( int i = 0; i < hailDensity; i++ )
                {
                    for( int j = 0; j < hailNum; j++ )
                    {
                        if( hail[ j ].isAlive() == false )
                        {
                            hail[ j ].stopWind();
                            if( windy ) hail[ j ].startWind( 10, windDir );
                            hail[ j ].createHail();
                            break;
                        }
                    }
                }
            }

            if( raining )
            {
                for( int i = 0; i < rainNum; i++ )
                {
                    if( rainDrop[ i ].isAlive() )
                    {
                        int value = 150 - rainDrop[ i ].getDepth() * 10;
                        g.setColor( new Color( value, value, value, 255 - rainDrop[ i ].getVisibility() ) );
                        if( !windy ) windDir = 0;
                        g.drawLine( rainDrop[ i ].getX(), rainDrop[ i ].getY(), rainDrop[ i ].getX() + windDir * 7, rainDrop[ i ].getY() + 7);
                        g.fillOval( rainDrop[ i ].getX() - windDir * 2, rainDrop[ i ].getY() - 2, 1, 1 );
                        g.drawLine( rainDrop[ i ].getX() - windDir * 6, rainDrop[ i ].getY() - 6, rainDrop[ i ].getX() - windDir * 4, rainDrop[ i ].getY() - 4 );
                        g.fillOval( rainDrop[ i ].getX() - windDir * 8, rainDrop[ i ].getY() - 8, 1, 1 );
                        g.drawLine( rainDrop[ i ].getX() - windDir * 11, rainDrop[ i ].getY() - 11, rainDrop[ i ].getX() - windDir * 10, rainDrop[ i ].getY() - 10 );
                        rainDrop[ i ].fade();
                    }
                }

                for( int i = 0; i < rainDensity; i++ )
                {
                    for( int j = 0; j < rainNum; j++ )
                    {
                        if( rainDrop[ j ].isAlive() == false )
                        {
                            rainDrop[ j ].createRainDrop();
                            rainDrop[ j ].setSpeed( rainSpeed );
                            break;
                        }
                    }
                }
            }

            if( day && sunny )
                g.drawImage( sun, width - 100, -70, parent );

            if( cloudDensity >= 1 )
                g.drawImage( shade, 0, 0, parent );

            if( cloudy )
            {
                g.drawImage( cloud, -50, -60, cloud.getWidth( null ) - 50, cloud.getHeight( null ) - 60, 0, 0, cloud.getWidth( null ), cloud.getHeight( null ), parent );
                g.drawImage( cloud, width + 50 - cloud.getWidth( null ), -60, width + 50, cloud.getHeight( null ) - 60, cloud.getWidth( null ), 0, 0, cloud.getHeight( null ), parent );
            }

            if( foggy ) g.drawImage( fog, 0, 0, parent );
            if( hazy ) g.drawImage( haze, 0, 0, parent );
            if( smoky ) g.drawImage( smoke, 0, 0, parent );

            if( lightning )
            {
                if( boltVisibility <= 0f )
                {
                    if( (int)(Math.random() * 30) == 0 )
                    {
                        boltVisibility = 1f;
                        boltPos = (int)(Math.random() * 2);
                    }
                }
                else
                {
                    g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, boltVisibility ) );

                    if( boltPos == 0 )
                        g.drawImage( bolt, 0, 0, bolt.getWidth( null ), bolt.getHeight( null ), 0, 0, bolt.getWidth( null ), bolt.getHeight( null ), parent );
                    else
                        g.drawImage( bolt, width - bolt.getWidth( null ), 0, width, bolt.getHeight( null ), bolt.getWidth( null ), 0, 0, bolt.getHeight( null ), parent );

                    g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f ) );
                    boltVisibility -= 0.2f;
                }
            }
        }
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public void refresh( String weatherData, int type, int view, int dateIndex )
    {
        this.weatherData = weatherData;
        this.type = type;
        this.view = view;
        this.dateIndex = dateIndex;

        setWeather();
    }

    private void initSnow()
    {
        snowFlake = new Snow[ flakeNum ];

        for( int i = 0; i < flakeNum; i++ )
            snowFlake[ i ] = new Snow( width, height );
    }

    private void initRain()
    {
        rainDrop = new Rain[ rainNum ];

        for( int i = 0; i < rainNum; i++ )
            rainDrop[ i ] = new Rain( width, height );
    }

    private void initHail()
    {
        hail = new Hail[ hailNum ];

        for( int i = 0; i < hailNum; i++ )
            hail[ i ] = new Hail( width, height );
    }

    private void setWeather()
    {
        weatherIcon = null;

        if( type == 0 && view == 0 )
        {
            width = 300;
            height = 300;

            mt = new MediaTracker( parent );

            weatherIcon = new Image[ 1 ];

            String data[] = weatherData.split( "&" );

            for( int i = 0; i < data.length; i++ )
            {
                if( data[ i ].toLowerCase().indexOf( ".gif" ) > 0 )
                {
                    weatherIcon[ 0 ] = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/ICONS/" + data[ i ] );
                    break;
                }
            }

            mt.addImage( weatherIcon[ 0 ], 0 );

            try
            {
                mt.waitForAll();
            }
            catch( InterruptedException e ){}
        }
        else if( type == 1 && view == 0 )
        {
            int numImages = 0;
            String temp = "";

            width = 450;
            height = 400;

            mt = new MediaTracker( parent );

            String data[] = weatherData.split( "&" );

            for( int i = 0; i < data.length; i++ )
            {
                if( data[ i ].toLowerCase().indexOf( ".gif" ) > 0 )
                {
                    numImages++;
                    temp += data[ i ] + "&";
                }
            }

            if( numImages > 0 )
            {
                String images[] = temp.split( "&" );
                weatherIcon = new Image[ numImages ];

                for( int i = 0; i < numImages; i++ )
                {
                    weatherIcon[ i ] = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/ICONS/" + images[ i ] );
                    mt.addImage( weatherIcon[ i ], 0 );
                }

                try
                {
                    mt.waitForAll();
                }
                catch( InterruptedException e ){}
            }
        }
        else
        {
            int iconNum = -1, index;
            String data[] = weatherData.split( "&" );

            if( type == 0 ) dateIndex = 0;

            for( int i = dateIndex * 6; i < data.length; i++ )
            {
                index = data[ i ].toLowerCase().indexOf( ".gif" );

                if( index > 0 )
                {
                    String temp = data[ i ].substring( 0, index ).trim();

                    if( Character.isDigit( temp.charAt( 0 ) ) )
                        iconNum = Integer.valueOf( temp );

                    break;
                }
            }

            if( iconNum < 0 )
            {
                unavailable = true;
                width = 400;
                height = 300;
                return;
            }
            else
                unavailable = false;

            sunny = cloudy = raining = snowing = foggy = windy = lightning = hailing = false;
            hazy = smoky = false;
            cloudDensity = 0;

            day = isDayTime();

            switch( iconNum )
            {
                case 1:  case 2:
                    windy = true;
                case 0:  case 3:  case 4:  case 17:  case 35:
                    raining = true;
                    rainDensity = 30;
                    rainSpeed = 5;
                    cloudy = true;
                    cloudDensity = 2;
                    lightning = true;
                    break;
                case 5:
                    raining = true;
                    rainDensity = 4;
                    rainSpeed = 5;
                    snowing = true;
                    snowDensity = 10;
                    cloudy = true;
                    cloudDensity = 0;
                    break;
                case 6:
                    raining = true;
                    rainDensity = 10;
                    rainSpeed = 3;
                case 7:
                    windy = true;
                    raining = true;
                    rainDensity = 4;
                    rainSpeed = 5;
                    snowing = true;
                    snowDensity = 10;
                    cloudy = true;
                    cloudDensity = 1;
                    break;
                case 18:
                    hailing = true;
                    cloudy = true;
                    cloudDensity = 1;
                    break;
                case 9:
                    raining = true;
                    rainDensity = 4;
                    rainSpeed = 2;
                    cloudy = true;
                    cloudDensity = 1;
                    hazy = true;
                    break;
                case 8:
                    hazy = true;
                case 10:
                    raining = true;
                    rainDensity = 4;
                    rainSpeed = 5;
                    cloudy = true;
                    cloudDensity = 1;
                    break;
                case 11:
                    raining = true;
                    rainDensity = 6;
                    rainSpeed = 3;
                    cloudy = true;
                    cloudDensity = 1;
                    break;
                case 12:
                    raining = true;
                    rainDensity = 15;
                    rainSpeed = 3;
                    cloudy = true;
                    cloudDensity = 1;
                    break;
                case 13:
                    snowing = true;
                    snowDensity = 5;
                    cloudy = true;
                    cloudDensity = 0;
                    break;
                case 14:
                    snowing = true;
                    snowDensity = 15;
                    cloudy = true;
                    cloudDensity = 0;
                    break;
                case 15:
                    windy = true;
                    snowing = true;
                    snowDensity = 15;
                    break;
                case 16:  case 43:
                    windy = true;
                case 42:
                    snowing = true;
                    snowDensity = 20;
                    cloudy = true;
                    cloudDensity = 0;
                    break;
                case 19:  case 22:
                    day = true;
                    sunny = true;
                    smoky = true;
                    break;
                case 20:
                    foggy = true;
                    break;
                case 21:
                    day = true;
                    sunny = true;
                    hazy = true;
                    break;
                case 23:  case 24:
                    windy = true;
                    break;
                case 26:
                    cloudy = true;
                    cloudDensity = 2;
                    break;
                case 27:
                    day = false;
                    cloudy = true;
                    cloudDensity = 2;
                    break;
                case 28:
                    day = true;
                    sunny = true;
                    cloudy = true;
                    cloudDensity = 2;
                    break;
                case 29:
                    day = false;
                    cloudy = true;
                    cloudDensity = 1;
                    break;
                case 30:
                    day = true;
                    sunny = true;
                    cloudy = true;
                    cloudDensity = 1;
                    break;
                case 31:
                    day = false;
                    break;
                case 32:  case 36:
                    day = true;
                    sunny = true;
                    break;
                case 33:
                    day = false;
                    cloudy = true;
                    cloudDensity = 0;
                    break;
                case 34:
                    day = true;
                    sunny = true;
                    cloudy = true;
                    cloudDensity = 0;
                    break;
                case 37:  case 38:
                    day = true;
                    sunny = true;
                    raining = true;
                    rainDensity = 30;
                    rainSpeed = 5;
                    cloudy = true;
                    cloudDensity = 2;
                    lightning = true;
                    break;
                case 39:  case 48:
                    day = true;
                    sunny = true;
                    raining = true;
                    rainDensity = 20;
                    rainSpeed = 3;
                    cloudy = true;
                    cloudDensity = 0;
                    break;
                case 40:
                    raining = true;
                    rainDensity = 20;
                    rainSpeed = 3;
                    cloudy = true;
                    cloudDensity = 1;
                    break;
                case 41:
                    day = true;
                    sunny = true;
                    snowing = true;
                    snowDensity = 20;
                    cloudy = true;
                    cloudDensity = 0;
                    break;
                case 45:
                    day = false;
                    raining = true;
                    rainDensity = 20;
                    rainSpeed = 3;
                    cloudy = true;
                    cloudDensity = 0;
                    break;
                case 46:
                    day = false;
                    snowing = true;
                    snowDensity = 20;
                    cloudy = true;
                    cloudDensity = 0;
                    break;
                case 47:
                    day = false;
                    raining = true;
                    rainDensity = 30;
                    rainSpeed = 5;
                    cloudy = true;
                    cloudDensity = 2;
                    lightning = true;
                    break;
                default:
                    unavailable = true;
                    width = 400;
                    height = 300;
                    return;
            }

            mt = new MediaTracker( parent );

            if( !snowing && day )
            {
                bg = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/bg.png" );
                tree = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/tree.png" );
            }
            else if( !snowing && !day )
            {
                bg = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/bg2.png" );
                tree = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/tree2.png" );
            }
            else if( snowing && day )
            {
                bg = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/bg3.png" );
                tree = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/tree3.png" );
            }
            else
            {
                bg = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/bg4.png" );
                tree = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/tree4.png" );
            }

            if( cloudy && day )
                cloud = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/cloud.png" );
            else if( cloudy && !day )
                cloud = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/cloud2.png" );
            else
                cloud = null;

            if( foggy )
                fog = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/fog.png" );
            else
                fog = null;

            if( day && sunny )
                sun = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/sun.png" );
            else
                sun = null;

            if( cloudy && cloudDensity == 1 )
                shade = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/shade.png" );
            else if( cloudy && cloudDensity > 1 )
                shade = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/shade2.png" );
            else
                shade = null;

            if( lightning )
                bolt = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/bolt.png" );
            else
                bolt = null;

            if( hazy )
                haze = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/haze.png" );
            else
                haze = null;

            if( smoky )
                smoke = Toolkit.getDefaultToolkit().getImage( "IMAGES/WEATHER/smoke.png" );
            else
                smoke = null;

            mt.addImage( bg, 0 );
            mt.addImage( tree, 0 );
            if( cloudy ) mt.addImage( cloud, 0 );
            if( foggy ) mt.addImage( fog, 0 );
            if( sunny ) mt.addImage( sun, 0 );
            if( cloudDensity >= 1 ) mt.addImage( shade, 0 );
            if( lightning ) mt.addImage( bolt, 0 );
            if( hazy ) mt.addImage( haze, 0 );
            if( smoky ) mt.addImage( smoke, 0 );

            try
            {
                mt.waitForAll();
            }
            catch( InterruptedException e ){}

            width = bg.getWidth( null );
            height = bg.getHeight( null );

            if( snowing ) initSnow();
            if( raining ) initRain();
            if( hailing ) initHail();
        }
    }

    public void setDate( String date )
    {
        this.date = date;
    }

    public boolean isDayTime()
    {
        if( type == 1 )
        {
            if( date.toLowerCase().indexOf( "night" ) < 0 )
                return true;
            else
                return false;
        }
        else
        {
            if( !weatherData.equals( "" ) )
            {
                String data[] = weatherData.split( "&" );

                for( int i = 0; i < data.length; i++ )
                {
                    if( data[ i ].toLowerCase().indexOf( "update" ) >= 0 )
                    {
                        int index = data[ i ].indexOf( ":" );

                        if( index > 0 )
                        {
                            int index2 = data[ i ].indexOf( " ", index - 3 );

                            if( index2 >= 0 )
                            {
                                String temp = data[ i ].substring( index2 + 1, index );

                                if( Character.isDigit( temp.trim().charAt( 0 ) ) )
                                {
                                    int hour = Integer.valueOf( temp );
                                    temp = data[ i ].toLowerCase();

                                    if( temp.indexOf( " pm" ) > 0 || temp.indexOf( " p.m." ) > 0 )
                                    {
                                        if( hour >= 8 && hour < 12 )
                                            return false;
                                        else
                                            return true;
                                    }
                                    else if( temp.indexOf( " am" ) > 0 || temp.indexOf( " a.m." ) > 0 )
                                    { 
                                        if( hour == 12 )
                                            return false;
                                        else if( hour >= 1 && hour <= 5 )
                                            return false;
                                        else
                                            return true;
                                    }
                                }
                            }
                        }

                        break;
                    }
                }
            }
        }

        return true;
    }
}
    