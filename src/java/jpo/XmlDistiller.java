package jpo;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.text.*;

/*
XmlDistiller.java:  class that writes the xml file

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

public class XmlDistiller extends Thread {


	/**
	 *  output file handle
	 */
	private BufferedWriter out;


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
	 *  variable to hold the name of the output file
	 */
	private File outputFile;
	
	
	/** 
	 *  highres picture directory if pictures need to be copied
	 */
	private File highresTargetDir;

	
	/**
	 *  lowres picture directory if pictures need to be copied
	 */
	private File lowresTargetDir;
	
	
	/**
	 *  the node to start from
	 */
	private SortableDefaultMutableTreeNode startNode;



	/**
	 *  temporary variable that indicates that the picutres should be copied too.
	 */
	private boolean copyPics;

	/**
	 *  @param outputFile    	The name of the file that is to be created
	 *  @param startNode		The node from which this is all to be built.
	 *  @param copyPics		Flag which instructs pictures to be copied too
	 *  @param runAsThread		Flag which can instruct this job not to run as a thread.
	 */
	XmlDistiller (File outputFile, SortableDefaultMutableTreeNode startNode, boolean copyPics, boolean runAsThread) {
		this.outputFile = outputFile;
		this.startNode = startNode;
		this.copyPics = copyPics;
		
		if ( runAsThread )
			start();
		else
			run();
	}

	
	
	/**current
	 *  method that is invoked by the thread to do things asynchroneousely
	 */
	public void run() {
		try {
			if ( copyPics ) {
				highresTargetDir  = new File(outputFile.getParentFile() , "Highres" );
				lowresTargetDir = new File(outputFile.getParentFile() , "Lowres" );
		
				highresTargetDir.mkdirs();
				lowresTargetDir.mkdirs();
			}

			FileWriter fw = new FileWriter( outputFile );
			out = new BufferedWriter( fw );			

			// header			
			out.write("<?xml version='1.0' encoding='" + fw.getEncoding() + "'?>");
			out.newLine();
			out.write("<!DOCTYPE collection SYSTEM \"" + Settings.COLLECTION_DTD + "\">");
			out.newLine();
			
			enumerateGroup( startNode );

			// categories
			out.write("<categories>");
			out.newLine();

			Iterator i = startNode.getCategoryIterator();
			Integer key;
			String category;
			while ( i.hasNext() ) {
				key = (Integer) i.next();
				category = (String) startNode.getCategory( key );
				out.write( "\t<category index=\"" + key.toString() + "\">");
				out.newLine();
				out.write( "\t\t<categoryDescription><![CDATA[" + category + "]]></categoryDescription>" );
				out.newLine();
				out.write( "\t</category>");
				out.newLine();
			}

			out.write("</categories>");
			out.newLine();

			out.write("</collection>");
			out.newLine();

			out.close();
			
			writeCollectionDTD( outputFile.getParentFile() );

			

		} catch ( SecurityException x ) {
			//e.printStackTrace();
			Tools.log( "XmlDistiller.run: SecurityException: " + x.getMessage() );
			JOptionPane.showMessageDialog(null, x.getMessage(), 
						"XmlDistiller: SecurityException",
						JOptionPane.ERROR_MESSAGE);
		} catch ( IOException x ) {
			//x.printStackTrace();
			Tools.log( "XmlDistiller.run: IOException: " + x.getMessage() );
			JOptionPane.showMessageDialog(null, x.getMessage(), 
						"XmlDistiller: IOExeption",
						JOptionPane.ERROR_MESSAGE);
		}
	}
	


	/** 
	 *  recursively invoked method to report all groups.
	 */
	private void enumerateGroup (SortableDefaultMutableTreeNode groupNode) throws IOException {
		g = (GroupInfo) groupNode.getUserObject();

		if ( copyPics ) {
			File targetLowresFile = Tools.inventPicFilename( lowresTargetDir, p.getLowresFilename() );
			Tools.copyPicture( g.getLowresURL (), targetLowresFile );
			g.dumpToXml ( out, targetLowresFile.toURI().toURL().toString(), groupNode == startNode, groupNode.getAllowEdits() );
		} else {
			g.dumpToXml ( out, groupNode == startNode, groupNode.getAllowEdits() );
		}
/*		if (groupNode == startNode) 
			out.write("<collection collection_name=\"" 
				+ Tools.escapeXML( g.getGroupName() )
				+ "\" collection_created=\"" 
				+ DateFormat.getDateInstance().format(Calendar.getInstance().getTime()) 
				+ "\""
				+ ( groupNode.getAllowEdits() ? " collection_protected=\"No\"" : " collection_protected=\"Yes\"" )
				+ ">");
		else 
			out.write("<group group_name=\"" + Tools.escapeXML( g.getGroupName() ) + "\">");
		out.newLine();*/
		

		Enumeration kids = groupNode.children();
		while (kids.hasMoreElements()) {
			n = (SortableDefaultMutableTreeNode) kids.nextElement();
			if (n.getUserObject() instanceof GroupInfo) {
				enumerateGroup(n);
			} else {
				writePicture(n);
			}
		}
		
		g.endGroupXML( out, groupNode == startNode );
/*		if (groupNode != startNode) 
			out.write("</group>");
		//else
			//out.write("</collection>");
		out.newLine();*/
	}	


	 
	/** 
	 *  write a picture to the output
	 */
	private void writePicture (SortableDefaultMutableTreeNode n) throws IOException {
		p = (PictureInfo) n.getUserObject();
		
		if ( copyPics ) {
			File targetHighresFile = Tools.inventPicFilename( highresTargetDir, p.getHighresFilename() );
			File targetLowresFile = Tools.inventPicFilename( lowresTargetDir, p.getLowresFilename() );
			Tools.copyPicture( p.getHighresURL (), targetHighresFile ); 
			Tools.copyPicture( p.getLowresURL (), targetLowresFile );
			p.dumpToXml( out, 
				targetHighresFile.toURI().toURL().toString(), 
				targetLowresFile.toURI().toURL().toString() );
		} else {
			p.dumpToXml( out );
		}
	}


	
	
	/**
	 *  writes the collection.dtd file to the target directory.
	 *  This file war written manually and added to the jar.
	 */
	
	public void writeCollectionDTD ( File directory ) {
		try {
			ClassLoader cl = this.getClass().getClassLoader();
			//InputStream in  = ClassLoader.getSystemResource( "jpo/collection.dtd" ).openStream();
			InputStream in  = cl.getResource( "jpo/collection.dtd" ).openStream();
			FileOutputStream out  = new FileOutputStream(new File(directory, "collection.dtd"));
			
			BufferedInputStream bin = new BufferedInputStream(in);
			BufferedOutputStream bout = new BufferedOutputStream(out);
			
			int c;
		
			while (( c = bin.read()) != -1)
				out.write(c);
			
			in.close();
			out.close();
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
				Settings.anchorFrame, 
				Settings.jpoResources.getString("DtdCopyError") + e.getMessage(), 
				Settings.jpoResources.getString("genericWarning"), 
				JOptionPane.ERROR_MESSAGE);

		}
	}
	
	
	
}


