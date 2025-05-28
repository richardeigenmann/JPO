package org.jpo.export;

import org.assertj.swing.edt.GuiActionRunner;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.GenerateWebsiteRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2018-2024 Richard Eigenmann.
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

class WebsiteGeneratorTest {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger(WebsiteGeneratorTest.class.getName());

    /**
     * Test of cleanupFilename method, of class WebsiteGenerator.
     */
    @Test
    void testCleanupFilename() {
        assertEquals("file_space.jpg", WebsiteGenerator.cleanupFilename("file space.jpg"));
        assertEquals("file_space.jpg", WebsiteGenerator.cleanupFilename("file%20space.jpg"));
        assertEquals("file_and_ampersand.jpg", WebsiteGenerator.cleanupFilename("file&ampersand.jpg"));
        assertEquals("filelpipe.jpg", WebsiteGenerator.cleanupFilename("file|pipe.jpg"));
        assertEquals("file_less.jpg", WebsiteGenerator.cleanupFilename("file<less.jpg"));
        assertEquals("file_greater.jpg", WebsiteGenerator.cleanupFilename("file>greater.jpg"));
        assertEquals("file_at.jpg", WebsiteGenerator.cleanupFilename("file@at.jpg"));
        assertEquals("file_colon.jpg", WebsiteGenerator.cleanupFilename("file:colon.jpg"));
        assertEquals("file_dollar.jpg", WebsiteGenerator.cleanupFilename("file$dollar.jpg"));
        assertEquals("file_pound.jpg", WebsiteGenerator.cleanupFilename("file£pound.jpg"));
        assertEquals("file_caret.jpg", WebsiteGenerator.cleanupFilename("file^caret.jpg"));
        assertEquals("file_tilde.jpg", WebsiteGenerator.cleanupFilename("file~tilde.jpg"));
        assertEquals("file_doublequote.jpg", WebsiteGenerator.cleanupFilename("file\"doublequote.jpg"));
        assertEquals("file_singlequote.jpg", WebsiteGenerator.cleanupFilename("file'singlequote.jpg"));
        assertEquals("file_backtick.jpg", WebsiteGenerator.cleanupFilename("file`backtick.jpg"));
        assertEquals("file_questionmark.jpg", WebsiteGenerator.cleanupFilename("file?questionmark.jpg"));
        assertEquals("file_squarebracket.jpg", WebsiteGenerator.cleanupFilename("file[squarebracket.jpg"));
        assertEquals("file_closesquarebracket.jpg", WebsiteGenerator.cleanupFilename("file]closesquarebracket.jpg"));
        assertEquals("file_opencurlybrace.jpg", WebsiteGenerator.cleanupFilename("file{opencurlybrace.jpg"));
        assertEquals("file_closecurlybrace.jpg", WebsiteGenerator.cleanupFilename("file}closecurlybrace.jpg"));
        assertEquals("file_star.jpg", WebsiteGenerator.cleanupFilename("file*star.jpg"));
        assertEquals("file_plus.jpg", WebsiteGenerator.cleanupFilename("file+plus.jpg"));
        assertEquals("file_forwardslash.jpg", WebsiteGenerator.cleanupFilename("file/forwardslash.jpg"));
        assertEquals("file_backslash.jpg", WebsiteGenerator.cleanupFilename("file\\backslash.jpg"));
        assertEquals("file_percentage.jpg", WebsiteGenerator.cleanupFilename("file%percentage.jpg"));
    }

    /**
     * Test of writeCss method, of class WebsiteGenerator.
     */
    @Test
    void testWriteCss(@TempDir Path tempDir) {
        final ArrayList<File> websiteMemberFiles = new ArrayList<>();
        WebsiteGenerator.writeCss(tempDir.toFile(), websiteMemberFiles);
        final var cssFile = new File(tempDir.toFile(), "jpo.css");
        assertThat(cssFile).exists();
        assertEquals(1, websiteMemberFiles.size());
    }

