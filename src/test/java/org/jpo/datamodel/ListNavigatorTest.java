package org.jpo.datamodel;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ListNavigatorTest {

    @Test
    public void getTitle() {
        ListNavigator n = new ListNavigator();
        assertNotNull(n);
        assertEquals("",n.getTitle());
    }

    @Test
    public void getNumberOfNodes() {
        ListNavigator n = new ListNavigator();
        assertNotNull(n);
        assertEquals(0,n.getNumberOfNodes());
    }


    @Test
    public void addNodeTest() {
        ListNavigator n = new ListNavigator();
        SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
        n.add(node);
        assertEquals(1,n.getNumberOfNodes());
        SortableDefaultMutableTreeNode extractedNode = n.getNode(0);
        assertEquals(node, extractedNode);
    }

    @Test
    public void clear() {
        ListNavigator n = new ListNavigator();
        SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
        n.add(node);
        assertEquals(1,n.getNumberOfNodes());
        n.clear();
        assertEquals(0,n.getNumberOfNodes());
    }

    @Test
    public void addListTest() {
        List<SortableDefaultMutableTreeNode> nodesList = new ArrayList<>();
        SortableDefaultMutableTreeNode node1 = new SortableDefaultMutableTreeNode();
        SortableDefaultMutableTreeNode node2 = new SortableDefaultMutableTreeNode();
        nodesList.add(node1);
        nodesList.add(node2);
        ListNavigator listNavigator = new ListNavigator();
        listNavigator.add(nodesList);
        assertEquals(2,listNavigator.getNumberOfNodes());
    }

    @Test
    public void removeNodeTest() {
        List<SortableDefaultMutableTreeNode> nodesList = new ArrayList<>();
        SortableDefaultMutableTreeNode node1 = new SortableDefaultMutableTreeNode();
        SortableDefaultMutableTreeNode node2 = new SortableDefaultMutableTreeNode();
        nodesList.add(node1);
        nodesList.add(node2);
        ListNavigator listNavigator = new ListNavigator();
        listNavigator.add(nodesList);
        assertEquals(2,listNavigator.getNumberOfNodes());
        listNavigator.removeNode(node1);
        assertEquals(1,listNavigator.getNumberOfNodes());
        assertEquals(node2, listNavigator.getNode(0));

    }
}