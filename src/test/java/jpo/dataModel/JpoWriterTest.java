package jpo.dataModel;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class JpoWriterTest {

    @Test
    public void writeCollectionDTD() {
        try {
            Path tempDirWithPrefix = Files.createTempDirectory("TestJpoWriter");
            System.out.println(tempDirWithPrefix);
            File tempDir = tempDirWithPrefix.toFile();
            File expectedDtdFile = new File(tempDir, "collection.dtd");
            assertFalse("File should not exist", expectedDtdFile.exists() );
            JpoWriter.writeCollectionDTD(tempDirWithPrefix.toFile());
            assertTrue("File should exist after writing", expectedDtdFile.exists() );
            assertTrue("could not delete the temp file", expectedDtdFile.delete());
            assertTrue ("could not delete the temp directory", tempDir.delete() );
        } catch (IOException e) {
            fail("Failed to write the DTD. Exception: " + e.getMessage());
        }
    }
}