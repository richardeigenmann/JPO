package jpo.dataModel;

import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.text.StringEscapeUtils;

/*
 PictureInfo.java:  the definitions for picture data

 Copyright (C) 2002-2018  Richard Eigenmann.
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
 * Objects of this type represent a single picture in the collection. Since
 * SortableDefaultMutableTreeNodes allow user objects to be attached to the node
 * this is a convenient place to store all the information that we have about a
 * picture.
 * <p>
 * The class provides several convenience methods to access the information.
 * <p>
 * This class must implement the Serializable interface or Drag and Drop will
 * not work.
 *
 * @see GroupInfo
 */
public class PictureInfo implements Serializable {

    /**
     * Keep serialisation happy
     */
    private static final long serialVersionUID = 1;

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( PictureInfo.class.getName() );

    /**
     * Constructor method. Creates the object and sets up the variables.
     *
     * TODO: What a strange signature. When would I ever use this?? RE, 2015
     *
     * @param highresLocation The highres location
     * @param description	The description of the image
     * @param filmReference	The reference to the film if any
     *
     * public PictureInfo( String highresLocation, String description, String
     * filmReference ) { this.imageLocation = highresLocation; this.description
     * = description; this.filmReference = filmReference;
     *
     * LOGGER.log( Level.FINE, "Highres_Name: {0}", highresLocation );
     * LOGGER.log( Level.FINE, "description: {0}", description ); LOGGER.log(
     * Level.FINE, "filmReference: {0}", filmReference ); }
     */
    /**
     * Constructor method. Creates the object and sets up the variables.
     *
     * TODO: What a strange signature. When would I erver use this?? RE, 2015
     *
     * @param highresURL The filename of the high resolution image
     * @param description	The description of the image
     */
    public PictureInfo( URL highresURL,
            String description ) {
        this.imageLocation = highresURL.toString();
        this.description = description;
        //this.filmReference = filmReference;

        LOGGER.log( Level.FINE, "Highres_Name: {0}", imageLocation );
        LOGGER.log( Level.FINE, "description: {0}", description );
        //LOGGER.log( Level.FINE, "filmReference: {0}", filmReference );

    }

    /**
     * Constructor without options. All strings are set to blanks
     */
    public PictureInfo() {
        imageLocation = "";
        description = "";
        filmReference = "";
    }

    /**
     * Constructor with just filename as option.
     *
     * @param highresLocation The highres location as a String
     * @param description Description
     */
    public PictureInfo( String highresLocation, String description ) {
        this.imageLocation = highresLocation;
        this.description = description;
        filmReference = "";
    }

    /**
     * returns the description of the image in the default <code>toString</code>
     * method.
     *
     * @return description
     */
    @Override
    public String toString() {
        return description;
    }

    /**
     * this method writes all attributes of the picture in the JPO xml data
     * format.
     *
     * @param out	The Bufferer Writer receiving the xml data
     * @throws IOException if there is a drama writing the file.
     */
    public void dumpToXml( BufferedWriter out )
            throws IOException {
        dumpToXml( out, getImageLocation() );
    }

