package jpo.gui.swing;

import com.google.common.eventbus.Subscribe;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.ShowGroupRequest;
import jpo.EventBus.ShowPictureOnMapRequest;
import jpo.EventBus.ShowPictureRequest;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
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

    final private PictureInfo myPictureInfo = new PictureInfo();
    {
        myPictureInfo.setDescription( "My Picture" );
    }
    final private GroupInfo myGroupInfo = new GroupInfo( "Parent Group" );
    final private SortableDefaultMutableTreeNode myNode = new SortableDefaultMutableTreeNode( myPictureInfo );
    final private SortableDefaultMutableTreeNode myParentNode = new SortableDefaultMutableTreeNode( myGroupInfo );
    {
        myParentNode.add( myNode );
        Settings.getPictureCollection().getRootNode().add( myParentNode );
    }
    final private SingleNodeNavigator myNavigator = new SingleNodeNavigator( myNode );
    private PicturePopupMenu myPicturePopupMenu;

    private JMenuItem title;
    private JMenuItem showPicture;
    private JMenuItem showMap;
    private JMenu navigateTo;
    private JMenuItem navigateTo_0;
    private JMenuItem categories;
    private JMenuItem selectForEmail;
    private JMenu userFunction;
    private JMenu rotation;
    private JMenuItem rotate90;
    private JMenuItem rotate180;
    private JMenuItem rotate270;
    private JMenuItem rotate0;
    private JMenuItem refreshThumbnail;
    private JMenu move;
    private JMenu copyImage;
    private JMenuItem copyImageChooseTargetDir;
    private JMenuItem copyImageToZipFile;
    private JMenuItem removeNode;
    private JMenu fileOperations;
    private JMenuItem properties;
    private JMenuItem consolidateHere;

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
                navigateTo_0 = navigateTo.getItem( 0 );
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

    private int showPictureEventCount = 0;

    /**
     * Test clicking showPicture
     */
    public void testShowPicture() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleShowPictureRequest( ShowPictureRequest request ) {
                SortableDefaultMutableTreeNode node = request.getNode();
                Object userObject = node.getUserObject();
                showPictureEventCount++;
            }
        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, showPictureEventCount );
        showPicture.doClick();
        assertEquals( "After clicking on the node the event count should be 1", 1, showPictureEventCount );
    }

    private int showMapEventCount = 0;

    /**
     * Test clicking showMap
     */
    public void testShowMap() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleShowPictureOnMapRequest( ShowPictureOnMapRequest request ) {
                showMapEventCount++;
            }
        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, showMapEventCount );
        showMap.doClick();
        assertEquals( "After clicking on the node the event count should be 1", 1, showMapEventCount );
    }

    private int navigateToEventCount = 0;

    public void testNavigateTo() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleShowGroupRequest( ShowGroupRequest request ) {
                navigateToEventCount++;
            }
        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, navigateToEventCount );
        navigateTo_0.doClick();
        assertEquals( "After clicking on the node the event count should be 1", 1, navigateToEventCount );
    }

}
