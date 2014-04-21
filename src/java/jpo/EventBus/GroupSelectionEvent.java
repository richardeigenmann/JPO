package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * An event to indicate that a group node was selected
 *
 * @author Richard Eigenmann
 */
public class GroupSelectionEvent implements NodeSelectionEvent {

    private final SortableDefaultMutableTreeNode node;

    /**
     * Constructor for the event
     *
     * @param node the node which was selected
     */
    public GroupSelectionEvent( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * Returns the node that was selected
     *
     * @return the node that was selected
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
