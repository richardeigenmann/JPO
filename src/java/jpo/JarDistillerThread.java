package jpo;

import java.io.*;
import java.net.*;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.util.*;
import javax.swing.*;
import java.text.*;
import java.lang.Math.*;
import java.util.jar.*;

/*
JarDistillerThread.java:  class that writes pictures to a jar file

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
 *  a class that exports a tree of chapters to an XML file
 */

public class JarDistillerThread extends Thread {



	/**
	 *  variable to hold the name of the output file which is passed as a parameter
	 */
	private File outputFile;

	/**
	 *   the Jar stream we shall write all the output to
	 */

	private JarOutputStream jarOut;
	
	
	/**
	 *  static size of the buffer to be used in copy operations
	 */
	private static final int BUFFER = 2048;


	/**
	 *  the buffer for read / write operations
	 */
	private byte data[] = new byte[BUFFER];;



	/**
	 *  temporary variable to hold the group information from the user object of the node
	 */
	private GroupInfo g;


	/**
	 *  temporary variable to hold the picture information from the user object of the node
	 */
	private PictureInfo p;

	/**
	 *  temporary node used in the Enumeration of the kids of the Group
	 */
	private SortableDefaultMutableTreeNode n;


	
	
	/** 
	 *  highres picture directory if pictures need to be copied
	 */
	private File highresTargetDir;

	
	/**
	 *  lowres picture directory if pictures need to be copied
	 */
	private File lowresTargetDir;
	
	
	/**
	 *  the node to start from which is passed as a parameter
	 */
	private SortableDefaultMutableTreeNode startNode;


	
	/**  
	 *   temporary variable used to calculate the spot before the file extension.
	 *   Scope to class to avoid re-creating the object over and over.
	 */
	private int dotPoint;


	/**
	 *   progress monitor that keeps users informed what is going on and that allows 
	 *   interruption of the thread.
	 */
	private ProgressMonitor progressMonitor;


	/**
	 *  variable to count how many pictures have been processed
	 */
	private int progressCounter = 0;
	 


	/**
	 *   Hashtable that contains the filenames used
	 */
	private Hashtable filenameHashtable = new Hashtable();
	

	/**
	 *   holds the whole xmlfile as it is being built
	 */
	private StringBuffer xmlData = new StringBuffer( 50000 );

	

	/**
	 *  Write all the nodes indicated by the startNode to the jar file indicated in the
	 *  parameter outputFile. Adds the class files of the JPO app. Adds a Manifest
	 *  that allows the jar to be started and includes an xml file called 
	 *  autostartJarPicturelist.xml which when present in the classpath is automatically 
	 *  started.
	 *
	 *  @param outputFile    	The name of the file that is to be created
	 *  @param startNode		The node from which this is all to be built.
	 */
	public JarDistillerThread (File outputFile, SortableDefaultMutableTreeNode startNode) {
		this.outputFile = outputFile;
		this.startNode = startNode;
		
		start();
	}

	
	
