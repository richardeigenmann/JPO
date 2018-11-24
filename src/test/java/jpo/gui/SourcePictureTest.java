package jpo.gui;

import jpo.dataModel.ExifInfoTest;
import jpo.dataModel.Settings;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class SourcePictureTest {

    @BeforeClass
    public static void beforeClass() {
        Settings.loadSettings(); // We need to start the cache
    }

    @Test
    public void getHeight() {
        SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        URL imageUrl = ExifInfoTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);

        s.loadPicture(imageUrl, 0.0);
        assertEquals("Height", 233, s.getHeight());
    }

    @Test
    public void getHeightWithRotation() {
        SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        URL imageUrl = ExifInfoTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);

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
        URL imageUrl = ExifInfoTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);

        s.loadPicture(imageUrl, 0.0);
        assertEquals("Height", 350, s.getWidth());
    }

    @Test
    public void getWidthWithRotation() {
        SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        URL imageUrl = ExifInfoTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);

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
        URL imageUrl = ExifInfoTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);

        s.loadPicture(imageUrl, 0.0);
        assertEquals("Size", new Dimension(350,233), s.getSize());
    }

    @Test
    public void getSizeWithRotation() {
        SourcePicture s = new SourcePicture();
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        URL imageUrl = ExifInfoTest.class.getClassLoader().getResource(NIKON_D100_IMAGE);

        s.loadPicture(imageUrl, 270.0);
        assertEquals("Size", new Dimension(233,350), s.getSize());
    }

    @Test
    public void getSizeNullPicture() {
        SourcePicture s = new SourcePicture();
        assertEquals("Size", new Dimension(0,0), s.getSize());
    }

}