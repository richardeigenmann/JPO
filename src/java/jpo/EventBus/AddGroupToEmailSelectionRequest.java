package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the group's child nodes should be added to the email selection
 * 
 * @author Richard eigenmann
 */
public class AddGroupToEmailSelectionRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * A request to add the child nodes of the group to the email selection
     * @param node The node to which the empty group should be added
     */
    public AddGroupToEmailSelectionRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * Returns the group node whose pictures are to be added
     * @return the Node with the group
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

    
}
