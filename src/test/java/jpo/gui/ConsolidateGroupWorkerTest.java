package jpo.gui;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 *
 * @author Richard Eigenmann
 */
public class ConsolidateGroupWorkerTest {

    /**
     * Creating a temporary directory to experiment with moving pictures
     */
    private static File tempDirectory1 = Files.createTempDir();

    @AfterClass
    public static void afterClass() {
        tempDirectory1.delete();
    }

    /**
     * Show that a null image file doesn't need to be moved.
     */
    @Test
    public void testNeedToMovePictureNull() {
        PictureInfo pi = new PictureInfo();

        try {
            boolean returnCode = ConsolidateGroupWorker.needToMovePicture( pi, tempDirectory1 );
            assertFalse( "Consolidation of a PictureInfo with a \"null\" image file should return false", returnCode );
        } catch ( NullPointerException ex ) {
            System.out.println( ex.getMessage() );
            Thread.dumpStack();
            fail( "Consolidation of a PictureInfo with a \"null\" image file should not throw a NPE" );
        }
    }

    /**
     * Test need to move Move a picture to the same directory
     */
    @Test
    public void testNeedToMovePictureSameDirectory() {
        // create the image file
        File imageFile = new File( tempDirectory1, "Image1.jpg" );

        try ( InputStream in = Settings.CLASS_LOADER.getResourceAsStream( "exif-test-nikon-d100-1.jpg" );
                FileOutputStream fout = new FileOutputStream( imageFile ) ) {
            IOUtils.copy( in, fout );
        } catch ( IOException ex ) {
            System.out.println( ex.getMessage() );
            fail( "Failed to create test image file" );
        }
        // test that is really exists
        assertTrue( "The image File must exist and be readable", imageFile.canRead() );

        PictureInfo pi = new PictureInfo();
        pi.setImageLocation( imageFile );

        boolean returnCode = ConsolidateGroupWorker.needToMovePicture( pi, tempDirectory1 );
        assertFalse( "Consolidation of a PictureInfo to the same directory should return false as nothing was moved", returnCode );
        imageFile.delete();
    }

    /**
     * Test need to Move a picture to a new directory
     */
    @Test
    public void testNeedToMovePictureNewDirectory() {
        // create the image file
        File imageFile = new File( tempDirectory1, "Image1.jpg" );

        try ( InputStream in = Settings.CLASS_LOADER.getResourceAsStream( "exif-test-nikon-d100-1.jpg" );
                FileOutputStream fout = new FileOutputStream( imageFile ) ) {
            IOUtils.copy( in, fout );
        } catch ( IOException ex ) {
            System.out.println( ex.getMessage() );
            fail( "Failed to create test image file" );
        }
        // test that is really exists
        assertTrue( "The image File must exist and be readable", imageFile.canRead() );

        PictureInfo pi = new PictureInfo();
        pi.setImageLocation( imageFile );

        File tempDirectory2 = new File( tempDirectory1, "subdir" );
        tempDirectory2.mkdir();

        boolean returnCode = ConsolidateGroupWorker.needToMovePicture( pi, tempDirectory2 );
        assertTrue( "Consolidation of a PictureInfo to a new directory should succeed", returnCode );

        imageFile.delete();
        tempDirectory2.delete();
    }

    /**
     * Test need to Move a read only picture to a new directory
     */
    @Test
    public void testNeedToMoveReadonlyPicture() {
        // create the image file
        File imageFile = new File( tempDirectory1, "ReadOnlyImage.jpg" );

        try ( InputStream in = Settings.CLASS_LOADER.getResourceAsStream( "exif-test-nikon-d100-1.jpg" );
                FileOutputStream fout = new FileOutputStream( imageFile ) ) {
            IOUtils.copy( in, fout );
        } catch ( IOException ex ) {
            System.out.println( ex.getMessage() );
            fail( "Failed to create test image file" );
        }
        assertTrue( "The test needs the image file to be read only.", imageFile.setReadOnly() );
        assertTrue( "The image File must exist and be readable", imageFile.canRead() );
        assertFalse( "The image File must exist and be readonly", imageFile.canWrite() );

        PictureInfo pi = new PictureInfo();
        pi.setImageLocation( imageFile );

        File tempDirectory2 = new File( tempDirectory1, "subdir" );
        tempDirectory2.mkdir();

        boolean returnCode = ConsolidateGroupWorker.needToMovePicture( pi, tempDirectory2 );
        assertTrue( "Consolidation of a readonly PictureInfo to a new directory should return true", returnCode );

        imageFile.setWritable( true );
        imageFile.delete();
        tempDirectory2.delete();
    }

