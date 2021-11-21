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
 Copyright (C) 2017 - 2021 Richard Eigenmann.
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
class PictureInfoTest {

    /**
     * Test of toString method, of class PictureInfo.
     */
    @Test
    void testToString() {
        final PictureInfo pictureInfo = new PictureInfo(new File("c:\\picture.jpg"), "My Sample Picture");
        assertEquals("My Sample Picture", pictureInfo.toString());
    }

    /**
     * Test of getDescription method, of class PictureInfo.
     */
    @Test
    void testGetDescription() {
        final PictureInfo pictureInfo = new PictureInfo(new File("c:\\picture.jpg"), "My Sample Picture");
        assertEquals("My Sample Picture", pictureInfo.getDescription());
    }

    /**
     * Test of setDescription method, of class PictureInfo.
     */
    @Test
    void testSetDescription() {
        final PictureInfo pictureInfo = new PictureInfo();
        final int[] changeEvents = {0};
        final PictureInfoChangeListener pictureInfoChangeListener = (PictureInfoChangeEvent arg0) -> changeEvents[0] += 1;
        pictureInfo.addPictureInfoChangeListener(pictureInfoChangeListener);
        pictureInfo.setDescription("A description");
        assertEquals("A description", pictureInfo.getDescription());
        assertEquals(1, changeEvents[0]);
        pictureInfo.setDescription("A different description");
        assertEquals("A different description", pictureInfo.getDescription());
        assertEquals(2, changeEvents[0]);
    }

    /**
     * Test Description change event
     */
    @Test
    void testSetDescriptionSame() {
        final PictureInfo pictureInfo = new PictureInfo();
        final int[] countEvents = {0};
        final PictureInfoChangeListener pictureInfoChangeListener = (PictureInfoChangeEvent arg0) -> countEvents[0] += 1;
        pictureInfo.addPictureInfoChangeListener(pictureInfoChangeListener);
        pictureInfo.setDescription("A picture description");
        // Expecting what went in to come out
        assertEquals("A picture description", pictureInfo.getDescription());
        assertEquals(1, countEvents[0]);
        pictureInfo.setDescription("A picture description");
        // Expecting what went in to come out
        assertEquals("A picture description", pictureInfo.getDescription());
        // Expecting no new change event because it was the same that went in
        assertEquals(1, countEvents[0]);
    }

    /**
     * Test of appendToDescription method, of class PictureInfo.
     */
    @Test
    void testAppendToDescription() {
        final PictureInfo pictureInfo = new PictureInfo();
        pictureInfo.setDescription("A picture description");
        pictureInfo.appendToDescription(" concatenated from two texts");
        assertEquals("A picture description concatenated from two texts", pictureInfo.getDescription());
    }

    /**
     * Test of descriptionContains method, of class PictureInfo.
     */
    @Test
    void testDescriptionContains() {
        final PictureInfo pictureInfo = new PictureInfo();
        pictureInfo.setDescription("A picture of a big town at sunset");
        assertTrue(pictureInfo.descriptionContains("town"));
    }

    /**
     * Test of getImageLocation method, of class PictureInfo.
     */
    @Test
    void testGetImageLocation() {
        final PictureInfo pictureInfo = new PictureInfo(new File("/dir/picture.jpg"), "My Sample Picture");
        final String imageLocation = pictureInfo.getImageLocation();
        assertEquals("file:/dir/picture.jpg", imageLocation);
    }

    @Test
    void testGetImageLocationNull() {
        final PictureInfo pictureInfo = new PictureInfo();
        final String imageLocation = pictureInfo.getImageLocation();
        assertEquals("", imageLocation);
    }


    /**
     * Test of getImageFile method, of class PictureInfo.
     */
    @Test
    void testGetImageFile() {
        final String FILENAME = "/dir/picture.jpg";
        final PictureInfo pictureInfo = new PictureInfo(new File(FILENAME), "My Sample Picture");
        assertEquals(new File(FILENAME), pictureInfo.getImageFile());
    }

