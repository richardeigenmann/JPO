package org.jpo.datamodel;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.geom.Point2D;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 PictureInfo.java:  the definitions for picture data

 Copyright (C) 2002-2023 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
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
public class PictureInfo implements Serializable, GroupOrPicture {

    /**
     * Keep serialisation happy
     */
    @Serial
    private static final long serialVersionUID = 1;

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger(PictureInfo.class.getName());
    /**
     * The listeners to be notified about changes to this PictureInfo object.
     */
    private final transient Set<PictureInfoChangeListener> pictureInfoListeners = Collections.synchronizedSet(new HashSet<>());
    /**
     * The description of the image.
     */
    private String description = "";
    //----------------------------------------
    private File imageFile;
    private String myImageLocation = "";

    //----------------------------------------
    /**
     * the hash code of the contents of the file. We use SHA-256 here in 2020.
     */
    private String sha256 = "";

    /**
     * The film reference of the image.
     */
    private String filmReference = "";
    /**
     * The time the image was created. This should be the original time when the
     * shutter snapped closed and not the time of scanning etc.
     */
    private String creationTime = "";
    /**
     * The time the image was created. This should be the original time when the
     * shutter snapped closed and not the time of scanning etc.
     */
    private String comment = "";
    /**
     * The time the image was created. This should be the original time when the
     * shutter snapped closed and not the time of scanning etc.
     */
    private String photographer = "";
    /**
     * The copyright holder of the image.
     */
    private String copyrightHolder = "";
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
     * The copyright holder of the image.
     */
    private Point2D.Double latLng;
    /**
     * Temporary variable to allow appending of characters as the XML file is
     * being read.
     */
    private String latLngString = "";
    /**
     * The category assignments are held in the categoryAssignments HashSet.
     */
    private final Set<Integer> categoryAssignments = new HashSet<>();
    /**
     * Temporary variable to allow appending of characters as the XML file is
     * being read.
     */
    private String categoryAssignmentString = "";

    //----------------------------------------

    /**
     * Constructor without options. All strings are set to blanks
     */
    public PictureInfo() {
    }

    /**
     * Constructor with just filename as option.
     *
     * @param imageFile   The file of the image
     * @param description Description
     */
    public PictureInfo(final File imageFile, final String description) {
        setImageLocation(imageFile);
        this.description = description;
        filmReference = "";
    }

    /**
     * Returns the creationTime as a formatted String. If the dateTime is null a
     * polite "Failed to Parse" string is returned
     *
     * @param dateTime the Calendar to format
     * @return the creation time as a formatted string
     */
    public static String getFormattedCreationTime(final Calendar dateTime) {
        if (dateTime == null) {
            return Settings.getJpoResources().getString("failedToParse");
        }
        return Settings.getJpoResources().getString("parsedAs")
                    + String.format("%tc", dateTime);
    }

