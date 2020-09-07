package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Richard Eigenmann
 */
public class NodeNavigatorTest {

    private static class NodeNavigatorImpl extends NodeNavigator {

        @Override
        public String getTitle() {
            return "Title";
        }

        @Override
        public int getNumberOfNodes() {
            return 5;
        }

        @Override
        public SortableDefaultMutableTreeNode getNode( int index ) {
            return new SortableDefaultMutableTreeNode();
        }
    }

    private static class MyNodeNavigatorListener implements NodeNavigatorListener {

        public int nodeLayoutChangedCount;

        @Override
        public void nodeLayoutChanged() {
            nodeLayoutChangedCount++;
        }

    }

    /**
     * Test of addNodeNavigatorListener method and removeNodeNavigatorListener
     * and notifyNodeNavigatorListeners
     */
    @Test
    public void testAddRemoveNodeNavigatorListener() {
        MyNodeNavigatorListener myNodeNavigatorListener = new MyNodeNavigatorListener();
        assertEquals( 0, myNodeNavigatorListener.nodeLayoutChangedCount );

        NodeNavigatorImpl nodeNavigatorImpl = new NodeNavigatorImpl();
        nodeNavigatorImpl.notifyNodeNavigatorListeners();
        assertEquals( 0, myNodeNavigatorListener.nodeLayoutChangedCount );

        nodeNavigatorImpl.addNodeNavigatorListener( myNodeNavigatorListener );
        nodeNavigatorImpl.notifyNodeNavigatorListeners();
        assertEquals( 1, myNodeNavigatorListener.nodeLayoutChangedCount );

        nodeNavigatorImpl.notifyNodeNavigatorListeners();
        assertEquals( 2, myNodeNavigatorListener.nodeLayoutChangedCount );

        nodeNavigatorImpl.removeNodeNavigatorListener( myNodeNavigatorListener );
        nodeNavigatorImpl.notifyNodeNavigatorListeners();
        assertEquals( 2, myNodeNavigatorListener.nodeLayoutChangedCount );
    }

}
