package org.jpo.datamodel;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/*
Copyright (C) 2002-2023 Richard Eigenmann.
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

    @Test
    void testGetMimeType() {
        assertEquals("null", MimeTypes.getMimeType(new File("")));
        try {
            assertEquals("image/jpeg",
                    MimeTypes.getMimeType(
                            new File(this.getClass().getClassLoader().getResource(
                                    "exif-test-sony-d700.jpg").toURI())));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            assertEquals("image/jpeg",
                    MimeTypes.getMimeType(
                            new File(this.getClass().getClassLoader().getResource(
                                    "exif-test-sony-d700.jpg").toURI())));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            assertEquals("application/msword",
                    MimeTypes.getMimeType(
                            new File(this.getClass().getClassLoader().getResource(
                                    "MSWord.doc").toURI())));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    MimeTypes.getMimeType(
                            new File(this.getClass().getClassLoader().getResource(
                                    "MSWord.docx").toURI())));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            assertEquals("application/vnd.oasis.opendocument.text",
                    MimeTypes.getMimeType(
                            new File(this.getClass().getClassLoader().getResource(
                                    "LibreOfficeText.odt").toURI())));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            assertEquals("video/mp4",
                    MimeTypes.getMimeType(
                            new File(this.getClass().getClassLoader().getResource(
                                    "PXL_20220212_171902333.mp4").toURI())));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testIsADocument() {
        var noFile = new File("");
        assertFalse(MimeTypes.isADocument(noFile));

        try {
            final var image = "exif-test-sony-d700.jpg";
            final var imageFile = new File(this.getClass().getClassLoader().getResource(image).toURI());
            assertFalse(MimeTypes.isADocument(imageFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            final var msWordDoc = "MSWord.doc";
            final var msWordDocFile = new File(this.getClass().getClassLoader().getResource(msWordDoc).toURI());
            assert (MimeTypes.isADocument(msWordDocFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            final var msWordDocx = "MSWord.docx";
            final var msWordDocxFile = new File(this.getClass().getClassLoader().getResource(msWordDocx).toURI());
            assert (MimeTypes.isADocument(msWordDocxFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            final var libreOfficeOdt = "LibreOfficeText.odt";
            final var libreOfficeOdtFile = new File(this.getClass().getClassLoader().getResource(libreOfficeOdt).toURI());
            assert (MimeTypes.isADocument(libreOfficeOdtFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            final var movie = "PXL_20220212_171902333.mp4";
            final var movieFile = new File(this.getClass().getClassLoader().getResource(movie).toURI());
            assertFalse(MimeTypes.isADocument(movieFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testIsMovie() {
        var noFile = new File("");
        assertFalse(MimeTypes.isAMovie(noFile));

        try {
            final var image = "exif-test-sony-d700.jpg";
            final var imageFile = new File(this.getClass().getClassLoader().getResource(image).toURI());
            assertFalse(MimeTypes.isAMovie(imageFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            final var msWordDoc = "MSWord.doc";
            final var msWordDocFile = new File(this.getClass().getClassLoader().getResource(msWordDoc).toURI());
            assertFalse(MimeTypes.isAMovie(msWordDocFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            final var msWordDocx = "MSWord.docx";
            final var msWordDocxFile = new File(this.getClass().getClassLoader().getResource(msWordDocx).toURI());
            assertFalse(MimeTypes.isAMovie(msWordDocxFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            final var libreOfficeOdt = "LibreOfficeText.odt";
            final var libreOfficeOdtFile = new File(this.getClass().getClassLoader().getResource(libreOfficeOdt).toURI());
            assertFalse(MimeTypes.isAMovie(libreOfficeOdtFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            final var movie = "PXL_20220212_171902333.mp4";
            final var movieFile = new File(this.getClass().getClassLoader().getResource(movie).toURI());
            assert (MimeTypes.isAMovie(movieFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void noPictureFile() {
        var noFile = new File("");
        assertFalse(MimeTypes.isAPicture(noFile));
    }

    @Test
    void jpgIsAPicture() {
        try {
            final var image = "exif-test-sony-d700.jpg";
            final var imageFile = new File(this.getClass().getClassLoader().getResource(image).toURI());
            assert (MimeTypes.isAPicture(imageFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void hdrIsAPicture() {
        try {
            final var image = "memorial_o876.hdr";
            final var imageFile = new File(this.getClass().getClassLoader().getResource(image).toURI());
            assert (MimeTypes.isAPicture(imageFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void pngIsAPicture() {
        try {
            final var image = "png.png";
            final var imageFile = new File(this.getClass().getClassLoader().getResource(image).toURI());
            assert (MimeTypes.isAPicture(imageFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }
    @Test
    void pnmIsAPicture() {
        try {
            final var image = "pnm.pnm";
            final var imageFile = new File(this.getClass().getClassLoader().getResource(image).toURI());
            assert (MimeTypes.isAPicture(imageFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }
    @Test
    void gifIsAPicture() {
        try {
            final var image = "gif.gif";
            final var imageFile = new File(this.getClass().getClassLoader().getResource(image).toURI());
            assert (MimeTypes.isAPicture(imageFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }
    @Test
    void psdIsAPicture() {
        try {
            final var image = "psd.psd";
            final var imageFile = new File(this.getClass().getClassLoader().getResource(image).toURI());
            assert (MimeTypes.isAPicture(imageFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Disabled("Travis doesn't like this")
    void sgiIsAPicture() {
        try {
            final var image = "sgi.sgi";
            final var imageFile = new File(this.getClass().getClassLoader().getResource(image).toURI());
            LOGGER.log(Level.INFO, "File: {0} isAPicture: {1}", new Object[]{imageFile, MimeTypes.isAPicture(imageFile)});
            assert (MimeTypes.isAPicture(imageFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Disabled("Travis doesn't like this")
    void tgaIsAPicture() {
        try {
            final var image = "tga.tga";
            final var imageFile = new File(this.getClass().getClassLoader().getResource(image).toURI());
            assert (MimeTypes.isAPicture(imageFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void tiffIsAPicture() {
        try {
            final var image = "tiff_image.tiff";
            final var imageFile = new File(this.getClass().getClassLoader().getResource(image).toURI());
            assert (MimeTypes.isAPicture(imageFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void docIsNotAPicture() {
        try {
            final var msWordDoc = "MSWord.doc";
            final var msWordDocFile = new File(this.getClass().getClassLoader().getResource(msWordDoc).toURI());
            assertFalse(MimeTypes.isAPicture(msWordDocFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void docxIsNotAPicture() {
        try {
            final var msWordDocx = "MSWord.docx";
            final var msWordDocxFile = new File(this.getClass().getClassLoader().getResource(msWordDocx).toURI());
            assertFalse(MimeTypes.isAPicture(msWordDocxFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void odtIsNotAPicture() {
        try {
            final var libreOfficeOdt = "LibreOfficeText.odt";
            final var libreOfficeOdtFile = new File(this.getClass().getClassLoader().getResource(libreOfficeOdt).toURI());
            assertFalse(MimeTypes.isAPicture(libreOfficeOdtFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void mp4IsNotAPicture() {
        try {
            final var movie = "PXL_20220212_171902333.mp4";
            final var movieFile = new File(this.getClass().getClassLoader().getResource(movie).toURI());
            assertFalse(MimeTypes.isAPicture(movieFile));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }
    @Test
    void pdfIsADDocument() {
        try {
            final var PDF_DOCUMENT = "pdf-document.pdf";
            final var pdfDocument = new File(this.getClass().getClassLoader().getResource(PDF_DOCUMENT).toURI());
            assert(MimeTypes.isADocument(pdfDocument));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

}