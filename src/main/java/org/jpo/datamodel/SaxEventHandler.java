package org.jpo.datamodel;

import org.jpo.gui.JpoResources;
import org.jpo.gui.swing.LabelFrame;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.datamodel.Settings.FieldCodes.*;

/*
 Copyright (C) 2017-2025 Richard Eigenmann.
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
 * A xml parser to load the collections.
 */
public class SaxEventHandler extends DefaultHandler {

    private static final String LINE_SEPERATOR = System.getProperty("line.separator");
    /**
     * constant
     */
    public static final String INDEX = "index";
    private final LabelFrame loadProgressGui;

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger(SaxEventHandler.class.getName());

    /**
     * temporary reference to the group node being read
     */
    private SortableDefaultMutableTreeNode currentGroup;

    private final StringBuilder lowresUrls;

    /**
     * variable used to interpret what the text is that is coming in through the
     * parser.
     */
    private Settings.FieldCodes currentField;

    /**
     * Constructs the Sax XML parser
     *
     * @param startNode       The starting node
     * @param loadProgressGui the progress GUI to update
     * @param lowresUrls      the lowresUrls to pick up on
     */
    public SaxEventHandler(final SortableDefaultMutableTreeNode startNode, final LabelFrame loadProgressGui, final StringBuilder lowresUrls) {
        currentGroup = startNode;
        this.loadProgressGui = loadProgressGui;
        this.lowresUrls = lowresUrls;
    }

    /**
     * temporary reference to the picture node being read
     */
    private SortableDefaultMutableTreeNode currentPicture;

    private Path baseDir = null;

    /**
     * method that gets invoked by the parser when a new element is discovered
     *
     * @param namespaceURI Namespace
     * @param lName local name
     * @param qName qualified name
     * @param attrs attributes
     */
    @Override
    public void startElement(final String namespaceURI,
                             final String lName,
                             final String qName,
                             final Attributes attrs ) {
        GroupInfo groupInfo;
        if ( ( "collection".equals( qName ) ) && ( attrs != null ) ) {
            groupInfo = new GroupInfo( attrs.getValue( "collection_name" ) );
            final var basDirName = attrs.getValue("basedir");
            if (basDirName != null) {
                baseDir = Paths.get(basDirName);
                LOGGER.log(Level.INFO, "Collection Base Directory is: {0}", basDirName);
            }

            currentGroup.setUserObject(groupInfo);
            currentGroup.getPictureCollection().setAllowEdits( attrs.getValue( "collection_protected" ).equals( "No" ) );
        } else if ( "group".equalsIgnoreCase( qName ) && attrs != null ) {
            incrementGroupCount();
            groupInfo = new GroupInfo( attrs.getValue( "group_name" ) != null ? attrs.getValue( "group_name" ) : "" );
            SortableDefaultMutableTreeNode nextCurrentGroup
                    = new SortableDefaultMutableTreeNode(groupInfo);
            currentGroup.add( nextCurrentGroup );
            currentGroup = nextCurrentGroup;
        } else if ( "picture".equalsIgnoreCase( qName) ) {
            incrementPictureCount();
            currentPicture = new SortableDefaultMutableTreeNode( new PictureInfo() );
            currentGroup.add( currentPicture );
        } else if ( "description".equalsIgnoreCase( qName ) ) {
            currentField = DESCRIPTION;
        } else if ( "file_url".equalsIgnoreCase( qName ) ) {
            currentField = FILE_URL;
        } else if ( "file".equalsIgnoreCase( qName ) ) {
            currentField = FILE;
        } else if ( "file_lowres_url".equalsIgnoreCase( qName ) ) {
            currentField = FILE_LOWRES_URL;
        } else if ( "film_reference".equalsIgnoreCase( qName ) ) {
            currentField = FILM_REFERENCE;
        } else if ( "creation_time".equalsIgnoreCase( qName ) ) {
            currentField = CREATION_TIME;
        } else if ( "comment".equalsIgnoreCase( qName ) ) {
            currentField = COMMENT;
        } else if ( "photographer".equalsIgnoreCase( qName ) ) {
            currentField = PHOTOGRAPHER;
        } else if ( "copyright_holder".equalsIgnoreCase( qName ) ) {
            currentField = COPYRIGHT_HOLDER;
        } else if ( "rotation".equalsIgnoreCase( qName ) ) {
            currentField = ROTATION;
        } else if ( "latlng".equalsIgnoreCase( qName ) ) {
            currentField = LATLNG;
        } else if ("checksum".equalsIgnoreCase(qName)) {
            currentField = IGNORE;
        } else if ("sha256".equalsIgnoreCase(qName)) {
            currentField = SHA256;
        } else if ("categoryAssignment".equalsIgnoreCase(qName) && attrs != null && attrs.getValue(INDEX) != null) {
            ((PictureInfo) currentPicture.getUserObject()).addCategoryAssignment(attrs.getValue(INDEX));
        } else if ("categories".equalsIgnoreCase(qName)) {
            currentField = CATEGORIES;
        } else if ("category".equalsIgnoreCase(qName) && attrs != null && attrs.getValue(INDEX) != null) {
            temporaryCategoryIndex = attrs.getValue(INDEX);
            currentField = CATEGORY;
        } else if ("categoryDescription".equalsIgnoreCase(qName)) {
            currentField = CATEGORY_DESCRIPTION;
        } else {
            LOGGER.log(Level.INFO, "XmlReader: Don''t know what to do with ELEMENT: {0}", qName);
        }
    }

