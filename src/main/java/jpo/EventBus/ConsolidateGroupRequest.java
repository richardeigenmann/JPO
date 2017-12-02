package jpo.EventBus;

import java.io.File;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * Request to indicate that the user would like bring up the Consolidate Group
 * dialog
 *
 * @author Richard Eigenmann
 */
public class ConsolidateGroupRequest implements Request {

    private final SortableDefaultMutableTreeNode node;
    private final File targetDir;

    /**
     * Request to indicate that the user would like to bring up the consolidate
     * Group dialog
     *
     * @param node The node for which the user would like the dialog to be done
     * @param targetDir the target directory. 
     */
    public ConsolidateGroupRequest( SortableDefaultMutableTreeNode node, File targetDir ) {
        this.node = node;
        this.targetDir = targetDir;
    }

    /**
     * The node for which the dialog should be executed
     *
     * @return the node
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

    /**
     * The target dir for the operation
     *
     * @return the target directory
     */
    public File getTargetDir() {
        return targetDir;
    }
    
    
}
