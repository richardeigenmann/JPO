package jpo.EventBus;

import java.io.File;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * Request to copy the pictures of the supplied nodes to the supplied zipfile
 *
 * @author Richard Eigenmann
 */
public class CopyToZipfileRequest implements Request {

    private final SortableDefaultMutableTreeNode[] nodes;
    private final File targetZipfile;

    /**
     * Request to indicate that the user would like to copy the pictures in the
     * selected nodes to a target zipfile
     *
     * @param nodes The nodes for which the user would like copy the pictures
     * @param targetZipfile the target zipfile
     */
    public CopyToZipfileRequest( SortableDefaultMutableTreeNode[] nodes, File targetZipfile ) {
        this.nodes = nodes;
        this.targetZipfile = targetZipfile;
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
     * @return the target zipfile
     */
    public File getTargetZipfile() {
        return targetZipfile;
    }

}