    /**
     * this field hold the value of the category index being parsed.
     */
    private String temporaryCategoryIndex = "";

    /**
     * this field hold the text of the category being parsed.
     */
    private String temporaryCategory = "";

    /**
     * method that gets invoked by the parser when an end element is discovered;
     * used here to go back to the parent group if a &lt;group&gt; tag is found.
     *
     * @param namespaceURI the URI
     * @param sName the simple name
     * @param qName the qualified name
     */
    @Override
    public void endElement(final String namespaceURI,
                           final String sName, // simple name
                           final String qName // qualified name
    ) {
        if ( null != qName ) {
            switch (qName) {
                case "group" -> currentGroup = currentGroup.getParent();
                case "file_lowres_URL" -> lowresUrls.append(LINE_SEPERATOR);
                case "ROTATION" -> ((PictureInfo) currentPicture.getUserObject()).parseRotation();
                case "LATLNG" -> ((PictureInfo) currentPicture.getUserObject()).parseLatLng();
                case "categoryDescription" -> {
                    currentGroup.getPictureCollection().addCategory(Integer.parseInt(temporaryCategoryIndex), temporaryCategory);
                    temporaryCategory = "";
                }
                default -> {
                    //Nothing needs to be done on the other types of endElement
                }
            }
        }
    }

    /**
     * method invoked by the parser when characters are read between tags. The
     * variable interpretChars is set so that the characters can be put in the
     * right place
     *
     * @param buf The buffer
     * @param offset Start offset
     * @param len Length
     */
    @Override
    public void characters(final char[] buf, final int offset, final int len ) {
        final var readString = new String(buf, offset, len);
        switch (currentField) {
            case DESCRIPTION -> ((PictureInfo) currentPicture.getUserObject()).appendToDescription(readString);
            case FILE_URL -> ((PictureInfo) currentPicture.getUserObject()).appendToImageLocation(readString);
            case FILE -> ((PictureInfo) currentPicture.getUserObject()).appendToImageFile(readString, baseDir);
            case FILE_LOWRES_URL -> lowresUrls.append(readString);
            case FILM_REFERENCE -> ((PictureInfo) currentPicture.getUserObject()).appendToFilmReference(readString);
            case CREATION_TIME -> ((PictureInfo) currentPicture.getUserObject()).appendToCreationTime(readString);
            case COMMENT -> ((PictureInfo) currentPicture.getUserObject()).appendToComment(readString);
            case PHOTOGRAPHER -> ((PictureInfo) currentPicture.getUserObject()).appendToPhotographer(readString);
            case COPYRIGHT_HOLDER -> ((PictureInfo) currentPicture.getUserObject()).appendToCopyrightHolder(readString);
            case ROTATION -> ((PictureInfo) currentPicture.getUserObject()).appendToRotation(readString);
            case LATLNG -> ((PictureInfo) currentPicture.getUserObject()).appendToLatLng(readString);
            case SHA256 -> ((PictureInfo) currentPicture.getUserObject()).appendToSha256(readString);
            case CATEGORIES -> LOGGER.log(Level.INFO, "XmlReader: parsing string on CATEGORIES: {0}", readString);
            case CATEGORY -> LOGGER.log(Level.INFO, "XmlReader: parsing string on CATEGORY: {0}", readString);
            case CATEGORY_DESCRIPTION -> {
                final var temporaryCategory1 = temporaryCategory;
                temporaryCategory = temporaryCategory1.concat(readString);
            }
            case IGNORE -> LOGGER.log(Level.INFO, "Ignoring data from file: {0}", readString);
            default -> LOGGER.log(Level.SEVERE, "Don''t recognize currentField: {0}", currentField);
        }
    }

    /**
     * try to resolve where the file belongs
     *
     * @param publicId public id
     * @param systemId system id
     * @return the dtd as an input source
     */
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws IOException {
        LOGGER.log(Level.INFO, "resolveEntity called with params publicId: {0} systemId: {1}", new Object[]{publicId, systemId});
        return getCollectionDtdInputSource();
    }

    /**
     * Returns the collection.dtd definition as an InputSource.
     *
     * @return The collection. dtd file
     * @throws IOException If something went wrong
     */
    public static InputSource getCollectionDtdInputSource() throws IOException {
        return new InputSource(SaxEventHandler.class.getClassLoader().getResource("collection.dtd").openStream());
    }

    /**
     * counter so that listeners can get some indication of what is going on
     */
    private int groupCount; // default is 0

    /**
     * Method to be called when a new Group is being parsed
     */
    private void incrementGroupCount() {
        groupCount++;
        if ( groupCount % 100 == 0 ) {
            informProgressGui();
        }
    }

    /**
     * counter so that listeners can get some indication of what is going on
     */
    private int pictureCount; // default is 0

    /**
     * Method to be called when a new Picture is being parsed
     */
    private void incrementPictureCount() {
        pictureCount++;
        if ( pictureCount % 800 == 0 ) {
            informProgressGui();
        }
    }

    /**
     * method to be called when the progress GUI should be updated. Since
     * updating every picture will slow down the loading This should only be
     * called every hundred pictures or so.
     */
    private void informProgressGui() {
        loadProgressGui.update(String.format(JpoResources.getResource("org.jpo.dataModel.XmlReader.progressUpdate"), groupCount, pictureCount));
    }
}
