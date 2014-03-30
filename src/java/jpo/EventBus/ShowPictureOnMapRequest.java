
package jpo.EventBus;


import jpo.dataModel.SortableDefaultMutableTreeNode;


/**
 * The receiver of this request is supposed to show a map with a teardrop for the location of the picture
 * 
 * @author Richard eigenmann
 */
public class ShowPictureOnMapRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * A request to show the supplied node in a a map
     * @param node The node with the picture to show on the map
     */
    public ShowPictureOnMapRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * Returns the node for which the map is to be shown.
     * @return the Node with the picture
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
