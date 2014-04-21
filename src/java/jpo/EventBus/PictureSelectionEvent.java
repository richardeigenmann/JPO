package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * An event to indicate that a picture was selected
 * @author Richard eigenmann
 */
public class PictureSelectionEvent implements NodeSelectionEvent {

    private final SortableDefaultMutableTreeNode node;

    /**
     * Creates an event to indicate a picture was selected
     * @param node the node that was selected
     */
    public PictureSelectionEvent( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * Returns the node that was selected
     * @return the selected node
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
