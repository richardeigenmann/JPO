package jpo.dataModel;

import java.util.*;
import java.io.*;
import java.net.*;
import com.drew.metadata.*;
import com.drew.metadata.exif.*;
import com.drew.metadata.iptc.*;
import com.drew.imaging.jpeg.*;
import java.util.logging.Logger;

/*
ExifInfo.java: This class interacts with Drew Noake's library and extracts the Exif information

Copyright (C) 2002 - 2009  Richard Eigenmann.
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
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( ExifInfo.class.getName() );

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
     *  A full dump of the Exif information
     */
    private StringBuffer exifDump;


    /**
     *   Constructor to create the object
     *
    public ExifInfo() {
    }*/
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

                Iterator tags = directory.getTagIterator();
                while ( tags.hasNext() ) {
                    Tag tag = (Tag) tags.next();
                    try {
                        exifDump.append( tag.getTagTypeHex() + " - " + tag.getTagName() + ":\t" + tag.getDescription() + "\n" );
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

    }


    /**
     *  This method tries to get a tag out of the Exif data
     *  @param directory The EXIF Directory
     *  @param tag the tag to search for
     *  @param inputString the String to return if the tag was not found.
     */
    private String tryToGetTag( Directory directory, int tag, String inputString ) {
        String searchString;
        try {
            searchString = directory.getDescription( tag );
        } catch ( MetadataException x ) {
            searchString = null;
        }
        if ( searchString == null ) {
            searchString = inputString;
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
                + Settings.jpoResources.getString( "ExifInfoTimeStamp" ) + "\t" + getCreateDateTime() + "\n";

    }


    /**
     * Returns the time the picture was created.
     * @return the createDateTime
     */
    public String getCreateDateTime() {
        return createDateTime;
    }


    /**
     * Sets the time the picutre was created
     * @param createDateTime the createDateTime to set
     */
    private void setCreateDateTime( String dateTime ) {
        this.createDateTime = dateTime;
    }
}
