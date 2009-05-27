package jpo.gui;

import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.*;
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
public class CollectionPropertiesJFrame 
	extends 	JFrame 
	implements 	ActionListener {


	/**
	 *  button to start the export
	 **/
	private JButton okJButton = new JButton ( Settings.jpoResources.getString("genericOKText") );


	/**
	 *  tickbox that indicates whether subdirectories are to be reconciled too
	 **/
	private JCheckBox editProtectJCheckBox = new JCheckBox ( Settings.jpoResources.getString("editProtectJCheckBoxLabel") );


	/**
	 *  reference to the collection
	 */
	private SortableDefaultMutableTreeNode statisticsNode;



	/** 
    	 *   Constructor to create a JFrame that displays statistics about the nodes underneath
	 *   the supplied node.
	 *   @param	statisticsNode	The node upon which the statistics should be produced.
	 */
	public CollectionPropertiesJFrame( SortableDefaultMutableTreeNode statisticsNode ) {
		this.statisticsNode = statisticsNode;
	
		setSize(460, 300);
		setLocationRelativeTo ( Settings.anchorFrame );
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e) {
				getRid();
			}
	        });  

		CollectionPropertiesJPanel controlJPanel = new CollectionPropertiesJPanel();
		controlJPanel.updateStats( statisticsNode );

		setTitle ( Settings.jpoResources.getString("CollectionPropertiesJFrameTitle") );
		controlJPanel.setBorder( new EmptyBorder( new Insets( 25,25,25,25) ) );


		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;


		constraints.gridx =0;

		constraints.gridy = 6;
		editProtectJCheckBox.setSelected( ! statisticsNode.getPictureCollection().getAllowEdits() );
		controlJPanel.add( editProtectJCheckBox, constraints );


		constraints.gridy++;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.NONE;
		okJButton.setPreferredSize( Settings.defaultButtonDimension );
	        okJButton.setMinimumSize( Settings.defaultButtonDimension );
	        okJButton.setMaximumSize( Settings.defaultButtonDimension );
		okJButton.setBorder(BorderFactory.createRaisedBevelBorder());
		okJButton.setDefaultCapable( true );
		getRootPane().setDefaultButton ( okJButton );
	        okJButton.addActionListener( this );
		controlJPanel.add( okJButton, constraints );
		
		
		getContentPane().add( controlJPanel );

		pack();
		setVisible(true);

	}





	/**
	 *  method that closes te frame and gets rid of it
	 */
	private void getRid() {
		setVisible ( false );
		dispose ();
	}



	/** 
	 *  method that analyses the user initiated action and performs what the user requested
         *
         * @param e
         */
	public void actionPerformed( ActionEvent e ) {
		if (e.getSource() == okJButton) {
			if ( statisticsNode.getPictureCollection().getAllowEdits() == editProtectJCheckBox.isSelected() ) {
				statisticsNode.getPictureCollection().setAllowEdits( ! editProtectJCheckBox.isSelected() );
				statisticsNode.getPictureCollection().setUnsavedUpdates( true );
			}
				
			getRid();
		} 
	}




		
			
	

}
