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
QueryJFrame.java:  creates a GUI to allow the user to specify his search

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
 * QueryJFrame.java:  creates a GUI to allow the user to specify his search
 *
 **/
public class QueryJFrame extends JFrame {

	/**
	 *  reference to the node that should be checked
	 */
	SortableDefaultMutableTreeNode startSearchNode;


	/**
	 *  the string that is searched for in all texts.
	 */
	private JTextField anyFieldJTextField = new JTextField();
	
	
	/**
	 *  the component that says whether the results should be added to the
	 *  tree or not
	 */
	//private JCheckBox saveResults = new JCheckBox( Settings.jpoResources.getString("searchDialogSaveResultsLabel"), true);

	/**
	 *  the lower date for a specified range
	 */
	private JTextField lowerDateJTextField = new JTextField("");


	/**
	 *  the upper date for a specified range
	 */
	private JTextField upperDateJTextField = new JTextField("");


	/** 
	 *  a reference to the collectionJTree that should show the results
	 */
	private CollectionJTree collectionJTree;
	
	/**
	 *  a reference to the ThumbnailJScrollpane that should show the results
	 */
	private ThumbnailJScrollPane thumbnailJScrollPane;
			    
	//private static final Dimension compactSize = new Dimension( 300, 350 );
	
	//private static final Dimension advancedSize = new Dimension( 300, 550 );
	
