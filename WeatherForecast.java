//********************************************************
//Brandon Grant
//Weather Forecast
//WeatherForecast.java
//********************************************************

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.*;
import java.io.*;
import java.text.*;
import javax.swing.*;
import javax.swing.text.*;

public class WeatherForecast extends JPanel implements Runnable, ActionListener, MouseListener, MouseMotionListener
{
    int appWidth, appHeight;
    int mouseX = 0, mouseY = 0, cursor = 0;
    int sColor = 0, cColor = 0;
    int weatherType = 0, weatherView = 0;
    int dateIndex = 0;
    int curInterval = 0, refInterval = 0;

    String overState = "";
    String chosenState = "";
    String weatherData = "";
    String cityData = "";
    String testFile = "";

    boolean waiting = false;
    boolean newCity = false;
    boolean searchWeather = false;
    boolean showWeather = false;
    boolean searchCity = false;
    boolean addZip = false;
    boolean editSets = false;

    boolean startup = true, updateList = true;
    boolean suShowWeather = false, suShowCities = false;
    String suCity, suState, suZip;
    int suWeatherType, suWeatherView;

    BufferedImage offscreenImage;
    Image map;
    Graphics2D offscreenGraphics;

    int mapWidth, mapHeight;

    Thread mainThread;
    Thread tempThread;

    FontMetrics fm;

    MediaTracker mt;

    State stateObj;
    City cityObj;
    DisplayWeather weatherObj;

    JFrame topFrame;
    JButton btnNewCity, btnWeatherReport, btnCurrentWeather, btnTenDayWeather, btnReturnUS, btnFindCity, btnAddCity;
    JButton btnReturn, btnGraphicalView, btnTextView, btnRefresh, btnAddZip, btnEditSets, btnSaveSets, btnRestSets;
    JTextField txtCity, txtCity2;
    JFormattedTextField txtState, txtState2, txtZip, txtZip2;
    JCheckBox chkShowCities, chkShowWeather, chkTimedRefresh;
    JLabel lblStatus;
    JComboBox cboDays, cboIntervals;
    JFileChooser jfcTest;
    JRadioButton rdbCurrentWeather, rdbTenDayWeather, rdbTextView, rdbGraphicalView;
    ButtonGroup grpType, grpView;
    Timer tmrStart, tmrRefresh;

    public WeatherForecast( JFrame frame )
    {
        topFrame = frame;

        mt = new MediaTracker( this );

        map = Toolkit.getDefaultToolkit().getImage( "IMAGES/MAPS/USA.gif" );
        map = map.getScaledInstance( (int)(0.75 * 640), (int)(0.75 * 480), Image.SCALE_SMOOTH );

        mt.addImage( map, 0 );

        try
        {
            mt.waitForAll();
        }
        catch( InterruptedException e ){}

        mapWidth = map.getWidth( null );
        mapHeight = map.getHeight( null );

        appWidth = mapWidth + 200;
        appHeight = mapHeight + 35;

        setSize( appWidth, appHeight );

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        topFrame.setLocation( screenSize.width / 2 - appWidth / 2, screenSize.height / 2 - appHeight / 2 );

        offscreenImage = new BufferedImage( appWidth, 480, BufferedImage.TYPE_INT_RGB );
        offscreenGraphics = offscreenImage.createGraphics();

        setLayout( null );

        btnNewCity = new JButton( "Add New City" );
        btnNewCity.setBounds( -100, -100, 175, 20 );
        btnNewCity.addActionListener( this );
        btnWeatherReport = new JButton( "Get Weather Report" );
        btnWeatherReport.setBounds( -100, -100, 175, 20 );
        btnWeatherReport.addActionListener( this );
        btnCurrentWeather = new JButton( "Get Current Weather" );
        btnCurrentWeather.setBounds( -100, -100, 175, 20 );
        btnCurrentWeather.addActionListener( this );
        btnTenDayWeather = new JButton( "Get 10-Day Forecast" );
        btnTenDayWeather.setBounds( -100, -100, 175, 20 );
        btnTenDayWeather.addActionListener( this );
        btnReturnUS = new JButton( "Return to USA Map" );
        btnReturnUS.setBounds( -100, -100, 175, 20 );
        btnReturnUS.addActionListener( this );
        btnFindCity = new JButton( "Find City" );
        btnFindCity.setBounds( -100, -100, 81, 20 );
        btnFindCity.addActionListener( this );
        btnAddCity = new JButton( "Add City" );
        btnAddCity.setBounds( -100, -100, 80, 20 );
        btnAddCity.addActionListener( this );
        btnReturn = new JButton( "Return to Map" );
        btnReturn.setBounds( -100, -100, 175, 20 );
        btnReturn.addActionListener( this );
        btnGraphicalView = new JButton( "Graphical View" );
        btnGraphicalView.setBounds( -100, -100, 175, 20 );
        btnGraphicalView.addActionListener( this );
        btnTextView = new JButton( "Text View" );
        btnTextView.setBounds( -100, -100, 175, 20 );
        btnTextView.addActionListener( this );
        btnRefresh = new JButton( "Refresh" );
        btnRefresh.setBounds( -100, -100, 175, 20 );
        btnRefresh.addActionListener( this );
        btnAddZip = new JButton( "Add Zip Codes" );
        btnAddZip.setBounds( -100, -100, 175, 20 );
        btnAddZip.addActionListener( this );
        btnEditSets = new JButton( "Edit Settings" );
        btnEditSets.setBounds( -100, -100, 175, 20 );
        btnEditSets.addActionListener( this );
        btnSaveSets = new JButton( "Save Settings" );
        btnSaveSets.setBounds( -100, -100, 165, 20 );
        btnSaveSets.addActionListener( this );
        btnRestSets = new JButton( "Restore Settings" );
        btnRestSets.setBounds( -100, -100, 165, 20 );
        btnRestSets.addActionListener( this );

        txtCity = new JTextField();
        txtCity.setBounds( -100, -100, 70, 20 );
        txtCity2 = new JTextField();
        txtCity2.setBounds( -100, -100, 60, 20 );
        try
        {
            txtState = new JFormattedTextField( new MaskFormatter( "UU" ) );
            txtState.setFocusLostBehavior( JFormattedTextField.PERSIST );
            txtState.setBounds( -100, -100, 35, 20 );
            txtState2 = new JFormattedTextField( new MaskFormatter( "UU" ) );
            txtState2.setFocusLostBehavior( JFormattedTextField.PERSIST );
            txtState2.setBounds( -100, -100, 35, 20 );
            txtZip = new JFormattedTextField( new MaskFormatter( "#####" ) );
            txtZip.setFocusLostBehavior( JFormattedTextField.PERSIST );
            txtZip.setBounds( -100, -100, 60, 20 );
            txtZip2 = new JFormattedTextField( new MaskFormatter( "#####" ) );
            txtZip2.setFocusLostBehavior( JFormattedTextField.PERSIST );
            txtZip2.setBounds( -100, -100, 40, 20 );
        }
        catch( Exception e ){}

        chkShowCities = new JCheckBox( "Always show city names", true );
        chkShowCities.setBackground( Color.white );
        chkShowCities.setBounds( -100, -100, 165, 20 );
        chkShowWeather = new JCheckBox( "Get weather on startup", true );
        chkShowWeather.setBackground( Color.white );
        chkShowWeather.setBounds( -100, -100, 170, 20 );
        chkShowWeather.addActionListener( this );
        chkTimedRefresh = new JCheckBox( "Timed Refresh", false );
        chkTimedRefresh.setBackground( Color.white );
        chkTimedRefresh.setBounds( -100, -100, 165, 20 );
        chkTimedRefresh.addActionListener( this );

        lblStatus = new JLabel( "" );
        lblStatus.setBounds( -100, -100, 10, 20 );

        cboDays = new JComboBox();
        cboDays.setEditable( false );
        cboDays.setBounds( -100, -100, 175, 20 );
        cboDays.addActionListener( this );
        cboIntervals = new JComboBox();
        cboIntervals.setEditable( false );
        cboIntervals.setBounds( -100, -100, 50, 20 );
        for( int i = 5; i <= 60; i += 5 )
            cboIntervals.addItem( Integer.toString( i ) );
        cboIntervals.addActionListener( this );

        jfcTest = new JFileChooser( "IMAGES/WEATHER/TEST" );

        rdbCurrentWeather = new JRadioButton( "Current" );
        rdbCurrentWeather.setBackground( Color.white );
        rdbCurrentWeather.setBounds( -100, -100, 75, 20 );
        rdbTenDayWeather = new JRadioButton( "Ten-Day" );
        rdbTenDayWeather.setBackground( Color.white );
        rdbTenDayWeather.setBounds( -100, -100, 75, 20 );
        rdbTextView = new JRadioButton( "Text" );
        rdbTextView.setBackground( Color.white );
        rdbTextView.setBounds( -100, -100, 70, 20 );
        rdbGraphicalView = new JRadioButton( "Graphical" );
        rdbGraphicalView.setBackground( Color.white );
        rdbGraphicalView.setBounds( -100, -100, 80, 20 );

        grpType = new ButtonGroup();
        grpType.add( rdbCurrentWeather );
        grpType.add( rdbTenDayWeather );
        grpView = new ButtonGroup();
        grpView.add( rdbTextView );
        grpView.add( rdbGraphicalView );

        tmrRefresh = new Timer( 60000, this );

        add( btnNewCity );
        add( btnWeatherReport );
        add( btnCurrentWeather );
        add( btnTenDayWeather );
        add( btnReturnUS );
        add( btnFindCity );
        add( btnAddCity );
        add( btnReturn );
        add( btnGraphicalView );
        add( btnTextView );
        add( btnRefresh );
        add( btnAddZip );
        add( btnEditSets );
        add( btnSaveSets );
        add( btnRestSets );
        add( txtCity );
        add( txtCity2 );
        add( txtState );
        add( txtState2 );
        add( txtZip );
        add( txtZip2 );
        add( chkShowCities );
        add( chkShowWeather );
        add( chkTimedRefresh );
        add( lblStatus );
        add( cboDays );
        add( cboIntervals );
        add( rdbCurrentWeather );
        add( rdbTenDayWeather );
        add( rdbTextView );
        add( rdbGraphicalView );

        this.setFocusable( true );
        this.addMouseListener( this );
        this.addMouseMotionListener( this );

        loadSettings();

        if( suShowWeather )
        {
            tmrStart = new Timer( 500, this );
            tmrStart.start();
        }

        mainThread = new Thread( this, "mainThread" );
        mainThread.start();
    }

