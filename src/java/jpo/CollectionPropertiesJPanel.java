package jpo;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.border.*;

/*
CollectionPropertiesJFrame.java:  
a class that creates a GUI, asks for a directory and then tells you if the files are in your collection.

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
 *  class that presents a GUI and shows the user which files
 *  are in his collection and which are not. It also tells what 
 *  it can find out about the file.
 */
public class CollectionPropertiesJPanel 
	extends JPanel {

	private JLabel collectionItemsLabel = new JLabel ();
	private JLabel collectionGroupsLabel = new JLabel ();
	private JLabel collectionPicturesLabel = new JLabel ();
	private JLabel collectionSizeJLabel = new JLabel ();
	
	/**
	 *  Indicates how many jobs are on the thumbnail creation queue.
	 */
	private JLabel queCountJLabel = new JLabel ();


	/** 
    	 *   Constructor to create a JFrame that displays statistics about the nodes underneath
	 *   the supplied node.
	 */
	public CollectionPropertiesJPanel() {
		setMinimumSize( new Dimension (100,100) );
		setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		
		constraints.gridx = 0; constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(4, 4, 4, 4);
		add( collectionItemsLabel, constraints );
		
		constraints.gridy++;
		add( collectionGroupsLabel, constraints );

		constraints.gridy++;
		add( collectionPicturesLabel, constraints );

		constraints.gridy++;
		add( collectionSizeJLabel, constraints );

		constraints.gridy++;
		add( queCountJLabel, constraints );
	}





	public void updateStats( SortableDefaultMutableTreeNode statisticsNode ) {
		int numberOfPictures = Tools.countPictures( statisticsNode );
		String sizeOfPictures = Tools.sizeOfPictures( statisticsNode );
		int numberOfNodes = Tools.countNodes( statisticsNode );
		int numberOfGroups = Tools.countGroups( statisticsNode );

		collectionItemsLabel.setText( Settings.jpoResources.getString("CollectionNodeCountLabel") + Integer.toString( numberOfNodes ) );
		collectionGroupsLabel.setText( Settings.jpoResources.getString("CollectionGroupCountLabel") + Integer.toString( numberOfGroups ) );
		collectionPicturesLabel.setText( Settings.jpoResources.getString("CollectionPictureCountLabel") + Integer.toString( numberOfPictures ) );
		collectionSizeJLabel.setText( Settings.jpoResources.getString("CollectionSizeJLabel") + sizeOfPictures );
		updateQueueCount();		
	}		
	

	/**
	 *  This method updates the label showing the entries on the queue. This was split out for performance
	 *  as counting the filesize could be very slow and doesn't change that often
	 */
	public void updateQueueCount() {
		queCountJLabel.setText( Settings.jpoResources.getString("queCountJLabel") + ThumbnailCreationQueue.countQueueRequests() );
	}

}