    /**
     * Test of writeRobotsTxt method, of class WebsiteGenerator.
     */
    @Test
    void testWriteRobotsTxt(@TempDir Path tempDir) {
        final ArrayList<File> websiteMemberFiles = new ArrayList<>();
        WebsiteGenerator.writeRobotsTxt(tempDir.toFile(), websiteMemberFiles);
        final var robotsFile = new File(tempDir.toFile(), "robots.txt");
        assertThat(robotsFile).exists();
        assertEquals(1, websiteMemberFiles.size());
    }

    /**
     * Test of writeJpoJs method, of class WebsiteGenerator.
     */
    @Test
    void testWriteJpoJs(@TempDir Path tempDir) {
        final ArrayList<File> websiteMemberFiles = new ArrayList<>();
        WebsiteGenerator.writeJpoJs(tempDir.toFile(), websiteMemberFiles);
        final var jsFile = new File(tempDir.toFile(), "jpo.js");
        assertThat(jsFile).exists();
        assertEquals(1, websiteMemberFiles.size());
    }

    @Test
    void testGenerateWebsite(@TempDir Path tempDir) {
        //assumeFalse(GraphicsEnvironment.isHeadless()); // There is a Progress Bar involved
        assumeFalse( System.getProperty("os.name").toLowerCase().startsWith("win") ); // Doesn't work on Windows

        // set up the request
        final var request = new GenerateWebsiteRequestDefaultOptions();
        request.setOutputTarget(GenerateWebsiteRequest.OutputTarget.OUTPUT_LOCAL_DIRECTORY);
        request.setWriteRobotsTxt(true);
        request.setOpenWebsiteAfterRendering(false);
        request.setPictureNaming(GenerateWebsiteRequest.PictureNamingType.PICTURE_NAMING_BY_SEQUENTIAL_NUMBER);
        request.setGenerateMouseover(true);
        try {
            request.setTargetDirectory(tempDir.toFile());
            LOGGER.log(Level.INFO, "Generating website into directory: {0}\nAsserting that the directory exists and is writeable", tempDir);
            assertTrue(request.getTargetDirectory().exists());
            assertTrue(request.getTargetDirectory().canWrite());

            final var rootNode = new SortableDefaultMutableTreeNode();
            rootNode.setUserObject(new GroupInfo("Root Node"));

            final var groupNode = new SortableDefaultMutableTreeNode();
            groupNode.setUserObject(new GroupInfo("Group Node"));
            request.setStartNode(groupNode);

            final var pictureNode = new SortableDefaultMutableTreeNode();
            final var imageFile = new File(ClassLoader.getSystemResources("exif-test-nikon-d100-1.jpg").nextElement().toURI());
            assertTrue(imageFile.canRead());
            LOGGER.log(Level.INFO, "Test image file absolute path: {0}", imageFile.getAbsolutePath());
            final var pictureInfo = new PictureInfo(imageFile, "Image 1");
            pictureNode.setUserObject(pictureInfo);
            groupNode.add(pictureNode);
            request.setThumbnailWidth(350);
            request.setThumbnailHeight(250);
        } catch (final IOException | URISyntaxException e) {
            fail(e.getMessage());
        }

        LOGGER.log(Level.INFO, "Generating website in GuiActionRunner thread now...");
        var websiteGenerator = GuiActionRunner.execute(() -> WebsiteGenerator.generateWebsite(request));

        final var midresHtml = new File(request.getTargetDirectory(), "jpo_00001.htm");

        await().until(() -> {
            LOGGER.log(Level.INFO, "Await until running: State Value: {0} Done: {1} midresFile.exists: {2}", new Object[]{websiteGenerator.getState(), websiteGenerator.isDone(), midresHtml.exists()});
            return websiteGenerator.isDone() && midresHtml.exists();
        });

        LOGGER.log(Level.INFO, "Asserting that file {0} exists", midresHtml);
        assertThat (midresHtml).exists();
        final var jpoCssFile = new File(request.getTargetDirectory(), "jpo.css");
        LOGGER.log(Level.INFO, "Asserting that file {0} exists", jpoCssFile);
        assertThat(jpoCssFile).exists();
        final var jpoJsFile = new File(request.getTargetDirectory(), "jpo.js");
        LOGGER.log(Level.INFO, "Asserting that file {0} exists", jpoJsFile);
        assertThat (jpoJsFile).exists();
        final var robotsFile = new File(request.getTargetDirectory(), "robots.txt");
        LOGGER.log(Level.INFO, "Asserting that file {0} exists", robotsFile);
        assertThat (robotsFile).exists();
        final var indexFile = new File(request.getTargetDirectory(), "index.htm");
        LOGGER.log(Level.INFO, "Asserting that file {0} exists", indexFile);
        assertThat (indexFile).exists();
        final var lowresPicture = new File(request.getTargetDirectory(), "jpo_00001_l.jpg");
        LOGGER.log(Level.INFO, "Asserting that file {0} exists", lowresPicture);
        assertThat (lowresPicture).exists();
        final var midresPicture = new File(request.getTargetDirectory(), "jpo_00001_m.jpg");
        LOGGER.log(Level.INFO, "Asserting that file {0} exists", midresPicture);
        assertThat (midresPicture).exists();
    }