    public static void main( String args[] )
    {
        JFrame frame = new JFrame();
        frame.setResizable( false );
        frame.setTitle( "Weather Forecast" );
        frame.setLayout( null );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.getContentPane().add( new WeatherForecast( frame ) );
        frame.setVisible( true );
    }

    public void paintComponent( Graphics g )
    {
        super.paintComponent( g );
        Graphics2D g2 = (Graphics2D)g;

        offscreenGraphics.setColor( Color.white );
        offscreenGraphics.fillRect( 0, 0, appWidth, 480 );

        fm = g.getFontMetrics( getFont() );

        int tWidth;
        int tAscent = fm.getAscent();
        String text;

        if( waiting )
        {
        }
        else if( searchWeather )
        {
            if( weatherData.equals( "" ) && cityObj.getZip().equals( "" ) )
                lblStatus.setText( "City information could not be found." );
            else if( weatherData.equals( "" ) )
                lblStatus.setText( "Weather information could not be found." );
            else
            {
                if( weatherObj == null )
                    weatherObj = new DisplayWeather( weatherData, weatherType, weatherView, dateIndex, this );
                else
                    weatherObj.refresh( weatherData, weatherType, weatherView, dateIndex );

                appWidth = weatherObj.getWidth() + 200;
                appHeight = weatherObj.getHeight() + 30;
                setSize( appWidth, appHeight );

                resetComponents();

                lblStatus.setText( "Weather information has been found." );

                showWeather = true;

                text = "";

                if( !cityObj.getName().equals( "" ) )
                {
                    text = cityObj.getName();

                    if( !cityObj.getState().trim().equals( "" ) && cityObj.getState().indexOf( " " ) < 0 )
                        text += ", " + cityObj.getState();

                    text += " ";
                }

                text += cityObj.getZip();

                topFrame.setTitle( "Viewing Weather for " + text );
            }

            searchWeather = false;
        }
        else if( searchCity )
        {
            if( cityData.equals( "" ) )
                lblStatus.setText( "City information could not be found." );
            else
            {
                lblStatus.setText( "City information has been found." );

                String data[] = cityData.split( "&" );

                cityObj.setZip( data[ 0 ] );
                cityObj.setName( data[ 1 ] );
                cityObj.setState( data[ 2 ] );
                cityObj.setLat( Math.round( Double.valueOf( data[ 3 ].trim() ).doubleValue() * 100.0 ) / 100.0 );
                cityObj.setLong( Math.round( Double.valueOf( data[ 4 ].trim() ).doubleValue() * 100.0 ) / 100.0 );

                if( cityObj.getLong() > 0 )
                    cityObj.setLong( -cityObj.getLong() );

                btnAddCity.setEnabled( true );
            }

            searchCity = false;
        }
        else if( addZip )
        {
            if( cityData.toLowerCase().equals( "true" ) )
                lblStatus.setText( "Zip codes have been added." );
            else
                lblStatus.setText( "City information could not be found." );

            addZip = false;
        }
        else if( showWeather )
        {
            if( cboDays.getItemCount() == 0 && weatherType == 1 && weatherView == 1 )
            {
                dateIndex = 0;

                String data[] = weatherData.split( "&" );

                for( int i = 0; i < data.length; i++ )
                {
                    if( data[ i ].toLowerCase().indexOf( ".gif" ) > 0 )
                        cboDays.addItem( data[ i - 2 ] + " " + data[ i - 1 ] );
                }

                weatherObj.setDate( cboDays.getItemAt( 0 ).toString() );
                weatherObj.refresh( weatherData, weatherType, weatherView, dateIndex );
            }

            weatherObj.display( offscreenGraphics );
            offscreenGraphics.setColor( Color.white );
            offscreenGraphics.fillRect( weatherObj.getWidth(), 0, 200, 480 );
            offscreenGraphics.fillRect( 0, weatherObj.getHeight(), appWidth, 30 );
            offscreenGraphics.setColor( Color.black );
            offscreenGraphics.drawLine( weatherObj.getWidth(), 0, weatherObj.getWidth(), appHeight - 30 );
            offscreenGraphics.drawLine( 0, weatherObj.getHeight(), appWidth, weatherObj.getHeight() );

            if( weatherView == 1 )
            {
                if( weatherType == 1 )
                {
                    offscreenGraphics.setColor( Color.black );
                    text = "Date:";
                    offscreenGraphics.drawString( text, weatherObj.getWidth() + 10, tAscent + 5 );

                    cboDays.setLocation( weatherObj.getWidth() + 10, tAscent + 10 );
                    btnCurrentWeather.setLocation( weatherObj.getWidth() + 10, tAscent + 35 );
                    btnTextView.setLocation( weatherObj.getWidth() + 10, tAscent + 60 );
                    btnRefresh.setLocation( weatherObj.getWidth() + 10, tAscent + 85 );
                    btnReturn.setLocation( weatherObj.getWidth() + 10, tAscent + 110 );
                    chkTimedRefresh.setLocation( weatherObj.getWidth() + 10, tAscent + 135 );

                    if( chkTimedRefresh.isSelected() )
                    {
                        offscreenGraphics.drawRect( weatherObj.getWidth() + 15, tAscent + 160, 165, 30 );
                        offscreenGraphics.drawString( "Interval:", weatherObj.getWidth() + 20, 2 * tAscent + 165 );
                        cboIntervals.setLocation( weatherObj.getWidth() + 70, tAscent + 165 );
                        offscreenGraphics.drawString( "minutes", weatherObj.getWidth() + 125, 2 * tAscent + 165 );
                    }
                }
                else
                {
                    btnTenDayWeather.setLocation( weatherObj.getWidth() + 10, 5 );
                    btnTextView.setLocation( weatherObj.getWidth() + 10, 30 );
                    btnRefresh.setLocation( weatherObj.getWidth() + 10, 55 );
                    btnReturn.setLocation( weatherObj.getWidth() + 10, 80 );
                    chkTimedRefresh.setLocation( weatherObj.getWidth() + 10, 105 );

                    if( chkTimedRefresh.isSelected() )
                    {
                        offscreenGraphics.drawRect( weatherObj.getWidth() + 15, 130, 165, 30 );
                        offscreenGraphics.drawString( "Interval:", weatherObj.getWidth() + 20, tAscent + 135 );
                        cboIntervals.setLocation( weatherObj.getWidth() + 70, 135 );
                        offscreenGraphics.drawString( "minutes", weatherObj.getWidth() + 125, tAscent + 135 );
                    }
                }
            }
            else
            {
                if( weatherType == 1 )
                    btnCurrentWeather.setLocation( weatherObj.getWidth() + 10, 5 );
                else
                    btnTenDayWeather.setLocation( weatherObj.getWidth() + 10, 5 );

                btnGraphicalView.setLocation( weatherObj.getWidth() + 10, 30 );
                btnRefresh.setLocation( weatherObj.getWidth() + 10, 55 );
                btnReturn.setLocation( weatherObj.getWidth() + 10, 80 );
                chkTimedRefresh.setLocation( weatherObj.getWidth() + 10, 105 );

                if( chkTimedRefresh.isSelected() )
                {
                    offscreenGraphics.drawRect( weatherObj.getWidth() + 15, 130, 165, 30 );
                    offscreenGraphics.drawString( "Interval:", weatherObj.getWidth() + 20, tAscent + 135 );
                    cboIntervals.setLocation( weatherObj.getWidth() + 70, 135 );
                    offscreenGraphics.drawString( "minutes", weatherObj.getWidth() + 125, tAscent + 135 );
                }
            }

            lblStatus.setBounds( 5, weatherObj.getHeight() + 5, appWidth - 10, 20 );
        }
        else if( chosenState.equals( "" ) )
        {
            offscreenGraphics.setColor( Color.black );
            offscreenGraphics.drawImage( map, 0, 0, this );
            offscreenGraphics.drawLine( mapWidth, 0, mapWidth, appHeight - 30 );
            offscreenGraphics.drawLine( 0, appHeight - 30, appWidth, appHeight - 30 );

            text = "To view weather, click a state or";
            offscreenGraphics.drawString( text, mapWidth + 10, tAscent + 5 );
            text = "enter city information below.";
            offscreenGraphics.drawString( text, mapWidth + 10, 2 * tAscent + 5 );
            text = "City:";
            offscreenGraphics.drawString( text, mapWidth + 10, 4 * tAscent );
            text = "State:";
            offscreenGraphics.drawString( text, mapWidth + 85, 4 * tAscent );
            text = "Zip Code:";
            offscreenGraphics.drawString( text, mapWidth + 125, 4 * tAscent );

            txtCity.setBounds( mapWidth + 10, 4 * tAscent + 5, 70, 20 );
            txtState.setLocation( mapWidth + 85, 4 * tAscent + 5 );
            txtZip.setLocation( mapWidth + 125, 4 * tAscent + 5 );
            btnWeatherReport.setLocation( mapWidth + 10, 4 * tAscent + 30 );
            btnEditSets.setLocation( mapWidth + 10, 4 * tAscent + 55 );
            lblStatus.setBounds( 5, appHeight - 25, appWidth - 10, 20 );

            if( editSets )
            {
                offscreenGraphics.drawRect( mapWidth + 10, 4 * tAscent + 80, 175, 225 );
                chkShowWeather.setLocation( mapWidth + 15, 4 * tAscent + 85 );

                if( chkShowWeather.isSelected() )
                {
                    offscreenGraphics.drawRect( mapWidth + 20, 4 * tAscent + 110, 155, 40 );
                    text = "City:";
                    offscreenGraphics.drawString( text, mapWidth + 25, 5 * tAscent + 110 );
                    text = "State:";
                    offscreenGraphics.drawString( text, mapWidth + 90, 5 * tAscent + 110 );
                    text = "Zip:";
                    offscreenGraphics.drawString( text, mapWidth + 130, 5 * tAscent + 110 );

                    txtCity2.setLocation( mapWidth + 25, 5 * tAscent + 115 );
                    txtState2.setLocation( mapWidth + 90, 5 * tAscent + 115 );
                    txtZip2.setLocation( mapWidth + 130, 5 * tAscent + 115 );

                    text = "Default Weather Type:";
                    offscreenGraphics.drawString( text, mapWidth + 20, 6 * tAscent + 140 );

                    rdbCurrentWeather.setLocation( mapWidth + 20, 6 * tAscent + 145 );
                    rdbTenDayWeather.setLocation( mapWidth + 95, 6 * tAscent + 145 );

                    text = "Default Weather View:";
                    offscreenGraphics.drawString( text, mapWidth + 20, 7 * tAscent + 165 );

                    rdbTextView.setLocation( mapWidth + 20, 7 * tAscent + 170 );
                    rdbGraphicalView.setLocation( mapWidth + 95, 7 * tAscent + 170 );
                    chkShowCities.setLocation( mapWidth + 15, 7 * tAscent + 190 );
                    btnSaveSets.setLocation( mapWidth + 15, 7 * tAscent + 215 );
                    btnRestSets.setLocation( mapWidth + 15, 7 * tAscent + 240 );
                }
                else
                {
                    text = "Default Weather Type:";
                    offscreenGraphics.drawString( text, mapWidth + 20, 5 * tAscent + 105 );

                    rdbCurrentWeather.setLocation( mapWidth + 20, 5 * tAscent + 110 );
                    rdbTenDayWeather.setLocation( mapWidth + 95, 5 * tAscent + 110 );

                    text = "Default Weather View:";
                    offscreenGraphics.drawString( text, mapWidth + 20, 6 * tAscent + 130 );

                    rdbTextView.setLocation( mapWidth + 20, 6 * tAscent + 135 );
                    rdbGraphicalView.setLocation( mapWidth + 95, 6 * tAscent + 135 );
                    chkShowCities.setLocation( mapWidth + 15, 6 * tAscent + 155 );
                    btnSaveSets.setLocation( mapWidth + 15, 6 * tAscent + 180 );
                    btnRestSets.setLocation( mapWidth + 15, 6 * tAscent + 205 );
                }
            }

            if( !overState.equals( "" ) )
            {
                int hdir = 0;

                tWidth = fm.stringWidth( overState );

                if( mouseX + tWidth + 5 >= mapWidth )
                    hdir = 1;

                offscreenGraphics.setColor( Color.white );
                offscreenGraphics.fillRect( mouseX - hdir * (tWidth + 5), mouseY - tAscent - 5, tWidth + 5, tAscent + 2 );
                offscreenGraphics.setColor( Color.black );
                offscreenGraphics.drawRect( mouseX - hdir * (tWidth + 5), mouseY - tAscent - 5, tWidth + 5, tAscent + 2 );
                offscreenGraphics.drawString( overState, mouseX - hdir * (tWidth + 5) + (tWidth + 5) / 2 - tWidth / 2, mouseY - (5 + (tAscent + 2) / 2) + tAscent / 2 );
            }
        }
        else
        {
            offscreenGraphics.setColor( Color.black );
            stateObj.display( offscreenGraphics, mouseX, mouseY, chkShowCities.isSelected() );
            offscreenGraphics.setColor( Color.black );
            offscreenGraphics.drawLine( stateObj.getWidth(), 0, stateObj.getWidth(), appHeight - 30 );
            offscreenGraphics.drawLine( 0, appHeight - 30, appWidth, appHeight - 30 );
            offscreenGraphics.drawString( "Click a city to view the weather.", stateObj.getWidth() + 10, tAscent + 5 );

            btnNewCity.setLocation( stateObj.getWidth() + 10, tAscent + 15 );

            if( newCity )
            {
                offscreenGraphics.drawRect( stateObj.getWidth() + 10, tAscent + 40, 175, 4 * tAscent + 20 );
                text = "City:";
                offscreenGraphics.drawString( text, stateObj.getWidth() + 15, 4 * tAscent + 15 );
                text = "Zip Code:";
                offscreenGraphics.drawString( text, stateObj.getWidth() + 120, 4 * tAscent + 15 );

                txtCity.setBounds( stateObj.getWidth() + 15, 4 * tAscent + 20, 100, 20 );
                txtZip.setLocation( stateObj.getWidth() + 120, 4 * tAscent + 20 );
                btnFindCity.setLocation( stateObj.getWidth() + 15, 4 * tAscent + 45 );
                btnAddCity.setLocation( stateObj.getWidth() + 100, 4 * tAscent + 45 );
                btnReturnUS.setLocation( stateObj.getWidth() + 10, 4 * tAscent + 80 );
                chkShowCities.setLocation( stateObj.getWidth() + 10, 4 * tAscent + 105 );
            }
            else
            {
                btnReturnUS.setLocation( stateObj.getWidth() + 10, tAscent + 40 );
                chkShowCities.setLocation( stateObj.getWidth() + 10, tAscent + 65 );
            }

            lblStatus.setBounds( 5, appHeight - 25, appWidth - 10, 20 );
        }

        g2.drawImage( offscreenImage, 0, 0, this );
    }