    /**
     * Show that consolidation of a PictureInfo with a null highres file
     * succeeds because they can't be moved.
     */
    @Test
    public void testMoveHighresPictureNull() {
        PictureInfo pi = new PictureInfo();

        try {
            boolean returnCode = ConsolidateGroupWorker.movePicture( pi, tempDirectory1 );
            assertFalse( "Consolidation of a PictureInfo with a \"null\" highres file should return false", returnCode );
        } catch ( NullPointerException ex ) {
            return;
        }
        fail( "Consolidation of a PictureInfo with a \"null\" highres file should throw a NPE" );

    }

    /**
     * Move a picture to the same directory
     */
    @Test
    public void testMoveHighresPictureSameDirectory() {
        // create the image file
        File imageFile = new File( tempDirectory1, "Image1.jpg" );

        try ( InputStream in = Settings.CLASS_LOADER.getResourceAsStream( "exif-test-nikon-d100-1.jpg" );
                FileOutputStream fout = new FileOutputStream( imageFile ) ) {
            IOUtils.copy( in, fout );
        } catch ( IOException ex ) {
            System.out.println( ex.getMessage() );
            fail( "Failed to create test image file" );
        }
        // test that is really exists
        assertTrue( "The image File must exist and be readable", imageFile.canRead() );

        PictureInfo pi = new PictureInfo();
        pi.setImageLocation( imageFile );

        boolean returnCode = ConsolidateGroupWorker.movePicture( pi, tempDirectory1 );
        assertTrue( "Consolidation of a PictureInfo to the same directory should return true", returnCode );

        assertTrue( "The image File must be in the same place", imageFile.canRead() );
        imageFile.delete();
    }

    /**
     * Move a picture to a new directory
     */
    @Test
    public void testMoveHighresPictureNewDirectory() {
        // create the image file
        File imageFile = new File( tempDirectory1, "Image1.jpg" );

        try ( InputStream in = Settings.CLASS_LOADER.getResourceAsStream( "exif-test-nikon-d100-1.jpg" );
                FileOutputStream fout = new FileOutputStream( imageFile ) ) {
            IOUtils.copy( in, fout );
        } catch ( IOException ex ) {
            System.out.println( ex.getMessage() );
            fail( "Failed to create test image file" );
        }
        // test that is really exists
        assertTrue( "The image File must exist and be readable", imageFile.canRead() );

        PictureInfo pi = new PictureInfo();
        pi.setImageLocation( imageFile );

        File tempDirectory2 = new File( tempDirectory1, "subdir" );
        tempDirectory2.mkdir();

        boolean returnCode = ConsolidateGroupWorker.movePicture( pi, tempDirectory2 );
        assertTrue( "Consolidation of a PictureInfo to a new directory should succeed", returnCode );

        assertFalse( "The old image File must be gone", imageFile.canRead() );
        File newFile = pi.getImageFile();
        assertTrue( "The PictureInfo points to the new file where it is readable", newFile.canRead() );
        newFile.delete();
        tempDirectory2.delete();
    }

    /**
     * Move a read only picture to a new directory
     */
    @Test
    public void testMoveReadonlyPictureNewDirectory() {
        // create the image file
        File imageFile = new File( tempDirectory1, "ReadOnlyImage.jpg" );

        try ( InputStream in = Settings.CLASS_LOADER.getResourceAsStream( "exif-test-nikon-d100-1.jpg" );
                FileOutputStream fout = new FileOutputStream( imageFile ) ) {
            IOUtils.copy( in, fout );
        } catch ( IOException ex ) {
            System.out.println( ex.getMessage() );
            fail( "Failed to create test image file" );
        }
        assertTrue( "The test needs the image file to be read only.", imageFile.setReadOnly() );
        assertTrue( "The image File must exist and be readable", imageFile.canRead() );
        assertFalse( "The image File must exist and be readonly", imageFile.canWrite() );

        PictureInfo pi = new PictureInfo();
        pi.setImageLocation( imageFile );

        File tempDirectory2 = new File( tempDirectory1, "subdir" );
        tempDirectory2.mkdir();

        boolean returnCode = ConsolidateGroupWorker.movePicture( pi, tempDirectory2 );
        assertFalse( "Consolidation of a readonly PictureInfo to a new directory should fail", returnCode );

        assertTrue( "The old image File must still be there", imageFile.canRead() );
        File newFile = pi.getImageFile();
        assertTrue( "The PictureInfo points to the readable location", newFile.canRead() );

        TestCase.assertEquals( "Old File and new Location are the same", imageFile, newFile );

        imageFile.setWritable( true );
        imageFile.delete();
        tempDirectory2.delete();
    }

}
