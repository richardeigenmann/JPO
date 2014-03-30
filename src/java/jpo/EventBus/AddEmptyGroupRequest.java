package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the user wants to add an empty group to the supplied node
 * 
 * @author Richard eigenmann
 */
public class AddEmptyGroupRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * A request to add an empty group to the supplied node
     * @param node The node to which the empty group should be added
     */
    public AddEmptyGroupRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * Returns the node to which the empty group should be added
     * @return the Node with the group
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

    
}