    /**
     * this method writes all attributes of the picture in the JPO xml data
     * format with the highres and lowres locations passed in as parameters.
     * This became necessary because when the XmlDistiller copies the pictures
     * to a new location we don't want to write the URLs of the original
     * pictures whilst all other attributes are retained.
     *
     * @param out	The Bufferer Writer receiving the xml data
     * @param highres	The URL of the highres file
     * @throws IOException If there was an IO error
     */
    public void dumpToXml( BufferedWriter out, String highres )
            throws IOException {
        out.write( "<picture>" );
        out.newLine();
        out.write( "\t<description><![CDATA[" + getDescription() + "]]></description>" );
        out.newLine();

        if ( ( getCreationTime() != null ) && ( getCreationTime().length() > 0 ) ) {
            out.write( "\t<CREATION_TIME><![CDATA[" + getCreationTime() + "]]></CREATION_TIME>" );
            out.newLine();
        }

        if ( highres.length() > 0 ) {
            out.write( "\t<file_URL>" + StringEscapeUtils.escapeXml11( highres ) + "</file_URL>" );
            out.newLine();
        }

        if ( checksum > Long.MIN_VALUE ) {
            out.write( "\t<checksum>" + Long.toString( checksum ) + "</checksum>" );
            out.newLine();
        }

        if ( getComment().length() > 0 ) {
            out.write( "\t<COMMENT>" + StringEscapeUtils.escapeXml11( getComment() ) + "</COMMENT>" );
            out.newLine();
        }

        if ( getPhotographer().length() > 0 ) {
            out.write( "\t<PHOTOGRAPHER>" + StringEscapeUtils.escapeXml11( getPhotographer() ) + "</PHOTOGRAPHER>" );
            out.newLine();
        }

        if ( getFilmReference().length() > 0 ) {
            out.write( "\t<film_reference>" + StringEscapeUtils.escapeXml11( getFilmReference() ) + "</film_reference>" );
            out.newLine();
        }

        if ( getCopyrightHolder().length() > 0 ) {
            out.write( "\t<COPYRIGHT_HOLDER>" + StringEscapeUtils.escapeXml11( getCopyrightHolder() ) + "</COPYRIGHT_HOLDER>" );
            out.newLine();
        }

        if ( getRotation() != 0 ) {
            //out.write( "\t<ROTATION>" + ( new Double( getRotation() ) ).toString() + "</ROTATION>" );
            out.write( String.format( "\t<ROTATION>%f</ROTATION>", getRotation() ) );
            out.newLine();
        }

        if ( latLng != null ) {
            out.write( String.format( "\t<LATLNG>%fx%f</LATLNG>", latLng.x, latLng.y ) );
            out.newLine();
        }

        if ( categoryAssignments != null ) {
            Iterator i = categoryAssignments.iterator();
            Integer assignment;
            while ( i.hasNext() ) {
                assignment = (Integer) i.next();
                out.write( "\t<categoryAssignment index=\"" + assignment.toString() + "\"/>" );
                out.newLine();
            }
        }

        out.write( "</picture>" );
        out.newLine();
    }

    //----------------------------------------
    /**
     * The description of the image.
     */
    private String description = "";

    /**
     * Returns the description of the image.
     *
     * @return	The description of the image.
     * @see #setDescription
     */
    public synchronized String getDescription() {
        return description;
    }

    /**
     * Sets the description of the image.
     *
     * @param desc New description of the image.
     * @see #getDescription
     */
    public synchronized void setDescription( String desc ) {
        LOGGER.log( Level.FINE, "setting description to: {0}", desc );
        if ( !desc.equals( description ) ) {
            description = desc;
            sendDescriptionChangedEvent();
        }
    }

    /**
     * Appends the text fragment to the description.
     *
     * @param s	The text fragment to append.
     */
    public synchronized void appendToDescription( String s ) {
        if ( s.length() > 0 ) {
            description = description.concat( s );
            sendDescriptionChangedEvent();
        }
    }

