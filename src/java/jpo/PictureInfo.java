package jpo;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;



/*
PictureInfo.java:  the definitions for picture data

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
 *  Objects of this type represent a single picture in the collection. Since SortableDefaultMutableTreeNodes
 *  allow user obects to be attached to the node this is a conveinent place to store all the information
 *  that we have about a picture. <p>
 *  The class provides several convenience methods to access the information.
 *  <p> This class must implement the Serializable interface or Drag and Drop will not work.
 *
 *  @see GroupInfo
 */
public class PictureInfo implements Serializable {



	/**
	 * Constructor method. Creates the object and sets up the variables.
	 *
	 * @param 	Highres_Name 		The filename of the high resolution image
	 * @param 	Lowres_Name		The filename of the low resolution image
	 * @param 	description		The description of the image
	 * @param	filmReference		The reference to the film if any
	 */
	public PictureInfo(String Highres_Name, 
			String Lowres_Name, 
			String description, 
			String filmReference) {
		this.Highres_Name = Highres_Name;
		this.Lowres_Name = Lowres_Name;
		this.description = description;
		this.filmReference = filmReference;
		
		Tools.log ( "Highres_Name: " + Highres_Name);
		Tools.log ( "Lowres_Name: " + Lowres_Name);
		Tools.log ( "description: " + description);
		Tools.log ( "filmReference: " + filmReference);
        }



	/**
	 * Constructor method. Creates the object and sets up the variables.
	 * @param 	highresURL 		The filename of the high resolution image
	 * @param 	Lowres_Name		The filename of the low resolution image
	 * @param 	description		The description of the image
	 * @param	filmReference		The reference to the film if any
	 */
	public PictureInfo(URL highresURL, 
			String Lowres_Name, 
			String description, 
			String filmReference) {
		this.Highres_Name = highresURL.toString();
		this.Lowres_Name = Lowres_Name;
		this.description = description;
		this.filmReference = filmReference;
		
		Tools.log ( "Highres_Name: " + Highres_Name);
		Tools.log ( "Lowres_Name: " + Lowres_Name);
		Tools.log ( "description: " + description);
		Tools.log ( "filmReference: " + filmReference);
		
        }


	/** 
	 *  Constructor without optons. All strings are set to blanks
	 */
	public PictureInfo() {
		Highres_Name = "";
		Lowres_Name = "";
		description = "";
		filmReference="";
        }
	
	

	/** 
	 * returns the description of the image in the default <code>toString</code> method.
	 */
       	public String toString() {
		return description;
       	}

	
	/**
	 *  this method writes all attributes of the picture in the JPO
	 *  xml data format.
	 *
	 *  @param out	The Bufferer Writer receiving the xml data
	 *  @throws IOException if there is a drama writing the file.
	 */
	public synchronized void dumpToXml ( BufferedWriter out ) 
		throws IOException {
		dumpToXml ( out, getHighresLocation(), getLowresLocation() );
	}


