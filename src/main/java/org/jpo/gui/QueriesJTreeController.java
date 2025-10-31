package org.jpo.gui;

import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.Query;
import org.jpo.datamodel.Tools;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.ShowQueryRequest;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
 Copyright (C) 2006-2024 Richard Eigenmann, Zurich, Switzerland
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * Controller for the searches JTree
 *
 * @author Richard Eigenmann
 */
public class QueriesJTreeController {

    /**
     * The private reference to the JTree representing the collection
     */
    private final JTree queriesJTree = new JTree();



    /**
     * Constructs a JTree for the queries
     * @param pictureCollection The PictureCollection on which the tree will be working
     */
    public QueriesJTreeController(final PictureCollection pictureCollection) {
        queriesJTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        queriesJTree.putClientProperty("JTree.lineStyle", "Angled");
        queriesJTree.setOpaque(true);
        queriesJTree.setEditable(false);
        queriesJTree.setShowsRootHandles(true);
        queriesJTree.setMinimumSize(Settings.JPO_NAVIGATOR_JTABBEDPANE_MINIMUM_SIZE);
        if (pictureCollection != null) {
            queriesJTree.setModel(pictureCollection.getQueriesTreeModel());
        }

        queriesJTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final var clickPath = queriesJTree.getPathForLocation(e.getX(), e.getY());
                if (clickPath != null && e.getClickCount() == 1 && (!e.isPopupTrigger())) {
                    final var clickNode = (DefaultMutableTreeNode) clickPath.getLastPathComponent();
                    if ((clickNode != null) && (clickNode.getUserObject() != null) && (clickNode.getUserObject() instanceof Query query)) {
                        JpoEventBus.getInstance().post(new ShowQueryRequest(query));
                    }
                }
            }
        });
    }

    /**
     * The private reference to the JScrollPane that holds the JTree.
     */
    private final JScrollPane jScrollPane = new JScrollPane( queriesJTree );


    /**
     * Returns a new view component with the JTree embedded in a JScrollpane
     *
     * @return the view
     */
    public JComponent getJComponent() {
        return jScrollPane;
    }

    /**
     * Moves the highlighted row to the indicated one and expands the tree if
     * necessary. Does not talk back to the collection controller as this should
     * be called from the collection controller.
     *
     * @param node The node which should be highlighted
     */
    public void setSelectedNode(final DefaultMutableTreeNode node) {
        Tools.checkEDT();
        var treePath = new TreePath(node.getPath());
        queriesJTree.setSelectionPath(treePath);
        queriesJTree.scrollPathToVisible(treePath);
    }
}