	/**
	 *  Creates a GUI to specify the search criteria.
	 *
	 **/
	public QueryJFrame( SortableDefaultMutableTreeNode startSearchNode, 
		CollectionJTree collectionJTree, 
		ThumbnailJScrollPane thumbnailJScrollPane ) {
		
		
		this.startSearchNode = startSearchNode;
		this.collectionJTree = collectionJTree;
		this.thumbnailJScrollPane = thumbnailJScrollPane;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
				getRid();
			}
	        });  
		
		setLocationRelativeTo( Settings.anchorFrame );
		setTitle ( Settings.jpoResources.getString("searchDialogTitle") );

		JPanel jPanel = new JPanel();
		jPanel.setLayout( new GridBagLayout() );
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx= 0;
		c.gridy= 0;
		c.fill = GridBagConstraints.NONE;
		
		//JLabel searchJLabel = new JLabel( Settings.jpoResources.getString("searchDialogLabel") );
		//jPanel.add( searchJLabel );
		
		anyFieldJTextField.setPreferredSize( new Dimension( 200, 40) );
		anyFieldJTextField.setMinimumSize( new Dimension( 200, 40) );
		anyFieldJTextField.setMaximumSize( new Dimension( 600, 40) );
		anyFieldJTextField.setBorder( 
			BorderFactory.createTitledBorder( 
				Settings.jpoResources.getString ("searchDialogLabel" ) ) );

		jPanel.add( anyFieldJTextField, c );

		final JLabel lowerDateJLabel = new JLabel( Settings.jpoResources.getString("lowerDateJLabel") );
		
		final JPanel dateRange = new JPanel();

		
		final JButton advancedFindJButton = new JButton( Settings.jpoResources.getString( "advancedFindJButtonOpen" ) );
		advancedFindJButton.addActionListener( new ActionListener() {
			private String savedLowerDateValue = Tools.currentDate("dd.MM.yyyy");
			private String savedUpperDateValue = Tools.currentDate("dd.MM.yyyy");
			
			public void actionPerformed (ActionEvent evt ) {
				if ( dateRange.isVisible() ) {
					dateRange.setVisible( false );
					
					savedLowerDateValue = lowerDateJTextField.getText(); 
					lowerDateJTextField.setText( "" );
					
					savedUpperDateValue = upperDateJTextField.getText(); 
					upperDateJTextField.setText( "" );

					advancedFindJButton.setText( Settings.jpoResources.getString( "advancedFindJButtonOpen" ) );
					//setSize( compactSize );
				} else {
					dateRange.setVisible( true );

					lowerDateJTextField.setText( savedLowerDateValue );					
					lowerDateJTextField.setVisible( true );
					
					upperDateJTextField.setText( savedUpperDateValue );					
					upperDateJTextField.setVisible( true );
					advancedFindJButton.setText( Settings.jpoResources.getString( "advancedFindJButtonClose" ) );
					//setSize( advancedSize );
				}
				//validate();
				pack();
			}
		} );
		c.gridx++;
		jPanel.add( advancedFindJButton, c );


		lowerDateJTextField.setPreferredSize( new Dimension( 100, 25) );
		lowerDateJTextField.setMinimumSize( new Dimension( 100, 25) );
		dateRange.add( lowerDateJTextField );

		upperDateJTextField.setPreferredSize( new Dimension( 100, 25) );
		upperDateJTextField.setMinimumSize( new Dimension( 100, 25) );
		dateRange.add( upperDateJTextField );

		dateRange.setBorder( 
			BorderFactory.createTitledBorder( 
				Settings.jpoResources.getString( "lowerDateJLabel" ) ) );


		//lowerDateJLabel.setVisible( false );
		dateRange.setVisible( false );
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 2;
		jPanel.add( dateRange, c );
		
		

		//c.gridy++;
		//jPanel.add( saveResults, c );

		
		JButton okJButton = new JButton( Settings.jpoResources.getString( "genericOKText" ) );
		JButton cancelJButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );

		// crate a JPanel for the buttons
		JPanel buttonJPanel = new JPanel ();
			
		// add the ok button
		okJButton.setPreferredSize( new Dimension(120, 25) );
		okJButton.setMinimumSize( Settings.defaultButtonDimension );
		okJButton.setMaximumSize( new Dimension(120, 25) );
		okJButton.setDefaultCapable( true );
		this.getRootPane().setDefaultButton ( okJButton );
		okJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				runQuery();
			}
		} );
		buttonJPanel.add ( okJButton );

		// add the cancel button
		cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
		cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
		cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
		cancelJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				getRid();
			}
		} );
		buttonJPanel.add ( cancelJButton );
		c.gridy++;
		jPanel.add( buttonJPanel, c );

		getContentPane().add( jPanel, BorderLayout.CENTER );
		//setSize( compactSize );
			
	 	//  As per http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
		Runnable runner = new FrameShower( this );
        	EventQueue.invokeLater( runner );
	}


	/**
	 *  method that closes te frame and gets rid of it
	 */
	private void getRid() {
		setVisible ( false );
		dispose ();
	}

	/**
	 *  method that runs the Query
	 */
	private void runQuery() {
		if ( ! ( startSearchNode.getUserObject() instanceof GroupInfo) ) {
			Tools.log("QueryJFrame.runQuery: can only be invoked on GroupInfo nodes! Ignoring request. You are on node: " + this.toString());
			return ;
		}

		TextQuery q = new TextQuery( anyFieldJTextField.getText() );
		q.setLowerDateRange( Tools.parseDate( lowerDateJTextField.getText() ) );
		q.setUpperDateRange( Tools.parseDate( upperDateJTextField.getText() ) );
		q.setStartNode( startSearchNode );
	
		if ( ( q.getLowerDateRange() != null ) 
		  && ( q.getUpperDateRange() != null )
		  && ( q.getLowerDateRange().compareTo( q.getUpperDateRange() ) > 0 ) ) {
			JOptionPane.showMessageDialog(
				this, 
				Settings.jpoResources.getString("dateRangeError"),
				Settings.jpoResources.getString("genericError"), 
				JOptionPane.ERROR_MESSAGE);
			return;
		}


		Settings.pictureCollection.addQueryToTreeModel( q );
		
		getRid();

		/*
		SortableDefaultMutableTreeNode resultNode = 
			Settings.top.getRootNode().findAndSave( q );
		if ( resultNode == null ) {
			JOptionPane.showMessageDialog(
				this, 
				Settings.jpoResources.getString("noSearchResults"),
				Settings.jpoResources.getString("searchDialogTitle"), 
				JOptionPane.INFORMATION_MESSAGE);
		} else {
			collectionJTree.setSelectedNode( resultNode );
			collectionJTree.expandPath( new TreePath ( resultNode.getPath()) );
			thumbnailJScrollPane.showGroup( resultNode );
			getRid();
		}*/
	}



	/**
	 *  This allows the search to be run in it's own thread
	 *
	 */
	private class QueryThread extends Thread {	
		/**
		 *  Constructor for the thread 
		 */
		public QueryThread () {
			start();
		}
	
		/**
		 *  this is run in it's own thread
		 */
		public void run() {
			//checkIntegrity();
		}
	} 


	

}
