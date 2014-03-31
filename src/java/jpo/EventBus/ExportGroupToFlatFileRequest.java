package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * Request to indicate that the user would like to export the pictures to a flat file
 * @author Richard eigenmann
 */
public class ExportGroupToFlatFileRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * Request to indicate that the user would like to export the pictures to a flat file
     * @param node The node for which the user would like the dialog to be done
     */
    public ExportGroupToFlatFileRequest( SortableDefaultMutableTreeNode node ) {
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
