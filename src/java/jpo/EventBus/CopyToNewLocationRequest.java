package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * Request to copy the pictures of the supplied nodes to a directory to be 
 * picked from a Filechooser
 *
 * @author Richard Eigenmann
 */
public class CopyToNewLocationRequest implements Request {

    private final SortableDefaultMutableTreeNode[] nodes;

    /**
     * Request to indicate that the user would like to copy the pictures in the selected nodes to a target directory
     *
     * @param nodes The nodes for which the user would like copy the pictures
     */
    public CopyToNewLocationRequest( SortableDefaultMutableTreeNode[] nodes ) {
        this.nodes = nodes;
    }

    /**
     * The nodes for which the dialog should be executed
     *
     * @return the node
     */
    public SortableDefaultMutableTreeNode[] getNodes() {
        return nodes;
    }

    
    
}
