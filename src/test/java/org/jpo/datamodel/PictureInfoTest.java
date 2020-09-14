package org.jpo.datamodel;

import com.google.common.hash.HashCode;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2017 - 2020 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 without even the implied warranty of MERCHANTABILITY or FITNESS 
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */


/**
 * Tests for the PictureInfo class
 *
 * @author Richard Eigenmann
 */
public class PictureInfoTest {

    /**
     * Test of toString method, of class PictureInfo.
     */
    @Test
    public void testToString() {
        PictureInfo pi = new PictureInfo(new File("c:\\picture.jpg"), "My Sample Picture");
        assertEquals("My Sample Picture", pi.toString());
    }

    /**
     * Test of getDescription method, of class PictureInfo.
     */
    @Test
    public void testGetDescription() {
        final PictureInfo pi = new PictureInfo(new File("c:\\picture.jpg"), "My Sample Picture");
        assertEquals("My Sample Picture", pi.getDescription());
    }

    /**
     * Test of setDescription method, of class PictureInfo.
     */
    @Test
    public void testSetDescription() {
        final PictureInfo pi = new PictureInfo();
        final int changeEvents[] = {0};
        final PictureInfoChangeListener picl = (PictureInfoChangeEvent arg0) -> changeEvents[0] += 1;
        pi.addPictureInfoChangeListener(picl);
        pi.setDescription("A description");
        assertEquals("A description", pi.getDescription());
        assertEquals(1, changeEvents[0]);
        pi.setDescription("A different description");
        assertEquals("A different description", pi.getDescription());
        assertEquals(2, changeEvents[0]);
    }

    /**
     * Test Description change event
     */
    @Test
    public void testSetDescriptionSame() {
        final PictureInfo pi = new PictureInfo();
        final int countEvents[] = {0};
        final PictureInfoChangeListener picl = (PictureInfoChangeEvent arg0) -> countEvents[0] += 1;
        pi.addPictureInfoChangeListener(picl);
        pi.setDescription("A picture description");
        // Expecting what went in to come out
        assertEquals("A picture description", pi.getDescription());
        assertEquals(1, countEvents[0]);
        pi.setDescription("A picture description");
        // Expecting what went in to come out
        assertEquals("A picture description", pi.getDescription());
        // Expecting no new change event because it was the same that went in
        assertEquals(1, countEvents[0]);
    }

    /**
     * Test of appendToDescription method, of class PictureInfo.
     */
    @Test
    public void testAppendToDescription() {
        final PictureInfo pi = new PictureInfo();
        pi.setDescription("A picture description");
        pi.appendToDescription(" concatenated from two texts");
        assertEquals("A picture description concatenated from two texts", pi.getDescription());
    }

    /**
     * Test of descriptionContains method, of class PictureInfo.
     */
    @Test
    public void testDescriptionContains() {
        final PictureInfo pi = new PictureInfo();
        pi.setDescription("A picture of a big town at sunset");
        assertTrue( pi.descriptionContains("town"));
    }

    /**
     * Test of getImageLocation method, of class PictureInfo.
     */
    @Test
    public void testGetImageLocation() {
        final PictureInfo pi = new PictureInfo(new File("/dir/picture.jpg"), "My Sample Picture");
        final String highresLocation = pi.getImageLocation();
        assertEquals("file:/dir/picture.jpg", highresLocation);
    }

    @Test
    public void testGetImageLocationNull() {
        final PictureInfo pi = new PictureInfo();
        final String highresLocation = pi.getImageLocation();
        assertEquals("", highresLocation);
    }


    /**
     * Test of getImageFile method, of class PictureInfo.
     */
    @Test
    public void testGetImageFile() {
        final String FILENAME = "/dir/picture.jpg";
        final PictureInfo pi = new PictureInfo(new File(FILENAME), "My Sample Picture");
        assertEquals( new File(FILENAME), pi.getImageFile());
    }

