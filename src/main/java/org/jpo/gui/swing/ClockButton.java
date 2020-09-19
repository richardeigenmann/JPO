package org.jpo.gui.swing;

import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2017-2020  Richard Eigenmann.
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
 * An icon of a clock
 *
 * @author Richard Eigenmann
 */
public class ClockButton extends NavBarButton {


    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ClockButton.class.getName());


    /**
     * Icon to indicate that the timer is active
     */
    private static final ImageIcon ICON_CLOCK_ON;

    static {
        final String ICON_CLOCK_ON_FILE = "icon_clock_on.gif";
        URL resource = CollectionJTree.class.getClassLoader().getResource(ICON_CLOCK_ON_FILE);
        if (resource == null) {
            LOGGER.log(Level.SEVERE, "Classloader could not find the file: {0}", ICON_CLOCK_ON_FILE);
            ICON_CLOCK_ON = null;
        } else {
            ICON_CLOCK_ON = new ImageIcon(resource);
        }
    }

    @TestOnly
    ImageIcon getClockIconOn() {
        return ICON_CLOCK_ON;
    }

    /**
     * Icon to indicate that the timer is available
     */
    private static final ImageIcon ICON_CLOCK_OFF;

    static {
        final String ICON_CLOCK_OFF_FILE = "icon_clock_off.gif";
        URL resource = CollectionJTree.class.getClassLoader().getResource(ICON_CLOCK_OFF_FILE);
        if (resource == null) {
            LOGGER.log(Level.SEVERE, "Classloader could not find the file: {0}", ICON_CLOCK_OFF_FILE);
            ICON_CLOCK_OFF = null;
        } else {
            ICON_CLOCK_OFF = new ImageIcon(resource);
        }
    }

    @TestOnly
    ImageIcon getClockIconOff() {
        return ICON_CLOCK_OFF;
    }

    /**
     * Constructs clock icon in the off state.
     *
     */
    ClockButton() {
        super( ICON_CLOCK_OFF );
    }

    /**
     * Switches the clock to busy mode
     */
    public void setClockBusy() {
        setIcon( ICON_CLOCK_ON );
    }

    /**
     * Switches the clock icon to idle mode
     */
    public void setClockIdle() {
        setIcon( ICON_CLOCK_OFF );
    }

}