	/**
	 *  method that is invoked by the thread to do things asynchroneousely
	 */
	public void run() {
		try {
			// create the progress monitor
			progressMonitor = 
				new ProgressMonitor (
					Settings.anchorFrame, 
					"Creating Jar File",
					"Description",
					0, Tools.countPictures( startNode ) );
			progressMonitor.setProgress(0);
			progressMonitor.setMillisToPopup( 0 );
			progressMonitor.setMillisToDecideToPopup( 0 );


			// create jar			
			FileOutputStream dest = new FileOutputStream( outputFile );
			jarOut = new JarOutputStream( new BufferedOutputStream( dest ) ); 
			

			// header			
			writeString(jarOut, "<?xml version='1.0' encoding='ISO-8859-1'?>\n");
			writeString(jarOut, "<!DOCTYPE collection SYSTEM \"" + Settings.COLLECTION_DTD + "\">\n");

		
			enumerateGroup( startNode );

			
			//data = new byte[BUFFER];
			//copyGroupPictures( startNode );
			
			
			// create xml
			progressMonitor.setNote("Generating autostartJarPicturelist.xml");
			jarOut.putNextEntry( new JarEntry( "autostartJarPicturelist.xml" ) );
			data = xmlData.toString().getBytes();
			jarOut.write( data, 0, data.length );



			// copy the class files.			
			progressMonitor.setNote( "Writing class files" );
			//File codeJarFile = Tools.searchClasspath ( "jpo/jpo.jar" );
				

			URL codeJarFile = Settings.cl.getResource( "jpo/Jpo.class" );
			if (codeJarFile != null ) {
				String codeJarFile2 = codeJarFile.toString().substring(0, codeJarFile.toString().indexOf("!") +2);

				Tools.log("JarDistillerThread.run: URL of codeJarFile reads: " + codeJarFile.toString());
				Tools.log("String reads: " + codeJarFile2);
				copyJarToJar ( codeJarFile2, jarOut );
			} else
				Tools.log("Could not find jar file for copy of program code.");


			// write the manifest
/*			progressMonitor.setNote( "Writing Manifest" );
			jarOut.putNextEntry( new JarEntry( "META-INF/MANIFEST.MF" ) );
			//writeString(jarOut, "Main-Class: jpo.Jpo\n");
			data = "Main-Class: jpo.Jpo\n".getBytes();
			jarOut.write( data, 0, data.length );
*/
			
			jarOut.close();
			if ( progressMonitor.isCanceled() )
				outputFile.delete();
			progressMonitor.close();
		

		} catch (SecurityException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog( Settings.anchorFrame, "Could not create directories for pictures ", 
						"SecurityException",
						JOptionPane.ERROR_MESSAGE);
		} catch (IOException x) {
			x.printStackTrace();
			JOptionPane.showMessageDialog( Settings.anchorFrame, "Could not create a file called " 
						+ outputFile + "\n" + x, 
						"IOExeption",
						JOptionPane.ERROR_MESSAGE);
		}
	}
	


	/** 
	 *  recursively invoked method to report all groups.
	 */
	private void enumerateGroup (SortableDefaultMutableTreeNode groupNode) throws IOException {
		g = (GroupInfo) groupNode.getUserObject();
		if (groupNode == startNode) 
			writeString(jarOut, "<collection collection_name=\"" + g.getGroupName() 
				+ "\" collection_created=\"" + DateFormat.getDateInstance().format(Calendar.getInstance().getTime()) + "\">");
		else 
			writeString(jarOut, "<group group_name=\"" + Tools.escapeXML(g.getGroupName()) + "\">");
		writeString(jarOut, "\n");
		

		Enumeration kids = groupNode.children();
		while (kids.hasMoreElements()) {
			n = (SortableDefaultMutableTreeNode) kids.nextElement();
			if (n.getUserObject() instanceof GroupInfo) {
				enumerateGroup(n);
			} else {
				writePicture(n);
			}
		}
		
		if (groupNode == startNode) 
			writeString(jarOut, "</collection>");
		else
			writeString(jarOut, "</group>");
		writeString(jarOut, "\n");
	}	




	/**
	 *  method that validates that the filename has not been used before. If it has
	 *  it adds a suffix to the filename to make it unique.
	 */
	private String preventDuplicateFilename( String targetFileName ) {
		//System.out.println("\n----\nTesting: " + targetFileName);
		
		if ( filenameHashtable.containsKey( targetFileName ) ) {
			//System.out.println("File exists in hashtable");
			String ext = Tools.getExtension( targetFileName );
			String rootName = Tools.getFilenameRoot( targetFileName );
			for (int i = 1; i < 10000; i++) {
				String testName = rootName + "_" + Integer.toString(i) + "." + ext;
				//System.out.println("Testing name: " + testName );
				if ( ! filenameHashtable.containsKey( testName ) ) {
					targetFileName = testName;
					break;
				}
			}
		}
		filenameHashtable.put( targetFileName,  new Boolean(true) );
		return targetFileName;
	}



