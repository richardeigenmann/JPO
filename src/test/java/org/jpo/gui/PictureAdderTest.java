package org.jpo.gui;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.awaitility.Durations;
import org.jpo.datamodel.ExifInfoTest;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class PictureAdderTest {

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    /**
     * Test constructor
     */
    @Test
    public void testConstructor() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            final var testNode = new SortableDefaultMutableTreeNode(new GroupInfo("Test Node"));
            final var NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
            final var imageFile = new File(ExifInfoTest.class.getClassLoader().getResource(NIKON_D100_IMAGE).toURI());
            final File[] files = {imageFile};
            final Collection<Integer> selectedCategories = new ArrayList<Integer>();
            SwingUtilities.invokeAndWait(() -> {
                final var pictureAdder = new PictureAdder(testNode, files, false, true, true, selectedCategories);
                pictureAdder.execute();
                assertNotNull(pictureAdder);
                await()
                        .atMost(Durations.ONE_SECOND)
                        .with()
                        .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS)
                        .until(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return testNode.getChildCount() > 0;
                            }
                        });
                assertEquals(1, testNode.getChildCount());
            });

        } catch (final InterruptedException | InvocationTargetException | URISyntaxException ex) {
            fail("Failed to create a PictureAdder");
            Thread.currentThread().interrupt();
        }
    }
}