package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * Request to indicate that the user would like to export the pictures to a new collection
 * @author Richard eigenmann
 */
public class ExportGroupToNewCollectionRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * Request to indicate that the user would like to export the pictures to a new collection
     * @param node The node for which the user would like the dialog to be done
     */
    public ExportGroupToNewCollectionRequest( SortableDefaultMutableTreeNode node ) {
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
