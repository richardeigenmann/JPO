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


 class SourcePictureTest {

     @BeforeAll
     public static void beforeAll() {
         Settings.loadSettings(); // We need to start the cache
     }

     @Test
     void getHeight() {
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
     void getHeightWithRotation() {
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
     void getHeightNullPicture() {
         final SourcePicture s = new SourcePicture();
         assertEquals(0, s.getHeight());
     }

     @Test
     void loadInexistentFile() {
         final SourcePicture s = new SourcePicture();
         s.loadPicture(new File("no such file.jpg"), 0.0);
         assertNull(s.getSourceBufferedImage());
     }


     @Test
     void getWidth() {
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
     void getWidthWithRotation() {
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
     void getWidthNullPicture() {
         final SourcePicture s = new SourcePicture();
         assertEquals(0, s.getWidth());
     }

     @Test
     void getSize() {
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
     void getSizeWithRotation() {
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
     void getSizeNullPicture() {
         final SourcePicture s = new SourcePicture();
         assertEquals(new Dimension(0, 0), s.getSize());
     }

     @Test
     void testJpgImage() {
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
     void testGetJpgImageIOReader() {
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
     void testHasJpgImageReader() {
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
     void testBmpImage() {
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
     void testGetBmpImageIOReader() {
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
     void testHasBmpImageReader() {
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
     void testTiffImage() {
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
     void testGetTiffImageIOReader() {
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
     void testHasTiffImageReader() {
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
     void testHdrImage() {
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
     void testGetHdrImageIOReader() {
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
     void testHasHdrImageReader() {
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
     void testPdfImage() {
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
     void testGetPdfImageIOReader() {
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
     void testHasPdfImageReader() {
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
     void testSvgImage() {
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
     void testGetSvgImageIOReader() {
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
     void testHasSvgImageReader() {
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
     void testPnmImage() {
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
     void testGetPnmImageIOReader() {
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
     void testHasPnmImageReader() {
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
     void testSgiImage() {
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
     void testGetSgiImageIOReader() {
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
     void testHasSgiImageReader() {
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
     void testTgaImage() {
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
     void testGetTgaImageIOReader() {
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
     void testHasTgaImageReader() {
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
     void testPsdImage() {
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
     void testGetPsdImageIOReader() {
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
     void testHasPsdImageReader() {
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
     void testIcoImage() {
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
     void testGetIcoImageIOReader() {
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
     void testHasIcoImageReader() {
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
     void testPngImage() {
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
     void testGetPngImageIOReader() {
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
     void testHasPngImageReader() {
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
     void testGifImage() {
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
     void testGetGifImageIOReader() {
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
     void testHasGifImageReader() {
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
     void testIffImage() {
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
     void testGetIffImageIOReader() {
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
     void testHasIffImageReader() {
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
     void testPcxImage() {
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
     void testGetPcxImageIOReader() {
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
     void testHasPcxImageReader() {
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
     void testPctImage() {
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
     void testGetPctImageIOReader() {
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
     void testHasPctImageReader() {
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
     void testClipPathImage() {
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
     void testGetClipPathImageIOReader() {
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
     void testHasClipPathImageReader() {
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
     void testIcnsImage() {
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
     void testGetIcnsImageIOReader() {
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
     void testHasIcnsImageReader() {
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
     void testThumbsDbImage() {
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
     void testGetThumbsDbImageIOReader() {
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
     void testHasThumbsDbImageReader() {
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