    /**
     * Test of getImageURIOrNull method, of class PictureInfo.
     */
    @Test
    public void testGetImageURIOrNull() {
        final PictureInfo pi = new PictureInfo();
        final String goodLocation = "/image.jpg";
        pi.setImageLocation(new File(goodLocation));
        final URI pulledUri = pi.getImageURIOrNull();
        assertEquals("file:" + goodLocation, pulledUri.toString());

        final PictureInfo pi2 = new PictureInfo();
        final URI nullUri = pi2.getImageURIOrNull();
        assertNull(nullUri);

        final PictureInfo pi3 = new PictureInfo();
        final String badLocation = "?äöü&~`";
        pi3.setImageLocation(new File(badLocation));
        final URI nullUri2 = pi3.getImageURIOrNull();
        assertTrue(nullUri2.toString().contains("äöü"));

    }

    @Test
    public void testSetImageLocationString() {
        final PictureInfo pi = new PictureInfo();
        pi.setImageLocation(new File("/dir/picture.jpg"));
        final File f = pi.getImageFile();
        assertEquals(f.toString(), "/dir/picture.jpg");
    }

    @Test
    public void testSetImageLocationWithSpace() {
        final PictureInfo pi = new PictureInfo();
        pi.setImageLocation(new File("/dir/picture file.jpg"));
        final File f = pi.getImageFile();
        assertEquals(f.toString(), "/dir/picture file.jpg");
    }

