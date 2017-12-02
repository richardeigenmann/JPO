package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the search dialog should be brought up
 *
 * @author Richard Eigenmann
 */
public class OpenSearchDialogRequest implements Request {

    private final SortableDefaultMutableTreeNode startNode;

    /**
     * A request to bring up the search dialog
     *
     * @param startNode The start node
     */
    public OpenSearchDialogRequest( SortableDefaultMutableTreeNode startNode  ) {
        this.startNode = startNode;
    }

    /**
     * Returns the start node for the search
     *
     * @return the start node
     */
    public SortableDefaultMutableTreeNode getStartNode() {
        return startNode;
    }

}
