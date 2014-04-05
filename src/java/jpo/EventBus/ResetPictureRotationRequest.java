package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * The receiver of this request is supposed to rotate the picture to the 0
 * rotation angle
 *
 * @author Richard eigenmann
 */
public class ResetPictureRotationRequest implements Request {

    private final SortableDefaultMutableTreeNode node;
    private final int priority;

    /**
     * A request to rotate the picture to 0 degrees rotation
     *
     * @param node The node to rename
     * @param priority The queue priority
     */
    public ResetPictureRotationRequest( SortableDefaultMutableTreeNode node, int priority ) {
        this.node = node;
        this.priority = priority;
    }

    /**
     * Returns the node to reset
     *
     * @return the Node to reset
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

    /**
     * Returns the queue priority
     */
    public int getPriority() {
        return priority;

    }

}
