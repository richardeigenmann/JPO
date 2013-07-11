package jpo.dataModel;

import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/*
 ExifInfoTest.java: This class interacts with Drew Noake's library and extracts the Exif information

 Copyright (C) 2013  Richard Eigenmann.
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

    public ExifInfoTest( String testName ) {
        super( testName );
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Override
    protected void setUp() throws Exception {
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Override
    protected void tearDown() throws Exception {
    }

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
        final double ZERO = 0;
        assertEquals( "Longitude parsing verification", ZERO, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", ZERO, exifInfo.latLng.getY() );
        assertEquals( "Lens parsing verification", "", exifInfo.lens );
        assertEquals( "ShutterSpeed parsing verification", "", exifInfo.shutterSpeed );
    }

    /**
     * Test that we get the correct data off a Nikon D100 image
     */
    public void testExifInfoD100() {
        ExifInfo exifInfo = new ExifInfo( Settings.cl.getResource( "exif-test-nikon-d100-1.jpg" ) );
        exifInfo.decodeExifTags();
        //System.out.println( exifInfo.getAllTags() );
        assertEquals( "Aperture parsing verification", "F11", exifInfo.aperture );
        assertEquals( "Camera parsing verification", "NIKON D100", exifInfo.camera );
        assertEquals( "CreateDateTime parsing verification", "2008:11:07 16:23:25", exifInfo.getCreateDateTime() );
        assertEquals( "ExifHeight parsing verification", "233 pixels", exifInfo.exifHeight );
        assertEquals( "ExifWidth parsing verification", "350 pixels", exifInfo.exifWidth );
        assertEquals( "FocalLength parsing verification", "82.0 mm", exifInfo.focalLength );
        assertEquals( "ISO parsing verification", "ISO 640", exifInfo.iso );
        final double ZERO = 0;
        assertEquals( "Longitude parsing verification", ZERO, exifInfo.latLng.getX() );
        assertEquals( "Latitude parsing verification", ZERO, exifInfo.latLng.getY() );
        assertEquals( "Lens parsing verification", "24-120mm f/3.5-5.6", exifInfo.lens );
        assertEquals( "ShutterSpeed parsing verification", "1/750 sec", exifInfo.shutterSpeed );

    }
}
