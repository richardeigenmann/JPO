package jpo.dataModel;

import java.io.File;
import java.util.Enumeration;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/*
NodeStatistics.java: A Class that counts nodes, groups, pictures and disk usage on the supplied node

Copyright (C) 2002 - 2009  Richard Eigenmann.
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
 * A Class that counts nodes, groups, pictures and disk usage on the supplied node
 * TODO: Make it a single pass statistic and cache the result. Perhaps listen to events on the node too.
 * @author Richard Eigenmann
 */
public class NodeStatistics {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( NodeStatistics.class.getName() );


    /**
     * Remebers the node for which stats are being collected
     */
    private DefaultMutableTreeNode myNode;


    /**
     * Constructs a NodeStatistics Object for the supplied node
     * @param nodeToAnalyse The node for which to perfrom the analysis
     */
    public NodeStatistics( DefaultMutableTreeNode nodeToAnalyse ) {
        if ( nodeToAnalyse != null ) {
            logger.fine( "new NodeStatistics on node: " + nodeToAnalyse.toString() );
        } else {
            logger.fine( "new NodeStatistics on null node" );
        }
        setNode( nodeToAnalyse );
    }


    /**
     * Sets the node to analysed
     * @param nodeToAnalyse
     */
    public void setNode( DefaultMutableTreeNode nodeToAnalyse ) {
        myNode = nodeToAnalyse;
    }


    /**
     * Returns the node being analysed.
     * @return the node being analysed
     */
    public DefaultMutableTreeNode getNode() {
        return myNode;
    }


    /**
     * Returns the number of nodes including the root node in the tree
     * @return The number of nodes including the root node
     */
    public int getNumberOfNodes() {
        int count = countNodes( myNode );
        logger.fine( String.format( "Number of nodes counted: %d", count ) );
        return count;
    }


    /**
     * Recursive static method that loops through the child nodes to find the
     * number of nodes. Returns 0 if the node is null.
     * @param start The node on which to start
     * @return The number of nodes including the root node or 0 if null is supplied.
     */
    private static int countNodes( TreeNode start ) {
        //never trust inputs
        if ( start == null ) {
            return 0;
        }

        int count = 1;
        TreeNode n;
        Enumeration nodes = start.children();
        while ( nodes.hasMoreElements() ) {
            n = (TreeNode) nodes.nextElement();
            if ( n.getChildCount() > 0 ) {
                count += countNodes( n );
            } else {
                count++;
            }
        }
        return count;
    }


    /**
     * Returns the number of nodes with the multilingual label in front of the number.
     * @return Returns the number of nodes with the multilingual label in front of the number
     */
    public String getNumberOfNodesString() {
        return Settings.jpoResources.getString( "CollectionNodeCountLabel" ) + Integer.toString( getNumberOfNodes() );
    }


    /**
     * Returns the number of groups in the supplied node.
     * @return Returns the number of groups in the supplied node.
     */
    public int getNumberOfGroups() {
        return countGroups( myNode );
    }


    /**
     * Recursive static method that counts the number of GroupInfo nodes underneath the start node.
     * @param startNode The node from which to start
     * @return the number of GroupInfo nodes underneath the start node.
     */
    private synchronized static int countGroups( TreeNode startNode ) {
        int count = 0;
        DefaultMutableTreeNode n;
        Enumeration nodes = startNode.children();
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
                // ignore if we get a cast error from the nextElement function;
            }
        }
        return count;
    }


    /**
     * Returns a multilingual label with the number of Groups
     * @return Returns a multilingual label with the number of Groups
     */
    public String getNumberOfGroupsString() {
        return Settings.jpoResources.getString( "CollectionGroupCountLabel" ) + Integer.toString( getNumberOfGroups() );
    }


    /**
     * Returns the number of Pictures found in the supplied node.
     * @return Returns the number of Pictures found in the supplied node.
     */
    public int getNumberOfPictures() {
        return countPictures( myNode, true );
    }


    /**
     * Returns the number of Pictures found in the supplied node prefixed with the multilingual label
     * @return Returns the number of Pictures found in the supplied node prefixed with the multilingual label
     */
    public String getNumberOfPicturesString() {
        return Settings.jpoResources.getString( "CollectionPictureCountLabel" ) + Integer.toString( getNumberOfPictures() );
    }


    /**
     *   Returns the number of PictureInfo Nodes in a subtree. Useful for progress monitors. If called with
     *   a null start node it returns 0. If called with a node that is actually a Query object it
     *   asks the Query for the count.
     *
     *   @param startNode	the node from which to count
     *   @param recurseSubgroups  indicator to say whether the next levels of groups should be counted too or not.
     *   @return the number of PictureInfo nodes
     */
    public synchronized static int countPictures( DefaultMutableTreeNode startNode, boolean recurseSubgroups ) {
        if ( startNode == null ) {
            return 0;
        }

        if ( startNode.getUserObject() instanceof Query ) {
            return ( (Query) startNode.getUserObject() ).getNumberOfResults();
        }

        int count = 0;
        DefaultMutableTreeNode n;
        Enumeration nodes = startNode.children();
        while ( nodes.hasMoreElements() ) {
            try {
                n = (DefaultMutableTreeNode) nodes.nextElement();
                if ( n.getUserObject() instanceof PictureInfo ) {
                    count++;
                }
                if ( recurseSubgroups && ( n.getChildCount() > 0 ) ) {
                    count += countPictures( n, true );
                }
            } catch ( ClassCastException ex ) {
                //ignore failing cast from nextElement()
            }
        }
        return count;
    }


    /**
     * Returns the bytes of the pictures underneath the supplied node
     * @return Returns the bytes of the pictures underneath the supplied node
     */
    public long getSizeOfPictures() {
        return sizeOfPicturesLong( myNode );
    }


    /**
     * Returns the bytes of the pictures underneath the supplied node as a String
     * @return Returns the bytes of the pictures underneath the supplied node
     */
    public String getSizeOfPicturesString() {
        return Settings.jpoResources.getString( "CollectionSizeJLabel" ) + Tools.fileSizeToString( getSizeOfPictures() );
    }


    /**
     * Returns the number of bytes the picture files in the subtree occupy.
     * If the node holds a query the query is enumerated.
     *
     * @param startNode   The node for which to add up the size of the pictures
     * @return  The number of bytes
     */
    private static long sizeOfPicturesLong( DefaultMutableTreeNode startNode ) {
        if ( startNode == null ) {
            return 0;
        }

        long size = 0;
        File testfile;
        DefaultMutableTreeNode n;


        if ( startNode.getUserObject() instanceof Query ) {
            Query q = (Query) startNode.getUserObject();
            for ( int i = 0; i < q.getNumberOfResults(); i++ ) {
                n = (DefaultMutableTreeNode) q.getIndex( i );
                if ( n.getUserObject() instanceof PictureInfo ) {
                    testfile = ( (PictureInfo) n.getUserObject() ).getHighresFile();
                    if ( testfile != null ) {
                        size += testfile.length();
                    }
                }
            }
        } else {
            Enumeration nodes = startNode.children();
            while ( nodes.hasMoreElements() ) {
                n = (DefaultMutableTreeNode) nodes.nextElement();
                if ( n.getUserObject() instanceof PictureInfo ) {
                    testfile = ( (PictureInfo) n.getUserObject() ).getHighresFile();
                    if ( testfile != null ) {
                        size += testfile.length();
                    }
                }
                if ( n.getChildCount() > 0 ) {
                    size += sizeOfPicturesLong( n );
                }
            }
        }

        return size;
    }
}
