package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class FlatGroupNavigatorTest {

    @Test
    public void testFlatGroupNavigator() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final var GROUP_INFO = "GroupInfo";
        final var node = new SortableDefaultMutableTreeNode(new GroupInfo(GROUP_INFO));
        final var navigator = new FlatGroupNavigator(node);
        assertEquals(GROUP_INFO, navigator.getTitle());
    }
}