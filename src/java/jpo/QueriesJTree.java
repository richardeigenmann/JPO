package jpo;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

import java.awt.event.*;

import java.util.*;
import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;

import java.io.*;
import java.io.IOException;
import java.net.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.DefaultListModel;


/*
QueriesJTree.java:  class that creates a JTree to display the queries

Copyright (C) 2006-2007  Richard Eigenmann, Zurich, Switzerland
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
 *   The is one of the main classes in the JPO application as it is an extended JTree that
 *   deals with most of the logic surrounding the collection and the user interactions with it.
 */
public class QueriesJTree extends JTree {

	/**
	 * Constructs a JTree for the queries
	 */
	public QueriesJTree() {
		//Tools.log("QueriesJTree.constructor: Setting model to: " + tm.toString() );
		getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		putClientProperty( "JTree.lineStyle", "Angled" );
		setOpaque( true );
		setEditable(false);
		setShowsRootHandles( true );
		setMinimumSize( Settings.jpoNavigatorJTabbedPaneMinimumSize );
		//setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );
		//setCellRenderer( new JpoTreeCellRenderer() );
		setModel( Settings.pictureCollection.getQueriesTreeModel() );

		//Add listener to components that can bring up groupPopupJPopupMenu menus.
		QueriesMouseAdapter mouseAdapter = new QueriesMouseAdapter();
		addMouseListener( mouseAdapter );

	}

	/**
	 *  create a QueryBrowser object that facilitates the browsing
	 */
	private QueryBrowser queryBrowser = new QueryBrowser();
	


	/**
	 *   a reference to the Thumbnail Pane that is displaying pictures. This
	 *   allows the QueriesJTree to tell the Thumbnail Pane to display searches 
	 *   of pictures via it's showGroup method.
	 */
	private ThumbnailJScrollPane associatedThumbnailJScrollPane;


	/**  
	 *   This method assigns the supplied ThumbnailJScrollpane with this JTree. This association is
	 *   used to allow the JTree to order the ThumbnailJScrollpane to display a different search.
	 */
	public void setAssociatedThumbnailJScrollpane( ThumbnailJScrollPane associatedThumbnailJScrollPane ) {
		this.associatedThumbnailJScrollPane = associatedThumbnailJScrollPane;
	}



	/**
	 *   a reference to the Info Panel that is displaying information. This
	 *   allows the QueriesJTree to tell the Info Panel about selections
	 *   via it's showInfo method.
	 */
	private InfoPanel associatedInfoPanel;


	/**  
	 *   This method assigns the supplied InfoPanel with this JTree. This association is
	 *   used to allow the JTree to tell the InfoPanel to show information about selections.
	 */
	public void setAssociatedInfoPanel( InfoPanel associatedInfoPanel ) {
		this.associatedInfoPanel = associatedInfoPanel;
	}



	/**  
	 *  subclass to deal with the Mouse events
	 **/
	private class QueriesMouseAdapter extends MouseAdapter {
	
		/**
		 *    If the mouse was clicked more than once using the left mouse button over a valid picture
		 *    node then the picture editor is opened.
		 */
		public void mouseClicked( MouseEvent e ) {
			TreePath clickPath = getPathForLocation( e.getX(), e.getY() );
			if ( clickPath == null ) return; // happens
			DefaultMutableTreeNode clickNode = (DefaultMutableTreeNode) clickPath.getLastPathComponent();

			if ( associatedInfoPanel != null ) {
				associatedInfoPanel.showInfo( clickNode );
			}

			if ( e.getClickCount() == 1 && (! e.isPopupTrigger() ) ) {
				if ( associatedThumbnailJScrollPane != null ) {
					if ( ( clickNode == null ) 
					  || ( clickNode.getUserObject() == null )
					  || ( ! ( clickNode.getUserObject() instanceof Query ) ) ) {
					  	return;
					}
					queryBrowser.setQuery( (Query) clickNode.getUserObject() );
					associatedThumbnailJScrollPane.show( queryBrowser );
				}
			}
		}
		
		/**
		 *   Override thge mousePressed event.
		 */
		public void mousePressed(MouseEvent e) {
			//maybeShowPopup(e);
		}
		
		/**
		 *  Override the mouseReleased event.
		 */
		public void mouseReleased(MouseEvent e) {
			//maybeShowPopup(e);
		}
		
		/**
		 *  This method figures out whether a popup window should be displayed and displays 
		 *  it.
		 *  @param   e	The MouseEvent that was trapped.
		 */
		private void maybeShowPopup( MouseEvent e ) {
			/*if ( e.isPopupTrigger() ) {
				popupPath = getPathForLocation(e.getX(), e.getY());
				if ( popupPath == null ) return; // happens
				popupNode = (SortableDefaultMutableTreeNode) popupPath.getLastPathComponent();
				setSelectionPath ( popupPath );
				Object nodeInfo = popupNode.getUserObject();
				
				if (nodeInfo instanceof GroupInfo) {
					GroupPopupMenu groupPopupMenu = new GroupPopupMenu( CollectionJTree, popupNode );				
					groupPopupMenu.show(e.getComponent(), e.getX(), e.getY());
				} else if (nodeInfo instanceof PictureInfo) {
					PicturePopupMenu picturePopupMenu = new PicturePopupMenu( popupNode );
					picturePopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}*/
		}
	}




}
