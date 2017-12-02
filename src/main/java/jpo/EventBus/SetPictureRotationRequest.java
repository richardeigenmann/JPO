package jpo.EventBus;

import jpo.cache.ThumbnailQueueRequest.QUEUE_PRIORITY;
import jpo.dataModel.SortableDefaultMutableTreeNode;




/**
 * The receiver of this request is supposed to rotate the picture to the 0
 * rotation angle
 *
 * @author Richard Eigenmann
 */
public class SetPictureRotationRequest implements Request {

    private final SortableDefaultMutableTreeNode node;
    private final QUEUE_PRIORITY priority;
    private final double angle;

    /**
     * A request to rotate the picture to 0 degrees rotation
     *
     * @param node The node to rename
     * @param angle the angle
     * @param priority The queue priority
     */
    public SetPictureRotationRequest( SortableDefaultMutableTreeNode node, double angle, QUEUE_PRIORITY priority ) {
        this.node = node;
        this.angle = angle;
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
     * Returns the angle
     * @return the new angle
     */
    public double getAngle() {
        return angle;

    }

    /**
     * Returns the queue priority
     * @return the priority for the queue
     */
    public QUEUE_PRIORITY getPriority() {
        return priority;

    }

}
