package jpo.gui.swing;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import junit.framework.TestCase;

/**
 * Tests for the GroupPopupMenu Class
 *
 * @author Richard Eigenmann
 */
public class GroupPopupMenuTest extends TestCase {

    /**
     * Constructor for the tests.
     * 
     * Note these tests are burdened with reflection to get at the inner workings
     * of the popup menu. Should I open up the fields in the popup menu class?
     * I think not because other classes don't need to see into the inner workings
     * of the popup menu. With the exception of this one that has to make sure 
     * the details of the class are working properly.
     *
     * @param testName test name
     */
    public GroupPopupMenuTest( String testName ) {
        super( testName );
    }

    protected GroupInfo myGroupInfo = new GroupInfo( "My Group" );
    protected SortableDefaultMutableTreeNode myNode = new SortableDefaultMutableTreeNode( myGroupInfo );
    protected GroupPopupMenu myGroupPopupMenu;

    protected JMenuItem showGroup;
    protected JMenuItem showPictures;
    protected JMenuItem find;
    protected JMenuItem categories;
    protected JMenuItem refreshIcon;
    protected JMenuItem editAsTable;
    protected JMenuItem add;
    protected JMenuItem move;
    protected JMenuItem removeNode;
    protected JMenuItem consolidate;
    protected JMenuItem sortBy;
    protected JMenuItem selectAllForEmailing;
    protected JMenuItem generateWebsite;
    protected JMenuItem exportToCollection;
    protected JMenuItem exportToFlatFile;
    protected JMenuItem exportToPicasa;
    protected JMenuItem properties;

    /**
     * Creates the objects for testing. Runs on the EDT.
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        myGroupPopupMenu = new GroupPopupMenu( myNode );

        Runnable r = new Runnable() {

            @Override
            public void run() {
                showGroup = (JMenuItem) myGroupPopupMenu.getComponent( 0 );
                showPictures = (JMenuItem) myGroupPopupMenu.getComponent( 1 );
                find = (JMenuItem) myGroupPopupMenu.getComponent( 2 );
                categories = (JMenuItem) myGroupPopupMenu.getComponent( 4 );
                refreshIcon = (JMenuItem) myGroupPopupMenu.getComponent( 5 );
                editAsTable = (JMenuItem) myGroupPopupMenu.getComponent( 7 );
                add = (JMenuItem) myGroupPopupMenu.getComponent( 9 );
                move = (JMenuItem) myGroupPopupMenu.getComponent( 10 );
                removeNode = (JMenuItem) myGroupPopupMenu.getComponent( 11 );
                consolidate = (JMenuItem) myGroupPopupMenu.getComponent( 13 );
                sortBy = (JMenuItem) myGroupPopupMenu.getComponent( 15 );
                selectAllForEmailing = (JMenuItem) myGroupPopupMenu.getComponent( 17 );
                generateWebsite = (JMenuItem) myGroupPopupMenu.getComponent( 18 );
                exportToCollection = (JMenuItem) myGroupPopupMenu.getComponent( 19 );
                exportToFlatFile = (JMenuItem) myGroupPopupMenu.getComponent( 20 );
                exportToPicasa = (JMenuItem) myGroupPopupMenu.getComponent( 21 );
                properties = (JMenuItem) myGroupPopupMenu.getComponent( 23 );
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
            popupNodeField = GroupPopupMenu.class.getDeclaredField( "popupNode" );
            popupNodeField.setAccessible( true );
            SortableDefaultMutableTreeNode verifyNode = (SortableDefaultMutableTreeNode) popupNodeField.get( myGroupPopupMenu );
            GroupInfo verifyGroupInfo = (GroupInfo) verifyNode.getUserObject();
            assertEquals( myGroupInfo, verifyGroupInfo );
        } catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex ) {
            Logger.getLogger( GroupPopupMenuTest.class.getName() ).log( Level.SEVERE, null, ex );
        }

    }

    /**
     * Get the children
     */
    public void testGetChildren() {
        assertEquals( "Show Group", showGroup.getText() );
        assertEquals( "Show Pictures", showPictures.getText() );
        assertEquals( "Find", find.getText() );
        assertEquals( "Categories", categories.getText() );
        assertEquals( "Refresh Icon", refreshIcon.getText() );
        assertEquals( "Edit as Table", editAsTable.getText() );
        assertEquals( "Add", add.getText() );
        assertEquals( "Move", move.getText() );
        assertEquals( "Remove Node", removeNode.getText() );
        assertEquals( "Consolidate/Move", consolidate.getText() );
        assertEquals( "Sort by", sortBy.getText() );
        assertEquals( "Select all for Emailing", selectAllForEmailing.getText() );
        assertEquals( "Generate Website", generateWebsite.getText() );
        assertEquals( "Export to Collection", exportToCollection.getText() );
        assertEquals( "Export to Flat File", exportToFlatFile.getText() );
        assertEquals( "Export to Picasa", exportToPicasa.getText() );
        assertEquals( "Properties", properties.getText() );
    }
}
