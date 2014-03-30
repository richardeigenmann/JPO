package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the user wants to remove a specific node
 * 
 * @author Richard eigenmann
 */
public class RemoveNodeRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * This request indicates that the user wants to remove a specific node
     * @param node The node to remove from it's parent
     */
    public RemoveNodeRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * The node to remove from it's parent
     * @return the node
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

    
}