    /**
     * Test of setImageLocation method, of class PictureInfo.
     */
    @Test
    public void testSetImageLocationUrl() {
        try {
            final PictureInfo pi = new PictureInfo();
            pi.setImageLocation(new File(new URL("file:///dir/picture.jpg").toURI()));
            final File f = pi.getImageFile();
            assertEquals(f.toString(), "/dir/picture.jpg");
        } catch (MalformedURLException | URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of appendToImageLocation method, of class PictureInfo.
     */
    @Test
    public void testAppendToImageLocation() {
        final PictureInfo pi = new PictureInfo();
        pi.appendToImageLocation("file:///dir/picture");
        pi.appendToImageLocation(".jpg");
        final File f = pi.getImageFile();
        assertEquals(f.toString(), "/dir/picture.jpg");
    }

    /**
     * Test of getImageFilename method, of class PictureInfo.
     */
    @Test
    public void testGetHighresFilename() {
        final PictureInfo pi = new PictureInfo();
        pi.setImageLocation(new File("/dir/picture.jpg"));
        final String filename = pi.getImageFile().getName();
        assertEquals(filename, "picture.jpg");
    }

    /**
     * A dumb PictureInfoChangeListener that only counts the events received
     */
    private final PictureInfoChangeListener pictureInfoChangeListener = new PictureInfoChangeListener() {

        @Override
        public void pictureInfoChangeEvent(PictureInfoChangeEvent pictureInfoChangeEvent) {
            eventsReceived++;
        }
    };

    private int eventsReceived;

    /**
     * Test the change listener
     */
    @Test
    public void testPictureInfoChangeListener() {
        eventsReceived = 0;
        final PictureInfo pi = new PictureInfo();
        assertEquals( 0, eventsReceived);
        pi.setDescription("Step 1");
        // There is no listener attached so there is no event
        assertEquals( 0, eventsReceived);
        pi.addPictureInfoChangeListener(pictureInfoChangeListener);
        pi.setDescription("Step 2");
        // The listener should have fired and we should have 1 event
        assertEquals( 1, eventsReceived);
        pi.removePictureInfoChangeListener(pictureInfoChangeListener);
        pi.setDescription("Step 3");
        // The detached listener should not have fired
        assertEquals( 1, eventsReceived);
    }

    /**
     * Test dumpToXml
     */
    @Test
    public void testDumpToXml() {
        final URL u = Objects.requireNonNull(PictureInfoTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg"));
        final PictureInfo pi = new PictureInfo(new File(u.getFile()), "First <Picture> & difficult xml chars ' \"");
        pi.setComment("Comment <<&>'\">");
        pi.setFilmReference("Reference <<&>'\">");
        pi.setRotation(45.1);
        pi.setPhotographer("Richard Eigenmann <<&>'\">");
        pi.setLatLng("22.67x33.89");
        pi.setCopyrightHolder("Sandra Keller <<&>'\">");
        pi.addCategoryAssignment("1");
        pi.setChecksum(1234);

        final StringWriter sw = new StringWriter();
        try (BufferedWriter bw = new BufferedWriter(sw)) {
            pi.dumpToXml(bw);
        } catch (IOException ex) {
            Logger.getLogger(PictureInfoTest.class.getName()).log(Level.SEVERE, "The dumpToXml should really not throw an IOException", ex);
            fail("Unexpected IOException");
        }

        final URL jpgResource = PictureInfoTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg");

        final String expected = "<picture>\n"
                + "\t<description><![CDATA[First <Picture> & difficult xml chars ' \"]]></description>\n"
                + "\t<file_URL>"
                + Objects.requireNonNull(jpgResource).toString()
                + "</file_URL>\n"
                + "\t<checksum>1234</checksum>\n"
                + "\t<COMMENT>Comment &lt;&lt;&amp;&gt;&apos;&quot;&gt;</COMMENT>\n"
                + "\t<PHOTOGRAPHER>Richard Eigenmann &lt;&lt;&amp;&gt;&apos;&quot;&gt;</PHOTOGRAPHER>\n"
                + "\t<film_reference>Reference &lt;&lt;&amp;&gt;&apos;&quot;&gt;</film_reference>\n"
                + "\t<COPYRIGHT_HOLDER>Sandra Keller &lt;&lt;&amp;&gt;&apos;&quot;&gt;</COPYRIGHT_HOLDER>\n"
                + "\t<ROTATION>45.100000</ROTATION>\n"
                + "\t<LATLNG>22.670000x33.890000</LATLNG>\n"
                + "\t<categoryAssignment index=\"1\"/>\n"
                + "</picture>\n";

        assertEquals(expected, sw.toString());
    }


    @Test
    public void testChecksum() {
        final PictureInfo pi = new PictureInfo();
        pi.setChecksum(123456789);
        assertEquals(123456789, pi.getChecksum());
        assertEquals("123456789", pi.getChecksumAsString());

        pi.setChecksum(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, pi.getChecksum());
        assertEquals("N/A", pi.getChecksumAsString());
    }

    @Test
    public void testCalculateChecksum() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final URL image = Objects.requireNonNull(PictureInfoTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg"));
        try {
            final PictureInfo pi = new PictureInfo(new File(image.toURI()), "Sample Picture");
            assertEquals("N/A", pi.getChecksumAsString());
            final TestPictureInfoChangeListener listener = new TestPictureInfoChangeListener();
            pi.addPictureInfoChangeListener(listener);
            pi.calculateChecksum();
            assertEquals("778423829", pi.getChecksumAsString());
            assertEquals(1, listener.events.size());
            assertTrue(listener.events.get(0).getChecksumChanged());
            pi.removePictureInfoChangeListener(listener);
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void calculateSha256() {
        final URL u = Objects.requireNonNull(PictureInfoTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg"));
        try {
            final PictureInfo pi = new PictureInfo(new File(u.toURI()), "Sample Picture");
            final HashCode hashCode = pi.calculateSha256();
            assertEquals("E7D7D40A06D1B974F741920A6489FDDA4CA4A05C55ED122C602B360640E9E67C", hashCode.toString().toUpperCase());

        } catch (final URISyntaxException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getSetFileHash() {
        final URL u = Objects.requireNonNull(PictureInfoTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg"));
        try {
            final PictureInfo pi = new PictureInfo(new File(u.toURI()), "Sample Picture");
            final TestPictureInfoChangeListener listener = new TestPictureInfoChangeListener();
            pi.addPictureInfoChangeListener(listener);
            assertNull(pi.getFileHash());
            assertEquals(0, listener.events.size());
            assertEquals("N/A", pi.getFileHashAsString());
            pi.setSha256();
            assertEquals("E7D7D40A06D1B974F741920A6489FDDA4CA4A05C55ED122C602B360640E9E67C", pi.getFileHashAsString());
            assertEquals(1, listener.events.size());
            assertTrue(listener.events.get(0).getFileHashChanged());

        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getSetFileHashBadFile() {
        final PictureInfo pi = new PictureInfo(new File("NoSuchFile.txt"), "Sample Picture");
        final TestPictureInfoChangeListener listener = new TestPictureInfoChangeListener();
        pi.addPictureInfoChangeListener(listener);
        assertNull(pi.getFileHash());
        pi.setSha256();
        assertEquals(0, listener.events.size());
        assertEquals("N/A", pi.getFileHashAsString());
    }

    @Test
    public void getSetFileHashGoodToBadFile() {
        final URL u = Objects.requireNonNull(PictureInfoTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg"));
        try {
            final PictureInfo pi = new PictureInfo(new File(u.toURI()), "Sample Picture");
            final TestPictureInfoChangeListener listener = new TestPictureInfoChangeListener();
            pi.addPictureInfoChangeListener(listener);
            assertNull(pi.getFileHash());
            pi.setSha256();
            assertEquals(1, listener.events.size());
            assertEquals("E7D7D40A06D1B974F741920A6489FDDA4CA4A05C55ED122C602B360640E9E67C", pi.getFileHashAsString());
            pi.setImageLocation(new File("NoSuchFile.txt"));
            assertEquals(2, listener.events.size());
            pi.setSha256();
            assertEquals(3, listener.events.size());
            assertEquals("N/A", pi.getFileHashAsString());
            assertTrue(listener.events.get(2).getFileHashChanged());

        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void getSetRotation() {
        final PictureInfo pi = new PictureInfo();
        assertEquals(0.0, pi.getRotation());
        pi.setRotation(45.5);
        assertEquals(45.5, pi.getRotation());

        pi.setRotation(90);
        assertEquals(90.0, pi.getRotation());

        pi.rotate(15.0);
        assertEquals(105.0, pi.getRotation());
    }

    @Test
    public void appendParseRotation() {
        final PictureInfo pi = new PictureInfo();
        pi.appendToRotation("270");
        pi.parseRotation();
        assertEquals(270.0, pi.getRotation());

        pi.appendToRotation("");
        assertEquals(270.0, pi.getRotation());

        pi.appendToRotation("5");
        pi.appendToRotation("6");
        pi.parseRotation();
        assertEquals(56.0, pi.getRotation());

        pi.appendToRotation("ABCD is not a number");
        pi.parseRotation();
        assertEquals(0.0, pi.getRotation());
    }

    @Test
    public void setRotationEvent() {
        final PictureInfo pi = new PictureInfo();
        final TestPictureInfoChangeListener listener = new TestPictureInfoChangeListener();
        pi.addPictureInfoChangeListener(listener);
        pi.setRotation(45.5);
        assertEquals(45.5, pi.getRotation());
        assertEquals(1, listener.events.size());
        assertTrue(listener.events.get(0).getRotationChanged());
        pi.removePictureInfoChangeListener(listener);
    }

    @Test
    public void setRotationEventTwice() {
        final PictureInfo pi = new PictureInfo();
        final TestPictureInfoChangeListener listener = new TestPictureInfoChangeListener();
        pi.addPictureInfoChangeListener(listener);
        pi.setRotation(45.5);
        assertEquals(45.5, pi.getRotation());
        assertEquals(1, listener.events.size());
        pi.setRotation(280.0);
        assertEquals(280.0, pi.getRotation());
        assertEquals(2, listener.events.size());
        assertTrue(listener.events.get(1).getRotationChanged());
        pi.removePictureInfoChangeListener(listener);
    }

    @Test
    public void setCategory() {
        final PictureInfo pi = new PictureInfo();
        final TestPictureInfoChangeListener listener = new TestPictureInfoChangeListener();
        assertEquals(0, listener.events.size());
        pi.addPictureInfoChangeListener(listener);
        pi.addCategoryAssignment("0");
        assertEquals(1, listener.events.size());
        pi.addCategoryAssignment("1");
        assertEquals(2, listener.events.size());

        // add the same one and we don't actually add to the set
        pi.addCategoryAssignment("1");
        assertEquals(2, listener.events.size());
    }


    private class TestPictureInfoChangeListener implements PictureInfoChangeListener {

        final List<PictureInfoChangeEvent> events = new ArrayList<>();

        @Override
        public void pictureInfoChangeEvent(PictureInfoChangeEvent pictureInfoChangeEvent) {
            events.add(pictureInfoChangeEvent);
        }
    }

}
