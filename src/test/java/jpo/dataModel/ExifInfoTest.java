package jpo.dataModel;

import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/*
 ExifInfoTest.java: This class interacts with Drew Noake's library and extracts the Exif information

 Copyright (C) 2013-2014  Richard Eigenmann.
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
 * JUnit tests for the ExifInfo class that calls Drew Noake's exif extraction
 * library
 *
 * @author Richard Eigenmann
 */
public class ExifInfoTest
        extends TestCase {

    /**
     * Constructor
     *
     * @param testName test name
     */
    public ExifInfoTest( String testName ) {
        super( testName );
    }

    /**
     * A handy reference to 0 in the form of a double
     */
    private static final double ZERO = 0;

    /**
     * That the Exif reader gracefully handles a null URL
     */
    public void testExifInfoNull() {
        ExifInfo exifInfo = new ExifInfo( null );
        exifInfo.decodeExifTags();
        assertEquals( "Aperture parsing verification", "", exifInfo.aperture );
        assertEquals( "Camera parsing verification", "", exifInfo.camera );
        assertEquals( "CreateDateTime parsing verification", "", exifInfo.getCreateDateTime() );
        assertEquals( "ExifHeight parsing verification", "", exifInfo.exifHeight );
        assertEquals( "ExifWidth parsing verification", "", exifInfo.exifWidth );
        assertEquals( "FocalLength parsing verification", "", exifInfo.focalLength );
        assertEquals( "ISO parsing verification", "", exifInfo.iso );
        assertEquals( "Longitude parsing verification", ZERO, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", ZERO, exifInfo.latLng.getY() );
        assertEquals( "Lens parsing verification", "", exifInfo.lens );
        assertEquals( "ShutterSpeed parsing verification", "", exifInfo.shutterSpeed );
        assertEquals( "Rotation parsing verification", 0, exifInfo.rotation );
    }

    /**
     * Test that we get the correct data off a Nikon D100 image
     */
    public void testExifInfoD100() {
        ExifInfo exifInfo = new ExifInfo( Settings.CLASS_LOADER.getResource( "exif-test-nikon-d100-1.jpg" ) );
        exifInfo.decodeExifTags();
        assertEquals( "Aperture parsing verification", "f/11.0", exifInfo.aperture );
        assertEquals( "Camera parsing verification", "NIKON D100", exifInfo.camera );
        assertEquals( "CreateDateTime parsing verification", "2008:11:07 16:23:25", exifInfo.getCreateDateTime() );
        assertEquals( "ExifHeight parsing verification", "233 pixels", exifInfo.exifHeight );
        assertEquals( "ExifWidth parsing verification", "350 pixels", exifInfo.exifWidth );
        assertEquals( "FocalLength parsing verification", "82.0 mm", exifInfo.focalLength );
        assertEquals( "ISO parsing verification", "ISO 640", exifInfo.iso );
        assertEquals( "Longitude parsing verification", ZERO, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", ZERO, exifInfo.latLng.getY() );
        assertEquals( "Lens parsing verification", "24-120mm f/3.5-5.6", exifInfo.lens );
        assertEquals( "ShutterSpeed parsing verification", "1/750 sec", exifInfo.shutterSpeed );
        assertEquals( "Rotation parsing verification", 0, exifInfo.rotation );
    }

    /**
     * Test that we get the correct data off a Samsung Galaxy S4 image which
     * doesn't have location info
     */
    public void testExifInfoS4() {
        ExifInfo exifInfo = new ExifInfo( Settings.CLASS_LOADER.getResource( "exif-test-samsung-s4.jpg" ) );
        exifInfo.decodeExifTags();
        assertEquals( "Aperture parsing verification", "f/2.2", exifInfo.aperture );
        assertEquals( "Camera parsing verification", "GT-I9505", exifInfo.camera );
        assertEquals( "CreateDateTime parsing verification", "2013:08:27 23:37:56", exifInfo.getCreateDateTime() );
        assertEquals( "ExifHeight parsing verification", "1836 pixels", exifInfo.exifHeight );
        assertEquals( "ExifWidth parsing verification", "3264 pixels", exifInfo.exifWidth );
        assertEquals( "FocalLength parsing verification", "4.2 mm", exifInfo.focalLength );
        assertEquals( "ISO parsing verification", "50", exifInfo.iso );
        assertEquals( "Longitude parsing verification", ZERO, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", ZERO, exifInfo.latLng.getY() );
        assertEquals( "Lens parsing verification", "", exifInfo.lens );
        assertEquals( "ShutterSpeed parsing verification", "1/1883 sec", exifInfo.shutterSpeed );
        assertEquals( "Rotation parsing verification", 0, exifInfo.rotation );
    }

    /**
     * Test that we get the correct data off a Samsung Galaxy S4 image which
     * doesn't have location info
     */
    public void testExifInfoS4Loc() {
        ExifInfo exifInfo = new ExifInfo( Settings.CLASS_LOADER.getResource( "exif-test-samsung-s4-loc.jpg" ) );
        exifInfo.decodeExifTags();
        assertEquals( "Aperture parsing verification", "f/2.2", exifInfo.aperture );
        assertEquals( "Camera parsing verification", "GT-I9505", exifInfo.camera );
        assertEquals( "CreateDateTime parsing verification", "2014:01:15 12:11:46", exifInfo.getCreateDateTime() );
        assertEquals( "ExifHeight parsing verification", "1836 pixels", exifInfo.exifHeight );
        assertEquals( "ExifWidth parsing verification", "3264 pixels", exifInfo.exifWidth );
        assertEquals( "FocalLength parsing verification", "4.2 mm", exifInfo.focalLength );
        assertEquals( "ISO parsing verification", "50", exifInfo.iso );
        assertEquals( "Longitude parsing verification", 46.80724713888888, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", 9.812583916666668, exifInfo.latLng.getY() );
        assertEquals( "Lens parsing verification", "", exifInfo.lens );
        assertEquals( "ShutterSpeed parsing verification", "1/1879 sec", exifInfo.shutterSpeed );
        assertEquals( "Rotation parsing verification", 0, exifInfo.rotation );
    }

    /**
     * Test that we get the correct rotation data off a Samsung Galaxy S4 image
     * which was not rotated
     */
    public void testExifInfoS4Rot0() {
        ExifInfo exifInfo = new ExifInfo( Settings.CLASS_LOADER.getResource( "exif-test-samsung-s4-roation-0.jpg" ) );
        exifInfo.decodeExifTags();
        assertEquals( "Aperture parsing verification", "", exifInfo.aperture );
        assertEquals( "Camera parsing verification", "GT-I9505", exifInfo.camera );
        assertEquals( "CreateDateTime parsing verification", "2014:04:21 09:30:13", exifInfo.getCreateDateTime() );
        assertEquals( "ExifWidth parsing verification", "2048 pixels", exifInfo.exifWidth );
        assertEquals( "ExifHeight parsing verification", "1152 pixels", exifInfo.exifHeight );
        assertEquals( "FocalLength parsing verification", "", exifInfo.focalLength );
        assertEquals( "ISO parsing verification", "", exifInfo.iso );
        assertEquals( "Longitude parsing verification", ZERO, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", ZERO, exifInfo.latLng.getY() );
        assertEquals( "Lens parsing verification", "", exifInfo.lens );
        assertEquals( "ShutterSpeed parsing verification", "", exifInfo.shutterSpeed );
        assertEquals( "Rotation parsing verification", 0, exifInfo.rotation );
    }

    /**
     * Test that we get the correct rotation data off a Samsung Galaxy S4 image
     * which was rotated left
     */
    public void testExifInfoS4RotLeft() {
        ExifInfo exifInfo = new ExifInfo( Settings.CLASS_LOADER.getResource( "exif-test-samsung-s4-roation-left.jpg" ) );
        exifInfo.decodeExifTags();
        assertEquals( "Aperture parsing verification", "", exifInfo.aperture );
        assertEquals( "Camera parsing verification", "GT-I9505", exifInfo.camera );
        assertEquals( "CreateDateTime parsing verification", "2014:04:21 09:30:22", exifInfo.getCreateDateTime() );
        assertEquals( "ExifWidth parsing verification", "2048 pixels", exifInfo.exifWidth );
        assertEquals( "ExifHeight parsing verification", "1152 pixels", exifInfo.exifHeight );
        assertEquals( "FocalLength parsing verification", "", exifInfo.focalLength );
        assertEquals( "ISO parsing verification", "", exifInfo.iso );
        assertEquals( "Longitude parsing verification", ZERO, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", ZERO, exifInfo.latLng.getY() );
        assertEquals( "Lens parsing verification", "", exifInfo.lens );
        assertEquals( "ShutterSpeed parsing verification", "", exifInfo.shutterSpeed );
        assertEquals( "Rotation parsing verification", 90, exifInfo.rotation );
    }

    /**
     * Test that we get the correct rotation data off a Samsung Galaxy S4 image
     * which was rotated left
     */
    public void testExifInfoS4RotRight() {
        ExifInfo exifInfo = new ExifInfo( Settings.CLASS_LOADER.getResource( "exif-test-samsung-s4-roation-right.jpg" ) );
        exifInfo.decodeExifTags();
        assertEquals( "Aperture parsing verification", "", exifInfo.aperture );
        assertEquals( "Camera parsing verification", "GT-I9505", exifInfo.camera );
        assertEquals( "CreateDateTime parsing verification", "2014:04:21 09:30:39", exifInfo.getCreateDateTime() );
        assertEquals( "ExifWidth parsing verification", "2048 pixels", exifInfo.exifWidth );
        assertEquals( "ExifHeight parsing verification", "1152 pixels", exifInfo.exifHeight );
        assertEquals( "FocalLength parsing verification", "", exifInfo.focalLength );
        assertEquals( "ISO parsing verification", "", exifInfo.iso );
        assertEquals( "Longitude parsing verification", ZERO, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", ZERO, exifInfo.latLng.getY() );
        assertEquals( "Lens parsing verification", "", exifInfo.lens );
        assertEquals( "ShutterSpeed parsing verification", "", exifInfo.shutterSpeed );
        assertEquals( "Rotation parsing verification", 270, exifInfo.rotation );
    }

    /**
     * Test that we get the correct rotation data off a Samsung Galaxy S4 image
     * which was rotated upside down
     */
    public void testExifInfoS4RotUpsideDown() {
        ExifInfo exifInfo = new ExifInfo( Settings.CLASS_LOADER.getResource( "exif-test-samsung-s4-upside-down.jpg" ) );
        exifInfo.decodeExifTags();
        assertEquals( "Aperture parsing verification", "", exifInfo.aperture );
        assertEquals( "Camera parsing verification", "GT-I9505", exifInfo.camera );
        assertEquals( "CreateDateTime parsing verification", "2014:04:21 09:30:29", exifInfo.getCreateDateTime() );
        assertEquals( "ExifWidth parsing verification", "2048 pixels", exifInfo.exifWidth );
        assertEquals( "ExifHeight parsing verification", "1152 pixels", exifInfo.exifHeight );
        assertEquals( "FocalLength parsing verification", "", exifInfo.focalLength );
        assertEquals( "ISO parsing verification", "", exifInfo.iso );
        assertEquals( "Longitude parsing verification", ZERO, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", ZERO, exifInfo.latLng.getY() );
        assertEquals( "Lens parsing verification", "", exifInfo.lens );
        assertEquals( "ShutterSpeed parsing verification", "", exifInfo.shutterSpeed );
        assertEquals( "Rotation parsing verification", 180, exifInfo.rotation );
    }

    /**
     * Tests for images from an Canon Eos 350d
     */
    public void testExifInfoEos350d() {
        ExifInfo exifInfo = new ExifInfo( Settings.CLASS_LOADER.getResource( "exif-test-canon-eos-350d.jpg" ) );
        exifInfo.decodeExifTags();
        //System.out.println( exifInfo.getAllTags() );
        assertEquals( "Aperture parsing verification", "f/20.0", exifInfo.aperture );
        assertEquals( "ShutterSpeed parsing verification", "1/200 sec", exifInfo.shutterSpeed );
        assertEquals( "Camera parsing verification", "Canon EOS 350D DIGITAL", exifInfo.camera );
        assertEquals( "CreateDateTime parsing verification", "2006:10:06 15:13:54", exifInfo.getCreateDateTime() );
        assertEquals( "ExifHeight parsing verification", "1664 pixels", exifInfo.exifHeight );
        assertEquals( "ExifWidth parsing verification", "2496 pixels", exifInfo.exifWidth );
        assertEquals( "FocalLength parsing verification", "18.0 mm", exifInfo.focalLength );
        assertEquals( "ISO parsing verification", "200", exifInfo.iso );
        assertEquals( "Longitude parsing verification", ZERO, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", ZERO, exifInfo.latLng.getY() );
        //TODO exiftool finds the lens but ExifInfo doesn't
        //assertEquals("Lens parsing verification", "18.0 - 55.0 mm (35 mm equivalent: 29.2 - 89.2 mm)", exifInfo.lens);
        assertEquals( "Lens parsing verification", "", exifInfo.lens );
    }

    /**
     * Tests for images from a Canon Eos 60D
     */
    public void testExifInfoCanonEos60D() {
        ExifInfo exifInfo = new ExifInfo( Settings.CLASS_LOADER.getResource( "exif-test-canon-eos-60d.jpg" ) );
        exifInfo.decodeExifTags();
        //System.out.println( exifInfo.getAllTags() );
        assertEquals( "Aperture parsing verification", "f/11.3", exifInfo.aperture );
        assertEquals( "ShutterSpeed parsing verification", "1/511 sec", exifInfo.shutterSpeed );
        assertEquals( "Camera parsing verification", "Canon EOS 60D", exifInfo.camera );
        assertEquals( "CreateDateTime parsing verification", "2013:05:09 13:55:16", exifInfo.getCreateDateTime() );
        assertEquals( "ExifHeight parsing verification", "3456 pixels", exifInfo.exifHeight );
        assertEquals( "ExifWidth parsing verification", "5184 pixels", exifInfo.exifWidth );
        assertEquals( "FocalLength parsing verification", "85.0 mm", exifInfo.focalLength );
        assertEquals( "ISO parsing verification", "400", exifInfo.iso );
        assertEquals( "Longitude parsing verification", ZERO, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", ZERO, exifInfo.latLng.getY() );
        assertEquals( "Lens parsing verification", "EF-S17-85mm f/4-5.6 IS USM", exifInfo.lens );

        assertEquals( "Rotation parsing verification", 0, exifInfo.rotation );
    }

    /**
     * Tests for images from an Canon Cybershot
     */
    public void testExifInfoCybershot1() {
        ExifInfo exifInfo = new ExifInfo( Settings.CLASS_LOADER.getResource( "exif-test-sony-cybershot-1.jpg" ) );
        exifInfo.decodeExifTags();
        //System.out.println( exifInfo.getAllTags() );
        assertEquals( "Aperture parsing verification", "f/4.0", exifInfo.aperture );
        assertEquals( "ShutterSpeed parsing verification", "1/480 sec", exifInfo.shutterSpeed );
        assertEquals( "Camera parsing verification", "CYBERSHOT", exifInfo.camera );
        assertEquals( "CreateDateTime parsing verification", "2002:02:20 16:17:37", exifInfo.getCreateDateTime() );
        assertEquals( "ExifHeight parsing verification", "768 pixels", exifInfo.exifHeight );
        assertEquals( "ExifWidth parsing verification", "1024 pixels", exifInfo.exifWidth );
        assertEquals( "FocalLength parsing verification", "6.1 mm", exifInfo.focalLength );
        assertEquals( "ISO parsing verification", "80", exifInfo.iso );
        assertEquals( "Longitude parsing verification", ZERO, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", ZERO, exifInfo.latLng.getY() );
        assertEquals( "Lens parsing verification", "", exifInfo.lens );
        assertEquals( "Rotation parsing verification", 0, exifInfo.rotation );
    }

    /**
     * Tests for a Sony D700
     */
    public void testExifInfoSonyD700() {
        ExifInfo exifInfo = new ExifInfo( Settings.CLASS_LOADER.getResource( "exif-test-sony-d700.jpg" ) );
        exifInfo.decodeExifTags();
        //System.out.println( exifInfo.getAllTags() );
        assertEquals( "Aperture parsing verification", "f/2.4", exifInfo.aperture );
        assertEquals( "ShutterSpeed parsing verification", "1/32 sec", exifInfo.shutterSpeed );
        assertEquals( "Camera parsing verification", "DSC-D700", exifInfo.camera );
        assertEquals( "CreateDateTime parsing verification", "1998:12:01 14:22:36", exifInfo.getCreateDateTime() );
        assertEquals( "ExifHeight parsing verification", "512 pixels", exifInfo.exifHeight );
        assertEquals( "ExifWidth parsing verification", "672 pixels", exifInfo.exifWidth );
        assertEquals( "FocalLength parsing verification", "", exifInfo.focalLength );
        assertEquals( "ISO parsing verification", "200", exifInfo.iso );
        assertEquals( "Longitude parsing verification", ZERO, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", ZERO, exifInfo.latLng.getY() );
        assertEquals( "Lens parsing verification", "", exifInfo.lens );
        assertEquals( "Rotation parsing verification", 0, exifInfo.rotation );
    }

    /**
     * Tests for a Sony P200
     */
    public void testExifInfoSonyP200() {
        ExifInfo exifInfo = new ExifInfo( Settings.CLASS_LOADER.getResource( "exif-test-sony-P200.jpg" ) );
        exifInfo.decodeExifTags();
        //System.out.println( exifInfo.getAllTags() );
        assertEquals( "Aperture parsing verification", "f/5.6", exifInfo.aperture );
        assertEquals( "ShutterSpeed parsing verification", "1/400 sec", exifInfo.shutterSpeed );
        assertEquals( "Camera parsing verification", "DSC-P200", exifInfo.camera );
        assertEquals( "CreateDateTime parsing verification", "2013:07:04 13:44:46", exifInfo.getCreateDateTime() );
        assertEquals( "ExifHeight parsing verification", "2304 pixels", exifInfo.exifHeight );
        assertEquals( "ExifWidth parsing verification", "3072 pixels", exifInfo.exifWidth );
        assertEquals( "FocalLength parsing verification", "7.9 mm", exifInfo.focalLength );
        assertEquals( "ISO parsing verification", "100", exifInfo.iso );
        assertEquals( "Longitude parsing verification", ZERO, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", ZERO, exifInfo.latLng.getY() );
        assertEquals( "Lens parsing verification", "", exifInfo.lens );
        assertEquals( "Rotation parsing verification", 0, exifInfo.rotation );
    }

}
