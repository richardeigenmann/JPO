package org.jpo.gui.swing;

import org.jpo.testground.PlayWithFontAwesome;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2020-2020 Richard Eigenmann.
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
 * Static class that loads the Font Awesome Font into a Java Font object.
 *
 * @see <a href="https://stackoverflow.com/questions/24177348/font-awesome-with-swing">https://stackoverflow.com/questions/24177348/font-awesome-with-swing</a>
 */

public class FontAwesomeFont {

    private FontAwesomeFont() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger LOGGER = Logger.getLogger(FontAwesomeFont.class.getName());

    private static Font fontAwesomeRegular24;
    private static Font fontAwesomeRegular18;
    private static Font fontAwesomeSolid24;

    static {
        try (final InputStream regular = PlayWithFontAwesome.class.getResourceAsStream("/Font Awesome 5 Free-Regular-400.otf");
             final InputStream solid = PlayWithFontAwesome.class.getResourceAsStream("/Font Awesome 5 Free-Solid-900.otf")) {
            final Font baseFontRegular = Font.createFont(Font.TRUETYPE_FONT, regular);
            fontAwesomeRegular24 = baseFontRegular.deriveFont(Font.PLAIN, 24f);
            fontAwesomeRegular18 = baseFontRegular.deriveFont(Font.PLAIN, 18f);
            final Font baseFontSolid = Font.createFont(Font.TRUETYPE_FONT, solid);
            fontAwesomeSolid24 = baseFontSolid.deriveFont(Font.PLAIN, 24f);
        } catch (final IOException | FontFormatException e) {
            LOGGER.log(Level.SEVERE, "Could not load FontAwesome font. Exception: {0}", e.getMessage());
        }
    }


    /**
     * Returns the static instance of the 24 size Font Awesome Font Regular-400
     *
     * @return The Java Font object
     */
    public static Font getFontAwesomeRegular24() {
        return fontAwesomeRegular24;
    }

    /**
     * Returns the static instance of the 24 size Font Awesome Font Solid-900
     *
     * @return The Java Font object
     */
    public static Font getFontAwesomeSolid24() {
        return fontAwesomeSolid24;
    }


    /**
     * Returns the static instance of the 18 size Font Awesome Font
     *
     * @return The Java Font object
     */
    public static Font getFontAwesomeRegular18() {
        return fontAwesomeRegular18;
    }

}
