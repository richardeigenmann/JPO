package org.jpo.gui;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.awaitility.Durations;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.PictureAdderRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

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


class PictureAdderTest {

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Test
    void testAddingSinglePicture() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            final var testNode = new SortableDefaultMutableTreeNode(new GroupInfo("Test Node"));
            final var pictureCollection = new PictureCollection();
            pictureCollection.getRootNode().add(testNode);

            final var NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
            final var imageFile = new File(PictureAdderTest.class.getClassLoader().getResource(NIKON_D100_IMAGE).toURI());
            final File[] files = {imageFile};
            final Collection<Integer> selectedCategories = new ArrayList<>();
            SwingUtilities.invokeAndWait(() -> {
                final var request = new PictureAdderRequest(testNode, files, false, true, true, selectedCategories);
                final var pictureAdder = new PictureAdder(request);
                pictureAdder.execute();
                assertNotNull(pictureAdder);
                await()

                        .atMost(Durations.ONE_SECOND)
                        .with()
                        .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS)
                        .until(() -> testNode.getChildCount() > 0);
                assertEquals(1, testNode.getChildCount());
            });

        } catch (final InterruptedException | InvocationTargetException | URISyntaxException ex) {
            fail("Errored in test: " + ex.getCause().getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testAddingMultiplePictures() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            final var testNode = new SortableDefaultMutableTreeNode(new GroupInfo("Test Node"));
            final var pictureCollection = new PictureCollection();
            pictureCollection.getRootNode().add(testNode);

            final var image1 = "exif-test-nikon-d100-1.jpg";
            final var image2 = "exif-test-samsung-s4.jpg";
            final var image3 = "exif-test-sony-d700.jpg";
            final var imageFile1 = new File(this.getClass().getClassLoader().getResource(image1).toURI());
            final var imageFile2 = new File(PictureAdderTest.class.getClassLoader().getResource(image2).toURI());
            final var imageFile3 = new File(PictureAdderTest.class.getClassLoader().getResource(image3).toURI());
            final File[] files = {imageFile1, imageFile2, imageFile3};
            final Collection<Integer> selectedCategories = new ArrayList<>();
            SwingUtilities.invokeAndWait(() -> {
                final var request = new PictureAdderRequest(testNode, files, false, true, true, selectedCategories);
                final var pictureAdder = new PictureAdder(request);
                pictureAdder.execute();
                assertNotNull(pictureAdder);
                await()
                        .atMost(Durations.TEN_SECONDS)
                        .with()
                        .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS)
                        .until(() -> testNode.getChildCount() > 2);
                assertEquals(3, testNode.getChildCount());
            });

        } catch (final InterruptedException | InvocationTargetException | URISyntaxException ex) {
            fail("Errored in test: " + ex.getCause().getMessage());
            Thread.currentThread().interrupt();
        }
    }


    @Test
    void testAddingMSWordDoc() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            final var testNode = new SortableDefaultMutableTreeNode(new GroupInfo("Test Node"));
            final var pictureCollection = new PictureCollection();
            pictureCollection.getRootNode().add(testNode);

            final var msWordDocFile = "MSWord.doc";
            final var imageFile = new File(PictureAdderTest.class.getClassLoader().getResource(msWordDocFile).toURI());
            final File[] files = {imageFile};
            final Collection<Integer> selectedCategories = new ArrayList<>();
            SwingUtilities.invokeAndWait(() -> {
                final PictureAdderRequest request = new PictureAdderRequest(testNode, files, false, true, true, selectedCategories);
                final var pictureAdder = new PictureAdder(request);
                pictureAdder.execute();
                assertNotNull(pictureAdder);
                await()

                        .atMost(Durations.ONE_MINUTE)
                        .with()
                        .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS)
                        .until(() -> testNode.getChildCount() > 0);
                assertEquals(1, testNode.getChildCount());
            });

        } catch (final InterruptedException | InvocationTargetException | URISyntaxException ex) {
            fail("Errored in test: " + ex.getCause().getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testAddingMSWordDocx() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            final var testNode = new SortableDefaultMutableTreeNode(new GroupInfo("Test Node"));
            final var pictureCollection = new PictureCollection();
            pictureCollection.getRootNode().add(testNode);

            final var msWordDocxFile = "MSWord.docx";
            final var imageFile = new File(PictureAdderTest.class.getClassLoader().getResource(msWordDocxFile).toURI());
            final File[] files = {imageFile};
            final Collection<Integer> selectedCategories = new ArrayList<>();
            SwingUtilities.invokeAndWait(() -> {
                final var request = new PictureAdderRequest(testNode, files, false, true, true, selectedCategories);
                final var pictureAdder = new PictureAdder(request);
                pictureAdder.execute();
                assertNotNull(pictureAdder);
                await()

                        .atMost(Durations.ONE_SECOND)
                        .with()
                        .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS)
                        .until(() -> testNode.getChildCount() > 0);
                assertEquals(1, testNode.getChildCount());
            });

        } catch (final InterruptedException | InvocationTargetException | URISyntaxException ex) {
            fail("Errored in test: " + ex.getCause().getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testAddingLibreOfficeText() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            final var testNode = new SortableDefaultMutableTreeNode(new GroupInfo("Test Node"));
            final var pictureCollection = new PictureCollection();
            pictureCollection.getRootNode().add(testNode);

            final var libreOfficeOdtFile = "LibreOfficeText.odt";
            final var imageFile = new File(PictureAdderTest.class.getClassLoader().getResource(libreOfficeOdtFile).toURI());
            final File[] files = {imageFile};
            final Collection<Integer> selectedCategories = new ArrayList<>();
            SwingUtilities.invokeAndWait(() -> {
                final var request = new PictureAdderRequest(testNode, files, false, true, true, selectedCategories);
                final var pictureAdder = new PictureAdder(request);
                pictureAdder.execute();
                assertNotNull(pictureAdder);
                await()

                        .atMost(Durations.ONE_SECOND)
                        .with()
                        .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS)
                        .until(() -> testNode.getChildCount() > 0);
                assertEquals(1, testNode.getChildCount());
            });

        } catch (final InterruptedException | InvocationTargetException | URISyntaxException ex) {
            fail("Errored in test: " + ex.getCause().getMessage());
            Thread.currentThread().interrupt();
        }
    }

}