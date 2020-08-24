package org.jpo.eventbus;

import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import java.util.List;

/**
 * Request to indicate that the user would like move the supplied nodes as last
 * child on the target node
 *
 * @author Richard Eigenmann
 */
public class MoveNodeToNodeRequest implements Request {

    private final List<SortableDefaultMutableTreeNode> movingNodes;
    private final SortableDefaultMutableTreeNode targetNode;

    /**
     * Request to indicate that the user would like move the supplied nodes as
     * last child on the target node
     *
     * @param movingNodes The nodes to move
     * @param targetNode the target node to which to add the node
     */
    public MoveNodeToNodeRequest( List<SortableDefaultMutableTreeNode> movingNodes, SortableDefaultMutableTreeNode targetNode ) {
        this.movingNodes = movingNodes;
        this.targetNode = targetNode;
    }

    /**
     * The node which should be moved
     *
     * @return the node
     */
    public List<SortableDefaultMutableTreeNode> getMovingNodes() {
        return movingNodes;
    }

    /**
     * The target node to which the node should be added as the last child
     *
     * @return the node
     */
    public SortableDefaultMutableTreeNode getTargetNode() {
        return targetNode;
    }

}