    /**
     * Returns the creationTime as a formatted String. If parsing doesn't work it returns an empty string
     *
     * @return the creation time as a formatted string
     */
    public String getFormattedCreationTimeForTimestamp() {
        final Calendar dateTime = getCreationTimeAsDate();
        if (dateTime == null) {
            return "";
        }
        return String.format("%tc", dateTime);
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
     * format with the highres and lowres locations passed in as parameters.
     * This became necessary because when the XmlDistiller copies the pictures
     * to a new location we don't want to write the URLs of the original
     * pictures whilst all other attributes are retained.
     *
     * @param out The Buffered Writer receiving the xml data
     * @throws IOException If there was an IO error
     */
    public void dumpToXml(final BufferedWriter out, final Path baseDir)
            throws IOException {
        out.write("<picture>");
        out.newLine();
        out.write("\t<description><![CDATA[" + getDescription() + "]]></description>");
        out.newLine();

        if ((getCreationTime() != null) && (getCreationTime().length() > 0)) {
            out.write("\t<CREATION_TIME><![CDATA[" + getCreationTime() + "]]></CREATION_TIME>");
            out.newLine();
        }

        if (getImageFile().toURI().toString().length() > 0) {
            final var file = getImageFile();
            final var relativeImageFile = getRelativePath(file, baseDir);
            out.write("\t<file>" + StringEscapeUtils.escapeXml11(relativeImageFile.toString()) + "</file>");
            out.newLine();
        }

        if ((!sha256.equals("")) && (!sha256.equals("N/A"))) {
            out.write("\t<sha256>" + sha256 + "</sha256>");
            out.newLine();
        }

        if (getComment().length() > 0) {
            out.write("\t<COMMENT>" + StringEscapeUtils.escapeXml11(getComment()) + "</COMMENT>");
            out.newLine();
        }

        if (getPhotographer().length() > 0) {
            out.write("\t<PHOTOGRAPHER>" + StringEscapeUtils.escapeXml11(getPhotographer()) + "</PHOTOGRAPHER>");
            out.newLine();
        }

        if (getFilmReference().length() > 0) {
            out.write("\t<film_reference>" + StringEscapeUtils.escapeXml11(getFilmReference()) + "</film_reference>");
            out.newLine();
        }

        if (getCopyrightHolder().length() > 0) {
            out.write("\t<COPYRIGHT_HOLDER>" + StringEscapeUtils.escapeXml11(getCopyrightHolder()) + "</COPYRIGHT_HOLDER>");
            out.newLine();
        }

        if (getRotation() != 0) {
            out.write(String.format("\t<ROTATION>%f</ROTATION>", getRotation()));
            out.newLine();
        }

        if (latLng != null) {
            out.write(String.format("\t<LATLNG>%fx%f</LATLNG>", latLng.x, latLng.y));
            out.newLine();
        }

        if (categoryAssignments != null) {
            for (final var categoryAssignment : categoryAssignments) {
                out.write("\t<categoryAssignment index=\"" + categoryAssignment + "\"/>");
                out.newLine();
            }
        }

        out.write("</picture>");
        out.newLine();
    }

    public static Path getRelativePath(final File imageFile, final Path baseDir) {
        return baseDir.relativize(imageFile.toPath());
    }

    /**
     * Returns the description of the image.
     *
     * @return The description of the image.
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
    public synchronized void setDescription(final String desc) {
        LOGGER.log(Level.FINE, "setting description to: {0}", desc);
        if (!desc.equals(description)) {
            description = desc;
            sendDescriptionChangedEvent();
        }
    }

    /**
     * Appends the text fragment to the description.
     *
     * @param s The text fragment to append.
     */
    public synchronized void appendToDescription(final String s) {
        if (s.length() > 0) {
            description = description.concat(s);
            sendDescriptionChangedEvent();
        }
    }

    /**
     * Checks whether the searchString parameter is contained in the
     * description. The search is case-insensitive.
     *
     * @param searchString The string to search for.
     * @return true if found. false if not.
     */
    public synchronized boolean descriptionContains(final String searchString) {
        return description.toUpperCase().contains(searchString.toUpperCase());
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the description was updated.
     */
    private void sendDescriptionChangedEvent() {
        LOGGER.fine("preparing to send description changed event");
        final var owningNode = getOwningNode();
        if (owningNode == null ) {
            // no owning node, no change notification
            return;
        }
        final var pictureCollection = owningNode.getPictureCollection();

        if (pictureCollection.getSendModelUpdates()) {
            final var pictureInfoChangeEvent = new PictureInfoChangeEvent(this);
            pictureInfoChangeEvent.setDescriptionChanged();
            sendPictureInfoChangedEvent(pictureInfoChangeEvent);
            LOGGER.fine("sent description changed event");
            pictureCollection.setUnsavedUpdates();
        }
    }

    /**
     * Returns the full path to the highres picture. If the PictureInto doesn't have an image location an empty
     * String is returned.
     *
     * @return The highres location
     * @see #setImageLocation
     */
    public synchronized String getImageLocation() {
        final var file = getImageFile();
        if (file != null) {
            return file.toURI().toString();
        }
        return "";
    }

    /**
     * Sets the image location and sends a sendImageLocationChangedEvent.
     *
     * @param file The new file of the picture.
     */
    public synchronized void setImageLocation(@NotNull final File file) {
        Objects.requireNonNull(file);
        imageFile = file;
        sendImageLocationChangedEvent();
    }

    /**
     * returns the file handle to the highres picture.
     *
     * @return the highres location or null if there is a failure
     */
    public synchronized File getImageFile() {
        return imageFile;
    }

    /**
     * returns the URI handle to the picture.
     *
     * @return The image location
     */
    public synchronized URI getImageURIOrNull() {
        try {
            return getImageFile().toURI();
        } catch (final NullPointerException | IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Appends the text to the field (used by XML parser).
     *
     * @param s The text fragment to be added to the image Location
     */
    public synchronized void appendToImageLocation(final String s) {
        if (s.length() > 0) {
            myImageLocation = myImageLocation.concat(s);
            try {
                imageFile = new File(new URI(myImageLocation));
            } catch (final URISyntaxException e) {
                // Ignore it
                LOGGER.log(Level.INFO, "Exception when parsing ImageLocation: {0} Exception: {1}", new Object[]{myImageLocation, e.getMessage()});
            }
            sendImageLocationChangedEvent();
        }
    }


    private String myImageFile = "";

    /**
     * Appends the text to the field (used by XML parser).
     *
     * @param s The text fragment to be added to the image Location
     */
    public synchronized void appendToImageFile(final String s, final Path baseDir) {
        if (s.length() > 0) {
            myImageFile = myImageFile.concat(s);
            imageFile = new File(baseDir.toFile(), myImageFile);
            sendImageLocationChangedEvent();
        }
    }


    //----------------------------------------

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the image location was updated.
     */
    private void sendImageLocationChangedEvent() {
        final var owningNode = getOwningNode();
        if (owningNode == null ) {
            // no owning node, no change notification
            return;
        }
        final var pictureCollection = owningNode.getPictureCollection();
        if (pictureCollection != null && pictureCollection.getSendModelUpdates()) {
            final var pictureInfoChangeEvent = new PictureInfoChangeEvent(this);
            pictureInfoChangeEvent.setHighresLocationChanged();
            sendPictureInfoChangedEvent(pictureInfoChangeEvent);
        }
    }

    /**
     * Returns the SHA-256 of the contents of the file
     *
     * @return the SHA-256 of the image file or null if not calculated
     */
    public synchronized String getSha256() {
        return sha256;
    }

    //----------------------------------------


    /**
     * calculates the SHA-256 hash of the picture.
     *
     * @return returns a HashCode object containing the SHA256 of the Image File
     * @throws IOException if the underlying library encounters and {@link IOException}
     */
    public static String calculateSha256(final File file) throws IOException {
        final var hash = Files.asByteSource(file).hash(Hashing.sha256());
        if (hash == null) {
            return "";
        }
        LOGGER.log(Level.FINE, "SHA-256 of file {0} is {1}", new Object[]{file, hash.toString().toUpperCase()});
        return hash.toString().toUpperCase();
    }

    /**
     * calculates the SHA-256 hash of the picture and saves it to the sha256 member
     * variable. If the value changes it sends a PictureInfoChangedEvent
     */
    public void setSha256() {
        try {
            setSha256(calculateSha256(getImageFile()));
        } catch (final IOException e) {
            LOGGER.severe("Could not create SHA-256 code: " + e.getMessage());
            sha256 = "";
            sendSha256ChangedEvent();
        }
    }

    /**
     * calculates the SHA-256 hash of the picture and saves it to the fileHash member
     * variable. If the value changes it sends a PictureInfoChangedEvent
     */
    public synchronized void setSha256(final String newSha256) {
        if ((sha256 == null) || (!sha256.equals(newSha256))) {
            sha256 = newSha256;
            sendSha256ChangedEvent();
        }
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the fileHash was updated.
     */
    private void sendSha256ChangedEvent() {
        final var owningNode = getOwningNode();
        if (owningNode == null ) {
            // no owning node, no change notification
            return;
        }
        final var pictureCollection = owningNode.getPictureCollection();

        if (pictureCollection.getSendModelUpdates()) {
            final var pictureInfoChangeEvent = new PictureInfoChangeEvent(this);
            pictureInfoChangeEvent.setSha256Changed();
            sendPictureInfoChangedEvent(pictureInfoChangeEvent);
            pictureCollection.setUnsavedUpdates();
        }
    }

    /**
     * Appends the text fragment to the sha256 field.
     *
     * @param s Text fragment
     */
    public synchronized void appendToSha256(final String s) {
        if (s.length() > 0) {
            sha256 = sha256.concat(s);
            sendSha256ChangedEvent();
        }
    }


    //----------------------------------------

    /**
     * Appends the string to the filmReference field.
     *
     * @param s Fragment to append to Film Reference
     */
    public synchronized void appendToFilmReference(final String s) {
        if (s.length() > 0) {
            filmReference = filmReference.concat(s);
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
     * @param newFilmReference The new film reference.
     */
    public synchronized void setFilmReference(final String newFilmReference) {
        if (!filmReference.equals(newFilmReference)) {
            filmReference = newFilmReference;
            sendFilmReferenceChangedEvent();
        }
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the film reference was updated.
     */
    private void sendFilmReferenceChangedEvent() {
        final var owningNode = getOwningNode();
        if (owningNode == null ) {
            // no owning node, no change notification
            return;
        }
        final var pictureCollection = owningNode.getPictureCollection();

        if (pictureCollection.getSendModelUpdates()) {
            final var pictureInfoChangeEvent = new PictureInfoChangeEvent(this);
            pictureInfoChangeEvent.setFilmReferenceChanged();
            sendPictureInfoChangedEvent(pictureInfoChangeEvent);
            pictureCollection.setUnsavedUpdates();
        }
    }

    /**
     * appends the text fragment to the creation time.
     *
     * @param textFragment The text fragment to add.
     */
    public synchronized void appendToCreationTime(final String textFragment) {
        if (textFragment.length() > 0) {
            creationTime = creationTime.concat(textFragment);
            sendCreationTimeChangedEvent();
        }
    }

    //----------------------------------------

    /**
     * Returns the creation Time.
     *
     * @return the creation Time
     */
    public synchronized String getCreationTime() {
        return creationTime;
    }

    /**
     * Sets the creationTime.
     *
     * @param newCreationTime The new creation time.
     */
    public synchronized void setCreationTime(final String newCreationTime) {
        if ((newCreationTime != null) && (!creationTime.equals(newCreationTime))) {
            creationTime = newCreationTime;
            sendCreationTimeChangedEvent();
        }
    }

    /**
     * Returns the creationTime as a Date object or null if the parsing failed.
     *
     * @return the creation time
     */
    public synchronized Calendar getCreationTimeAsDate() {
        return Tools.parseDate(creationTime);
    }

    /**
     * Returns the creationTime as a string after it has been parsed.
     * Essentially this is a utility method to identify what the Date parser is
     * doing.
     *
     * @return the creation time
     */
    public synchronized String getFormattedCreationTime() {
        final var dateTime = getCreationTimeAsDate();
        return getFormattedCreationTime(dateTime);
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the film reference was updated.
     */
    private void sendCreationTimeChangedEvent() {
        final var owningNode = getOwningNode();
        if (owningNode == null ) {
            // no owning node, no change notification
            return;
        }
        final var pictureCollection = owningNode.getPictureCollection();

        if (pictureCollection.getSendModelUpdates()) {
            final var pictureInfoChangeEvent = new PictureInfoChangeEvent(this);
            pictureInfoChangeEvent.setCreationTimeChanged();
            sendPictureInfoChangedEvent(pictureInfoChangeEvent);
            pictureCollection.setUnsavedUpdates();
        }
    }

    //----------------------------------------

    /**
     * Appends the text fragment to the comment.
     *
     * @param textFragment the text fragment
     */
    public synchronized void appendToComment(final String textFragment) {
        if (textFragment.length() > 0) {
            comment = comment.concat(textFragment);
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
     * Sets the comment.
     *
     * @param newComment The new comment
     */
    public synchronized void setComment(final String newComment) {
        if (!comment.equals(newComment)) {
            comment = newComment;
            sendCommentChangedEvent();
        }
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the comment was updated.
     */
    private void sendCommentChangedEvent() {
        final var owningNode = getOwningNode();
        if (owningNode == null ) {
            // no owning node, no change notification
            return;
        }
        final var pictureCollection = owningNode.getPictureCollection();

        if (pictureCollection.getSendModelUpdates()) {
            final var pictureInfoChangeEvent = new PictureInfoChangeEvent(this);
            pictureInfoChangeEvent.setCommentChanged();
            sendPictureInfoChangedEvent(pictureInfoChangeEvent);
            pictureCollection.setUnsavedUpdates();
        }
    }

    /**
     * Appends the text fragment to the photographer field.
     *
     * @param textFragment The photographer.
     */
    public synchronized void appendToPhotographer(final String textFragment) {
        if (textFragment.length() > 0) {
            photographer = photographer.concat(textFragment);
            sendPhotographerChangedEvent();
        }
    }

    //----------------------------------------

    /**
     * Returns the photographer.
     *
     * @return The Photographer.
     */
    public synchronized String getPhotographer() {
        return photographer;
    }

    /**
     * Sets the Photographer.
     *
     * @param newPhotographer The new Photographer
     */
    public synchronized void setPhotographer(final String newPhotographer) {
        if (!photographer.equals(newPhotographer)) {
            photographer = newPhotographer;
            sendPhotographerChangedEvent();
        }
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the photographer was updated.
     */
    private void sendPhotographerChangedEvent() {
        final var owningNode = getOwningNode();
        if (owningNode == null ) {
            // no owning node, no change notification
            return;
        }
        final var pictureCollection = owningNode.getPictureCollection();

        if (pictureCollection.getSendModelUpdates()) {
            final var pictureInfoChangeEvent = new PictureInfoChangeEvent(this);
            pictureInfoChangeEvent.setPhotographerChanged();
            sendPictureInfoChangedEvent(pictureInfoChangeEvent);
            pictureCollection.setUnsavedUpdates();
        }
    }

    /**
     * appends the text fragment to the copyright holder field.
     *
     * @param textFragment The text fragment.
     */
    public synchronized void appendToCopyrightHolder(final String textFragment) {
        if (textFragment.length() > 0) {
            copyrightHolder = copyrightHolder.concat(textFragment);
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
     * Sets the copyright holder.
     *
     * @param newCopyrightHolder The copyright holder
     */
    public synchronized void setCopyrightHolder(final String newCopyrightHolder) {
        if (!this.copyrightHolder.equals(newCopyrightHolder)) {
            this.copyrightHolder = newCopyrightHolder;
            sendCopyrightHolderChangedEvent();
        }
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the copyright holder was updated.
     */
    private void sendCopyrightHolderChangedEvent() {
        final var owningNode = getOwningNode();
        if (owningNode == null ) {
            // no owning node, no change notification
            return;
        }
        final var pictureCollection = owningNode.getPictureCollection();

        if (pictureCollection.getSendModelUpdates()) {
            final var pictureInfoChangeEvent = new PictureInfoChangeEvent(this);
            pictureInfoChangeEvent.setCopyrightHolderChanged();
            sendPictureInfoChangedEvent(pictureInfoChangeEvent);
            pictureCollection.setUnsavedUpdates();
        }
    }

    /**
     * Appends the text fragment to the rotation field.
     * does not send a rotationChangedEvent as the rotation has not yet been parsed
     *
     * @param textFragment Text fragment
     */
    public synchronized void appendToRotation(final String textFragment) {
        if (textFragment.length() > 0) {
            rotationString = rotationString.concat(textFragment);
        }
    }

    /**
     * Converts the temporary rotationString to the rotation double.
     */
    public synchronized void parseRotation() {
        try {
            rotation = Double.parseDouble(rotationString);
            rotationString = "";
        } catch (NumberFormatException x) {
            LOGGER.severe(String.format("Can't parse rotation: %s", rotationString));
            rotation = 0;
        }
        sendRotationChangedEvent();

    }

    //----------------------------------------

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
    public synchronized void setRotation(final double rotation) {
        if (this.rotation != rotation) {
            this.rotation = rotation;
            sendRotationChangedEvent();
        }
    }

    /**
     * Sets the rotation.
     *
     * @param rotation The new rotation for the PictureInfo.
     */
    public synchronized void setRotation(final int rotation) {
        setRotation((double) rotation);
    }

    /**
     * Changes the angle by the supplied angle the picture by an angle.
     *
     * @param angle the new angle
     */
    public synchronized void rotate(double angle) {
        setRotation((getRotation() + angle) % 360);
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the copyright holder was updated.
     */
    private void sendRotationChangedEvent() {
        final var owningNode = getOwningNode();
        if (owningNode == null ) {
            // no owning node, no change notification
            return;
        }
        final var pictureCollection = owningNode.getPictureCollection();

        if (pictureCollection.getSendModelUpdates()) {
            final var pictureInfoChangeEvent = new PictureInfoChangeEvent(this);
            pictureInfoChangeEvent.setRotationChanged();
            sendPictureInfoChangedEvent(pictureInfoChangeEvent);
            pictureCollection.setUnsavedUpdates();
        }
    }

    /**
     * appends the text fragment to the latlng string.
     *
     * @param textFragment The text fragment.
     */
    public synchronized void appendToLatLng(final String textFragment) {
        if (textFragment.length() > 0) {
            latLngString = latLngString.concat(textFragment);
        }
    }

    /**
     * Converts the temporary latLngString to a LatLng Point.
     */
    public synchronized void parseLatLng() {
        try {
            final var latLngArray = latLngString.split("x");
            final var lat = Double.parseDouble(latLngArray[0]);
            final var lng = Double.parseDouble(latLngArray[1]);
            setLatLng(new Point2D.Double(lat, lng));
            latLngString = null;
        } catch (final NumberFormatException x) {
            LOGGER.info(String.format("Failed to parse string %s into latitude and longitude", latLngString));
        }
    }

    /**
     * returns the Latitude and Longitude.
     *
     * @return The Latitude and Longitude
     */
    public synchronized Point2D.Double getLatLng() {
        if (latLng == null) {
            setLatLng(new Point2D.Double(0, 0));
        }
        return latLng;
    }

    /**
     * Sets the Latitude and Longitude.
     *
     * @param newLatLng The latitude and longitude holder
     */
    public synchronized void setLatLng(final Point2D.Double newLatLng) {
        if ((latLng == null) || (latLng.x != newLatLng.x) || (latLng.y != newLatLng.y)) {
            latLng = newLatLng;
            sendLatLngChangedEvent();
        }
    }

    //----------------------------------------

    /**
     * Sets the Latitude and Longitude.
     *
     * @param newLatLng The latitude and longitude in the format of 2 doubles
     *                  with an x
     */
    public synchronized void setLatLng(final String newLatLng) {
        this.latLngString = newLatLng;
        parseLatLng();
    }

    /**
     * returns the Latitude and Longitude as a String
     *
     * @return The latitude and longitude in the format of 2 doubles with an x
     */
    public synchronized String getLatLngString() {
        final var latLang = getLatLng();
        NumberFormat numberFormatter = NumberFormat.getNumberInstance();
        return numberFormatter.format(latLang.x) + "x" + numberFormatter.format(latLang.y);

    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the copyright holder was updated.
     */
    private void sendLatLngChangedEvent() {
        final var owningNode = getOwningNode();
        if (owningNode == null ) {
            // no owning node, no change notification
            return;
        }
        final var pictureCollection = owningNode.getPictureCollection();

        if (pictureCollection.getSendModelUpdates()) {
            final var pictureInfoChangeEvent = new PictureInfoChangeEvent(this);
            pictureInfoChangeEvent.setLatLngChanged();
            sendPictureInfoChangedEvent(pictureInfoChangeEvent);
            pictureCollection.setUnsavedUpdates();
        }
    }

    /**
     * Returns a Set of categories that have been applied to this picture
     *
     * @return the Set of categories
     */
    public Set<Integer> getCategoryAssignments() {
        return categoryAssignments;
    }

    /**
     * removes all category Assignments
     */
    public synchronized void clearCategoryAssignments() {
        sendCategoryAssignmentsChangedEvent();
        categoryAssignments.clear();
    }

    /**
     * Appends the text fragment to the categoryAssignmentString field.
     *
     * @param string Text fragment
     */
    public synchronized void appendToCategoryAssignment(final String string) {
        if (string.length() > 0) {
            categoryAssignmentString = categoryAssignmentString.concat(string);
        }
    }

    /**
     * Adds to the index number received as a string to the Set of categories
     *
     * @param string Text fragment
     */
    public synchronized void addCategoryAssignment(final String string) {
        if (string.length() > 0) {
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
    public synchronized void addCategoryAssignment(final Integer key) {
        if (categoryAssignments.add(key)) {
            sendCategoryAssignmentsChangedEvent();
        }
    }

    /**
     * Sets the supplied Collection as the categories of the
     * picture, clearing out any pre-existing ones.
     *
     * @param ca the supplied hash set
     */
    public synchronized void setCategoryAssignment(@NotNull final Collection<Integer> ca) {
        Objects.requireNonNull(ca);
        clearCategoryAssignments();
        ca.forEach(this::addCategoryAssignment);
    }

    /**
     * Converts the temporary categoryAssignmentString to a categoryAssignment and calls addCategoryAssignment
     */
    public synchronized void parseCategoryAssignment() {
        try {
            final var category = Integer.valueOf(categoryAssignmentString);
            categoryAssignmentString = "";
            addCategoryAssignment(category);
        } catch (final NumberFormatException x) {
            LOGGER.log(Level.INFO, "NumberFormatException: {0} on picture: {1} because: {2}", new Object[]{categoryAssignmentString, getImageFile(), x.getMessage()});
        }
    }

    /**
     * Returns whether the category is part of the attributes of the picture
     *
     * @param key the key
     * @return true if the key was in the categories
     */
    public synchronized boolean containsCategory(final Integer key) {
        return categoryAssignments.contains(key);
    }

    /**
     * Removes the supplied category from the picture if it was there
     *
     * @param key the key to search for
     */
    public synchronized void removeCategory(final Object key) {
        if (categoryAssignments.remove(key)) {
            sendCategoryAssignmentsChangedEvent();
        }
    }

    //-------------------------------------------

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the copyright holder was updated.
     */
    private void sendCategoryAssignmentsChangedEvent() {
        final var owningNode = getOwningNode();
        if (owningNode == null ) {
            // no owning node, no change notification
            return;
        }
        final var pictureCollection = owningNode.getPictureCollection();

        if (pictureCollection.getSendModelUpdates()) {
            final PictureInfoChangeEvent pce = new PictureInfoChangeEvent(this);
            pce.setCategoryAssignmentsChanged();
            sendPictureInfoChangedEvent(pce);
            pictureCollection.setUnsavedUpdates();
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
        final var pictureInfoChangeEvent = new PictureInfoChangeEvent(this);
        pictureInfoChangeEvent.setWasSelected();
        sendPictureInfoChangedEvent(pictureInfoChangeEvent);
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
        final var pictureInfoChangeEvent = new PictureInfoChangeEvent(this);
        pictureInfoChangeEvent.setWasUnselected();
        sendPictureInfoChangedEvent(pictureInfoChangeEvent);
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
        final var pictureInfoChangeEvent = new PictureInfoChangeEvent(this);
        pictureInfoChangeEvent.setWasMailSelected();
        sendPictureInfoChangedEvent(pictureInfoChangeEvent);
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
        final var pictureInfoChangeEvent = new PictureInfoChangeEvent(this);
        pictureInfoChangeEvent.setWasMailUnselected();
        sendPictureInfoChangedEvent(pictureInfoChangeEvent);
    }

    /**
     * Returns a new PictureInfo object which is identical to the current one.
     *
     * @return a clone of the current PictureInfo object.
     */
    public PictureInfo getClone() {
        final var clone = new PictureInfo();
        clone.setDescription(this.getDescription());
        clone.setImageLocation(this.getImageFile());
        clone.setFilmReference(this.getFilmReference());
        clone.setCreationTime(this.getCreationTime());
        clone.setComment(this.getComment());
        clone.setPhotographer(this.getPhotographer());
        clone.setCopyrightHolder(this.getCopyrightHolder());
        clone.setRotation(this.getRotation());
        return clone;
    }

    /**
     * Registers a listener for picture info change events
     *
     * @param pictureInfoChangeListener The object that will receive
     *                                  notifications.
     */
    public void addPictureInfoChangeListener(final PictureInfoChangeListener pictureInfoChangeListener) {
        pictureInfoListeners.add(pictureInfoChangeListener);
    }

    /**
     * Removes the supplied listener
     *
     * @param pictureInfoChangeListener The listener that doesn't want to
     *                                  notifications anymore.
     */
    public void removePictureInfoChangeListener(
            final PictureInfoChangeListener pictureInfoChangeListener) {
        pictureInfoListeners.remove(pictureInfoChangeListener);
    }

    /**
     * Send PictureInfoChangeEvents.
     *
     * @param pictureInfoChangeEvent The Event we want to notify.
     */
    private void sendPictureInfoChangedEvent(final PictureInfoChangeEvent pictureInfoChangeEvent) {
        final var owningNode = getOwningNode();
        if (owningNode == null ) {
            // no owning node, no change notification
            return;
        }
        final var pictureCollection = owningNode.getPictureCollection();

        if (pictureCollection != null && pictureCollection.getSendModelUpdates()) {
            synchronized (pictureInfoListeners) {
                pictureInfoListeners.forEach(pictureInfoChangeListener
                        -> pictureInfoChangeListener.pictureInfoChangeEvent(pictureInfoChangeEvent)
                );
            }
        }
    }

    //-------------------------------------------

    /**
     * Checks whether the searchString parameter is contained in any of the
     * fields. It doesn't check the checksum or rotation. It does
     * check the description, highres name, film reference, creation time,
     * comment and copyright holder
     *
     * @param searchString The string to search for.
     * @return true if found. false if not.
     */
    public synchronized boolean anyMatch(final String searchString) {
        final String uppercaseSearchString = searchString.toUpperCase();

        return descriptionContains(searchString)
                || (getPhotographer().toUpperCase().contains(uppercaseSearchString))
                || (getImageFile().toString().toUpperCase().contains(uppercaseSearchString))
                || (getFilmReference().toUpperCase().contains(uppercaseSearchString))
                || (getCreationTime().toUpperCase().contains(uppercaseSearchString))
                || (getComment().toUpperCase().contains(uppercaseSearchString))
                || (getCopyrightHolder().toUpperCase().contains(uppercaseSearchString));
    }

    /**
     * Defines how PictureInfo objects compare themselves
     *
     * @param otherPictureInfo The other GroupInfo object
     * @param sortField        which attribute to use in the comparison
     * @return negative number if this is less than or Zero if same or positive number if other is less than this
     */
    public int compareTo(final @NotNull PictureInfo otherPictureInfo, final Settings.FieldCodes sortField) {
        return switch (sortField) {
            case FILM_REFERENCE -> this.getFilmReference().compareTo(otherPictureInfo.getFilmReference());
            case CREATION_TIME -> compareDates(this.getCreationTimeAsDate(), otherPictureInfo.getCreationTimeAsDate());
            case COMMENT -> this.getComment().compareTo(otherPictureInfo.getComment());
            case PHOTOGRAPHER -> this.getPhotographer().compareTo(otherPictureInfo.getPhotographer());
            case COPYRIGHT_HOLDER -> this.getCopyrightHolder().compareTo(otherPictureInfo.getCopyrightHolder());
            default -> this.getDescription().compareTo(otherPictureInfo.getDescription());
        };
    }

    private int compareDates(final Calendar myCreationCalendar, final Calendar otherCreationCalendar){
        if ( myCreationCalendar == null || otherCreationCalendar == null ) {
            return 0; // retain the sort order
        }
        return myCreationCalendar.compareTo(otherCreationCalendar);
    }

    private SortableDefaultMutableTreeNode myOwningNode = null;
    @Override
    public void setOwningNode(SortableDefaultMutableTreeNode sortableDefaultMutableTreeNode) {
        myOwningNode = sortableDefaultMutableTreeNode;
    }

    @Override
    public SortableDefaultMutableTreeNode getOwningNode( ) {
        return myOwningNode;
    }
}
