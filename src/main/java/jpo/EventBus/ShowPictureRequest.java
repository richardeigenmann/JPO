package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;


/**
 * The receiver of this request is supposed to spawn a full screen view of the picture 
 * under the supplied node.
 * 
 * @author Richard Eigenmann
 */
public class ShowPictureRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * A request to show the supplied node in a full screen view
     * @param node The node with the picture to show
     */
    public ShowPictureRequest( SortableDefaultMutableTreeNode node ) {
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