    /**
     * Checks whether the searchString parameter is contained in the
     * description. The search is case insensitive.
     *
     * @param	searchString	The string to search for.
     * @return	true if found. false if not.
     */
    public synchronized boolean descriptionContains( String searchString ) {
        return description.toUpperCase().contains( searchString.toUpperCase() );
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the description was updated.
     */
    private void sendDescriptionChangedEvent() {
        LOGGER.fine( "preparing to send description changed event" );
        if ( Settings.getPictureCollection().getSendModelUpdates() ) {
            PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
            pce.setDescriptionChanged();
            sendPictureInfoChangedEvent( pce );
            LOGGER.fine( "sent description changed event" );
            Settings.getPictureCollection().setUnsavedUpdates();
        }
    }

    //----------------------------------------
    /**
     * The full path to the high resolution version of the picture.
     *
     *
     */
    private String imageLocation = "";

    /**
     * Returns the full path to the highres picture.
     *
     * @return The highres location
     * @see #setImageLocation
     */
    public synchronized String getImageLocation() {
        return imageLocation;
    }

    /**
     * returns the file handle to the highres picture.
     *
     * @see	#getImageURL()
     * @return the highres location or null if there is a failure
     */
    public synchronized File getImageFile() {
        File returnFile;
        try {
            returnFile = new File( new URI( imageLocation ) );
        } catch ( IllegalArgumentException | URISyntaxException x ) {
            LOGGER.severe( x.getMessage() );
            return null;
        }
        return returnFile;
    }

    /**
     * returns the URL handle to the picture.
     *
     * @return the image location
     * @throws MalformedURLException if the location could not be converted to a
     * URL.
     */
    public synchronized URL getImageURL() throws MalformedURLException {
        URL highresURL = new URL( imageLocation );
        return highresURL;
    }

    /**
     * returns the URL handle to the picture or null. I invented this because I
     * got fed up trying and catching the MalformedURLException that could be
     * thrown.
     *
     * @return the image location
     */
    public synchronized URL getImageURLOrNull() {
        try {
            URL highresURL = new URL( imageLocation );
            return highresURL;
        } catch ( MalformedURLException x ) {
            LOGGER.log( Level.FINE, "Caught an unexpected MalformedURLException: {0}", x.getMessage() );
            return null;
        }
    }

    /**
     * returns the URI handle to the picture.
     *
     * @return The image location
     */
    public synchronized URI getImageURIOrNull() {
        try {
            return new URI( imageLocation );
        } catch ( IllegalArgumentException | URISyntaxException x ) {
            return null;
        }
    }

    /**
     * Sets the full path to the picture.
     *
     * @param s The new location for the picture.
     * @see #getImageLocation
     */
    public synchronized void setImageLocation( String s ) {
        if ( !imageLocation.equals( s ) ) {
            imageLocation = s;
            sendImageLocationChangedEvent();
        }
        getImageFile(); // just so that it creates a failure if the filename is not conform.
    }

    /**
     * Sets the full path to the picture.
     *
     * @param u The new location for the picture.
     */
    public synchronized void setImageLocation( URL u ) {
        String s = u.toString();
        if ( !imageLocation.equals( s ) ) {
            imageLocation = s;
            sendImageLocationChangedEvent();
        }
    }

    /**
     * Sets the image location
     *
     * @param file The new file of the picture.
     */
    public synchronized void setImageLocation( File file ) {
        try {
            PictureInfo.this.setImageLocation( file.toURI().toURL() );
        } catch ( MalformedURLException ex ) {
            LOGGER.severe( "How could we get a MalformedURLException if java is doing the toURI and toURL itself? " + ex.getMessage() );
        }
    }

    /**
     * Appends the text to the field (used by XML parser).
     *
     * @param s The text fragment to be added to the image Location
     */
    public synchronized void appendToImageLocation( String s ) {
        if ( s.length() > 0 ) {
            imageLocation = imageLocation.concat( s );
            sendImageLocationChangedEvent();
        }
    }

    /**
     * Returns just the Filename of the picture.
     *
     * @return the Filename
     */
    public synchronized String getImageFilename() {
        return new File( imageLocation ).getName();

    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the image location was updated.
     */
    private void sendImageLocationChangedEvent() {
        if ( Settings.getPictureCollection().getSendModelUpdates() ) {
            PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
            pce.setHighresLocationChanged();
            sendPictureInfoChangedEvent( pce );
            Settings.getPictureCollection().setUnsavedUpdates();
        }
    }

    //----------------------------------------
    /**
     * the value of the checksum of the image file
     */
    private long checksum = Long.MIN_VALUE;

    /**
     * Returns the value of the checksum or Long.MIN_VALUE if it is not set.
     *
     * @return the Checksum
     */
    public synchronized long getChecksum() {
        return checksum;
    }

    /**
     * returns the value of the checksum or the text "N/A" if not defined.
     *
     * @return the checksum
     */
    public synchronized String getChecksumAsString() {
        if ( checksum != Long.MIN_VALUE ) {
            return Long.toString( checksum );
        } else {
            return "N/A";
        }
    }

    /**
     * allows the checksum to be set
     *
     * @param newValue the new value
     */
    public synchronized void setChecksum( long newValue ) {
        checksum = newValue;
        sendChecksumChangedEvent();
    }

    /**
     * calculates the Adler32 checksum of the current picture.
     */
    public synchronized void calculateChecksum() {
        URL pictureURL = getImageURLOrNull();
        if ( pictureURL == null ) {
            LOGGER.log( Level.SEVERE, "Aborting due to bad URL: {0}", getImageLocation() );
            return;
        }

        InputStream in;
        try {
            in = pictureURL.openStream();
        } catch ( IOException x ) {
            LOGGER.severe( String.format( "Couldn't open URL %s. Leaving Checksum unchanged.", pictureURL.toString() ) );
            return;
        }

        BufferedInputStream bin = new BufferedInputStream( in );

        checksum = Tools.calculateChecksum( bin );

        LOGGER.log( Level.FINE, "Checksum is: {0}", Long.toString( checksum ) );
        sendChecksumChangedEvent();
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the checksum was updated.
     */
    private void sendChecksumChangedEvent() {
        if ( Settings.getPictureCollection().getSendModelUpdates() ) {
            PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
            pce.setChecksumChanged();
            sendPictureInfoChangedEvent( pce );
            Settings.getPictureCollection().setUnsavedUpdates();
        }
    }

    /**
     * Temporary variable to allow appending of characters as the XML file is
     * being read.
     */
    private String checksumString = "";

    /**
     * Appends the text fragment to the checksum field.
     *
     * @param s Text fragment
     */
    public synchronized void appendToChecksum( String s ) {
        if ( s.length() > 0 ) {
            checksumString = checksumString.concat( s );
            sendChecksumChangedEvent();
        }
    }

    /**
     * Converts the temporary checksumString to the checksum long.
     */
    public synchronized void parseChecksum() {
        try {
            LOGGER.log( Level.FINE, "PictureInfo.parseChecksum: {0}", checksumString );
            checksum = ( new Long( checksumString ) );
            checksumString = "";
        } catch ( NumberFormatException x ) {
            LOGGER.log( Level.INFO, "PictureInfo.parseChecksum: invalid checksum: {0} on picture: {1} --> Set to MIN", new Object[]{ checksumString, getImageFilename() } );
            checksum = Long.MIN_VALUE;
        }
        sendChecksumChangedEvent();
    }

    //----------------------------------------
    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the thumbnail was updated.
     */
    public void sendThumbnailChangedEvent() {
        if ( Settings.getPictureCollection().getSendModelUpdates() ) {
            PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
            pce.setThumbnailChanged();
            sendPictureInfoChangedEvent( pce );
        }
    }

    //----------------------------------------
    /**
     * The film reference of the image.
     */
    private String filmReference = "";

    /**
     * Appends the string to the filmRreference field.
     *
     * @param s Fragment to append to Film Reference
     */
    public synchronized void appendToFilmReference( String s ) {
        if ( s.length() > 0 ) {
            filmReference = filmReference.concat( s );
            sendFilmReferenceChangedEvent();
        }
    }

    /**
     * Returns the film reference.
     *
     * @return the film reference
     */
    public synchronized String getFilmReference() {
        return filmReference;
    }

    /**
     * Sets the film reference.
     *
     * @param s The new film reference.
     */
    public synchronized void setFilmReference( String s ) {
        if ( !filmReference.equals( s ) ) {
            filmReference = s;
            sendFilmReferenceChangedEvent();
        }
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the film reference was updated.
     */
    private void sendFilmReferenceChangedEvent() {
        if ( Settings.getPictureCollection().getSendModelUpdates() ) {
            PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
            pce.setFilmReferenceChanged();
            sendPictureInfoChangedEvent( pce );
            Settings.getPictureCollection().setUnsavedUpdates();
        }
    }

    //----------------------------------------
    /**
     * The time the image was created. This should be the original time when the
     * shutter snapped closed and not the time of scanning etc.
     */
    private String creationTime = "";

    /**
     * Sets the creationTime.
     *
     * @param s The new creation time.
     */
    public synchronized void setCreationTime( String s ) {
        if ( ( s != null ) && ( !creationTime.equals( s ) ) ) {
            creationTime = s;
            sendCreationTimeChangedEvent();
        }
    }

    /**
     * appends the text fragement to the creation time.
     *
     * @param s The text fragment to add.
     */
    public synchronized void appendToCreationTime( String s ) {
        if ( s.length() > 0 ) {
            creationTime = creationTime.concat( s );
            sendCreationTimeChangedEvent();
        }
    }

    /**
     * Returns the creation Time.
     *
     * @return the creation Time
     */
    public synchronized String getCreationTime() {
        return creationTime;
    }

    /**
     * Returns the creationTime as a Date object or null if the parsing failed.
     *
     * @return the creation time
     */
    public synchronized Calendar getCreationTimeAsDate() {
        return ( Tools.parseDate( creationTime ) );
    }

    /**
     * Returns the creationTime as a string after it has been parsed.
     * Essentially this is a utility method to identify what the Date parser is
     * doing.
     *
     * @return the creation time
     */
    public synchronized String getFormattedCreationTime() {
        Calendar dateTime = getCreationTimeAsDate();
        return getFormattedCreationTime( dateTime );
    }

    /**
     * Returns the creationTime as a formatted String. If the dateTime is null a
     * polite "Failed to Parse" string is returned
     *
     * @param dateTime the Calendar to format
     * @return the creation time as a formatted string
     */
    public static String getFormattedCreationTime( Calendar dateTime ) {
        String formattedDate;
        if ( dateTime == null ) {
            formattedDate = Settings.jpoResources.getString( "failedToParse" );
        } else {
            formattedDate = Settings.jpoResources.getString( "parsedAs" )
                    + String.format( "%tc", dateTime );
        }
        return formattedDate;
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the film reference was updated.
     */
    private void sendCreationTimeChangedEvent() {
        if ( Settings.getPictureCollection().getSendModelUpdates() ) {
            PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
            pce.setCreationTimeChanged();
            sendPictureInfoChangedEvent( pce );
            Settings.getPictureCollection().setUnsavedUpdates();
        }
    }

    //----------------------------------------
    /**
     * The time the image was created. This should be the original time when the
     * shutter snapped closed and not the time of scanning etc.
     */
    private String comment = "";

    /**
     * Sets the comment.
     *
     * @param s The new comment
     */
    public synchronized void setComment( String s ) {
        if ( !comment.equals( s ) ) {
            comment = s;
            sendCommentChangedEvent();
        }
    }

    /**
     * Appends the text fragment to the comment.
     *
     * @param s the text fragment
     */
    public synchronized void appendToComment( String s ) {
        if ( s.length() > 0 ) {
            comment = comment.concat( s );
            sendCommentChangedEvent();
        }
    }

    /**
     * Returns the comment.
     *
     * @return The comment.
     */
    public synchronized String getComment() {
        return comment;
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the comment was updated.
     */
    private void sendCommentChangedEvent() {
        if ( Settings.getPictureCollection().getSendModelUpdates() ) {
            PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
            pce.setCommentChanged();
            sendPictureInfoChangedEvent( pce );
            Settings.getPictureCollection().setUnsavedUpdates();
        }
    }

    //----------------------------------------
    /**
     * The time the image was created. This should be the original time when the
     * shutter snapped closed and not the time of scanning etc.
     */
    private String photographer = "";

    /**
     * Sets the Photographer.
     *
     * @param s The new Photographer
     */
    public synchronized void setPhotographer( String s ) {
        if ( !photographer.equals( s ) ) {
            photographer = s;
            sendPhotographerChangedEvent();
        }
    }

    /**
     * Appends the text fragment to the photographer field.
     *
     * @param s The photographer.
     */
    public synchronized void appendToPhotographer( String s ) {
        if ( s.length() > 0 ) {
            photographer = photographer.concat( s );
            sendPhotographerChangedEvent();
        }
    }

    /**
     * Returns the photographer.
     *
     * @return The Photographer.
     */
    public synchronized String getPhotographer() {
        return photographer;
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the photographer was updated.
     */
    private void sendPhotographerChangedEvent() {
        if ( Settings.getPictureCollection().getSendModelUpdates() ) {
            PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
            pce.setPhotographerChanged();
            sendPictureInfoChangedEvent( pce );
            Settings.getPictureCollection().setUnsavedUpdates();
        }
    }

    //----------------------------------------
    /**
     * The copyright holder of the image.
     */
    private String copyrightHolder = "";

    /**
     * Sets the copyright holder.
     *
     * @param s The copyright holder
     */
    public synchronized void setCopyrightHolder( String s ) {
        if ( !copyrightHolder.equals( s ) ) {
            copyrightHolder = s;
            sendCopyrightHolderChangedEvent();
        }
    }

    /**
     * appends the text fragment to the copyright holder field.
     *
     * @param s The text fragment.
     */
    public synchronized void appendToCopyrightHolder( String s ) {
        if ( s.length() > 0 ) {
            copyrightHolder = copyrightHolder.concat( s );
            sendCopyrightHolderChangedEvent();
        }
    }

    /**
     * returns the copyright holder.
     *
     * @return The copyright holder
     */
    public synchronized String getCopyrightHolder() {
        return copyrightHolder;
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the copyright holder was updated.
     */
    private void sendCopyrightHolderChangedEvent() {
        if ( Settings.getPictureCollection().getSendModelUpdates() ) {
            PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
            pce.setCopyrightHolderChanged();
            sendPictureInfoChangedEvent( pce );
            Settings.getPictureCollection().setUnsavedUpdates();
        }
    }

    //----------------------------------------
    /**
     * The rotation factor to apply after loading the image.
     */
    private double rotation;  // default is 0

    /**
     * Temporary variable to allow appending of characters as the XML file is
     * being read.
     */
    private String rotationString = "";

    /**
     * Appends the text fragment to the rotation field.
     *
     * @param s Text fragment
     */
    public synchronized void appendToRotation( String s ) {
        if ( s.length() > 0 ) {
            rotationString = rotationString.concat( s );
            sendRotationChangedEvent();
        }
    }

    /**
     * Converts the temporary rotationString to the rotation double.
     */
    public synchronized void parseRotation() {
        try {
            rotation = ( new Double( rotationString ) );
            rotationString = null;
        } catch ( NumberFormatException x ) {
            LOGGER.log( Level.INFO, "invalid rotation: {0} on picture: {1} --> Set to Zero", new Object[]{ rotationString, getImageFilename() } );
            rotation = 0;
        }
        sendRotationChangedEvent();

    }

    /**
     * Returns the rotation.
     *
     * @return The rotation of the image.
     */
    public synchronized double getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation.
     *
     * @param rotation The new rotation for the PictureInfo.
     */
    public synchronized void setRotation( double rotation ) {
        if ( this.rotation != rotation ) {
            this.rotation = rotation;
            sendRotationChangedEvent();
        }
    }

    /**
     * Changes the angle by the supplied angle the picture by an angle.
     *
     * @param angle the new angle
     */
    public synchronized void rotate( double angle ) {
        setRotation( ( getRotation() + angle ) % 360 );
    }

    /**
     * Sets the rotation.
     *
     * @param rotation The new rotation for the PictureInfo.
     */
    public synchronized void setRotation( int rotation ) {
        setRotation( (double) rotation );
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the copyright holder was updated.
     */
    private void sendRotationChangedEvent() {
        if ( Settings.getPictureCollection().getSendModelUpdates() ) {
            PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
            pce.setRotationChanged();
            sendPictureInfoChangedEvent( pce );
            Settings.getPictureCollection().setUnsavedUpdates();
        }
    }

    //----------------------------------------
    /**
     * The copyright holder of the image.
     */
    private Point2D.Double latLng;

    /**
     * Sets the Latitude and Longitude.
     *
     * @param newLatLng The latitude and longitude holder
     */
    public synchronized void setLatLng( Point2D.Double newLatLng ) {
        if ( ( latLng == null ) || ( latLng.x != newLatLng.x ) || ( latLng.y != newLatLng.y ) ) {
            latLng = newLatLng;
            sendLatLngChangedEvent();
        }
    }

    /**
     * Sets the Latitude and Longitude.
     *
     * @param newLatLng The latitude and longitude in the format of 2 doubles
     * with an x
     */
    public synchronized void setLatLng( String newLatLng ) {
        this.latLngString = newLatLng;
        parseLatLng();
    }

    /**
     * Temporary variable to allow appending of characters as the XML file is
     * being read.
     */
    private String latLngString = "";

    /**
     * appends the text fragment to the latlng string.
     *
     * @param s The text fragment.
     */
    public synchronized void appendToLatLng( String s ) {
        if ( s.length() > 0 ) {
            latLngString = latLngString.concat( s );
        }
    }

    /**
     * Converts the temporary latLngString to a LatLng Point.
     */
    public synchronized void parseLatLng() {
        try {
            String[] latLngArray = latLngString.split( "x" );
            Double lat = ( new Double( latLngArray[0] ) );
            Double lng = ( new Double( latLngArray[1] ) );
            setLatLng( new Point2D.Double( lat, lng ) );
            latLngString = null;
        } catch ( NumberFormatException x ) {
            LOGGER.info( String.format( "Failed to parse string %s into latitude and longitude", latLngString ) );
        }
    }

    /**
     * returns the Latitude and Longitude.
     *
     * @return The Latitude and Longitude
     */
    public synchronized Point2D.Double getLatLng() {
        if ( latLng == null ) {
            setLatLng( new Point2D.Double( 0, 0 ) );
        }
        return latLng;
    }

    /**
     * returns the Latitude and Longitude as a String
     *
     * @return The latitude and longitude in the format of 2 doubles with an x
     */
    public synchronized String getLatLngString() {
        Point2D.Double latLang = getLatLng();
        NumberFormat numberFormatter;
        numberFormatter = NumberFormat.getNumberInstance();
        String formattedString = numberFormatter.format( latLang.x ) + "x" + numberFormatter.format( latLang.y );
        return formattedString;

    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the copyright holder was updated.
     */
    private void sendLatLngChangedEvent() {
        if ( Settings.getPictureCollection().getSendModelUpdates() ) {
            PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
            pce.setLatLngChanged();
            sendPictureInfoChangedEvent( pce );
            Settings.getPictureCollection().setUnsavedUpdates();
        }
    }

    //----------------------------------------
    /**
     * The category assignments are held in the categoryAssignments HashSet.
     */
    public HashSet<Object> categoryAssignments;

    /**
     * removes all category Assignments
     */
    public synchronized void clearCategoryAssignments() {
        if ( categoryAssignments != null ) {
            sendCategoryAssignmentsChangedEvent();
            categoryAssignments.clear();
        }
    }

    /**
     * Temporary variable to allow appending of characters as the XML file is
     * being read.
     */
    private String categoryAssignmentString = "";

    /**
     * Returns an Array of the category assignments associated with this picture
     *
     * @return the category assignments as an array
     */
    public synchronized Object[] getCategoryAssignmentsAsArray() {
        return categoryAssignments.toArray();
    }

    /**
     * Appends the text fragment to the categoryAssignmentString field.
     *
     * @param string Text fragment
     */
    public synchronized void appendToCategoryAssignment( String string ) {
        if ( string.length() > 0 ) {
            categoryAssignmentString = categoryAssignmentString.concat( string );
        }
    }

    /**
     * Adds to the categoryAssignmentString HashSet.
     *
     * @param string Text fragment
     */
    public synchronized void addCategoryAssignment( String string ) {
        if ( string.length() > 0 ) {
            categoryAssignmentString = string;
            parseCategoryAssignment();
        }
    }

    /**
     * Adds the supplied Object to the categoryAssignment HashSet. If the Object
     * already existed it doesn't get added a second time.
     *
     * @param key the key to add
     */
    public synchronized void addCategoryAssignment( Object key ) {
        if ( categoryAssignments == null ) {
            categoryAssignments = new HashSet<>();
        }
        if ( categoryAssignments.add( key ) ) {
            sendCategoryAssignmentsChangedEvent();
        }
    }

    /**
     * sets the supplied HashSet as the valid HashSet for the Categories of the
     * picture
     *
     * @param ca the supplied hash set
     */
    public synchronized void setCategoryAssignment( HashSet<Object> ca ) {
        if ( ca != null ) {
            categoryAssignments = ca;
        }
    }

    /**
     * Converts the temporary categoryAssignmentString to a categoryAssignment.
     */
    public synchronized void parseCategoryAssignment() {
        try {
            Integer category = new Integer( categoryAssignmentString );
            categoryAssignmentString = "";
            addCategoryAssignment( category );
        } catch ( NumberFormatException x ) {
            LOGGER.log( Level.INFO, "PictureInfo.parseCategoryAssignment: NumberFormatException: {0} on picture: {1} because: {2}", new Object[]{ categoryAssignmentString, getImageFilename(), x.getMessage() } );
        }
        sendCategoryAssignmentsChangedEvent();
    }

    /**
     * Returns whether the category is part of the attributes of the picture
     *
     * @param key the key
     * @return true if the key was in the categories
     */
    public synchronized boolean containsCategory( Object key ) {
        if ( categoryAssignments == null ) {
            return false;
        }
        return categoryAssignments.contains( key );
    }

    /**
     * Removes the supplied category from the picture if it was there
     *
     * @param key the key to search for
     */
    public synchronized void removeCategory( Object key ) {
        if ( categoryAssignments != null && categoryAssignments.remove( key ) ) {
            sendCategoryAssignmentsChangedEvent();
        }
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the copyright holder was updated.
     */
    private void sendCategoryAssignmentsChangedEvent() {
        if ( Settings.getPictureCollection().getSendModelUpdates() ) {
            PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
            pce.setCategoryAssignmentsChanged();
            sendPictureInfoChangedEvent( pce );
            Settings.getPictureCollection().setUnsavedUpdates();
        }
    }

    //-------------------------------------------
    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the node was selected. Strictly speaking this is not a PictureInfo
     * level event but a node level event. However, because I have the
     * PictureInfoChangeEvent structure in place this is a good place to put
     * this notification.
     */
    public void sendWasSelectedEvent() {
        PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
        pce.setWasSelected();
        sendPictureInfoChangedEvent( pce );
    }

    //-------------------------------------------
    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the node was unselected. Strictly speaking this is not a PictureInfo
     * level event but a node level event. However, because I have the
     * PictureInfoChangeEvent structure in place this is a good place to put
     * this notification.
     */
    public void sendWasUnselectedEvent() {
        PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
        pce.setWasUnselected();
        sendPictureInfoChangedEvent( pce );
    }

    //-------------------------------------------
    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the node was mailSelected. Strictly speaking this is not a
     * PictureInfo level event but a node level event. However, because I have
     * the PictureInfoChangeEvent structure in place this is a good place to put
     * this notification.
     */
    public void sendWasMailSelectedEvent() {
        PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
        pce.setWasMailSelected();
        sendPictureInfoChangedEvent( pce );
    }

    //-------------------------------------------
    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the node was mailUnselected. Strictly speaking this is not a
     * PictureInfo level event but a node level event. However, because I have
     * the PictureInfoChangeEvent structure in place this is a good place to put
     * this notification.
     */
    public void sendWasMailUnselectedEvent() {
        PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
        pce.setWasMailUnselected();
        sendPictureInfoChangedEvent( pce );
    }

    //-------------------------------------------
    /**
     * Returns a new PictureInfo object which is identical to the current one.
     *
     * @return a clone of the current PictureInfo object.
     */
    public PictureInfo getClone() {
        PictureInfo clone = new PictureInfo();
        clone.setDescription( this.getDescription() );
        clone.setImageLocation( this.getImageLocation() );
        clone.setFilmReference( this.getFilmReference() );
        clone.setCreationTime( this.getCreationTime() );
        clone.setComment( this.getComment() );
        clone.setPhotographer( this.getPhotographer() );
        clone.setCopyrightHolder( this.getCopyrightHolder() );
        clone.setRotation( this.getRotation() );
        return clone;
    }

    /**
     * The listeners to be notified about changes to this PictureInfo object.
     */
    private final transient Set<PictureInfoChangeListener> pictureInfoListeners = Collections.synchronizedSet( new HashSet<PictureInfoChangeListener>() );

    /**
     * Registers a listener for picture info change events
     *
     * @param pictureInfoChangeListener	The object that will receive
     * notifications.
     */
    public void addPictureInfoChangeListener( PictureInfoChangeListener pictureInfoChangeListener ) {
        pictureInfoListeners.add( pictureInfoChangeListener );
    }

    /**
     * Removes the supplied listener
     *
     * @param pictureInfoChangeListener	The listener that doesn't want to
     * notifications any more.
     */
    public void removePictureInfoChangeListener(
            PictureInfoChangeListener pictureInfoChangeListener ) {
        pictureInfoListeners.remove( pictureInfoChangeListener );
    }

    /**
     * Send PictureInfoChangeEvents.
     *
     * @param pce The Event we want to notify.
     */
    private void sendPictureInfoChangedEvent( PictureInfoChangeEvent pce ) {
        if ( Settings.getPictureCollection().getSendModelUpdates() ) {
            synchronized ( pictureInfoListeners ) {
                pictureInfoListeners.stream().forEach( pictureInfoChangeListener
                        -> pictureInfoChangeListener.pictureInfoChangeEvent( pce )
                );
            }
        }
    }

    //-------------------------------------------
    /**
     * Checks whether the searchString parameter is contained in any of the
     * fields. It doesn't check the checksum, lowres filename, rotation. It does
     * check the description, highres name, film reference, creation time,
     * comment and copyright holder
     *
     * @param	searchString	The string to search for.
     * @return	true if found. false if not.
     */
    public synchronized boolean anyMatch( String searchString ) {
        String uppercaseSearchString = searchString.toUpperCase();
        boolean found = descriptionContains( searchString )
                || ( getPhotographer().toUpperCase().contains( uppercaseSearchString ) )
                || ( imageLocation.toUpperCase().contains( uppercaseSearchString ) )
                || ( getFilmReference().toUpperCase().contains( uppercaseSearchString ) )
                || ( getCreationTime().toUpperCase().contains( uppercaseSearchString ) )
                || ( getComment().toUpperCase().contains( uppercaseSearchString ) )
                || ( getCopyrightHolder().toUpperCase().contains( uppercaseSearchString ) );

        return found;
    }
}
