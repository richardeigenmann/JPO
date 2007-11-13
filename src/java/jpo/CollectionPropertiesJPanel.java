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

Copyright (C) 2002-2007  Richard Eigenmann.
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
	private JLabel queueCountJLabel = new JLabel ();




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
		constraints.insets = new Insets(0, 4, 0, 4);
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
		add( queueCountJLabel, constraints );
	}




        /**
         *  This method will update the statistics based on the supplied input node.
         *  @param  statisticsNode   The node that is being analysed.
         */
	public void updateStats( DefaultMutableTreeNode statisticsNode ) {
		//Tools.log("CollectionPropertiesJPanel.updateStats: called on node: " + statisticsNode.toString() );
		if ( Settings.pictureCollection.fileLoading ) {
			Tools.log("CollectionPropertiesJPanel.updateStats: Still busy loading the file. Aborting");
			return;
		}

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
	 *  This method updates the label showing the entries on the queue. This was split so that costly operations could be slowed down but it doesn't
         *  Seem to be a problem. The memory usage and the Thumbnails on queue are only shown when the log file is being written as normal users will
         *  hardly be interested in this detail.
	 */
	public void updateQueueCount() {
                if ( Settings.writeLog ) {
                        freeMemoryJLabel.setVisible( true );
        		freeMemoryJLabel.setText( Tools.freeMemory() );
                        queueCountJLabel.setVisible( true );
        		queueCountJLabel.setText( Settings.jpoResources.getString("queCountJLabel") + ThumbnailCreationQueue.getQueueRequestCount() );
                } else {
                        freeMemoryJLabel.setVisible( false );
                        queueCountJLabel.setVisible( false );
                }
	}

}
