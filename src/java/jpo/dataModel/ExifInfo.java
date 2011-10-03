package jpo.dataModel;

import java.util.*;
import java.io.*;
import java.net.*;
import com.drew.metadata.*;
import com.drew.metadata.exif.*;
import com.drew.metadata.iptc.*;
import com.drew.imaging.jpeg.*;
import java.awt.geom.Point2D;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
ExifInfo.java: This class interacts with Drew Noake's library and extracts the Exif information

Copyright (C) 2002 - 2011  Richard Eigenmann.
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
/**
 * Class that interacts with Drew Noake's library and extracts the Exif information
 *
 * @author  Richard Eigenmann
 */
public class ExifInfo {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ExifInfo.class.getName() );
    /**
     *  The URL or the image to be decoded
     */
    private URL pictureUrl;
    /**
     *  The brand and model of the camera
     */
    public String camera;
    /**
     *  The lens used
     */
    public String lens;
    /**
     *  The aperture setting
     */
    public String aperture;
    /**
     *  The shutter speed
     */
    public String shutterSpeed;
    /**
     *  The focal length
     */
    public String focalLength;
    /**
     *  The ISO sensitivity
     */
    public String iso;
    /**
     *  The camera timestamp
     */
    private String createDateTime;
    /**
     *  The Longitude
     */
    private String longitude;
    /**
     * Whether it's W or E
     */
    private String longitudeRef;
    /**
     *  The latitude
     */
    private String latitude;
    /**
     * Whether it's N or S
     */
    private String latitudeRef;
    /**
     * The parsed GPS coordinates
     */
    public Point2D.Double latLng;
    /**
     * The parsed width
     */
    public String exifWidth = "N/A";
    /**
     * The parsed width
     */
    public String exifHeight = "N/A";
    /**
     *  A full dump of the Exif information
     */
    private StringBuffer exifDump;

    /**
     *   Constructor to create the object
     *
     * @param pictureUrl
     */
    public ExifInfo( URL pictureUrl ) {
        setUrl( pictureUrl );
    }

    /**
     *  Use this method to set the URL of the picture to be decoded. Afterwards call
     *  decodeExifTags.
     *
     * @param pictureUrl
     */
    public void setUrl( URL pictureUrl ) {
        this.pictureUrl = pictureUrl;
        nullifyVars();
    }

    /**
     *  This method sets the variables of the ExifInfo to null.
     *  Changed null to "" as null gives runtime errors when rendering the strings.
     */
    private void nullifyVars() {
        camera = "";
        lens = "";
        aperture = "";
        shutterSpeed = "";
        focalLength = "";
        iso = "";
        setCreateDateTime( "" );
        exifDump = new StringBuffer( "" );
    }

    /**
     *   This method decodes the Exif tags and stores the data
     */
    public void decodeExifTags() {
        if ( pictureUrl == null ) {
            //logger.info ("ExifInfo.decodeExifTags: called with a null pictureUrl. aborting" );
            return;
        }

        try {

            InputStream highresStream = pictureUrl.openStream();
            JpegSegmentReader reader = new JpegSegmentReader( new BufferedInputStream( highresStream ) );
            byte[] exifSegment = reader.readSegment( JpegSegmentReader.SEGMENT_APP1 );
            byte[] iptcSegment = reader.readSegment( JpegSegmentReader.SEGMENT_APPD );

            Metadata metadata = new Metadata();
            new ExifReader( exifSegment ).extract( metadata );
            new IptcReader( iptcSegment ).extract( metadata );

            Iterator directories = metadata.getDirectoryIterator();
            if ( !directories.hasNext() ) {
                exifDump.append( Settings.jpoResources.getString( "noExifTags" ) );
            }
            String searchString;
            while ( directories.hasNext() ) {
                Directory directory = (Directory) directories.next();

                camera = tryToGetTag( directory, ExifDirectory.TAG_MODEL, camera );
                lens = tryToGetTag( directory, NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS, lens );
                aperture = tryToGetTag( directory, ExifDirectory.TAG_FNUMBER, aperture );
                shutterSpeed = tryToGetTag( directory, ExifDirectory.TAG_EXPOSURE_TIME, shutterSpeed );
                focalLength = tryToGetTag( directory, ExifDirectory.TAG_FOCAL_LENGTH, focalLength );
                iso = tryToGetTag( directory, NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_1, iso );
                setCreateDateTime( tryToGetTag( directory, ExifDirectory.TAG_DATETIME_ORIGINAL, getCreateDateTime() ) );
                latitude = tryToGetTag( directory, GpsDirectory.TAG_GPS_LATITUDE, latitude );
                latitudeRef = tryToGetTag( directory, GpsDirectory.TAG_GPS_LATITUDE_REF, "" );
                longitude = tryToGetTag( directory, GpsDirectory.TAG_GPS_LONGITUDE, longitude );
                longitudeRef = tryToGetTag( directory, GpsDirectory.TAG_GPS_LONGITUDE_REF, "" );
                exifWidth = tryToGetTag( directory, ExifDirectory.TAG_EXIF_IMAGE_WIDTH, exifWidth );
                //LOGGER.info( "Width: " + exifWidth );
                exifHeight = tryToGetTag( directory, ExifDirectory.TAG_EXIF_IMAGE_HEIGHT, exifHeight );
                //LOGGER.info( "Height: " + exifHeight );
                latLng = parseGPS();

                Iterator tags = directory.getTagIterator();
                while ( tags.hasNext() ) {
                    Tag tag = (Tag) tags.next();
                    try {
                        exifDump.append( tag.getTagTypeHex() ).append( " - " ).append( tag.getTagName() ).append( ":\t" ).append( tag.getDescription() ).append( "\n" );
                    } catch ( MetadataException x ) {
                        //logger.info ("ExifInfo: problem with tag: " + x.getMessage());
                    }
                }
            }
        } catch ( MalformedURLException x ) {
            //logger.info( "MalformedURLException: " + x.getMessage() );
        } catch ( IOException x ) {
            //logger.info( "IOException: " + x.getMessage() );
        } catch ( JpegProcessingException x ) {
            //x.printStackTrace();
            //exifDump.append( "No EXIF header found\n" + x.getMessage() );
        }
        if ( camera == null ) {
            camera = "";
        }
        if ( lens == null ) {
            lens = "";
        }
        if ( aperture == null ) {
            aperture = "";
        }
        if ( shutterSpeed == null ) {
            shutterSpeed = "";
        }
        if ( focalLength == null ) {
            focalLength = "";
        }
        if ( iso == null ) {
            iso = "";
        }
        if ( getCreateDateTime() == null ) {
            setCreateDateTime( "" );
        }
        if ( latitude == null ) {
            latitude = "";
        }
        if ( longitude == null ) {
            longitude = "";
        }

    }

    /**
     *  This method tries to get a tag out of the Exif data
     *  @param directory The EXIF Directory
     *  @param tag the tag to search for
     *  @param defaultValue the String to return if the tag was not found.
     */
    private String tryToGetTag( Directory directory, int tag, String defaultValue ) {
        String searchString;
        try {
            searchString = directory.getDescription( tag );
        } catch ( MetadataException x ) {
            searchString = null;
        }
        if ( searchString == null ) {
            searchString = defaultValue;
        }
        return searchString;
    }

    /**
     *  This method returns all the tags as they were decoded in a single string
     *
     * @return Returns the tags as they were decoded
     */
    public String getAllTags() {
        return exifDump.toString();
    }

    /**
     *  This method returns a brief summary of the photographic settings
     *
     * @return Returns a brief summary of the photographic settings
     */
    public String getBriefPhotographicSummary() {
        return Settings.jpoResources.getString( "ExifInfoCamera" ) + "\t" + camera + "\n"
                + Settings.jpoResources.getString( "ExifInfoShutterSpeed" ) + "\t" + shutterSpeed + "\n"
                + Settings.jpoResources.getString( "ExifInfoAperture" ) + "\t" + aperture + "\n"
                + Settings.jpoResources.getString( "ExifInfoTimeStamp" ) + "\t" + getCreateDateTime() + "\n";
    }

    /**
     *  This method returns a comprehensive summary of the photographic settings
     *
     * @return Returns a comprehensive summary of the photographic settings
     */
    public String getComprehensivePhotographicSummary() {
        return Settings.jpoResources.getString( "ExifInfoCamera" ) + "\t" + camera + "\n"
                + Settings.jpoResources.getString( "ExifInfoLens" ) + "\t" + lens + "\n"
                + Settings.jpoResources.getString( "ExifInfoShutterSpeed" ) + "\t" + shutterSpeed + "\n"
                + Settings.jpoResources.getString( "ExifInfoAperture" ) + "\t" + aperture + "\n"
                + Settings.jpoResources.getString( "ExifInfoFocalLength" ) + "\t" + focalLength + "\n"
                + Settings.jpoResources.getString( "ExifInfoISO" ) + "\t" + iso + "\n"
                + Settings.jpoResources.getString( "ExifInfoTimeStamp" ) + "\t" + getCreateDateTime() + "\n"
                + Settings.jpoResources.getString( "ExifInfoLatitude" ) + "\t" + latitude + " " + latitudeRef + "\n"
                + Settings.jpoResources.getString( "ExifInfoLongitude" ) + "\t" + longitude + " " + longitudeRef + "\n";
    }

    /**
     * Returns the time the picture was created.
     * @return the createDateTime
     */
    public String getCreateDateTime() {
        return createDateTime;
    }

    /**
     * Sets the time the picture was created
     * @param createDateTime the createDateTime to set
     */
    private void setCreateDateTime( String dateTime ) {
        this.createDateTime = dateTime;
    }

    /** Attempt to parse the GPS data
     *
     */
    private Point2D.Double parseGPS() {
        double longitudeD = 0f;
        double latitudeD = 0f;

        if ( ( longitude == null ) || ( latitude == null ) ) {
            return new Point2D.Double( latitudeD, longitudeD );
        }

        LOGGER.fine( String.format( "Trying to parse longitude: %s and latitude: %s", longitude, latitude ) );
        Pattern pattern = Pattern.compile( "(.*)\"(.*)'(.*)" );
        Matcher longitudeMatcher = pattern.matcher( longitude );
        if ( longitudeMatcher.matches() ) {
            longitudeD = Integer.parseInt( longitudeMatcher.group( 1 ) ) + ( Double.parseDouble( longitudeMatcher.group( 2 ) ) / 60 ) + ( Double.parseDouble( longitudeMatcher.group( 3 ) ) / 3600 );
            if ( longitudeRef.equals( "W" ) ) {
                longitudeD *= -1f;
            }
            LOGGER.fine( String.format( "Longitude %s matches %s %s %s --> %f", longitude, longitudeMatcher.group( 1 ), longitudeMatcher.group( 2 ), longitudeMatcher.group( 3 ), longitudeD ) );
        } else {
            LOGGER.fine( String.format( "Longitude %s made no sense", longitude ) );
        }
        Matcher latitudeMatcher = pattern.matcher( latitude );
        if ( latitudeMatcher.matches() ) {
            latitudeD = Integer.parseInt( latitudeMatcher.group( 1 ) ) + ( Double.parseDouble( latitudeMatcher.group( 2 ) ) / 60 ) + ( Double.parseDouble( latitudeMatcher.group( 3 ) ) / 3600 );
            if ( latitudeRef.equals( "S" ) ) {
                latitudeD *= -1f;
            }
            LOGGER.fine( String.format( "Latitude %s matches %s %s %s --> %f", latitude, latitudeMatcher.group( 1 ), latitudeMatcher.group( 2 ), latitudeMatcher.group( 3 ), latitudeD ) );
        } else {
            LOGGER.fine( String.format( "Latitude %s made no sense", latitude ) );
        }
        return new Point2D.Double( latitudeD, longitudeD );

    }
}
