package org.jpo.datamodel;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/*
 Copyright (C) 2022  Richard Eigenmann.
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

class ImageIOTest {
    @Test
    void testGetJpgImageIOReader() {
        final var JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(JPG_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasJpgImageReader() {
        final var JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(JPG_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetBmpImageIOReader() {
        final var BMP_IMAGE_FILE = "bmp.bmp";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(BMP_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.bmp.BMPImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasBmpImageReader() {
        final var BMP_IMAGE_FILE = "bmp.bmp";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(BMP_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetTiffImageIOReader() {
        final var TIFF_IMAGE_FILE = "tiff_image.tiff";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(TIFF_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasTiffImageReader() {
        final var TIFF_IMAGE_FILE = "tiff_image.tiff";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(TIFF_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetHdrImageIOReader() {
        final var HDR_IMAGE_FILE = "memorial_o876.hdr";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(HDR_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.hdr.HDRImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Disabled("doesn't work")
    void testHasHdrImageReader() {
        final var HDR_IMAGE_FILE = "memorial_o876.hdr";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(HDR_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Disabled("ImageIO PDF doesn't seem to work")
    void testGetPdfImageIOReader() {
        final var PDF_IMAGE_FILE = "pdf-document.pdf";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PDF_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.pdf.PDFImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Disabled("ImageIO PDF doesn't seem to work")
    void testHasPdfImageReader() {
        final var PDF_IMAGE_FILE = "pdf-document.pdf";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PDF_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetSvgImageIOReader() {
        final var SVG_IMAGE_FILE = "Ghostscript_Tiger.svg";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(SVG_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.svg.SVGImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasSvgImageReader() {
        final var SVG_IMAGE_FILE = "Ghostscript_Tiger.svg";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(SVG_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetPnmImageIOReader() {
        final var PNM_IMAGE_FILE = "pnm.pnm";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PNM_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.pnm.PNMImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasPnmImageReader() {
        final var PNM_IMAGE_FILE = "pnm.pnm";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PNM_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetSgiImageIOReader() {
        final var SGI_IMAGE_FILE = "sgi.sgi";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(SGI_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.sgi.SGIImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasSgiImageReader() {
        final var SGI_IMAGE_FILE = "sgi.sgi";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(SGI_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetTgaImageIOReader() {
        final var TGA_IMAGE_FILE = "tga.tga";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(TGA_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.tga.TGAImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasTgaImageReader() {
        final var TGA_IMAGE_FILE = "tga.tga";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(TGA_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetPsdImageIOReader() {
        final var PSD_IMAGE_FILE = "psd.psd";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PSD_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.psd.PSDImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasPsdImageReader() {
        final var PSD_IMAGE_FILE = "psd.psd";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PSD_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetIcoImageIOReader() {
        final var ICO_IMAGE_FILE = "favicon.ico";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(ICO_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.bmp.ICOImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasIcoImageReader() {
        final var ICO_IMAGE_FILE = "favicon.ico";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(ICO_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetPngImageIOReader() {
        final var PNG_IMAGE_FILE = "png.png";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PNG_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.sun.imageio.plugins.png.PNGImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasPngImageReader() {
        final var PNG_IMAGE_FILE = "png.png";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PNG_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetGifImageIOReader() {
        final var GIF_IMAGE_FILE = "gif.gif";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(GIF_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.sun.imageio.plugins.gif.GIFImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasGifImageReader() {
        final var GIF_IMAGE_FILE = "gif.gif";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(GIF_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetIffImageIOReader() {
        final var IFF_IMAGE_FILE = "AmigaAmiga.iff";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(IFF_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.iff.IFFImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasIffImageReader() {
        final var IFF_IMAGE_FILE = "AmigaAmiga.iff";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(IFF_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }


    @Test
    void testGetPcxImageIOReader() {
        final String PCX_IMAGE_FILE = "pcx.pcx";
        final URL imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PCX_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final ImageReader reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.pcx.PCXImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasPcxImageReader() {
        final var PCX_IMAGE_FILE = "pcx.pcx";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PCX_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetPctImageIOReader() {
        final var PICT_IMAGE_FILE = "food.pct";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PICT_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.pict.PICTImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasPctImageReader() {
        final var PICT_IMAGE_FILE = "food.pct";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(PICT_IMAGE_FILE));
        try {
            final File image;
            image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetClipPathImageIOReader() {
        final var CLIP_PATH_IMAGE_FILE = "grape_with_path.jpg";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(CLIP_PATH_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testHasClipPathImageReader() {
        final var CLIP_PATH_IMAGE_FILE = "grape_with_path.jpg";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(CLIP_PATH_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetIcnsImageIOReader() {
        final var ICNS_IMAGE_FILE = "7zIcon.icns";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(ICNS_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.icns.ICNSImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Disabled("doesn't work")
    void testHasIcnsImageReader() {
        final var ICNS_IMAGE_FILE = "7zIcon.icns";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(ICNS_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetThumbsDbImageIOReader() {
        final var THUMBS_DB_IMAGE_FILE = "Thumbs.db";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(THUMBS_DB_IMAGE_FILE));
        try (final var input = imageUrl.openStream();
             final var iis = javax.imageio.ImageIO.createImageInputStream(input)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.thumbsdb.ThumbsDBImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Disabled("doesn't work")
    void testHasThumbsDbImageReader() {
        final var THUMBS_DB_IMAGE_FILE = "Thumbs.db";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(THUMBS_DB_IMAGE_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertTrue(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void doesntReadMsWord() {
        final var MS_WORD_FILE = "MSWord.doc";
        final var imageUrl = Objects.requireNonNull(this.getClass().getClassLoader().getResource(MS_WORD_FILE));
        try {
            final var image = new File(imageUrl.toURI());
            assertFalse(org.jpo.datamodel.ImageIO.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

}