	/**
	 *  this method writes all attributes of the picture in the JPO
	 *  xml data format with the highres and lowres locations passed in as 
	 *  parameters. This became nescesary because when the XmlDistiller 
	 *  copies the pictures to a new location we don't want to write the
	 *  URLs of the original pictures whilst all other attributes are retained.
	 *
	 *  @param out	The Bufferer Writer receiving the xml data
	 *  @param highres	The URL of the highres file
	 *  @param lowres	The URL of the lowres file
	 *  @throws IOException  If there was an IO error
	 */
	public synchronized void dumpToXml ( BufferedWriter out, String highres, String lowres ) 
		throws IOException {
		out.write("<picture>");
		out.newLine();
		out.write("\t<description><![CDATA[" + getDescription() + "]]></description>");
		out.newLine();

		if ( ( getCreationTime() != null ) && ( getCreationTime().length() > 0 ) ) {
			out.write("\t<CREATION_TIME><![CDATA[" + getCreationTime() + "]]></CREATION_TIME>");
			out.newLine();
		}
		
		if ( highres.length() > 0 ) {
			out.write("\t<file_URL>" + Tools.escapeXML( highres )+ "</file_URL>");
			out.newLine();
		}

		if ( checksum > Long.MIN_VALUE ) {
			out.write("\t<checksum>" + Long.toString( checksum ) + "</checksum>");
			out.newLine();
		}

		
		if ( lowres.length() > 0 ) {
			out.write("\t<file_lowres_URL>" + Tools.escapeXML( lowres ) + "</file_lowres_URL>");
			out.newLine();
		}

		if ( getComment().length() > 0 ) {
			out.write("\t<COMMENT>" + Tools.escapeXML( getComment() )+ "</COMMENT>");
			out.newLine();
		}

		if ( getPhotographer().length() > 0 ) {
			out.write("\t<PHOTOGRAPHER>" + Tools.escapeXML( getPhotographer() ) + "</PHOTOGRAPHER>");
			out.newLine();
		}

		if ( getFilmReference().length() > 0 ) {
			out.write("\t<film_reference>" + Tools.escapeXML( getFilmReference() ) + "</film_reference>");
			out.newLine();
		}

		if ( getCopyrightHolder().length() > 0 ) {
			out.write("\t<COPYRIGHT_HOLDER>" + Tools.escapeXML( getCopyrightHolder() ) + "</COPYRIGHT_HOLDER>");
			out.newLine();
		}

		
		if ( getRotation() != 0 ) {
			out.write("\t<ROTATION>" + (new Double(getRotation())).toString() + "</ROTATION>");
			out.newLine();
		}
		
		if ( categoryAssignments != null ) {
			Iterator i = categoryAssignments.iterator();
			Integer assignment;
			while ( i.hasNext() ) {
				assignment = (Integer) i.next();
				out.write("\t<categoryAssignment index=\"" + assignment.toString()+ "\"/>" );
				out.newLine();
			}
		}


		out.write("</picture>");
		out.newLine(); 
	}



	//----------------------------------------


	/**
	 * The description of the image.
	*/
	private String  description = "";

	/** 
	 *  Returns the description of the image.
	 *
	 *  @return	The description of the image.
	 *  @see #setDescription
	 */
       	public String getDescription() {
		return description;
       	}


	/** 
	 *  Sets the description of the image.
	 *
	 *  @param desc  New description of the image.
	 *  @see #getDescription
	 */
       	public synchronized void setDescription( String desc ) {
		if ( ! desc.equals( description ) ) {
			description = desc;
			sendDescriptionChangedEvent();
		}
       	}



	/** 
	 *  Appends the text fragment to the description.
	 *  @param s	The text fragment to append.
	 */
	public synchronized void appendToDescription(String s) {
		if ( s.length() > 0 ) {
			description = description.concat(s);
			sendDescriptionChangedEvent();
		}
       	}


	/**
	 *  Checks whether the searchString parameter is contained in the description.
	 *  The search is case insensitive.
	 *
	 *  @param	searchString	The string to search for.
	 *  @return	true if found. false if not.
	 */
	public boolean descriptionContains( String searchString ) {
		return description.toUpperCase().indexOf( searchString.toUpperCase() ) > -1;
	}


	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the description was updated.
	 */
	private void sendDescriptionChangedEvent() {
		if ( Settings.top.getSendModelUpdates() ) {
			PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
			pce.setDescriptionChanged();
			sendPictureInfoChangedEvent( pce );
			Settings.top.setUnsavedUpdates();
		}
	}

	//----------------------------------------


	/**
	 * The full path to the high resolution version of the picture.
	 * @see #Lowres_Name

	*/
	private String  Highres_Name = "";


	/** 
	 * Returns the full path to the highres picture.
	 * @return  The highres location
	 * @see #setHighresLocation
	 */
       	 public  String getHighresLocation() {
		return Highres_Name;
       	}