    @Test
    void testGenerateZipFile(@TempDir Path tempDir) {
        final var request = new GenerateWebsiteRequestDefaultOptions();
        request.setGenerateZipfile(true);
        request.setPictureNaming(GenerateWebsiteRequest.PictureNamingType.PICTURE_NAMING_BY_SEQUENTIAL_NUMBER);
        request.setSequentialStartNumber(5);
        final var startNode = new SortableDefaultMutableTreeNode();
        startNode.setUserObject(new GroupInfo("Root Node"));
        request.setStartNode(startNode);
        try {
            request.setTargetDirectory(tempDir.toFile());
            startNode.add(new SortableDefaultMutableTreeNode(new PictureInfo(new File(WebsiteGeneratorTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg").toURI()), "Picture 1")));
            WebsiteGenerator.generateZipfileTest(request);

            final File generatedFile = new File(request.getTargetDirectory(), request.getDownloadZipFileName());
            try (final ZipFile generatedZipFile = new ZipFile(generatedFile)) {
                assertThat (generatedFile).exists();
                assertEquals(1, generatedZipFile.size());
                assertNotNull(generatedZipFile.getEntry("jpo_00005_h.jpg"));
            }

        } catch (final IOException | URISyntaxException e) {
            fail("Hit an unexpected IOException: " + e.getMessage());
        }
    }

    @Test
    void testGenerateFolderIcon(@TempDir Path tempDir) {
        final var request = new GenerateWebsiteRequestDefaultOptions();
        try {
            request.setTargetDirectory(tempDir.toFile());
            final List<File> websiteMemberFiles = new ArrayList<>();
            WebsiteGenerator.writeFolderIconTest(request, websiteMemberFiles);

            assertEquals(1, websiteMemberFiles.size());
            assertThat(websiteMemberFiles.get(0)).exists();
            assertEquals(WebsiteGenerator.FOLDER_ICON, websiteMemberFiles.get(0).getName());
        } catch (final IOException e) {
            fail("Hit an unexpected IOException: " + e.getMessage());
        }
    }


    @Test
    void getStartIndex() {
        assertEquals(0, WebsiteGenerator.getStartIndex(0, 5));
        assertEquals(0, WebsiteGenerator.getStartIndex(1, 5));
        assertEquals(0, WebsiteGenerator.getStartIndex(4, 5));
        assertEquals(0, WebsiteGenerator.getStartIndex(5, 5));
        assertEquals(0, WebsiteGenerator.getStartIndex(15, 5));
        assertEquals(0, WebsiteGenerator.getStartIndex(16, 5));
        assertEquals(0, WebsiteGenerator.getStartIndex(19, 5));
        assertEquals(5, WebsiteGenerator.getStartIndex(20, 5));
        assertEquals(205, WebsiteGenerator.getStartIndex(223, 5));
    }


    @Test
    void getEndIndex() {
        assertEquals(5, WebsiteGenerator.getEndIndex(0, 3, 5));
        assertEquals(35, WebsiteGenerator.getEndIndex(0, 300, 5));
        assertEquals(135, WebsiteGenerator.getEndIndex(100, 300, 5));
    }
}