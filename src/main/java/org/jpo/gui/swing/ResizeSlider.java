package org.jpo.gui.swing;

import javax.swing.*;
import java.awt.*;

/*
Copyright (C) 2021  Richard Eigenmann, Zürich, Switzerland
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
 * A slider to scale the thumbnails
 */
public class ResizeSlider extends JSlider {

    /**
     * The largest size for the thumbnail slider
     */
    private static final int THUMBNAILSIZE_SLIDER_MIN = 5;

    /**
     * The smallest size for the thumbnail slider
     */
    public static final int THUMBNAILSIZE_SLIDER_MAX = 20;

    /**
     * The starting position for the thumbnail slider
     */
    private static final int THUMBNAILSIZE_SLIDER_INIT = 20;

    /**
     * Returns a styled JSlider
     */
    public ResizeSlider() {
        super(SwingConstants.HORIZONTAL,
                THUMBNAILSIZE_SLIDER_MIN, THUMBNAILSIZE_SLIDER_MAX, THUMBNAILSIZE_SLIDER_INIT);
        setSnapToTicks(false);
        setMaximumSize(new Dimension(150, 40));
        setMajorTickSpacing(4);
        setMinorTickSpacing(2);
        setPaintTicks(true);
        setPaintLabels(false);
    }
}
