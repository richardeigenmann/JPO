package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/*
 Copyright (C) 2013-2024 Richard Eigenmann.
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
 * JUnit tests for the ExifInfo class that calls Drew Noake's exif extraction
 * library
 *
 * @author Richard Eigenmann
 */
class ExifInfoTest {


    /**
     * A handy reference to 0 in the form of a double
     */
    private static final double ZERO = 0;

    /**
     * That the Exif reader gracefully handles a null URL
     */
    @Test
    void testExifInfoNull() {
        var exifInfo = new ExifInfo(null);
        exifInfo.decodeExifTags();
        assertEquals("", exifInfo.getAperture());
        assertEquals( "", exifInfo.getCamera());
        assertEquals( "", exifInfo.getCreateDateTime());
        assertEquals( "", exifInfo.getExifHeight());
        assertEquals("", exifInfo.getExifWidth());
        assertEquals( "", exifInfo.getFocalLength());
        assertEquals( "", exifInfo.getIso());
        assertEquals(ZERO, exifInfo.getLatLng().getX());
        assertEquals(ZERO, exifInfo.getLatLng().getY());
        assertEquals("", exifInfo.getLens());
        assertEquals("", exifInfo.getShutterSpeed());
        assertEquals(0, exifInfo.getRotation());
    }

