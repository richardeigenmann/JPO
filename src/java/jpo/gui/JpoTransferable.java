package jpo.gui;

import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.*;
import jpo.dataModel.PictureInfo;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/*
JpoTransferable.java:  a transferable to drag and drop nodes of the Jpo application

Copyright (C) 2002-2009  Richard Eigenmann.
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
 *
 *
 */
public class JpoTransferable implements Transferable, ClipboardOwner {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( JpoTransferable.class.getName() );

    /**
     *   This is the reference to the node that is being transferred
     */
    private Object[] dmtn;

    /**
     *   holds the hash code of the original object.
     */
    private int originalHashCode;


    /**
     *  Constructor
     *
     *  @param dmtn  The SortableDefaultMutableTreeNode to be transferred
     **/
    public JpoTransferable( Object[] dmtn ) {
        this.dmtn = dmtn;
        originalHashCode = dmtn.hashCode();
    }

    /**
     *  Definition of the data flavor for the nodes.
     */
    public static DataFlavor dmtnFlavor = null;


    static {
        try {
            dmtnFlavor = new DataFlavor( Object.class, "JpoTransferable" );
        } catch ( Exception e ) {
            logger.info( "JpoTransferable.dmtnFlavor failed to initialize: " + e.getMessage() );
        }
    }

    /**
     *  Definition of the data flavor for the nodes.
     */
    public static DataFlavor jpegImage = null;


    static {
        try {
            jpegImage = new DataFlavor( "image/jpeg" );
        } catch ( Exception e ) {
            logger.info( "JpoTransferable.jpegImage failed to initialize: " + e.getMessage() );
        }
    }

    /**
     *  Definition of the data flavor for the original hash code.
     */
    public static DataFlavor originalHashCodeFlavor = null;


    ;


    static {
        try {
            originalHashCodeFlavor = new DataFlavor( Integer.class, "OriginalHashCodeFlavor" );
        } catch ( Exception e ) {
            logger.info( "JpoTransferable.OriginalHashCodeFlavor failed to initialize: " + e.getMessage() );
        }
    }

    /**
     *  Definition of the data flavor for String.
     */
    public static final DataFlavor stringFlavor = DataFlavor.stringFlavor;

    /**
     *  Definition of the data flavor for File List.
     */
    public static final DataFlavor javaFileListFlavor = DataFlavor.javaFileListFlavor;

    /**
     *   Definition of the data flavours supported by this Transferrable.
     */
    private static final DataFlavor[] flavors = {
        //DataFlavor.imageFlavor,
        //jpegImage,
        javaFileListFlavor,
        JpoTransferable.dmtnFlavor
    //JpoTransferable.originalHashCodeFlavor
    // JpoTransferable.stringFlavor
    };


    /**
     *   Returns the supported flavours.
     * @return
     */
    public DataFlavor[] getTransferDataFlavors() {
        //logger.info("JpoTransferabe.getTransferDataFlavors: returning flavors: "  );
        //for ( int i = 0; i<flavors.length; i++ ) {
        //logger.info("JpoTransferabe.getTransferDataFlavors: supported flavor: " + flavors[i].toString() );
        //}
        return flavors;
    }


    /**
     *   Returns the transferable.
     * @param flavor 
     * @return
     * @throws UnsupportedFlavorException
     * @throws IOException
     */
    public Object getTransferData( DataFlavor flavor )
            throws UnsupportedFlavorException, IOException {
        if ( flavor.equals( dmtnFlavor ) ) {
            logger.info( "dmtn Flavor Transferable" );
            return dmtn;
        } else if ( flavor.equals( originalHashCodeFlavor ) ) {
            logger.info( "originalHashCodeTransferable" );
            return new Integer( originalHashCode );
        } else if ( flavor.equals( stringFlavor ) ) {
            logger.info( "String Transferable" );
            return dmtn.toString();
        } else if ( flavor.equals( jpegImage ) ) {
            ScalablePicture sp = new ScalablePicture();
            File hrf = null;
            for ( int i = 0; i < dmtn.length; i++ ) {
                logger.info( "Transferable: " + Integer.toString( i ) );
                if ( dmtn[i] instanceof SortableDefaultMutableTreeNode ) {
                    SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) dmtn[i];
                    Object userObject = n.getUserObject();
                    if ( userObject instanceof PictureInfo ) {
                        PictureInfo pi = (PictureInfo) userObject;
                        logger.info( "Location: " + pi.getHighresURLOrNull().toString() );
                        hrf = pi.getHighresFile();
                        //sp.loadPictureImd( pi.getHighresURLOrNull(), pi.getRotation() );
                        logger.info( "Done loading" );
                    }
                }
            }
            //return sp.getScaledPicture();
            return new FileInputStream( hrf );
        } else if ( flavor.equals( javaFileListFlavor ) ) {
            Vector<File> fileList = new Vector<File>();
            for ( int i = 0; i < dmtn.length; i++ ) {
                logger.info( "Transferable: " + Integer.toString( i ) );
                if ( dmtn[i] instanceof SortableDefaultMutableTreeNode ) {
                    SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) dmtn[i];
                    Object userObject = n.getUserObject();
                    if ( userObject instanceof PictureInfo ) {
                        PictureInfo pi = (PictureInfo) userObject;
                        logger.info( "Adding: " + pi.getHighresFile().toString() );
                        fileList.add( pi.getHighresFile() );
                        logger.info( "Done loading" );
                    }
                }
            }
            return (List) fileList;
        } else {
            throw new UnsupportedFlavorException( flavor );
        }
    }

    /**
     *  Definition of the flavorList
     */
    private static final List flavorList = Arrays.asList( flavors );


    /**
     *   Returns if a flavor is supported.
     * @param flavor
     * @return
     */
    public boolean isDataFlavorSupported( DataFlavor flavor ) {
        logger.info( "JpoTransferable.isDataFlavorSupported invoked. Returning: " + ( flavorList.contains( flavor ) ? "True" : "False" ) );
        return ( flavorList.contains( flavor ) );
    }


    /**
     *  Returns a String description of the object
     * @return
     */
    @Override
    public String toString() {
        String retString = dmtn.toString();
        if ( retString == null ) {
            retString = "";
        }
        retString = "JpoTransferable for node: " + retString;
        return retString;
    }


    /**
     *  Comes from the clipboard owner interface
     * @param clipboard
     * @param contents
     */
    public void lostOwnership( Clipboard clipboard, Transferable contents ) {
        logger.info( "JpoTransferable.lostOwnership happened." );
    }
}
