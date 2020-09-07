package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class JpoWriterTest {

    @Test
    public void writeCollectionDTD() {
        try {
            Path tempDirWithPrefix = Files.createTempDirectory("TestJpoWriter");
            System.out.println(tempDirWithPrefix);
            File tempDir = tempDirWithPrefix.toFile();
            File expectedDtdFile = new File(tempDir, "collection.dtd");
            assertFalse( expectedDtdFile.exists() );
            JpoWriter.writeCollectionDTD(tempDirWithPrefix.toFile());
            assertTrue( expectedDtdFile.exists() );
            assertTrue( expectedDtdFile.delete());
            assertTrue ( tempDir.delete() );
        } catch (IOException e) {
            fail("Failed to write the DTD. Exception: " + e.getMessage());
        }
    }
}