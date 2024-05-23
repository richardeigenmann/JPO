package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 Copyright (C) 2023-2024 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 *
 * @author Richard Eigenmann
 */
class NodeNavigatorTest {

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

        private int nodeLayoutChangedCount;

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
    void testAddRemoveNodeNavigatorListener() {
        final var myNodeNavigatorListener = new MyNodeNavigatorListener();
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
