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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import static org.awaitility.Awaitility.await;
import static org.jpo.datamodel.Tools.copyResourceToTempFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
Copyright (C) 2023-2026 Richard Eigenmann.
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
    void testAddingSinglePicture() throws InterruptedException, InvocationTargetException, IOException {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final var testNode = new SortableDefaultMutableTreeNode(new GroupInfo("Test Node"));
        final var pictureCollection = new PictureCollection();
        pictureCollection.getRootNode().add(testNode);

        final var NIKON_D100_IMAGE = "/exif-test-nikon-d100-1.jpg";
        final var imageFile = copyResourceToTempFile( NIKON_D100_IMAGE);
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
    }

    @Test
    void testAddingMultiplePictures() throws IOException, InterruptedException, InvocationTargetException {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final var testNode = new SortableDefaultMutableTreeNode(new GroupInfo("Test Node"));
        final var pictureCollection = new PictureCollection();
        pictureCollection.getRootNode().add(testNode);

        final var image1 = "/exif-test-nikon-d100-1.jpg";
        final var image2 = "/exif-test-samsung-s4.jpg";
        final var image3 = "/exif-test-sony-d700.jpg";
        final var imageFile1 = copyResourceToTempFile(image1);
        final var imageFile2 = copyResourceToTempFile(image2);
        final var imageFile3 = copyResourceToTempFile(image3);
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
    }


    @Test
    void testAddingMSWordDoc() throws IOException, InterruptedException, InvocationTargetException {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final var testNode = new SortableDefaultMutableTreeNode(new GroupInfo("Test Node"));
        final var pictureCollection = new PictureCollection();
        pictureCollection.getRootNode().add(testNode);

        final var msWordDocFileName = "/MSWord.doc";
        final var msWordDocFile = copyResourceToTempFile(msWordDocFileName);
        final File[] files = {msWordDocFile};
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
    }

    @Test
    void testAddingMSWordDocx() throws IOException, InterruptedException, InvocationTargetException {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final var testNode = new SortableDefaultMutableTreeNode(new GroupInfo("Test Node"));
        final var pictureCollection = new PictureCollection();
        pictureCollection.getRootNode().add(testNode);

        final var msWordDocxFileName = "/MSWord.docx";
        final var msWordDocFile = copyResourceToTempFile(msWordDocxFileName);
        final File[] files = {msWordDocFile};
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
    }

    @Test
    void testAddingLibreOfficeText() throws InterruptedException, InvocationTargetException, IOException {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final var testNode = new SortableDefaultMutableTreeNode(new GroupInfo("Test Node"));
        final var pictureCollection = new PictureCollection();
        pictureCollection.getRootNode().add(testNode);

        final var libreOfficeOdtFileName = "/LibreOfficeText.odt";
        final var libreOfficeFile = copyResourceToTempFile(libreOfficeOdtFileName);
        final File[] files = {libreOfficeFile};
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
    }

}