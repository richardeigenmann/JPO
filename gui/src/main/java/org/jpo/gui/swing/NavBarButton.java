package org.jpo.gui.swing;

import org.jpo.gui.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/*
Copyright (C) 2020-2024 Richard Eigenmann, Zürich, Switzerland
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
 * Extends the default JButton with no border, standard background color,
 * standard dimensions of 24 pixels and tooltip at 0, -20 Uses the
 * Settings.PICTUREVIEWER_BACKGROUND_COLOR for the background.
 */
public class NavBarButton extends JButton {

    /**
     * Constructs the NavBarButton
     *
     * @param icon The icon to show
     */
    public NavBarButton(final Icon icon) {
        super(icon);
        setBorderPainted(false);
        setBackground(Settings.getPictureviewerBackgroundColor());
        final Dimension navButtonSize = new Dimension(24, 24);
        setMinimumSize(navButtonSize);
        setPreferredSize(navButtonSize);
        setMaximumSize(navButtonSize);
    }

    /**
     * Overriding the position of the tooltip so that it comes 20 pixels above
     * the mouse pointer
     *
     * @return the point
     */
    @Override
    public Point getToolTipLocation(MouseEvent event) {
        return new Point(0, -20);
    }
}