	/** 
	 * returns the file handle to the highres picture.
	 * @see	#getHighresURL()
	 * @return the highres location.
	 */
       	public File getHighresFile() {
		File  returnFile;
		try {
			returnFile = new File( new URI (Highres_Name) );
		} catch ( URISyntaxException x ) {
			returnFile = null;
		}
		return returnFile;
       	}


	/** 
	 * returns the URL handle to the highres picture.
	 *  @return the highres location
	 *  @throws  MalformedURLException if the location could not be converted to a URL.
	 */
       	public URL getHighresURL()  throws MalformedURLException {
		URL highresURL = new URL ( Highres_Name );
		return highresURL;
       	}


	/** 
	 * returns the URL handle to the highres picture or null. I invented this 
	 * because I got fed up trying and catching the MalformedURLException that
	 * could be thrown.
	 *  @return the highres location
	 */
       	public URL getHighresURLOrNull(){
		try {
			URL highresURL = new URL ( Highres_Name );
			return highresURL;
		} catch ( MalformedURLException x ) {
			Tools.log ( "Caught an unexpected MalformedURLException: " + x.getMessage() );
			return null;
		}
       	}
	
	/** 
	 * returns the URI handle to the highres picture.
	 * @return The highres location
	 */
       	public URI getHighresURIOrNull()  {
		try {
			return new URI ( Highres_Name );
		} catch ( URISyntaxException x ) {
			return null;
		}
       	}


	/** 
	 * Sets the full path to the highres picture.
	 * @param s The new location for the highres picture.
	 * @see #getHighresLocation
	 */
       	public synchronized void setHighresLocation(String s) {
		if ( ! Highres_Name.equals( s ) ) {
			Highres_Name = s;
			sendHighresLocationChangedEvent();
		}
		getHighresFile(); // just so that it creates a failure if the filename is not conform.
       	}



	/** 
	 * Sets the full path to the highres picture.
	 * @param u The new location for the highres picture.
	 */
       	public synchronized void setHighresLocation( URL u) {
		String s = u.toString();
		if ( ! Highres_Name.equals( s ) ) {
			Highres_Name = s;
			sendHighresLocationChangedEvent();
		}
       	}



	/** 
	 * Appends the text to the field (used by XML parser).
	 * @param s The text fragement to be added to the Highres Location
	 */
       	public synchronized void appendToHighresLocation(String s) {
		if ( s.length() > 0 ) {
			Highres_Name = Highres_Name.concat(s);
			sendHighresLocationChangedEvent();
		}
       	}




	/**
	 *  Returns just the Filename of the highres picture.
	 *  @return the highres Filename
	 */
	public String getHighresFilename() {
		return new File(Highres_Name).getName();		
	
	} 
	

	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the highres location was updated.
	 */
	private void sendHighresLocationChangedEvent() {
		if ( Settings.top.getSendModelUpdates() ) {
			PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
			pce.setHighresLocationChanged();
			sendPictureInfoChangedEvent( pce );
			Settings.top.setUnsavedUpdates();
		}
	}




	//----------------------------------------


	/**
	 *  the value of the checksum of the image file
	 */
	private long checksum = Long.MIN_VALUE;	


	/**
	 *  returns the value of the checksum or Long.MIN_VALUE if it is not set.
	 */
	public long getChecksum() {
		return checksum;
	}
	
	/**
	 *  returns the value of the checksum or the text "N/A" if not defined.
	 */
	public String getChecksumAsString() {
		if ( checksum != Long.MIN_VALUE ) {
			return Long.toString( checksum );
		} else {
			return "N/A";
		}
	}
	
	
	/**
	 *  allows the checksum to be set
	 */
	public void setChecksum( long newValue ) {
		checksum = newValue;
		sendChecksumChangedEvent();
	}


