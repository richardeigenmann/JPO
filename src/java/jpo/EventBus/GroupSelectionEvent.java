package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 *
 * @author Richard Eigenmann
 */
public class GroupSelectionEvent implements NodeSelectionEvent {

    private final SortableDefaultMutableTreeNode node;

    public GroupSelectionEvent( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
