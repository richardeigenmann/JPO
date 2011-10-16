/*
Webserver.java:  utilities for the JPO application
 *
Copyright (C) 2002-2010  Richard Eigenmann.
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version. This program is distributed
in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
more details. You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
The license is in gpl.txt.
See http://www.gnu.org/copyleft/gpl.html for the details.
 */
package webserver;

import java.awt.Desktop;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;
import jpo.export.HtmlDistiller;

/**
 * Class based on the Singleton pattern to fire up the Nano webserver and
 * interact with the browser on the desktop to implement GoogleMaps for
 * Latitude and Longitude.
 * @author Richard Eigenmann
 */
public final class Webserver
        extends NanoHTTPD {

    /**
     * Privates Klassenattribut,
     * wird beim erstmaligen Gebrauch (nicht beim Laden) der Klasse erzeugt
     */
    private static Webserver instance;

    public static final int PORT = 36126;

    /**
     * 
     */
    public static InetAddress address;


    /** Konstruktor ist privat, Klasse darf nicht von außen instanziiert werden. */
    private Webserver() throws IOException {
        super( address, PORT );
    }


    /**
     * Statische Methode „getInstance()“ liefert die einzige Instanz der Klasse zurück.
     * Ist synchronisiert und somit thread-sicher.
     * @return returns the instance
     */
    public synchronized static Webserver getInstance() {
        if ( instance == null ) {
            try {
                address = InetAddress.getByName( "localhost" );
                System.out.println( "Address: " + address.getHostAddress() );
                instance = new Webserver();
            } catch ( IOException ex ) {
                Logger.getLogger( Webserver.class.getName() ).log( Level.SEVERE, null, ex );
                instance = null;
            }
        }
        return instance;
    }


    @Override
    public Response serve( String uri, String method, Properties header,
            Properties parms ) {
        System.out.println( method + " '" + uri + "' " );

        String mimeType = MIME_HTML;
        String msg = "<html><head><title>Sorry</head><body>Sorry. bad request</body></html>";
        if ( "/googlemaps.html".equals( uri.toLowerCase() ) ) {
            msg = Tools.copyFromJarToString( HtmlDistiller.class, "googlemaps.html" );
        } else if ( "/googlemaps.js".equals( uri.toLowerCase() ) ) {
            msg = Tools.copyFromJarToString( HtmlDistiller.class, "googlemaps.js" );
        } else if ( "/googlemaps.css".equals( uri.toLowerCase() ) ) {
            msg = Tools.copyFromJarToString( HtmlDistiller.class, "googlemaps.css" );
            mimeType = MIME_CSS;
        } else if ( "/submit".equals( uri.toLowerCase() ) ) {
            System.out.println( uri );
            System.out.println( header );
            System.out.println( parms );
            String hashCode = parms.getProperty( "hashcode" );
            String latitude = parms.getProperty( "latitude" );
            String longitude = parms.getProperty( "longitude" );
            msg = String.format( "Thanks for sending back latitude: %s and longitude: %s on object: %s", latitude, longitude, hashCode );
            if ( pi.hashCode() == Integer.parseInt( hashCode ) ) {
                pi.setLatLng( new Point2D.Double( Double.parseDouble( latitude ), Double.parseDouble( longitude ) ) );
            }
        } else {
            return new NanoHTTPD.Response( HTTP_NOTFOUND, MIME_HTML, msg );
        }

        return new NanoHTTPD.Response( HTTP_OK, mimeType, msg );
    }

    private PictureInfo pi;


    /**
     * Opens the browser on the desktop and puts it onto
     * @param sdmtn
     */
    public void browse( SortableDefaultMutableTreeNode sdmtn ) {
        try {
            String doc = "googlemaps.html";
            pi = (PictureInfo) sdmtn.getUserObject();  // can throw an class cast exception in which case we terminate without going to browser
            Point2D.Double latLng = pi.getLatLng();
            Desktop.getDesktop().browse( new URI( "http", null, address.getHostAddress(), PORT, "/" + doc, "hashcode=" + Integer.toString( pi.hashCode() ) + ";lat=" + Double.toString( latLng.x ) + ";lng=" + Double.toString( latLng.y ), null ) );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}

