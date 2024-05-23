package org.jpo.gui.swing;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2021-2024 Richard Eigenmann.
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
 * Static class that loads the Roboto Font into a Java Font object.
 * <p>
 * The Roboto font comes from Google and is licenced under the Apache License, Version 2.0.
 *
 * @see <a href="https://fonts.google.com/specimen/Roboto">https://fonts.google.com/specimen/Roboto</a>
 */

public class RobotoFont {

    private RobotoFont() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger LOGGER = Logger.getLogger(RobotoFont.class.getName());

    private static Font fontRobotoThin24;
    private static Font fontRobotoThin18;
    private static Font fontRobotoThin12;
    private static Font fontRobotoThin10;

    static {
        try (final var regular = RobotoFont.class.getResourceAsStream("/Roboto-Thin.ttf")) {
            final var baseFontRegular = Font.createFont(Font.TRUETYPE_FONT, regular);
            fontRobotoThin24 = baseFontRegular.deriveFont(Font.PLAIN, 24f);
            fontRobotoThin18 = baseFontRegular.deriveFont(Font.PLAIN, 18f);
            fontRobotoThin12 = baseFontRegular.deriveFont(Font.PLAIN, 12f);
            fontRobotoThin10 = baseFontRegular.deriveFont(Font.PLAIN, 10f);
        } catch (final IOException | FontFormatException e) {
            LOGGER.log(Level.SEVERE, "Could not load FontAwesome font. Exception: {0}", e.getMessage());
        }
    }


    /**
     * Returns the static instance of the 24 size Font Roboto-Thin
     *
     * @return The Java Font object
     */
    public static Font getFontRobotoThin24() {
        return fontRobotoThin24;
    }


    /**
     * Returns the static instance of the 18 size Font Roboto-Thin
     *
     * @return The Java Font object
     */
    public static Font getFontRobotoThin18() {
        return fontRobotoThin18;
    }

    /**
     * Returns the static instance of the 18 size Font Roboto-Thin
     *
     * @return The Java Font object
     */
    public static Font getFontRobotoThin12() {
        return fontRobotoThin12;
    }

    /**
     * Returns the static instance of the 18 size Font Roboto-Thin
     *
     * @return The Java Font object
     */
    public static Font getFontRobotoThin10() {
        return fontRobotoThin10;
    }

}
