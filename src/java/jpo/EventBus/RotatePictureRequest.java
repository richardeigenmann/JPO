package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.cache.ThumbnailQueueRequest.QUEUE_PRIORITY;

/*
 RotatePictureRequest: Request to rotate picture

 Copyright (C) 2015 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * The receiver of this request is supposed to rotate the picture of the node by the specified angle
 * 
 * @author Richard Eigenmann
 */
public class RotatePictureRequest implements Request {

    private final SortableDefaultMutableTreeNode node;
    private final double angle;
    private final QUEUE_PRIORITY priority;

    /**
     * A request to rotate the picture by the specified angle
     * @param node The node to rename
     * @param angle The angle in degrees for the rotation
     * @param priority The queue priority
     */
    public RotatePictureRequest( SortableDefaultMutableTreeNode node, double angle, QUEUE_PRIORITY priority ) {
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
    public QUEUE_PRIORITY getPriority() {
        return priority;
    }

}