    /**
     * Test of getImageURIOrNull method, of class PictureInfo.
     */
    @Test
    void testGetImageURIOrNull() {
        final PictureInfo pictureInfo = new PictureInfo();
        final String goodLocation = "/image.jpg";
        pictureInfo.setImageLocation(new File(goodLocation));
        final URI pulledUri = pictureInfo.getImageURIOrNull();
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
    void testSetImageLocationString() {
        final PictureInfo pictureInfo = new PictureInfo();
        pictureInfo.setImageLocation(new File("/dir/picture.jpg"));
        final File imageFile = pictureInfo.getImageFile();
        assertEquals("/dir/picture.jpg", imageFile.toString());
    }

    @Test
    void testSetImageLocationWithSpace() {
        final PictureInfo pictureInfo = new PictureInfo();
        pictureInfo.setImageLocation(new File("/dir/picture file.jpg"));
        final File imageFile = pictureInfo.getImageFile();
        assertEquals("/dir/picture file.jpg", imageFile.toString());
    }

    /**
     * Test of setImageLocation method, of class PictureInfo.
     */
    @Test
    void testSetImageLocationUrl() {
        try {
            final PictureInfo pictureInfo = new PictureInfo();
            pictureInfo.setImageLocation(new File(new URL("file:///dir/picture.jpg").toURI()));
            final File imageFile = pictureInfo.getImageFile();
            assertEquals("/dir/picture.jpg", imageFile.toString());
        } catch (final MalformedURLException | URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of appendToImageLocation method, of class PictureInfo.
     */
    @Test
    void testAppendToImageLocation() {
        final PictureInfo pictureInfo = new PictureInfo();
        pictureInfo.appendToImageLocation("file:///dir/picture");
        pictureInfo.appendToImageLocation(".jpg");
        final File imageFile = pictureInfo.getImageFile();
        assertEquals("/dir/picture.jpg", imageFile.toString());
    }

    /**
     * Test of getImageFilename method, of class PictureInfo.
     */
    @Test
    void testGetHighresFilename() {
        final PictureInfo pictureInfo = new PictureInfo();
        pictureInfo.setImageLocation(new File("/dir/picture.jpg"));
        final String filename = pictureInfo.getImageFile().getName();
        assertEquals("picture.jpg", filename);
    }

    /**
     * Test the change listener
     */
    @Test
    void testPictureInfoChangeListener() {
        int[] eventsReceived = new int[1];
        eventsReceived[0] = 0;
        final PictureInfoChangeListener pictureInfoChangeListener = e -> eventsReceived[0]++;
        final PictureInfo pictureInfo = new PictureInfo();
        assertEquals(0, eventsReceived[0]);
        pictureInfo.setDescription("Step 1");
        // There is no listener attached so there is no event
        assertEquals(0, eventsReceived[0]);
        pictureInfo.addPictureInfoChangeListener(pictureInfoChangeListener);
        pictureInfo.setDescription("Step 2");
        // The listener should have fired and we should have 1 event
        assertEquals(1, eventsReceived[0]);
        pictureInfo.removePictureInfoChangeListener(pictureInfoChangeListener);
        pictureInfo.setDescription("Step 3");
        // The detached listener should not have fired
        assertEquals(1, eventsReceived[0]);
    }

    /**
     * Test dumpToXml
     */
    @Test
    void testDumpToXml() {
        final URL url = Objects.requireNonNull(PictureInfoTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg"));
        final PictureInfo pictureInfo = new PictureInfo(new File(url.getFile()), "First <Picture> & difficult xml chars ' \"");
        pictureInfo.setComment("Comment <<&>'\">");
        pictureInfo.setFilmReference("Reference <<&>'\">");
        pictureInfo.setRotation(45.1);
        pictureInfo.setPhotographer("Richard Eigenmann <<&>'\">");
        pictureInfo.setLatLng("22.67x33.89");
        pictureInfo.setCopyrightHolder("Sandra Keller <<&>'\">");
        pictureInfo.addCategoryAssignment("1");
        pictureInfo.setChecksum(1234);

        final StringWriter stringWriter = new StringWriter();
        try (final BufferedWriter bufferedWriter = new BufferedWriter(stringWriter)) {
            pictureInfo.dumpToXml(bufferedWriter);
        } catch (final IOException ex) {
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

        assertEquals(expected, stringWriter.toString());
    }


    @Test
    void testChecksum() {
        final PictureInfo pictureInfo = new PictureInfo();
        pictureInfo.setChecksum(123456789);
        assertEquals(123456789, pictureInfo.getChecksum());
        assertEquals("123456789", pictureInfo.getChecksumAsString());

        pictureInfo.setChecksum(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, pictureInfo.getChecksum());
        assertEquals("N/A", pictureInfo.getChecksumAsString());
    }

    @Test
    void testCalculateChecksum() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final URL image = Objects.requireNonNull(PictureInfoTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg"));
        try {
            final PictureInfo pictureInfo = new PictureInfo(new File(image.toURI()), "Sample Picture");
            assertEquals("N/A", pictureInfo.getChecksumAsString());
            final TestPictureInfoChangeListener listener = new TestPictureInfoChangeListener();
            pictureInfo.addPictureInfoChangeListener(listener);
            pictureInfo.calculateChecksum();
            assertEquals("778423829", pictureInfo.getChecksumAsString());
            assertEquals(1, listener.events.size());
            assertTrue(listener.events.get(0).getChecksumChanged());
            pictureInfo.removePictureInfoChangeListener(listener);
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }

    }

    @Test
    void calculateSha256() {
        final URL url = Objects.requireNonNull(PictureInfoTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg"));
        try {
            final PictureInfo pictureInfo = new PictureInfo(new File(url.toURI()), "Sample Picture");
            final HashCode hashCode = pictureInfo.calculateSha256();
            assertEquals("E7D7D40A06D1B974F741920A6489FDDA4CA4A05C55ED122C602B360640E9E67C", hashCode.toString().toUpperCase());

        } catch (final URISyntaxException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getSetFileHash() {
        final URL url = Objects.requireNonNull(PictureInfoTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg"));
        try {
            final PictureInfo pictureInfo = new PictureInfo(new File(url.toURI()), "Sample Picture");
            final TestPictureInfoChangeListener listener = new TestPictureInfoChangeListener();
            pictureInfo.addPictureInfoChangeListener(listener);
            assertNull(pictureInfo.getFileHash());
            assertEquals(0, listener.events.size());
            assertEquals("N/A", pictureInfo.getFileHashAsString());
            pictureInfo.setSha256();
            assertEquals("E7D7D40A06D1B974F741920A6489FDDA4CA4A05C55ED122C602B360640E9E67C", pictureInfo.getFileHashAsString());
            assertEquals(1, listener.events.size());
            assertTrue(listener.events.get(0).getFileHashChanged());

        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getSetFileHashBadFile() {
        final PictureInfo pictureInfo = new PictureInfo(new File("NoSuchFile.txt"), "Sample Picture");
        final TestPictureInfoChangeListener listener = new TestPictureInfoChangeListener();
        pictureInfo.addPictureInfoChangeListener(listener);
        assertNull(pictureInfo.getFileHash());
        pictureInfo.setSha256();
        assertEquals(0, listener.events.size());
        assertEquals("N/A", pictureInfo.getFileHashAsString());
    }

    @Test
    void getSetFileHashGoodToBadFile() {
        final URL url = Objects.requireNonNull(PictureInfoTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg"));
        try {
            final PictureInfo pictureInfo = new PictureInfo(new File(url.toURI()), "Sample Picture");
            final TestPictureInfoChangeListener listener = new TestPictureInfoChangeListener();
            pictureInfo.addPictureInfoChangeListener(listener);
            assertNull(pictureInfo.getFileHash());
            pictureInfo.setSha256();
            assertEquals(1, listener.events.size());
            assertEquals("E7D7D40A06D1B974F741920A6489FDDA4CA4A05C55ED122C602B360640E9E67C", pictureInfo.getFileHashAsString());
            pictureInfo.setImageLocation(new File("NoSuchFile.txt"));
            assertEquals(2, listener.events.size());
            pictureInfo.setSha256();
            assertEquals(3, listener.events.size());
            assertEquals("N/A", pictureInfo.getFileHashAsString());
            assertTrue(listener.events.get(2).getFileHashChanged());

        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }


    @Test
    void getSetRotation() {
        final PictureInfo pictureInfo = new PictureInfo();
        assertEquals(0.0, pictureInfo.getRotation());
        pictureInfo.setRotation(45.5);
        assertEquals(45.5, pictureInfo.getRotation());

        pictureInfo.setRotation(90);
        assertEquals(90.0, pictureInfo.getRotation());

        pictureInfo.rotate(15.0);
        assertEquals(105.0, pictureInfo.getRotation());
    }

    @Test
    void appendParseRotation() {
        final PictureInfo pictureInfo = new PictureInfo();
        pictureInfo.appendToRotation("270");
        pictureInfo.parseRotation();
        assertEquals(270.0, pictureInfo.getRotation());

        pictureInfo.appendToRotation("");
        assertEquals(270.0, pictureInfo.getRotation());

        pictureInfo.appendToRotation("5");
        pictureInfo.appendToRotation("6");
        pictureInfo.parseRotation();
        assertEquals(56.0, pictureInfo.getRotation());

        pictureInfo.appendToRotation("ABCD is not a number");
        pictureInfo.parseRotation();
        assertEquals(0.0, pictureInfo.getRotation());
    }

    @Test
    void setRotationEvent() {
        final PictureInfo pictureInfo = new PictureInfo();
        final TestPictureInfoChangeListener listener = new TestPictureInfoChangeListener();
        pictureInfo.addPictureInfoChangeListener(listener);
        pictureInfo.setRotation(45.5);
        assertEquals(45.5, pictureInfo.getRotation());
        assertEquals(1, listener.events.size());
        assertTrue(listener.events.get(0).getRotationChanged());
        pictureInfo.removePictureInfoChangeListener(listener);
    }

    @Test
    void setRotationEventTwice() {
        final PictureInfo pictureInfo = new PictureInfo();
        final TestPictureInfoChangeListener listener = new TestPictureInfoChangeListener();
        pictureInfo.addPictureInfoChangeListener(listener);
        pictureInfo.setRotation(45.5);
        assertEquals(45.5, pictureInfo.getRotation());
        assertEquals(1, listener.events.size());
        pictureInfo.setRotation(280.0);
        assertEquals(280.0, pictureInfo.getRotation());
        assertEquals(2, listener.events.size());
        assertTrue(listener.events.get(1).getRotationChanged());
        pictureInfo.removePictureInfoChangeListener(listener);
    }

    @Test
    void setCategory() {
        final PictureInfo pictureInfo = new PictureInfo();
        final TestPictureInfoChangeListener listener = new TestPictureInfoChangeListener();
        assertEquals(0, listener.events.size());
        pictureInfo.addPictureInfoChangeListener(listener);
        pictureInfo.addCategoryAssignment("0");
        assertEquals(1, listener.events.size());
        pictureInfo.addCategoryAssignment("1");
        assertEquals(2, listener.events.size());

        // add the same one and we don't actually add to the set
        pictureInfo.addCategoryAssignment("1");
        assertEquals(2, listener.events.size());
    }


    private class TestPictureInfoChangeListener implements PictureInfoChangeListener {

        final List<PictureInfoChangeEvent> events = new ArrayList<>();

        @Override
        public void pictureInfoChangeEvent(PictureInfoChangeEvent pictureInfoChangeEvent) {
            events.add(pictureInfoChangeEvent);
        }
    }

    @Test
    void DateFunctions() {
        final PictureInfo pictureInfo = new PictureInfo();
        pictureInfo.setCreationTime("2021-10-02 at 14.43.23");
        assertEquals("2021-10-02 at 14.43.23", pictureInfo.getCreationTime());
        assertEquals(Tools.parseDate("2021-10-02 14:43:23"), pictureInfo.getCreationTimeAsDate());
        assertEquals("Parses as: Sat Oct 02 14:43:23 CEST 2021", pictureInfo.getFormattedCreationTime());
    }
}