    public void run()
    {
        if( Thread.currentThread().getName().equals( "tempThread" ) )
        {
            if( searchWeather )
            {
                waiting = true;

                String data[];

                if( cityObj.getZip().equals( "" ) )
                {
                    lblStatus.setText( "Searching zipinfo.com for ZIP code.  Please wait..." );

                    data = stateObj.getCityData( cityObj.getName(), "", 5 ).split( "&" );

                    if( data.length >= 3 )
                    {
                        cityObj.setZip( data[ 0 ] );
                        cityObj.setName( data[ 1 ] );
                        cityObj.setState( data[ 2 ] );
                    }
                }

                if( cityObj.getZip().equals( "" ) )
                    weatherData = "";
                else
                {
                    lblStatus.setText( "Searching weather.com.  Please wait..." );

                    if( weatherType == 1 )
                        weatherData =  getTenDayWeather( cityObj.getZip() );
                    else
                        weatherData =  getCurrentWeather( cityObj.getZip() );
                        //weatherData = "46.gif&NA&0°F&Feels Like&0°F&Updated Mar 17 05:00 p.m. ET&NA&UV Index:&NA&Wind:&NA&Humidity:&NA&Pressure:&NA&Dew Point:&NA&Visibility:&NA&NA";
                }

                waiting = false;
            }
            else if( searchCity )
            {
                waiting = true;

                lblStatus.setText( "Searching zipinfo.com for city information.  Please wait..." );

                if( cityObj.getZip().equals( "" ) )
                    cityData = stateObj.getCityData( cityObj.getName(), "", 5 );
                else
                    cityData = stateObj.getCityData( "", cityObj.getZip(), 5 );

                waiting = false;
            }
            else if( addZip )
            {
                waiting = true;
                lblStatus.setText( "Searching zipinfo.com for ZIP codes.  Please wait..." );
                cityData = Boolean.toString( stateObj.addZip() );
                waiting = false;
            }

            return;
        }

        while( mainThread != null )
        {
            try
            {
                if( Thread.currentThread().getName().equals( "tempThread" ) )
                    tempThread.join();

                mainThread.sleep( 80 );

                if( waiting == true )
                {
                    if( cursor != 1 )
                    {
                        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
                        cursor = 1;
                    }

                    continue;
                }

                if( cursor != 0 )
                {
                    setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                    cursor = 0;
                }

                topFrame.setSize( appWidth, appHeight + 35 );

                overState = "";

                if( showWeather )
                {
                }
                else if( chosenState.equals( "" ) )
                {
                    if( mouseX >= mapWidth || mouseY >= mapHeight )
                        sColor = -1;
                    else
                        sColor = offscreenImage.getRGB( mouseX, mouseY );

                    switch( sColor )
                    {
                        case -1:
                            overState = "";
                            break;
                        case -26317:
                            overState = "Alabama";
                            break;
                        case -3342388:
                            overState = "Alaska";
                            break;
                        case -16738048:
                            overState = "Arizona";
                            break;
                        case -16737793:
                            overState = "Arkansas";
                            break;
                        case -6750208:
                            overState = "California";
                            break;
                        case -16737895:
                            overState = "Colorado";
                            break;
                        case -3368653:
                            overState = "Connecticut";
                            break;
                        case -6684724:
                            overState = "Delaware";
                            break;
                        case -3368551:
                            overState = "Florida";
                            break;
                        case -6697780:
                            overState = "Georgia";
                            break;
                        case -13057:
                            overState = "Hawaii";
                            break;
                        case -6724045:
                            overState = "Idaho";
                            break;
                        case -6710785:
                            overState = "Illinois";
                            break;
                        case -13159:
                            overState = "Indiana";
                            break;
                        case -103:
                            overState = "Iowa";
                            break;
                        case -6750055:
                            overState = "Kansas";
                            break;
                        case -65383:
                            overState = "Kentucky";
                            break;
                        case -6684673:
                            overState = "Louisiana";
                            break;
                        case -52:
                            overState = "Maine";
                            break;
                        case -6697882:
                            overState = "Maryland";
                            break;
                        case -3394765:
                            overState = "Massachusetts";
                            break;
                        case -3355393:
                            overState = "Michigan";
                            break;
                        case -65281:
                            overState = "Minnesota";
                            break;
                        case -6737152:
                            overState = "Mississippi";
                            break;
                        case -16711783:
                            overState = "Missouri";
                            break;
                        case -3355495:
                            overState = "Montana";
                            break;
                        case -16776961:
                            overState = "Nebraska";
                            break;
                        case -6711040:
                            overState = "Nevada";
                            break;
                        case -26266:
                            overState = "New Jersey";
                            break;
                        case -205:
                            overState = "New York";
                            break;
                        case -3381760:
                            overState = "Oklahoma";
                            break;
                        case -10079437:
                            overState = "Oregon";
                            break;
                        case -13108:
                            overState = "Pennsylvania";
                            break;
                        case -13408666:
                            overState = "New Hampshire";
                            break;
                        case -3342337:
                            overState = "New Mexico";
                            break;
                        case -3355546:
                            overState = "North Carolina";
                            break;
                        case -16711681:
                            overState = "North Dakota";
                            break;
                        case -10053172:
                            overState = "Ohio";
                            break;
                        case -10066279:
                            overState = "Rhode Island";
                            break;
                        case -13369549:
                            overState = "South Carolina";
                            break;
                        case -16777063:
                            overState = "South Dakota";
                            break;
                        case -6749953:
                            overState = "Tennessee";
                            break;
                        case -6710989:
                            overState = "Texas";
                            break;
                        case -256:
                            overState = "Utah";
                            break;
                        case -3368449:
                            overState = "Vermont";
                            break;
                        case -52276:
                            overState = "Virginia";
                            break;
                        case -13395559:
                            overState = "Washington";
                            break;
                        case -6723892:
                            overState = "West Virginia";
                            break;
                        case -16764007:
                            overState = "Wisconsin";
                            break;
                        case -16711936:
                            overState = "Wyoming";
                            break;
                        default:
                            overState = "";
                    }

                    if( !overState.equals( "" ) )
                    {
                        if( cursor != 2 )
                        {
                            setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                            cursor = 2;
                        }
                    }
                    else
                    {
                        if( cursor != 0 )
                        {
                            setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                            cursor = 0;
                        }
                    }
                }
                else
                {
                    if( stateObj != null )
                    {
                        if( mouseX >= stateObj.getWidth() || mouseY >= stateObj.getHeight() )
                        {
                        }
                        else
                        {
                            cColor = offscreenImage.getRGB( mouseX, mouseY );

                            if( cColor == -65536 )
                            {
                                if( cursor != 2 )
                                {
                                    setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                                    cursor = 2;
                                }
                            }
                            else
                            {
                                if( cursor != 0 )
                                {
                                    setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                                    cursor = 0;
                                }
                            }
                        }
                    }
                }
            }
            catch( InterruptedException e ){}

            repaint();
        }
    }

