package org.jpo.export;

import org.apache.commons.io.FileUtils;
import org.jpo.cache.JpoCache;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.GenerateWebsiteRequest;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class WebsiteGeneratorTest {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger(WebsiteGeneratorTest.class.getName());

    /**
     * Test of cleanupFilename method, of class WebsiteGenerator.
     */
    @Test
    public void testCleanupFilename() {
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
    public void testWriteCss() {
        try {
            final Path path = Files.createTempDirectory("UnitTestsTempDir");
            WebsiteGenerator.writeCss(path.toFile());
            final File cssFile = new File(path.toFile(), "jpo.css");
            assertTrue(cssFile.exists());
            Files.delete(cssFile.toPath());
            Files.delete(path);
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test of writeRobotsTxt method, of class WebsiteGenerator.
     */
    @Test
    public void testWriteRobotsTxt() {
        try {
            final Path path = Files.createTempDirectory("UnitTestsTempDir");
            WebsiteGenerator.writeRobotsTxt(path.toFile());
            final File robotsFile = new File(path.toFile(), "robots.txt");
            assertTrue(robotsFile.exists());
            Files.delete(robotsFile.toPath());
            Files.delete(path);
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test of writeJpoJs method, of class WebsiteGenerator.
     */
    @Test
    public void testWriteJpoJs() {
        try {
            final Path path = Files.createTempDirectory("UnitTestsTempDir");
            WebsiteGenerator.writeJpoJs(path.toFile());
            final File jsFile = new File(path.toFile(), "jpo.js");
            assertTrue(jsFile.exists());
            Files.delete(jsFile.toPath());
            Files.delete(path);
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testGenerateWebsite() {
        assumeFalse(GraphicsEnvironment.isHeadless()); // There is a Progress Bar involved

        // set up the request
        final GenerateWebsiteRequest request = new GenerateWebsiteRequestDefaultOptions();
        request.setOutputTarget(GenerateWebsiteRequest.OutputTarget.OUTPUT_LOCAL_DIRECTORY);
        request.setWriteRobotsTxt(true);
        request.setOpenWebsiteAfterRendering(false);
        request.setPictureNaming(GenerateWebsiteRequest.PictureNamingType.PICTURE_NAMING_BY_SEQUENTIAL_NUMBER);
        try {
            final Path tempDirWithPrefix = Files.createTempDirectory("Website");
            request.setTargetDirectory(tempDirWithPrefix.toFile());
            LOGGER.log(Level.INFO, "Generating website into directory: {0}", tempDirWithPrefix);

            final SortableDefaultMutableTreeNode rootNode = new SortableDefaultMutableTreeNode();
            rootNode.setUserObject(new GroupInfo("Root Node"));

            final SortableDefaultMutableTreeNode groupNode = new SortableDefaultMutableTreeNode();
            groupNode.setUserObject(new GroupInfo("Group Node"));
            request.setStartNode(groupNode);


            final SortableDefaultMutableTreeNode pi1 = new SortableDefaultMutableTreeNode();
            final File imageFile = new File(WebsiteGeneratorTest.class.getClassLoader().getResource("exif-test-nikon-d100-1.jpg").toURI());
            final PictureInfo pi = new PictureInfo(imageFile, "Image 1");
            pi1.setUserObject(pi);
            groupNode.add(pi1);
            request.setThumbnailWidth(350);
            request.setThumbnailHeight(250);

            // There is something very strange going on with the cache access
            JpoCache.removeFromHighresCache(imageFile);

        } catch (final IOException | URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            SwingUtilities.invokeAndWait(() -> {
                WebsiteGenerator myWebsiteGenerator = WebsiteGenerator.generateWebsite(request);
                while (!myWebsiteGenerator.isDone()) {
                    LOGGER.info("Waiting for website to finish rendering...");
                    try {
                        Thread.sleep(400);
                    } catch (final InterruptedException e) {
                        fail("Why did the loop to wait for the website to render get interrupted?");
                        Thread.currentThread().interrupt();
                    }
                }
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            LOGGER.severe("Why was the website generation interrupted?");
            LOGGER.severe(ex.getMessage());
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }

        final File jpoCssFile = new File(request.getTargetDirectory(), "jpo.css");
        LOGGER.log(Level.INFO, "Asserting that file {0} exists", jpoCssFile);
        assert (jpoCssFile.exists());
        final File jpoJsFile = new File(request.getTargetDirectory(), "jpo.js");
        LOGGER.log(Level.INFO, "Asserting that file {0} exists", jpoJsFile);
        assert (jpoJsFile.exists());
        final File robotsFile = new File(request.getTargetDirectory(), "robots.txt");
        LOGGER.log(Level.INFO, "Asserting that file {0} exists", robotsFile);
        assert (robotsFile.exists());
        final File indexFile = new File(request.getTargetDirectory(), "index.htm");
        LOGGER.log(Level.INFO, "Asserting that file {0} exists", indexFile);
        assert (indexFile.exists());
        final File lowresPicture = new File(request.getTargetDirectory(), "jpo_00001_l.jpg");
        LOGGER.log(Level.INFO, "Asserting that file {0} exists", lowresPicture);
        assert (lowresPicture.exists());
        final File midresPicture = new File(request.getTargetDirectory(), "jpo_00001_m.jpg");
        LOGGER.log(Level.INFO, "Asserting that file {0} exists", midresPicture);
        assert (midresPicture.exists());
        final File midresHtml = new File(request.getTargetDirectory(), "jpo_00001.htm");
        LOGGER.log(Level.INFO, "Asserting that file {0} exists", midresHtml);
        assert (midresHtml.exists());

        // cleanup
        try {
            FileUtils.deleteDirectory(request.getTargetDirectory());
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Could not delete directory {0}  Exception: {1}", new Object[]{request.getTargetDirectory().toPath(), e.getMessage()});
            fail(e.getMessage());
        }
    }

    @Test
    public void testGenerateZipFile() {
        final GenerateWebsiteRequest request = new GenerateWebsiteRequestDefaultOptions();
        request.setGenerateZipfile(true);
        request.setPictureNaming(GenerateWebsiteRequest.PictureNamingType.PICTURE_NAMING_BY_SEQUENTIAL_NUMBER);
        request.setSequentialStartNumber(5);
        final SortableDefaultMutableTreeNode startNode = new SortableDefaultMutableTreeNode();
        startNode.setUserObject(new GroupInfo("Root Node"));
        request.setStartNode(startNode);
        try {
            final Path tempDirWithPrefix = Files.createTempDirectory("GenerateZipFile");
            request.setTargetDirectory(tempDirWithPrefix.toFile());
            startNode.add(new SortableDefaultMutableTreeNode(new PictureInfo(new File(WebsiteGeneratorTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg").toURI()), "Picture 1")));
            WebsiteGenerator.generateZipfileTest(request);

            final File generatedFile = new File(request.getTargetDirectory(), request.getDownloadZipFileName());
            try (final ZipFile generatedZipFile = new ZipFile(generatedFile)) {
                assert (Files.exists(generatedFile.toPath()));
                assertEquals(1, generatedZipFile.size());
                final ZipEntry entry = generatedZipFile.getEntry("jpo_00005_h.jpg");
                assertNotNull(entry);
            }

            FileUtils.deleteDirectory(request.getTargetDirectory());
        } catch (final IOException | URISyntaxException e) {
            fail("Hit an unexpected IOException: " + e.getMessage());
        }
    }

    @Test
    public void testGenerateFolderIcon() {
        final GenerateWebsiteRequest request = new GenerateWebsiteRequestDefaultOptions();
        try {
            final Path tempDirWithPrefix = Files.createTempDirectory("GenerateFolderIcon");
            request.setTargetDirectory(tempDirWithPrefix.toFile());
            final List<File> files = new ArrayList<>();
            WebsiteGenerator.writeFolderIconTest(request, files);

            assertEquals(1, files.size());
            assert (Files.exists(files.get(0).toPath()));
            assertEquals(WebsiteGenerator.FOLDER_ICON, files.get(0).getName());

            FileUtils.deleteDirectory(request.getTargetDirectory());
        } catch (final IOException e) {
            fail("Hit an unexpected IOException: " + e.getMessage());
        }
    }


}