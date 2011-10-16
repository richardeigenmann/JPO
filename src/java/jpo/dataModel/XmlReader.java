package jpo.dataModel;

import jpo.gui.LoadProgressGui;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

/*
XmlReader.java:  class that reads the xml file

Copyright (C) 2002 - 2010  Richard Eigenmann.
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
 *  class that reads an XML collection and creates a tree of SortableDefaultMutableTreeNodes
 */
public class XmlReader extends DefaultHandler {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( XmlReader.class.getName() );

    /**
     *  temporary reference to the group node being read
     */
    private SortableDefaultMutableTreeNode currentGroup;

    /**
     *  temporary reference to the picture node being read
     */
    private SortableDefaultMutableTreeNode currentPicture;

    /**
     * 	variable used to interpret what the text is that is coming in
     *	through the parser.
     */
    private int interpretChars;

    /**
     *  Temporary variable to hold the GroupInfo of the group being created.
     */
    private GroupInfo gi;

    private LoadProgressGui lpg = new LoadProgressGui();


    /**
     *   Constructor an XML parser that can read our picture list XML files.
     *
     *   @param  inputStream  The stream which is to be parsed
     *   @param  startNode	The node that becomes the root node for the nodes being read.
     *  			Whether unsaved changes should be set or not depends entirely on
     *                      the context and is not set by the parser.
     */
    public XmlReader( InputStream inputStream, SortableDefaultMutableTreeNode startNode ) {
        BufferedInputStream bufferedInputStream = new BufferedInputStream( inputStream );

        currentGroup = startNode;

        // Use the validating parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating( true );

        try {
            // Parse the input
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse( bufferedInputStream, this );

        } catch ( SAXParseException spe ) {
            // Error generated by the parser
            LOGGER.info( "\n** Parsing error" + ", line " + spe.getLineNumber() + ", uri " + spe.getSystemId() );
            LOGGER.info( "   " + spe.getMessage() );

            // Use the contained exception, if any
            Exception x = spe;
            if ( spe.getException() != null ) {
                x = spe.getException();
            }
            //x.printStackTrace();

        } catch ( SAXException sxe ) {
            // Error generated by this application
            // (or a parser-initialization error)
            Exception x = sxe;
            if ( sxe.getException() != null ) {
                x = sxe.getException();
            }
            //x.printStackTrace();

        } catch ( ParserConfigurationException pce ) {
            LOGGER.info( "XmlReader: Parser with specified options can't be built" + pce.getMessage() );
            //pce.printStackTrace();

        } catch ( IOException ioe ) {
            LOGGER.info( "XmlReader: I/O error: " + ioe.getMessage() );
            ioe.printStackTrace();
        }

        correctJarReferences( startNode );
        // new IntegrityChecker( startNode );
        lpg.getRid();
        lpg = null;
    }


