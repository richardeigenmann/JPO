package org.jpo.datamodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static org.jpo.datamodel.Tools.copyResourceToTempFile;
import static org.junit.jupiter.api.Assertions.*;

/*
Copyright (C) 2002-2026 Richard Eigenmann.
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

class MimeTypesTest {

    private static final Logger LOGGER = Logger.getLogger(MimeTypesTest.class.getName());


    @ParameterizedTest( name = "file {0} expectedMimeType {1} isAPicture {2} isADocument {3} isAMovie {4}")
    @CsvSource(delimiter = '|', value = {
            "/exif-test-sony-d700.jpg | image/jpeg | true | false | false",
            "/exif-test-nikon-d100-1.jpg | image/jpeg | true | false | false",
            "/gif.gif | image/gif | true | false | false",
            "/memorial_o876.hdr | application/envi.hdr | true | false | false",
            "/png.png | image/png | true | false | false",
            "/pnm.pnm | image/x-portable-pixmap | true | false | false",
            "/psd.psd | image/vnd.adobe.photoshop | true | false | false",
            "/sgi.sgi | image/x-rgb | true | false | false",
            "/tga.tga | image/x-tga | true | false | false",
            "/tiff_image.tiff | image/tiff | true | false | false",
            "/MSWord.doc | application/msword | false | true | false",
            "/MSWord.docx | application/vnd.openxmlformats-officedocument.wordprocessingml.document | false | true | false",
            "/LibreOfficeText.odt | application/vnd.oasis.opendocument.text | false | true | false",
            "/pdf-document.pdf | application/pdf | false | true | false",
            "/PXL_20220212_171902333.mp4 | video/mp4 | false | false | true",
    })
    void testMimeTypes(String inputFilename, String expectedMimeType,boolean isAPicture, boolean isADocument, boolean isAMovie) throws IOException{
        var resource = getClass().getResourceAsStream(inputFilename);
        assertNotNull(resource, "Could not find resource: " + inputFilename);
        try ( final var iis = javax.imageio.ImageIO.createImageInputStream(resource) ) {
            var inputFile = copyResourceToTempFile(inputFilename);
            assertEquals(expectedMimeType, MimeTypes.getMimeType(inputFile));
            assertEquals(isAPicture, MimeTypes.isAPicture(inputFile));
            assertEquals(isADocument, MimeTypes.isADocument(inputFile));
            assertEquals(isAMovie, MimeTypes.isAMovie(inputFile));
        }
    }

    @Test
    void testGetMimeTypeOfNull() {
        assertEquals("null", MimeTypes.getMimeType(new File("")));
    }

    @Test
    void testNoFileIsNotADocument() {
        var noFile = new File("");
        assertFalse(MimeTypes.isADocument(noFile));
    }

    @Test
    void testIsMovie() {
        var noFile = new File("");
        assertFalse(MimeTypes.isAMovie(noFile));
    }

    @Test
    void noPictureFile() {
        var noFile = new File("");
        assertFalse(MimeTypes.isAPicture(noFile));
    }


}