    public void mouseClicked( MouseEvent e )
    {
        if( waiting )
            return;

        if( e.getButton() == MouseEvent.BUTTON1 )
        {
            if( showWeather )
            {
                if( weatherView == 1 )
                {
                    if( e.getClickCount() == 2 )
                    {
                        try
                        {
                            jfcTest.showOpenDialog( null );

                            if( jfcTest.getSelectedFile() != null )
                            {
                                testFile = jfcTest.getSelectedFile().getPath();

                                FileReader fr = new FileReader( testFile );
                                BufferedReader br = new BufferedReader( fr );

                                String line = br.readLine();

                                if( line.toLowerCase().indexOf( ".gif" ) > 0 )
                                    weatherObj.refresh( line, weatherType, weatherView, 0 );
                                    
                                br.close();
                            }
                        }
                        catch( Exception ex ){}
                    }
                }
            }
            else if( chosenState == "" && overState != "" )
            {
                chosenState = overState;
                overState = "";

                stateObj = new State( chosenState, getStateAbbr( chosenState ), this );

                if( cursor != 0 )
                {
                    setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                    cursor = 0;
                }

                appWidth = stateObj.getWidth() + 200;

                if( stateObj.getHeight() >= 186 )
                    appHeight = stateObj.getHeight() + 30;
                else
                    appHeight = 216;

                setSize( appWidth, appHeight );
                topFrame.setTitle( chosenState );

                resetComponents();
            }
            else
            {
                if( !chosenState.equals( "" ) && stateObj != null && mouseX < stateObj.getWidth() && mouseY < stateObj.getHeight() )
                {
                    cityObj = stateObj.getCurrentCity();

                    if( cityObj != null )
                    {
                        if( !cityObj.getZip().equals( "" ) )
                        {
                            waiting = true;
                            weatherType = suWeatherType;
                            weatherView = suWeatherView;
                            weatherData = "";
                            dateIndex = 0;
                            searchWeather = true;
                            tempThread = new Thread( this, "tempThread" );
                            tempThread.start();
                        }
                    }
                }
                else
                {
                }
            }
        }
    }

