package org.jpo.gui.swing;

import org.jpo.datamodel.Settings;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.PictureControllerZoomRequest;
import org.jpo.eventbus.Zoom;
import org.jpo.datamodel.ScalablePicture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;


/*
 Copyright (C) 2002-2024 Richard Eigenmann.
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
 * The job of this Component is to scale and display a picture.
 * It notifies the registered parent about status changes so that a description
 * object can be updated. When the image has been rendered and displayed the
 * legend of the image is set to the parent with the ready status.<p>
 * The image is centred on the component to the {@link #focusPoint} in the
 * coordinate space of the image. This translated using the
 * {@link ScalablePicture#setScaleFactor( double )} to the coordinate space of
 * the JComponent
 * <p> <img src=Mathematics.png alt="Mathematics"></p>
 *
 *
 */
public class PictureController extends JComponent {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(PictureController.class.getName());

    /**
     * returns whether the picture should be centered when scaled
     *
     * @return true if the picture should be centered when scaled
     */
    public boolean isCenterWhenScaled() {
        return centerWhenScaled;
    }

    /**
     * Remembers whether the picture should be centered when scaled
     *
     * @param centerWhenScaled whether the picture should be centered when scaled
     */
    public void setCenterWhenScaled(final boolean centerWhenScaled) {
        this.centerWhenScaled = centerWhenScaled;
    }

    /**
     * Flag that lets this JComponent know if the picture is to be fitted into
     * the available space when the threads return the scaled picture.
     */
    private boolean centerWhenScaled;

    /**
     * This point of the picture will be put at the middle of the screen
     * component. The coordinates are in x,y in the coordinate space of the
     * picture.
     */
    public final Point focusPoint = new Point();

    private final transient PictureControllerImage pictureControllerImage;

