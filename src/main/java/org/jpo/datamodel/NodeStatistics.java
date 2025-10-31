package org.jpo.datamodel;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jpo.gui.JpoResources;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.File;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2002-2025 Richard Eigenmann.
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
 * A Class that counts nodes, groups, pictures and disk usage on the supplied
 * node
 *
 * @author Richard Eigenmann
 */
public class NodeStatistics {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( NodeStatistics.class.getName() );
    /**
     * Remembers the node for which stats are being collected
     */
    private DefaultMutableTreeNode myNode;

    /**
     * Constructs a NodeStatistics Object for the supplied node
     *
     * @param nodeToAnalyse The node for which to perform the analysis
     */
    public NodeStatistics( DefaultMutableTreeNode nodeToAnalyse ) {
        setNode( nodeToAnalyse );
    }

    /**
     * Sets the node to be analysed
     *
     * @param nodeToAnalyse the nodes
     */
    public final void setNode( DefaultMutableTreeNode nodeToAnalyse ) {
        myNode = nodeToAnalyse;
    }

    /**
     * Returns the node being analysed.
     *
     * @return the node being analysed
     */
    public DefaultMutableTreeNode getNode() {
        return myNode;
    }

    /**
     * Returns the number of nodes including the root node in the tree
     *
     * @return The number of nodes including the root node
     */
    public int getNumberOfNodes() {
        if (myNode == null) {
            return 0;
        }
        int count = countNodes( myNode );
        LOGGER.log(Level.FINE,"Number of nodes counted: {0}", count );
        return count;
    }

