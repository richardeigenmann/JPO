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
CollectionPropertiesJPanel.java: a panel that shows some counts about the collection

Copyright (C) 2002-2006  Richard Eigenmann.
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
 *  a panel that shows some counts about the collection
 */
public class CollectionPropertiesJPanel 
	extends JPanel {

	/**
	 *   Number of Nodes in the tree including the current node
	 */
	private JLabel collectionItemsLabel = new JLabel ();
	
	/**
	 *  Number of Groups in the tree excluding the current node
	 */
	private JLabel collectionGroupsLabel = new JLabel ();
	
	/**
	 *  Number of Pictures in the tree
	 */
	private JLabel collectionPicturesLabel = new JLabel ();
	
	/**
	 *  Number of Jobs on the Thumbnail queue
	 */
	private JLabel collectionSizeJLabel = new JLabel ();

	/**
	 *  Free Application Memory
	 */
	private JLabel freeMemoryJLabel = new JLabel ();
	
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
		add( freeMemoryJLabel, constraints );

		constraints.gridy++;
		add( queCountJLabel, constraints );
	}





	public void updateStats( DefaultMutableTreeNode statisticsNode ) {
		Tools.log("CollectionPropertiesJPanel.updateStats: inkoved on " + statisticsNode.toString() );
		int numberOfNodes = Tools.countNodes( statisticsNode );
		collectionItemsLabel.setText( Settings.jpoResources.getString("CollectionNodeCountLabel") + Integer.toString( numberOfNodes ) );

		int numberOfGroups = Tools.countGroups( statisticsNode );
		collectionGroupsLabel.setText( Settings.jpoResources.getString("CollectionGroupCountLabel") + Integer.toString( numberOfGroups ) );

		int numberOfPictures = Tools.countPictures( statisticsNode );
		collectionPicturesLabel.setText( Settings.jpoResources.getString("CollectionPictureCountLabel") + Integer.toString( numberOfPictures ) );

		String sizeOfPictures = Tools.sizeOfPictures( statisticsNode );
		collectionSizeJLabel.setText( Settings.jpoResources.getString("CollectionSizeJLabel") + sizeOfPictures );

		updateQueueCount();		
	}		
	

	/**
	 *  This method updates the label showing the entries on the queue. This was split out for performance
	 *  as counting the filesize could be very slow and doesn't change that often
	 */
	public void updateQueueCount() {
		freeMemoryJLabel.setText( Tools.freeMemory() );
		queCountJLabel.setText( Settings.jpoResources.getString("queCountJLabel") + ThumbnailCreationQueue.countQueueRequests() );
	}

}
