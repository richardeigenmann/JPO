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

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import static com.drew.metadata.exif.ExifDirectoryBase.*;

/*
 Copyright (C) 2002-2024 Richard Eigenmann.
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
    private static final Logger LOGGER = Logger.getLogger(ExifInfo.class.getName());
    /**
     * The File of the image to be decoded
     */
    private final File pictureFile;
    /**
     * The brand and model of the camera
     */
    private String camera = "";

    /**
     * Returns the camera brand
     *
     * @return the camera brand
     */
    public String getCamera() {
        return camera;
    }

    /**
     * Returns the lens used to take the picture
     *
     * @return the lens used to take the picture
     */
    public String getLens() {
        return lens;
    }

    /**
     * Returns the aperture used to take the picture
     *
     * @return Returns the aperture used to take the picture
     */
    public String getAperture() {
        return aperture;
    }

    /**
     * Returns the shutter speed used to take the picture
     *
     * @return Returns the shutter speed used to take the picture
     */
    public String getShutterSpeed() {
        return shutterSpeed;
    }

    /**
     * Returns the focal length used in the lens
     *
     * @return Returns the focal length used in the lens
     */
    public String getFocalLength() {
        return focalLength;
    }

    /**
     * Returns the ISO sensitivity used when taking the picture
     *
     * @return Returns the ISO sensitivity used when taking the picture
     */
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

    /**
     * Returns the Latitude and Longitude
     *
     * @return the Latitude and Longitude
     */
    public Point2D.Double getLatLng() {
        return latLng;
    }

    /**
     * The parsed GPS coordinates
     */
    private final Point2D.Double latLng = new Point2D.Double(0, 0);

    /**
     * Returns the width as stored in the Exif
     *
     * @return Returns the width as stored in the Exif
     */
    public String getExifWidth() {
        return exifWidth;
    }

    /**
     * The width
     */
    private String exifWidth= "";


    /**
     * The parsed width
     */
    private String exifHeight= "";

    /**
     * Returns the height as stored in the Exif
     *
     * @return Returns the height as stored in the Exif
     */
    public String getExifHeight() {
        return exifHeight;
    }

    /**
     * Returns the rotation in the Exif
     *
     * @return the rotation in the Exif
     */
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
            final var imageStream = new FileInputStream(pictureFile);
            final var metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(imageStream));

            extractDimensions(metadata);

            final var exifSubIFDdirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exifSubIFDdirectory != null) {
                setCreateDateTime(tryToGetTag(exifSubIFDdirectory, TAG_DATETIME_ORIGINAL, getCreateDateTime()));
                aperture = tryToGetTag( exifSubIFDdirectory, TAG_FNUMBER, aperture );
                aperture = tryToGetTag( exifSubIFDdirectory, TAG_APERTURE, aperture );
                shutterSpeed = tryToGetTag( exifSubIFDdirectory, TAG_EXPOSURE_TIME, shutterSpeed );
                shutterSpeed = tryToGetTag( exifSubIFDdirectory, TAG_SHUTTER_SPEED, shutterSpeed );
                focalLength = tryToGetTag( exifSubIFDdirectory, TAG_FOCAL_LENGTH, focalLength );
                iso = tryToGetTag( exifSubIFDdirectory, TAG_ISO_EQUIVALENT, iso );
                lens = tryToGetTag( exifSubIFDdirectory, TAG_LENS, lens );
            }

            final var exifSubIFD0directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if ( exifSubIFD0directory != null ) {
                camera = StringUtils.stripEnd( tryToGetTag( exifSubIFD0directory, TAG_MODEL, camera ), " " );
                final String rotationString = StringUtils.stripEnd(tryToGetTag(exifSubIFD0directory, TAG_ORIENTATION, ""), " ");

                switch (rotationString) {
                    case "Top, left side (Horizontal / normal)" -> rotation = 0;
                    case "Right side, top (Rotate 90 CW)" -> rotation = 90;
                    case "Left side, bottom (Rotate 270 CW)" -> rotation = 270;
                    case "Bottom, right side (Rotate 180)" -> rotation = 180;
                    default -> rotation = 0;
                }

            }

            final var gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if ( gpsDirectory != null ) {
                GeoLocation location = gpsDirectory.getGeoLocation();
                if ( location != null ) {
                    latLng.x = location.getLatitude();
                    latLng.y = location.getLongitude();
                }
            }

            final var nikonType2MakernoteDirectory = metadata.getFirstDirectoryOfType(NikonType2MakernoteDirectory.class);
            if ( nikonType2MakernoteDirectory != null ) {
                iso = tryToGetTag( nikonType2MakernoteDirectory, NikonType2MakernoteDirectory.TAG_ISO_1, iso );
                lens = tryToGetTag( nikonType2MakernoteDirectory, NikonType2MakernoteDirectory.TAG_LENS, lens );
            }

            final var canonMakernoteDirectory = metadata.getFirstDirectoryOfType(CanonMakernoteDirectory.class);
            if ( canonMakernoteDirectory != null ) {
                lens = tryToGetTag(canonMakernoteDirectory, CanonMakernoteDirectory.TAG_LENS_MODEL, lens);
                if ("".equals(lens)) {
                    lens = removeLastChars(tryToGetTag(canonMakernoteDirectory, CanonMakernoteDirectory.CameraSettings.TAG_SHORT_FOCAL_LENGTH, ""), 2)
                            + " - " + removeLastChars(tryToGetTag(canonMakernoteDirectory, CanonMakernoteDirectory.CameraSettings.TAG_LONG_FOCAL_LENGTH, ""), 2)
                            + "mm";
                }
            }

            for (final var directory : metadata.getDirectories()) {
                directory.getTags().forEach(tag
                        -> exifDump.append(tag.getTagTypeHex()).append(" - ").append(tag.getTagName()).append(":\t").append(tag.getDirectoryName()).append(":\t").append(tag.getDescription()).append("\n")
                );
            }
        } catch (final ImageProcessingException | NullPointerException | IOException x) {
            LOGGER.severe(x.getMessage());
        }

    }

    private Dimension extractDimensions(final Metadata metadata) {
        final var jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
        if (jpegDirectory != null) {
            exifWidth = tryToGetTag(jpegDirectory, JpegDirectory.TAG_IMAGE_WIDTH, getExifWidth());
            exifHeight = tryToGetTag(jpegDirectory, JpegDirectory.TAG_IMAGE_HEIGHT, getExifHeight());
        }
        return new Dimension (0,0);
    }

    private static String removeLastChars(String str, int chars) {
        return str.substring(0, str.length() - chars);
    }

    /**
     * This method tries to get a tag out of the Exif data
     *
     * @param directory    The EXIF Directory
     * @param tag          the tag to search for
     * @param defaultValue the String to return if the tag was not found.
     * @return the tag
     */
    private String tryToGetTag(Directory directory, int tag, String defaultValue) {
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
        return Settings.getJpoResources().getString("ExifInfoCamera") + "\t" + camera + "\n"
                + Settings.getJpoResources().getString("ExifInfoLens") + "\t" + lens + "\n"
                + Settings.getJpoResources().getString("ExifInfoShutterSpeed") + "\t" + shutterSpeed + "\n"
                + Settings.getJpoResources().getString("ExifInfoAperture") + "\t" + aperture + "\n"
                + Settings.getJpoResources().getString("ExifInfoFocalLength") + "\t" + focalLength + "\n"
                + Settings.getJpoResources().getString("ExifInfoISO") + "\t" + iso + "\n"
                + Settings.getJpoResources().getString("ExifInfoTimeStamp") + "\t" + getCreateDateTime() + "\n"
                + Settings.getJpoResources().getString("ExifInfoLatitude") + "\t" + latitude + " " + latitudeRef + "\n"
                + Settings.getJpoResources().getString("ExifInfoLongitude") + "\t" + longitude + " " + longitudeRef + "\n";
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
