package org.jpo.gui;

import org.jpo.datamodel.Settings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;


public class SourcePictureTest {

    @BeforeAll
    public static void beforeAll() {
        Settings.loadSettings(); // We need to start the cache
    }

    @Test
    public void getHeight() {
        final SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        final URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);
        try {
            final File imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
            s.loadPicture(imageFile, 0.0);
            assertEquals(233, s.getHeight());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getHeightWithRotation() {
        final SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        final URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);
        try {
            final File imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
            s.loadPicture(imageFile, 90.0);
            assertEquals(350, s.getHeight());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void getHeightNullPicture() {
        final SourcePicture s = new SourcePicture();
        assertEquals(0, s.getHeight());
    }

    @Test
    public void loadInexistentFile() {
        final SourcePicture s = new SourcePicture();
        s.loadPicture(new File("no such file.jpg"), 0.0);
        assertNull(s.getSourceBufferedImage());
    }


    @Test
    public void getWidth() {
        final SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        final URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

        s.loadPicture(imageFile, 0.0);
        assertEquals(350, s.getWidth());
    }

    @Test
    public void getWidthWithRotation() {
        final SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        final URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

        s.loadPicture(imageFile, 90.0);
        assertEquals(233, s.getWidth());
    }

    @Test
    public void getWidthNullPicture() {
        final SourcePicture s = new SourcePicture();
        assertEquals(0, s.getWidth());
    }

    @Test
    public void getSize() {
        final SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        final URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

        s.loadPicture(imageFile, 0.0);
        assertEquals(new Dimension(350, 233), s.getSize());
    }

    @Test
    public void getSizeWithRotation() {
        final SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        final URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

        s.loadPicture(imageFile, 270.0);
        assertEquals(new Dimension(233, 350), s.getSize());
    }

    @Test
    public void getSizeNullPicture() {
        final SourcePicture s = new SourcePicture();
        assertEquals(new Dimension(0, 0), s.getSize());
    }

    @Test
    public void testJpgImage() {
        final SourcePicture s = new SourcePicture();
        final String JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
        final URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(JPG_IMAGE_FILE);
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

        s.loadPicture(imageFile, 0.0);
        assertNotNull(s.getSourceBufferedImage());
        assertEquals(233, s.getHeight());
        assertEquals(350, s.getWidth());
    }

    @Test
    public void testGetJpgImageIOReader() {
        final String JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(JPG_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasJpgImageReader() {
        final String JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(JPG_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBmpImage() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SourcePicture s = new SourcePicture();
        final String BMP_IMAGE_FILE = "bmp.bmp";
        final URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(BMP_IMAGE_FILE);
        try {
            final File imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(100, s.getHeight());
            assertEquals(150, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetBmpImageIOReader() {
        final String BMP_IMAGE_FILE = "bmp.bmp";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(BMP_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.bmp.BMPImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasBmpImageReader() {
        final String BMP_IMAGE_FILE = "bmp.bmp";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(BMP_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void testTiffImage() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SourcePicture s = new SourcePicture();
        final String TIFF_IMAGE_FILE = "tiff_image.tiff";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(TIFF_IMAGE_FILE));
        try {
            final File imageFile = new File(imageUrl.toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(100, s.getHeight());
            assertEquals(200, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetTiffImageIOReader() {
        final String TIFF_IMAGE_FILE = "tiff_image.tiff";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(TIFF_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasTiffImageReader() {
        final String TIFF_IMAGE_FILE = "tiff_image.tiff";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(TIFF_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void testHdrImage() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SourcePicture s = new SourcePicture();
        final String HDR_IMAGE_FILE = "memorial_o876.hdr";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(HDR_IMAGE_FILE));
        try {
            final File imageFile = new File(imageUrl.toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(768, s.getHeight());
            assertEquals(512, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetHdrImageIOReader() {
        final String HDR_IMAGE_FILE = "memorial_o876.hdr";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(HDR_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.hdr.HDRImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasHdrImageReader() {
        final String HDR_IMAGE_FILE = "memorial_o876.hdr";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(HDR_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Disabled("ImageIO PDF doesn't seem to work")
    public void testPdfImage() {
        final SourcePicture s = new SourcePicture();
        final String PDF_IMAGE_FILE = "pdf-document.pdf";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PDF_IMAGE_FILE));
        try {
            final File imageFile = new File(imageUrl.toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(768, s.getHeight());
            assertEquals(512, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    @Disabled("ImageIO PDF doesn't seem to work")
    public void testGetPdfImageIOReader() {
        final String PDF_IMAGE_FILE = "pdf-document.pdf";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PDF_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.pdf.PDFImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Disabled("ImageIO PDF doesn't seem to work")
    public void testHasPdfImageReader() {
        final String PDF_IMAGE_FILE = "pdf-document.pdf";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PDF_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void testSvgImage() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SourcePicture s = new SourcePicture();
        final String SVG_IMAGE_FILE = "Ghostscript_Tiger.svg";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(SVG_IMAGE_FILE));
        try {
            s.loadPicture(new File(imageUrl.toURI()), 0.0);
            assertNotNull(s.getSourceBufferedImage());
            // Size is not stable. Sometimes we get 400, Sometimes 900
            Logger.getLogger(SourcePictureTest.class.getName()).log(Level.INFO, "Height of {0} is {1}", new Object[]{SVG_IMAGE_FILE, s.getHeight()});
            assertTrue(s.getHeight() == 400 || s.getHeight() == 900);
            Logger.getLogger(SourcePictureTest.class.getName()).log(Level.INFO, "Width of {0} is {1}", new Object[]{SVG_IMAGE_FILE, s.getWidth()});
            assertTrue(s.getWidth() == 900 || s.getWidth() == 800);
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetSvgImageIOReader() {
        final String SVG_IMAGE_FILE = "Ghostscript_Tiger.svg";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(SVG_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.svg.SVGImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasSvgImageReader() {
        final String SVG_IMAGE_FILE = "Ghostscript_Tiger.svg";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(SVG_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void testPnmImage() {
        final SourcePicture s = new SourcePicture();
        final String PNM_IMAGE_FILE = "pnm.pnm";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PNM_IMAGE_FILE));
        try {
            final File imageFile = new File(imageUrl.toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(100, s.getHeight());
            assertEquals(150, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetPnmImageIOReader() {
        final String PNM_IMAGE_FILE = "pnm.pnm";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PNM_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.pnm.PNMImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasPnmImageReader() {
        final String PNM_IMAGE_FILE = "pnm.pnm";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PNM_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void testSgiImage() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SourcePicture s = new SourcePicture();
        final String SGI_IMAGE_FILE = "sgi.sgi";
        final URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(SGI_IMAGE_FILE);
        try {
            final File imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(100, s.getHeight());
            assertEquals(150, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetSgiImageIOReader() {
        final String SGI_IMAGE_FILE = "sgi.sgi";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(SGI_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.sgi.SGIImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasSgiImageReader() {
        final String SGI_IMAGE_FILE = "sgi.sgi";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(SGI_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTgaImage() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SourcePicture s = new SourcePicture();
        final String TGA_IMAGE_FILE = "tga.tga";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(TGA_IMAGE_FILE));
        try {
            final File imageFile = new File(imageUrl.toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(100, s.getHeight());
            assertEquals(150, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetTgaImageIOReader() {
        final String TGA_IMAGE_FILE = "tga.tga";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(TGA_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.tga.TGAImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasTgaImageReader() {
        final String TGA_IMAGE_FILE = "tga.tga";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(TGA_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void testPsdImage() {
        final SourcePicture s = new SourcePicture();
        final String PSD_IMAGE_FILE = "psd.psd";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PSD_IMAGE_FILE));
        try {
            final File imageFile = new File(imageUrl.toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(100, s.getHeight());
            assertEquals(150, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetPsdImageIOReader() {
        final String PSD_IMAGE_FILE = "psd.psd";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PSD_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.psd.PSDImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasPsdImageReader() {
        final String PSD_IMAGE_FILE = "psd.psd";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PSD_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIcoImage() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SourcePicture s = new SourcePicture();
        final String ICO_IMAGE_FILE = "favicon.ico";
        final URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(ICO_IMAGE_FILE);
        try {
            final File imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(64, s.getHeight());
            assertEquals(64, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetIcoImageIOReader() {
        final String ICO_IMAGE_FILE = "favicon.ico";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(ICO_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.bmp.ICOImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasIcoImageReader() {
        final String ICO_IMAGE_FILE = "favicon.ico";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(ICO_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPngImage() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SourcePicture s = new SourcePicture();
        final String PNG_IMAGE_FILE = "png.png";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PNG_IMAGE_FILE));
        try {
            final File imageFile = new File(imageUrl.toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(100, s.getHeight());
            assertEquals(150, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetPngImageIOReader() {
        final String PNG_IMAGE_FILE = "png.png";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PNG_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.sun.imageio.plugins.png.PNGImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasPngImageReader() {
        final String PNG_IMAGE_FILE = "png.png";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PNG_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGifImage() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SourcePicture s = new SourcePicture();
        final String GIF_IMAGE_FILE = "gif.gif";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(GIF_IMAGE_FILE));
        try {
            final File imageFile = new File(imageUrl.toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(100, s.getHeight());
            assertEquals(150, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetGifImageIOReader() {
        final String GIF_IMAGE_FILE = "gif.gif";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(GIF_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.sun.imageio.plugins.gif.GIFImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasGifImageReader() {
        final String GIF_IMAGE_FILE = "gif.gif";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(GIF_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIffImage() {
        final SourcePicture s = new SourcePicture();
        final String IFF_IMAGE_FILE = "AmigaAmiga.iff";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(IFF_IMAGE_FILE));
        try {
            final File imageFile = new File(imageUrl.toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(150, s.getHeight());
            assertEquals(200, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetIffImageIOReader() {
        final String IFF_IMAGE_FILE = "AmigaAmiga.iff";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(IFF_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.iff.IFFImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasIffImageReader() {
        final String IFF_IMAGE_FILE = "AmigaAmiga.iff";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(IFF_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPcxImage() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SourcePicture s = new SourcePicture();
        final String PCX_IMAGE_FILE = "pcx.pcx";
        final URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(PCX_IMAGE_FILE);
        try {
            final File imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(100, s.getHeight());
            assertEquals(150, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetPcxImageIOReader() {
        final String PCX_IMAGE_FILE = "pcx.pcx";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PCX_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.pcx.PCXImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasPcxImageReader() {
        final String PCX_IMAGE_FILE = "pcx.pcx";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PCX_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPctImage() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SourcePicture s = new SourcePicture();
        final String PICT_IMAGE_FILE = "food.pct";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PICT_IMAGE_FILE));
        try {
            final File imageFile = new File(imageUrl.toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(194, s.getHeight());
            assertEquals(146, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetPctImageIOReader() {
        final String PICT_IMAGE_FILE = "food.pct";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PICT_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.pict.PICTImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasPctImageReader() {
        final String PICT_IMAGE_FILE = "food.pct";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(PICT_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testClipPathImage() {
        final SourcePicture s = new SourcePicture();
        final String CLIP_PATH_IMAGE_FILE = "grape_with_path.jpg";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(CLIP_PATH_IMAGE_FILE));
        try {
            final File imageFile = new File(imageUrl.toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(1800, s.getHeight());
            assertEquals(857, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetClipPathImageIOReader() {
        final String CLIP_PATH_IMAGE_FILE = "grape_with_path.jpg";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(CLIP_PATH_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasClipPathImageReader() {
        final String CLIP_PATH_IMAGE_FILE = "grape_with_path.jpg";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(CLIP_PATH_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIcnsImage() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SourcePicture s = new SourcePicture();
        final String ICNS_IMAGE_FILE = "7zIcon.icns";
        final URL imageUrl = SourcePictureTest.class.getClassLoader().getResource(ICNS_IMAGE_FILE);
        try {
            final File imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(16, s.getHeight());
            assertEquals(16, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetIcnsImageIOReader() {
        final String ICNS_IMAGE_FILE = "7zIcon.icns";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(ICNS_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.icns.ICNSImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasIcnsImageReader() {
        final String ICNS_IMAGE_FILE = "7zIcon.icns";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(ICNS_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testThumbsDbImage() {
        final SourcePicture s = new SourcePicture();
        final String THUMBS_DB_IMAGE_FILE = "Thumbs.db";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(THUMBS_DB_IMAGE_FILE));
        try {
            final File imageFile = new File(imageUrl.toURI());
            s.loadPicture(imageFile, 0.0);
            assertNotNull(s.getSourceBufferedImage());
            assertEquals(96, s.getHeight());
            assertEquals(96, s.getWidth());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetThumbsDbImageIOReader() {
        final String THUMBS_DB_IMAGE_FILE = "Thumbs.db";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(THUMBS_DB_IMAGE_FILE));
        try (final InputStream input = imageUrl.openStream();
             final ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            final ImageReader reader = SourcePicture.getImageIOReader(iis);
            assertTrue(reader.toString().startsWith("com.twelvemonkeys.imageio.plugins.thumbsdb.ThumbsDBImageReader"));
        } catch (final NoSuchElementException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHasThumbsDbImageReader() {
        final String THUMBS_DB_IMAGE_FILE = "Thumbs.db";
        final URL imageUrl = Objects.requireNonNull(SourcePictureTest.class.getClassLoader().getResource(THUMBS_DB_IMAGE_FILE));
        try {
            final File image = new File(imageUrl.toURI());
            assertTrue(SourcePicture.jvmHasReader(image));
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

}