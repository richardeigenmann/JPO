package org.jpo.gui;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SingleNodeNavigator;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Tests for the PictureViewer
 *
 * @author Richard Eigenmann
 */
class PictureViewerTest {

    @BeforeAll
    static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Test
    void testConstructor() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var pictureViewer = new PictureViewer();
                assertNotNull(pictureViewer);
                pictureViewer.closeViewerTest();
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }


    @Test
    void testShowPicture() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    final var image = new File(PictureViewerTest.class.getClassLoader().getResource("exif-test-nikon-d100-1.jpg").toURI());
                    final var pictureInfo = new PictureInfo();
                    pictureInfo.setImageLocation(image);
                    final var DESCRIPTION = "A test image";
                    pictureInfo.setDescription(DESCRIPTION);
                    final var node = new SortableDefaultMutableTreeNode(pictureInfo);
                    final var navigator = new SingleNodeNavigator(node);
                    final var pictureViewer = new PictureViewer();
                    pictureViewer.showNode(navigator, 0);
                    assertEquals(node, pictureViewer.getCurrentNodeTest());
                    pictureViewer.closeViewerTest();
                } catch (final URISyntaxException e) {
                    fail(e.getMessage());
                }
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}
