package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class FlatGroupNavigatorTest {

    @Test
    public void testFlatGroupNavigator() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final String GROUP_INFO = "GroupInfo";
        final SortableDefaultMutableTreeNode n = new SortableDefaultMutableTreeNode(new GroupInfo(GROUP_INFO));
        final FlatGroupNavigator navigator = new FlatGroupNavigator(n);
        assertEquals(GROUP_INFO, navigator.getTitle());
    }
}