package org.jpo.eventbus;

import org.jpo.datamodel.NodeStatisticsTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FileLoadRequestTest {

    @Test
    void makeFileLoadRequest() {
        try {
            final File existingFile = new File(Objects.requireNonNull(NodeStatisticsTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg")).toURI());
            final FileLoadRequest request = new FileLoadRequest(existingFile);
        } catch (final IllegalArgumentException e) {
            fail("There wasn't supposed to be an IllegalArgumentException in this test. Exception reads: " + e.getMessage());
        } catch (final URISyntaxException e) {
            fail("Test was supposed to create a request for a file that exists");
        }
    }

    @Test
    void makeFileLoadRequestInexistantFile() {
        try {
            final File inexistantFile = new File("no_such_file.txt");
            final FileLoadRequest request = new FileLoadRequest(inexistantFile);
            fail("An IllegalArgumentException was supposed to be thrown");
        } catch (final IllegalArgumentException e) {
            assertEquals("File \"no_such_file.txt\" must exist before we can load it!", e.getMessage());
            return;
        }
    }

    @Test
    void makeFileLoadRequestUnreadableFile() {
        Path tempDir = null;
        File unreadableFile = null;
        try {
            tempDir = Files.createTempDirectory("makeFileLoadRequestUnreadableFile");
            unreadableFile = new File(tempDir.toFile(), "unreadableFile.jpg");
            final FileWriter writer = new FileWriter(unreadableFile);
            writer.write("Some random text");
            writer.close();
            unreadableFile.setReadable(false);
            final FileLoadRequest request = new FileLoadRequest(unreadableFile);
            fail("An IllegalArgumentException was supposed to be thrown");
        } catch (final IllegalArgumentException e) {
            assertEquals("File \"" + unreadableFile + "\" must be readable for FileLoadRequest!", e.getMessage());
        } catch (final IOException e) {
            fail("Something went wrong in the test: " + e.getMessage());
        } finally {
            try {
                Files.delete(unreadableFile.toPath());
                Files.delete(tempDir);
            } catch (IOException e) {
                fail("Could no clean up from test: " + e.getMessage());
            }

        }
    }

    @Test
    void makeFileLoadRequestOnDirectory() {
        try {
            final File directory = new File(".");
            final FileLoadRequest request = new FileLoadRequest(directory);
        } catch (final IllegalArgumentException e) {
            assertEquals("\".\" is a directory. FileLoadRequest can only handle actual files.", e.getMessage());
            return;
        }
        fail("An IllegalArgumentException was supposed to be thrown");
    }


}