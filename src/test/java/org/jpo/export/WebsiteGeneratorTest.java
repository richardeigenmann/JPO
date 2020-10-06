package org.jpo.export;

import org.apache.commons.io.FileUtils;
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
import java.util.logging.Level;
import java.util.logging.Logger;

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
        final String filename = "directory\\file.xml";  // actually contains directory\file.xml
        final String wanted = "directory_file.xml";
        // A backslash could be made into an underscore
        assertEquals(wanted, WebsiteGenerator.cleanupFilename(filename));
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
    public void getFilenameRoot() {
        String root = WebsiteGenerator.getFilenameRoot("gaga.txt");
        assertEquals("gaga", root);
    }

    @Test
    public void testGenerateWebsite() {
        assumeFalse(GraphicsEnvironment.isHeadless()); // There is a Progress Bar involved

        // set up the request
        final GenerateWebsiteRequest request = new GenerateWebsiteRequestDefaultOptions();
        request.setOutputTarget(GenerateWebsiteRequest.OutputTarget.OUTPUT_LOCAL_DIRECTORY);
        request.setWriteRobotsTxt(true);
        request.setOpenWebsiteAfterRendering(false);
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
            PictureInfo pi = new PictureInfo(imageFile, "Image 1");
            pi1.setUserObject(pi);
            groupNode.add(pi1);
            request.setThumbnailWidth(350);
            request.setThumbnailHeight(250);

        } catch (final IOException | URISyntaxException e) {
            fail(e.getMessage());
        }

        // run the wizard
        try {
            SwingUtilities.invokeAndWait(() -> {
                WebsiteGenerator.generateWebsite(request);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }

        // Since the EDT is single threaded, this should wait for the WebsiteGenerator to finish before we do the assertions
        try {
            SwingUtilities.invokeAndWait(() -> {
                final File jpoCssFile = new File(request.getTargetDirectory(), "jpo.css");
                LOGGER.log(Level.INFO, "Asserting that file {0} exists", jpoCssFile);
                assert (jpoCssFile.exists());
                final File robotsFile = new File(request.getTargetDirectory(), "robots.txt");
                LOGGER.log(Level.INFO, "Asserting that file {0} exists", robotsFile);
                assert (robotsFile.exists());
                final File indexFile = new File(request.getTargetDirectory(), "index.htm");
                LOGGER.log(Level.INFO, "Asserting that file {0} exists", indexFile);
                assert (indexFile.exists());
                try {
                    FileUtils.deleteDirectory(request.getTargetDirectory());
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Could not delete directory {0}  Exception: {1}", new Object[]{request.getTargetDirectory().toPath(), e.getMessage()});
                    fail(e.getMessage());
                }
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}