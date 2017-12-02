package jpo.EventBus;

import java.io.File;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * Request to copy the pictures of the supplied nodes to the supplied directory
 *
 * @author Richard Eigenmann
 */
public class CopyToDirRequest implements Request {

    private final SortableDefaultMutableTreeNode[] nodes;
    private final File targetLocation;

    /**
     * Request to indicate that the user would like to copy the pictures in the
     * selected nodes to a target directory
     *
     * @param nodes The nodes for which the user would like copy the pictures
     * @param targetLocation The target directory
     */
    public CopyToDirRequest( SortableDefaultMutableTreeNode[] nodes, File targetLocation ) {
        this.nodes = nodes;
        this.targetLocation = targetLocation;
    }

    /**
     * The nodes for which the dialog should be executed
     *
     * @return the node
     */
    public SortableDefaultMutableTreeNode[] getNodes() {
        return nodes;
    }

    /**
     * Returns the target directory
     * @return the target directory
     */
    public File getTargetLocation() {
        return targetLocation;
    }

}
