package jpo.gui.swing;

import jpo.dataModel.Settings;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import jpo.dataModel.Tools;

/*
CollectionJTree.java:  class that creates a JTree for the collection

Copyright (C) 2002 - 2010  Richard Eigenmann, Zurich, Switzerland
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
 *  This is the View object of the CollectionJTreeController.
 *  All it can do is display the nodes of the data model and add a non standard set of icons depending on
 *  the userObject in the TreeNodes.
 */
public class CollectionJTree
        extends JTree {

    /**
     * Constructor for the CollectionJTree, sets styles, cellrenderer, minimum size.
     */
    public CollectionJTree() {
        Tools.checkEDT();
        putClientProperty( "JTree.lineStyle", "Angled" );
        setShowsRootHandles( true );
        setOpaque( true );
        setBackground( Settings.JPO_BACKGROUND_COLOR );
        setMinimumSize( Settings.jpoNavigatorJTabbedPaneMinimumSize );

        final DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {

            /**
             *  Overridden method that sets the icon in the JTree to either a
             *  {@link #CLOSED_FOLDER_ICON} or a {@link #OPEN_FOLDER_ICON} or a
             *  {@link #PICTURE_ICON} depending on what sort of userObject
             *  the SortableDefaultMutableTreeNode is carrying and the expansion state
             *  of the node.
             *  First we let the super implementation give us the component.
             *  Then we look at the userObject and it's class and figure
             *  out what sort of icon to give it.
             */
            @Override
            public Component getTreeCellRendererComponent(
                    JTree tree,
                    Object value,
                    boolean sel,
                    boolean expanded,
                    boolean leaf,
                    int row,
                    boolean hasFocus ) {
                Object userObject = ( (DefaultMutableTreeNode) value ).getUserObject();
                super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus );
                if ( userObject instanceof PictureInfo ) {
                    setIcon( PICTURE_ICON );
                } else if ( userObject instanceof GroupInfo ) {
                    if ( expanded ) {
                        setIcon( OPEN_FOLDER_ICON );
                    } else {
                        setIcon( CLOSED_FOLDER_ICON );
                        //else let the L&F take over
                    }
                }
                //else let the L&F take over

                return this;
            }
        };

        TreeCellEditor localCellEditor = new DefaultCellEditor( new JTextField() );
        TreeCellEditor treeCellEditor = new DefaultTreeCellEditor( this, renderer, localCellEditor ) {

            /**
             *  This solution to the bug 4745084 found on
             *  http://forum.java.sun.com/thread.jspa?threadID=196868&start=15&tstart=0
             *  The problem is that when you hit F2 to edit the field without this override you
             *  fall back on the default icon set.
             */
            @Override
            protected void determineOffset( JTree tree, Object value,
                    boolean isSelected, boolean isExpanded, boolean isLeaf,
                    int row ) {
                super.determineOffset( tree, value, isSelected, isExpanded, isLeaf, row );
                Component rendererComponent = super.renderer.getTreeCellRendererComponent( tree, value, isSelected, isExpanded, isLeaf, row, true );
                if ( rendererComponent instanceof JLabel ) {
                    super.editingIcon = ( (JLabel) rendererComponent ).getIcon();
                }
            }
        };
        setCellRenderer( renderer );
        setCellEditor( treeCellEditor );

    }


    @Override
    public JToolTip createToolTip() {
        MultiLineToolTip tip = new MultiLineToolTip();
        tip.setComponent( this );
        return tip;
    }

    /**
     *  Icon of a closed folder to be used on groups that are not expanded in the JTree.
     */
    private static final ImageIcon CLOSED_FOLDER_ICON = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_folder_closed.gif" ) );

    /**
     *  Icon of an open folder to be used on groups that are expanded in the JTree.
     */
    private static final ImageIcon OPEN_FOLDER_ICON = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_folder_open.gif" ) );

    /**
     *  Icon of a picture for use on picture bearing nodes in the JTree.
     */
    private static final ImageIcon PICTURE_ICON = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_picture.gif" ) );
}
