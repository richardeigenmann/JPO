package jpo;

import java.io.*;
import java.util.*;


/*
Camera.java:  information about the digital camera as seen by the filesystem

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
 * A class which holds information about the digital camera as seen by the filesystem
 *
 */

public class Camera implements Serializable {
	/**
	 *  The description of the Camera
	 **/
       	public String description = Settings.jpoResources.getString("newCamera");
	
	/**
	 *  The root directory of the camera on the filesystem
	 */
	public String rootDir = System.getProperty("java.io.tmpdir");


	/**
	 *  The command that will connect the camera from the computer.
	 */
	public String connectScript = "";

	/**
	 *  The command that will disconnect the camera from the computer.
	 */
	public String disconnectScript = "";


	/**
	 *  Indicator that tells the program to find new pictures based on filename if true.
	 */
	public boolean useFilename = true;


	private HashMap oldImage = new HashMap();
	
	private HashMap newImage = new HashMap();



	/**
	 *   Constructor to create a new Camera object. 
	 **/			      
	public Camera() {
	}

	/**
	 *   toString method that returns the descrition of the camera
	 **/
        public String toString() {
		return description;
	}



	/**
	 *   stores the checksum and file in the provided HashMap
	 */
	public void storePicture( HashMap hm, File f, long checksum ) {
		hm.put( f, new Long( checksum ) );
	}

	/**
	 *   stores the checksum and file in the newImage HashMap
	 */
	public void storePictureNewImage( File f, long checksum ) {
		storePicture( newImage, f, checksum);
	}


	/**
	 *  returns whether the provided checksum or file is registered in the old camera image.
	 */
	public boolean inOldImage( File f, long checksum ) {
		return inOldImage ( f ) || inOldImage( checksum );
	}

	/**
	 *  returns whether the provided checksum registered in the old camera image.
	 *  it determines whether to check by checksum or file based on the useChecksum and useFilename 
	 *  flags.
	 */
	public boolean inOldImage( long checksum) {
		//Tools.log("Camera.inOldImage: Checking Checksum: " + Long.toString(checksum) );
		return oldImage.containsValue( new Long( checksum ) );
	}

	/**
	 *  returns whether the provided file is registered in the old camera image.
	 *  it determines whether to check by checksum or file based on the useChecksum and useFilename 
	 *  flags.
	 */
	public boolean inOldImage( File f ) {
		//Tools.log("Camera.inOldImage: Checking File: " + f.toString() );
		return oldImage.containsKey( f );
	}


	/**
	 *  copies the entry specified by the file in the oldImage HashMap to the newImage HashMap.
	 */
	public void copyToNewImage( File f ) {
		Long checksumLong = (Long) oldImage.get( f );
		if ( checksumLong != null )
			storePictureNewImage( f, checksumLong.longValue() );
	}


	
	/**
	 *   deletes all entries in the new Image.
	 */
	public void zapNewImage() {
		newImage.clear();
	}

	/**
	 *   deletes all entries in the new Image.
	 */
	public void zapOldImage() {
		oldImage.clear();
	}

	/**
	 *   builds old image from the files on the camera.
	 */
	public void buildOldImage() {
			File rootDir = new File( this.rootDir );
			if ( ! rootDir.isDirectory() ) {
				Tools.log("Camera.buildOldImage was attempted on a non directory: " + this.rootDir );
				return;
			}
			int countFiles = Tools.countfiles( rootDir.listFiles() );
			if ( countFiles < 1 ) {
				Tools.log("Camera.buildOldImage was attempted for no files" );
				return;
			}
			ProgressGui progGui = new ProgressGui( countFiles, 
				Settings.jpoResources.getString( "countingChecksum"), 
				Settings.jpoResources.getString( "countingChecksumComplete" ) );
			zapOldImage();
			
			buildOldImage( rootDir.listFiles(), progGui );
			
	}

	
	/**
	 *   this method recursively gos through the directories to identify the checksums 
	 *   of the pictures in the camera
	 */
	protected void buildOldImage( File[] files, ProgressGui progGui ) {
			for ( int i = 0; (i < files.length) && ( ! progGui.interrupt ); i++ ) {
			File f = files[i];
			if ( ! f.isDirectory() ) {
				long checksum = Tools.calculateChecksum( f );
				storePicture( oldImage, f, checksum );
				progGui.progressIncrement();
			} else {
				if ( Tools.hasPictures( f ) ) {
					buildOldImage( f.listFiles(), progGui );
				}
			}
		}
	}

	
	/**
	 *  copies the entries in the newImage to the oldImage and zaps the new image.
	 */
	public void storeNewImage() {
		oldImage.putAll( newImage );
	}

	/**
	 *  counts the number of pictures for which a checksum is held in the HashMap
	 */
	public String getOldIndexCountAsString() {
		return Integer.toString( oldImage.size() );
	}

	/**
	 *  Method that executes the connect script for the camera if present.
	 *  @return  true if successful, false if not.
	 */
	public boolean runConnectScript() {
		Tools.log("Camera.runConnectScript: Trying to run: " + connectScript);
		try {	
			Process p;
			if ( ! connectScript.equals( "" ) ) {
				p = Runtime.getRuntime().exec( connectScript );
			}
			return true;
		} catch ( IOException x ) {
			Tools.log("Camera.runConnectScript could not run connect Script due to an IOException: " + x.getMessage() );
			return false;
		}
	}
	
	
	/**
	 *  Method that executes the disconnect script for the camera if present.
	 *  @return  true if successful, false if not.
	 */
	public boolean runDisconnectScript() {
		Tools.log("Camera.runDisconnectScript: Trying to run: " + disconnectScript);
		try {	
			Process p;
			if ( ! disconnectScript.equals( "" ) ) {
				p = Runtime.getRuntime().exec( disconnectScript );
			}
			return true;
		} catch ( IOException x ) {
			Tools.log("Camera.runDisonnectScript could not run connect Script due to an IOException: " + x.getMessage() );
			return false;
		}
	}


}
