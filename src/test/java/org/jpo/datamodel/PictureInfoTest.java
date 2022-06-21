package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

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

/*
 Copyright (C) 2017 - 2022 Richard Eigenmann.
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
        pictureInfo.setSha256("1234");

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
                + "\t<sha256>1234</sha256>\n"
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
    void testSha256() {
        final PictureInfo pictureInfo = new PictureInfo();
        pictureInfo.setSha256("123456789");
        assertEquals("123456789", pictureInfo.getSha256());
    }

    @Test
    void calculateSha256() {
        final var url = Objects.requireNonNull(PictureInfoTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg"));
        try {
            final var pictureInfo = new PictureInfo(new File(url.toURI()), "Sample Picture");
            final var hashCode = pictureInfo.calculateSha256();
            assertEquals("E7D7D40A06D1B974F741920A6489FDDA4CA4A05C55ED122C602B360640E9E67C", hashCode.toString().toUpperCase());

        } catch (final URISyntaxException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getSetSha256() {
        final var url = Objects.requireNonNull(PictureInfoTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg"));
        try {
            final var pictureInfo = new PictureInfo(new File(url.toURI()), "Sample Picture");
            final var listener = new TestPictureInfoChangeListener();
            pictureInfo.addPictureInfoChangeListener(listener);
            assertEquals("", pictureInfo.getSha256());
            assertEquals(0, listener.events.size());
            pictureInfo.setSha256();
            assertEquals("E7D7D40A06D1B974F741920A6489FDDA4CA4A05C55ED122C602B360640E9E67C", pictureInfo.getSha256());
            assertEquals(1, listener.events.size());
            assertTrue(listener.events.get(0).getSha256Changed());

        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getSetFileHashBadFile() {
        final var pictureInfo = new PictureInfo(new File("NoSuchFile.txt"), "Sample Picture");
        final var listener = new TestPictureInfoChangeListener();
        pictureInfo.addPictureInfoChangeListener(listener);
        assertEquals("", pictureInfo.getSha256());
        pictureInfo.setSha256();
        assertEquals(1, listener.events.size());
        assertEquals("", pictureInfo.getSha256());
    }

    @Test
    void getSetFileHashGoodToBadFile() {
        final var url = Objects.requireNonNull(PictureInfoTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg"));
        try {
            final var pictureInfo = new PictureInfo(new File(url.toURI()), "Sample Picture");
            final var listener = new TestPictureInfoChangeListener();
            pictureInfo.addPictureInfoChangeListener(listener);
            assertEquals("", pictureInfo.getSha256());
            pictureInfo.setSha256();
            assertEquals(1, listener.events.size());
            assertEquals("E7D7D40A06D1B974F741920A6489FDDA4CA4A05C55ED122C602B360640E9E67C", pictureInfo.getSha256());
            pictureInfo.setImageLocation(new File("NoSuchFile.txt"));
            assertEquals(2, listener.events.size());
            pictureInfo.setSha256();
            assertEquals(3, listener.events.size());
            assertEquals("", pictureInfo.getSha256());
            assertTrue(listener.events.get(2).getSha256Changed());

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
    void dateFunctions() {
        final PictureInfo pictureInfo = new PictureInfo();
        System.setProperty("user.timezone", "Europe/Zurich");
        pictureInfo.setCreationTime("2021-10-02 at 14.43.23");
        assertEquals("2021-10-02 at 14.43.23", pictureInfo.getCreationTime());
        assertEquals(Tools.parseDate("2021-10-02 14:43:23"), pictureInfo.getCreationTimeAsDate());
        // can't figure out how to get travis to honor the timezone so we just compare the first 31 characters
        assertEquals("Parses as: Sat Oct 02 14:43:23 CEST 2021".substring(0, 31), pictureInfo.getFormattedCreationTime().substring(0, 31));
    }

    @Test
    void compareTo() {
        final var pictureInfo1 = new PictureInfo(new File("File1.jpg"), "First PictureInfo");
        final var pictureInfo2 = new PictureInfo(new File("File2.jpg"), "Second PictureInfo");
        assert (pictureInfo1.compareTo(pictureInfo2, Settings.FieldCodes.DESCRIPTION) < 0);
        assert (pictureInfo2.compareTo(pictureInfo1, Settings.FieldCodes.DESCRIPTION) > 0);
        assert (pictureInfo1.compareTo(pictureInfo1, Settings.FieldCodes.DESCRIPTION) == 0);

        pictureInfo1.setFilmReference("Reference 1");
        pictureInfo2.setFilmReference("Reference 2");
        assert (pictureInfo1.compareTo(pictureInfo2, Settings.FieldCodes.FILM_REFERENCE) < 0);
        assert (pictureInfo2.compareTo(pictureInfo1, Settings.FieldCodes.FILM_REFERENCE) > 0);
        assert (pictureInfo1.compareTo(pictureInfo1, Settings.FieldCodes.FILM_REFERENCE) == 0);

        pictureInfo1.setCreationTime("2021-10-02 at 14.43.23");
        pictureInfo2.setCreationTime("2021-10-02 at 14.43.24");
        assert (pictureInfo1.compareTo(pictureInfo2, Settings.FieldCodes.CREATION_TIME) < 0);
        assert (pictureInfo2.compareTo(pictureInfo1, Settings.FieldCodes.CREATION_TIME) > 0);
        assert (pictureInfo1.compareTo(pictureInfo1, Settings.FieldCodes.CREATION_TIME) == 0);

        pictureInfo1.setComment("Comment 1");
        pictureInfo2.setComment("Comment 2");
        assert (pictureInfo1.compareTo(pictureInfo2, Settings.FieldCodes.COMMENT) < 0);
        assert (pictureInfo2.compareTo(pictureInfo1, Settings.FieldCodes.COMMENT) > 0);
        assert (pictureInfo1.compareTo(pictureInfo1, Settings.FieldCodes.COMMENT) == 0);

        pictureInfo1.setPhotographer("Photographer 1");
        pictureInfo2.setPhotographer("Photographer 2");
        assert (pictureInfo1.compareTo(pictureInfo2, Settings.FieldCodes.PHOTOGRAPHER) < 0);
        assert (pictureInfo2.compareTo(pictureInfo1, Settings.FieldCodes.PHOTOGRAPHER) > 0);
        assert (pictureInfo1.compareTo(pictureInfo1, Settings.FieldCodes.PHOTOGRAPHER) == 0);

        pictureInfo1.setCopyrightHolder("Copyright 1");
        pictureInfo2.setCopyrightHolder("Copyright 2");
        assert (pictureInfo1.compareTo(pictureInfo2, Settings.FieldCodes.COPYRIGHT_HOLDER) < 0);
        assert (pictureInfo2.compareTo(pictureInfo1, Settings.FieldCodes.COPYRIGHT_HOLDER) > 0);
        assert (pictureInfo1.compareTo(pictureInfo1, Settings.FieldCodes.COPYRIGHT_HOLDER) == 0);
    }

}
