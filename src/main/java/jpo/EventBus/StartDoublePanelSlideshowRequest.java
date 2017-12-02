package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;


/**
 * The receiver of this request is supposed to spawn two panels and start a random slideshow
 * 
 * @author Richard Eigenmann
 */
public class StartDoublePanelSlideshowRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * A request to start a double panel slideshow
     * @param node The root node
     */
    public StartDoublePanelSlideshowRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * Returns the node for which the picture is to be shown.
     * @return the Node with the picture
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
