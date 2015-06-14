//********************************************************
//Brandon Grant
//Weather Forecast
//State.java
//********************************************************

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;

public class State
{
    private Image map;

    private String classPath;
    private String stateData;
    private String cityData;

    private double leftBound, rightBound, topBound, bottomBound;
    private double longSpan, latSpan;

    private int mapWidth, mapHeight;

    private int cityNum = 0;
    private int chosenCity = -1;
    private City[] city;
    private int cityX, cityY;

    private String cityName;
    private String stateName;
    private String stateAbbr;

    private int hdir, vdir;
    private int tWidth, tAscent;

    private WeatherForecast parent;

    private MediaTracker mt;

    private FontMetrics fm;

    public State( String stateName, String stateAbbr, WeatherForecast parent )
    {
        this.stateName = stateName;
        this.stateAbbr = stateAbbr;
        this.parent = parent;

        if( stateName.equals( "" ) || stateAbbr.equals( "" ) )
            return;

        mt = new MediaTracker( parent );

        map = Toolkit.getDefaultToolkit().getImage( "IMAGES/MAPS/" + stateName + ".gif" );

        mt.addImage( map, 0 );

        try
        {
            mt.waitForAll();
        }
        catch( InterruptedException e ){}

        mapWidth = map.getWidth( null );
        mapHeight = map.getHeight( null );

        stateData = "STATES/" + stateName + ".txt";
        cityData = "STATES/CITIES/" + stateName + " Cities.txt";

        try
        {
            FileReader fr = new FileReader( stateData );
            BufferedReader br = new BufferedReader( fr );
            String line = br.readLine();
            leftBound = Double.valueOf( line.trim() ).doubleValue();
            line = br.readLine();
            rightBound = Double.valueOf( line.trim() ).doubleValue();
            line = br.readLine();
            topBound = Double.valueOf( line.trim() ).doubleValue();
            line = br.readLine();
            bottomBound = Double.valueOf( line.trim() ).doubleValue();
            br.close();
        }
        catch( IOException e ){}

        longSpan = leftBound - rightBound;
        latSpan = topBound - bottomBound;

        try
        {
            FileReader fr = new FileReader( cityData );
            BufferedReader br = new BufferedReader( fr );

            String line = br.readLine();

            while( line != null )
            {
                cityNum++;
                line = br.readLine();
            }

            city = new City[ cityNum ];

            br.close();

            fr = new FileReader( cityData );
            br = new BufferedReader( fr );

            for( int i = 0; i < city.length; i++ )
            {
                line = br.readLine();

                String[] cityParts = line.split( "  " );

                if( cityParts.length < 4 )
                    city[ i ] = new City( cityParts[ 2 ], stateAbbr, Double.valueOf( cityParts[ 0 ].trim() ).doubleValue(), Double.valueOf( cityParts[ 1 ].trim() ).doubleValue(), "" );
                else
                    city[ i ] = new City( cityParts[ 2 ], stateAbbr, Double.valueOf( cityParts[ 0 ].trim() ).doubleValue(), Double.valueOf( cityParts[ 1 ].trim() ).doubleValue(), cityParts[ 3 ] );
            }

            br.close();
        }
        catch( IOException e ){}
    }

