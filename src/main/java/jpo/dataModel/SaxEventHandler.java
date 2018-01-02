package jpo.dataModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jpo.dataModel.Settings.FieldCodes.CATEGORIES;
import static jpo.dataModel.Settings.FieldCodes.CATEGORY;
import static jpo.dataModel.Settings.FieldCodes.CATEGORY_DESCRIPTION;
import static jpo.dataModel.Settings.FieldCodes.CHECKSUM;
import static jpo.dataModel.Settings.FieldCodes.COMMENT;
import static jpo.dataModel.Settings.FieldCodes.COPYRIGHT_HOLDER;
import static jpo.dataModel.Settings.FieldCodes.CREATION_TIME;
import static jpo.dataModel.Settings.FieldCodes.DESCRIPTION;
import static jpo.dataModel.Settings.FieldCodes.FILE_LOWRES_URL;
import static jpo.dataModel.Settings.FieldCodes.FILE_URL;
import static jpo.dataModel.Settings.FieldCodes.FILM_REFERENCE;
import static jpo.dataModel.Settings.FieldCodes.LATLNG;
import static jpo.dataModel.Settings.FieldCodes.PHOTOGRAPHER;
import static jpo.dataModel.Settings.FieldCodes.ROTATION;
import jpo.gui.LabelFrame;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*
 Copyright (C) 2017-2018  Richard Eigenmann.
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

    private final LabelFrame loadProgressGui;

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( SaxEventHandler.class.getName() );

    /**
     * temporary reference to the group node being read
     */
    private SortableDefaultMutableTreeNode currentGroup;

    /**
     * Temporary variable to hold the GroupInfo of the group being created.
     */
    private GroupInfo groupInfo;

    private final StringBuilder lowresUrls;
    /**
     * variable used to interpret what the text is that is coming in through the
     * parser.
     */
    private Settings.FieldCodes currentField;

    public SaxEventHandler( SortableDefaultMutableTreeNode startNode, LabelFrame loadProgressGui, StringBuilder lowresUrls ) {
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
     * @throws SAXException Exception if bad
     */
    @Override
    public void startElement( String namespaceURI,
            String lName,
            String qName,
            Attributes attrs )
            throws SAXException {
        if ( ( "collection".equals( qName ) ) && ( attrs != null ) ) {
            groupInfo = new GroupInfo( attrs.getValue( "collection_name" ) );
            if ( attrs.getValue( "collection_icon" ).length() > 1 ) {
                lowresUrls.append( System.getProperty( "line.separator" ) );
                lowresUrls.append( attrs.getValue( "collection_icon" ) );
                lowresUrls.append( System.getProperty( "line.separator" ) );
            }
            currentGroup.setUserObject( groupInfo );
            currentGroup.getPictureCollection().setAllowEdits( attrs.getValue( "collection_protected" ).equals( "No" ) );
        } else if ( "group".equals( qName ) && attrs != null ) {
            incrementGroupCount();
            groupInfo = new GroupInfo( attrs.getValue( "group_name" ) != null ? attrs.getValue( "group_name" ) : "" );
            if ( attrs.getValue( "group_icon" ).length() > 1 ) {
                lowresUrls.append( attrs.getValue( "group_icon" ) );
                lowresUrls.append( System.getProperty( "line.separator" ) );
            }
            SortableDefaultMutableTreeNode nextCurrentGroup
                    = new SortableDefaultMutableTreeNode( groupInfo );
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
     * @throws SAXException Exception if it went wrong
     */
    @Override
    public void endElement( String namespaceURI,
            String sName, // simple name
            String qName // qualified name
    )
            throws SAXException {
        if ( null != qName ) {
            switch ( qName ) {
                case "group":
                    currentGroup = (SortableDefaultMutableTreeNode) currentGroup.getParent();
                    break;
                case "file_lowres_URL":
                    lowresUrls.append( System.getProperty( "line.separator" ) );
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
                    //LOGGER.severe( "Don't recognize qName: " + qName + " Ignoring" );
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
     * @throws SAXException Throws and exception if parsing doesn't work
     */
    @Override
    public void characters( char buf[], int offset, int len ) throws SAXException {
        String s = new String( buf, offset, len );
        switch ( currentField ) {
            case DESCRIPTION:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToDescription( s );
                break;
            case FILE_URL:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToImageLocation( s );
                break;
            case FILE_LOWRES_URL:
                //( (PictureInfo) currentPicture.getUserObject() ).appendToLowresLocation( s );
                lowresUrls.append( s );
                break;
            case FILM_REFERENCE:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToFilmReference( s );
                break;
            case CREATION_TIME:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToCreationTime( s );
                break;
            case COMMENT:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToComment( s );
                break;
            case PHOTOGRAPHER:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToPhotographer( s );
                break;
            case COPYRIGHT_HOLDER:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToCopyrightHolder( s );
                break;
            case ROTATION:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToRotation( s );
                break;
            case LATLNG:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToLatLng( s );
                break;
            case CHECKSUM:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToChecksum( s );
                break;
            case CATEGORIES:
                LOGGER.log( Level.INFO, "XmlReader: parsing string on CATEGORIES: {0}", s );
                break;
            case CATEGORY:
                LOGGER.log( Level.INFO, "XmlReader: parsing string on CATEGORY: {0}", s );
                break;
            case CATEGORY_DESCRIPTION:
                temporaryCategory = temporaryCategory.concat( s );
                break;
                                default:
                    LOGGER.severe( "Don't recognize currentField: " + currentField  );
                    break;
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
    public InputSource resolveEntity( String publicId, String systemId ) {
        LOGGER.info( "resolveEntity called with params publicId: "
                + publicId + " systemId: " + systemId );
        return getCollectionDtdInputSource();
    }

    public static InputSource getCollectionDtdInputSource() {
        final String COLLECTION_DTD_FILE_NAME = "collection.dtd";
        LOGGER.info( "Trying to load the collection.dtd from the resource: "
                + COLLECTION_DTD_FILE_NAME );
        URL collectionDtd = XmlReader.class.getClassLoader().getResource( COLLECTION_DTD_FILE_NAME );
        if ( collectionDtd == null ) {
            LOGGER.severe( "Failed to find the file " + COLLECTION_DTD_FILE_NAME
                    + ". Did something go wrong in the packaging of the application?" );
            return null;
        } else {
            LOGGER.info( "Loading collection.dtd from URL: " + collectionDtd.toString() );
        }
        try {
            InputStream collectionDtdInputStream = collectionDtd.openStream();
            return new InputSource( collectionDtdInputStream );
        } catch ( IOException ex ) {
            LOGGER.log( Level.SEVERE, "Could not open the collection.dtd XML Document Type Descriptor.\nException: {0}", ex.getMessage() );
            return null;
        }
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
        loadProgressGui.update( String.format( Settings.jpoResources.getString( "jpo.dataModel.XmlReader.progressUpdate" ), groupCount, pictureCount ) );
    }
}
