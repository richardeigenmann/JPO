/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpo.gui.swing;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 *
 * @author richi
 */
public interface PictureControllerImage {

    /**
     * return the current scale factor
     *
     * @return the scale factor
     */
    public double getScaleFactor();

    /**
     * return the width of the original image or Zero if there is none
     *
     * @return the original width of the image
     */
    public int getOriginalWidth();

    /**
     * return the height of the original image or Zero if there is none
     *
     * @return the original height of the image
     */
    public int getOriginalHeight();

    /**
     * set the scale factor to the new desired value. The scale factor is a
     * multiplier by which the original picture needs to be multiplied to get
     * the size of the picture on the screen. You must call
     * {@link #createScaledPictureInThread(int)} to make anything happen.<p>
     *
     * Example: Original is 3000 x 2000 --> Scale Factor 0.10 --> Target Picture
     * is 300 x 200
     *
     * @param newFactor
     */
    public void setScaleFactor( double newFactor );

    /**
     * method that creates the scaled image in the background in it's own
     * thread. When done something needs to ask the PictureController to repaint.
     *
     * @param priority The priority this image takes relative to the others.
     */
    public void createScaledPictureInThread( int priority );

    /**
     * invoke this method to tell the scale process to figure out the scale
     * factor so that the image fits either by height or by width into the
     * indicated dimension.
     *
     * @param newSize
     */
    public void setScaleSize( Dimension newSize );

    /**
     * return the scaled image
     *
     * @return the scaled image
     */
    public BufferedImage getScaledPicture();

}
