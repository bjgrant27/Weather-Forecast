//********************************************************
//Brandon Grant
//Weather Forecast
//City.java
//********************************************************

import java.awt.*;

public class City
{
    private double latitude, longitude;
    private String cname, sname, zip;

    public City()
    {
        cname = "";
        sname = "";
        latitude = 0;
        longitude = 0;
        zip = "";
    }

    public City( String cname, String sname, double latitude, double longitude, String zip )
    {
        this.cname = cname;
        this.sname = sname;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zip = zip;
    }

    public String getName()
    {
        return cname;
    }

    public String getState()
    {
        return sname;
    }

    public double getLat()
    {
        return latitude;
    }

    public double getLong()
    {
        return longitude;
    }

    public String getZip()
    {
        return zip;
    }

    public void setName( String cname )
    {
        this.cname = cname;
    }

    public void setState( String sname )
    {
        this.sname = sname;
    }

    public void setLat( double latitude )
    {
        this.latitude = latitude;
    }

    public void setLong( double longitude )
    {
        this.longitude = longitude;
    }

    public void setZip( String zip )
    {
        this.zip = zip;
    }
}
    