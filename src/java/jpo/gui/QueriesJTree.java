package jpo.gui;

import jpo.dataModel.Settings;
import jpo.dataModel.QueryBrowser;
import jpo.*;
import jpo.dataModel.Query;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import jpo.dataModel.Tools;


/*
QueriesJTree.java:  Controller for the Searches JTree

Copyright (C) 2006 - 2009  Richard Eigenmann, Zurich, Switzerland
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
 *   Controller for the Searches JTree
 */
public class QueriesJTree
        extends JTree {

    /**
     * Constructs a JTree for the queries
     */
    public QueriesJTree() {
        Tools.checkEDT();
        getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        putClientProperty( "JTree.lineStyle", "Angled" );
        setOpaque( true );
        setEditable( false );
        setShowsRootHandles( true );
        setMinimumSize( Settings.jpoNavigatorJTabbedPaneMinimumSize );
        setModel( Settings.pictureCollection.getQueriesTreeModel() );

        //Add listener to components that can bring up groupPopupJPopupMenu menus.
        QueriesMouseAdapter mouseAdapter = new QueriesMouseAdapter();
        addMouseListener( mouseAdapter );

    }


    /**
     * Returns a view component with the JTree embedded in a JScrollpane
     * @return
     */
    public JComponent getJScrollPane() {
        return new JScrollPane( this );
    }


    /**
     *  Moves the highlighted row to the indicated one and expands the tree if necessary.
     *  Does not talk back to the collection controller as this should be called from the collection controller.
     *  @param node  The node which should be highlighted
     */
    public void setSelectedNode( final DefaultMutableTreeNode node ) {
        Tools.checkEDT();
        TreePath tp = new TreePath( node.getPath() );
        setSelectionPath( tp );
        scrollPathToVisible( tp );
    }

    /**
     *  subclass to deal with the Mouse events
     **/
    private class QueriesMouseAdapter
            extends MouseAdapter {

        /**
         *    If the mouse was clicked more than once using the left mouse button over a valid picture
         *    node then the picture editor is opened.
         */
        @Override
        public void mouseClicked( MouseEvent e ) {
            TreePath clickPath = getPathForLocation( e.getX(), e.getY() );
            if ( clickPath == null ) {
                return; // happens
            } // happens
            DefaultMutableTreeNode clickNode = (DefaultMutableTreeNode) clickPath.getLastPathComponent();

            if ( e.getClickCount() == 1 && ( !e.isPopupTrigger() ) ) {
                if ( ( clickNode == null ) || ( clickNode.getUserObject() == null ) || ( !( clickNode.getUserObject() instanceof Query ) ) ) {
                    return;
                }
                QueryBrowser queryBrowser = new QueryBrowser( (Query) clickNode.getUserObject() );
                Jpo.showThumbnails( queryBrowser );
            }
        }


        /**
         *   Override thge mousePressed event.
         */
        @Override
        public void mousePressed( MouseEvent e ) {
            //maybeShowPopup(e);
        }


        /**
         *  Override the mouseReleased event.
         */
        @Override
        public void mouseReleased( MouseEvent e ) {
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
