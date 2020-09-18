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
            final Path path = Files.createTempDirectory( "UnitTestsTempDir" );
            WebsiteGenerator.writeCss( path.toFile() );
            final File cssFile = new File(path.toFile(), "jpo.css");
            assertTrue( cssFile.exists() );
            Files.delete(cssFile.toPath() );
            Files.delete(path);
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
            final Path path = Files.createTempDirectory( "UnitTestsTempDir" );
            WebsiteGenerator.writeRobotsTxt( path.toFile() );
            final File robotsFile = new File(path.toFile(), "robots.txt");
            assertTrue( robotsFile.exists() );
            Files.delete(robotsFile.toPath());
            Files.delete( path );
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
            final Path path = Files.createTempDirectory( "UnitTestsTempDir" );
            WebsiteGenerator.writeJpoJs( path.toFile() );
            final File jsFile = new File(path.toFile(), "jpo.js");
            assertTrue( jsFile.exists() );
            Files.delete( jsFile.toPath() );
            Files.delete( path );
        } catch ( IOException ex ) {
            fail(  ex.getMessage() );
        }
    }

    @Test
    public void getFilenameRoot() {
        String root = WebsiteGenerator.getFilenameRoot("gaga.txt");
        assertEquals("gaga", root);
    }

}