	/**
	 *  calculates the Adler32 checksum of the current picture.
	 */
	public void calculateChecksum() {
		URL pictureURL = getHighresURLOrNull();
		if ( pictureURL == null ) {
			Tools.log( "PictureInfo.calculateChecksum din't get the URL. Aborting." );
			return;
		}

		InputStream in;
		try {
			in = pictureURL.openStream();
		} catch ( IOException x ) {
			Tools.log( "PictureInfo.calculateChecksum couldn't open URL. Aborting." );
			return;
		}

		BufferedInputStream bin = new BufferedInputStream( in );
		
		checksum = Tools.calculateChecksum ( bin );
		
		// Tools.log( "Checksum is: " + Long.toString( checksum ) );
		sendChecksumChangedEvent();
	}

	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the checksum was updated.
	 */
	private void sendChecksumChangedEvent() {
		if ( Settings.top.getSendModelUpdates() ) {
			PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
			pce.setChecksumChanged();
			sendPictureInfoChangedEvent( pce );
			Settings.top.setUnsavedUpdates();
		}
	}



	/**
	 *  Temporary variable to allow appending of characters as the XML file 
	 *  is being read.
	 */
	private String checksumString = "";

	/** 
	 * Appends the text fragment to the checksum field.
	 * @param s Text fragment
	 */
       	public synchronized void appendToChecksum(String s) {
		if ( s.length() > 0 ) {
			checksumString = checksumString.concat(s);
			sendChecksumChangedEvent();
		}
	}
	
	/**
	 *  Converts the temporary checksumString to the checksum long.
	 */
	public void parseChecksum() {
		try {
			//Tools.log("PictureInfo.parseChecksum: " + checksumString);
			checksum = (new Long( checksumString ) ).longValue();
			checksumString = "";
		} catch( NumberFormatException x ) {
			Tools.log("PictureInfo.parseChecksum: invalid checksum: " + checksumString + " on picture: " + getHighresFilename() + " --> Set to MIN");
			checksum = Long.MIN_VALUE;
		}
		sendChecksumChangedEvent();
       	}




	//----------------------------------------



