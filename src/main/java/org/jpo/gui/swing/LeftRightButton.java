package org.jpo.gui.swing;

import javax.swing.ImageIcon;
import java.util.Objects;
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
 *
 * @author richi
 */
public class LeftRightButton extends NavBarButton {

    /**
     * The state of the button
     */
    public enum BUTTON_STATE {

        /**
         * Indicates that we are at the beginning of a set
         */
        BEGINNING,
        /**
         * Indicates that we are at a set boundary but can go back
         */
        HAS_LEFT,
        /**
         * Indicates that we can go back
         */
        HAS_PREVIOUS,
        /**
         * Indicates that we can go forward
         */
        HAS_RIGHT,
        /**
         * Indicates that we are at a boundary but can go forward
         */
        HAS_NEXT,
        /**
         * Indicates that we are at the end of set
         */
        END
    }

    private static final ClassLoader CLASS_LOADER = LeftRightButton.class.getClassLoader();

    /**
     * Icon pointing left
     */
    private static final ImageIcon ICON_ARROW_LEFT = new ImageIcon(Objects.requireNonNull(CLASS_LOADER.getResource("icon_previous.gif")));
    /**
     * Double left pointing icon
     */
    private static final ImageIcon ICON_DOUBLE_ARROW_LEFT = new ImageIcon(Objects.requireNonNull(CLASS_LOADER.getResource("icon_prevprev.gif")));
    /**
     * Icon pointing left with a bar to indicate you can't go left
     */
    private static final ImageIcon ICON_ARROW_LEFT_STOP = new ImageIcon(Objects.requireNonNull(CLASS_LOADER.getResource("icon_noprev.gif")));

    /**
     * Icon pointing right
     */
    private static final ImageIcon ICON_ARROW_RIGHT = new ImageIcon(Objects.requireNonNull(CLASS_LOADER.getResource("icon_next.gif")));
    /**
     * Double right pointing icon
     */
    private static final ImageIcon ICON_DOUBLE_ARROW_RIGHT = new ImageIcon(Objects.requireNonNull(CLASS_LOADER.getResource("icon_nextnext.gif")));
    /**
     * Icon pointing right at a bar to indicate you can't go right
     */
    private static final ImageIcon ICON_ARROW_RIGHT_STOP = new ImageIcon(Objects.requireNonNull(CLASS_LOADER.getResource("icon_nonext.gif")));

    /**
     * Constructs the left button
     */
    public LeftRightButton() {
        super( ICON_ARROW_LEFT );
    }

    /**
     * Sets the appropriate icon for the state
     *
     * @param state the state from the enum to set
     */
    public void setDecoration( BUTTON_STATE state ) {
        switch ( state ) {
            case BEGINNING:
                setIcon( ICON_ARROW_LEFT_STOP );
                break;
            case HAS_PREVIOUS:
                setIcon( ICON_DOUBLE_ARROW_LEFT );
                break;
            case HAS_LEFT:
                setIcon( ICON_ARROW_LEFT );
                break;
            case HAS_RIGHT:
                setIcon( ICON_ARROW_RIGHT );
                break;
            case HAS_NEXT:
                setIcon( ICON_DOUBLE_ARROW_RIGHT );
                break;
            default: // END:
                setIcon( ICON_ARROW_RIGHT_STOP );
                break;
        }
    }

}
