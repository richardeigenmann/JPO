package jpo.dataModel;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.exif.NikonType2MakernoteDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 ExifInfo.java: This class interacts with Drew Noake's library and extracts the Exif information

 Copyright (C) 2002 - 2013  Richard Eigenmann.
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
     * The URL or the image to be decoded
     */
    private URL pictureUrl;
    /**
     * The brand and model of the camera
     */
    public String camera = "";
    /**
     * The lens used
     */
    public String lens = "";
    /**
     * The aperture setting
     */
    public String aperture = "";
    /**
     * The shutter speed
     */
    public String shutterSpeed = "";
    /**
     * The focal length
     */
    public String focalLength = "";
    /**
     * The ISO sensitivity
     */
    public String iso = "";
    /**
     * The camera timestamp
     */
    private String createDateTime = "";
    /**
     * The Longitude
     */
    private String longitude = "";
    /**
     * Whether it's W or E
     */
    private String longitudeRef = "";
    /**
     * The latitude
     */
    private String latitude = "";
    /**
     * Whether it's N or S
     */
    private String latitudeRef = "";
    /**
     * The parsed GPS coordinates
     */
    public Point2D.Double latLng = new Point2D.Double(0, 0);
    /**
     * The parsed width
     */
    public String exifWidth = "";
    /**
     * The parsed width
     */
    public String exifHeight = "";
    /**
     * A full dump of the Exif information
     */
    private StringBuffer exifDump = new StringBuffer("");

    /**
     * Constructor to create the object. Call
     *
     * @see decodeExifTags next.
     *
     * @param pictureUrl
     */
    public ExifInfo(URL pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    /**
     * This method decodes the Exif tags and stores the data
     */
    public void decodeExifTags() {

        try {
            InputStream highresStream = pictureUrl.openStream();
            boolean waitforbytes = false;
            Metadata metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(highresStream), waitforbytes);

            ExifSubIFDDirectory exifSubIFDdirectory = metadata.getDirectory(ExifSubIFDDirectory.class);
            setCreateDateTime(tryToGetTag(exifSubIFDdirectory, ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL, getCreateDateTime()));
            aperture = tryToGetTag(exifSubIFDdirectory, ExifSubIFDDirectory.TAG_FNUMBER, aperture);
            shutterSpeed = tryToGetTag(exifSubIFDdirectory, ExifSubIFDDirectory.TAG_EXPOSURE_TIME, shutterSpeed);
            focalLength = tryToGetTag(exifSubIFDdirectory, ExifSubIFDDirectory.TAG_FOCAL_LENGTH, focalLength);
            iso = tryToGetTag(exifSubIFDdirectory, ExifSubIFDDirectory.TAG_ISO_EQUIVALENT, iso);
            lens = tryToGetTag(exifSubIFDdirectory, ExifSubIFDDirectory.TAG_LENS, lens);

            JpegDirectory jpegDirectory = metadata.getDirectory(JpegDirectory.class);
            exifWidth = tryToGetTag(jpegDirectory, JpegDirectory.TAG_JPEG_IMAGE_WIDTH, exifWidth);
            exifHeight = tryToGetTag(jpegDirectory, JpegDirectory.TAG_JPEG_IMAGE_HEIGHT, exifHeight);

            ExifIFD0Directory exifSubIFD0directory = metadata.getDirectory(ExifIFD0Directory.class);
            camera = rtrim(tryToGetTag(exifSubIFD0directory, ExifIFD0Directory.TAG_MODEL, camera));

            NikonType2MakernoteDirectory nikonType2MakernoteDirectory = metadata.getDirectory(NikonType2MakernoteDirectory.class);
            if (nikonType2MakernoteDirectory != null) {
                iso = tryToGetTag(nikonType2MakernoteDirectory, NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_1, iso);
                //System.out.println( directory.getName() + "iso: " + iso );
                lens = tryToGetTag(nikonType2MakernoteDirectory, NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS, lens);
            }


            for (Directory directory : metadata.getDirectories()) {
                //camera = rtrim(tryToGetTag(directory, ExifIFD0Directory.TAG_MODEL, camera));


                //    if ("Nikon Makernote".equals(directory.getName())) {
                //      iso = tryToGetTag(directory, NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_1, iso);
                //System.out.println( directory.getName() + "iso: " + iso );
                //    lens = tryToGetTag(directory, NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS, lens);
                //}
                //setCreateDateTime(tryToGetTag(directory, ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL, getCreateDateTime()));
                //setCreateDateTime( tryToGetTag( directory, ExifIFD0Directory.TAG_DATETIME, getCreateDateTime() ) );
                //latitude = tryToGetTag(directory, GpsDirectory.TAG_GPS_LATITUDE, latitude);
                //latitudeRef = tryToGetTag(directory, GpsDirectory.TAG_GPS_LATITUDE_REF, "");
                //longitude = tryToGetTag(directory, GpsDirectory.TAG_GPS_LONGITUDE, longitude);
                //longitudeRef = tryToGetTag(directory, GpsDirectory.TAG_GPS_LONGITUDE_REF, "");
                //if ("Jpeg".equals(directory.getName())) {
                //exifWidth = tryToGetTag( directory, ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH, exifWidth );
                //exifWidth = tryToGetTag(directory, JpegDirectory.TAG_JPEG_IMAGE_WIDTH, exifWidth);
                //LOGGER.info( "Width: " + exifWidth );
                //exifHeight = tryToGetTag( directory, ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT, exifHeight );
                //exifHeight = tryToGetTag(directory, JpegDirectory.TAG_JPEG_IMAGE_HEIGHT, exifHeight);
                //System.out.println( directory.getName() + "exifHeight: " + exifHeight );
                //LOGGER.info( "Height: " + exifHeight );
                //}
                //latLng = parseGPS();

                for (Tag tag : directory.getTags()) {
                    exifDump.append(tag.getTagTypeHex()).append(" - ").append(tag.getTagName()).append(":\t").append(tag.getDirectoryName()).append(":\t").append(tag.getDescription()).append("\n");
                }
            }
            GpsDirectory gpsDirectory = metadata.getDirectory(GpsDirectory.class);
            GeoLocation location = gpsDirectory.getGeoLocation();
            latLng.x = location.getLatitude();
            latLng.y = location.getLongitude();
            //LOGGER.info(location.toString());
        } catch (ImageProcessingException x) {
            LOGGER.severe("ImageProcessingException: " + x.getMessage());
        } catch (MalformedURLException x) {
            LOGGER.severe("MalformedURLException: " + x.getMessage());
        } catch (IOException x) {
            LOGGER.severe("IOException: " + x.getMessage());
        } catch (NullPointerException x) {
            LOGGER.severe("Now why would we be trying to decode Exiff info on a null URL other than for testing?\n" + x.getMessage());
        }


    }

    /**
     * This method tries to get a tag out of the Exif data
     *
     * @param directory The EXIF Directory
     * @param tag the tag to search for
     * @param defaultValue the String to return if the tag was not found.
     */
    private String tryToGetTag(Directory directory, int tag, String defaultValue) {
        String searchString;
        searchString = directory.getDescription(tag);
        if (searchString == null) {
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
     * This method returns a brief summary of the photographic settings
     *
     * @return Returns a brief summary of the photographic settings
     */
    public String getBriefPhotographicSummary() {
        return Settings.jpoResources.getString("ExifInfoCamera") + "\t" + camera + "\n"
                + Settings.jpoResources.getString("ExifInfoShutterSpeed") + "\t" + shutterSpeed + "\n"
                + Settings.jpoResources.getString("ExifInfoAperture") + "\t" + aperture + "\n"
                + Settings.jpoResources.getString("ExifInfoTimeStamp") + "\t" + getCreateDateTime() + "\n";
    }

    /**
     * This method returns a comprehensive summary of the photographic settings
     *
     * @return Returns a comprehensive summary of the photographic settings
     */
    public String getComprehensivePhotographicSummary() {
        return Settings.jpoResources.getString("ExifInfoCamera") + "\t" + camera + "\n"
                + Settings.jpoResources.getString("ExifInfoLens") + "\t" + lens + "\n"
                + Settings.jpoResources.getString("ExifInfoShutterSpeed") + "\t" + shutterSpeed + "\n"
                + Settings.jpoResources.getString("ExifInfoAperture") + "\t" + aperture + "\n"
                + Settings.jpoResources.getString("ExifInfoFocalLength") + "\t" + focalLength + "\n"
                + Settings.jpoResources.getString("ExifInfoISO") + "\t" + iso + "\n"
                + Settings.jpoResources.getString("ExifInfoTimeStamp") + "\t" + getCreateDateTime() + "\n"
                + Settings.jpoResources.getString("ExifInfoLatitude") + "\t" + latitude + " " + latitudeRef + "\n"
                + Settings.jpoResources.getString("ExifInfoLongitude") + "\t" + longitude + " " + longitudeRef + "\n";
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
    private void setCreateDateTime(String dateTime) {
        this.createDateTime = dateTime;
    }

    /**
     * Attempt to parse the GPS data
     *
     */
    private Point2D.Double parseGPS() {
        double longitudeD = 0f;
        double latitudeD = 0f;

        if ((longitude == null) || (latitude == null)) {
            return new Point2D.Double(latitudeD, longitudeD);
        }

        LOGGER.fine(String.format("Trying to parse longitude: %s and latitude: %s", longitude, latitude));
        Pattern pattern = Pattern.compile("(.*)\"(.*)'(.*)");
        Matcher longitudeMatcher = pattern.matcher(longitude);
        if (longitudeMatcher.matches()) {
            longitudeD = Integer.parseInt(longitudeMatcher.group(1)) + (Double.parseDouble(longitudeMatcher.group(2)) / 60) + (Double.parseDouble(longitudeMatcher.group(3)) / 3600);
            if (longitudeRef.equals("W")) {
                longitudeD *= -1f;
            }
            LOGGER.fine(String.format("Longitude %s matches %s %s %s --> %f", longitude, longitudeMatcher.group(1), longitudeMatcher.group(2), longitudeMatcher.group(3), longitudeD));
        } else {
            LOGGER.fine(String.format("Longitude %s made no sense", longitude));
        }
        Matcher latitudeMatcher = pattern.matcher(latitude);
        if (latitudeMatcher.matches()) {
            latitudeD = Integer.parseInt(latitudeMatcher.group(1)) + (Double.parseDouble(latitudeMatcher.group(2)) / 60) + (Double.parseDouble(latitudeMatcher.group(3)) / 3600);
            if (latitudeRef.equals("S")) {
                latitudeD *= -1f;
            }
            LOGGER.fine(String.format("Latitude %s matches %s %s %s --> %f", latitude, latitudeMatcher.group(1), latitudeMatcher.group(2), latitudeMatcher.group(3), latitudeD));
        } else {
            LOGGER.fine(String.format("Latitude %s made no sense", latitude));
        }
        return new Point2D.Double(latitudeD, longitudeD);

    }

    /**
     * Nicked from
     * http://stackoverflow.com/questions/15567010/what-is-a-good-alternative-of-ltrim-and-rtrim-in-java
     *
     * @param s String to rtrim
     * @return the rtrimmed string
     */
    public static String rtrim(String s) {
        int i = s.length() - 1;
        while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
            i--;
        }
        return s.substring(0, i + 1);
    }
}