    /**
     * method that gets invoked by the parser when a new element is discovered
     * @param namespaceURI 
     * @param lName
     * @param qName
     * @param attrs
     * @throws SAXException
     */
    @Override
    public void startElement( String namespaceURI,
            String lName, // local name
            String qName, // qualified name
            Attributes attrs )
            throws SAXException {
        if ( ( "collection".equals( qName ) ) && ( attrs != null ) ) {
            gi = new GroupInfo( attrs.getValue( "collection_name" ) );
            gi.setLowresLocation( attrs.getValue( "collection_icon" ) );
            currentGroup.setUserObject( gi );
            currentGroup.getPictureCollection().setAllowEdits( attrs.getValue( "collection_protected" ).equals( "No" ) );
        } else if ( "group".equals( qName ) ) {
            incrementGroupCount();
            gi = new GroupInfo( attrs.getValue( "group_name" ) );
            gi.setLowresLocation( attrs.getValue( "group_icon" ) );
            SortableDefaultMutableTreeNode nextCurrentGroup =
                    new SortableDefaultMutableTreeNode( gi );
            currentGroup.add( nextCurrentGroup );
            currentGroup = nextCurrentGroup;
        } else if ( "picture".equals( qName ) ) {
            incrementPictureCount();
            currentPicture = new SortableDefaultMutableTreeNode( new PictureInfo() );
            currentGroup.add( currentPicture );
        } else if ( "description".equals( qName ) ) {
            interpretChars = Settings.DESCRIPTION;
        } else if ( "file_URL".equals( qName ) ) {
            interpretChars = Settings.FILE_URL;
        } else if ( "file_lowres_URL".equals( qName ) ) {
            interpretChars = Settings.FILE_LOWRES_URL;
        } else if ( "film_reference".equals( qName ) ) {
            interpretChars = Settings.FILM_REFERENCE;
        } else if ( "CREATION_TIME".equals( qName ) ) {
            interpretChars = Settings.CREATION_TIME;
        } else if ( "COMMENT".equals( qName ) ) {
            interpretChars = Settings.COMMENT;
        } else if ( "PHOTOGRAPHER".equals( qName ) ) {
            interpretChars = Settings.PHOTOGRAPHER;
        } else if ( "COPYRIGHT_HOLDER".equals( qName ) ) {
            interpretChars = Settings.COPYRIGHT_HOLDER;
        } else if ( "ROTATION".equals( qName ) ) {
            interpretChars = Settings.ROTATION;
        } else if ( "LATLNG".equals( qName ) ) {
            interpretChars = Settings.LATLNG;
        } else if ( "checksum".equals( qName ) ) {
            interpretChars = Settings.CHECKSUM;
        } else if ( "categoryAssignment".equals( qName ) ) {
            ( (PictureInfo) currentPicture.getUserObject() ).addCategoryAssignment( attrs.getValue( "index" ) );
        } else if ( "categories".equals( qName ) ) {
            interpretChars = Settings.CATEGORIES;
        } else if ( "category".equals( qName ) ) {
            temporaryCategoryIndex = attrs.getValue( "index" );
            interpretChars = Settings.CATEGORY;
        } else if ( "categoryDescription".equals( qName ) ) {
            interpretChars = Settings.CATEGORY_DESCRIPTION;
        } else {
            LOGGER.info( "XmlReader: Don't know what to do with ELEMENT: " + qName );
        }
    }

    /**
     *  this field hold the value of the category index being parsed.
     */
    private String temporaryCategoryIndex = "";

    /**
     *  this field hold the texte of the category being parsed.
     */
    private String temporaryCategory = "";


    /**
     *  method that gets invoked by the parser when a end element is discovered; used
     *  here to go back to the parent group if a </group> tag is found.
     * @param namespaceURI
     * @param sName
     * @param qName
     * @throws SAXException
     */
    @Override
    public void endElement( String namespaceURI,
            String sName, // simple name
            String qName // qualified name
            )
            throws SAXException {
        if ( "group".equals( qName ) ) {
            currentGroup = (SortableDefaultMutableTreeNode) currentGroup.getParent();
        } else if ( "ROTATION".equals( qName ) ) {
            //logger.info("ROTATION here");
            ( (PictureInfo) currentPicture.getUserObject() ).parseRotation();
        } else if ( "LATLNG".equals( qName ) ) {
            ( (PictureInfo) currentPicture.getUserObject() ).parseLatLng();
        } else if ( "checksum".equals( qName ) ) {
            //logger.info("CHECKSUM here");
            ( (PictureInfo) currentPicture.getUserObject() ).parseChecksum();
        } else if ( "categoryDescription".equals( qName ) ) {
            currentGroup.getPictureCollection().addCategory( new Integer( Integer.parseInt( temporaryCategoryIndex ) ), temporaryCategory );
            temporaryCategory = "";
        }
    }


    /**
     * method invoked by the parser when characters are read between tags. The
     * variable interpretChars is set so that the characters can be put in the right place
     * @param buf
     * @param offset
     * @param len
     * @throws SAXException
     */
    @Override
    public void characters( char buf[], int offset, int len ) throws SAXException {
        // LOGGER.info("CHARS:   "+ new String(buf, offset, len));
        String s = new String( buf, offset, len );
        // LOGGER.info(s);
        switch ( interpretChars ) {
            case Settings.DESCRIPTION:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToDescription( s );
                break;
            case Settings.FILE_URL:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToHighresLocation( s );
                break;
            case Settings.FILE_LOWRES_URL:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToLowresLocation( s );
                break;
            case Settings.FILM_REFERENCE:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToFilmReference( s );
                break;
            case Settings.CREATION_TIME:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToCreationTime( s );
                break;
            case Settings.COMMENT:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToComment( s );
                break;
            case Settings.PHOTOGRAPHER:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToPhotographer( s );
                break;
            case Settings.COPYRIGHT_HOLDER:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToCopyrightHolder( s );
                break;
            case Settings.ROTATION:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToRotation( s );
                break;
            case Settings.LATLNG:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToLatLng( s );
                break;
            case Settings.CHECKSUM:
                ( (PictureInfo) currentPicture.getUserObject() ).appendToChecksum( s );
                break;
            case Settings.CATEGORIES:
                LOGGER.info( "XmlReader: parsing string on CATEGORIES: " + s );
                break;
            case Settings.CATEGORY:
                LOGGER.info( "XmlReader: parsing string on CATEGORY: " + s );
                break;
            case Settings.CATEGORY_DESCRIPTION:
                temporaryCategory = temporaryCategory.concat( s );
                break;
        }
    }


