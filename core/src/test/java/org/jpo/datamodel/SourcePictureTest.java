package org.jpo.datamodel;

import com.google.common.hash.Hashing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2023-2026 Richard Eigenmann.
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

     @Test
     void getHeightWithRotation() throws IOException {
         final var sourcePicture = new SourcePicture();
         final var NIKON_D100_IMAGE = "/exif-test-nikon-d100-1.jpg";
         final var imageFile = Tools.copyResourceToTempFile(NIKON_D100_IMAGE);
         final var hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
         sourcePicture.loadPicture(hash.toString(), imageFile, 90.0);
         assertEquals(350, sourcePicture.getHeight());
     }

     @Test
     void getHeightNullPicture() {
         final var sourcePicture = new SourcePicture();
         assertEquals(0, sourcePicture.getHeight());
     }

     @Test
     void getWidthWithRotation() throws IOException {
         final var sourcePicture = new SourcePicture();
         final var NIKON_D100_IMAGE = "/exif-test-nikon-d100-1.jpg";
         final var imageFile = Tools.copyResourceToTempFile(NIKON_D100_IMAGE);
         final var hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
         sourcePicture.loadPicture(hash.toString(), imageFile, 90.0);
         assertEquals(233, sourcePicture.getWidth());
     }

     @Test
     void getWidthNullPicture() {
         final var sourcePicture = new SourcePicture();
         assertEquals(0, sourcePicture.getWidth());
     }

     @Test
     void getSizeWithRotation() throws IOException {
         final var sourcePicture = new SourcePicture();
         final var NIKON_D100_IMAGE = "/exif-test-nikon-d100-1.jpg";
         final var imageFile = Tools.copyResourceToTempFile(NIKON_D100_IMAGE);
         final var hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
         sourcePicture.loadPicture(hash.toString(), imageFile, 270.0);
         assertEquals(new Dimension(233, 350), sourcePicture.getSize());
     }

     @Test
     void getSizeNullPicture() {
         final var sourcePicture = new SourcePicture();
         assertEquals(new Dimension(0, 0), sourcePicture.getSize());
     }

     @ParameterizedTest( name = "file {0} expectedWidth {1} expectedHeight {2}")
     @CsvSource(delimiter = '|', value = {
         "/bmp.bmp | 150 | 100",
         "/Thumbs.db | 96 | 96",
         "/7zIcon.icns | 16 | 16",
         "/favicon.ico | 64 | 64",
         "/AmigaAmiga.iff | 200 | 150",
         "/grape_with_path.jpg | 857 | 1800",
         "/exif-test-nikon-d100-1.jpg | 350 | 233",
         "/gif.gif | 150 | 100",
         "/memorial_o876.hdr | 512 | 768",
         "/pcx.pcx | 150 | 100",
         "/food.pct | 146 | 194",
         "/png.png | 150 | 100",
         "/pnm.pnm | 150 | 100",
         "/psd.psd | 150 | 100",
         "/sgi.sgi | 150 | 100",
         "/tga.tga | 150 | 100",
         "/tiff_image.tiff | 200 | 100",
     })
     void testImageSize(String inputFilename, int expectedWidth, int expectedHeight) throws IOException {
         final var sourcePicture = new SourcePicture();
         final var imageFile = Tools.copyResourceToTempFile(inputFilename);
         final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
         sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
         assertNotNull(sourcePicture.getSourceBufferedImage());
         assertEquals(expectedHeight, sourcePicture.getHeight());
         assertEquals(expectedWidth, sourcePicture.getWidth());
     }

     @Test
     void testSvgImage() throws IOException {
         assumeFalse(GraphicsEnvironment.isHeadless());
         final var sourcePicture = new SourcePicture();
         final var SVG_IMAGE_FILE = "/Ghostscript_Tiger.svg";
         final var imageFile = Tools.copyResourceToTempFile(SVG_IMAGE_FILE);
         final var imageFileSha256Hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
         sourcePicture.loadPicture(imageFileSha256Hash.toString(), imageFile, 0.0);
         assertNotNull(sourcePicture.getSourceBufferedImage());
         // Size is not stable. Sometimes we get 400, Sometimes 900
         Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Height of {0} is {1}", new Object[]{SVG_IMAGE_FILE, sourcePicture.getHeight()});
         assertTrue(sourcePicture.getHeight() == 400 || sourcePicture.getHeight() == 900);
         Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Width of {0} is {1}", new Object[]{SVG_IMAGE_FILE, sourcePicture.getWidth()});
         assertTrue(sourcePicture.getWidth() == 900 || sourcePicture.getWidth() == 800);
     }

     @Test
     void testSequentialLoad() throws IOException {
         final var sourcePicture = new SourcePicture();
         final var JPG_IMAGE_FILE_1 = "/exif-test-nikon-d100-1.jpg";
         final var JPG_IMAGE_FILE_2 = "/exif-test-canon-eos-350d.jpg";
         final var imageFile_1 = Tools.copyResourceToTempFile(JPG_IMAGE_FILE_1);
         final var imageFileSha256Hash_1 = com.google.common.io.Files.asByteSource(imageFile_1).hash(Hashing.sha256());
         sourcePicture.loadPicture(imageFileSha256Hash_1.toString(), imageFile_1, 0.0);
         assertNotNull(sourcePicture.getSourceBufferedImage());
         assertEquals(350, sourcePicture.getWidth());
         assertEquals(233, sourcePicture.getHeight());

         final var imageFile_2 = Tools.copyResourceToTempFile(JPG_IMAGE_FILE_2);
         final var imageFileSha256Hash_2 = com.google.common.io.Files.asByteSource(imageFile_2).hash(Hashing.sha256());
         sourcePicture.loadPicture(imageFileSha256Hash_2.toString(), imageFile_2, 0.0);
         assertNotNull(sourcePicture.getSourceBufferedImage());
         assertEquals(2496, sourcePicture.getWidth());
         assertEquals(1664, sourcePicture.getHeight());
     }

     @Test
     void testSequentialLoadWithAbort() throws IOException {
         final var sourcePicture = new SourcePicture();
         final var JPG_IMAGE_FILE_1 = "/exif-test-nikon-d100-1.jpg";
         final var JPG_IMAGE_FILE_2 = "/exif-test-canon-eos-350d.jpg";
         final var imageFile_1 = Tools.copyResourceToTempFile(JPG_IMAGE_FILE_1);
         final var imageFileSha256Hash_1 = com.google.common.io.Files.asByteSource(imageFile_1).hash(Hashing.sha256());
         sourcePicture.loadPicture(imageFileSha256Hash_1.toString(), imageFile_1, 0.0);
         assertNotNull(sourcePicture.getSourceBufferedImage());
         assertEquals(350, sourcePicture.getWidth());
         assertEquals(233, sourcePicture.getHeight());
         sourcePicture.stopLoading();

         final var imageFile_2 = Tools.copyResourceToTempFile(JPG_IMAGE_FILE_2);
         final var imageFileSha256Hash_2 = com.google.common.io.Files.asByteSource(imageFile_2).hash(Hashing.sha256());
         sourcePicture.loadPicture(imageFileSha256Hash_2.toString(), imageFile_2, 0.0);
         assertNotNull(sourcePicture.getSourceBufferedImage());
         assertEquals(2496, sourcePicture.getWidth());
         assertEquals(1664, sourcePicture.getHeight());
     }


}