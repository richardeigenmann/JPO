package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;



/**
 * The receiver of this request is supposed to spawn the Picture Info Editor
 * for the supplied node.
 * 
 * @author Richard Eigenmann
 */
public class ShowPictureInfoEditorRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * A request to bring up the editor for the supplied node
     * @param node The node with the picture to show
     */
    public ShowPictureInfoEditorRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * Returns the node for which the picture info editor is to be shown.
     * @return the Node with the picture
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
