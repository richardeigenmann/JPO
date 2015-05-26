package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the user wants to edit the group as a table
 * 
 * @author Richard Eigenmann
 */
public class ShowGroupAsTableRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * A request to show the group as a table
     * @param node The node with the thumbnails to show
     */
    public ShowGroupAsTableRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * Returns the node for which the group is to be shown
     * @return the Node with the group
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
