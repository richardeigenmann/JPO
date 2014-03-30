package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 *
 * @author Richard eigenmann
 */
public class PictureSelectionEvent implements NodeSelectionEvent {

    private final SortableDefaultMutableTreeNode node;

    public PictureSelectionEvent( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
