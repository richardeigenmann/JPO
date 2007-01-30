package jpo;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.lang.*;
import javax.jnlp.*;
import javax.swing.Timer;


/*
IntegrityChecker.java:  creates a frame and checks the integrity of the collection

Copyright (C) 2002-2007  Richard Eigenmann, Zurich, Switzerland
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
 * IntegrityChecker.java:  creates a frame and checks the integrity of the collection
 *
 **/
public class IntegrityChecker extends JFrame {

	JCheckBox check1 = new JCheckBox( Settings.jpoResources.getString("check1") );
	JCheckBox check2 = new JCheckBox( Settings.jpoResources.getString("check2") );
	JCheckBox check3 = new JCheckBox( Settings.jpoResources.getString("check2") );
	JButton closeButton = 
		new JButton( Settings.jpoResources.getString("genericOKText") );

	Timer timer = new Timer( 15000, new ActionListener() {
		public void actionPerformed (ActionEvent evt ) {
			getRid();
		}    
	});


	/**
	 *  reference to the node that should be checked
	 */
	SortableDefaultMutableTreeNode startNode;

			    
	/**
	 *  Constructor for the window that shows the various checks being performed.
	 *
	 **/
	public IntegrityChecker( SortableDefaultMutableTreeNode startNode ) {
		this.startNode = startNode;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
				getRid();
			}
	        });  
		
		setLocationRelativeTo( Settings.anchorFrame );
		setTitle ( Settings.jpoResources.getString("IntegrityCheckerTitle") );

		JPanel jPanel = new JPanel();
		JLabel integrityCheckerLabel = 
			new JLabel( Settings.jpoResources.getString("integrityCheckerLabel") );
		jPanel.add( integrityCheckerLabel );
	        jPanel.setLayout( new GridLayout(0, 1) );
		check1.setEnabled( false );
		jPanel.add( check1 );
		check2.setEnabled( false );
		jPanel.add( check2 );
		check3.setEnabled( false );
		jPanel.add( check3 );
		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				getRid();
			}
		});
		//closeButton.setEnabled( false );
		closeButton.setMaximumSize( Settings.defaultButtonDimension );
		closeButton.setMinimumSize( Settings.defaultButtonDimension );
		closeButton.setPreferredSize( Settings.defaultButtonDimension );
		jPanel.add( closeButton );

		getContentPane().add( jPanel, BorderLayout.CENTER );
		setSize( new Dimension( 300, 150 ) );
			
	 	//  As per http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
		Runnable runner = new FrameShower( this );
        	EventQueue.invokeLater( runner );

		IntegrityCheckerThread ict = new IntegrityCheckerThread();
	}


	/**
	 *  method that closes te frame and gets rid of it
	 */
	private void getRid() {
		stopChecks();
		startNode = null;
		setVisible ( false );
		dispose ();
	}


	/**
	 *  This method does the checking
	 */
	private void checkIntegrity() {
		// from http://www.rgagnon.com/javadetails/java-0349.html
		Font f = check1.getFont();
		// bold
		//check1.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
		// unbold
		//check1.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
		check1.setFont(f.deriveFont(f.getStyle() | Font.ITALIC));
		check1.setForeground( Color.black );
		int badDates = checkDates();
		check1.setText( Settings.jpoResources.getString("check1done") + Integer.toString(badDates) );
		check1.setSelected( true );
		
		check2.setFont(f.deriveFont(f.getStyle() | Font.ITALIC));
		check2.setForeground( Color.black );
		int verifyChecksums = verifyChecksums();
		check2.setSelected( true );
		check2.setText( Settings.jpoResources.getString("check2done") + Integer.toString( verifyChecksums ) );
		
		check3.setForeground( Color.black );
		check3.setSelected( true );
		//closeButton.setEnabled( true );
		
		timer.setRepeats( false );
		timer.start();
		
	}
	


	/**
	 *   Variable that allows the checkDates method to be terminated gracefully 
	 *   by setting it to false;
	 */
	public boolean checkDates = false;

	/**
	 *  Checks all the nodes and reports those nodes whose date can't be parsed properly
	 */
	private int checkDates() {
		int count = 0;
		SortableDefaultMutableTreeNode testNode;
		PictureInfo pi;
		Object nodeObject;
		for (Enumeration e = startNode.breadthFirstEnumeration() ; e.hasMoreElements() && checkDates ;) { 
			testNode =  (SortableDefaultMutableTreeNode) e.nextElement(); 
			nodeObject = testNode.getUserObject();
			if  ( ( nodeObject instanceof PictureInfo ) )  {
				pi = (PictureInfo) nodeObject;
				if ( pi.getCreationTimeAsDate() == null ) {
					Tools.log( "IntegrityChecker.checkDates: Can't parse date: " + pi.getCreationTime() + " from Node: " + pi.getDescription() );
					count++;
				} else {
					// Tools.log( "IntegrityChecker.checkDates:" + pi.getFormattedCreationTime() + " from " + pi.getCreationTime() + " from Node: " + pi.getDescription() ); 
				}
			}
		}
		return count;
	} 


	/**
	 *   Variable that allows the verifyChecksums method to be terminated gracefully 
	 *   by setting it to false;
	 */
	public boolean verifyChecksums = true;
	
	
	/**
	 *  Checks all the nodes and adds checksums for those nodes that didn't have one
	 */
	private int verifyChecksums() {
		int count = 0;
		SortableDefaultMutableTreeNode testNode;
		PictureInfo pi;
		Object nodeObject;
		long checksum;
		for (Enumeration e = startNode.breadthFirstEnumeration() ; e.hasMoreElements() && verifyChecksums; ) { 
			testNode =  (SortableDefaultMutableTreeNode) e.nextElement(); 
			nodeObject = testNode.getUserObject();
			if  ( ( nodeObject instanceof PictureInfo ) )  {
				pi = (PictureInfo) nodeObject;
				File f = pi.getHighresFile();
				if ( f != null ) {
					checksum = Tools.calculateChecksum( f );
					if ( pi.getChecksum() != checksum ) {
						pi.setChecksum( checksum );
						count++;
						if ( count % 10 == 0 ) {
							check2.setText( Settings.jpoResources.getString("check2progress") + Integer.toString( count ) );
						}
					}
				}
			}
		}
		return count;
	} 


	/**
	 *  This method sets the boolean variables to false that will stop the integrity checkers.
	 */
	public void stopChecks() {
		checkDates = false;
		verifyChecksums = false;
	}



	/**
	 *  This class allows the integrity check to run in it's own thread.
	 *
	 */
	private class IntegrityCheckerThread extends Thread {	
		/**
		 *  Constructor for the thread 
		 */
		public IntegrityCheckerThread () {
			start();
		}
	
		/**
		 *  this is run in it's own thread
		 */
		public void run() {
			checkIntegrity();
		}
	} 


	

}
