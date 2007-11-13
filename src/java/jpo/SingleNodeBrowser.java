package jpo;

/*
SingleNodeBrower.java:  an implementation of the ThumbnailBrowserInterface for "browsing" a single picture.
 
Copyright (C) 2006-2007  Richard Eigenmann, Zürich, Switzerland
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
 *  This class implements the ThumbnailBrowserInterface in the specific manner that is required for
 *  displaying a single picture in the ThumbnailJScrollPane.
 */

public class SingleNodeBrowser extends ThumbnailBrowser {
    
    /**
     *  Constructor for a SingleNodeBrowser.
     *
     *  @param singleNode    The Node which is to be "browsed".
     */
    SingleNodeBrowser( SortableDefaultMutableTreeNode singleNode ) {
        //Tools.log("SingleNodeBrowser: constructor called on node: " + singleNode.toString() );
        this.singleNode = singleNode;
    }
    
    /**
     *  A reference to the node for which this SingleNodeBrowser was created.
     */
    private SortableDefaultMutableTreeNode singleNode = null;
    
    
    /**
     *  returns the string Sequential
     */
    public String getTitle() {
        if ( ( singleNode != null )
        && ( singleNode.getUserObject() instanceof PictureInfo ) ) {
            return ( (PictureInfo) singleNode.getUserObject() ).getDescription();
        } else {
            return "Single picture";
        }
    }
    
    
    /**
     *  Returns the number of pictures in this group. The number is 0 to the number of pictures minus 1
     *  because counting starts at 0. So 3 nodes in the group returns 2 meaning node0, node1, node2
     */
    public int getNumberOfNodes() {
        //Tools.log("SingleNodeBrowser.getNumberOfNodes: returning: 0");
        return 0;
    }
    
    
    
    /**
     *  This method returns the node for the indicated position in the group.
     *
     *  @param index   The component index that is to be returned. The number is from 0 to
     *                 {@link #getNumberOfNodes}. If there are 3 nodes request getNode(0),
     *                 getNode(1) and getNode(2).
     */
    public SortableDefaultMutableTreeNode getNode( int index ) {
        //Tools.log("SingleNodeBrowser.getNode: requested for node: " + Integer.toString( index ) );
        return singleNode;
    }
    
    
    
    /**
     *  This method unregisters the TreeModelListener and sets the variables to null;
     */
    public void cleanup() {
        super.cleanup();
        singleNode = null;
    }
    
    
    
    
    
    
    
}
