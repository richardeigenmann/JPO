package jpo.dataModel;

import java.util.Vector;
import jpo.gui.SingleNodeBrowser;
import jpo.gui.Thumbnail;
import junit.framework.TestCase;

/**
 *
 * @author richi
 */
public class PictureInfoChangeListenerTest extends TestCase {

    public PictureInfoChangeListenerTest( String testName ) {
        super( testName );
    }


    /**
     * Test a Memory Leak scenaro
     */
    public void testPictureListenerAddAndRemove() {
        PictureInfo pi = new PictureInfo();
        Vector changeListeners = pi.getPictureInfoListeners();
        assertTrue( "Verify that there is no Listerner to start off with", changeListeners.isEmpty() );

        SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode( pi );
        Thumbnail t = new Thumbnail( 350 );
        SingleNodeBrowser snb = new SingleNodeBrowser( node );
        t.setNode( snb, 0 );
        changeListeners = pi.getPictureInfoListeners();
        assertEquals( "The PictureInfo should now have one change listener", 1, changeListeners.size());

        node.setUserObject( new PictureInfo()) ;
        t.setNode( snb, 0 );
        changeListeners = pi.getPictureInfoListeners();
        assertTrue( "Verify that there is no Listerner after the node has been set to another object", changeListeners.isEmpty() );
    }

    /**
     * Test another Memory Leak scenaro
     */
    public void testPictureListenerAddAndRemove2() {
        PictureInfo pi = new PictureInfo();
        Vector changeListeners = pi.getPictureInfoListeners();
        assertTrue( "Verify that there is no Listerner to start off with", changeListeners.isEmpty() );

        SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode( pi );
        Thumbnail t = new Thumbnail( 350 );
        SingleNodeBrowser snb = new SingleNodeBrowser( node );
        t.setNode( snb, 0 );
        changeListeners = pi.getPictureInfoListeners();
        assertEquals( "The PictureInfo should now have one change listener", 1, changeListeners.size());

        SortableDefaultMutableTreeNode node2 = new SortableDefaultMutableTreeNode( new PictureInfo() )  ;
        snb = new SingleNodeBrowser( node2 );
        t.setNode( snb, 0 );
        changeListeners = pi.getPictureInfoListeners();
        assertTrue( "Verify that there is no Listerner after the Thumbnail has been set to another node", changeListeners.isEmpty() );
    }


}