    public void display( Graphics2D g, int mouseX, int mouseY, boolean showNames )
    {
        fm = g.getFontMetrics( parent.getFont() );

        g.drawImage( map, 0, 0, parent );

        chosenCity = -1;
        
        for( int i = 0; i < city.length; i++ )
        {
            cityX = longToX( city[ i ].getLong() );
            cityY = latToY( city[ i ].getLat() );
            cityName = city[ i ].getName();

            tWidth = fm.stringWidth( cityName );
            tAscent = fm.getAscent();

            hdir = 0;
            vdir = -1;

            if( showNames )
            {
                if( cityX + tWidth + 5 >= mapWidth )
                    hdir = 1;

                if( cityY - tAscent - 5 <= 0 )
                    vdir = 1;

                g.setColor( Color.blue );
            }

            double xDist = Math.abs( XtoLong( 3 ) - XtoLong( 1 ) );
            double yDist = Math.abs( YtoLat( 3 ) - YtoLat( 1 ) );

            if( XtoLong( mouseX ) >= city[ i ].getLong() - xDist && XtoLong( mouseX ) <= city[ i ].getLong() + xDist && YtoLat( mouseY ) >= city[ i ].getLat() - yDist && YtoLat( mouseY ) <= city[ i ].getLat() + yDist )
                chosenCity = i;
            else
            {
                if( showNames )
                {
                    if( vdir == -1 )
                        g.drawRect( cityX - hdir * (tWidth + 5), cityY - tAscent - 5, tWidth + 5, tAscent + 2 );
                    else
                        g.drawRect( cityX - hdir * (tWidth + 5), cityY + 5, tWidth + 5, tAscent + 2 );

                    g.drawString( cityName, cityX - hdir * (tWidth + 5) + (tWidth + 5) / 2 - tWidth / 2, cityY + vdir * (5 + (tAscent + 2) / 2) + tAscent / 2 );
                }
                g.setColor( Color.black );
                g.fillOval( cityX - 2, cityY - 2, 4, 4 );
            }
        }

        if( chosenCity >= 0 )
        {
            cityX = longToX( city[ chosenCity ].getLong() );
            cityY = latToY( city[ chosenCity ].getLat() );
            cityName = city[ chosenCity ].getName();

            tWidth = fm.stringWidth( cityName );
            tAscent = fm.getAscent();

            hdir = 0;
            vdir = -1;

            if( cityX + tWidth + 5 >= mapWidth )
                hdir = 1;

            if( cityY - tAscent - 5 <= 0 )
                vdir = 1;

            g.setColor( Color.yellow );

            if( vdir == -1 )
            {
                g.fillRect( cityX - hdir * (tWidth + 5), cityY - tAscent - 5, tWidth + 5, tAscent + 2 );
                g.setColor( Color.blue );
                g.drawRect( cityX - hdir * (tWidth + 5), cityY - tAscent - 5, tWidth + 5, tAscent + 2 );
            }
            else
            {
                g.fillRect( cityX - hdir * (tWidth + 5), cityY + 5, tWidth + 5, tAscent + 2 );
                g.setColor( Color.blue );
                g.drawRect( cityX - hdir * (tWidth + 5), cityY + 5, tWidth + 5, tAscent + 2 );
            }

            g.drawString( cityName, cityX - hdir * (tWidth + 5) + (tWidth + 5) / 2 - tWidth / 2, cityY + vdir * (5 + (tAscent + 2) / 2) + tAscent / 2 );
            g.setColor( Color.red );
            g.fillOval( cityX - 2, cityY - 2, 4, 4 );
        }
    }

    public String getCityData( String name, String zip, int type )
    {
        String data = "";

        URL url;

        try
        {
            if( zip.equals( "" ) )
                url = new URL( "http://zipinfo.com/cgi-local/zipsrch.exe?ll=ll&zip=" + name.replace( " ", "+" ) + "," + stateAbbr );
            else
                url = new URL( "http://zipinfo.com/cgi-local/zipsrch.exe?ll=ll&zip=" + zip );

            BufferedReader br = new BufferedReader( new InputStreamReader( url.openStream() ) );

            String line = br.readLine();

            while( line != null )
            {
                if( !line.equals( "" ) )
                    data += line;

                line = br.readLine();
            }
        } catch( Exception e )
        {
            return "";
        }

        int index = data.toLowerCase().indexOf( "(west)" );

        if( index < 0 )
            return "";

        int index2 = data.toLowerCase().indexOf( "</table>", index );

        if( index2 < 0 )
            return "";

        data = data.substring( index + 6, index2 );

        index = data.indexOf( "<" );

        while( index >= 0 )
        {
            index2 = data.indexOf( ">" );

            if( index2 > 0 )
                data = data.substring( 0, index ) + "&" + data.substring( index2 + 1 );

            index = data.indexOf( "<" );
        }

        index = data.indexOf( "&&" );

        while( index >= 0 )
        {
            data = data.replace( "&&", "&" );
            index = data.indexOf( "&&" );
        }

        index = data.indexOf( "  " );

        while( index >= 0 )
        {
            data = data.replace( "  ", " " );
            index = data.indexOf( "  " );
        }

        data = data.trim();

        if( !data.equals( "" ) && data.indexOf( "&" ) == 0 )
            data = data.substring( 1 );

        if( !data.equals( "" ) && data.lastIndexOf( "&" ) == data.length() - 1 )
            data = data.substring( 0, data.length() - 1 );

        if( data.toLowerCase().indexOf( "&" + stateAbbr.toLowerCase() ) < 0 )
            return "";

        if( zip.equals( "" ) )
        {
            index = data.toLowerCase().indexOf( name.toLowerCase() );
        
            if( index < 0 )
                return "";

            data = data.substring( index - 6 );

            index2 = data.toLowerCase().indexOf( "&" + stateAbbr.toLowerCase() + "&", index + name.length() + 2 );

            if( index2 > 0 )
            {
                index = data.lastIndexOf( "&", index2 - 1 );
                index = data.lastIndexOf( "&", index - 2 );

                if( index > 0 )
                    data = data.substring( 0, index );
            }
        }

        String data2[];

        data2 = data.split( "&" );

        if( !zip.equals( "" ) )
        {
            String temp = data2[ 0 ];
            data2[ 0 ] = data2[ 2 ];
            data2[ 2 ] = temp;
            temp = data2[ 1 ];
            data2[ 1 ] = data2[ 2 ];
            data2[ 2 ] = temp;
        }

        if( type == 5 )
            return data2[ 0 ] + "&" + data2[ 1 ] + "&" + data2[ 2 ] + "&" + data2[ 3 ] + "&" + data2[ 4 ];
        else
            return data2[ type ];
    }

