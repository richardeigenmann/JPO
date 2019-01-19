package jpo.gui;

import jpo.dataModel.Settings;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Objects;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.*;

public class SourcePictureTest {

    @BeforeClass
    public static void beforeClass() {
        Settings.loadSettings(); // We need to start the cache
    }

    @Test
    public void getHeight() {
        SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);

        s.loadPicture(imageUrl, 0.0);
        assertEquals("Height", 233, s.getHeight());
    }

    @Test
    public void getHeightWithRotation() {
        SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);

        s.loadPicture(imageUrl, 90.0);
        assertEquals("Height", 350, s.getHeight());
    }

    @Test
    public void getHeightNullPicture() {
        SourcePicture s = new SourcePicture();
        assertEquals("Height", 0, s.getHeight());
    }


    @Test
    public void getWidth() {
        SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);

        s.loadPicture(imageUrl, 0.0);
        assertEquals("Width", 350, s.getWidth());
    }

    @Test
    public void getWidthWithRotation() {
        SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);

        s.loadPicture(imageUrl, 90.0);
        assertEquals("Height", 233, s.getWidth());
    }

    @Test
    public void getWidthNullPicture() {
        SourcePicture s = new SourcePicture();
        assertEquals("Height", 0, s.getWidth());
    }

    @Test
    public void getSize() {
        SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);

        s.loadPicture(imageUrl, 0.0);
        assertEquals("Size", new Dimension(350, 233), s.getSize());
    }

    @Test
    public void getSizeWithRotation() {
        SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);

        s.loadPicture(imageUrl, 270.0);
        assertEquals("Size", new Dimension(233, 350), s.getSize());
    }

    @Test
    public void getSizeNullPicture() {
        SourcePicture s = new SourcePicture();
        assertEquals("Size", new Dimension(0, 0), s.getSize());
    }

    @Test
    public void testJpgImage() {
        SourcePicture s = new SourcePicture();
        final String JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
        URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(JPG_IMAGE_FILE);

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a jpg image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 233, s.getHeight());
        assertEquals("Width", 350, s.getWidth());
    }

    @Test
    public void testGetJpgImageIOReader() {
        final String JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(JPG_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for jpg image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasJpgImageReader() {
        final String JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(JPG_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a jpg image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBmpImage() {
        SourcePicture s = new SourcePicture();
        final String BMP_IMAGE_FILE = "bmp.bmp";
        URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(BMP_IMAGE_FILE);

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a bmp image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 100, s.getHeight());
        assertEquals("Width", 150, s.getWidth());
    }

    @Test
    public void testGetBmpImageIOReader() {
        final String BMP_IMAGE_FILE = "bmp.bmp";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(BMP_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.bmp.BMPImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.bmp.BMPImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for bmp image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasBmpImageReader() {
        final String BMP_IMAGE_FILE = "bmp.bmp";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(BMP_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a bmp image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void testTiffImage() {
        SourcePicture s = new SourcePicture();
        final String TIFF_IMAGE_FILE = "tiff_image.tiff";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(TIFF_IMAGE_FILE));

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a tiff image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 100, s.getHeight());
        assertEquals("Width", 200, s.getWidth());
    }

    @Test
    public void testGetTiffImageIOReader() {
        final String TIFF_IMAGE_FILE = "tiff_image.tiff";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(TIFF_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for tiff image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasTiffImageReader() {
        final String TIFF_IMAGE_FILE = "tiff_image.tiff";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(TIFF_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a tiff image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void testHdrImage() {
        SourcePicture s = new SourcePicture();
        final String HDR_IMAGE_FILE = "memorial_o876.hdr";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(HDR_IMAGE_FILE));

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a hdr image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 768, s.getHeight());
        assertEquals("Width", 512, s.getWidth());
    }

    @Test
    public void testGetHdrImageIOReader() {
        final String HDR_IMAGE_FILE = "memorial_o876.hdr";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(HDR_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.hdr.HDRImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.hdr.HDRImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for hdr image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasHdrImageReader() {
        final String HDR_IMAGE_FILE = "memorial_o876.hdr";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(HDR_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a hdf image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }
    @Test
    @Ignore("ImageIO PDF doesn't seem to work")
    public void testPdfImage() {
        SourcePicture s = new SourcePicture();
        final String PDF_IMAGE_FILE = "pdf-document.pdf";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PDF_IMAGE_FILE));

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a pdf image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 768, s.getHeight());
        assertEquals("Width", 512, s.getWidth());
    }

    @Test
    @Ignore("ImageIO PDF doesn't seem to work")
    public void testGetPdfImageIOReader() {
        final String PDF_IMAGE_FILE = "pdf-document.pdf";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PDF_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.pdf.PdfImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.pdf.PDFImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for pdf document for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    @Ignore("ImageIO PDF doesn't seem to work")
    public void testHasPdfImageReader() {
        final String PDF_IMAGE_FILE = "pdf-document.pdf";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PDF_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a pdf document but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void testSvgImage() {
        SourcePicture s = new SourcePicture();
        final String SVG_IMAGE_FILE = "Ghostscript_Tiger.svg";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(SVG_IMAGE_FILE));

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a svg image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 400, s.getHeight());
        assertEquals("Width", 400, s.getWidth());
    }

    @Test
    public void testGetSvgImageIOReader() {
        final String SVG_IMAGE_FILE = "Ghostscript_Tiger.svg";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(SVG_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.svg.SVGImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.svg.SVGImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for svg image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasSvgImageReader() {
        final String SVG_IMAGE_FILE = "Ghostscript_Tiger.svg";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(SVG_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a svg image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void testPnmImage() {
        SourcePicture s = new SourcePicture();
        final String PNM_IMAGE_FILE = "pnm.pnm";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PNM_IMAGE_FILE));

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a pnm image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 100, s.getHeight());
        assertEquals("Width", 150, s.getWidth());
    }

    @Test
    public void testGetPnmImageIOReader() {
        final String PNM_IMAGE_FILE = "pnm.pnm";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PNM_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.pnm.PNMImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.pnm.PNMImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for pnm image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasPnmImageReader() {
        final String PNM_IMAGE_FILE = "pnm.pnm";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PNM_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a pnm image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }



    @Test
    public void testSgiImage() {
        SourcePicture s = new SourcePicture();
        final String SGI_IMAGE_FILE = "sgi.sgi";
        URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(SGI_IMAGE_FILE);

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a sgi image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 100, s.getHeight());
        assertEquals("Width", 150, s.getWidth());
    }

    @Test
    public void testGetSgiImageIOReader() {
        final String SGI_IMAGE_FILE = "sgi.sgi";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(SGI_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.sgi.SGIImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.sgi.SGIImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for sgi image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasSgiImageReader() {
        final String SGI_IMAGE_FILE = "sgi.sgi";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(SGI_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a sgi image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTgaImage() {
        SourcePicture s = new SourcePicture();
        final String TGA_IMAGE_FILE = "tga.tga";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(TGA_IMAGE_FILE));

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a tga image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 100, s.getHeight());
        assertEquals("Width", 150, s.getWidth());
    }

    @Test
    public void testGetTgaImageIOReader() {
        final String TGA_IMAGE_FILE = "tga.tga";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(TGA_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.tga.TGAImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.tga.TGAImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for tga image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasTgaImageReader() {
        final String TGA_IMAGE_FILE = "tga.tga";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(TGA_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a tga image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void testPsdImage() {
        SourcePicture s = new SourcePicture();
        final String PSD_IMAGE_FILE = "psd.psd";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PSD_IMAGE_FILE));

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a psd image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 100, s.getHeight());
        assertEquals("Width", 150, s.getWidth());
    }

    @Test
    public void testGetPsdImageIOReader() {
        final String PSD_IMAGE_FILE = "psd.psd";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PSD_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.psd.PSDImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.psd.PSDImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for psd image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasPsdImageReader() {
        final String PSD_IMAGE_FILE = "psd.psd";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PSD_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a psd image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIcoImage() {
        SourcePicture s = new SourcePicture();
        final String ICO_IMAGE_FILE = "favicon.ico";
        URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(ICO_IMAGE_FILE);

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load an ico image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 64, s.getHeight());
        assertEquals("Width", 64, s.getWidth());
    }

    @Test
    public void testGetIcoImageIOReader() {
        final String ICO_IMAGE_FILE = "favicon.ico";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(ICO_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.bmp.ICOImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.bmp.ICOImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for ico image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasIcoImageReader() {
        final String ICO_IMAGE_FILE = "favicon.ico";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(ICO_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load an ico image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPngImage() {
        SourcePicture s = new SourcePicture();
        final String PNG_IMAGE_FILE = "png.png";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PNG_IMAGE_FILE));

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a png image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 100, s.getHeight());
        assertEquals("Width", 150, s.getWidth());
    }

    @Test
    public void testGetPngImageIOReader() {
        final String PNG_IMAGE_FILE = "png.png";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PNG_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            // Note: No 12 monkeys here!
            assertTrue("Found reader should start with \"com.sun.imageio.png.PNGImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.sun.imageio.plugins.png.PNGImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for png image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasPngImageReader() {
        final String PNG_IMAGE_FILE = "png.png";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PNG_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a png image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGifImage() {
        SourcePicture s = new SourcePicture();
        final String GIF_IMAGE_FILE = "gif.gif";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(GIF_IMAGE_FILE));

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a gif image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 100, s.getHeight());
        assertEquals("Width", 150, s.getWidth());
    }

    @Test
    public void testGetGifImageIOReader() {
        final String GIF_IMAGE_FILE = "gif.gif";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(GIF_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            // Note: No 12 monkeys here!
            assertTrue("Found reader should start with \"com.sun.imageio.gif.GIFImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.sun.imageio.plugins.gif.GIFImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for gif image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasGifImageReader() {
        final String GIF_IMAGE_FILE = "gif.gif";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(GIF_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a gif image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIffImage() {
        SourcePicture s = new SourcePicture();
        final String IFF_IMAGE_FILE = "AmigaAmiga.iff";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(IFF_IMAGE_FILE));

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a iff image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 150, s.getHeight());
        assertEquals("Width", 200, s.getWidth());
    }

    @Test
    public void testGetIffImageIOReader() {
        final String IFF_IMAGE_FILE = "AmigaAmiga.iff";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(IFF_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.iff.IFFImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.iff.IFFImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for iff image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasIffImageReader() {
        final String IFF_IMAGE_FILE = "AmigaAmiga.iff";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(IFF_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a iff image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPcxImage() {
        SourcePicture s = new SourcePicture();
        final String PCX_IMAGE_FILE = "pcx.pcx";
        URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(PCX_IMAGE_FILE);

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a pcx image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 100, s.getHeight());
        assertEquals("Width", 150, s.getWidth());
    }

    @Test
    public void testGetPcxImageIOReader() {
        final String PCX_IMAGE_FILE = "pcx.pcx";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PCX_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.pcx.PCXImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.pcx.PCXImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for pcx image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasPcxImageReader() {
        final String PCX_IMAGE_FILE = "pcx.pcx";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PCX_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a pcx image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPctImage() {
        SourcePicture s = new SourcePicture();
        final String PICT_IMAGE_FILE = "food.pct";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PICT_IMAGE_FILE));

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a pct image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 194, s.getHeight());
        assertEquals("Width", 146, s.getWidth());
    }

    @Test
    public void testGetPctImageIOReader() {
        final String PICT_IMAGE_FILE = "food.pct";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PICT_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.pict.PICTImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.pict.PICTImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for pct image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasPctImageReader() {
        final String PICT_IMAGE_FILE = "food.pct";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PICT_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a pct image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testClipPathImage() {
        SourcePicture s = new SourcePicture();
        final String CLIP_PATH_IMAGE_FILE = "grape_with_path.jpg";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(CLIP_PATH_IMAGE_FILE));

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a clipPath image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 1800, s.getHeight());
        assertEquals("Width", 857, s.getWidth());
    }

    @Test
    public void testGetClipPathImageIOReader() {
        final String CLIP_PATH_IMAGE_FILE = "grape_with_path.jpg";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(CLIP_PATH_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for clipPath image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasClipPathImageReader() {
        final String CLIP_PATH_IMAGE_FILE = "grape_with_path.jpg";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(CLIP_PATH_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a clipPath image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIcnsImage() {
        SourcePicture s = new SourcePicture();
        final String ICNS_IMAGE_FILE = "7zIcon.icns";
        URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(ICNS_IMAGE_FILE);

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a icns image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 16, s.getHeight());
        assertEquals("Width", 16, s.getWidth());
    }

    @Test
    public void testGetIcnsImageIOReader() {
        final String ICNS_IMAGE_FILE = "7zIcon.icns";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(ICNS_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.icns.ICNSImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.icns.ICNSImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for icns image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasIcnsImageReader() {
        final String ICNS_IMAGE_FILE = "7zIcon.icns";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(ICNS_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a icns image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testThumbsDbImage() {
        SourcePicture s = new SourcePicture();
        final String THUMBS_DB_IMAGE_FILE = "Thumbs.db";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(THUMBS_DB_IMAGE_FILE));

        s.loadPicture(imageUrl, 0.0);
        assertNotNull("We were trying to load a ThumbsDb image but it was null!", s.getSourceBufferedImage());
        assertEquals("Height", 96, s.getHeight());
        assertEquals("Width", 96, s.getWidth());
    }

    @Test
    public void testGetThumbsDbImageIOReader() {
        final String THUMBS_DB_IMAGE_FILE = "Thumbs.db";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(THUMBS_DB_IMAGE_FILE));
        try (InputStream input = imageUrl.openStream();
             ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue("Found reader should start with \"com.twelvemonkeys.imageio.plugins.thumbsdb.ThumbsDBImageReader\" but reads: " + reader.toString(),
                    reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.thumbsdb.ThumbsDBImageReader"));
        } catch (NoSuchElementException e) {
            fail("Failed to find a reader for ThumbsDb image for URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        } catch (IOException e) {
            fail("Failed to open inputstream from URL: " + imageUrl.toString() + "\nException: " + e.getMessage());
        }
    }

    @Test
    public void testHasThumbsDbImageReader() {
        final String THUMBS_DB_IMAGE_FILE = "Thumbs.db";
        URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(THUMBS_DB_IMAGE_FILE));
        try {
            File image = new File( imageUrl.toURI());
            assertTrue("We were trying to load a ThumbsDb image but the JVM doesn't think it has a reader for it", SourcePicture.jvmHasReader(image));
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }


}