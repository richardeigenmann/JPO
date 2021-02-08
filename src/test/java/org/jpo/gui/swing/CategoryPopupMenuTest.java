package org.jpo.gui.swing;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class CategoryPopupMenuTest {

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    /**
     * Tests constructing a CategoryEditorJFrame
     */
    @Test
    public void testConstructor() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
                final File picture1 = new File("picture1.jpg");
                final PictureInfo pictureInfo1 = new PictureInfo(picture1, "Image 1");
                node.setUserObject(pictureInfo1);

                final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>(List.of(node));

                final CategoryPopupMenu jPopupMenu = new CategoryPopupMenu(nodes);
                assertNotNull(jPopupMenu);
                assertEquals(jPopupMenu.getLabel(), "Add a Category to the Picture");
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}