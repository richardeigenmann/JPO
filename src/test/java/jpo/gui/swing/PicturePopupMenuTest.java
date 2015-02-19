package jpo.gui.swing;

import com.google.common.eventbus.Subscribe;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import jpo.EventBus.AddPictureNodesToEmailSelectionRequest;
import jpo.EventBus.ClearEmailSelectionRequest;
import jpo.EventBus.ConsolidateGroupRequest;
import jpo.EventBus.DeleteNodeFileRequest;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.RefreshThumbnailRequest;
import jpo.EventBus.RemoveNodeRequest;
import jpo.EventBus.RemovePictureNodesFromEmailSelectionRequest;
import jpo.EventBus.RenamePictureRequest;
import jpo.EventBus.RotatePictureRequest;
import jpo.EventBus.RunUserFunctionRequest;
import jpo.EventBus.SetPictureRotationRequest;
import jpo.EventBus.ShowCategoryUsageEditorRequest;
import jpo.EventBus.ShowGroupRequest;
import jpo.EventBus.ShowPictureInfoEditorRequest;
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
        try {
            File temp = File.createTempFile( "JPO-Unit-Test", ".jpg");
            temp.deleteOnExit();
            myPictureInfo.setHighresLocation( temp );
        } catch ( IOException ex ) {
            Logger.getLogger( PicturePopupMenuTest.class.getName() ).log( Level.SEVERE, null, ex );
        }
        
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
    private JMenuItem unselectForEmail;
    private JMenuItem clearEmailSelection;
    private JMenu userFunction;
    private JMenuItem userFunction_0;
    private JMenuItem userFunction_1;
    private JMenuItem userFunction_2;
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
    private JMenuItem rename;
    private JMenuItem delete;
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
                unselectForEmail = (JMenuItem) myPicturePopupMenu.getComponent( 7 );
                clearEmailSelection = (JMenuItem) myPicturePopupMenu.getComponent( 8 );
                userFunction = (JMenu) myPicturePopupMenu.getComponent( 9 );
                userFunction_0 = userFunction.getItem( 0 );
                userFunction_1 = userFunction.getItem( 1 );
                userFunction_2 = userFunction.getItem( 2 );
                rotation = (JMenu) myPicturePopupMenu.getComponent( 10 );
                rotate90 = rotation.getItem( 0 );
                rotate180 = rotation.getItem( 1 );
                rotate270 = rotation.getItem( 2 );
                rotate0 = rotation.getItem( 3 );
                refreshThumbnail = (JMenuItem) myPicturePopupMenu.getComponent( 11 );
                move = (JMenu) myPicturePopupMenu.getComponent( 12 );
                copyImage = (JMenu) myPicturePopupMenu.getComponent( 13 );
                copyImageChooseTargetDir = copyImage.getItem( 0 );
                copyImageToZipFile = copyImage.getItem( 12 );
                removeNode = (JMenuItem) myPicturePopupMenu.getComponent( 14 );
                fileOperations = (JMenu) myPicturePopupMenu.getComponent( 15 );
                rename = fileOperations.getItem( 0 );
                delete = fileOperations.getItem( 1 );
                properties = (JMenuItem) myPicturePopupMenu.getComponent( 16 );
                consolidateHere = (JMenuItem) myPicturePopupMenu.getComponent( 17 );
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

    private int categoriesEventCount = 0;

    public void testCategories() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleShowCategoryUsageEditorRequest( ShowCategoryUsageEditorRequest request ) {
                categoriesEventCount++;
            }
        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, categoriesEventCount );
        categories.doClick();
        assertEquals( "After clicking on the node the event count should be 1", 1, categoriesEventCount );
    }

    private int addToMailSelectEventCount = 0;

    public void testSelectForEmail() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleAddPictureModesToEmailSelectionRequest( AddPictureNodesToEmailSelectionRequest request ) {
                addToMailSelectEventCount++;
            }
        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, addToMailSelectEventCount );
        selectForEmail.doClick();
        assertEquals( "After clicking on the node the event count should be 1", 1, addToMailSelectEventCount );
    }

    private int removeFromMailSelectEventCount = 0;

    public void testUnselectForEmail() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleRemovePictureModesFromEmailSelectionRequest( RemovePictureNodesFromEmailSelectionRequest request ) {
                removeFromMailSelectEventCount++;
            }
        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, removeFromMailSelectEventCount );
        unselectForEmail.doClick();
        assertEquals( "After clicking on the node the event count should be 1", 1, removeFromMailSelectEventCount );
    }

    private int clearEmailSelectionEventCount = 0;

    public void testClearEmailSelection() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleClearEmailSelectionRequest( ClearEmailSelectionRequest request ) {
                clearEmailSelectionEventCount++;
            }
        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, clearEmailSelectionEventCount );
        clearEmailSelection.doClick();
        assertEquals( "After clicking on the node the event count should be 1", 1, clearEmailSelectionEventCount );
    }

    // TODO: Test the selection logic of the Select for EMail and unselect for EMail
    private int userFunctionEventCount = 0;

    public void testUserFunctions() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleRunUserFunctionRequest( RunUserFunctionRequest request ) {
                userFunctionEventCount++;
            }
        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, userFunctionEventCount );
        userFunction_0.doClick();
        userFunction_1.doClick();
        userFunction_2.doClick();
        assertEquals( "After clicking on the node the event count should be 3", 3, userFunctionEventCount );
    }

    private int rotationEventCount = 0;

    public void testRotation() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleRotatePictureRequestRequest( RotatePictureRequest request ) {
                rotationEventCount++;
            }

            @Subscribe
            public void handleSetPictureRotationRequest( SetPictureRotationRequest request ) {
                rotationEventCount++;
            }

        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, rotationEventCount );
        rotate90.doClick();
        rotate180.doClick();
        rotate270.doClick();
        rotate0.doClick();
        assertEquals( "After clicking on the node the event count should be 4", 4, rotationEventCount );
    }

    private int refreshEventCount = 0;

    public void testRefresh() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleRefreshThumbnailRequest( RefreshThumbnailRequest request ) {
                refreshEventCount++;
            }
        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, refreshEventCount );
        refreshThumbnail.doClick();
        assertEquals( "After clicking on the node the event count should be 1", 1, refreshEventCount );
    }

    private int removeEventCount = 0;

    public void testRemove() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleRemoveNodeRequest( RemoveNodeRequest request ) {
                removeEventCount++;
            }
        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, removeEventCount );
        removeNode.doClick();
        assertEquals( "After clicking on the node the event count should be 1", 1, removeEventCount );
    }

    private int renameEventCount = 0;

    public void testRename() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleRenamePictureRequest( RenamePictureRequest request ) {
                renameEventCount++;
            }
        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, renameEventCount );
        rename.doClick();
        assertEquals( "After clicking on the node the event count should be 1", 1, renameEventCount );
    }

    private int deleteEventCount = 0;

    public void testDelete() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleDeleteNodeFileRequest( DeleteNodeFileRequest request ) {

                deleteEventCount++;
            }
        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, deleteEventCount );
        delete.doClick();
        assertEquals( "After clicking on the node the event count should be 1", 1, deleteEventCount );
    }

    private int propertiesEventCount = 0;

    public void testProperties() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleShowPictureInfoEditorRequest( ShowPictureInfoEditorRequest request ) {
                propertiesEventCount++;
            }
        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, propertiesEventCount );
        properties.doClick();
        assertEquals( "After clicking on the node the event count should be 1", 1, propertiesEventCount );
    }

    private int consolidateHereEventCount = 0;

    public void testConsolidateHere() {
        JpoEventBus.getInstance().register( new Object() {
            @Subscribe
            public void handleConsolidateGroupRequest( ConsolidateGroupRequest request ) {
                consolidateHereEventCount++;
            }
        } );
        assertEquals( "Before clicking on the node the event count should be 0", 0, consolidateHereEventCount );
        consolidateHere.doClick();
        assertEquals( "After clicking on the node the event count should be 1", 1, consolidateHereEventCount );
    }

}