    /**
     * Test that we get the correct data off a Nikon D100 image
     */
    @Test
    void testExifInfoD100() {
        final var NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        try {
            var exifInfo = new ExifInfo(new File(Objects.requireNonNull(ExifInfoTest.class.getClassLoader().getResource(NIKON_D100_IMAGE)).toURI()));
            exifInfo.decodeExifTags();
            assertEquals("f/11.0", exifInfo.getAperture());
            assertEquals("NIKON D100", exifInfo.getCamera());
            assertEquals("2008:11:07 16:23:25", exifInfo.getCreateDateTime());
            assertEquals("233 pixels", exifInfo.getExifHeight());
            assertEquals("350 pixels", exifInfo.getExifWidth());
            assertEquals("82 mm", exifInfo.getFocalLength());
            assertEquals("ISO 640", exifInfo.getIso());
            assertEquals(ZERO, exifInfo.getLatLng().getX());
            assertEquals(ZERO, exifInfo.getLatLng().getY());
            assertEquals("24-120mm f/3.5-5.6", exifInfo.getLens());
            assertEquals("1/750 sec", exifInfo.getShutterSpeed());
            assertEquals(0, exifInfo.getRotation());
        } catch (NullPointerException | URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test that we get the correct data off a Samsung Galaxy S4 image which
     * doesn't have location info
     */
    @Test
    void testExifInfoS4() {
        final var SAMSUNG_S4_IMAGE = "exif-test-samsung-s4.jpg";
        try {
            var exifInfo = new ExifInfo(new File(Objects.requireNonNull(ExifInfoTest.class.getClassLoader().getResource(SAMSUNG_S4_IMAGE)).toURI()));
            exifInfo.decodeExifTags();
            assertEquals("f/2.2", exifInfo.getAperture());
            assertEquals("GT-I9505", exifInfo.getCamera());
            assertEquals("2013:08:27 23:37:56", exifInfo.getCreateDateTime());
            assertEquals("1836 pixels", exifInfo.getExifHeight());
            assertEquals("3264 pixels", exifInfo.getExifWidth());
            assertEquals("4.2 mm", exifInfo.getFocalLength());
            assertEquals("50", exifInfo.getIso());
            assertEquals( ZERO, exifInfo.getLatLng().getX());
            assertEquals( ZERO, exifInfo.getLatLng().getY());
            assertEquals( "", exifInfo.getLens());
            assertEquals( "1/1883 sec", exifInfo.getShutterSpeed());
            assertEquals( 0, exifInfo.getRotation());
        } catch (NullPointerException | URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test that we get the correct data off a Samsung Galaxy S4 image which
     * doesn't have location info
     */
    @Test
    void testExifInfoS4Loc() {
        final var SAMSUNG_S4_LOC_IMAGE = "exif-test-samsung-s4-loc.jpg";
        try {
            var exifInfo = new ExifInfo(new File(Objects.requireNonNull(ExifInfoTest.class.getClassLoader().getResource(SAMSUNG_S4_LOC_IMAGE)).toURI()));
            exifInfo.decodeExifTags();
            assertEquals( "f/2.2", exifInfo.getAperture());
            assertEquals("GT-I9505", exifInfo.getCamera());
            assertEquals( "2014:01:15 12:11:46", exifInfo.getCreateDateTime());
            assertEquals( "1836 pixels", exifInfo.getExifHeight());
            assertEquals("3264 pixels", exifInfo.getExifWidth());
            assertEquals( "4.2 mm", exifInfo.getFocalLength());
            assertEquals( "50", exifInfo.getIso());
            assertEquals( 46.80724713888888, exifInfo.getLatLng().getX());
            assertEquals( 9.812583916666668, exifInfo.getLatLng().getY());
            assertEquals( "", exifInfo.getLens());
            assertEquals( "1/1879 sec", exifInfo.getShutterSpeed());
            assertEquals( 0, exifInfo.getRotation());
        } catch (NullPointerException | URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test that we get the correct rotation data off a Samsung Galaxy S4 image
     * which was not rotated
     */
    @Test
    void testExifInfoS4Rot0() {
        final var SAMSUNG_S4_ROT0_IMAGE = "exif-test-samsung-s4-rotation-0.jpg";
        try {
            var exifInfo = new ExifInfo(new File(Objects.requireNonNull(ExifInfoTest.class.getClassLoader().getResource(SAMSUNG_S4_ROT0_IMAGE)).toURI()));
            exifInfo.decodeExifTags();
            assertEquals("", exifInfo.getAperture());
            assertEquals("GT-I9505", exifInfo.getCamera());
            assertEquals("2014:04:21 09:30:13", exifInfo.getCreateDateTime());
            assertEquals("2048 pixels", exifInfo.getExifWidth());
            assertEquals("1152 pixels", exifInfo.getExifHeight());
            assertEquals("", exifInfo.getFocalLength());
            assertEquals("", exifInfo.getIso());
            assertEquals( ZERO, exifInfo.getLatLng().getX());
            assertEquals(ZERO, exifInfo.getLatLng().getY());
            assertEquals("", exifInfo.getLens());
            assertEquals("", exifInfo.getShutterSpeed());
            assertEquals(0, exifInfo.getRotation());
        } catch (NullPointerException | URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test that we get the correct rotation data off a Samsung Galaxy S4 image
     * which was rotated left
     */
    @Test
    void testExifInfoS4RotLeft() {
        final var SAMSUNG_S4_ROT_LEFT_IMAGE = "exif-test-samsung-s4-rotation-left.jpg";
        try {
            var exifInfo = new ExifInfo(new File(Objects.requireNonNull(ExifInfoTest.class.getClassLoader().getResource(SAMSUNG_S4_ROT_LEFT_IMAGE)).toURI()));
            exifInfo.decodeExifTags();
            assertEquals("", exifInfo.getAperture());
            assertEquals("GT-I9505", exifInfo.getCamera());
            assertEquals("2014:04:21 09:30:22", exifInfo.getCreateDateTime());
            assertEquals("2048 pixels", exifInfo.getExifWidth());
            assertEquals("1152 pixels", exifInfo.getExifHeight());
            assertEquals("", exifInfo.getFocalLength());
            assertEquals("", exifInfo.getIso());
            assertEquals(ZERO, exifInfo.getLatLng().getX());
            assertEquals(ZERO, exifInfo.getLatLng().getY());
            assertEquals("", exifInfo.getLens());
            assertEquals("", exifInfo.getShutterSpeed());
            assertEquals(90, exifInfo.getRotation());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test that we get the correct rotation data off a Samsung Galaxy S4 image
     * which was rotated left
     */
    @Test
    void testExifInfoS4RotRight() {
        final var SAMSUNG_S4_ROT_RIGHT_IMAGE = "exif-test-samsung-s4-rotation-right.jpg";
        try {
            var exifInfo = new ExifInfo(new File(Objects.requireNonNull(ExifInfoTest.class.getClassLoader().getResource(SAMSUNG_S4_ROT_RIGHT_IMAGE)).toURI()));
            exifInfo.decodeExifTags();
            assertEquals("", exifInfo.getAperture());
            assertEquals("GT-I9505", exifInfo.getCamera());
            assertEquals("2014:04:21 09:30:39", exifInfo.getCreateDateTime());
            assertEquals("2048 pixels", exifInfo.getExifWidth());
            assertEquals("1152 pixels", exifInfo.getExifHeight());
            assertEquals("", exifInfo.getFocalLength());
            assertEquals("", exifInfo.getIso());
            assertEquals(ZERO, exifInfo.getLatLng().getX());
            assertEquals(ZERO, exifInfo.getLatLng().getY());
            assertEquals("", exifInfo.getLens());
            assertEquals("", exifInfo.getShutterSpeed());
            assertEquals(270, exifInfo.getRotation());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test that we get the correct rotation data off a Samsung Galaxy S4 image
     * which was rotated upside down
     */
    @Test
    void testExifInfoS4RotUpsideDown() {
        final var SAMSUNG_S4_ROT_UPSIDEDOWN_IMAGE = "exif-test-samsung-s4-upside-down.jpg";
        try {
            var exifInfo = new ExifInfo(new File(Objects.requireNonNull(ExifInfoTest.class.getClassLoader().getResource(SAMSUNG_S4_ROT_UPSIDEDOWN_IMAGE)).toURI()));
            exifInfo.decodeExifTags();
            assertEquals("", exifInfo.getAperture());
            assertEquals("GT-I9505", exifInfo.getCamera());
            assertEquals("2014:04:21 09:30:29", exifInfo.getCreateDateTime());
            assertEquals("2048 pixels", exifInfo.getExifWidth());
            assertEquals("1152 pixels", exifInfo.getExifHeight());
            assertEquals("", exifInfo.getFocalLength());
            assertEquals("", exifInfo.getIso());
            assertEquals(ZERO, exifInfo.getLatLng().getX());
            assertEquals(ZERO, exifInfo.getLatLng().getY());
            assertEquals("", exifInfo.getLens());
            assertEquals("", exifInfo.getShutterSpeed());
            assertEquals(180, exifInfo.getRotation());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests for images from a Canon Eos 350d
     */
    @Test
    void testExifInfoEos350d() {
        final var CANON_EOS350D_IMAGE = "exif-test-canon-eos-350d.jpg";
        try {
            final var exifInfo = new ExifInfo(new File(Objects.requireNonNull(ExifInfoTest.class.getClassLoader().getResource(CANON_EOS350D_IMAGE)).toURI()));
            exifInfo.decodeExifTags();
            assertEquals("f/20.0", exifInfo.getAperture());
            assertEquals("1/200 sec", exifInfo.getShutterSpeed());
            assertEquals("Canon EOS 350D DIGITAL", exifInfo.getCamera());
            assertEquals("2006:10:06 15:13:54", exifInfo.getCreateDateTime());
            assertEquals("1664 pixels", exifInfo.getExifHeight());
            assertEquals("2496 pixels", exifInfo.getExifWidth());
            assertEquals("18 mm", exifInfo.getFocalLength());
            assertEquals("200", exifInfo.getIso());
            assertEquals(ZERO, exifInfo.getLatLng().getX());
            assertEquals(ZERO, exifInfo.getLatLng().getY());
            assertEquals("18 - 55mm", exifInfo.getLens());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests for images from a Canon Eos 60D
     */
    @Test
    void testExifInfoCanonEos60D() {
        final var CANON_EOS60D_IMAGE = "exif-test-canon-eos-60d.jpg";
        try {
            var exifInfo = new ExifInfo(new File(Objects.requireNonNull(ExifInfoTest.class.getClassLoader().getResource(CANON_EOS60D_IMAGE)).toURI()));
            exifInfo.decodeExifTags();
            assertEquals("f/11.3", exifInfo.getAperture());
            assertEquals("1/511 sec", exifInfo.getShutterSpeed());
            assertEquals("Canon EOS 60D", exifInfo.getCamera());
            assertEquals("2013:05:09 13:55:16", exifInfo.getCreateDateTime());
            assertEquals("3456 pixels", exifInfo.getExifHeight());
            assertEquals("5184 pixels", exifInfo.getExifWidth());
            assertEquals("85 mm", exifInfo.getFocalLength());
            assertEquals("400", exifInfo.getIso());
            assertEquals(ZERO, exifInfo.getLatLng().getX());
            assertEquals(ZERO, exifInfo.getLatLng().getY());
            assertEquals("EF-S17-85mm f/4-5.6 IS USM", exifInfo.getLens());
            assertEquals(0, exifInfo.getRotation());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests for images from a Canon Cybershot
     */
    @Test
    void testExifInfoCybershot1() {
        final var CANON_CYBERSHOT1_IMAGE = "exif-test-sony-cybershot-1.jpg";
        try {
            var exifInfo = new ExifInfo(new File(Objects.requireNonNull(ExifInfoTest.class.getClassLoader().getResource(CANON_CYBERSHOT1_IMAGE)).toURI()));
            exifInfo.decodeExifTags();
            assertEquals("f/4.0", exifInfo.getAperture());
            assertEquals("1/480 sec", exifInfo.getShutterSpeed());
            assertEquals("CYBERSHOT", exifInfo.getCamera());
            assertEquals("2002:02:20 16:17:37", exifInfo.getCreateDateTime());
            assertEquals("768 pixels", exifInfo.getExifHeight());
            assertEquals("1024 pixels", exifInfo.getExifWidth());
            assertEquals("6.1 mm", exifInfo.getFocalLength());
            assertEquals("80", exifInfo.getIso());
            assertEquals(ZERO, exifInfo.getLatLng().getX());
            assertEquals(ZERO, exifInfo.getLatLng().getY());
            assertEquals("", exifInfo.getLens());
            assertEquals(0, exifInfo.getRotation());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests for a Sony D700
     */
    @Test
    void testExifInfoSonyD700() {
        final var SONY_D700_IMAGE = "exif-test-sony-d700.jpg";
        try {
            var exifInfo = new ExifInfo(new File(ClassLoader.getSystemResources(SONY_D700_IMAGE).nextElement().toURI()));
            exifInfo.decodeExifTags();
            assertEquals("f/2.4", exifInfo.getAperture());
            assertEquals("1/32 sec", exifInfo.getShutterSpeed());
            assertEquals("DSC-D700", exifInfo.getCamera());
            assertEquals("1998:12:01 14:22:36", exifInfo.getCreateDateTime());
            assertEquals("512 pixels", exifInfo.getExifHeight());
            assertEquals("672 pixels", exifInfo.getExifWidth());
            assertEquals("", exifInfo.getFocalLength());
            assertEquals("200", exifInfo.getIso());
            assertEquals(ZERO, exifInfo.getLatLng().getX());
            assertEquals(ZERO, exifInfo.getLatLng().getY());
            assertEquals("", exifInfo.getLens());
            assertEquals(0, exifInfo.getRotation());
        } catch (URISyntaxException | IOException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests for a Sony P200
     */
    @Test
    void testExifInfoSonyP200() {
        final var SONY_P200_IMAGE = "exif-test-sony-P200.jpg";
        try {
            var exifInfo = new ExifInfo(new File(Objects.requireNonNull(ExifInfoTest.class.getClassLoader().getResource(SONY_P200_IMAGE)).toURI()));
            exifInfo.decodeExifTags();
            assertEquals("f/5.6", exifInfo.getAperture());
            assertEquals("1/400 sec", exifInfo.getShutterSpeed());
            assertEquals("DSC-P200", exifInfo.getCamera());
            assertEquals("2013:07:04 13:44:46", exifInfo.getCreateDateTime());
            assertEquals("2304 pixels", exifInfo.getExifHeight());
            assertEquals("3072 pixels", exifInfo.getExifWidth());
            assertEquals("7.9 mm", exifInfo.getFocalLength());
            assertEquals("100", exifInfo.getIso());
            assertEquals(ZERO, exifInfo.getLatLng().getX());
            assertEquals(ZERO, exifInfo.getLatLng().getY());
            assertEquals("", exifInfo.getLens());
            assertEquals(0, exifInfo.getRotation());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

}
