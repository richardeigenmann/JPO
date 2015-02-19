package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the user wants to delete the file of a specific node and remove the node
 *
 * @author Richard eigenmann
 */
public class DeleteNodeFileRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * This request indicates that the user wants to delete the file of a specific node
     *
     * @param nodes The node for which the file and the node are to be deleted
     */
    public DeleteNodeFileRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * The node to delete
     *
     * @return the node
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
