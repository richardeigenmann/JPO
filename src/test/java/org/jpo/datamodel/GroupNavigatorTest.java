package org.jpo.datamodel;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Richard Eigenmann
 */
public class GroupNavigatorTest {

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    private final GroupInfo groupInfo = new GroupInfo( "Group1" );
    private final SortableDefaultMutableTreeNode groupNode = new SortableDefaultMutableTreeNode( groupInfo );

    private final PictureInfo pictureInfo1 = new PictureInfo();
    private final SortableDefaultMutableTreeNode pictureNode1 = new SortableDefaultMutableTreeNode( pictureInfo1 );

    private final PictureInfo pictureInfo2 = new PictureInfo();
    private final SortableDefaultMutableTreeNode pictureNode2 = new SortableDefaultMutableTreeNode( pictureInfo2 );

    private final GroupInfo groupInfo2 = new GroupInfo( "Group2" );
    private final SortableDefaultMutableTreeNode groupNode2 = new SortableDefaultMutableTreeNode( groupInfo2 );

    /**
     * Test of setNode method, of class GroupNavigator.
     */
    @BeforeEach
    public void testSetNode() {
        final var groupNavigator = new GroupNavigator(groupNode2);
        assertEquals( groupNode2, groupNavigator.getGroupNode() );
    }

    /**
     * Test of getTitle method, of class GroupNavigator.
     */
    @Test
    public void testGetTitle() {
        final var groupNavigator = new GroupNavigator(groupNode);
        assertEquals( "Group1", groupNavigator.getTitle() );
    }

    /**
     * Test of getNumberOfNodes method, of class GroupNavigator.
     */
    @Test
    public void testGetNumberOfNodes() {
        final var groupNavigator = new GroupNavigator(groupNode);
        assertEquals(  0, groupNavigator.getNumberOfNodes() );
        groupNode.add( pictureNode1 );
        groupNode.add( pictureNode2 );
        assertEquals( 2, groupNavigator.getNumberOfNodes() );
        try {
            SwingUtilities.invokeAndWait(groupNode::removeAllChildren);
        } catch ( InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( GroupNavigatorTest.class.getName() ).log( Level.SEVERE, null, ex );
            Thread.currentThread().interrupt();
        }

        // After removing all children we expect to have 0 nodes
        assertEquals( 0, groupNavigator.getNumberOfNodes() );
    }

}
