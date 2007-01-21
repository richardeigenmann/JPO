package jpo;


/**
 *  This class fills in the abstract methods so that we can have a object of the ThumbnailBrowser
 *  type to kick around the RelayoutListener
 */
public class MockThumbnailBrowser extends ThumbnailBrowser {
    
    
    public String getTitle() {
        return "Dummy Title";
    }
    
    
    public int getNumberOfNodes() {
        return 0;
    }
    
    public SortableDefaultMutableTreeNode getNode( int index ) {
        return new SortableDefaultMutableTreeNode();
    }
}
