package jpo;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;


/*
GroupInfo.java:  definitions for the group objects

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
 * A class which holds information about the group and has been 
 * given the intelligence of how to write itself and it's pictures to an html
 * document.
 *  <p> This class must implement the Serializable interface or Drag and Drop will not work.
 *
 * @see PictureInfo
 */

public class GroupInfo implements Serializable {
	/**
	 *  The description of the GroupInfo.
	 **/
       	public String GroupDescription;
	

	/**
	 *   Constructor to create a new GroupInfo object. 
	 *
	 *   @param	description	The description of the Group
	 **/			      
	public GroupInfo( String description ) {
		setGroupName( description );
	}

	/**
	 *   toString method that returns the descrition of the group
	 **/
        public String toString() {
		return GroupDescription;
	}

	
	/**
	 *   Returns the description of the group.
	 *
	 *   @return	The description of the Group.
	 *   @see #setGroupName
	 **/			      
	public String getGroupName() {
		return GroupDescription;
	}


	/**
	 *   Set name of the GroupIno. The synchronized keyword is really important
	 *   because it prevents the JTree from displaying inconsistent
	 *   Group Nodes which have nodes with very tall textareas. - I hope it does
	 *
	 *   @param name 	The new description of the GroupInfo
	 *   @see #getGroupName
	 **/			      
	public synchronized void setGroupName( String name ) {
		GroupDescription = name;

		if ( (Settings.top != null) && (Settings.top.getSendModelUpdates()) ) {
			Settings.top.setUnsavedUpdates();
		}
	}


	//----------------------------------------



	/**
	 * The full path to the lowres version of the picture.
	 *
	*/
	private String  Lowres_Name = "";


	/** 
	 * Returns the full path to the lowres picture.
	 * @return   The lowres location.
	 */
       	public String getLowresLocation() {
		return Lowres_Name;
       	}


	/** 
	 * Returns the file handle to the lowres picture.
	 * @see	#getLowresURL()
	 * @return  The file handle for the lowres picture.
	 */
       	public File getLowresFile() {
		try {
			return  new File(new URI(Lowres_Name));
		} catch ( URISyntaxException x ) {
		Tools.log ( "Conversion of " + Lowres_Name + " to URI failed: " + x.getMessage() );
			return null;
		}
       	}



	/** 
	 * Returns the URL handle to the lowres picture.
	 * @return  The URL of the lowres picture.
	 * @throws MalformedURLException if there was a drama
	 */
       	public URL getLowresURL() throws MalformedURLException  {
		URL lowresURL = new URL ( Lowres_Name );
		return lowresURL;
       	}


	/** 
	 * returns the URL handle to the lowres picture or null. I invented this 
	 * because I got fed up trying and catching the MalformedURLException that
	 * could be thrown.
	 *  @return the lowres location
	 */
       	public URL getLowresURLOrNull(){
		try {
			URL lowresURL = new URL ( Lowres_Name );
			return lowresURL;
		} catch ( MalformedURLException x ) {
			Tools.log ( "Caught an unexpected MalformedURLException: " + x.getMessage() );
			return null;
		}
       	}




	/** 
	 * Sets the full path to the lowres picture.
	 * @param s The new location
	 */
       	public synchronized void setLowresLocation(String s) {
		if ( ! Lowres_Name.equals( s ) ) {
			Lowres_Name = s;
			if ( (Settings.top != null) && (Settings.top.getSendModelUpdates()) ) {
				Settings.top.setUnsavedUpdates();
			}
		}
       	}



	/** 
	 * Sets the full path to the highres picture.
	 * @param u The new location for the highres picture.
	 */
       	public synchronized void setLowresLocation( URL u) {
		String s = u.toString();
		if ( ! Lowres_Name.equals( s ) ) {
			Lowres_Name = s;
			if ( Settings.top.getSendModelUpdates() ) {
					Settings.top.setUnsavedUpdates();
			}
		}
       	}


	/** 
	 *  Appends the text to the lowres location (for the XML parser).
	 *  @param  s  The text fragement to be added to the Lowres Location.
	 */
       	public synchronized void appendToLowresLocation( String s ) {
		if ( s.length() > 0 ) {
			Lowres_Name = Lowres_Name.concat(s);
			if ( (Settings.top != null) && (Settings.top.getSendModelUpdates()) ) {
				Settings.top.setUnsavedUpdates();
			}
		}		
       	}


	/**
	 *  Returns just the Filename of the lowres picture.
	 *  @return  the filename of the lowres picture without any preceeding path.
	 */
	public String getLowresFilename() {
		return new File(Lowres_Name).getName();		
	
	} 


	/**
	 *  this method writes all attributes of the picture in the JPO
	 *  xml data format.
	 *
	 *  @param out	The Bufferer Writer receiving the xml data
	 *  @throws IOException if there is a drama writing the file.
	 */
	public synchronized void dumpToXml ( BufferedWriter out, boolean rootNode, boolean protection ) 
		throws IOException {
		dumpToXml ( out, getLowresLocation(), rootNode, protection );
	}


	/**
	 *  this method writes all attributes of the picture in the JPO
	 *  xml data format with the highres and lowres locations passed in as 
	 *  parameters. This became nescesary because when the XmlDistiller 
	 *  copies the pictures to a new location we don't want to write the
	 *  URLs of the original pictures whilst all other attributes are retained.
	 *
	 *  @param out	The Bufferer Writer receiving the xml data
	 *  @param lowres	The URL of the lowres file
	 *  @throws IOException  If there was an IO error
	 */
	public synchronized void dumpToXml ( BufferedWriter out, String lowres, boolean rootNode, boolean protection ) 
		throws IOException {
		
		if ( rootNode ) {
			out.write("<collection collection_name=\"" 
				+ Tools.escapeXML( getGroupName() )
				+ "\" collection_created=\"" 
				+ DateFormat.getDateInstance().format(Calendar.getInstance().getTime()) 
				+ "\""
				+ ( protection ? " collection_protected=\"No\"" : " collection_protected=\"Yes\"" ) );
		} else {
			out.write("<group group_name=\"" + Tools.escapeXML( getGroupName() ) + "\"");
		}
		out.newLine();

		if ( lowres.length() > 0 ) {
			if ( rootNode ) {
				out.write("collection_icon=\"");
			} else {
				out.write("group_icon=\"");
			}
			out.write( Tools.escapeXML( lowres ) + "\"");
		}

		out.write( ">");
		out.newLine();
	}

	public synchronized void endGroupXML ( BufferedWriter out, boolean rootNode ) 
		throws IOException {
		
		if (! rootNode) {
			out.write("</group>");
		}  // if it is root Node then the XmlDistiller adds the categories and end collection tag.
		out.newLine();
	}		

	

}