	/**
	 * The full path to the lowres version of the picture.
	 *
	 * @see #Highres_Name
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
			sendLowresLocationChangedEvent();
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
			sendLowresLocationChangedEvent();
		}
       	}


	/** 
	 *  Appends the text to the lowres location (for the XML parser).
	 *  @param  s  The text fragement to be added to the Lowres Location.
	 */
       	public synchronized void appendToLowresLocation( String s ) {
		if ( s.length() > 0 ) {
			Lowres_Name = Lowres_Name.concat(s);
			sendLowresLocationChangedEvent();
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
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the lowres location was updated.
	 */
	private void sendLowresLocationChangedEvent() {
		if ( Settings.top.getSendModelUpdates() ) {
			PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
			pce.setLowresLocationChanged();
			sendPictureInfoChangedEvent( pce );
			Settings.top.setUnsavedUpdates();
		}
	}


	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the lthumbnail was updated.
	 */
	public void sendThumbnailChangedEvent() {
		if ( Settings.top.getSendModelUpdates() ) {
			PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
			pce.setThumbnailChanged();
			sendPictureInfoChangedEvent( pce );
		}
	}


	//----------------------------------------


	/**
	 * The film reference of the image.
	*/
	private String  filmReference = "";

	/** 
	 * Appends the fractment to the film referencefield.
	 * @param s Fragment to append to Film Reference
	 */
       	public synchronized void appendToFilmReference( String s ) {
		if ( s.length() > 0 ) {
			filmReference = filmReference.concat( s );
			sendFilmReferenceChangedEvent();
		}
       	}
	
	
	/**
	 *  Returns the film reference.
	 *  @return  the film reference
	 */
	public String getFilmReference() {
		return filmReference;
	}


	/** 
	 * Sets the film reference.
	 * @param s The new film reference.
	 */
       	public synchronized void setFilmReference( String s ) {
		if ( ! filmReference.equals( s ) ) {
			filmReference = s;
			sendFilmReferenceChangedEvent();
		}
       	}


	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the film reference was updated.
	 */
	private void sendFilmReferenceChangedEvent() {
		if ( Settings.top.getSendModelUpdates() ) {
			PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
			pce.setFilmReferenceChanged();
			sendPictureInfoChangedEvent( pce );
			Settings.top.setUnsavedUpdates();
		}
	}


	//----------------------------------------


	/**
	 *  The time the image was created. This should be the original 
         *  time when the shutter snapped closed and not the time of 
         *  scanning etc.
	 */
	private String creationTime = "";


	/** 
	 * Sets the creationTime.
	 * @param s The new creation time.
	 */
       	public synchronized void setCreationTime( String s ) {
		if ( ( s != null ) && ( ! creationTime.equals( s ) ) ) {
			creationTime = s;
			sendCreationTimeChangedEvent();
		}
       	}


	/** 
	 * appends the text fragement to the creation time.
	 * @param  s  The text fragment to add.
	 */
       	public synchronized void appendToCreationTime( String s ) {
		if ( s.length() > 0 ) {
			creationTime = creationTime.concat(s);
			sendCreationTimeChangedEvent();
		}
       	}
	
	
	/**
	 *  Returns the creation Time.
	 *  @return the creation Time
	 */
	public String getCreationTime() {
		return creationTime;
	}
	
	
	/**
	 *  Returns the creationTime as a Date object or null if the parsing failed.
	 */
	public Date getCreationTimeAsDate() {
		return ( Tools.parseDate( creationTime ) );
	}

	/**
	 *  Returns the creationTime as a string after it has been parsed. Essentially this is 
	 *  a utility method to identify what the Date parser is doing.
	 */
	public String getFormattedCreationTime() {
		String formattedDate;
		Date d = getCreationTimeAsDate();
		if ( d == null ) {
			formattedDate = Settings.jpoResources.getString("failedToParse");
		} else {
			formattedDate = Settings.jpoResources.getString("parsedAs") +
				DateFormat.getDateTimeInstance().format( d );
		}	
		return formattedDate;
	}

	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the film reference was updated.
	 */
	private void sendCreationTimeChangedEvent() {
		if ( Settings.top.getSendModelUpdates() ) {
			PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
			pce.setCreationTimeChanged();
			sendPictureInfoChangedEvent( pce );
			Settings.top.setUnsavedUpdates();
		}
	}


	//----------------------------------------


	/**
	 *  The time the image was created. This should be the original 
         *  time when the shutter snapped closed and not the time of 
         *  scanning etc.
	 */
	private String comment = "";


	/** 
	 * Sets the comment.
	 * @param s The new comment
	 */
       	public synchronized void setComment( String s ) {
		if ( ! comment.equals( s ) ) {
			comment = s;
			sendCommentChangedEvent();
		}
       	}


	/** 
	 * Appends the text fragment to the comment.
	 * @param s the text fragment
	 */
       	public synchronized void appendToComment( String s ) {
		if ( s.length() > 0 ) {
			comment = comment.concat(s);
			sendCommentChangedEvent();
		}
       	}
	
	
	/**
	 *  Returns the comment.
	 *  @return The comment.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the comment was updated.
	 */
	private void sendCommentChangedEvent() {
		if ( Settings.top.getSendModelUpdates() ) {
			PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
			pce.setCommentChanged();
			sendPictureInfoChangedEvent( pce );
			Settings.top.setUnsavedUpdates();
		}
	}

	//----------------------------------------


	/**
	 *  The time the image was created. This should be the original 
         *  time when the shutter snapped closed and not the time of 
         *  scanning etc.
	 */
	private String photographer = "";


	/** 
	 * Sets the Photographer.
	 * @param s The new Photographer
	 */
       	public synchronized void setPhotographer( String s ) {
		if ( ! photographer.equals( s ) ) {
			photographer = s;
			sendPhotographerChangedEvent();
		}
       	}


	/** 
	 * Appends the text fragement to the photographer field.
	 * @param s The photographer.
	 */
       	public synchronized void appendToPhotographer( String s ) {
		if ( s.length() > 0 ) {
			photographer = photographer.concat(s);
			sendPhotographerChangedEvent();
		}
       	}
	
	
	/**
	 *  Returns the photographer.
	 *  @return The Photographer.
	 */
	public String getPhotographer() {
		return photographer;
	}


	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the photographer was updated.
	 */
	private void sendPhotographerChangedEvent() {
		if ( Settings.top.getSendModelUpdates() ) {
			PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
			pce.setPhotographerChanged();
			sendPictureInfoChangedEvent( pce );
			Settings.top.setUnsavedUpdates();
		}
	}

	//----------------------------------------


	/**
	 *  The copyright holder of the image.
	 */
	private String copyrightHolder = "";


	/** 
	 * Sets the copyright holder.
	 * @param s The copyright holder
	 */
       	public synchronized void setCopyrightHolder( String s ) {
		if ( ! copyrightHolder.equals( s ) ) {
			copyrightHolder = s;
			sendCopyrightHolderChangedEvent();
		}
       	}


	/** 
	 * appends the text fragment to the copyright holder field.
	 * @param s The text fragment.
	 */
       	public synchronized void appendToCopyrightHolder( String s ) {
		if ( s.length() > 0 ) {
			copyrightHolder = copyrightHolder.concat(s);
			sendCopyrightHolderChangedEvent();
		}
       	}
	
	
	/**
	 *  returns the copyright holder.
	 *  @return The copyright holder
	 */
	public String getCopyrightHolder() {
		return copyrightHolder;
	}

	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the copyright holder was updated.
	 */
	private void sendCopyrightHolderChangedEvent() {
		if ( Settings.top.getSendModelUpdates() ) {
			PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
			pce.setCopyrightHolderChanged();
			sendPictureInfoChangedEvent( pce );
			Settings.top.setUnsavedUpdates();
		}
	}

	//----------------------------------------


	/**
	 * The rotation factor to apply after loading the image.
	*/
	private double  rotation = 0;
	
	/**
	 *  Temporary variable to allow appending of characters as the XML file 
	 *  is being read.
	 */
	private String rotationString = "";

	/** 
	 * Appends the text fragment to the rotation field.
	 * @param s Text fragment
	 */
       	public synchronized void appendToRotation(String s) {
		if ( s.length() > 0 ) {
			rotationString = rotationString.concat(s);
			sendRotationChangedEvent();
		}
	}
	
	/**
	 *  Converts the temporary rotationString to the rotation double.
	 */
	public void parseRotation() {
		try {
			rotation = (new Double( rotationString ) ).doubleValue();
			rotationString = "";
		} catch( NumberFormatException x ) {
			Tools.log("PictureInfo.appendToRotation: invalid rotation: " + rotationString + " on picture: " + getHighresFilename() + " --> Set to Zero");
			rotation = 0;
		}
		sendRotationChangedEvent();

       	}
	
	
	/**
	 *  Returns the rotation.
	 *  @return  The rotation of the image.
	 */
	public double getRotation() {
		return rotation;
	}


	/** 
	 *  Sets the rotation.
	 *  @param  rotation 	The new rotation for the PictureInfo.
	 */
       	public synchronized void setRotation( double rotation ) {
		if ( this.rotation != rotation ) {
			this.rotation = rotation;
			sendRotationChangedEvent();
		}
       	}

	/** 
	 *  Sets the rotation.
	 *  @param  rotation 	The new rotation for the PictureInfo.
	 */
       	public synchronized void setRotation( int rotation ) {
		setRotation( (double) rotation );
       	}


	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the copyright holder was updated.
	 */
	private void sendRotationChangedEvent() {
		if ( Settings.top.getSendModelUpdates() ) {
			PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
			pce.setRotationChanged();
			sendPictureInfoChangedEvent( pce );
			Settings.top.setUnsavedUpdates();
		}
	}

	//----------------------------------------


	/**
	 * The category assginments are held in the categoryAssignments HashSet.
	 */
	public HashSet categoryAssignments;

	/**
	 *  removes all category Assignments
	 */
	public void clearCategoryAssignments() {
		if ( categoryAssignments != null ) {
			categoryAssignments.clear();
		}
	}
	
	/**
	 *  Temporary variable to allow appending of characters as the XML file 
	 *  is being read.
	 */
	private String categoryAssignmentString = "";

	/**
	 * Returns an Array of the category assignments associated with this picture
	 */
	public Object[] getCategoryAssignmentsAsArray() {
		return categoryAssignments.toArray();
	}
	
	

	/** 
	 * Appends the text fragment to the categoryAssignmentString field.
	 * @param s Text fragment
	 */
       	public synchronized void appendToCategoryAssignment( String s ) {
		if ( s.length() > 0 ) {
			categoryAssignmentString = categoryAssignmentString.concat(s);
		}
	}


	/** 
	 * Adds to the categoryAssignmentString HashSet.
	 * @param s Text fragment
	 */
       	public synchronized void addCategoryAssignment( String s ) {
		if ( s.length() > 0 ) {
			categoryAssignmentString = s;
			parseCategoryAssignment();
		}
	}

	
	/** 
	 * Adds to the categoryAssignment HashSet.
	 */
       	public synchronized void addCategoryAssignment( Object key ) {
		if ( categoryAssignments == null ) {
			categoryAssignments = new HashSet();
		}
		categoryAssignments.add( key );
	}

	
	/**
	 *  Converts the temporary categoryAssignmentString to a categoryAssignment.
	 */
	public void parseCategoryAssignment() {
		try {
			Integer category = new Integer( categoryAssignmentString );
			categoryAssignmentString = "";
			addCategoryAssignment( category );
		} catch( NumberFormatException x ) {
			Tools.log("PictureInfo.parseCategoryAssignment: NumberFormatException: " + categoryAssignmentString + " on picture: " + getHighresFilename() + " because: " + x.getMessage() );
		}
		sendCategoryAssignmentsChangedEvent();
       	}
	

	/**
	 *  Returns whether the category is part of the attributes of the picture
	 */
	public boolean containsCategory( Object key ) {
		if ( categoryAssignments == null ) {
			return false;
		}
		return categoryAssignments.contains( key );
	}


	/**
	 *  Returns whether the category is part of the attributes of the picture
	 */
	public void removeCategory( Object key ) {
		if ( categoryAssignments != null ) {
			categoryAssignments.remove( key );
		}
	}


	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the copyright holder was updated.
	 */
	private void sendCategoryAssignmentsChangedEvent() {
		if ( Settings.top.getSendModelUpdates() ) {
			PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
			pce.setCategoryAssignmentsChanged();
			sendPictureInfoChangedEvent( pce );
			Settings.top.setUnsavedUpdates();
		}
	}


	//-------------------------------------------

	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the node was selected. Strictly speaking this is not a 
	 *  PictureInfo level event but a node level event. However, because I have 
	 *  the PictureInfoChangeEvent structure in place this is a good place to 
	 *  put this notification.
	 */
	public void sendWasSelectedEvent() {
		PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
		pce.setWasSelected();
		sendPictureInfoChangedEvent( pce );
	}


	//-------------------------------------------

	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the node was unselected. Strictly speaking this is not a 
	 *  PictureInfo level event but a node level event. However, because I have 
	 *  the PictureInfoChangeEvent structure in place this is a good place to 
	 *  put this notification.
	 */
	public void sendWasUnselectedEvent() {
		PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
		pce.setWasUnselected();
		sendPictureInfoChangedEvent( pce );
	}


	//-------------------------------------------

	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the node was mailSelected. Strictly speaking this is not a 
	 *  PictureInfo level event but a node level event. However, because I have 
	 *  the PictureInfoChangeEvent structure in place this is a good place to 
	 *  put this notification.
	 */
	public void sendWasMailSelectedEvent() {
		PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
		pce.setWasMailSelected();
		sendPictureInfoChangedEvent( pce );
	}


	//-------------------------------------------

	/**
	 *  Creates a PictureChangedEvent and sends it to inform listening 
	 *  objects that the node was mailUnselected. Strictly speaking this is not a 
	 *  PictureInfo level event but a node level event. However, because I have 
	 *  the PictureInfoChangeEvent structure in place this is a good place to 
	 *  put this notification.
	 */
	public void sendWasMailUnselectedEvent() {
		PictureInfoChangeEvent pce = new PictureInfoChangeEvent( this );
		pce.setWasMailUnselected();
		sendPictureInfoChangedEvent( pce );
	}


	//-------------------------------------------
	
	/** 
	 *  Returns a new PictureInfo object which is identical to the current one.
	 *  @return  a clone of the current PictureInfo object.
	 */
	public PictureInfo getClone() {
		PictureInfo clone = new PictureInfo();
		clone.setDescription( this.getDescription() );
		clone.setHighresLocation( this.getHighresLocation() );
		clone.setLowresLocation( this.getLowresLocation() );
		clone.setFilmReference( this.getFilmReference() );
		clone.setCreationTime( this.getCreationTime() );
		clone.setComment( this.getComment() );
		clone.setPhotographer( this.getPhotographer() );
		clone.setCopyrightHolder( this.getCopyrightHolder() );
		clone.setRotation( this.getRotation() );
		return clone;
	}




	/**
	 *  A vector that holds all the listeners that want to be notified about 
	 *  changes to this PictureInfo object.
	 */
	private Vector pictureInfoListeners = new Vector();
	
	/**
	 *  Method to register the listening object of the status events.
	 *  @param listener	The object that will receive notifications.
	 */
	public void addPictureInfoChangeListener ( PictureInfoChangeListener listener ) {
		//Tools.log("SourcePicture.addListener: listener added on SourcePicture " + Integer.toString(this.hashCode()) + " of class: " + listener.getClass().toString() );
		pictureInfoListeners.add( listener );
	}


	/**
	 *  Method to register the listening object of the status events. Will NOT throw an
	 *  exception if the listener was not in the Vector.
	 *
	 *  @param listener	The listener that doesn't want to notifications any more.
	 */
	public void removePictureInfoChangeListener ( PictureInfoChangeListener listener ) {
		//Tools.log("SourcePicture.removeListener: listener removed from SourcePicture " + Integer.toString(this.hashCode()) + " of class: " + listener.getClass().toString() );
		pictureInfoListeners.remove( listener );
	}


	/**
	 *  Send PictureInfoChangeEvents.
	 *  @param pce The Event we want to notify.
	 */
	private void sendPictureInfoChangedEvent ( PictureInfoChangeEvent pce ) {
		if ( Settings.top.getSendModelUpdates() ) {
			Enumeration e = pictureInfoListeners.elements();
			while ( e.hasMoreElements() ) {
				((PictureInfoChangeListener) e.nextElement()).pictureInfoChangeEvent( pce );
			}
		}
	}

	//-------------------------------------------
	


	/**
	 *  Checks whether the searchString parameter is contained in any of the fields.
	 *  It doesn't check the checksum, lowres filename, rotation.
	 *  It does chech te description, highres name, film reference, creation time, comment and copyright holder
	 *
	 *  @param	searchString	The string to search for.
	 *  @return	true if found. false if not.
	 */
	public boolean anyMatch( String searchString ) {
		boolean found = descriptionContains( searchString )
		|| ( getPhotographer().toUpperCase().indexOf( searchString.toUpperCase() ) > -1 )
		|| ( Highres_Name.toUpperCase().indexOf( searchString.toUpperCase() ) > -1 )
		|| ( getFilmReference().toUpperCase().indexOf( searchString.toUpperCase() ) > -1 )
		|| ( getCreationTime().toUpperCase().indexOf( searchString.toUpperCase() ) > -1 )
		|| ( getComment().toUpperCase().indexOf( searchString.toUpperCase() ) > -1 )
		|| ( getCopyrightHolder().toUpperCase().indexOf( searchString.toUpperCase() ) > -1 )
		;

		
		return found;
	}

}
