package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the user wants to see a set of thumbnails
 * 
 * @author Richard eigenmann
 */
public class ShowGroupRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * A request to show the thumbnails of the group node
     * @param node The node with the thumbnails to show
     */
    public ShowGroupRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * Returns the node for which the thumbnails are to be shown.
     * @return the Node with the group
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
