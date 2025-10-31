package org.jpo.gui;

import com.google.common.hash.Hashing;
import org.jpo.datamodel.SourcePicture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2023-2025 Richard Eigenmann.
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

 class SourcePictureTest {

     @BeforeAll
     static void beforeAll() {
         Settings.loadSettings(); // We need to start the cache
     }

     @Test
     void getHeight() {
         final var sourcePicture = new SourcePicture();
         final var NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
         final var imageUrl = this.getClass().getClassLoader().getResource(NIKON_D100_IMAGE);
         try {
             final var imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
             final var hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(hash.toString(), imageFile, 0.0);
             assertEquals(233, sourcePicture.getHeight());
         } catch (final URISyntaxException| IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void getHeightWithRotation() {
         final var sourcePicture = new SourcePicture();
         final var NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
         final var imageUrl = this.getClass().getClassLoader().getResource(NIKON_D100_IMAGE);
         try {
             final var imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
             final var hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(hash.toString(), imageFile, 90.0);
             assertEquals(350, sourcePicture.getHeight());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void getHeightNullPicture() {
         final var sourcePicture = new SourcePicture();
         assertEquals(0, sourcePicture.getHeight());
     }

     @Test
     void getWidth() {
         final var sourcePicture = new SourcePicture();
         final var NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
         final var imageUrl = this.getClass().getClassLoader().getResource(NIKON_D100_IMAGE);
         File imageFile;
         try {
             imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
             final var hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(hash.toString(), imageFile, 0.0);
             assertEquals(350, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }

     }

     @Test
     void getWidthWithRotation() {
         final var sourcePicture = new SourcePicture();
         final var NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
         final var imageUrl = this.getClass().getClassLoader().getResource(NIKON_D100_IMAGE);
         File imageFile;
         try {
             imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
             final var hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(hash.toString(), imageFile, 90.0);
             assertEquals(233, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }

     }

     @Test
     void getWidthNullPicture() {
         final var sourcePicture = new SourcePicture();
         assertEquals(0, sourcePicture.getWidth());
     }

     @Test
     void getSize() {
         final var sourcePicture = new SourcePicture();
         final var NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
         final var imageUrl = this.getClass().getClassLoader().getResource(NIKON_D100_IMAGE);
         try {
             final var imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
             final var hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(hash.toString(), imageFile, 0.0);
             assertEquals(new Dimension(350, 233), sourcePicture.getSize());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }

     }

     @Test
     void getSizeWithRotation() {
         final var sourcePicture = new SourcePicture();
         final var NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
         final var imageUrl = this.getClass().getClassLoader().getResource(NIKON_D100_IMAGE);
         File imageFile;
         try {
             imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
             final var hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(hash.toString(), imageFile, 270.0);
             assertEquals(new Dimension(233, 350), sourcePicture.getSize());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }

     }

     @Test
     void getSizeNullPicture() {
         final var sourcePicture = new SourcePicture();
         assertEquals(new Dimension(0, 0), sourcePicture.getSize());
     }

     @Test
     void testJpgImage() {
         final var sourcePicture = new SourcePicture();
         final var JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
         final var imageUrl = this.getClass().getClassLoader().getResource(JPG_IMAGE_FILE);
         try {
             final var imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(233, sourcePicture.getHeight());
             assertEquals(350, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testBmpImage() {
         assumeFalse(GraphicsEnvironment.isHeadless());
         final var sourcePicture = new SourcePicture();
         final var BMP_IMAGE_FILE = "bmp.bmp";
         final var imageUrl = this.getClass().getClassLoader().getResource(BMP_IMAGE_FILE);
         try {
             final var imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(100, sourcePicture.getHeight());
             assertEquals(150, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testTiffImage() {
         assumeFalse(GraphicsEnvironment.isHeadless());
         final SourcePicture s = new SourcePicture();
         final String TIFF_IMAGE_FILE = "tiff_image.tiff";
         final URL imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(TIFF_IMAGE_FILE));
         try {
             final File imageFile = new File(imageUrl.toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             s.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(s.getSourceBufferedImage());
             assertEquals(100, s.getHeight());
             assertEquals(200, s.getWidth());
        } catch (final URISyntaxException | IOException e) {
            fail(e.getMessage());
        }
    }

     @Test
     void testHdrImage() {
         assumeFalse(GraphicsEnvironment.isHeadless());
         final var sourcePicture = new SourcePicture();
         final var HDR_IMAGE_FILE = "memorial_o876.hdr";
         final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(HDR_IMAGE_FILE));
         try {
             final var imageFile = new File(imageUrl.toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(768, sourcePicture.getHeight());
             assertEquals(512, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testSvgImage() {
         assumeFalse(GraphicsEnvironment.isHeadless());
         final var sourcePicture = new SourcePicture();
         final var SVG_IMAGE_FILE = "Ghostscript_Tiger.svg";
         final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(SVG_IMAGE_FILE));
         try {
             final var imageFile = new File(imageUrl.toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             // Size is not stable. Sometimes we get 400, Sometimes 900
             Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Height of {0} is {1}", new Object[]{SVG_IMAGE_FILE, sourcePicture.getHeight()});
             assertTrue(sourcePicture.getHeight() == 400 || sourcePicture.getHeight() == 900);
             Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Width of {0} is {1}", new Object[]{SVG_IMAGE_FILE, sourcePicture.getWidth()});
             assertTrue(sourcePicture.getWidth() == 900 || sourcePicture.getWidth() == 800);
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
        }
    }

     @Test
     void testPnmImage() {
         final var sourcePicture = new SourcePicture();
         final var PNM_IMAGE_FILE = "pnm.pnm";
         final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PNM_IMAGE_FILE));
         try {
             final var imageFile = new File(imageUrl.toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(100, sourcePicture.getHeight());
             assertEquals(150, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testSgiImage() {
         assumeFalse(GraphicsEnvironment.isHeadless());
         final var sourcePicture = new SourcePicture();
         final var SGI_IMAGE_FILE = "sgi.sgi";
         final var imageUrl = this.getClass().getClassLoader().getResource(SGI_IMAGE_FILE);
         try {
             final var imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(100, sourcePicture.getHeight());
             assertEquals(150, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testTgaImage() {
         assumeFalse(GraphicsEnvironment.isHeadless());
         final var sourcePicture = new SourcePicture();
         final var TGA_IMAGE_FILE = "tga.tga";
         final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(TGA_IMAGE_FILE));
         try {
             final var imageFile = new File(imageUrl.toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(100, sourcePicture.getHeight());
             assertEquals(150, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testPsdImage() {
         final var sourcePicture = new SourcePicture();
         final var PSD_IMAGE_FILE = "psd.psd";
         final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PSD_IMAGE_FILE));
         try {
             final var imageFile = new File(imageUrl.toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(100, sourcePicture.getHeight());
             assertEquals(150, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }

    }

     @Test
     void testIcoImage() {
         assumeFalse(GraphicsEnvironment.isHeadless());
         final var sourcePicture = new SourcePicture();
         final var ICO_IMAGE_FILE = "favicon.ico";
         final var imageUrl = this.getClass().getClassLoader().getResource(ICO_IMAGE_FILE);
         try {
             final var imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(64, sourcePicture.getHeight());
             assertEquals(64, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }

    }

     @Test
     void testPngImage() {
         assumeFalse(GraphicsEnvironment.isHeadless());
         final var sourcePicture = new SourcePicture();
         final var PNG_IMAGE_FILE = "png.png";
         final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PNG_IMAGE_FILE));
         try {
             final var imageFile = new File(imageUrl.toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(100, sourcePicture.getHeight());
             assertEquals(150, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testGifImage() {
         assumeFalse(GraphicsEnvironment.isHeadless());
         final var sourcePicture = new SourcePicture();
         final var GIF_IMAGE_FILE = "gif.gif";
         final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(GIF_IMAGE_FILE));
         try {
             final var imageFile = new File(imageUrl.toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(100, sourcePicture.getHeight());
             assertEquals(150, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testIffImage() {
         final var sourcePicture = new SourcePicture();
         final var IFF_IMAGE_FILE = "AmigaAmiga.iff";
         final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(IFF_IMAGE_FILE));
         try {
             final var imageFile = new File(imageUrl.toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(150, sourcePicture.getHeight());
             assertEquals(200, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testPcxImage() {
         assumeFalse(GraphicsEnvironment.isHeadless());
         final var sourcePicture = new SourcePicture();
         final var PCX_IMAGE_FILE = "pcx.pcx";
         final var imageUrl = this.getClass().getClassLoader().getResource(PCX_IMAGE_FILE);
         try {
             final var imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(100, sourcePicture.getHeight());
             assertEquals(150, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testPctImage() {
         assumeFalse(GraphicsEnvironment.isHeadless());
         final var sourcePicture = new SourcePicture();
         final var PICT_IMAGE_FILE = "food.pct";
         final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PICT_IMAGE_FILE));
         try {
             final var imageFile = new File(imageUrl.toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(194, sourcePicture.getHeight());
             assertEquals(146, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testClipPathImage() {
         final var sourcePicture = new SourcePicture();
         final var CLIP_PATH_IMAGE_FILE = "grape_with_path.jpg";
         final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(CLIP_PATH_IMAGE_FILE));
         try {
             final var imageFile = new File(imageUrl.toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(1800, sourcePicture.getHeight());
             assertEquals(857, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testIcnsImage() {
         assumeFalse(GraphicsEnvironment.isHeadless());
         final var sourcePicture = new SourcePicture();
         final var ICNS_IMAGE_FILE = "7zIcon.icns";
         final var imageUrl = SourcePictureTest.class.getClassLoader().getResource(ICNS_IMAGE_FILE);
         try {
             final var imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(16, sourcePicture.getHeight());
             assertEquals(16, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testThumbsDbImage() {
         final var sourcePicture = new SourcePicture();
         final var THUMBS_DB_IMAGE_FILE = "Thumbs.db";
         final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(THUMBS_DB_IMAGE_FILE));
         try {
             final var imageFile = new File(imageUrl.toURI());
             final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(96, sourcePicture.getHeight());
             assertEquals(96, sourcePicture.getWidth());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testSequentialLoad() {
         final var sourcePicture = new SourcePicture();
         final var JPG_IMAGE_FILE_1 = "exif-test-nikon-d100-1.jpg";
         final var JPG_IMAGE_FILE_2 = "exif-test-canon-eos-350d.jpg";
         final var imageUrl_1 = this.getClass().getClassLoader().getResource(JPG_IMAGE_FILE_1);
         final var imageUrl_2 = this.getClass().getClassLoader().getResource(JPG_IMAGE_FILE_2);
         try {
             final var imageFile_1 = new File(Objects.requireNonNull(imageUrl_1).toURI());
             final var imageFileSha256Hash_1 = com.google.common.io.Files.asByteSource(imageFile_1).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash_1.toString(), imageFile_1, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(350, sourcePicture.getWidth());
             assertEquals(233, sourcePicture.getHeight());

             final var imageFile_2 = new File(Objects.requireNonNull(imageUrl_2).toURI());
             final var imageFileSha256Hash_2 = com.google.common.io.Files.asByteSource(imageFile_2).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash_2.toString(), imageFile_2, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(2496, sourcePicture.getWidth());
             assertEquals(1664, sourcePicture.getHeight());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }

     @Test
     void testSequentialLoadWithAbort() {
         final var sourcePicture = new SourcePicture();
         final var JPG_IMAGE_FILE_1 = "exif-test-nikon-d100-1.jpg";
         final var JPG_IMAGE_FILE_2 = "exif-test-canon-eos-350d.jpg";
         final var imageUrl_1 = this.getClass().getClassLoader().getResource(JPG_IMAGE_FILE_1);
         final var imageUrl_2 = this.getClass().getClassLoader().getResource(JPG_IMAGE_FILE_2);
         try {
             final var imageFile_1 = new File(Objects.requireNonNull(imageUrl_1).toURI());
             final var imageFileSha256Hash_1 = com.google.common.io.Files.asByteSource(imageFile_1).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash_1.toString(), imageFile_1, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(350, sourcePicture.getWidth());
             assertEquals(233, sourcePicture.getHeight());
             sourcePicture.stopLoading();

             final var imageFile_2 = new File(Objects.requireNonNull(imageUrl_2).toURI());
             final var imageFileSha256Hash_2 = com.google.common.io.Files.asByteSource(imageFile_2).hash(Hashing.sha256());
             sourcePicture.loadPicture(imageFileSha256Hash_2.toString(), imageFile_2, 0.0);
             assertNotNull(sourcePicture.getSourceBufferedImage());
             assertEquals(2496, sourcePicture.getWidth());
             assertEquals(1664, sourcePicture.getHeight());
         } catch (final URISyntaxException | IOException e) {
             fail(e.getMessage());
         }
     }


}