    /**
     * Constructs a PicturePane components.
     * @param pictureControllerImage image
     */
    public PictureController( final PictureControllerImage pictureControllerImage ) {
        this.pictureControllerImage = pictureControllerImage;
        initComponents();

        // make graphics faster
        this.setDoubleBuffered(false);

        final var mouseListener = new PictureControllerMouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                int k = keyEvent.getKeyCode();
                if (handleKeystroke(k)) {
                    keyEvent.consume();
                }
            }
        } );

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (centerWhenScaled) {
                    resetPicture();
                }
            }

        });
    }

    /**
     * Handles the keystroke and returns if it was able to find a handler for the keystroke
     *
     * @param keyCode The keyCode
     * @return true if there was some action to do on the keyCode, false if not
     */
    private boolean handleKeystroke(int keyCode) {
        if ((keyCode == KeyEvent.VK_SPACE) || (keyCode == KeyEvent.VK_HOME)) {
            resetPicture();
            return true;
        } else if ((keyCode == KeyEvent.VK_PAGE_UP)) {
            JpoEventBus.getInstance().post(new PictureControllerZoomRequest(this, Zoom.IN));
            return true;
        } else if ((keyCode == KeyEvent.VK_PAGE_DOWN)) {
            JpoEventBus.getInstance().post(new PictureControllerZoomRequest(this, Zoom.OUT));
            return true;
        } else if ((keyCode == KeyEvent.VK_1)) {
            JpoEventBus.getInstance().post(new PictureControllerZoomRequest(this, Zoom.NO_SCALE));
            return true;
        } else if ((keyCode == KeyEvent.VK_UP) || (keyCode == KeyEvent.VK_KP_UP)) {
            scrollDown();
            return true;
        } else if ((keyCode == KeyEvent.VK_DOWN) || (keyCode == KeyEvent.VK_KP_DOWN)) {
            scrollUp();
            return true;
        } else if ((keyCode == KeyEvent.VK_LEFT) || (keyCode == KeyEvent.VK_KP_LEFT)) {
            scrollRight();
            return true;
        } else if ((keyCode == KeyEvent.VK_RIGHT) || (keyCode == KeyEvent.VK_KP_RIGHT)) {
            scrollLeft();
            return true;
        }
        return false;
    }

    /**
     * Initialises the widgets
     */
    private void initComponents() {
        setFocusable(true);
    }

    /**
     * Sets the scale of the picture to the current screen size and centres it
     * there.
     */
    public void resetPicture() {
        JpoEventBus.getInstance().post(new PictureControllerZoomRequest(this, Zoom.FIT));
        centerImage();
        requestFocusInWindow();
        centerWhenScaled = true;
    }


    /**
     * Deals with a Zoom request by changing the doom factor
     *
     * @param request The request
     */
    public void handleZoomRequest(final PictureControllerZoomRequest request) {
        switch (request.zoom()) {
            case IN -> zoomIn();
            case OUT -> zoomOut();
            case FIT -> zoomToFit();
            case NO_SCALE -> zoomFull();
        }
    }


    /**
     * Multiplies the scale factor so that paint() method scales the image
     * larger. This method calls
     * {@link ScalablePicture#createScaledPictureInThread(int)} which in turn
     * will tell this object by means of the status update that the image is
     * ready and should be repainted.
     */
    public void zoomIn() {
        final double oldScaleFactor = pictureControllerImage.getScaleFactor();
        double newScaleFactor = oldScaleFactor * 1.5;

        // If scaling goes from scale down to scale up, set ScaleFactor to exactly 1
        if ( ( oldScaleFactor < 1 ) && ( newScaleFactor > 1 ) ) {
            newScaleFactor = 1;
        }

        // Check if the picture would get to large and cause the system to "hang"
        if ((pictureControllerImage.getOriginalWidth() * pictureControllerImage.getScaleFactor() < Settings.getMaximumPictureSize()) && (pictureControllerImage.getOriginalHeight() * pictureControllerImage.getScaleFactor() < Settings.getMaximumPictureSize())) {
            pictureControllerImage.setScaleFactor(newScaleFactor);
            pictureControllerImage.createScaledPictureInThread(Thread.MAX_PRIORITY);
        }
    }

    /**
     * method that zooms out on the image leaving the centre where it is. This
     * method calls {@link ScalablePicture#createScaledPictureInThread(int)}
     * which in turn will tell this object by means of the status update that
     * the image is ready and should be repainted.
     */
    public void zoomOut() {
        pictureControllerImage.setScaleFactor( pictureControllerImage.getScaleFactor() / 1.5 );
        pictureControllerImage.createScaledPictureInThread( Thread.MAX_PRIORITY );
    }

    /**
     * this method sets the desired scaled size of the ScalablePicture to the
     * size of the JPanel and fires off a createScaledPictureInThread request if
     * the ScalablePicture has been loaded or is ready.
     *
     * @see ScalablePicture#createScaledPictureInThread(int)
     *
     */
    public void zoomToFit() {
        pictureControllerImage.setScaleSize( getSize() );
        pictureControllerImage.createScaledPictureInThread( Thread.MAX_PRIORITY );
    }

    /**
     * method that zooms the image to 100%. This method calls
     * {@link ScalablePicture#createScaledPictureInThread(int)} which in turn
     * will tell this object by means of the status update that the image is
     * ready and should be repainted.
     */
    public void zoomFull() {
        pictureControllerImage.setScaleFactor( 1 );
        pictureControllerImage.createScaledPictureInThread( Thread.MAX_PRIORITY );
    }

    ///////////////////////////////////////////////////////////////
    // Scrolling Methods                                         //
    ///////////////////////////////////////////////////////////////
    /**
     * Set image to centre of panel by putting the coordinates of the middle of
     * the original image into the Centre to X and Centre to Y variables by
     * invoking the setCenterLocation method. This method calls
     * <code>repaint()</code> directly since no time-consuming image operations
     * need to take place.
     */
    public void centerImage() {
        final int originalHeight = pictureControllerImage.getOriginalHeight();
        if ( originalHeight != 0 ) {
            setCenterLocation( pictureControllerImage.getOriginalWidth() / 2, originalHeight / 2 );
            repaint();
        }
    }

    /**
     * This is the factor by how much the scrollxxx methods will scroll.
     * Currently set to a fixed 5%.
     */
    private static final float SCROLL_FACTOR = 0.05f;

    /**
     * method that moves the image up by 10% of the pixels shown on the screen.
     * This method calls <code>repaint()</code> directly since no time-consuming
     * image operations need to take place.
     * <p><img src=scrollUp.png alt="Scroll Up"></p>
     * 
     * @see #scrollUp()
     * @see #scrollDown()
     * @see #scrollLeft()
     * @see #scrollRight()
     *
     */
    public void scrollUp() {
        // if the bottom edge of the picture is visible, do not scroll
        if ( ( ( pictureControllerImage.getOriginalHeight() - focusPoint.y ) * pictureControllerImage.getScaleFactor() ) + getSize().height / (double) 2 > getSize().height ) {
            focusPoint.y += (int) ( getSize().height * SCROLL_FACTOR / pictureControllerImage.getScaleFactor() );
            repaint();
        } else {
            LOGGER.warning( "scrollUp rejected because bottom of picture is already showing." );
        }
    }

    /**
     * method that moves the image down by 10% of the pixels shown on the
     * screen. This method calls <code>repaint()</code> directly since no
     * time-consuming image operations need to take place.
     * <p><img src=scrollDown.png alt="Scroll Down"></p>
     * @see #scrollUp()
     * @see #scrollDown()
     * @see #scrollLeft()
     * @see #scrollRight()
     */
    public void scrollDown() {
        if ( getSize().height / (double) 2 - focusPoint.y * pictureControllerImage.getScaleFactor() < 0 ) {
            focusPoint.y -= (int) ( getSize().height * SCROLL_FACTOR / pictureControllerImage.getScaleFactor() );
            repaint();
        } else {
            LOGGER.warning( "PicturePane.scrollDown rejected because top edge is aready visible" );
        }
    }

    /**
     * method that moves the image left by 10% of the pixels shown on the
     * screen. This method calls <code>repaint()</code> directly since no
     * time-consuming image operations need to take place. works just like
     * {@link #scrollUp()}.
     *
     * @see #scrollUp()
     * @see #scrollDown()
     * @see #scrollLeft()
     * @see #scrollRight()
     */
    public void scrollLeft() {
        // if the bottom edge of the picture is visible, do not scroll
        if ( ( ( pictureControllerImage.getOriginalWidth() - focusPoint.x ) * pictureControllerImage.getScaleFactor() ) + getSize().width / (double) 2 > getSize().width ) {
            focusPoint.x += (int) ( getSize().width * SCROLL_FACTOR / pictureControllerImage.getScaleFactor() );
            repaint();
        } else {
            LOGGER.warning( "scrollLeft rejected because right edge of picture is already showing." );
        }
    }

    /**
     * method that moves the image right by 10% of the pixels shown on the
     * screen. This method calls <code>repaint()</code> directly since no
     * time-consuming image operations need to take place. works just like
     * {@link #scrollDown()}.
     *
     * @see #scrollUp()
     * @see #scrollDown()
     * @see #scrollLeft()
     * @see #scrollRight()
     */
    public void scrollRight() {
        if ( getSize().width / (double) 2 - focusPoint.x * pictureControllerImage.getScaleFactor() < 0 ) {
            focusPoint.x -= (int) ( getSize().width * SCROLL_FACTOR / pictureControllerImage.getScaleFactor() );
            repaint();
        } else {
            LOGGER.warning( "scrollRight rejected because left edge is aready visible" );
        }
    }

    /**
     * method to set the centre of the image to the true coordinates in the
     * picture but doesn't call <code>repaint()</code>
     *
     * @param xParameter the x coordinates
     * @param yParameter the y coordinates
     */
    public void setCenterLocation(int xParameter, int yParameter) {
        focusPoint.setLocation(xParameter, yParameter);
    }

    /**
     * we are overriding the default paintComponent method, grabbing the
     * Graphics handle and doing our own drawing here. Essentially this method
     * draws a large background color rectangle. A drawRenderedImage is then
     * painted doing an affine transformation on the scaled image to position it
     * so the desired point is in the middle of the Graphics object. The
     * picture is not scaled here because this is a slow operation and only
     * needs to be done once, while moving the image is something the user is
     * likely to do more often.
     *
     * @param g Graphics
     */
    @Override
    public void paintComponent( Graphics g ) {
        final int windowWidth = getSize().width;
        final int windowHeight = getSize().height;

        if ( pictureControllerImage.getScaledPicture() != null ) {
            Graphics2D g2d = (Graphics2D) g;

            final int xOffset = (int) ((windowWidth / 2.0) - (focusPoint.x * pictureControllerImage.getScaleFactor()));
            final int yOffset = (int) ((windowHeight / 2.0) - (focusPoint.y * pictureControllerImage.getScaleFactor()));

            // clear damaged component area
            final Rectangle clipBounds = g2d.getClipBounds();
            g2d.setColor(getBackground());
            g2d.fillRect(clipBounds.x,
                    clipBounds.y,
                    clipBounds.width,
                    clipBounds.height);

            g2d.drawRenderedImage(pictureControllerImage.getScaledPicture(), AffineTransform.getTranslateInstance(xOffset, yOffset));

        } else {
            // paint a black square
            g.setClip( 0, 0, windowWidth, windowHeight );
            g.setColor( getBackground() );
            g.fillRect( 0, 0, windowWidth, windowHeight );
        }
    }

    /**
     * This class deals with the mouse events. Is built so that the picture can
     * be dragged if the mouse button is pressed and the mouse moved. If the
     * left button is clicked the picture is zoomed in, middle resets to full
     * screen, right zooms out.
     */
    class PictureControllerMouseListener extends MouseAdapter {

        /**
         * used in dragging to find out how much the mouse has moved from the
         * last time
         */
        private int lastX;
        private int lastY;

        /**
         * This method traps the mouse events and changes the scale and position
         * of the displayed picture.
         */
        @Override
        public void mouseClicked(final MouseEvent e) {
            if (e.getButton() == 3) {
                // Right Mousebutton zooms out
                centerWhenScaled = false;
                JpoEventBus.getInstance().post(new PictureControllerZoomRequest(PictureController.this, Zoom.OUT));
            } else if ( e.getButton() == 2 ) {
                // Middle Mousebutton resets
                JpoEventBus.getInstance().post(new PictureControllerZoomRequest(PictureController.this, Zoom.FIT));
                centerWhenScaled = true;
            } else if ( e.getButton() == 1 ) {
                // Left Mousebutton zooms in on selected spot
                // Convert screen coordinates of the mouse click into true
                // coordinates on the picture:

                final int windowWidth = getSize().width;
                final int windowHeight = getSize().height;

                final int xOffset = e.getX() - (windowWidth / 2);
                final int yOffset = e.getY() - (windowHeight / 2);

                setCenterLocation(
                        focusPoint.x + (int) (xOffset / pictureControllerImage.getScaleFactor()),
                        focusPoint.y + (int) (yOffset / pictureControllerImage.getScaleFactor()));
                centerWhenScaled = false;
                JpoEventBus.getInstance().post(new PictureControllerZoomRequest(PictureController.this, Zoom.IN));
            }
        }

        /**
         * method that is invoked when the user drags the mouse with a button
         * pressed. Moves the picture around
         */
        @Override
        public void mouseDragged( final MouseEvent e ) {
            if ( !dragging ) {
                // Switch into dragging mode and record current coordinates
                LOGGER.fine("PicturePane.mouseDragged: Switching to drag mode.");
                lastX = e.getX();
                lastY = e.getY();

                setDragging(true);

            } else {
                // was already dragging
                int x = e.getX();
                int y = e.getY();

                focusPoint.setLocation((int) (focusPoint.x + ((lastX - x) / pictureControllerImage.getScaleFactor())),
                        (int) (focusPoint.y + ((lastY - y) / pictureControllerImage.getScaleFactor())));
                lastX = x;
                lastY = y;

                setDragging(true);
                repaint();
            }
            centerWhenScaled = false;
        }

        /**
         * Flag that lets the object know if the mouse is in dragging mode.
         */
        private boolean dragging;  // Java sets default to false

        /**
         * Sets the cursor to a move cursor if dragging is on
         *
         * @param dragging true if dragging, false if not dragging
         */
        public void setDragging( final boolean dragging ) {
            this.dragging = dragging;
            if ( !this.dragging ) {
                setDefaultCursor();
            } else {
                setMoveCursor();
            }
        }

        /**
         * Makes the cursor in the picture panel a default cursor
         */
        private void setDefaultCursor() {
            setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
        }

        /**
         * Makes the cursor in the picture panel a move cursor
         */
        private void setMoveCursor() {
            setCursor( new Cursor( Cursor.MOVE_CURSOR ) );
        }

        /**
         * When mouse is releases we switch off the dragging mode
         */
        @Override
        public void mouseReleased(final MouseEvent e) {
            if (dragging) {
                setDragging(false);
            }
        }

        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            JpoEventBus.getInstance().post(
                    new PictureControllerZoomRequest(PictureController.this,
                            e.getWheelRotation() < 0 ? Zoom.IN : Zoom.OUT));
        }
    }

}
