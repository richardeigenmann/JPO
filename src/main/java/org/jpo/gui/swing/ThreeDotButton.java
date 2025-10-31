package org.jpo.gui.swing;

import org.jpo.gui.Settings;
import org.jpo.gui.JpoResources;

import javax.swing.*;

/*
 Copyright (C) 2010-2025 Richard Eigenmann, Zurich, Switzerland
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY,
 without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 * This class overrides a JButton and sets the size and text.
 *
 * @author Richard Eigenmann
 */
public class ThreeDotButton extends JButton {

    /**
     * Creates a three dot button
     */
    public ThreeDotButton() {
        super();
        setText(JpoResources.getResource("threeDotText"));
        setPreferredSize(Settings.THREE_DOT_BUTTON_DIMENSION);
        setMinimumSize(Settings.THREE_DOT_BUTTON_DIMENSION);
        setMaximumSize(Settings.THREE_DOT_BUTTON_DIMENSION);
    }

}
