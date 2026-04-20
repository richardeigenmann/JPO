package org.jpo.datamodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.jpo.datamodel.Tools.copyResourceToTempFile;
import static org.junit.jupiter.api.Assertions.*;

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

class JpoImageIOTest {

    @ParameterizedTest( name = "{0} reader {1}")
    @CsvSource(delimiter = '|', value = {
            "/Thumbs.db | com.twelvemonkeys.imageio.plugins.thumbsdb.ThumbsDBImageReader",
            "/gif.gif | com.sun.imageio.plugins.gif.GIFImageReader",
            "/memorial_o876.hdr | com.twelvemonkeys.imageio.plugins.hdr.HDRImageReader",
            "/7zIcon.icns | com.twelvemonkeys.imageio.plugins.icns.ICNSImageReader",
            "/favicon.ico | com.twelvemonkeys.imageio.plugins.bmp.ICOImageReader",
            "/AmigaAmiga.iff | com.twelvemonkeys.imageio.plugins.iff.IFFImageReader",
            "/exif-test-nikon-d100-1.jpg | com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader",
            "/grape_with_path.jpg | com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader",
            "/pcx.pcx | com.twelvemonkeys.imageio.plugins.pcx.PCXImageReader",
            "/food.pct | com.twelvemonkeys.imageio.plugins.pict.PICTImageReader",
            "/png.png | com.sun.imageio.plugins.png.PNGImageReader",
            "/pnm.pnm | com.twelvemonkeys.imageio.plugins.pnm.PNMImageReader",
            "/psd.psd | com.twelvemonkeys.imageio.plugins.psd.PSDImageReader",
            "/sgi.sgi | com.twelvemonkeys.imageio.plugins.sgi.SGIImageReader",
            "/Ghostscript_Tiger.svg | com.twelvemonkeys.imageio.plugins.svg.SVGImageReader",
            "/tga.tga | com.twelvemonkeys.imageio.plugins.tga.TGAImageReader",
            "/tiff_image.tiff | com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader"
    })
    void testReader(String inputFilename, String expectedReaderClassName) throws IOException{
        var resource = getClass().getResourceAsStream(inputFilename);
        assertNotNull(resource, "Could not find resource: " + inputFilename);
        try ( final var iis = javax.imageio.ImageIO.createImageInputStream(resource) ) {

            var imageFile = copyResourceToTempFile(inputFilename);
            assertTrue(JpoImageIO.jvmHasReader(imageFile));

            final var reader = JpoImageIO.getImageIOReader(iis);
            assertNotNull(reader, "No reader found for " + inputFilename);

            final var actualClassName = reader.getClass().getName();
            assertTrue(actualClassName.startsWith(expectedReaderClassName),
                    () -> "Expected reader to start with " + expectedReaderClassName + " but got " + actualClassName);
            assertTrue(reader.toString().startsWith(expectedReaderClassName));
        }
    }

    @Test
    void doesntReadMsWord() {
        final var MS_WORD_FILE = "/MSWord.doc";
        try {
            var imageFile = copyResourceToTempFile(MS_WORD_FILE);
            assertFalse(JpoImageIO.jvmHasReader(imageFile));
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }



}