package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * Request to indicate that the user would like bring up the Consolidate Group dialog
 * @author Richard eigenmann
 */
public class ConsolidateGroupRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * Request to indicate that the user would like to bring up the consolidate Group dialog
     * @param node The node for which the user would like the dialog to be done
     */
    public ConsolidateGroupRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * The node for which the dialog should be executed
     * @return the node
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
