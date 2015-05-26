package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;


/**
 * The receiver of this request is supposed to rotate the picture of the node by the specified angle
 * 
 * @author Richard Eigenmann
 */
public class RotatePictureRequest implements Request {

    private final SortableDefaultMutableTreeNode node;
    private final double angle;
    private final int priority;

    /**
     * A request to rotate the picture by the specified angle
     * @param node The node to rename
     * @param angle The angle in degrees for the rotation
     * @param priority The queue priority
     */
    public RotatePictureRequest( SortableDefaultMutableTreeNode node, double angle, int priority ) {
        this.node = node;
        this.angle = angle;
        this.priority = priority;
    }

    /**
     * Returns the node with the picture to be rotated
     * @return the Node with the picture
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }
    
    /**
     * Returns the angle in degrees that the picture is to be rotated by
     * @return  the angle
     */
    public double getAngle() {
        return angle;
    }
    
    
    /**
     * Returns the queue priority
     * @return the queue priority
     */
    public int getPriority() {
        return priority;
    }

}
