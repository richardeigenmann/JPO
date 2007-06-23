package jpo;

import java.util.List;
import java.awt.datatransfer.*;
import java.io.*;
import java.net.URL;
import java.util.*;

/*
JpoTransferable.java:  a transferable to drag and drop nodes of the Jpo application
 
Copyright (C) 2002-2007  Richard Eigenmann.
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
     *   This is the reference to the node that is being transferred
     */
    private Object [] dmtn;
    
    
    /**
     *   holds the hash code of the original object.
     */
    private int originalHashCode;
    
    
    
    /**
     *  Constructor
     *
     *  @param dmtn  The SortableDefaultMutableTreeNode to be transferred
     **/
    public JpoTransferable( Object [] dmtn ) {
        this.dmtn = dmtn;
        originalHashCode = dmtn.hashCode();
    }
    
    
    /**
     *  Definition of the data flavor for the nodes.
     */
    public static DataFlavor dmtnFlavor = null;
    static{
        try{
            dmtnFlavor = new DataFlavor( Object.class, "JpoTransferable" );
        } catch ( Exception e ) {
            Tools.log( "JpoTransferable.dmtnFlavor failed to initialize: " + e.getMessage() );
        }
    }

    /**
     *  Definition of the data flavor for the nodes.
     */
    public static DataFlavor jpegImage = null;
    static{
        try{
            jpegImage = new DataFlavor( "image/jpeg" );
        } catch ( Exception e ) {
            Tools.log( "JpoTransferable.jpegImage failed to initialize: " + e.getMessage() );
        }
    }
    
    
    /**
     *  Definition of the data flavor for the original hash code.
     */
    public static DataFlavor originalHashCodeFlavor = null;;
    static{
        try{
            originalHashCodeFlavor = new DataFlavor( Integer.class, "OriginalHashCodeFlavor" );
        } catch ( Exception e ) {
            Tools.log( "JpoTransferable.OriginalHashCodeFlavor failed to initialize: " + e.getMessage() );
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
     */
    public DataFlavor[] getTransferDataFlavors() {
        //Tools.log("JpoTransferabe.getTransferDataFlavors: returning flavors: "  );
        //for ( int i = 0; i<flavors.length; i++ ) {
            //Tools.log("JpoTransferabe.getTransferDataFlavors: supported flavor: " + flavors[i].toString() );
        //}
        return flavors;
    }
    
    
    /**
     *   Returns the transferable.
     */
    public Object getTransferData( DataFlavor flavor )
    throws UnsupportedFlavorException, IOException {
        if ( flavor.equals( dmtnFlavor )) {
            Tools.log("dmtn Flavor Transferable");
            return dmtn;
        } else if ( flavor.equals( originalHashCodeFlavor )) {
            Tools.log("originalHashCodeTransferable");
            return new Integer( originalHashCode ) ;
        } else if ( flavor.equals( stringFlavor)) {
            Tools.log("String Transferable");
            return dmtn.toString();
        } else if ( flavor.equals( jpegImage )) {
            ScalablePicture sp = new ScalablePicture();
            File hrf = null;
            for ( int i=0; i < dmtn.length; i++ ) {
                Tools.log("Transferable: "+ Integer.toString(i));
                if ( dmtn[i] instanceof SortableDefaultMutableTreeNode ) {
                    SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) dmtn[i];
                    Object userObject = n.getUserObject();
                    if ( userObject instanceof PictureInfo ) {
                        PictureInfo pi = (PictureInfo) userObject;
                        Tools.log( "Location: " + pi.getHighresURLOrNull().toString() );
                        hrf = pi.getHighresFile();
                        //sp.loadPictureImd( pi.getHighresURLOrNull(), pi.getRotation() );
                        Tools.log("Done loading");
                    }
                }
            }
            //return sp.getScaledPicture();
            return new FileInputStream( hrf );
        } else if ( flavor.equals( javaFileListFlavor )) {
            Vector fileList = new Vector();
            for ( int i=0; i < dmtn.length; i++ ) {
                Tools.log("Transferable: "+ Integer.toString(i));
                if ( dmtn[i] instanceof SortableDefaultMutableTreeNode ) {
                    SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) dmtn[i];
                    Object userObject = n.getUserObject();
                    if ( userObject instanceof PictureInfo ) {
                        PictureInfo pi = (PictureInfo) userObject;
                        Tools.log( "Adding: " + pi.getHighresFile().toString() );
                        fileList.add ( pi.getHighresFile() );
                        Tools.log("Done loading");
                    }
                }
            }
            return (List) fileList;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
    
    
    
    
    
    
    
    
    
    /**
     *  Definition of the flavorList
     */
    private static final List flavorList = Arrays.asList( flavors );
    
    
    /**
     *   Returns if a flavor is supported.
     */
    public boolean isDataFlavorSupported( DataFlavor flavor ) {
        Tools.log("JpoTransferable.isDataFlavorSupported invoked. Returning: " + ( flavorList.contains( flavor ) ? "True" : "False" ) );
        return ( flavorList.contains( flavor ) );
    }
    
    
    
    
    /**
     *  Returns a String description of the object
     */
    public String toString() {
        String retString = dmtn.toString();
        if ( retString == null ) { retString = ""; }
        retString =  "JpoTransferable for node: " +  retString;
        return retString;
    }
    
    /**
     *  Comes from the clipboard owner interface
     */
    public void lostOwnership( Clipboard clipboard, Transferable contents ) {
        Tools.log( "JpoTransferable.lostOwnership happened.");
    }
}
