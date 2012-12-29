package jpo.dataModel;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import junit.framework.TestCase;

/*
 * ApplicationJMenuBarTest.java JUnit based test
 *
 */
/**
 *
 * @author Richard Eigenmann
 */
public class PictureCollectionTest
        extends TestCase {

    public PictureCollectionTest(String testName) {
        super(testName);
    }
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(PictureCollectionTest.class.getName());
    /**
     * Let's have a nice little collection for some tests....
     */
    PictureCollection pc;
    final PictureInfo pi1 = new PictureInfo("/images/image1.jpg", "/lowresimages/image1lowres.jpg", "Picture 1", "Reference1");
    final PictureInfo pi2 = new PictureInfo("/images/image2.jpg", "/lowresimages/image2lowres.jpg", "Picture 2", "Reference2");
    final PictureInfo pi3 = new PictureInfo("/images/image3.jpg", "/lowresimages/image3lowres.jpg", "Picture 3", "Reference3");
    final PictureInfo pi4 = new PictureInfo("/images/image4.jpg", "/lowresimages/image4lowres.jpg", "Picture 4", "Reference4");
    final PictureInfo pi5 = new PictureInfo("/images/image5.jpg", "/lowresimages/image5lowres.jpg", "Picture 5", "Reference5");
    final PictureInfo pi6 = new PictureInfo("/images/image6.jpg", "/lowresimages/image6lowres.jpg", "Picture 6", "Reference6");
    final SortableDefaultMutableTreeNode picture1 = new SortableDefaultMutableTreeNode(pi1);
    final SortableDefaultMutableTreeNode picture2 = new SortableDefaultMutableTreeNode(pi2);
    final SortableDefaultMutableTreeNode picture3 = new SortableDefaultMutableTreeNode(pi3);
    final SortableDefaultMutableTreeNode picture4 = new SortableDefaultMutableTreeNode(pi4);
    final SortableDefaultMutableTreeNode picture5 = new SortableDefaultMutableTreeNode(pi5);
    final SortableDefaultMutableTreeNode picture6 = new SortableDefaultMutableTreeNode(pi6);
    final SortableDefaultMutableTreeNode group1 = new SortableDefaultMutableTreeNode(new GroupInfo("Group1"));
    final SortableDefaultMutableTreeNode group2 = new SortableDefaultMutableTreeNode(new GroupInfo("Group2"));
    final SortableDefaultMutableTreeNode group3 = new SortableDefaultMutableTreeNode(new GroupInfo("Group3"));
    final SortableDefaultMutableTreeNode group4 = new SortableDefaultMutableTreeNode(new GroupInfo("Group4"));
    final SortableDefaultMutableTreeNode group5 = new SortableDefaultMutableTreeNode(new GroupInfo("Group5"));
    final SortableDefaultMutableTreeNode group6 = new SortableDefaultMutableTreeNode(new GroupInfo("Group6"));

    @Override
    protected void setUp() throws Exception {
        pc = new PictureCollection();
        pc.getRootNode().add(group1);
        pc.getRootNode().add(group2);
        pc.getRootNode().add(group3);
        pc.getRootNode().add(group4);
        group1.add(picture1);
        group2.add(picture2);
        group3.add(picture3);
        group4.add(picture4);
        group4.add(group5);
        group5.add(group6);
        group6.add(picture5);
        group6.add(picture6);
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public void testFindParentGroups() {
        assertNotNull("Test that something is returned when looking for parent groups", pc.findParentGroups(picture1));
    }

    public void testFindParentGroups1() {
        assertNull("Test that it returns null if the node is not a PictureInfo node", pc.findParentGroups(group1));
    }

    public void testFindParentGroups2() {
        //test that the parent group is one of the returned groups
        SortableDefaultMutableTreeNode[] sdmtns = pc.findParentGroups(picture1);
        boolean found = false;
        for (int i = 0; i < sdmtns.length; i++) {
            found = found || (sdmtns[i] == group1);
        }
        assertTrue("Test that the parent group is amongst the found groups", found);
    }

    public void testFindParentGroups3() {
        //test that the 4 groups which refer to the same picture are returned
        SortableDefaultMutableTreeNode[] sdmtns = pc.findParentGroups(picture1);
        assertEquals("Test that the 3 groups refering to the same picture are found", 4, sdmtns.length);
    }

    public void testSetXmlFile() {
        File f = new File("/dir/test.xml");
        pc.setXmlFile(f);
        File f2 = pc.getXmlFile();
        assertEquals("Checking that we get the same file back that we put in", f, f2);

        pc.clearCollection();
        File f3 = pc.getXmlFile();
        assertNull("Check that a clearCollection sets the file name to null", f3);
    }

    /**
     * I had a concurrent modification problem on "clear selections" so 
     * here are a few tests to verify the selection thing works.
     */
    public void testSelections() {
        assertEquals("Testing that the selection array is empty before we start", 0, pc.getSelectedNodes().length);
        pc.addToSelectedNodes(group1);
        pc.addToSelectedNodes(picture1);
        assertEquals("We should have 2 nodes selected now", 2, pc.getSelectedNodes().length);
        assertEquals("We should have 2 nodes selected now", 2, pc.countSelectedNodes());
        assertTrue("We sould find that the node we selected is actually in the selected set", pc.isSelected(group1));
        assertTrue("We sould find that the second node we selected is actually in the selected set", pc.isSelected(picture1));
        assertFalse("A Node that was not selected should not be in the selection", pc.isSelected(group2));

        pc.removeFromSelection(group1);
        assertEquals("We should have 1 nodes selected now", 1, pc.getSelectedNodes().length);
        assertFalse("We sould find that the node we deselected is actually gone", pc.isSelected(group1));
        assertTrue("We sould find that the second node we selected is still in the selected set", pc.isSelected(picture1));
        assertFalse("A Node that was not selected should not be in the selection", pc.isSelected(group2));

        pc.addToSelectedNodes(group1);
        pc.addToSelectedNodes(group1); //why not add it again?
        assertEquals("Twice the same node plus one equals 2", 2, pc.getSelectedNodes().length);

        pc.clearSelection(); // this is where we the concurrent modification happened
        assertEquals("Testing that the selection array is empty again", 0, pc.getSelectedNodes().length);

        pc.removeFromSelection(group1); // How about removing somehting that is not there?
        assertEquals("Testing that the selection array stayed", 0, pc.getSelectedNodes().length);
    }

    /**
     * I had a concurrent modification problem on "clear selections" so
     * here are a few tests to verify the selection thing works.
     */
    public void testMailSelections() {
        assertEquals("Testing that the mail selection array is empty before we start", 0, pc.getMailSelectedNodes().length);
        pc.addToMailSelection(group1);
        pc.addToMailSelection(picture1);
        assertEquals("We should have 2 nodes selected now", 2, pc.getMailSelectedNodes().length);
        assertTrue("We sould find that the node we selected is actually in the selected set", pc.isMailSelected(group1));
        assertTrue("We sould find that the second node we selected is actually in the selected set", pc.isMailSelected(picture1));
        assertFalse("A Node that was not selected should not be in the selection", pc.isMailSelected(group2));

        pc.removeFromMailSelection(group1);
        assertEquals("We should have 1 nodes selected now", 1, pc.getMailSelectedNodes().length);
        assertFalse("We sould find that the node we deselected is actually gone", pc.isMailSelected(group1));
        assertTrue("We sould find that the second node we selected is still in the selected set", pc.isMailSelected(picture1));
        assertFalse("A Node that was not selected should not be in the selection", pc.isMailSelected(group2));

        pc.addToMailSelection(group1);
        pc.addToMailSelection(group1); //why not add it again?
        assertEquals("Twice the same node plus one picture equals 2", 2, pc.getMailSelectedNodes().length);

        pc.toggleMailSelected(picture1);
        assertEquals("Should be only group1 selected now", 1, pc.getMailSelectedNodes().length);

        pc.clearMailSelection(); // this is where we the concurrent modification happened
        assertEquals("Testing that the selection array is empty again", 0, pc.getMailSelectedNodes().length);

        pc.removeFromMailSelection(group1); // How about removing somehting that is not there?
        assertEquals("Testing that the selection array stayed", 0, pc.getMailSelectedNodes().length);
    }

    /**
     * Since I had a concurrent modification problem on the clear selections
     * here are a few tests to verify the selection thing works.
     */
    public void testAddToMailSelection() {
        assertEquals("Testing that the mail selection array is empty before we start", 0, pc.getMailSelectedNodes().length);
        pc.addToMailSelection(picture1);
        assertEquals("We should have 1 nodes selected now", 1, pc.getMailSelectedNodes().length);
        pc.addToMailSelection(picture1); //adding the same node again
        assertEquals("We should have 1 nodes selected now", 1, pc.getMailSelectedNodes().length);
    }
    /**
     * Let's create a quick and dirty change listener
     */
    private PictureInfoChangeListener listener = new PictureInfoChangeListener() {

        @Override
        public void pictureInfoChangeEvent(PictureInfoChangeEvent e) {
            if (e.getWasSelected()) {
                selectedCount++;
            } else if (e.getWasUnselected()) {
                unselectedCount++;
            } else if (e.getWasMailSelected()) {
                mailSelectedCount++;
            } else if (e.getWasMailUnselected()) {
                mailUnselectedCount++;
            }
        }
    };
    int selectedCount;
    int unselectedCount;
    int mailSelectedCount;
    int mailUnselectedCount;

    public void testSelectNotification() {
        pi1.addPictureInfoChangeListener(listener);
        selectedCount = 0;
        unselectedCount = 0;
        pc.addToSelectedNodes(picture1);
        assertEquals("We should have received a notification that the picture was selected", 1, selectedCount);

        // do it again.
        pc.addToSelectedNodes(picture1);
        assertEquals("As we are adding the same node again we should not get a change event", 1, selectedCount);

        // add another node where we are not listening.
        pc.addToSelectedNodes(picture2);
        assertEquals("As we are not listening on the second node we should still be with 1 event", 1, selectedCount);

        pc.removeFromSelection(picture1);
        assertEquals("We should have received a notification that the picture was unselected", 1, unselectedCount);

        pc.addToSelectedNodes(picture1);
        assertEquals("We should have received a notification that the picture was selected", 2, selectedCount);

        pc.clearSelection();
        assertEquals("We should have received a notification that the picture was unselected", 2, unselectedCount);

        pi1.removePictureInfoChangeListener(listener);
        pc.addToSelectedNodes(picture1);
        assertEquals("We should not have received a notification that the picture was selected", 2, selectedCount);

        pc.clearSelection();
        assertEquals("We should have received a notification that the picture was unselected", 2, unselectedCount);
    }

    public void testClearMailSelection() {
        pi1.addPictureInfoChangeListener(listener);
        mailSelectedCount = 0;
        mailUnselectedCount = 0;
        pc.addToMailSelection(picture1);
        assertEquals("We should have received a notification that the picture was selected", 1, mailSelectedCount);

        assertEquals("Before the removal we should have 0 unselect events", 0, mailUnselectedCount);
        pc.clearMailSelection();
        assertEquals("We should have received a notification that the picture was unselected", 1, mailUnselectedCount);
    }
   
    
    
    public void testMailSelectNotification() {
        pi1.addPictureInfoChangeListener(listener);
        mailSelectedCount = 0;
        mailUnselectedCount = 0;
        pc.addToMailSelection(picture1);
        assertEquals("We should have received a notification that the picture was selected", 1, mailSelectedCount);

        // do it again.
        pc.addToMailSelection(picture1);
        assertEquals("As we are adding the same node again we should not get a change event", 1, mailSelectedCount);

        // add another node where we are not listening.
        pc.addToMailSelection(picture2);
        assertEquals("As we are not listening on the second node we should still be with 1 event", 1, mailSelectedCount);

        assertEquals("Before the removal we should have 0 unselect events", 0, mailUnselectedCount);
        pc.removeFromMailSelection(picture1);
        assertEquals("We should have received a notification that the picture was unselected", 1, mailUnselectedCount);

        pc.addToMailSelection(picture1);
        assertEquals("We should have received a notification that the picture was selected", 2, mailSelectedCount);

        pc.clearMailSelection();
        assertEquals("We should have received a notification that the picture was unselected", 2, mailUnselectedCount);

        pi1.removePictureInfoChangeListener(listener);
        pc.addToMailSelection(picture1);
        assertEquals("We should not have received a notification that the picture was selected", 2, mailSelectedCount);

        pc.clearSelection();
        assertEquals("We should have received a notification that the picture was unselected", 2, mailUnselectedCount); 
       
    }
    
    
    
    int nodeschanged = 0;
    int nodesinserted = 0;
    int nodesremoved = 0;
    int nodestructurechanged = 0;

    /**
     * In this test we want to see whether a change to an attribute in the
     * picture results in a treeModel change event being fired
     */
    public void testChangeNotification() {
        nodeschanged = 0;
        nodesinserted = 0;
        nodesremoved = 0;
        nodestructurechanged = 0;
        pc.getTreeModel().addTreeModelListener(new TreeModelListener() {

            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                nodeschanged++;
            }

            @Override
            public void treeNodesInserted(TreeModelEvent e) {
                nodesinserted++;
            }

            @Override
            public void treeNodesRemoved(TreeModelEvent e) {
                nodesremoved++;
            }

            @Override
            public void treeStructureChanged(TreeModelEvent e) {
                nodestructurechanged++;
            }
        });
        
        //TODO: review this; why does the root node, the model and the picturecollection have to be tied together
        // via the Settings?
        Settings.pictureCollection = pc;
        assertEquals("Before updating the description we should have 0 nodes changed: ", 0, nodeschanged);
        pi1.setDescription("Changed Description");
        try {
            Thread.sleep(80);  // give the threads some time to do the notifications.
        } catch (InterruptedException ex) {
            Logger.getLogger(PictureCollectionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals("After updating the description we should have 1 node changed: ", 1, nodeschanged);
        assertEquals("No nodes should have been inserted: ", 0, nodesinserted);
        assertEquals("No nodes should have been removed: ", 0, nodesremoved);
        assertEquals("No nodes structure change should have been notified: ", 0, nodestructurechanged);
    }
}
