package jpo;

import java.awt.dnd.*;
import java.util.List;
import java.awt.datatransfer.*;
import javax.swing.tree.*;
import java.io.*;
import java.util.*;

/*
JpoTransferable.java:  a transferable to drag and drop nodes of the Jpo application

Copyright (C) 2002  Richard Eigenmann.
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
	 *   Returns the transferable.
	 */
	public Object getTransferData( DataFlavor flavor ) 
		    throws UnsupportedFlavorException, IOException {
		if ( flavor.equals( dmtnFlavor )) {
			return dmtn;
		} else if ( flavor.equals( originalHashCodeFlavor)) {
			return new Integer( originalHashCode ) ;
		} else if ( flavor.equals( stringFlavor)) {
			return dmtn.toString();
		} else {
			throw new UnsupportedFlavorException (flavor);
		}
	}
	


	/**
	 *  Definition of the data flavor for the nodes.
	 */
	public static DataFlavor dmtnFlavor;
	static{
		try{
			dmtnFlavor = new DataFlavor( Object.class, "JpoTransferable" );
		} catch ( Exception e ) {
			Tools.log( "JpoTransferable.dmtnFlavor failed to initialize: " + e.getMessage() );
		}
	}

	/**
	 *  Definition of the data flavor for the original hash code.
	 */
	public static DataFlavor originalHashCodeFlavor;
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
	 *   Definition of the data flavours supported by this Transferrable.
	 */
	private static final DataFlavor[] flavors = {
		JpoTransferable.dmtnFlavor,
		JpoTransferable.originalHashCodeFlavor,
		JpoTransferable.stringFlavor
	};
	
	
	/**
	 *   Returns the supported flavours.
	 */
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}
	

	/**
	 *  Definition of the flavorList
	 */	
	private static final List flavorList = Arrays.asList( flavors );

	
	/**
	 *   Returns if a flavor is supported.
	 */
	public boolean isDataFlavorSupported( DataFlavor flavor ) {
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
