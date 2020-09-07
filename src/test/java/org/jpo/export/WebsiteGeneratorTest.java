package org.jpo.export;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class WebsiteGeneratorTest {

    /**
     * Test of cleanupFilename method, of class WebsiteGenerator.
     */
    @Test
    public void testCleanupFilename() {
        String filename = "directory\\file.xml";  // actually contains directory\file.xml
        String wanted = "directory_file.xml";  // actually contains directory\file.xml
        String got = WebsiteGenerator.cleanupFilename( filename );
        // A backslash could be made into an underscore
        assertEquals( wanted, got );
    }

    /**
     * Test of writeCss method, of class WebsiteGenerator.
     */
    @Test
    public void testWriteCss() {
        try {
            Path path = Files.createTempDirectory( "UnitTestsTempDir" );
            WebsiteGenerator.writeCss( path.toFile() );
            File cssFile = new File( path.toFile(), "jpo.css" );
            // org.jpo.css was supposed to exists in directory " + path.toString()
            assertTrue( cssFile.exists() );
            // The org.jpo.css file could not be deleted
            assertTrue( cssFile.delete() );
            // The temporary directory could not be deleted
            assertTrue(  path.toFile().delete() );
        } catch ( IOException ex ) {
            fail( ex.getMessage() );
        }
    }

    /**
     * Test of writeRobotsTxt method, of class WebsiteGenerator.
     */
    @Test
    public void testWriteRobotsTxt() {
        try {
            Path path = Files.createTempDirectory( "UnitTestsTempDir" );
            WebsiteGenerator.writeRobotsTxt( path.toFile() );
            File cssFile = new File( path.toFile(), "robots.txt" );
            // robots.txt was supposed to exists in directory " + path.toString()
            assertTrue( cssFile.exists() );
            // The robots.txt file could not be deleted
            assertTrue( cssFile.delete() );
            // The temporary directory could not be deleted
            assertTrue( path.toFile().delete() );
        } catch ( IOException ex ) {
            fail( ex.getMessage() );
        }
    }

    /**
     * Test of writeJpoJs method, of class WebsiteGenerator.
     */
    @Test
    public void testWriteJpoJs() {
        try {
            Path path = Files.createTempDirectory( "UnitTestsTempDir" );
            WebsiteGenerator.writeJpoJs( path.toFile() );
            File cssFile = new File( path.toFile(), "jpo.js" );
            // org.jpo.js was supposed to exists in directory " + path.toString()
            assertTrue( cssFile.exists() );
            // The org.jpo.js file could not be deleted
            assertTrue( cssFile.delete() );
            // The temporary directory could not be deleted
            assertTrue( path.toFile().delete() );
        } catch ( IOException ex ) {
            fail( "Was not supposed to fail with the following Exception: " + ex.getMessage() );
        }
    }

    @Test
    public void getFilenameRoot() {
        String root = WebsiteGenerator.getFilenameRoot("gaga.txt");
        assertEquals("gaga", root);
    }

}