package jpo;

import java.io.*;
import javax.swing.*;
import javax.swing.tree.*;

/*
XmlReaderThread.java:  class that reads an XML stream

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
 *  This class was invented to spawn a new thread and to the Xml parsing there.
 */
public class XmlReaderThread extends Thread {


	/**
	 *  temporary storage for the file to be parsed
	 */
	private InputStream inputStream;

	
	/**
	 *  temporary storage for the root node where the entries are to be added
	 */
	private SortableDefaultMutableTreeNode rootNode;
	

	/**
	 *  temporary storage for the tree to expand afterwards
	 */	
	private JTree jTree;


	/**
	 *  This thread type class fires off an XmlReader in a thread.
	 *
	 *  @param  inputStream    	The Xml stream to be parsed
	 *  @param  rootNode		The root node where the entries are to be added
	 *  @param  jTree		The JTree that needs to be expanded afterwards
	 *  @see  XmlReader
	 */
	XmlReaderThread (InputStream inputStream, SortableDefaultMutableTreeNode rootNode, JTree jTree) {
		this.inputStream = inputStream;
		this.rootNode = rootNode;
		this.jTree = jTree;
		start();
	}
	
	
	/**
	 *   this method is invoked as a new thread by the JVM
	 */
	public void run () {
		new XmlReader ( inputStream, rootNode );
		TreePath rootTreePath = new TreePath( rootNode.getPath() );
		jTree.expandPath( rootTreePath );
		jTree.scrollPathToVisible( rootTreePath );
		jTree.setSelectionPath ( rootTreePath );

	}
}