    public void mouseReleased( MouseEvent e )
    {
    }

    public void mouseEntered( MouseEvent e )
    {
    }

    public void mouseExited( MouseEvent e )
    {
    }

    public void mousePressed( MouseEvent e )
    {
    }

    public void mouseDragged( MouseEvent e )
    {
    }

    public void mouseMoved( MouseEvent e )
    {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void actionPerformed( ActionEvent e )
    {
        if( waiting )
            return;

        if( e.getSource() == btnReturnUS )
        {
            chosenState = "";

            showWeather = false;

            stateObj = null;
            weatherObj = null;

            appWidth = mapWidth + 200;
            appHeight = mapHeight + 35;

            setSize( appWidth, appHeight );
            topFrame.setTitle( "Weather Forecast" );

            resetComponents();
        }
        else if( e.getSource() == btnNewCity )
        {
            if( newCity == false )
                newCity = true;
            else
                newCity = false;

            resetComponents();
        }
        else if( e.getSource() == btnFindCity )
        {
            btnAddCity.setEnabled( false );

            if( !txtZip.getText().trim().equals( "" ) && txtZip.getText().indexOf( " " ) < 0 )
            {
                waiting = true;
                searchCity = true;
                cityObj = new City( txtCity.getText(), stateObj.getAbbr(), 0, 0, txtZip.getText() );
                tempThread = new Thread( this, "tempThread" );
                tempThread.start();
            }
            else if( !txtCity.getText().equals( "" ) )
            {
                waiting = true;
                searchCity = true;
                cityObj = new City( txtCity.getText(), stateObj.getAbbr(), 0, 0, "" );
                tempThread = new Thread( this, "tempThread" );
                tempThread.start();
            }
            else
                lblStatus.setText( "Please enter the city and/or a valid ZIP code." );
        }
        else if( e.getSource() == btnAddCity )
        {
            btnAddCity.setEnabled( false );

            if( cityObj != null )
            {
                if( stateObj.addCity( cityObj ) )
                    lblStatus.setText( "The city has been added successfully." );
                else
                    lblStatus.setText( "The city could not be added.  It may already exist." );
            }
        }
        else if( e.getSource() == btnWeatherReport )
        {
            if( !txtZip.getText().trim().equals( "" ) && txtZip.getText().indexOf( " " ) < 0 )
            {
                waiting = true;
                weatherType = suWeatherType;
                weatherView = suWeatherView;
                weatherData = "";
                dateIndex = 0;
                searchWeather = true;
                cityObj = new City( txtCity.getText(), txtState.getText(), 0, 0, txtZip.getText() );
                tempThread = new Thread( this, "tempThread" );
                tempThread.start();
            }
            else if( !txtState.getText().trim().equals( "" ) && txtState.getText().indexOf( " " ) < 0 && !txtCity.getText().equals( "" ) )
            {
                waiting = true;
                weatherType = suWeatherType;
                weatherView = suWeatherView;
                weatherData = "";
                dateIndex = 0;
                searchWeather = true;
                stateObj = new State( "", txtState.getText(), this );
                cityObj = new City( txtCity.getText(), txtState.getText(), 0, 0, "" );
                tempThread = new Thread( this, "tempThread" );
                tempThread.start();
            }
            else
                lblStatus.setText( "Please enter the city, state and/or a valid ZIP code." );
        }
        else if( e.getSource() == btnCurrentWeather )
        {
            waiting = true;
            weatherType = 0;
            weatherData = "";
            dateIndex = 0;
            searchWeather = true;
            tempThread = new Thread( this, "tempThread" );
            tempThread.start();
        }
        else if( e.getSource() == btnTenDayWeather )
        {
            waiting = true;
            weatherType = 1;
            weatherData = "";
            dateIndex = 0;
            searchWeather = true;
            tempThread = new Thread( this, "tempThread" );
            tempThread.start();
        }
        else if( e.getSource() == btnRefresh )
        {
            waiting = true;
            searchWeather = true;
            weatherData = "";
            tempThread = new Thread( this, "tempThread" );
            tempThread.start();
        }
        else if( e.getSource() == btnReturn )
        {
            showWeather = false;

            weatherObj = null;

            chkTimedRefresh.setSelected( false );
            tmrRefresh.stop();

            if( chosenState.equals( "" ) )
            {
                appWidth = mapWidth + 200;
                appHeight = mapHeight + 35;
                topFrame.setTitle( "Weather Forecast" );
            }
            else
            {
                appWidth = stateObj.getWidth() + 200;
                appHeight = stateObj.getHeight() + 30;
                topFrame.setTitle( chosenState );
            }

            setSize( appWidth, appHeight );

            resetComponents();
        }
        else if( e.getSource() == btnGraphicalView )
        {
            weatherView = 1;
            dateIndex = 0;
            weatherObj.refresh( weatherData, weatherType, weatherView, dateIndex );

            appWidth = weatherObj.getWidth() + 200;
            appHeight = weatherObj.getHeight() + 30;
            setSize( appWidth, appHeight );

            resetComponents();
        }
        else if( e.getSource() == btnTextView )
        {
            weatherView = 0;
            dateIndex = 0;
            weatherObj.refresh( weatherData, weatherType, weatherView, dateIndex );

            appWidth = weatherObj.getWidth() + 200;
            appHeight = weatherObj.getHeight() + 30;
            setSize( appWidth, appHeight );

            resetComponents();
        }
        else if( e.getSource() == btnAddZip )
        {
            addZip = true;
            tempThread = new Thread( this, "tempThread" );
            tempThread.start();
        }
        else if( e.getSource() == btnEditSets )
        {
            editSets = !editSets;
            resetComponents();
        }
        else if( e.getSource() == btnSaveSets )
        {
            if( chkShowWeather.isSelected() )
            {
                if( txtZip2.getText().trim().equals( "" ) || txtZip2.getText().indexOf( " " ) >= 0 )
                {
                    if( txtState2.getText().trim().equals( "" ) || txtState2.getText().indexOf( " " ) >= 0 || txtCity2.getText().equals( "" ) )
                    {
                        lblStatus.setText( "Please enter the city, state and/or a valid ZIP code." );
                        return;
                    }
                }
            }

            if( saveSettings() )
                lblStatus.setText( "The settings have been saved." );
            else
                lblStatus.setText( "An error has occurred while saving the settings." );
        }
        else if( e.getSource() == btnRestSets )
        {
            resetComponents();
            loadSettings();
            lblStatus.setText( "The settings have been restored." );
        }
        else if( e.getSource() == chkShowWeather )
        {
            resetComponents();
        }
        else if( e.getSource() == chkTimedRefresh )
        {
            boolean temp = chkTimedRefresh.isSelected();

            resetComponents();
            chkTimedRefresh.setSelected( temp );

            if( chkTimedRefresh.isSelected() )
                cboIntervals.setSelectedIndex( 0 );
            else
                tmrRefresh.stop();
        }
        else if( e.getSource() == cboDays )
        {
            if( cboDays.getSelectedIndex() >= 0 )
            {
                dateIndex = cboDays.getSelectedIndex();
                weatherObj.setDate( cboDays.getItemAt( dateIndex ).toString() );
                weatherObj.refresh( weatherData, weatherType, weatherView, dateIndex );
            }
        }
        else if( e.getSource() == cboIntervals )
        {
            tmrRefresh.stop();

            if( showWeather )
            {
                curInterval = 0;
                refInterval = 5 + 5 * cboIntervals.getSelectedIndex();
                tmrRefresh.start();
            }
        }
        else if( e.getSource() == tmrStart )
        {
            if( suZip.trim().length() == 5 )
            {
                weatherType = suWeatherType;
                weatherView = suWeatherView;
                weatherData = "";
                dateIndex = 0;
                searchWeather = true;
                cityObj = new City( suCity, suState, 0, 0, suZip );
                tempThread = new Thread( this, "tempThread" );
                tempThread.start();
            }
            else if( !suCity.trim().equals( "" ) && suState.trim().length() == 2 )
            {
                weatherType = suWeatherType;
                weatherView = suWeatherView;
                weatherData = "";
                dateIndex = 0;
                searchWeather = true;
                stateObj = new State( "", suState, this );
                cityObj = new City( suCity, suState, 0, 0, "" );
                tempThread = new Thread( this, "tempThread" );
                tempThread.start();
            }

            tmrStart.stop();
        }
        else if( e.getSource() == tmrRefresh )
        {
            if( showWeather )
            {
                curInterval++;

                if( curInterval >= refInterval )
                {
                    curInterval = 0;
                    waiting = true;
                    searchWeather = true;
                    weatherData = "";
                    tempThread = new Thread( this, "tempThread" );
                    tempThread.start();
                }
            }
            else
            {
                curInterval = 0;
                tmrRefresh.stop();
            }
        }
    }

    public String getStateAbbr( String name )
    {
        String abbr = "";

        if( name.equals( "Alabama" ) )
            abbr = "AL";
        else if( name.equals( "Alaska" ) )
            abbr = "AK";
        else if( name.equals( "Arizona" ) )
            abbr = "AZ";
        else if( name.equals( "Arkansas" ) )
            abbr = "AR";
        else if( name.equals( "California" ) )
            abbr = "CA";
        else if( name.equals( "Colorado" ) )
            abbr = "CO";
        else if( name.equals( "Connecticut" ) )
            abbr = "CT";
        else if( name.equals( "Delaware" ) )
            abbr = "DE";
        else if( name.equals( "Florida" ) )
            abbr = "FL";
        else if( name.equals( "Georgia" ) )
            abbr = "GA";
        else if( name.equals( "Hawaii" ) )
            abbr = "HI";
        else if( name.equals( "Idaho" ) )
            abbr = "ID";
        else if( name.equals( "Illinois" ) )
            abbr = "IL";
        else if( name.equals( "Indiana" ) )
            abbr = "IN";
        else if( name.equals( "Iowa" ) )
            abbr = "IA";
        else if( name.equals( "Kansas" ) )
            abbr = "KS";
        else if( name.equals( "Kentucky" ) )
            abbr = "KY";
        else if( name.equals( "Louisiana" ) )
            abbr = "LA";
        else if( name.equals( "Maine" ) )
            abbr = "ME";
        else if( name.equals( "Maryland" ) )
            abbr = "MD";
        else if( name.equals( "Massachusetts" ) )
            abbr = "MA";
        else if( name.equals( "Michigan" ) )
            abbr = "MI";
        else if( name.equals( "Minnesota" ) )
            abbr = "MN";
        else if( name.equals( "Mississippi" ) )
            abbr = "MS";
        else if( name.equals( "Missouri" ) )
            abbr = "MO";
        else if( name.equals( "Montana" ) )
            abbr = "MT";
        else if( name.equals( "Nebraska" ) )
            abbr = "NE";
        else if( name.equals( "Nevada" ) )
            abbr = "NV";
        else if( name.equals( "New Hampshire" ) )
            abbr = "NH";
        else if( name.equals( "New Jersey" ) )
            abbr = "NJ";
        else if( name.equals( "New Mexico" ) )
            abbr = "NM";
        else if( name.equals( "New York" ) )
            abbr = "NY";
        else if( name.equals( "North Carolina" ) )
            abbr = "NC";
        else if( name.equals( "North Dakota" ) )
            abbr = "ND";
        else if( name.equals( "Ohio" ) )
            abbr = "OH";
        else if( name.equals( "Oklahoma" ) )
            abbr = "OK";
        else if( name.equals( "Oregon" ) )
            abbr = "OR";
        else if( name.equals( "Pennsylvania" ) )
            abbr = "PA";
        else if( name.equals( "Rhode Island" ) )
            abbr = "RI";
        else if( name.equals( "South Carolina" ) )
            abbr = "SC";
        else if( name.equals( "South Dakota" ) )
            abbr = "SD";
        else if( name.equals( "Tennessee" ) )
            abbr = "TN";
        else if( name.equals( "Texas" ) )
            abbr = "TX";
        else if( name.equals( "Utah" ) )
            abbr = "UT";
        else if( name.equals( "Vermont" ) )
            abbr = "VT";
        else if( name.equals( "Virginia" ) )
            abbr = "VA";
        else if( name.equals( "Washington" ) )
            abbr = "WA";
        else if( name.equals( "West Virginia" ) )
            abbr = "WV";
        else if( name.equals( "Wisconsin" ) )
            abbr = "WI";
        else if( name.equals( "Wyoming" ) )
            abbr = "WY";

        return abbr;
    }

    public void resetComponents()
    {
        btnNewCity.setLocation( -100, -100 );
        btnWeatherReport.setLocation( -100, -100 );
        btnCurrentWeather.setLocation( -100, -100 );
        btnTenDayWeather.setLocation( -100, -100 );
        btnReturnUS.setLocation( -100, -100 );
        btnFindCity.setLocation( -100, -100 );
        btnAddCity.setLocation( -100, -100 );
        btnAddCity.setEnabled( false );
        btnReturn.setLocation( -100, -100 );
        btnGraphicalView.setLocation( -100, -100 );
        btnTextView.setLocation( -100, -100 );
        btnRefresh.setLocation( -100, -100 );
        btnAddZip.setLocation( -100, -100 );
        btnEditSets.setLocation( -100, -100 );
        btnSaveSets.setLocation( -100, -100 );
        btnRestSets.setLocation( -100, -100 );
        txtCity.setLocation( -100, -100 );
        txtCity.setText( "" );
        txtCity2.setLocation( -100, -100 );
        txtState.setLocation( -100, -100 );
        txtState.setValue( null );
        txtState2.setLocation( -100, -100 );
        txtZip.setLocation( -100, -100 );
        txtZip.setValue( null );
        txtZip2.setLocation( -100, -100 );
        chkShowCities.setLocation( -100, -100 );
        chkShowWeather.setLocation( -100, -100 );
        chkTimedRefresh.setLocation( -100, -100 );
        lblStatus.setText( "" );
        cboDays.setLocation( -100, -100 );
        cboDays.removeAllItems();
        cboIntervals.setLocation( -100, -100 );
        rdbCurrentWeather.setLocation( -100, -100 );
        rdbTenDayWeather.setLocation( -100, -100 );
        rdbTextView.setLocation( -100, -100 );
        rdbGraphicalView.setLocation( -100, -100 );
    }

    public String getTenDayWeather( String zip )
    {
        String data = "";

        try
        {
            URL url = new URL( "http://www.weather.com/weather/print/" + zip );

            BufferedReader br = new BufferedReader( new InputStreamReader( url.openStream() ) );

            String line = br.readLine();

            boolean begin = false;

            while( line != null )
            {
                if( line.toLowerCase().equals( "<!-- begin loop -->" ) )
                    begin = true;

                if( begin == true && line.toLowerCase().equals( "<!-- begin loop -->" ) == false && line.equals( "" ) == false )
                    data += line;

                line = br.readLine();

                if( line.toLowerCase().equals( "<!-- end loop -->" ) )
                    line = null;
            }
        } catch( Exception e )
        {
            return "";
        }

        data = data.replace( "http://image.weather.com/web/common/wxicons/31/", ">" );
        data = data.replace( ".gif", ".gif<" );

        int index = data.indexOf( "<" );

        while( index >= 0 )
        {
            int index2 = data.indexOf( ">" );

            if( index2 > 0 )
                data = data.substring( 0, index ) + "&" + data.substring( index2 + 1 );

            index = data.indexOf( "<" );
        }

        index = data.indexOf( "  " );

        while( index >= 0 )
        {
            data = data.replace( "  ", " " );
            index = data.indexOf( "  " );
        }

        data = data.trim();

        data = data.replace( "&deg;", "°" );
        data = data.replace( " &", "&" );
        data = data.replace( "& ", "&" );

        index = data.indexOf( "&&" );

        while( index >= 0 )
        {
            data = data.replace( "&&", "&" );
            index = data.indexOf( "&&" );
        }

        if( data.equals( "" ) == false )
        {
            if( data.indexOf( "&" ) == 0 )
                data = data.substring( 1 );

            if( data.lastIndexOf( "&" ) == data.length() - 1 )
                data = data.substring( 0, data.length() - 1 );
        }

        return data;
    }

    public String getCurrentWeather( String zip )
    {
        String data = "";
        String pic = "";

        try
        {
            URL url = new URL( "http://www.weather.com/weather/local/" + zip );

            BufferedReader br = new BufferedReader( new InputStreamReader( url.openStream() ) );

            String line = br.readLine();

            boolean begin = false;
            boolean end = false;

            pic = "http://image.weather.com/web/common/wxicons/52/";

            while( line != null )
            {
                if( line.toLowerCase().indexOf( pic ) >= 0 )
                    begin = true;

                if( begin == true && line.equals( "" ) == false )
                    data += line;

                line = br.readLine();

                if( begin == true && line.toLowerCase().indexOf( "visibility:" ) >= 0 )
                    end = true;
                else if( end == true && line.toLowerCase().indexOf( "</table>" ) >= 0 )
                    line = null;
            }
        } catch( Exception e )
        {
            return "";
        }

        data = data.replace( "&nbsp;", " " );

        int index = data.toLowerCase().indexOf( pic );

        if( index < 0 )
            return "";

        data = data.replace( pic, ">" );
        data = data.replace( ".gif", ".gif<" );

        index = data.indexOf( "<" );

        while( index >= 0 )
        {
            int index2 = data.indexOf( ">" );

            if( index2 > 0 )
                data = data.substring( 0, index ) + "&" + data.substring( index2 + 1 );

            index = data.indexOf( "<" );
        }

        index = data.indexOf( "  " );

        while( index >= 0 )
        {
            data = data.replace( "  ", " " );
            index = data.indexOf( "  " );
        }

        index = data.indexOf( "\t" );

        while( index >= 0 )
        {
            data = data.replace( "\t", "" );
            index = data.indexOf( "\t" );
        }

        data = data.replace( " &", "&" );
        data = data.replace( "& ", "&" );

        index = data.indexOf( "&&" );

        while( index >= 0 )
        {
            data = data.replace( "&&", "&" );
            index = data.indexOf( "&&" );
        }

        data = data.trim();

        if( data.equals( "" ) == false && data.indexOf( "&" ) == 0 )
            data = data.substring( 1 );

        if( data.equals( "" ) == false && data.lastIndexOf( "&" ) == data.length() - 1 )
            data = data.substring( 0, data.length() - 1 );

        data = data.replace( "&deg;", "°" );

        return data;
    }

    public void loadSettings()
    {
        if( startup )
        {
            startup = false;

            suCity = suState = suZip = "";
            suShowWeather = false;
            suWeatherType = 0;
            suWeatherView = 0;

            try
            {
                FileReader fr = new FileReader( "Settings.txt" );
                BufferedReader br = new BufferedReader( fr );

                String line = br.readLine();

                while( line != null )
                {
                    int index = line.indexOf( "=" );

                    if( index > 0 )
                    {
                        String key = line.substring( 0, index );
                        String value = line.substring( index + 1 );

                        if( key.equals( "sw" ) )
                        {
                            if( value.equals( "1" ) )
                                suShowWeather = true;
                            else
                                suShowWeather = false;
                        }
                        else if( key.equals( "sc" ) )
                        {
                            if( value.equals( "1" ) )
                                suShowCities = true;
                            else
                                suShowCities = false;
                        }
                        else if( key.equals( "type" ) )
                        {
                            if( value.equals( "1" ) )
                                suWeatherType = 1;
                            else
                                suWeatherType = 0;
                        }
                        else if( key.equals( "view" ) )
                        {
                            if( value.equals( "1" ) )
                               suWeatherView = 1;
                            else
                                suWeatherView = 0;
                        }

                        if( suShowWeather )
                        {
                            if( key.equals( "city" ) )
                                suCity = value;
                            else if( key.equals( "state" ) )
                                suState = value;
                            else if( key.equals( "zip" ) )
                                suZip = value;
                        }
                    }

                    line = br.readLine();
                }

                br.close();
            }
            catch( IOException e ){}
        }

        chkShowWeather.setSelected( suShowWeather );
        chkShowCities.setSelected( suShowCities );
        txtCity2.setText( suCity );
        txtState2.setValue( suState );
        txtZip2.setValue( suZip );

        if( suWeatherType == 0 )
            rdbCurrentWeather.setSelected( true );
        else
            rdbTenDayWeather.setSelected( true );

        if( suWeatherView == 0 )
            rdbTextView.setSelected( true );
        else
            rdbGraphicalView.setSelected( true );

        weatherType = suWeatherType;
        weatherView = suWeatherView;
    }

    public boolean saveSettings()
    {
        try
        {
            FileWriter fw = new FileWriter( "Settings.txt" );
            BufferedWriter bw = new BufferedWriter( fw );

            suShowWeather = chkShowWeather.isSelected();

            if( suShowWeather )
            {
                bw.write( "sw=1" );
                bw.newLine();
                suCity = txtCity2.getText();
                bw.write( "city=" + suCity );
                bw.newLine();
                suState = txtState2.getText();
                bw.write( "state=" + suState );
                bw.newLine();
                suZip = txtZip2.getText();
                bw.write( "zip=" + suZip );
                bw.newLine();
            }
            else
            {
                bw.write( "sw=0" );
                bw.newLine();
                suCity = "";
                bw.write( "city=" );
                bw.newLine();
                suState = "";
                bw.write( "state=" );
                bw.newLine();
                suZip = "";
                bw.write( "zip=" );
                bw.newLine();
            }

            suShowCities = chkShowCities.isSelected();

            if( suShowCities )
            {
                bw.write( "sc=1" );
                bw.newLine();
            }
            else
            {
                bw.write( "sc=0" );
                bw.newLine();
            }

            if( rdbTenDayWeather.isSelected() )
            {
                suWeatherType = 1;
                bw.write( "type=1" );
                bw.newLine();
            }
            else
            {
                suWeatherType = 0;
                bw.write( "type=0" );
                bw.newLine();
            }

            if( rdbGraphicalView.isSelected() )
            {
                suWeatherView = 1;
                bw.write( "view=1" );
            }
            else
            {
                suWeatherView = 0;
                bw.write( "view=0" );
            }

            bw.close();

            return true;
        }
        catch( IOException e ){ return false; }
    }
}
    