    /**
     * Recursive static method that loops through the child nodes to find the
     * number of nodes. Returns 0 if the node is null.
     *
     * @param start The node on which to start
     * @return The number of nodes including the root node or 0 if null is
     * supplied.
     */
    public static int countNodes( @NotNull TreeNode start ) {
        Tools.warnOnEDT();

        int count = 1;
        TreeNode n;
        final Enumeration<? extends TreeNode> nodes = start.children();
        while ( nodes.hasMoreElements() ) {
            n = nodes.nextElement();
            if ( n.getChildCount() > 0 ) {
                count += countNodes( n );
            } else {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the number of nodes with the multilingual label in front of the
     * number.
     *
     * @return Returns the number of nodes with the multilingual label in front
     * of the number
     */
    public String getNumberOfNodesString() {
        return JpoResources.getResource("CollectionNodeCountLabel") + getNumberOfNodes();
    }

    /**
     * Returns the number of groups in the supplied node.
     *
     * @return Returns the number of groups in the supplied node.
     */
    public int getNumberOfGroups() {
        if (myNode == null) {
            return 0;
        }
        return countGroups( myNode );
    }

    /**
     * Recursive static method that counts the number of GroupInfo nodes
     * underneath the start node.
     *
     * @param startNode The node from which to start
     * @return the number of GroupInfo nodes underneath the start node.
     */
    private static synchronized  int countGroups( TreeNode startNode ) {
        Tools.warnOnEDT();
        int count = 0;
        DefaultMutableTreeNode n;
        final Enumeration<? extends TreeNode> nodes = startNode.children();
        while ( nodes.hasMoreElements() ) {
            try {
                n = (DefaultMutableTreeNode) nodes.nextElement();
                if ( n.getUserObject() instanceof GroupInfo ) {
                    count++;
                }
                if ( n.getChildCount() > 0 ) {
                    count += countGroups( n );
                }
            } catch ( ClassCastException ex ) {
                // ignore if we get a cast error from the nextElement function
            }
        }
        return count;
    }

    /**
     * Returns a multilingual label with the number of Groups
     *
     * @return Returns a multilingual label with the number of Groups
     */
    public String getNumberOfGroupsString() {
        return JpoResources.getResource("CollectionGroupCountLabel") + getNumberOfGroups();
    }

    /**
     * Returns the number of Pictures found in the supplied node.
     *
     * @return Returns the number of Pictures found in the supplied node.
     */
    public int getNumberOfPictures() {
        return countPictures( myNode, true );
    }

    /**
     * Returns the number of Pictures found in the supplied node prefixed with
     * the multilingual label
     *
     * @return Returns the number of Pictures found in the supplied node
     * prefixed with the multilingual label
     */
    public String getNumberOfPicturesString() {
        return JpoResources.getResource("CollectionPictureCountLabel") + getNumberOfPictures();
    }

    /**
     * Returns the number of PictureInfo nodes in a subtree recursing through
     * the Groups.
     *
     * @param startNode The Start node
     * @return The number of PictureInfo nodes
     */
    public static int countPicturesRecursively( DefaultMutableTreeNode startNode ) {
        return countPictures( startNode, true );
    }

    /**
     * Returns the number of PictureInfo Nodes in a subtree. Useful for progress
     * monitors. If called with a null start node it returns 0. If called with a
     * node that is actually a Query object it asks the Query for the count.
     *
     * @param startNode        the node from which to count
     * @param recurseSubgroups indicator to say whether the next levels of
     *                         groups should be counted too or not.
     * @return the number of PictureInfo nodes
     */
    public static synchronized int countPictures(final DefaultMutableTreeNode startNode, boolean recurseSubgroups) {
        if (startNode == null) {
            return 0;
        }

        if (startNode.getUserObject() instanceof Query query) {
            return query.getNumberOfResults();
        }

        int count = 0;
        Object nextElement;
        final Enumeration<? extends TreeNode> nodes = startNode.children();
        while (nodes.hasMoreElements()) {
            nextElement = nodes.nextElement();
            if (nextElement instanceof DefaultMutableTreeNode node) {
                if (node.getUserObject() instanceof PictureInfo) {
                    count++;
                }
                if (recurseSubgroups && (node.getChildCount() > 0)) {
                    count += countPictures(node, true);
                }
            }
        }
        return count;
    }

    /**
     * Returns the bytes of the pictures underneath the supplied node
     *
     * @return Returns the bytes of the pictures underneath the supplied node
     */
    public long getSizeOfPictures() {
        return sizeOfPicturesLong( myNode );
    }

    /**
     * Returns the bytes of the pictures underneath the supplied node as a
     * String
     *
     * @return Returns the bytes of the pictures underneath the supplied node
     */
    public String getSizeOfPicturesString() {
        return JpoResources.getResource("CollectionSizeJLabel") + FileUtils.byteCountToDisplaySize( getSizeOfPictures());
    }

    /**
     * Returns the number of bytes the picture files in the subtree occupy. If
     * the node holds a query the query is enumerated.
     *
     * @param startNode The node for which to add up the size of the pictures
     * @return The number of bytes
     */
    private static long sizeOfPicturesLong(final DefaultMutableTreeNode startNode) {
        Tools.warnOnEDT(); // really?

        long size = 0;
        DefaultMutableTreeNode n;

        if (startNode.getUserObject() instanceof Query q) {
            for (int i = 0; i < q.getNumberOfResults(); i++) {
                n = q.getIndex(i);
                if (n.getUserObject() instanceof PictureInfo pi) {
                    size += sizeOfPictureInfo(pi);
                }
            }
        } else if (startNode.getUserObject() instanceof PictureInfo pi) {
            size = sizeOfPictureInfo(pi);
        } else if (startNode instanceof SortableDefaultMutableTreeNode node && startNode.getUserObject() instanceof GroupInfo) {
            size = node.getChildPictureNodesDFS()
                    .mapToLong(nde -> sizeOfPictureInfo((PictureInfo) nde.getUserObject()))
                    .sum();
        }
        return size;
    }

    /**
     * Returns the number of bytes in the pictureInfo object
     *
     * @param pictureInfo The PictureInfo to query
     * @return The number of bytes
     */
    private static long sizeOfPictureInfo(final PictureInfo pictureInfo) {
        final File testfile = (pictureInfo.getImageFile());
        if (testfile != null) {
            return testfile.length();
        }
        return 0;
    }
}
