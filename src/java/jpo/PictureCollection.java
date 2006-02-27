package jpo;

import javax.swing.tree.*;

/*
PictureCollection.java:  An object that holds all the references to a collection of pictures

Copyright (C) 2006  Richard Eigenmann.
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
 *  An object that holds all the references to a collection of pictures
 */

public class PictureCollection {

	/**
	 *  Constructs a new PictureCollection object.
	 */
	public PictureCollection() {
		rootNode = new SortableDefaultMutableTreeNode( true );
		this.createQueriesTreeModel();
	}



	/**
	 *  The root node of the tree data model. It holds all the branches 
	 *  to the groups and pictures
	 **/
	private SortableDefaultMutableTreeNode rootNode;



	/**
	 *  This method returns the root node of the collection
	 */
	public SortableDefaultMutableTreeNode getRootNode() {
		return rootNode;
	} 


	/**
	 *  This method wipes out the data in the picture collection
	 */
	public void clearCollection() {
		clearQueriesTreeModel();		
	}











	/**
	 *   This variable holds the reference to the queries executed against the collection.
	 */
	private TreeModel queriesTreeModel;


	/**
	 *   Call this method when you need the TreeModel for the queries
	 */
	public TreeModel getQueriesTreeModel() {
		return( queriesTreeModel );
	}


	/**
	 *   Call this method when you need the toot Node for the queries
	 */
	public DefaultMutableTreeNode getQueriesRootNode() {
		return( (DefaultMutableTreeNode) queriesTreeModel.getRoot() );
	}


	/**
	 *   Call this method when you need to set the TreeModel for the queries
	 */
	public void setQueriesTreeModel( TreeModel tm ) {
		queriesTreeModel = tm;
	}

	/**
	 *   Call this method when you need to create a new TreeModel for the queries.
	 */
	public void createQueriesTreeModel() {
		setQueriesTreeModel( new DefaultTreeModel( new DefaultMutableTreeNode ( Settings.jpoResources.getString("queriesTreeModelRootNode") ) ) );
	}


	/**
	 *   Clear out the nodes in the exisitng queries Tree Model
	 */
	public void clearQueriesTreeModel() {
		((DefaultMutableTreeNode) queriesTreeModel.getRoot()).removeAllChildren();
	}

	/**
	 *   Call this method when you need to add a query to the tree model.
	 */
	public void addQueryToTreeModel( Query q ) {
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( q );
		getQueriesRootNode().add( newNode );
		( (DefaultTreeModel) queriesTreeModel ).nodesWereInserted( getQueriesRootNode(), new int[] { getQueriesRootNode().getIndex( newNode ) } );
	}



}