    /**
     *
     * @param buf
     * @param offset
     * @param len
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void ignorableWhitespace( char buf[], int offset, int len )
            throws SAXException {
        // Ignore it
    }


    /**
     *
     * @param target
     * @param data
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void processingInstruction( String target, String data ) throws SAXException {
        nl();
        emit( "PROCESS: " );
        emit( "<?" + target + " " + data + "?>" );
    }


    /**
     *  try to resolve where the file belongs
     * @param publicId
     * @param systemId
     * @return the dtd as an input source
     */
    @Override
    public InputSource resolveEntity( String publicId, String systemId ) {
        return new InputSource( Settings.cl.getResourceAsStream( "jpo/collection.dtd" ) );
    }


    //===========================================================
    // SAX ErrorHandler methods
    //===========================================================
    // treat validation errors as fatal
    /**
     *
     * @param e
     * @throws org.xml.sax.SAXParseException
     */
    @Override
    public void error( SAXParseException e ) throws SAXParseException {
        throw e;
    }


    // dump warnings too
    /**
     *
     * @param err
     * @throws org.xml.sax.SAXParseException
     */
    @Override
    public void warning( SAXParseException err ) throws SAXParseException {
        LOGGER.info( "** Warning" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
        LOGGER.info( "   " + err.getMessage() );
    }


    //===========================================================
    // Utility Methods ...
    //===========================================================
    // Wrap I/O exceptions in SAX exceptions, to
    // suit handler signature requirements
    private void emit( String s ) throws SAXException {
    }


    // Start a new line
    // and indent the next line appropriately
    private void nl() throws SAXException {
        String lineEnd = System.getProperty( "line.separator" );
    }


    /**
     *  This method runs through all the URL strings and changes the
     *  jar:&excl; references with the path to the jar.
     *  I am prepared to admit this is a sloppy way of building this. The
     *  problem is that since the parser doesn't always return the whole
     *  URL in one go I could be reading fragments and those will not translate
     *  well. That's also the reason for using append in the adding of
     *  the data.
     * @param startNode
     */
    public void correctJarReferences( SortableDefaultMutableTreeNode startNode ) {
        Enumeration kids = startNode.children();
        while ( kids.hasMoreElements() ) {
            SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) kids.nextElement();

            if ( n.getUserObject() instanceof PictureInfo ) {
                PictureInfo pi = (PictureInfo) n.getUserObject();
                if ( pi.getHighresLocation().startsWith( "jar:!" ) ) {
                    pi.setHighresLocation(
                            pi.getHighresLocation().replaceFirst( "jar:!", Settings.jarRoot ) );
                }
                if ( pi.getLowresLocation().startsWith( "jar:!" ) ) {
                    pi.setLowresLocation(
                            pi.getLowresLocation().replaceFirst( "jar:!", Settings.jarRoot ) );
                }
            }

            if ( n.getChildCount() > 0 ) {
                correctJarReferences( n );
            }
        }

    }

    /**
     *  counter so that listeners can get some indication of what is going on
     */
    private int groupCount = 0;


    /**
     *  Method to be called when a new Group is being parsed
     */
    private void incrementGroupCount() {
        groupCount++;
        if ( groupCount % 100 == 0 ) {
            informProgressGui();
        }
    }

    /**
     *  counter so that listeners can get some indication of what is going on
     */
    private int pictureCount = 0;


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
     *  method to be called when the progress GUI should be updated. Since updating every picture will slow down the loading
     *  This should only be called every hundred pictures or so.
     */
    private void informProgressGui() {
        lpg.update( "Loaded: " + Integer.toString( groupCount ) + " Groups, " + Integer.toString( pictureCount ) + " Pictures" );
    }
}
