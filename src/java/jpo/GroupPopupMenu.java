package jpo;

import java.awt.event.*;
import javax.swing.*;

/*
GroupPopupMenu.java: popup menu for groups
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
 * A class that generates a popup menu for a group node. This became nescessary primarily because
 * the code was getting a bit long and was clutterin up a different class. Seperating out the 
 * popup menu and making it an object and forcing an interface on the object instantiating
 * it is propbably more in line with the OO philosophy.
 *
 */

class GroupPopupMenu extends JPopupMenu
			implements ActionListener,
				   RecentDropNodeListener {


	/** 
	 *  menu item that allows the user to edit the group description
	 **/
	private JMenuItem groupFindJMenuItem  = new JMenuItem( Settings.jpoResources.getString("groupFindJMenuItemLabel") ); 


	/** 
	 *  menu item that allows the user to edit the group description
	 **/
	private JMenuItem groupEditJMenuItem = new JMenuItem( Settings.jpoResources.getString("groupEditJMenuItemLabel") ); 

	/** 
	 *  menu item that allows the user to edit the group data as a Table
	 **/
	private JMenuItem groupTableJMenuItem  = new JMenuItem( Settings.jpoResources.getString("groupTableJMenuItemLabel") );
	
	/** 
	 *  menu item that allows the user to remove a group from the index
	 **/
	private JMenuItem groupRemove 
		= new JMenuItem( Settings.jpoResources.getString("groupRemoveLabel") );

	
	/** 
	 *  menu item that allows the user to export the group to several different formats
	 **/
	private JMenuItem groupExportHtml 
		= new JMenuItem( Settings.jpoResources.getString("groupExportHtmlMenuText") );

	/** 
	 *  menu item that allows the user to export the group to several different formats
	 **/
	private JMenuItem groupExportNewCollection
		= new JMenuItem( Settings.jpoResources.getString("groupExportNewCollectionMenuText") );


	/** 
	 *  menu item that allows the user to export the group to a flat list of filenames
	 **/
	private JMenuItem groupExportFlatFile
		= new JMenuItem( Settings.jpoResources.getString("groupExportFlatFileMenuText") );


	/** 
	 *  menu item that allows the user to export the group to several different formats
	 **/
	private JMenuItem groupExportJar
		= new JMenuItem( Settings.jpoResources.getString("groupExportJarMenuText") );


	/** 
	 *  menu item that allows the user to rejuest the group to be shown. 
	 * 
	 **/
	private JMenuItem groupSlideshowJMenuItem
		= new JMenuItem( Settings.jpoResources.getString( "groupSlideshowJMenuItemLabel" ) );


	/**
	 *  submenu which has several navigation options
	 */
	private JMenu addGroupJMenu
		= new JMenu( Settings.jpoResources.getString("addGroupJMenuLabel") );


	/** 
	 *  menu item that allows adding a new blank group
	 * 
	 **/
	private JMenuItem addNewGroupJMenuItem
		= new JMenuItem ( Settings.jpoResources.getString("addNewGroupJMenuItemLabel") );


	/** 
	 *  menu item that allows adding indivudual pictures
	 * 
	 **/
	private JMenuItem addPicturesJMenuItem
		= new JMenuItem ( Settings.jpoResources.getString("addPicturesJMenuItemLabel") );



	/** 
	 *  menu item that allows adding a collection of pictures
	 * 
	 **/
	private JMenuItem addCollectionJMenuItem
		= new JMenuItem ( Settings.jpoResources.getString("addCollectionJMenuItemLabel") );


	/** 
	 *  menu item that allows adding from a list of filenames
	 * 
	 **/
	private JMenuItem addFlatFileJMenuItem
		= new JMenuItem ( Settings.jpoResources.getString("addFlatFileJMenuItemLabel") );


	/**
	 *  submenu which has several navigation options
	 */
	private JMenu moveGroupNodeJMenu
		= new JMenu( Settings.jpoResources.getString("moveNodeJMenuLabel") );


	/** 
	 *  menu item that allows move to top op list
	 * 
	 **/
	private JMenuItem moveGroupToTopJMenuItem
		= new JMenuItem ( Settings.jpoResources.getString("moveGroupToTopJMenuItem") );


	/** 
	 *  menu item that allows move up in the list
	 * 
	 **/
	private JMenuItem moveGroupUpJMenuItem
		= new JMenuItem ( Settings.jpoResources.getString("moveGroupUpJMenuItem") );


	/** 
	 *  menu item that allows move up in the list
	 * 
	 **/
	private JMenuItem moveGroupDownJMenuItem
		= new JMenuItem ( Settings.jpoResources.getString("moveGroupDownJMenuItem") );


	/** 
	 *  menu item that allows move to top op list
	 * 
	 **/
	private JMenuItem moveGroupToBottomJMenuItem
		= new JMenuItem ( Settings.jpoResources.getString("moveGroupToBottomJMenuItem") );


	/** 
	 *  menu item that allows indenting the group
	 * 
	 **/
	private JMenuItem indentJMenuItem
		= new JMenuItem ( Settings.jpoResources.getString("indentJMenuItem") );


	/** 
	 *  menu item that allows outdenting the group
	 * 
	 **/
	private JMenuItem outdentJMenuItem
		= new JMenuItem ( Settings.jpoResources.getString("outdentJMenuItem") );


	/**
	 *  a separator for the Move menu
	 */
	private JSeparator movePictureNodeSeparator = new JSeparator();


	/**
	 *  menu items for the recently dropped group nodes
	 */
	private JMenuItem [] recentDropNodes = new JMenuItem[ Settings.maxDropNodes ];




	/** 
	 *  menu item that brings a dialog to ask where to consolidate the files to
	 * 
	 **/
	private JMenuItem consolidateMoveJMenuItem  = new JMenuItem ( Settings.jpoResources.getString("consolidateMoveLabel") );


	/**
	 *  submenu which has several sort options
	 */
	private JMenu sortJMenu = new JMenu( Settings.jpoResources.getString("sortJMenu") );


	/** 
	 *  requests a sort by Description
	 * 
	 **/
	private JMenuItem sortByDescriptionJMenuItem  = new JMenuItem ( Settings.jpoResources.getString("sortByDescriptionJMenuItem") );

	/** 
	 *  requests a sort by Film Reference
	 * 
	 **/
	private JMenuItem sortByFilmReferenceJMenuItem  = new JMenuItem ( Settings.jpoResources.getString("sortByFilmReferenceJMenuItem") );

	/** 
	 *  requests a sort by Creation Time 
	 * 
	 **/
	private JMenuItem sortByCreationTimeJMenuItem  = new JMenuItem ( Settings.jpoResources.getString("sortByCreationTimeJMenuItem") );


	/** 
	 *  requests a sort by Comment 
	 * 
	 **/
	private JMenuItem sortByCommentJMenuItem  = new JMenuItem ( Settings.jpoResources.getString("sortByCommentJMenuItem") );

	/** 
	 *  requests a sort by Photographer
	 * 
	 **/
	private JMenuItem sortByPhotographerJMenuItem  = new JMenuItem ( Settings.jpoResources.getString("sortByPhotographerJMenuItem") );

	/** 
	 *  requests a sort by Copyright Holder
	 * 
	 **/
	private JMenuItem sortByCopyrightHolderTimeJMenuItem  = new JMenuItem ( Settings.jpoResources.getString("sortByCopyrightHolderTimeJMenuItem") );
	
	
	/**
	 *  object that must implement the functions dealing with the user
	 *  request
	 */
	private GroupPopupInterface caller;
	
	
	/**
	 *  the node we are doing the popup menu for
	 */
	private final SortableDefaultMutableTreeNode popupNode;


	/**
	 *   Creates a popup menu for a group.
	 *   @param  caller   the caller that will get the requests to do things
	 *   @param  node 	the node for which the popup menu is being created.
	 */
	public GroupPopupMenu ( GroupPopupInterface caller, SortableDefaultMutableTreeNode node ) {
		this.caller = caller;
		this.popupNode = node;

		groupSlideshowJMenuItem.addActionListener( this );
		add( groupSlideshowJMenuItem );

		groupFindJMenuItem.addActionListener( this );
		add(groupFindJMenuItem);

		addSeparator();

		if ( Settings.top.getAllowEdits() ) {

			groupEditJMenuItem.addActionListener( this );
			add( groupEditJMenuItem) ;

			addSeparator();

			groupTableJMenuItem.addActionListener( this );
			add( groupTableJMenuItem );

			addSeparator();

			add( addGroupJMenu );

			addNewGroupJMenuItem.addActionListener( this );
			addGroupJMenu.add( addNewGroupJMenuItem );

			addPicturesJMenuItem.addActionListener( this );
			addGroupJMenu.add( addPicturesJMenuItem );

			addCollectionJMenuItem.addActionListener( this );
			addGroupJMenu.add( addCollectionJMenuItem );


			addFlatFileJMenuItem.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e ) {
					popupNode.addFlatFile();
				}
			});
			addGroupJMenu.add( addFlatFileJMenuItem );

			add( moveGroupNodeJMenu );

			for ( int i = 0; i < Settings.maxDropNodes; i++ ) {
				recentDropNodes[ i ] = new JMenuItem ();
				recentDropNodes[ i ].addActionListener( this );
				moveGroupNodeJMenu.add( recentDropNodes[ i ] );
			}
			moveGroupNodeJMenu.add( movePictureNodeSeparator );
			recentDropNodesChanged();

		
			moveGroupToTopJMenuItem.addActionListener( this );
			moveGroupNodeJMenu.add( moveGroupToTopJMenuItem );
		
			moveGroupUpJMenuItem.addActionListener( this );
			moveGroupNodeJMenu.add( moveGroupUpJMenuItem );

			moveGroupDownJMenuItem.addActionListener( this );
			moveGroupNodeJMenu.add( moveGroupDownJMenuItem );

			moveGroupToBottomJMenuItem.addActionListener( this );
			moveGroupNodeJMenu.add( moveGroupToBottomJMenuItem );

			indentJMenuItem.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e ) {
					popupNode.indentNode();
				}
			});
			moveGroupNodeJMenu.add( indentJMenuItem );

			outdentJMenuItem.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e ) {
					popupNode.outdentNode();
				}
			});
			moveGroupNodeJMenu.add( outdentJMenuItem );

			groupRemove.addActionListener( this );
			add( groupRemove );

			addSeparator();
		
			consolidateMoveJMenuItem.addActionListener( this );
			add( consolidateMoveJMenuItem );
		
			addSeparator();

			add( sortJMenu );

			sortByDescriptionJMenuItem.addActionListener( this );
			sortJMenu.add( sortByDescriptionJMenuItem );

			sortByFilmReferenceJMenuItem.addActionListener( this );
			sortJMenu.add( sortByFilmReferenceJMenuItem );

			sortByCreationTimeJMenuItem.addActionListener( this );
			sortJMenu.add( sortByCreationTimeJMenuItem );

			sortByCommentJMenuItem.addActionListener( this );
			sortJMenu.add( sortByCommentJMenuItem );

			sortByPhotographerJMenuItem.addActionListener( this );
			sortJMenu.add( sortByPhotographerJMenuItem );

			sortByCopyrightHolderTimeJMenuItem.addActionListener( this );
			sortJMenu.add( sortByCopyrightHolderTimeJMenuItem );

			addSeparator();
		}

		
		groupExportHtml.addActionListener( this );
		add(groupExportHtml);

		groupExportNewCollection.addActionListener( this );
		add(groupExportNewCollection);

		/* Disabled because it's not thought through and the resulting jar file is
		   too difficult to load.
		groupExportJar.addActionListener( this );
		add(groupExportJar);
		*/

		groupExportFlatFile.addActionListener( this );
		add( groupExportFlatFile );
	}


	/**
	 *  Here we receive notification that the nodes have been updated
	 */
	public void recentDropNodesChanged () {
		boolean dropNodesVisible = false;
		for ( int i = 0; i < Settings.maxDropNodes; i++ ) {
			if ( ( Settings.recentDropNodes[ i ] != null ) ) {
				recentDropNodes[ i ].setText( "To Group: " + Settings.recentDropNodes[ i ].toString() );
				recentDropNodes[ i ].setVisible( true );
				dropNodesVisible = true;
			} else {
				recentDropNodes[ i ].setVisible( false );
			}
		}
		if ( dropNodesVisible ) {
			movePictureNodeSeparator.setVisible( true );
		} else {
			movePictureNodeSeparator.setVisible( false );
		}

	}


	/** 
	 *  method that analyses the user initiated action and performs what the user reuqested.
	 **/
	public void actionPerformed( ActionEvent e ) {
		// Group popup menu				

		if ( e.getSource() == groupSlideshowJMenuItem )
			caller.requestSlideshow();				

		else if ( e.getSource() == groupFindJMenuItem )
			caller.requestFind();
			
		else if ( e.getSource() == groupEditJMenuItem )
			caller.requestEditGroupNode();				

		else if ( e.getSource() == groupTableJMenuItem )
			caller.requestEditGroupTable();

		else if ( e.getSource() == addNewGroupJMenuItem )
			caller.requestAddGroup();				
		else if ( e.getSource() == addPicturesJMenuItem )
			caller.requestAdd();				
		else if ( e.getSource() == addCollectionJMenuItem )
			caller.requestAddCollection();				
		else if ( e.getSource() == groupExportHtml ) 
			caller.requestGroupExportHtml();
		else if ( e.getSource() == groupExportJar ) 
			caller.requestGroupExportJar();
		else if ( e.getSource() == groupExportFlatFile ) 
			caller.requestGroupExportFlatFile();
		else if ( e.getSource() == groupExportNewCollection ) 
			caller.requestGroupExportNewCollection();
			
		else if ( e.getSource() == groupRemove ) 
			caller.requestGroupRemove();

		else if (e.getSource() == consolidateMoveJMenuItem )			
			caller.requestConsolidateGroup();

		else if (e.getSource() == moveGroupToTopJMenuItem )			
			caller.requestMoveGroupToTop();
		else if (e.getSource() == moveGroupUpJMenuItem )			
			caller.requestMoveGroupUp();
		else if (e.getSource() == moveGroupDownJMenuItem )			
			caller.requestMoveGroupDown();
		else if (e.getSource() == moveGroupToBottomJMenuItem )			
			caller.requestMoveGroupToBottom();
		else if ( checkDropNodes( e.getSource() ) ) {
			// checkDropNodes does the required action
		}


		else if (e.getSource() == sortByDescriptionJMenuItem )			
			caller.requestSort( Settings.DESCRIPTION );
		else if (e.getSource() == sortByFilmReferenceJMenuItem )			
			caller.requestSort( Settings.FILM_REFERENCE );
		else if (e.getSource() == sortByCreationTimeJMenuItem )			
			caller.requestSort( Settings.CREATION_TIME );
		else if (e.getSource() == sortByCommentJMenuItem )			
			caller.requestSort( Settings.COMMENT );
		else if (e.getSource() == sortByPhotographerJMenuItem )			
			caller.requestSort( Settings.PHOTOGRAPHER );
		else if (e.getSource() == sortByCopyrightHolderTimeJMenuItem )			
			caller.requestSort( Settings.COPYRIGHT_HOLDER );

		else 
			JOptionPane.showMessageDialog(null, 
				"GroupPopupMenu.java: Unknown event", 
				"Error", 
				JOptionPane.ERROR_MESSAGE);
			
	}


	/**
	 *  checks if the event object is one of the drop nodes
	 *  @return returns true if the object was found in the list and the action was submitted.
	 */
	private boolean checkDropNodes ( Object o ) {
		for ( int i = 0; i < Settings.maxDropNodes; i++ ) {
			if ( ( recentDropNodes[ i ] !=null ) && ( o.hashCode() == recentDropNodes[ i ].hashCode() ) ) {
				caller.requestMoveToNode( Settings.recentDropNodes[ i ] );
				Settings.memorizeGroupOfDropLocation( Settings.recentDropNodes[ i ] );
				return true;
			}
		}
		return false;
	}


	

}
