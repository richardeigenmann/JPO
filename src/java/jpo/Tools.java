package jpo;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.dnd.*;
import java.util.zip.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.text.*;

import javax.swing.border.*;
import javax.swing.WindowConstants.*;
import java.awt.event.*;
import java.awt.*;


/*
Tools.java:  utilities for the JPO application

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
 *   sepearate class to hold a collection of static methods that are frequently needed.
 **/
public class Tools {

	/**
	 *   method that converts any XML problem characters (&, <, >, ", ') to the 
	 *   predefined codes.
	 */
	public static String escapeXML(String s) {
		s = s.replaceAll("&", "&amp;");
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");
		s = s.replaceAll("\"", "&quot;");
		s = s.replaceAll("'", "&apos;");			
		return s;
	}
	


	/**
	 *   returns the file extension of the indicated url
	 *   @param url   The URL object for which the extension is being requested
	 */
	public static String getExtension(URL url) {
		return getExtension ( url.toString() );
	}

	
	/**
	 *   return the file extension of a file.
	 *   @param file   The File object for which the extension is being requested
	 */
	public static String getExtension(File file) {
		return getExtension ( file.getName() );
	}



	/**
	 *   return the file extension of a string
	 *   @param s   The string for which the extension is being requested
	 */
	public static String getExtension(String s) {
		String ext = null;
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1) {
			ext = s.substring(i+1);
		}
		return ext;
	}


	/**
	 *   return everything of the filename up to the extension.
	 *   @param s   The string for which the root of the filename is being requested
	 */
	public static String getFilenameRoot(String s) {
		String fnroot = null;
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1) {
			fnroot = s.substring(0, i);
		}
		return fnroot;
	}




	
	/**
	 *   method that tests the extension of the string in a JTextField for being 
	 *   the correct extension. If not the correct extension is added. The case
	 *   of the extension is ignored.
	 */
	public static void setFilenameExtension (String extension, JTextField jTextField) {
		if ( ! jTextField.getText().toUpperCase().endsWith( extension.toUpperCase() ) ) {
			jTextField.setText( jTextField.getText() + "." + extension );
		}
	}


	/**
	 *   method that tests the file extension of a File object for being 
	 *   the correct extension. Either the same file object is returned or a 
	 *   new one is created with the correct extension.
	 *   If not the correct extension is added. The case
	 *   of the extension is ignored.
	 */
	public static File correctFilenameExtension (String extension, File testFile) {
		if ( ! testFile.getName().toUpperCase().endsWith( extension.toUpperCase() ) ) {
			return new File( testFile.getPath() + "." + extension );
		}
		return testFile;
	}



	/**
	 *  Counts the number of real files in the array of files.
	 *  @return  the number of real files in the array of files
	 */
	public static int countfiles( File[] fileArray ) {
		if ( fileArray == null )
			return 0;

		int numFiles = 0;
		for ( int i = 0; i < fileArray.length; i++ ) {
			File fileEntry = fileArray[i];
			try {
				if ( ! fileEntry.isDirectory() ) {
					numFiles++;
				} else {
					numFiles += countfiles( fileEntry.listFiles() );
				}
			} catch ( SecurityException x ) {
				// Log the error and ignore it and continue
				Tools.log( "Tools.countfiles: got a SecurityException on file: " + fileEntry.toString() + " \n" + x.getMessage() );
			} 
		}

		return numFiles;
	}




	/**
	 *  This method checks whether the JVM has an image reader for the supplied
	 *  File.
	 *  @param  testFile	The file to be checked
	 *  @return  true if the JVM has a reader false if not.
	 */
	public static boolean jvmHasReader( File testFile ) {
		try {
			FileImageInputStream testStream =  new FileImageInputStream( testFile );
			boolean hasReader = ImageIO.getImageReaders( testStream ).hasNext();
			testStream.close();
			return  hasReader;
		} catch (MalformedURLException x) {
			Tools.log("Tools.jvmHasReader.MalformedURLException: " + testFile.getPath() + "\nError: " + x.getMessage());
			return false;
		} catch (FileNotFoundException x) {
			Tools.log("Tools.jvmHasReader.File not found: " + testFile.getPath() + "\nError: " + x.getMessage());
			return false;
		} catch (IOException x) {
			Tools.log("Tools.jvmHasReader.IO Exception on: " + testFile.getPath() + "\nError: " + x.getMessage());
			return false;
		}
 	}


	/**
	 *  This method looks into the supplied subdirectory and tries to see if there is 
	 *  at least one picture in it for which our Java Environment has a decoder.
	 *
	 *  @param  subDirectory	The File representing the subdirectory to be recursively searched
	 *  @return true if there is at leas one picture in the subdirectory, false if there is nothing.
	 */
	public static boolean hasPictures( File subDirectory ) {
		File[] fileArray = subDirectory.listFiles();
		//Tools.log( "Tools.hasPictures: directory " + subDirectory.toString() + " has " + Integer.toString(fileArray.length) + " entries" );
		if ( fileArray == null )
			return false;
			
		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].isDirectory()) {
				if ( hasPictures( fileArray[i] ) )
					return true;
			} else {
				//Tools.log( "PictureAdder.hasPictures: checking: " + fileArray[i].toString() );
				if ( Tools.jvmHasReader ( fileArray[i] ) ) {
					return true;
				}  
			}	
		}
		return false;
	}
	


	/**
	 *  method to copy any file from a source location to a target location
	 */
	public static long copyPicture ( URL a, URL b ) {
		try {
			InputStream in  = a.openStream();
			OutputStream out  = b.openConnection().getOutputStream();
			
			BufferedInputStream bin = new BufferedInputStream(in);
			BufferedOutputStream bout = new BufferedOutputStream(out);
			
			long crc = copyBufferedStream ( bin, bout );
			
			return crc;
			
		} catch ( IOException e ) {
			JOptionPane.showMessageDialog(
				Settings.anchorFrame, 
				Settings.jpoResources.getString("copyPictureError1")
					+ a.toString() 
					+ Settings.jpoResources.getString("copyPictureError2") 
					+ b.toString() 
					+ Settings.jpoResources.getString("copyPictureError3") 
					+ e.getMessage() , 
				Settings.jpoResources.getString("genericError"), 
				JOptionPane.ERROR_MESSAGE);
			return Long.MIN_VALUE;
		}
	}	


	/**
	 *  method to copy any file from a source location to a target File location. Works 
	 *  better because files are writable whilst most URL are read only.
	 */
	public static long copyPicture ( URL a, File b ) {
		try {
			InputStream in  = a.openStream();
			OutputStream out  = new FileOutputStream( b );
			
			BufferedInputStream bin = new BufferedInputStream(in);
			BufferedOutputStream bout = new BufferedOutputStream(out);
			
			long crc = copyBufferedStream ( bin, bout );
			
			return crc;
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
				Settings.anchorFrame, 
				Settings.jpoResources.getString("copyPictureError1")
					+ a.toString() 
					+ Settings.jpoResources.getString("copyPictureError2") 
					+ b.toString() 
					+ Settings.jpoResources.getString("copyPictureError3") 
					+ e.getMessage() , 
				Settings.jpoResources.getString("genericError"), 
				JOptionPane.ERROR_MESSAGE);
			return Long.MIN_VALUE;
		}
	}	



	/** 
	 *  method to copy any file from a source File to a target File location.
	 */
	public static long copyPicture ( File a, File b ){
		try {
			InputStream in  = new FileInputStream( a );
			OutputStream out  = new FileOutputStream( b );
			
			BufferedInputStream bin = new BufferedInputStream( in );
			BufferedOutputStream bout = new BufferedOutputStream( out );
			
			long crc = copyBufferedStream ( bin, bout );
			
			return crc;
			
		} catch ( IOException e ) {
			JOptionPane.showMessageDialog(
				Settings.anchorFrame, 
				Settings.jpoResources.getString("copyPictureError1")
					+ a.toString() 
					+ Settings.jpoResources.getString("copyPictureError2") 
					+ b.toString() 
					+ Settings.jpoResources.getString("copyPictureError3") 
					+ e.getMessage() , 
				Settings.jpoResources.getString("genericError"), 
				JOptionPane.ERROR_MESSAGE);
			return Long.MIN_VALUE;
		}
	}	
			



	/**
	 *  method to copy any file from a source stream to a output stream
	 *  @return  the crc of the file
	 */
	public static long copyBufferedStream ( BufferedInputStream bin, BufferedOutputStream bout ) 
		throws IOException {
		
		Adler32 crc = new Adler32();
		int c;
		
		while (( c = bin.read()) != -1) {
			bout.write(c);
			crc.update( c );
		}
			
		bin.close();
		bout.close();
		return crc.getValue();

	}





	/**
	 *  method to move any file from a source location to a target location.
	 *  It checks whether the file was mentioned in the highres or lowres fields of other nodes
	 *  and corrects those references if found.
	 */
	public static boolean movePicture (URL a, URL b) throws IOException {

		File sourceFile = new File ( a.getFile() );
		File targetFile = new File ( b.getFile() );
		
		if ( targetFile.exists() ) throw new IOException ("Target File " + b.toString() + " already exists. Move aborted!");
		
		if ( sourceFile.renameTo ( targetFile )) {
			Tools.log ( "Picture " + a.toString() + " moved to " + b.toString() );		
		} else {
			// perhaps target was on a different filesystem. Trying copying
			Tools.log ( "Rename from " + a.toString() + " to " + b.toString() + " failed. Trying copying" );		
			if ( copyPicture(a, targetFile) > Long.MIN_VALUE ) {
				Tools.log ( "Copy worked. Deleting source file." );
				sourceFile.delete();
			} else {
				Tools.log ( "Copy failed too." );
				return false;
			}
		}
		
		
		//  search for other picture nodes in the tree using this image file
		SortableDefaultMutableTreeNode node;
		Object nodeObject;
		Enumeration enum = Settings.top.preorderEnumeration();
		while ( enum.hasMoreElements() ) {
			node = (SortableDefaultMutableTreeNode) enum.nextElement();
			nodeObject = node.getUserObject();
			if  (nodeObject instanceof PictureInfo) {
				//Tools.log( "Tools.movePicture: checking: " + ( (PictureInfo) nodeObject ).getHighresLocation() + " against " + sourceFile );
				if ( ((PictureInfo) nodeObject ).getHighresFile().equals( sourceFile ) )   {
					//Tools.log ( "Another picture node was using the same highres URL. Node changed: " + ( (PictureInfo) nodeObject ).getDescription() );
					((PictureInfo) nodeObject ).setHighresLocation( targetFile.toURI().toURL() );
				}
				if ( ((PictureInfo) nodeObject ).getLowresFile().equals( sourceFile ) ) {
					//Tools.log ( "Another picture node was using the same lowres URL. Node changed: " + ( (PictureInfo) nodeObject ).getDescription() );
					((PictureInfo) nodeObject ).setLowresLocation( targetFile.toURI().toURL() );
				}
			}
		}
		return true;
	}	


	/**
	 *   count the number of pictures in a subtree. Useful for progress monitors.
	 */
	public static int countPictures ( SortableDefaultMutableTreeNode startNode ) {
		return countPictures ( startNode, true );
	}


	
	
	/**
	 *   count the number of pictures in a subtree. Useful for progress monitors.
	 *
	 *   @param startNode	the node from which to count
	 *   @param recurseSubgroups  indicator to say whether the next levels of groups should be counted too or not.
	 */
	public static int countPictures ( SortableDefaultMutableTreeNode startNode, boolean recurseSubgroups ) {
		int count = 0;
		Enumeration nodes = startNode.children();
		while ( nodes.hasMoreElements() ) {
			SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) nodes.nextElement();
			if ( n.getUserObject() instanceof PictureInfo)
				count++;
			if ( recurseSubgroups && ( n.getChildCount() > 0 ) )
				count += countPictures ( n );
		}
		return count;
	}

	
	/**
	 *   sums the filesize of the pictures in a subtree. 
	 */
	public static String sizeOfPictures ( SortableDefaultMutableTreeNode startNode ) {
		long size = sizeOfPicturesLong ( startNode );
		return fileSizeToString( size );
	}


	/**
	 *  Converts a long value into a human readable size such a 245 B, 15 KB, 3 MB, 85 GB, 2 TB
	 */
	public static String fileSizeToString( long size ) {
		String suffix = " B";
		if ( size > 1024 ) {
			size = size / 1024;
			suffix = " KB";
		}	
		if ( size > 1024 ) {
			size = size / 1024;
			suffix = " MB";
		}	
		if ( size > 1024 ) {
			size = size / 1024;
			suffix = " GB";
		}
		if ( size > 1024 ) {
			size = size / 1024;
			suffix = " TB";
		}
		suffix = Long.toString( size ) + suffix;
 		return suffix;
	}

	/**
	 *   sums the filesize of the pictures in a subtree.
	 */
	public static long sizeOfPicturesLong ( SortableDefaultMutableTreeNode startNode ) {
		long size = 0;
		File testfile;
		Enumeration nodes = startNode.children();
		while ( nodes.hasMoreElements() ) {
			SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) nodes.nextElement();
			if ( n.getUserObject() instanceof PictureInfo) {
				testfile = ((PictureInfo) n.getUserObject()).getHighresFile();
				if ( testfile != null ) 
					size += testfile.length();
			}
			if ( n.getChildCount() > 0 )
				size += sizeOfPicturesLong ( n );
		}
 		return size;
	}



	/**
	 *   count the number of nodes in a subtree. 
	 */
	public static int countNodes ( SortableDefaultMutableTreeNode startNode ) {
		int count = 1;
		Enumeration nodes = startNode.children();
		while ( nodes.hasMoreElements() ) {
			SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) nodes.nextElement();
			if ( n.getChildCount() > 0 )
				count += countNodes ( n );
			else 
				count++;
		}
		return count;
	}



	/**
	 *   count the number of groups excluding the starting one
	 */
	public static int countGroups ( SortableDefaultMutableTreeNode startNode ) {
		int count = 0;
		Enumeration nodes = startNode.children();
		while ( nodes.hasMoreElements() ) {
			SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) nodes.nextElement();
			if ( n.getUserObject() instanceof GroupInfo )
				count++;
			if ( n.getChildCount() > 0 )
				count += countGroups ( n );
		}
		return count;
	}



	/**
	 *  searches for an item in the classpath that ends exactly like the supplied string.
	 *  Returns a new file object for that item or null if not found.
	 *  Invented to find the code classes of the 
	 *  JPO app which sit in jpo.jar file
	 */
	public static File searchClasspath ( String searchName ) {
		// find the jar file as last item in the Jar file.
		String classpath = System.getProperty("java.class.path");
		Tools.log("Tools.searchClasspath: searching for " + searchName + " in " + classpath);
		String testToken;
		StringTokenizer st = new StringTokenizer( classpath, ":");
		while ( st.hasMoreTokens() ) {
			testToken = st.nextToken();
			if ( testToken.endsWith( searchName ) )
				return new File( testToken );
		}
		return null;
	}



	/**
	 *  writer that get's the debug output if there is any to be written
	 */
	public static BufferedWriter logfile;


	
	/**
	 *  writes a message to the logfile. There are several things that can go wrong here:
	 *  The logfile path can be totally messed up i.e. not a valid filename. It could be
	 *  non writable. In this case the error is reported and the log is shown on the screen.
	 */
	public static synchronized void log ( String message ) {
		if ( Settings.writeLog ) {
			try {
				if (( logfile == null ) && ( Settings.writeLog ) )
					logfile = new BufferedWriter( new FileWriter( Settings.logfile, true) );	
				logfile.write( message );
				logfile.newLine();
				logfile.flush();
			} catch ( IOException x ) {
				System.err.println( message );
			}
		}
	}


	


	/**
	 *  proper way to close the logfile
	 */
	public static void closeLogfile () {
		try {
			if ( logfile != null ) {
				logfile.close();
				logfile = null;
			}
		} catch ( IOException x ) {
			// could not close the logfile; so what?
		}
	}



	
	/**
	 *   method that returns a file handle for a picture that does not exist in the target 
	 *   directory. It tries the combination of path and name first and then tries to 
	 *   suffix _0 _1 _2 etc to the name. If that fails it combines random characters and
	 *   then fails, returning null.
	 */
	public static URL inventPicURL ( File targetDir, String startName ) {
		try{
			return inventPicFilename( targetDir, startName ).toURI().toURL();
		} catch (MalformedURLException x) {
			return null;
		}
	}



	/**
	 *   method that returns a file handle for a picture that does not exist in the target 
	 *   directory. It tries the combination of path and name first and then tries to 
	 *   suffix _0 _1 _2 etc to the name. If that fails it combines random characters and
	 *   then fails, returning null.
	 */
	public static File inventPicFilename ( File targetDir, String startName ) {
			File testFile = new File (targetDir, startName);
			if ( ! testFile.exists() )
				return testFile;
			
			int dotPoint = startName.lastIndexOf(".");
			String startNameRoot = startName.substring(0, dotPoint);
			String startNameSuffix = startName.substring(dotPoint);
		
				
			for (int i=1; i < 10; i++) {
				testFile = new File (targetDir, startNameRoot + "_" + Integer.toString(i) + startNameSuffix);
				if ( ! testFile.exists() ) 
					return testFile;
			}
		
			String randomName;
				
			return null;
	}



	/**
	 *  method that returns a new lowres URL that has not been used before. 
	 */
	public static String lowresFilename() {
		File testLowresFilename;
		for (int i=1; i < 10000; i++) {
			Settings.thumbnailCounter++;
			if ( (Settings.thumbnailCounter % 10) == 0 )
				Tools.log ("considering " + Settings.thumbnailCounter + " filenames for lowres filename");
			testLowresFilename = new File ( Settings.thumbnailPath, Settings.thumbnailPrefix  + Integer.toString(Settings.thumbnailCounter) + ".jpg");
			if (! testLowresFilename.exists()) {
				try {
					Tools.log( "Tools.lowresFilename: assigning: " + testLowresFilename.toURI().toURL().toString());
					Settings.unsavedSettingChanges = true;
					return ( testLowresFilename.toURI().toURL().toString() );
				} catch (MalformedURLException x) {
					return (null);
				}
			}
		}
		Tools.log ("lowresFilename: Could not create a lowres filename.");
		return null;
	}




	/**
	 *  method that returns whether a URL is a file:// URL or not.
	 *  Returns true if it is a file, false if it's anything else such as http://
	 */
	public static boolean isUrlFile ( URL testURL ) {
		return ( testURL.getProtocol().equals("file") ); 

	}	
	


	/**
	 *  convenience method to log the amount of free memory
	 **/
	public static int freeMem() {
		int memory = (int) Runtime.getRuntime().freeMemory()/1024/1024;
		Tools.log("Free memory: " + memory + "MB");
		return memory;
	}





	/**
	 *  method that strips out the root filename from a File object. <p>
	 *  Example: c:\directory\geysir.jpg returns geysir
	 */
	public static String stripOutFilenameRoot ( File file ) {
		String description = file.getName();
		int lastDotIndex = description.lastIndexOf( "." );
		if ( lastDotIndex > -1 )
			description = description.substring( 0, lastDotIndex );
		int lastDirectorySeparator = description.lastIndexOf( File.pathSeparator );
		if ( lastDirectorySeparator > -1 )
			description = description.substring( lastDirectorySeparator );
		return description;
	}




	/**
	 *  Method that chooses an xml file or returns null
	 */
	public static File chooseXmlFile () {
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setFileSelectionMode( javax.swing.JFileChooser.FILES_ONLY );
		jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "fileOpenButtonText" ) );
		jFileChooser.setDialogTitle( Settings.jpoResources.getString( "fileOpenHeading" ) );
		jFileChooser.setFileFilter( new XmlFilter() );
		jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );
		
		int returnVal = jFileChooser.showOpenDialog( Settings.anchorFrame );
		if ( returnVal == javax.swing.JFileChooser.APPROVE_OPTION ) 
			return jFileChooser.getSelectedFile();
		else 
			return null;
	}



	/**
	 *  Analyses the drag event and sets the cursor to the appropriate style.
	 *
	 *  @param event the DragSourceDragEvent for which the cursor is to be adjusted
	 */
	public static void setDragCursor( DragSourceDragEvent event ) {
		//Tools.log( "Tools.setDragCursor: invoked");
		DragSourceContext context = event.getDragSourceContext();
		int dndCode = event.getDropAction();
		if( ( dndCode & DnDConstants.ACTION_COPY ) != 0) {
			//Tools.log( "CleverJTree.setDragCursor: figures it is a Copy Event");
			context.setCursor( DragSource.DefaultCopyDrop );	  
		} else if( ( dndCode & DnDConstants.ACTION_MOVE ) != 0 ) {
			//Tools.log( "CleverJTree.setDragCursor: figures it is a Move Event");
			context.setCursor( DragSource.DefaultMoveDrop );	  
		} else {
			//Tools.log( "CleverJTree.setDragCursor: figures it is an Invalid Event: Code: " +Integer.toString(dndCode));
			//Tools.log( "ACTION_COPY is: " + Integer.toString( DnDConstants.ACTION_COPY ) );
			//Tools.log( "ACTION_COPY_OR_MOVE is: " + Integer.toString( DnDConstants.ACTION_COPY_OR_MOVE ) );
			//Tools.log( "ACTION_LINK is: " + Integer.toString( DnDConstants.ACTION_LINK ) );
			//Tools.log( "ACTION_MOVE is: " + Integer.toString( DnDConstants.ACTION_MOVE ) );
			//Tools.log( "ACTION_NONE is: " + Integer.toString( DnDConstants.ACTION_NONE ) );
			//Tools.log( "ACTION_REFERENCE is: " + Integer.toString( DnDConstants.ACTION_REFERENCE ) );
			context.setCursor( DragSource.DefaultMoveNoDrop );	  
		}
	}

	

	/** 
	 *  Calculates a checksum from the supplied File
	 *
	 *  @return  returns the checksum as a Long or Long.MIN_VALUE to indicate failure.
	 */
	public static long calculateChecksum( File f ) {
		long checksum;
		try {
			checksum = calculateChecksum( new BufferedInputStream( new FileInputStream( f ) ) );
		} catch ( FileNotFoundException x ) {
			checksum = Long.MIN_VALUE;
		}
		return checksum;
	}
	
	

	/** 
	 *  Calculates a checksum from the supplied input stream
	 *  originally taken from: Java ist auch eine Insel (2. Aufl.) von Christian Ullenboom Programmieren für die Java 2-Plattform in der Version 1.4
	 *
	 *  @return  returns the checksum as a Long or Long.MIN_VALUE to indicate failure.
	 */
	public static long calculateChecksum( InputStream in ) {
		Adler32 crc = new Adler32();
		int blockLen;

		try {
			while ( (blockLen=(int)in.available()) > 0 ) {
				byte ba[] = new byte[blockLen];
				in.read( ba );
				crc.update( ba );
			}
			return crc.getValue();
		} catch ( IOException x ) {
			Tools.log( "Tools.calculateChecksum trapped an IOException. Aborting. Reason:\n" + x.getMessage() );
			return Long.MIN_VALUE;
		}
	}


	/**
	 *  returns the current date and time formatted per the formatting string. 
	 *  See the API doc on SimpleDateFormat for the meaning of the letters.
	 */
	public static String currentDate( String formatString ) {
		SimpleDateFormat formatter = new SimpleDateFormat( formatString );
		Date currentTime = new Date();
		String dateString = formatter.format( currentTime );
		return dateString;
	}


	/**
	 *  This method tries it's best to parse the supplied date into a Java Date object.
	 *
	 *  @param dateString   the String to be parsed
	 *  @return   the Java Date object or null if it could not be parsed.
	 */
	public static Date parseDate( String dateString ) {
			SimpleDateFormat df = new SimpleDateFormat();
		df.setLenient( true );
		String[] patterns = {"dd.MM.yyyy HH:mm",
			"yyyy:MM:dd HH:mm:ss",
			"dd.MM.yyyy",
			"dd.MM.yy",
			"MM/dd/yy",
			"MM/dd/yyyy",
			"dd MMM yyyy",
			"dd MMM yy"};
		Date d = null;
		boolean notFound = true;
		for ( int i=0; ( i< patterns.length ) && notFound ; i++ ) {
			try {
				df.applyPattern( patterns[i] );
				d = df.parse( dateString );
				notFound = false;
			} catch ( ParseException x ) {
			}
		}
		return d;
	}

	/**
	 *  This method uses some code I got off the web to dtermine the rectangle of the 
	 *  screen size. This is more complicated than it appears on the face of it because
	 *  some users have multiple screens making up a large virtual desktop so the top left corner 
	 *  is not neccesarily (0,0).
	 *
	 *  @return   A rectangle with the coordinates of the desktop.
	 */
	public static Rectangle getScreenDimensions() {
		Rectangle virtualBounds = new Rectangle();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs =  ge.getScreenDevices();
		for (int j = 0; j < gs.length; j++) {
			GraphicsDevice gd = gs[j];
			GraphicsConfiguration[] gc = gd.getConfigurations();
			for (int i=0; i < gc.length; i++) {
				virtualBounds = virtualBounds.union(gc[i].getBounds());
			}
		}
		return virtualBounds;			
	} 




	/**
	 *   This method figures out the dimensions of the supplied JTextArea for it's current content.
	 *   It is not terribly exact.
	 *
	 *   @param   ta   The JTextArea for which you want to know the dimensions
	 *   @param   horizontalWidth   The horizontal width that should be used in the dimension.
	 */
	public static Dimension getJTextAreaDimension( JTextArea ta, int horizontalWidth ) {
		// figure out the size of the JTextArea
		int fontHeight = ta.getFontMetrics(ta.getFont()).getHeight();
		int stringWidth = ta.getFontMetrics(ta.getFont()).stringWidth( ta.getText() );
		int lines = (int) stringWidth / horizontalWidth;
		int adjustedLines =  (int) ( lines * 1.1 ) + 2;
		//Tools.log("ThumbnailDescriptionJPanel: lineCount: " + Integer.toString(lines) + " stringWidth: " + Integer.toString( stringWidth ) );
		return new Dimension( horizontalWidth, adjustedLines * fontHeight );
	}



	/**
	 *  Converts the color into a string for web pages
	 */
	public static String getColor( Color c ) {
		String s = "#";
		s += Integer.toString( c.getRed(), 16);
		s += Integer.toString( c.getGreen(), 16);
		s += Integer.toString( c.getBlue(), 16);
		return s;
	}
	
}