    public boolean addZip()
    {
        try
        {
            FileReader fr = new FileReader( cityData );
            BufferedReader br = new BufferedReader( fr );
            FileWriter fw = new FileWriter( "STATES/CITIES/Temp Cities.txt" );
            BufferedWriter bw = new BufferedWriter( fw );

            String line = br.readLine();

            while( line != null )
            {
                String[] cityParts = line.split( "  " );
                String cityZip = getCityData( cityParts[ 2 ], "", 0 );
                
                if( cityZip == "" )
                    return false;

                bw.write( line + "  " + cityZip );

                line = br.readLine();

                if( line != null )
                    bw.newLine();
            }

            br.close();
            bw.close();

            return true;
        }
        catch( IOException e ){ return false; }
    }

    public double XtoLong( int x )
    {
        x =  Math.abs( x - mapWidth );
        return Math.round( (x / (double)mapWidth * longSpan + rightBound) * 100.0 ) / 100.0;
    }

    public double YtoLat( int y )
    {
        y = Math.abs( y - mapHeight );
        return Math.round( (y / (double)mapHeight * latSpan + bottomBound) * 100.0 ) / 100.0;
    }

    public int longToX( double lg )
    {
        return (int)Math.round((leftBound - lg) / longSpan * mapWidth);
    }

    public int latToY( double lt )
    {
        return (int)Math.round((topBound - lt) / latSpan * mapHeight);
    }

    public String getName()
    {
        return stateName;
    }

    public String getAbbr()
    {
        return stateAbbr;
    }

    public int getWidth()
    {
        return mapWidth;
    }

    public int getHeight()
    {
        return mapHeight;
    }

    public City getCurrentCity()
    {
        if( chosenCity < 0 )
            return null;
        else
            return city[ chosenCity ];
    }

    public boolean overCity()
    {
        if( chosenCity < 0 )
            return false;
        else
            return true;
    }

    public boolean addCity( City newCity )
    {
        for( int i = 0; i < cityNum; i++ )
        {
            if( city[ i ].getZip().equals( newCity.getZip() ) )
                return false;
        }

        if( newCity.getLong() < leftBound )
            newCity.setLong( leftBound );
        else if( newCity.getLong() > rightBound )
            newCity.setLong( rightBound );
        if( newCity.getLat() > topBound )
            newCity.setLat( topBound );
        else if( newCity.getLat() < bottomBound )
            newCity.setLat( bottomBound );

        String data;

        String temp = Double.toString( newCity.getLat() );

        if( temp.indexOf( "." ) < 0 )
            data = temp + ".00";
        else if( temp.indexOf( "." ) == temp.length() - 2 )
            data = temp + "0";
        else
            data = temp;

        temp = Double.toString( newCity.getLong() );

        if( temp.indexOf( "." ) < 0 )
            data += "  " + temp + ".00";
        else if( temp.indexOf( "." ) == temp.length() - 2 )
            data += "  " + temp + "0";
        else
            data += "  " + temp;

        data += "  " + newCity.getName() + "  " + newCity.getZip();

        try
        {
            FileWriter fw = new FileWriter( cityData, true );
            BufferedWriter bw = new BufferedWriter( fw );

            bw.newLine();
            bw.append( data );
            bw.flush();
            bw.close();
        }
        catch( IOException e ){ return false; }

        City tempCity[] = new City[ cityNum + 1 ];

        for( int i = 0; i < cityNum; i++ )
            tempCity[ i ] = city[ i ];

        tempCity[ cityNum ] = newCity;
        cityNum++;
        city = tempCity;

        return true;
    }
}
    