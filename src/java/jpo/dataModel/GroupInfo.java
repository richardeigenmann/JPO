package jpo.dataModel;

import jpo.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;


/*
GroupInfo.java:  definitions for the group objects

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
    private StringBuffer groupName;

    /**
     *   Constructor to create a new GroupInfo object.
     *
     *   @param	description	The description of the Group
     **/
    public GroupInfo( String description ) {
        setGroupName( description );
    }

    /**
     *   toString method that returns the description of the group
     *
     * @return
     */
    @Override
    public String toString() {
        return groupName.toString();
    }

    /**
     *   Returns the description of the group.
     *
     *   @return	The description of the Group.
     *   @see #setGroupName
     **/
    public String getGroupName() {
        return groupName.toString();
    }

    /**
     *   Set name of the GroupInfo
     *
     *   @param name 	The new description of the GroupInfo
     *   @see #getGroupName
     **/
    public void setGroupName( String name ) {
        groupName = new StringBuffer( name) ;

        if ( ( Settings.pictureCollection != null ) && ( Settings.pictureCollection.getSendModelUpdates() ) ) {
            Settings.pictureCollection.setUnsavedUpdates();
        }
    }
    //----------------------------------------
    /**
     * The full path to the lowres version of the picture.
     *
     */
    private String lowresLocation = "";

    /**
     * Returns the full path to the lowres picture.
     * @return   The lowres location.
     */
    public String getLowresLocation() {
        return lowresLocation;
    }

    /**
     * Returns the file handle to the lowres picture.
     * @see	#getLowresURL()
     * @return  The file handle for the lowres picture. Returns null if there is no filename.
     */
    public File getLowresFile() {
        if ( lowresLocation.equals( "") ) {
            return null;
        }
        try {
            return new File( new URI( lowresLocation ) );
        } catch ( URISyntaxException x ) {
            Tools.log( "Conversion of " + lowresLocation + " to URI failed: " + x.getMessage() );
            return null;
        }
    }

    /**
     * Returns the URL handle to the lowres picture.
     * @return  The URL of the lowres picture.
     * @throws MalformedURLException if there was a drama
     */
    public URL getLowresURL() throws MalformedURLException {
        URL lowresURL = new URL( lowresLocation );
        return lowresURL;
    }

    /**
     * returns the URL handle to the lowres picture or null. I invented this
     * because I got fed up trying and catching the MalformedURLException that
     * could be thrown.
     *  @return the lowres location
     */
    public URL getLowresURLOrNull() {
        try {
            URL lowresURL = new URL( lowresLocation );
            return lowresURL;
        } catch ( MalformedURLException x ) {
            Tools.log( "Caught an unexpected MalformedURLException: " + x.getMessage() );
            return null;
        }
    }

    /**
     * Sets the full path to the lowres picture.
     * @param s The new location
     */
    public synchronized void setLowresLocation( String s ) {
        if ( !lowresLocation.equals( s ) ) {
            lowresLocation = s;
            if ( ( Settings.pictureCollection != null ) && ( Settings.pictureCollection.getSendModelUpdates() ) ) {
                Settings.pictureCollection.setUnsavedUpdates();
            }
        }
    }

    /**
     * Sets the full path to the highres picture.
     * @param u The new location for the highres picture.
     */
    public synchronized void setLowresLocation( URL u ) {
        String s = u.toString();
        if ( !lowresLocation.equals( s ) ) {
            lowresLocation = s;
            if ( Settings.pictureCollection.getSendModelUpdates() ) {
                Settings.pictureCollection.setUnsavedUpdates();
            }
        }
    }

    /**
     *  Appends the text to the lowres location (for the XML parser).
     *  @param  s  The text fragement to be added to the Lowres Location.
     */
    public synchronized void appendToLowresLocation( String s ) {
        if ( s.length() > 0 ) {
            lowresLocation = lowresLocation.concat( s );
            if ( ( Settings.pictureCollection != null ) && ( Settings.pictureCollection.getSendModelUpdates() ) ) {
                Settings.pictureCollection.setUnsavedUpdates();
            }
        }
    }

    /**
     *  Returns just the Filename of the lowres picture.
     *  @return  the filename of the lowres picture without any preceeding path.
     */
    public String getLowresFilename() {
        return new File( lowresLocation ).getName();

    }

    /**
     *  this method writes all attributes of the picture in the JPO
     *  xml data format.
     *
     *  @param out	The Bufferer Writer receiving the xml data
     *  @param rootNode
     * @param protection
     * @throws IOException if there is a drama writing the file.
     */
    public void dumpToXml( BufferedWriter out, boolean rootNode, boolean protection )
            throws IOException {
        dumpToXml( out, getLowresLocation(), rootNode, protection );
    }

    /**
     *  this method writes all attributes of the picture in the JPO
     *  xml data format with the highres and lowres locations passed in as
     *  parameters. This became necessary because when the XmlDistiller
     *  copies the pictures to a new location we don't want to write the
     *  URLs of the original pictures whilst all other attributes are retained.
     *
     *  @param out	The Bufferer Writer receiving the xml data
     *  @param lowres	The URL of the lowres file
     *  @param rootNode
     * @param protection 
     * @throws IOException  If there was an IO error
     */
    public void dumpToXml( BufferedWriter out, String lowres, boolean rootNode, boolean protection )
            throws IOException {

        if ( rootNode ) {
            out.write( "<collection collection_name=\"" + Tools.escapeXML( getGroupName() ) + "\" collection_created=\"" + DateFormat.getDateInstance().format( Calendar.getInstance().getTime() ) + "\"" + ( protection ? " collection_protected=\"No\"" : " collection_protected=\"Yes\"" ) );
        } else {
            out.write( "<group group_name=\"" + Tools.escapeXML( getGroupName() ) + "\"" );
        }
        out.newLine();

        if ( lowres.length() > 0 ) {
            if ( rootNode ) {
                out.write( "collection_icon=\"" );
            } else {
                out.write( "group_icon=\"" );
            }
            out.write( Tools.escapeXML( lowres ) + "\"" );
        }

        out.write( ">" );
        out.newLine();
    }

    public void endGroupXML( BufferedWriter out, boolean rootNode )
            throws IOException {

        if ( !rootNode ) {
            out.write( "</group>" );
        }  // if it is root Node then the XmlDistiller adds the categories and end collection tag.
        out.newLine();
    }
}
