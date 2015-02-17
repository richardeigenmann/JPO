package jpo.gui.swing;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.SingleNodeNavigator;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import junit.framework.TestCase;

/**
 * Tests for the GroupPopupMenu Class
 *
 * @author Richard Eigenmann
 */
public class PicturePopupMenuTest extends TestCase {

    /**
     * Constructor for the tests.
     *
     * Note these tests are burdened with reflection to get at the inner
     * workings of the popup menu. Should I open up the fields in the popup menu
     * class? I think not because other classes don't need to see into the inner
     * workings of the popup menu. With the exception of this one that has to
     * make sure the details of the class are working properly.
     *
     * @param testName test name
     */
    public PicturePopupMenuTest( String testName ) {
        super( testName );
    }

    protected PictureInfo myPictureInfo = new PictureInfo();

    {
        myPictureInfo.setDescription( "My Picture" );
    }
    protected SortableDefaultMutableTreeNode myNode = new SortableDefaultMutableTreeNode( myPictureInfo );
    protected SingleNodeNavigator myNavigator = new SingleNodeNavigator( myNode );
    protected PicturePopupMenu myPicturePopupMenu;

    protected JMenuItem title;
    protected JMenuItem showPicture;
    protected JMenuItem showMap;
    protected JMenu navigateTo;
    protected JMenuItem categories;
    protected JMenuItem selectForEmail;
    protected JMenu userFunction;
    protected JMenu rotation;
    protected JMenuItem rotate90;
    protected JMenuItem rotate180;
    protected JMenuItem rotate270;
    protected JMenuItem rotate0;
    protected JMenuItem refreshThumbnail;
    protected JMenu move;
    protected JMenu copyImage;
    protected JMenuItem copyImageChooseTargetDir;
    protected JMenuItem copyImageToZipFile;
    protected JMenuItem removeNode;
    protected JMenu fileOperations;
    protected JMenuItem properties;
    protected JMenuItem consolidateHere;

    /**
     * Creates the objects for testing. Runs on the EDT.
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        myPicturePopupMenu = new PicturePopupMenu( myNavigator, 0 );

        Runnable r = new Runnable() {

            @Override
            public void run() {
                title = (JMenuItem) myPicturePopupMenu.getComponent( 0 );
                showPicture = (JMenuItem) myPicturePopupMenu.getComponent( 2 );
                showMap = (JMenuItem) myPicturePopupMenu.getComponent( 3 );
                navigateTo = (JMenu) myPicturePopupMenu.getComponent( 4 );
                categories = (JMenuItem) myPicturePopupMenu.getComponent( 5 );
                selectForEmail = (JMenuItem) myPicturePopupMenu.getComponent( 6 );
                userFunction = (JMenu) myPicturePopupMenu.getComponent( 7 );
                rotation = (JMenu) myPicturePopupMenu.getComponent( 8 );
                rotate90 = rotation.getItem( 0 );
                rotate180 = rotation.getItem( 1 );
                rotate270 = rotation.getItem( 2 );
                rotate0 = rotation.getItem( 3 );
                refreshThumbnail = (JMenuItem) myPicturePopupMenu.getComponent( 9 );
                move = (JMenu) myPicturePopupMenu.getComponent( 10 );
                copyImage = (JMenu) myPicturePopupMenu.getComponent( 11 );
                copyImageChooseTargetDir = copyImage.getItem( 0 );
                copyImageToZipFile = copyImage.getItem( 12 );
                removeNode = (JMenuItem) myPicturePopupMenu.getComponent( 12 );
                fileOperations = (JMenu) myPicturePopupMenu.getComponent( 13 );
                properties = (JMenuItem) myPicturePopupMenu.getComponent( 14 );
                consolidateHere = (JMenuItem) myPicturePopupMenu.getComponent( 15 );
            }
        };
        SwingUtilities.invokeAndWait( r );

    }

    /**
     * Test that out Group Node was created for the correct node.
     */
    public void testRememberingPopupNode() {
        try {
            Field popupNodeField;
            popupNodeField = PicturePopupMenu.class.getDeclaredField( "popupNode" );
            popupNodeField.setAccessible( true );
            SortableDefaultMutableTreeNode verifyNode = (SortableDefaultMutableTreeNode) popupNodeField.get( myPicturePopupMenu );
            PictureInfo verifyPictureInfo = (PictureInfo) verifyNode.getUserObject();
            assertEquals( myPictureInfo, verifyPictureInfo );
        } catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex ) {
            Logger.getLogger( PicturePopupMenuTest.class.getName() ).log( Level.SEVERE, null, ex );
        }

    }

    /**
     * Get the children
     */
    public void testGetChildren() {
        assertEquals( "My Picture", title.getText() );
        assertEquals( "Show Picture", showPicture.getText() );
        assertEquals( "Show Map", showMap.getText() );
        assertEquals( "Navigate to", navigateTo.getText() );
        assertEquals( "Categories", categories.getText() );
        assertEquals( "Select for email", selectForEmail.getText() );
        assertEquals( "User Function", userFunction.getText() );
        assertEquals( "Rotation", rotation.getText() );
        assertEquals( "Rotate Right 90", rotate90.getText() );
        assertEquals( "Rotate 180", rotate180.getText() );
        assertEquals( "Rotate Left 270", rotate270.getText() );
        assertEquals( "No Rotation", rotate0.getText() );
        assertEquals( "Refresh Thumbnail", refreshThumbnail.getText() );
        assertEquals( "Move", move.getText() );
        assertEquals( "Copy Image", copyImage.getText() );
        assertEquals( "choose target directory", copyImageChooseTargetDir.getText() );
        assertEquals( "to zip file", copyImageToZipFile.getText() );
        assertEquals( "Remove Node", removeNode.getText() );
        assertEquals( "File operations", fileOperations.getText() );
        assertEquals( "Properties", properties.getText() );
        assertEquals( "Consolidate Here", consolidateHere.getText() );
    }
}
