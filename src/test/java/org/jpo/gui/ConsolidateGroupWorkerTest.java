package org.jpo.gui;

import com.google.common.io.Files;
import junit.framework.TestCase;
import org.apache.commons.compress.utils.IOUtils;
import org.jpo.dataModel.PictureInfo;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Logger;

import static junit.framework.TestCase.*;

/*
 ConsolidateGroupWorkerTest.java: 

 Copyright (C) 2016-2019  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 without even the implied warranty of MERCHANTABILITY or FITNESS 
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 *
 * @author Richard Eigenmann
 */
public class ConsolidateGroupWorkerTest {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ConsolidateGroupWorkerTest.class.getName() );

    /**
     * Show that a null image file doesn't need to be moved.
     */
    @Test
    public void testNeedToMovePictureNull() {
        final PictureInfo pictureInfo = new PictureInfo();
        final File tempTargetDirectory = Files.createTempDir();

        try {
            ConsolidateGroupWorker.needToMovePicture(pictureInfo, tempTargetDirectory );
            fail("the needToMovePicture should not handle null inputs; the are invalid");
        } catch ( NullPointerException ex ) {
            // this is good
        }
        assertTrue(tempTargetDirectory.delete());
    }

    /**
     * Show that an image that doesn't exist doesn't need to be moved.
     */
    @Test
    public void testNeedToMoveNonexistentPicture() {
        final File tempSourceDirectory = Files.createTempDir();
        final File sourceImageFile = new File( tempSourceDirectory, "Image1.jpg" );
        // Java File object exists but not on the disk

        final PictureInfo pi = new PictureInfo();
        pi.setImageLocation( sourceImageFile );

        final File tempTargetDirectory = Files.createTempDir();
        boolean returnCode = ConsolidateGroupWorker.needToMovePicture(pi, tempTargetDirectory);
        assertTrue("Based on the info in the filenames the picture would need to be moved", returnCode);
        assertTrue(tempSourceDirectory.delete());
        assertTrue(tempTargetDirectory.delete());
    }

    /**
     * Test need to move Move a picture to the same directory returns false
     */
    @Test
    public void testNeedToMovePictureSameDirectory() {
        final File tempSourceDirectory = Files.createTempDir();
        final File sourceImageFile = new File( tempSourceDirectory, "Image1.jpg" );

        try ( InputStream in = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream( "exif-test-nikon-d100-1.jpg" );
                FileOutputStream fout = new FileOutputStream( sourceImageFile ) ) {
            Objects.requireNonNull(in, "The input stream of the image must not be null!");
            IOUtils.copy(Objects.requireNonNull(in), fout );
        } catch ( IOException ex ) {
            LOGGER.severe( ex.getMessage() );
            fail( "Failed to create test image file" );
        }
        // test that is really exists
        assertTrue( "The image File must exist and be readable", sourceImageFile.canRead() );

        PictureInfo pi = new PictureInfo();
        pi.setImageLocation( sourceImageFile );

        boolean returnCode = ConsolidateGroupWorker.needToMovePicture( pi, tempSourceDirectory );
        assertFalse( "Consolidation of a PictureInfo to the same directory should return false as nothing was moved", returnCode );
        assertTrue(sourceImageFile.delete());
        assertTrue(tempSourceDirectory.delete());
    }

    /**
     * Test need to Move a picture to a new directory
     */
    @Test
    public void testNeedToMovePictureNewDirectory() {
        final File tempSourceDirectory = Files.createTempDir();
        final File imageFile = new File( tempSourceDirectory, "Image1.jpg" );

        try ( InputStream in = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream( "exif-test-nikon-d100-1.jpg" );
                FileOutputStream fout = new FileOutputStream( imageFile ) ) {
            Objects.requireNonNull(in, "The input stream of the image must not be null!");
            IOUtils.copy(Objects.requireNonNull(in), fout );
        } catch ( IOException ex ) {
            LOGGER.severe( ex.getMessage() );
            fail( "Failed to create test image file" );
        }
        // test that is really exists
        assertTrue( "The image File must exist and be readable", imageFile.canRead() );

        PictureInfo pi = new PictureInfo();
        pi.setImageLocation( imageFile );

        File tempTargetDirectory = Files.createTempDir();

        final boolean returnCode = ConsolidateGroupWorker.needToMovePicture( pi, tempTargetDirectory );
        assertTrue( "Consolidation of a PictureInfo to a new directory should succeed", returnCode );

        assertTrue(imageFile.delete());
        assertTrue(tempTargetDirectory.delete());
        assertTrue(tempSourceDirectory.delete());
    }

    /**
     * Test if we would need to move a picture to a new directory or if it can
     * stay in the place it was.
     *
     * @see
     * <a href="http://stackoverflow.com/questions/28366433/file-canwrite-and-files-iswritable-not-giving-correct-value-on-linux">Stackoverflow</a>
     */
    @Test
    public void testNeedToMoveReadonlyPicture() {
        final File tempSourceDirectory = Files.createTempDir();
        final File sourceImageFile = new File( tempSourceDirectory, "ReadOnlyImage.jpg" );

        try ( InputStream in = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream( "exif-test-nikon-d100-1.jpg" );
                FileOutputStream fout = new FileOutputStream( sourceImageFile ) ) {
            Objects.requireNonNull(in, "The input stream of the image must not be null!");
            IOUtils.copy(Objects.requireNonNull(in), fout );
        } catch ( IOException ex ) {
            LOGGER.severe( ex.getMessage() );
            fail( "Failed to create test image file" );
        }
        assertTrue(sourceImageFile.setReadOnly());
        assertTrue( "The image File must exist and be readable", sourceImageFile.canRead() );
        if ( !System.getProperty( "user.name" ).equals( "root" ) ) {
            //This test doesn't work when running on Linux as root because root can always write to a file.
            assertFalse( "The image File must exist and be readonly", sourceImageFile.canWrite() );
        }

        PictureInfo pi = new PictureInfo();
        pi.setImageLocation( sourceImageFile );

        final File tempTargetDirectory = new File( tempSourceDirectory, "subdir" );

        final boolean returnCode = ConsolidateGroupWorker.needToMovePicture( pi, tempTargetDirectory );
        assertTrue( "Consolidation of a readonly PictureInfo to a new directory should return true", returnCode );
        assertTrue(sourceImageFile.delete());
        assertFalse(tempTargetDirectory.exists());
        assertTrue(tempSourceDirectory.delete());
    }

    /**
     * Show that consolidation of a PictureInfo with a null file succeeds
     * because they can't be moved.
     */
    @Test
    public void testMovePictureNull() {
        final File tempTargetDirectory = Files.createTempDir();

        try {
            boolean returnCode = ConsolidateGroupWorker.movePicture(new PictureInfo(), tempTargetDirectory );
            assertFalse( "Consolidation of a PictureInfo with a \"null\" highres file should return false", returnCode );
        } catch ( NullPointerException ex ) {
            assertTrue(tempTargetDirectory.delete());
            return;
        }
        fail( "Consolidation of a PictureInfo with a \"null\" highres file should throw a NPE" );
    }

    /**
     * Move a picture to the same directory and verify that it stays in the same
     * place.
     */
    @Test
    public void testMovePictureSameDirectory() {
        final File tempSourceDirectory = Files.createTempDir();
        final File sourceImageFile = new File( tempSourceDirectory, "Image1.jpg" );

        try ( InputStream in = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream( "exif-test-nikon-d100-1.jpg" );
                FileOutputStream fout = new FileOutputStream( sourceImageFile ) ) {
            Objects.requireNonNull(in, "The input stream of the image must not be null!");
            IOUtils.copy(Objects.requireNonNull(in), fout );
        } catch ( IOException ex ) {
            LOGGER.severe( ex.getMessage() );
            fail( "Failed to create test image file in test testMovePictureSameDirectory." );
        }
        assertTrue( "The image File must exist", sourceImageFile.exists() );
        assertTrue( "The image File must be readable", sourceImageFile.canRead() );

        final PictureInfo pi = new PictureInfo();
        pi.setImageLocation( sourceImageFile );

        boolean returnCode = ConsolidateGroupWorker.movePicture( pi, tempSourceDirectory );
        assertTrue( "Consolidation of a PictureInfo to the same directory should return true", returnCode );

        assertTrue( "The image File must be in the same place", sourceImageFile.exists() );
        assertTrue(sourceImageFile.delete());
        assertTrue(tempSourceDirectory.delete());
    }

    /**
     * Move a picture to a new directory
     */
    @Test
    public void testMovePictureNewDirectory() {
        final File tempSourceDirectory = Files.createTempDir();
        final File sourceImageFile = new File( tempSourceDirectory, "Image1.jpg" );

        try ( InputStream in = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream( "exif-test-nikon-d100-1.jpg" );
                FileOutputStream fout = new FileOutputStream( sourceImageFile ) ) {
            Objects.requireNonNull(in, "The input stream of the image must not be null!");
            IOUtils.copy(Objects.requireNonNull(in), fout );
        } catch ( IOException ex ) {
            LOGGER.severe( ex.getMessage() );
            fail( "Failed to create test image file" );
        }
        // test that is really exists
        assertTrue( "The image File must exist and be readable", sourceImageFile.canRead() );

        final PictureInfo pi = new PictureInfo();
        pi.setImageLocation( sourceImageFile );

        final File tempTargetDirectory = new File( tempSourceDirectory, "subdir" );
        assertTrue(tempTargetDirectory.mkdir());

        boolean returnCode = ConsolidateGroupWorker.movePicture( pi, tempTargetDirectory );
        assertTrue( "Consolidation of a PictureInfo to a new directory should succeed", returnCode );

        assertFalse( "The old image File must be gone", sourceImageFile.canRead() );
        final File newFile = pi.getImageFile();
        assertTrue( "The PictureInfo points to the new file where it is readable", newFile.canRead() );
        assertTrue(newFile.delete());
        assertTrue(tempTargetDirectory.delete());
        assertTrue(tempSourceDirectory.delete());
    }

    /**
     * Try to move a read only picture to a new directory and verify that this
     * succeeds.
     */
    @Test
    public void testMoveReadonlyPictureNewDirectory() {
        final File tempSourceDirectory = Files.createTempDir();
        final File sourceImageFile = new File( tempSourceDirectory, "ReadOnlyImage.jpg" );

        try ( InputStream in = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream( "exif-test-nikon-d100-1.jpg" );
                FileOutputStream fout = new FileOutputStream( sourceImageFile ) ) {
            Objects.requireNonNull(in, "The input stream of the image must not be null!");
            IOUtils.copy(Objects.requireNonNull(in), fout );
        } catch ( IOException ex ) {
            LOGGER.severe( ex.getMessage() );
            fail( "Failed to create test image file" );
        }
        assertTrue(sourceImageFile.setReadOnly());
        assertTrue( "The source image File must exist and be readable but we can't read file: " + sourceImageFile, sourceImageFile.canRead() );
        if ( !System.getProperty( "user.name" ).equals( "root" ) ) {
            // on Linux as root a file is always writable therefore bypassing this non-essential check
            assertFalse( "The source image File must exist and must not be writable but we can write to file: " + sourceImageFile, sourceImageFile.canWrite() );
        }

        final PictureInfo pictureInfo = new PictureInfo();
        pictureInfo.setImageLocation( sourceImageFile );

        final File tempTargetDirectory = Files.createTempDir();
        boolean returnCode = ConsolidateGroupWorker.movePicture(pictureInfo, tempTargetDirectory );
        assertTrue( "Consolidation of a readonly PictureInfo to a new directory should succeed but the move from " + sourceImageFile + " to " + tempTargetDirectory + " seems to have failed!", returnCode );

        assertFalse( "The old image File must be gone", sourceImageFile.exists() );
        assertTrue( "The PictureInfo points to the readable location", pictureInfo.getImageFile().canRead() );

        TestCase.assertEquals( "File is in the new Location", tempTargetDirectory, pictureInfo.getImageFile().getParentFile() );

        assertFalse(sourceImageFile.exists());
        assertTrue(tempSourceDirectory.delete());
        assertTrue(pictureInfo.getImageFile().delete());
        assertTrue(tempTargetDirectory.delete());
    }

}
