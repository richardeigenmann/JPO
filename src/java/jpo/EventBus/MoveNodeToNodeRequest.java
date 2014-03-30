package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * Request to indicate that the user would like move the supplied node as last
 * child on the target node
 *
 * @author Richard eigenmann
 */
public class MoveNodeToNodeRequest implements Request {

    private final SortableDefaultMutableTreeNode movingNode;
    private final SortableDefaultMutableTreeNode targetNode;

    /**
     * Request to indicate that the user would like move the supplied node as
     * last child on the target node
     *
     * @param movingNode The node to move
     * @param targetNode the target node to which to add the node
     */
    public MoveNodeToNodeRequest( SortableDefaultMutableTreeNode movingNode, SortableDefaultMutableTreeNode targetNode ) {
        this.movingNode = movingNode;
        this.targetNode = targetNode;
    }

    /**
     * The node which should be moved
     *
     * @return
     */
    public SortableDefaultMutableTreeNode getMovingNode() {
        return movingNode;
    }

    /**
     * The target node to which the node should be added as the last child
     *
     * @return
     */
    public SortableDefaultMutableTreeNode getTargetNode() {
        return targetNode;
    }

}
