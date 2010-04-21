package jpo.dataModel;

import java.lang.reflect.InvocationTargetException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import jpo.gui.ThumbnailController;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 *
 * @author Richard Eigenmann
 */
public class PictureInfoChangeListenerTest
        extends TestCase {

    public PictureInfoChangeListenerTest( String testName ) {
        super( testName );
    }


    /**
     * Check how the Picture Listeners add and remove
     * A new PictureInfo has no Listeners
     * Then we add it to a node it should have one
     * Then we put the node onto a thumbnail we should have two
     * We then put a different node on the thumbnail the original PictureInfo we should have one listener
     * If the node takes on a different Picture the listeners must be all gone
     */
    public void testPictureListenerAddAndRemove() {
        PictureInfo pi = new PictureInfo();
        Vector changeListeners = pi.getPictureInfoListeners();
        assertTrue( "Verify that there is no Listerner to start off with", changeListeners.isEmpty() );

        SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode( pi );
        assertEquals( "The PictureInfo should now have one change listener from the SDMTN", 1, changeListeners.size() );

        ThumbnailController tc1 = getNewThumbnailController();

        SingleNodeNavigator snb = new SingleNodeNavigator( node );
        tc1.setNode( snb, 0 );
        assertEquals( "The PictureInfo should now have 2 change listeners", 2, changeListeners.size() );

        SortableDefaultMutableTreeNode differentNode = new SortableDefaultMutableTreeNode( new PictureInfo() );
        SingleNodeNavigator snb2 = new SingleNodeNavigator( differentNode );
        tc1.setNode( snb2, 0 );
        assertEquals( "The PictureInfo should now have 1 change listeners", 1, changeListeners.size() );

        node.setUserObject( new PictureInfo() );
        tc1.setNode( snb, 0 );
        assertTrue( "Verify that there is no Listerner after the node has been set to another object", changeListeners.isEmpty() );
        tc1 = null;
    }


    /**
     * Check the PictureListeners some more
     * A new PictureInfo has no listeners
     * After adding it to a node it has one listener
     * After adding the node to a Thumbnail it has one listener
     * Now we replace the PictureInfo on the node
     * The node should have remove itself from the the PictureInfo and attach
     * The Thumbnail should unattach itself from the PictureInfo and attach itself to the new PictureInfo
     *
     */
    public void testPictureListenerAddAndRemove2() {
        PictureInfo pi = new PictureInfo();
        Vector changeListeners = pi.getPictureInfoListeners();
        assertTrue( "Verify that there is no Listerner to start off with", changeListeners.isEmpty() );

        SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode( pi );
        assertEquals( "The PictureInfo should now have one change listener from the SDMTN", 1, changeListeners.size() );

        ThumbnailController tc2 = getNewThumbnailController();

        SingleNodeNavigator snb = new SingleNodeNavigator( node );
        tc2.setNode( snb, 0 );
        assertEquals( "The PictureInfo should now have 2 change listeners", 2, changeListeners.size() );

        PictureInfo pi2 = new PictureInfo();
        node.setUserObject( pi2 );
        assertEquals( "The PictureInfo should now have 0 change listeners", 0, changeListeners.size() );

        tc2 = null;
    }

    // helps with inner class
    ThumbnailController tc;


    /**
     * helps to get a ThumbnailController from the EDT
     * @return The ThumbnailController
     */
    private ThumbnailController getNewThumbnailController() {
        // create the Thumbnail Controller on EDT
        Runnable r = new Runnable() {

            public void run() {
                tc = new ThumbnailController( 350 );
            }
        };
        try {
            SwingUtilities.invokeAndWait( r );
        } catch ( InterruptedException ex ) {
            Logger.getLogger( PictureInfoChangeListenerTest.class.getName() ).log( Level.SEVERE, null, ex );
            throw new AssertionFailedError( "Got an interrupted exception instead of a ThumbnailController" );
        } catch ( InvocationTargetException ex ) {
            Logger.getLogger( PictureInfoChangeListenerTest.class.getName() ).log( Level.SEVERE, null, ex );
            throw new AssertionFailedError( "Got an exception instead of a ThumbnailController" );
        }
        return tc;
    }
}
