package org.jpo.datamodel;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.exif.makernotes.CanonMakernoteDirectory;
import com.drew.metadata.exif.makernotes.NikonType2MakernoteDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import org.apache.commons.lang3.StringUtils;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.logging.Logger;

/*
 ExifInfo.java: This class interacts with Drew Noake's library and extracts the Exif information

 Copyright (C) 2002 - 2019  Richard Eigenmann.
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
 * Class that interacts with Drew Noake's library and extracts the Exif
 * information
 *
 * @author Richard Eigenmann
 */
public class ExifInfo {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ExifInfo.class.getName() );
    /**
     * The File of the image to be decoded
     */
    private final File pictureFile;
    /**
     * The brand and model of the camera
     */
    private String camera = "";
    public String getCamera() {
        return camera;
    }

    public String getLens() {
        return lens;
    }

    public String getAperture() {
        return aperture;
    }

    public String getShutterSpeed() {
        return shutterSpeed;
    }

    public String getFocalLength() {
        return focalLength;
    }

    public String getIso() {
        return iso;
    }

    /**
     * The lens used
     */
    private String lens = "";
    /**
     * The aperture setting
     */
    private String aperture = "";
    /**
     * The shutter speed
     */
    private String shutterSpeed = "";
    /**
     * The focal length
     */
    private String focalLength = "";
    /**
     * The ISO sensitivity
     */
    private String iso = "";
    /**
     * The camera timestamp
     */
    private String createDateTime = "";

    public Point2D.Double getLatLng() {
        return latLng;
    }

    /**
     * The parsed GPS coordinates
     */
    private final Point2D.Double latLng = new Point2D.Double( 0, 0 );

    public String getExifWidth() {
        return exifWidth;
    }

    /**
     * The parsed width
     */
    private String exifWidth = "";

    public String getExifHeight() {
        return exifHeight;
    }

    /**
     * The parsed width
     */

    private  String exifHeight = "";

    public int getRotation() {
        return rotation;
    }

    /**
     * A full dump of the Exif information
     */

    private int rotation;  // default is 0

    /**
     * A full dump of the Exif information
     */
    private final StringBuilder exifDump = new StringBuilder();

    /**
     * Constructor to create the object. Call
     *
     * @see #decodeExifTags() next.
     *
     * @param pictureFile The URL of the picture
     */
    public ExifInfo( File pictureFile ) {
        this.pictureFile = pictureFile;
    }

    /**
     * This method decodes the Exif tags and stores the data
     */
    public void decodeExifTags() {
        if ( pictureFile == null ) {
            LOGGER.severe( "Can't decode Exif tags on a null File!" );
            return;
        }
        try {
            InputStream imageStream = new FileInputStream(pictureFile);
            Metadata metadata = ImageMetadataReader.readMetadata( new BufferedInputStream( imageStream ) );

            JpegDirectory jpegDirectory = metadata.getFirstDirectoryOfType( JpegDirectory.class );
            if ( jpegDirectory != null ) {
                exifWidth = tryToGetTag( jpegDirectory, JpegDirectory.TAG_IMAGE_WIDTH, exifWidth );
                exifHeight = tryToGetTag( jpegDirectory, JpegDirectory.TAG_IMAGE_HEIGHT, exifHeight );
            }

            ExifSubIFDDirectory exifSubIFDdirectory = metadata.getFirstDirectoryOfType( ExifSubIFDDirectory.class );
            if ( exifSubIFDdirectory != null ) {
                setCreateDateTime( tryToGetTag( exifSubIFDdirectory, ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL, getCreateDateTime() ) );
                aperture = tryToGetTag( exifSubIFDdirectory, ExifSubIFDDirectory.TAG_FNUMBER, aperture );
                aperture = tryToGetTag( exifSubIFDdirectory, ExifSubIFDDirectory.TAG_APERTURE, aperture );
                shutterSpeed = tryToGetTag( exifSubIFDdirectory, ExifSubIFDDirectory.TAG_EXPOSURE_TIME, shutterSpeed );
                shutterSpeed = tryToGetTag( exifSubIFDdirectory, ExifSubIFDDirectory.TAG_SHUTTER_SPEED, shutterSpeed );
                focalLength = tryToGetTag( exifSubIFDdirectory, ExifSubIFDDirectory.TAG_FOCAL_LENGTH, focalLength );
                iso = tryToGetTag( exifSubIFDdirectory, ExifSubIFDDirectory.TAG_ISO_EQUIVALENT, iso );
                lens = tryToGetTag( exifSubIFDdirectory, ExifSubIFDDirectory.TAG_LENS, lens );
            }

            ExifIFD0Directory exifSubIFD0directory = metadata.getFirstDirectoryOfType( ExifIFD0Directory.class );
            if ( exifSubIFD0directory != null ) {
                camera = StringUtils.stripEnd( tryToGetTag( exifSubIFD0directory, ExifIFD0Directory.TAG_MODEL, camera ), " " );
                String rotationString = StringUtils.stripEnd( tryToGetTag( exifSubIFD0directory, ExifIFD0Directory.TAG_ORIENTATION, "" ), " " );

                switch ( rotationString ) {
                    case "Top, left side (Horizontal / normal)":
                        rotation = 0;
                        break;
                    case "Right side, top (Rotate 90 CW)":
                        rotation = 90;
                        break;
                    case "Left side, bottom (Rotate 270 CW)":
                        rotation = 270;
                        break;
                    case "Bottom, right side (Rotate 180)":
                        rotation = 180;
                        break;
                    default:
                        rotation = 0;
                        break;
                }

            }

            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType( GpsDirectory.class );
            if ( gpsDirectory != null ) {
                GeoLocation location = gpsDirectory.getGeoLocation();
                if ( location != null ) {
                    latLng.x = location.getLatitude();
                    latLng.y = location.getLongitude();
                }
            }

            NikonType2MakernoteDirectory nikonType2MakernoteDirectory = metadata.getFirstDirectoryOfType( NikonType2MakernoteDirectory.class );
            if ( nikonType2MakernoteDirectory != null ) {
                iso = tryToGetTag( nikonType2MakernoteDirectory, NikonType2MakernoteDirectory.TAG_ISO_1, iso );
                lens = tryToGetTag( nikonType2MakernoteDirectory, NikonType2MakernoteDirectory.TAG_LENS, lens );
            }

            CanonMakernoteDirectory canonMakernoteDirectory = metadata.getFirstDirectoryOfType( CanonMakernoteDirectory.class );
            if ( canonMakernoteDirectory != null ) {
                lens = tryToGetTag( canonMakernoteDirectory, CanonMakernoteDirectory.TAG_LENS_MODEL, lens );
            }

            for ( Directory directory : metadata.getDirectories() ) {
                directory.getTags().forEach(tag
                        -> exifDump.append( tag.getTagTypeHex() ).append( " - " ).append( tag.getTagName() ).append( ":\t" ).append( tag.getDirectoryName() ).append( ":\t" ).append( tag.getDescription() ).append( "\n" )
                );
            }
        } catch ( ImageProcessingException | NullPointerException | IOException x ) {
            LOGGER.severe( x.getMessage() );
        }

    }

    /**
     * This method tries to get a tag out of the Exif data
     *
     * @param directory The EXIF Directory
     * @param tag the tag to search for
     * @param defaultValue the String to return if the tag was not found.
     * @return the tag
     */
    private String tryToGetTag( Directory directory, int tag, String defaultValue ) {
        String searchString;
        searchString = directory.getDescription( tag );
        if ( searchString == null ) {
            searchString = defaultValue;
        }
        return searchString;
    }

    /**
     * This method returns all the tags as they were decoded in a single string
     *
     * @return Returns the tags as they were decoded
     */
    public String getAllTags() {
        return exifDump.toString();
    }


    /**
     * This method returns a comprehensive summary of the photographic settings
     *
     * @return Returns a comprehensive summary of the photographic settings
     */
    public String getComprehensivePhotographicSummary() {
        String longitude = "";
        String longitudeRef = "";
        String latitude = "";
        // Whether it's N or S
        String latitudeRef = "";
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
     *
     * @return the createDateTime
     */
    public String getCreateDateTime() {
        return createDateTime;
    }

    /**
     * Sets the time the picture was created
     *
     * @param dateTime the createDateTime to set
     */
    private void setCreateDateTime( String dateTime ) {
        this.createDateTime = dateTime;
    }

}
