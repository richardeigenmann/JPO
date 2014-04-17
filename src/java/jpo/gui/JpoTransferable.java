package jpo.gui;

import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.PictureInfo;
import java.util.logging.Logger;

/*
JpoTransferable.java:  a transferable to drag and drop nodes of the Jpo application

Copyright (C) 2002 - 2014  Richard Eigenmann.
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
public class JpoTransferable
        implements Transferable, ClipboardOwner {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( JpoTransferable.class.getName() );


    /**
     *  Constructs a JpoTransferable
     *
     *  @param transferableNodes  The nodes to be transferred
     **/
    public JpoTransferable( Object[] transferableNodes ) {
        LOGGER.fine( String.format( "A Transferable has been created with %d nodes", transferableNodes.length ) );
        this.transferableNodes = transferableNodes;
        //originalHashCode = java.util.Arrays.hashCode( transferableNodes );
    }

    /**
     *  The nodes being transferred
     */
    private final Object[] transferableNodes;

    /**
     *  Definition of the data flavor as a jpo internal object
     */
    public static final DataFlavor jpoNodeFlavor = new DataFlavor( Object.class, "JpoTransferable" );

    /**
     *  Definition of the data flavor for the original hash code.
     */
    public static final DataFlavor originalHashCodeFlavor = new DataFlavor( Integer.class, "OriginalHashCodeFlavor" );

    /**
     *   Definition of the data flavours supported by this Transferrable.
     */
    private static final DataFlavor[] flavors = {
        jpoNodeFlavor,
        DataFlavor.imageFlavor,
        DataFlavor.javaFileListFlavor,
        DataFlavor.stringFlavor
    };


    /**
     * Returns a well formated description of the supported transferrables
     * @return a well formated description of the supported transferrables
     */
    private static String flavorsToString() {
        StringBuffer sb = new StringBuffer( String.format( "%d Transferable flavors supported: ", flavors.length ) );


        for ( DataFlavor flavor : flavors ) {
            sb.append( flavor.toString() + ", " );
        }
        return ( sb.toString() );


    }


    /**
     * Returns the supported transferable data flavours.
     * @return The transferable data flavours
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        LOGGER.fine( flavorsToString() );


        return flavors;


    }


    /**
     * Returns if a requested flavor is supported.
     * @param flavor The flavor to query
     * @return whether it is supported or not
     */
    @Override
    public boolean isDataFlavorSupported( DataFlavor flavor ) {
        LOGGER.fine( String.format( "Requested flavor %s is supported: %b", flavor.toString(), Arrays.asList( flavors ).contains( flavor ) ) );
        return ( Arrays.asList( flavors ).contains( flavor ) );
    }


    /**
     * Returns the transferable in the requested flavor
     * @param flavor The flavor to return the transferable in
     * @return The transferable
     * @throws UnsupportedFlavorException You get this exception if you request something that is not supported
     * @throws IOException There could also be an io exception thrown
     */
    @Override
    public Object getTransferData( DataFlavor flavor )
            throws UnsupportedFlavorException, IOException {
        LOGGER.fine( String.format( "Transferable requested as DataFlavor: %s", flavor.toString() ) );
        if ( flavor.equals( jpoNodeFlavor ) ) {
            LOGGER.fine( "returning the Java array of nodes as a transferable" );
            return transferableNodes;
        } else if ( flavor.equals( DataFlavor.stringFlavor ) ) {
            return getStringTransferData();
        } else if ( flavor.equals( DataFlavor.javaFileListFlavor ) ) {
            return getJavaFileListTransferable();
        } else if ( flavor.equals( DataFlavor.imageFlavor ) ) {
            return getImageTransferable();
        } else {
            throw new UnsupportedFlavorException( flavor );
        }
    }


    /**
     * Returns the transfer data in the DataFlavor format for a string
     * @return the transfer data as a String
     */
    private Object getStringDescriptionTransferData() {
        StringBuffer objectDescriptions = new StringBuffer( "" );
        for ( Object o : transferableNodes ) {
            objectDescriptions.append( o.toString() + "\n" );
        }
        LOGGER.fine( String.format( "Returning the following String as stringFlavor: %s", objectDescriptions.toString() ) );
        return objectDescriptions.toString();
    }


    /**
     * Returns the transfer data in the DataFlavor format for a string
     * @return the transfer data as a String
     */
    private Object getStringTransferData() {
        StringBuffer filenames = new StringBuffer( "" );
        for ( Object transferableNode : transferableNodes ) {
            if ( transferableNode instanceof SortableDefaultMutableTreeNode ) {
                SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) transferableNode;
                Object userObject = n.getUserObject();
                if ( userObject instanceof PictureInfo ) {
                    PictureInfo pi = (PictureInfo) userObject;
                    filenames.append( pi.getHighresFile() + "\n" );
                }
            }
        }
        LOGGER.fine( String.format( "Returning the following String as stringFlavor: %s", filenames.toString() ) );
        return filenames.toString();
    }


    /**
     * Returns the transfer data as a List for the javaFileListFlavor
     * @return the transferable as a List
     */
    private Object getJavaFileListTransferable() {
        List<File> fileList = new ArrayList<>();
        for ( Object transferableNode : transferableNodes ) {
            if ( transferableNode instanceof SortableDefaultMutableTreeNode ) {
                SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) transferableNode;
                Object userObject = n.getUserObject();
                if ( userObject instanceof PictureInfo ) {
                    PictureInfo pi = (PictureInfo) userObject;
                    fileList.add( pi.getHighresFile() );
                }
            }
        }
        LOGGER.fine( String.format( "Returning %d files in a list", fileList.size() ) );
        return (List) fileList;
    }


    /**
     * Returns the transfer data as a List for the javaFileListFlavor
     * @return the transferable as a List
     */
    private Object getImageTransferable() {
        Image img = null;
        for ( Object transferableNode : transferableNodes ) {
            if ( transferableNode instanceof SortableDefaultMutableTreeNode ) {
                SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) transferableNode;
                Object userObject = n.getUserObject();
                if ( userObject instanceof PictureInfo ) {
                    PictureInfo pi = (PictureInfo) userObject;
                    SourcePicture sp = new SourcePicture();
                    sp.loadPicture( pi.getHighresURLOrNull(), pi.getRotation() );
                    img = sp.getSourceBufferedImage();
                }
            }
        }
        LOGGER.fine( String.format( "Returning a BufferedImage in the Transferable" ) );
        return img;
    }


    /**
     * Returns information about the transferable
     * @return information about the transferable
     */
    @Override
    public String toString() {
        StringBuffer objectDescriptions = new StringBuffer( String.format( "JpoTransferable for %d nodes: ", transferableNodes.length ) );


        for ( Object o : transferableNodes ) {
            objectDescriptions.append( o.toString() + ", " );


        }
        return objectDescriptions.toString();


    }


    /**
     * Comes from the clipboard owner interface
     * @param clipboard
     * @param contents
     */
    @Override
    public void lostOwnership( Clipboard clipboard, Transferable contents ) {
        LOGGER.info( String.format( "lostOwnership clipboard: %s, Transferable: %s", clipboard.toString(), contents.toString() ) );

    }
}
