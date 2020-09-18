package org.jpo.datamodel;

import org.jpo.gui.swing.LabelFrame;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.datamodel.Settings.FieldCodes.*;

/*
 Copyright (C) 2017-2020  Richard Eigenmann.
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
public class SaxEventHandler extends DefaultHandler {

    private static final String LINE_SEPERATOR = System.getProperty("line.separator");
    private final LabelFrame loadProgressGui;

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( SaxEventHandler.class.getName() );

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

    public SaxEventHandler( final SortableDefaultMutableTreeNode startNode, final LabelFrame loadProgressGui, final StringBuilder lowresUrls ) {
        currentGroup = startNode;
        this.loadProgressGui = loadProgressGui;
        this.lowresUrls = lowresUrls;
    }

    /**
     * temporary reference to the picture node being read
     */
    private SortableDefaultMutableTreeNode currentPicture;

    /**
     * method that gets invoked by the parser when a new element is discovered
     *
     * @param namespaceURI Namespace
     * @param lName local name
     * @param qName qualified name
     * @param attrs attributes
     */
    @Override
    public void startElement( final String namespaceURI,
            final String lName,
            final String qName,
            final Attributes attrs )
            {
        GroupInfo groupInfo;
        if ( ( "collection".equals( qName ) ) && ( attrs != null ) ) {
            groupInfo = new GroupInfo( attrs.getValue( "collection_name" ) );
            currentGroup.setUserObject(groupInfo);
            currentGroup.getPictureCollection().setAllowEdits( attrs.getValue( "collection_protected" ).equals( "No" ) );
        } else if ( "group".equals( qName ) && attrs != null ) {
            incrementGroupCount();
            groupInfo = new GroupInfo( attrs.getValue( "group_name" ) != null ? attrs.getValue( "group_name" ) : "" );
            SortableDefaultMutableTreeNode nextCurrentGroup
                    = new SortableDefaultMutableTreeNode(groupInfo);
            currentGroup.add( nextCurrentGroup );
            currentGroup = nextCurrentGroup;
        } else if ( "picture".equals( qName ) ) {
            incrementPictureCount();
            currentPicture = new SortableDefaultMutableTreeNode( new PictureInfo() );
            currentGroup.add( currentPicture );
        } else if ( "description".equals( qName ) ) {
            currentField = DESCRIPTION;
        } else if ( "file_URL".equals( qName ) ) {
            currentField = FILE_URL;
        } else if ( "file_lowres_URL".equals( qName ) ) {
            currentField = FILE_LOWRES_URL;
        } else if ( "film_reference".equals( qName ) ) {
            currentField = FILM_REFERENCE;
        } else if ( "CREATION_TIME".equals( qName ) ) {
            currentField = CREATION_TIME;
        } else if ( "COMMENT".equals( qName ) ) {
            currentField = COMMENT;
        } else if ( "PHOTOGRAPHER".equals( qName ) ) {
            currentField = PHOTOGRAPHER;
        } else if ( "COPYRIGHT_HOLDER".equals( qName ) ) {
            currentField = COPYRIGHT_HOLDER;
        } else if ( "ROTATION".equals( qName ) ) {
            currentField = ROTATION;
        } else if ( "LATLNG".equals( qName ) ) {
            currentField = LATLNG;
        } else if ( "checksum".equals( qName ) ) {
            currentField = CHECKSUM;
        } else if ( "categoryAssignment".equals( qName ) && attrs != null && attrs.getValue( "index" ) != null ) {
            ( (PictureInfo) currentPicture.getUserObject() ).addCategoryAssignment( attrs.getValue( "index" ) );
        } else if ( "categories".equals( qName ) ) {
            currentField = CATEGORIES;
        } else if ( "category".equals( qName ) && attrs != null && attrs.getValue( "index" ) != null ) {
            temporaryCategoryIndex = attrs.getValue( "index" );
            currentField = CATEGORY;
        } else if ( "categoryDescription".equals( qName ) ) {
            currentField = CATEGORY_DESCRIPTION;
        } else {
            LOGGER.log( Level.INFO, "XmlReader: Don''t know what to do with ELEMENT: {0}", qName );
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
     * method that gets invoked by the parser when a end element is discovered;
     * used here to go back to the parent group if a &lt;group&gt; tag is found.
     *
     * @param namespaceURI the URI
     * @param sName the simple name
     * @param qName the qualified name
     */
    @Override
    public void endElement( final String namespaceURI,
            final String sName, // simple name
            final String qName // qualified name
    ) {
        if ( null != qName ) {
            switch ( qName ) {
                case "group":
                    currentGroup = currentGroup.getParent();
                    break;
                case "file_lowres_URL":
                    lowresUrls.append(LINE_SEPERATOR);
                    break;
                case "ROTATION":
                    ( (PictureInfo) currentPicture.getUserObject() ).parseRotation();
                    break;
                case "LATLNG":
                    ( (PictureInfo) currentPicture.getUserObject() ).parseLatLng();
                    break;
                case "checksum":
                    ( (PictureInfo) currentPicture.getUserObject() ).parseChecksum();
                    break;
                case "categoryDescription":
                    currentGroup.getPictureCollection().addCategory( Integer.parseInt( temporaryCategoryIndex ), temporaryCategory );
                    temporaryCategory = "";
                    break;
                default:
                    //Nothing needs to be done on the other types of endElement
                    break;
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
        final String s = new String(buf, offset, len);
        switch (currentField) {
            case DESCRIPTION -> ((PictureInfo) currentPicture.getUserObject()).appendToDescription(s);
            case FILE_URL -> ((PictureInfo) currentPicture.getUserObject()).appendToImageLocation(s);
            case FILE_LOWRES_URL -> lowresUrls.append(s);
            case FILM_REFERENCE -> ((PictureInfo) currentPicture.getUserObject()).appendToFilmReference(s);
            case CREATION_TIME -> ((PictureInfo) currentPicture.getUserObject()).appendToCreationTime(s);
            case COMMENT -> ((PictureInfo) currentPicture.getUserObject()).appendToComment(s);
            case PHOTOGRAPHER -> ((PictureInfo) currentPicture.getUserObject()).appendToPhotographer(s);
            case COPYRIGHT_HOLDER -> ((PictureInfo) currentPicture.getUserObject()).appendToCopyrightHolder(s);
            case ROTATION -> ((PictureInfo) currentPicture.getUserObject()).appendToRotation(s);
            case LATLNG -> ((PictureInfo) currentPicture.getUserObject()).appendToLatLng(s);
            case CHECKSUM -> ((PictureInfo) currentPicture.getUserObject()).appendToChecksum(s);
            case CATEGORIES -> LOGGER.log(Level.INFO, "XmlReader: parsing string on CATEGORIES: {0}", s);
            case CATEGORY -> LOGGER.log(Level.INFO, "XmlReader: parsing string on CATEGORY: {0}", s);
            case CATEGORY_DESCRIPTION -> temporaryCategory = temporaryCategory.concat(s);
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

    /*public static InputSource getCollectionDtdInputSource() {
        final String COLLECTION_DTD_FILE_NAME = "collection.dtd";
        LOGGER.log(Level.INFO, "Trying to load the collection.dtd from the resource: {0}", COLLECTION_DTD_FILE_NAME );
        final URL collectionDtd = XmlReader.class.getClassLoader().getResource( COLLECTION_DTD_FILE_NAME );
        if ( collectionDtd == null ) {
            LOGGER.log(Level.SEVERE, "Failed to find the file {0}. Did something go wrong in the packaging of the application?" , COLLECTION_DTD_FILE_NAME);
            return null;
        } else {
            LOGGER.log(Level.INFO, "Loading collection.dtd from URL: {0}", collectionDtd );
        }
        try {
            final InputStream collectionDtdInputStream = collectionDtd.openStream();
            return new InputSource( collectionDtdInputStream );
        } catch ( final IOException ex ) {
            LOGGER.log( Level.SEVERE, "Could not open the collection.dtd XML Document Type Descriptor.\nException: {0}", ex.getMessage() );
            return null;
        }
    }*/
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
        loadProgressGui.update(String.format(Settings.getJpoResources().getString("org.jpo.dataModel.XmlReader.progressUpdate"), groupCount, pictureCount));
    }
}
