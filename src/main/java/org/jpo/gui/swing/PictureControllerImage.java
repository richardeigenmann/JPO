package org.jpo.gui.swing;

import java.awt.*;
import java.awt.image.BufferedImage;

/*
 PictureControllerImage.java:  The interface to define the capabilities a Picture controller requires from the provider of the BufferedImage

 Copyright (C) 2002 - 2014  Richard Eigenmann.
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
 *The interface to define the capabilities a Picture Controller requires from the provider of the BufferedImage
 * @author Richard Eigenmann
 */
public interface PictureControllerImage {

    /**
     * The implementing image provider needs to return the scale factor to which the image is scaled
     *
     * @return the scale factor
     */
    double getScaleFactor();

    /**
     * return the width of the original image or Zero if there is none
     *
     * @return the original width of the image
     */
    int getOriginalWidth();

    /**
     * return the height of the original image or Zero if there is none
     *
     * @return the original height of the image
     */
    int getOriginalHeight();

    /**
     * Set the scale factor to the new desired value. The scale factor is a
     * multiplier by which the original picture needs to be multiplied to get
     * the size of the picture on the screen.
     * 
     * The image should only be scaled when 
     * {@link #createScaledPictureInThread(int)} is called.<p>
     *
     * Example: Original is 3000 x 2000 --&gt; Scale Factor 0.10 --&gt; Target Picture
     * is 300 x 200
     *
     * @param newFactor new facture
     */
    void setScaleFactor(double newFactor);

    /**
     * The expectation is that when this method is called on the implementing class that
     * it will go off and scale the image. The implementing classes need to ensure that
     * the PictureController is requested to repaint itself when the image is ready.
     *
     * @param priority The Thread priority
     */
    void createScaledPictureInThread(int priority);

    /**
     * invoke this method to tell the scale process to figure out the scale
     * factor so that the image fits either by height or by width into the
     * indicated dimension.
     *
     * @param newSize new size
     */
    void setScaleSize(Dimension newSize);

    /**
     * Must return the scaled image for drawing.
     *
     * @return the scaled image
     */
    BufferedImage getScaledPicture();

}
