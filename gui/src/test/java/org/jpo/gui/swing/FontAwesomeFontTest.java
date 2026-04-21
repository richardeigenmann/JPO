package org.jpo.gui.swing;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/*
 Copyright (C) 2026 Richard Eigenmann.
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

class FontAwesomeFontTest {

    @Test
    void getFontAwesomeRegular24() {
        Font font = FontAwesomeFont.getFontAwesomeRegular24();
        assertNotNull(font, "Font should be loaded");
        assertEquals(24f, font.getSize2D(), 0.01, "Font size should be 24");
        assertEquals(Font.PLAIN, font.getStyle(), "Font style should be PLAIN");
        assertTrue(font.getFontName().contains("Font Awesome 5 Free Regular") || font.getFontName().contains("Font Awesome 5 Free"),
                "Font name should contain 'Font Awesome 5 Free Regular' but was: " + font.getFontName());
    }

    @Test
    void getFontAwesomeSolid24() {
        Font font = FontAwesomeFont.getFontAwesomeSolid24();
        assertNotNull(font, "Font should be loaded");
        assertEquals(24f, font.getSize2D(), 0.01, "Font size should be 24");
        assertEquals(Font.PLAIN, font.getStyle(), "Font style should be PLAIN");
        assertTrue(font.getFontName().contains("Font Awesome 5 Free Solid") || font.getFontName().contains("Font Awesome 5 Free"),
                "Font name should contain 'Font Awesome 5 Free Solid' but was: " + font.getFontName());
    }

    @Test
    void getFontAwesomeRegular18() {
        Font font = FontAwesomeFont.getFontAwesomeRegular18();
        assertNotNull(font, "Font should be loaded");
        assertEquals(18f, font.getSize2D(), 0.01, "Font size should be 18");
        assertEquals(Font.PLAIN, font.getStyle(), "Font style should be PLAIN");
        assertTrue(font.getFontName().contains("Font Awesome 5 Free Regular") || font.getFontName().contains("Font Awesome 5 Free"),
                "Font name should contain 'Font Awesome 5 Free Regular' but was: " + font.getFontName());
    }
}