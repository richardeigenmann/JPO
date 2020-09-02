package org.jpo.datamodel;

import org.junit.Test;

import static org.junit.Assert.*;

public class FlatGroupNavigatorTest {

    @Test
    public void testFlatGroupNavigator() {
        final String GROUP_INFO = "GroupInfo";
        final SortableDefaultMutableTreeNode n = new SortableDefaultMutableTreeNode(new GroupInfo(GROUP_INFO));
        final FlatGroupNavigator navigator = new FlatGroupNavigator(n);
        assertEquals(GROUP_INFO, navigator.getTitle());
    }
}