	/**
	 *  convenience method to write a string to a JarOutputStream
	 */
	private void writeString( JarOutputStream jarOut, String s ) throws IOException {
		//data = s.getBytes();
		//jarOut.write( data, 0, data.length );
		xmlData.append( s );
	}




	 
	/** 
	 *  write the tags for a picture to the xml file.
	 */
	private void writePicture (SortableDefaultMutableTreeNode n) throws IOException {
		PictureInfo p = (PictureInfo) n.getUserObject();
		String highresFilename = new File(p.getHighresURL().getFile()).getName();
		String lowresFilename = new File(p.getLowresURL().getFile()).getName();
		highresFilename = "Highres/" + highresFilename;
		lowresFilename = "Lowres/" + lowresFilename;
		highresFilename = preventDuplicateFilename( highresFilename );
		lowresFilename = preventDuplicateFilename( lowresFilename );
				
		progressMonitor.setNote( highresFilename );
		copyFileToJar( 	p.getHighresURL(), highresFilename, jarOut );
		copyFileToJar( 	p.getLowresURL(), lowresFilename, jarOut );
				
		progressCounter++;
		progressMonitor.setProgress( progressCounter );

		writeString(jarOut, "<picture>\n");
		writeString(jarOut, "<description><![CDATA[" + p.getDescription() + "]]></description>\n");
		writeString(jarOut, "<file_URL>jar:!/" + highresFilename + "</file_URL>\n");
		writeString(jarOut, "<file_lowres_URL>jar:!/" + lowresFilename + "</file_lowres_URL>\n");
		writeString(jarOut, "</picture>\n");
	}








	/**
	 *  method to add the contents of the indicated source jar file into the target jar file
	 */
	public void copyJarToJar ( String sourceFile, JarOutputStream jarOut) {
		try {

			//FileInputStream in  = new FileInputStream( sourceFile );
			URL u = new URL( sourceFile );
			JarURLConnection juc = (JarURLConnection) u.openConnection();
			JarFile jf = juc.getJarFile();
			
			//BufferedInputStream bin = new BufferedInputStream( juc.getInputStream() );
			//FileInputStream in  = new FileInputStream( "F:/Documents and Settings/Richi/.javaws/cache/http/Dj-po.sourceforge.net/P80/DMjpo/RMjpo.jar");
			//BufferedInputStream bin = new BufferedInputStream( in );
			//JarInputStream jarIn = new JarInputStream( bin );
			//JarInputStream jarIn = new JarInputStream( juc.getInputStream(), true );

			
			byte data[] = new byte[BUFFER];;
			JarEntry sourceEntry;
			InputStream in;
			//while ( ( sourceEntry = jarIn.getNextJarEntry() ) != null ) {
			for ( Enumeration e = jf.entries(); e.hasMoreElements(); ) {
				sourceEntry = (JarEntry) e.nextElement();
				JarEntry outputEntry = new JarEntry( sourceEntry.getName());
				jarOut.putNextEntry( outputEntry );
				
				int count;
				in = jf.getInputStream( sourceEntry );
				while (( count = in.read( data, 0, BUFFER )) != -1)
					jarOut.write(data, 0, count);
				in.close();
			}
			
			//jarIn.close();
		} catch (IOException e) {
			Tools.log( "Could not copy " + sourceFile );
			Tools.log( "Reason: " + e );
			e.printStackTrace();
		} catch (Exception e) {
			Tools.log( "Could not copy " + sourceFile );
			Tools.log( "Reason: " + e );
			e.printStackTrace();
		}
	}	



	/**
	 *  method to copy any file from a source location to a target location
	 */
	public void copyFileToJar (URL a, String targetFileName, JarOutputStream jarOut) {
		//System.out.println("URL: " + a);
		//System.out.println("targetFileName: " + targetFileName);
		//System.out.println("jarOut: " + jarOut);
		try {
			InputStream in  = a.openStream();
			BufferedInputStream bin = new BufferedInputStream(in);

			JarEntry entry = new JarEntry( targetFileName );
			jarOut.putNextEntry( entry );

			int count;
			byte data[] = new byte[BUFFER];;
			while (( count = bin.read( data, 0, BUFFER )) != -1)
				jarOut.write(data, 0, count);
			
			in.close();
		} catch (IOException e) {
			System.err.println( "Could not copy " + a );
			System.err.println( "Reason: " + e );
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println( "Could not copy " + a );
			System.err.println( "Reason: " + e );
			e.printStackTrace();
		}
	}	



	
}


