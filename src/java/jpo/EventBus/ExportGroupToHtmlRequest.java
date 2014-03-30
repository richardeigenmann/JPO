package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * Request to indicate that the user would like to see the Export to HTML wizard
 * @author Richard eigenmann
 */
public class ExportGroupToHtmlRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * Request to indicate that the user would like to see the Export to HTML wizard
     * @param node The node for which the user would like the dialog to be done
     */
    public ExportGroupToHtmlRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * The node for which the dialog should be executed
     * @return 
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
