package jpo.gui.swing;

import javax.swing.ImageIcon;

/*
 Copyright (C) 2017  Richard Eigenmann.
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

    private static final ClassLoader CLASS_LOADER = LeftRightButton.class.getClassLoader();

    /**
     * Icon to indicate that the timer is active
     */
    private static final ImageIcon ICON_CLOCK_ON = new ImageIcon( CLASS_LOADER.getResource( "icon_clock_on.gif" ) );

    /**
     * Icon to indicate that the timer is available
     */
    private static final ImageIcon ICON_CLOCK_OFF = new ImageIcon( CLASS_LOADER.getResource( "icon_clock_off.gif" ) );

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
