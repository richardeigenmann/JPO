package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ListNavigatorTest {

    @Test
    void getTitle() {
        final ListNavigator listNavigator = new ListNavigator();
        assertNotNull(listNavigator);
        assertEquals("", listNavigator.getTitle());
    }

    @Test
    void getNumberOfNodes() {
        final ListNavigator listNavigator = new ListNavigator();
        assertNotNull(listNavigator);
        assertEquals(0, listNavigator.getNumberOfNodes());
    }


    @Test
    void addNodeTest() {
        final ListNavigator listNavigator = new ListNavigator();
        final SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
        listNavigator.add(node);
        assertEquals(1, listNavigator.getNumberOfNodes());
        SortableDefaultMutableTreeNode extractedNode = listNavigator.getNode(0);
        assertEquals(node, extractedNode);
    }

    @Test
    void clear() {
        final ListNavigator listNavigator = new ListNavigator();
        final SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
        listNavigator.add(node);
        assertEquals(1, listNavigator.getNumberOfNodes());
        listNavigator.clear();
        assertEquals(0, listNavigator.getNumberOfNodes());
    }

    @Test
    void addListTest() {
        final List<SortableDefaultMutableTreeNode> nodesList = new ArrayList<>();
        final SortableDefaultMutableTreeNode node1 = new SortableDefaultMutableTreeNode();
        final SortableDefaultMutableTreeNode node2 = new SortableDefaultMutableTreeNode();
        nodesList.add(node1);
        nodesList.add(node2);
        final ListNavigator listNavigator = new ListNavigator();
        listNavigator.add(nodesList);
        assertEquals(2, listNavigator.getNumberOfNodes());
    }

    @Test
    void removeNodeTest() {
        final List<SortableDefaultMutableTreeNode> nodesList = new ArrayList<>();
        final SortableDefaultMutableTreeNode node1 = new SortableDefaultMutableTreeNode();
        final SortableDefaultMutableTreeNode node2 = new SortableDefaultMutableTreeNode();
        nodesList.add(node1);
        nodesList.add(node2);
        ListNavigator listNavigator = new ListNavigator();
        listNavigator.add(nodesList);
        assertEquals(2, listNavigator.getNumberOfNodes());
        listNavigator.removeNode(node1);
        assertEquals(1, listNavigator.getNumberOfNodes());
        assertEquals(node2, listNavigator.getNode(0));

    }
}