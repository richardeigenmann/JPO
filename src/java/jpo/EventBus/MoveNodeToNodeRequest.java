package jpo.EventBus;

import java.util.ArrayList;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * Request to indicate that the user would like move the supplied nodes as last
 * child on the target node
 *
 * @author Richard eigenmann
 */
public class MoveNodeToNodeRequest implements Request {

    private final ArrayList<SortableDefaultMutableTreeNode> movingNodes;
    private final SortableDefaultMutableTreeNode targetNode;

    /**
     * Request to indicate that the user would like move the supplied nodes as
     * last child on the target node
     *
     * @param movingNodes The nodes to move
     * @param targetNode the target node to which to add the node
     */
    public MoveNodeToNodeRequest( ArrayList<SortableDefaultMutableTreeNode> movingNodes, SortableDefaultMutableTreeNode targetNode ) {
        this.movingNodes = movingNodes;
        this.targetNode = targetNode;
    }

    /**
     * The node which should be moved
     *
     * @return the node
     */
    public ArrayList<SortableDefaultMutableTreeNode> getMovingNodes() {
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
