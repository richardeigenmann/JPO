package jpo;

import java.io.*;
import java.util.*;
import javax.swing.*;




/*
FlatFileDistiller.java:  class that writes the filenames of the pictures to a flat file
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

public class FlatFileDistillerThread extends Thread {


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
	 *  the node to start from
	 */
	SortableDefaultMutableTreeNode startNode;



	/**
	 *  @param outputFile    	The name of the file that is to be created
	 *  @param startNode		The node from which this is all to be built.
	 */
	FlatFileDistillerThread (File outputFile, SortableDefaultMutableTreeNode startNode) {
		this.outputFile = outputFile;
		this.startNode = startNode;
		
		start();
	}

	
	
	/**
	 *  method that is invoked by the thread to do things asynchroneousely
	 */
	public void run() {
		try {
			out = new BufferedWriter(new FileWriter(outputFile));			
			enumerateGroup( startNode );
			out.close();
		

		} catch (SecurityException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Could not create directories for pictures ", 
						"SecurityException",
						JOptionPane.ERROR_MESSAGE);
		} catch (IOException x) {
			x.printStackTrace();
			JOptionPane.showMessageDialog(null, "Could not create a file called " 
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
		Enumeration kids = groupNode.children();
		while (kids.hasMoreElements()) {
			n = (SortableDefaultMutableTreeNode) kids.nextElement();
			if (n.getUserObject() instanceof GroupInfo) {
				enumerateGroup(n);
			} else {
				p = (PictureInfo) n.getUserObject();
				out.write( p.getHighresLocation() );
				out.newLine();
			}
		}
	}	




	
}


