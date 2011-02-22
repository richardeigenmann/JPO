package jpo.dataModel;

import java.awt.Dimension;

/*
SizeCalculator.java:  Helper class that calculates scale and new dimensions.

Copyright (C) 2010-2011  Richard Eigenmann, Zurich, Switzerland
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
 * Class to calculate the scale and dimension of a source dimension so that
 * it fits into a target dimension.
 *
 * @author Richard Eigenmann
 */
public class SizeCalculator {

    /**
     * This field holds the scale factor for the transformation from the source
     * dimension to the maximum dimension.
     */
    public double ScaleFactor = 0;

    /**
     * This field holds the new scaled dimension.
     */
    public Dimension scaledSize = null;

    /**
     * Creates an object for the calculation and immediately populates the
     * result fields.
     * @param sourceWidth the width of the original dimension
     * @param sourceHeight the height of the original dimension
     * @param maxWidth the maximum width of the output dimension
     * @param maxHeight the maximum height of the output dimension
     */
    public SizeCalculator ( int sourceWidth, int sourceHeight, int maxWidth, int maxHeight ) {
        // Scale so that the enire picture fits in the component.
        if ( ((double) sourceHeight / maxHeight) > ((double) sourceWidth / maxWidth) ) {
            // Vertical scaling
            ScaleFactor = ((double) maxHeight / sourceHeight);
        } else {
            // Horizontal scaling
            ScaleFactor = ((double) maxWidth / sourceWidth);
        }
        scaledSize = new Dimension( (int) (sourceWidth * ScaleFactor), (int) (sourceHeight * ScaleFactor) );
    }

    public Dimension getScaledSize () {
        return scaledSize;
    }
}
