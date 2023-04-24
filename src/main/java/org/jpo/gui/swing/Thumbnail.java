package org.jpo.gui.swing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2010-2023 Richard Eigenmann, Zurich, Switzerland
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
 * This class extends a JComponent showing an ImageIcon. The ImageIcon can be
 * scaled down with the {@link #setFactor} method.
 */
public class Thumbnail extends JComponent {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(Thumbnail.class.getName());

    /**
     * Constructor to be called on the EDT.
     */
    public Thumbnail() {
        Tools.checkEDT();
        initComponents();
    }

    /**
     * Initialises the Component
     */
    private void initComponents() {
        setVisible(false);
        setOpaque(false);
        setBackground(Settings.getUnselectedColor());
        setFont(RobotoFont.getFontRobotoThin12());
    }

    /**
     * sets the image the Thumbnail. If we are not on the EDT this is submitted to the EDT.
     *
     * @param icon The ImageIcon that should be displayed
     */
    public void setImageIcon(@NotNull final ImageIcon icon) {
        LOGGER.log(Level.FINE, "Setting image on thumbnail hashCode: {0}", hashCode());
        final Runnable runnable = () -> {
            img = icon.getImage();
            if (img == null) {
                return;
            }
            imgOb = icon.getImageObserver();
            thumbnailHeight = img.getHeight(imgOb);

            final BufferedImage source = new BufferedImage(img.getWidth(imgOb), thumbnailHeight, BufferedImage.TYPE_INT_BGR);
            source.createGraphics().drawImage(img, 0, 0, null);

            // force update of layout
            setVisible(false);
            setVisible(true);
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    /**
     * Overridden method to allow the setting of the size when not visible. This
     * was a bit problematic as the Component which is showing the Thumbnails
     * was not adjusting to the new image size. The revalidate() cured this.
     *
     * @param visibility true for visible, false for non visible.
     */
    @Override
    public void setVisible(final boolean visibility) {
        Tools.checkEDT();
        super.setVisible(visibility);
        if (visibility && getSize().height != thumbnailHeight) {
            // finally I found the solution to the size issue! Unless it's set to
            // non-visible the whole rendering engine sees no point in fixing the size.
            Thumbnail.super.setVisible(false);
            Thumbnail.super.setVisible(true);
        }
    }

    /**
     * The factor which is multiplied with the ThumbnailController to determine
     * how large it is shown.
     */
    private float thumbnailScaleFactor = 1;

    /**
     * This method sets the scaling factor for the display of a thumbnail. 0..1
     *
     * @param thumbnailSizeFactor factor
     */
    public void setFactor(final float thumbnailSizeFactor) {
        LOGGER.log(Level.FINE, "Scaling factor is being set to {0}", thumbnailSizeFactor);
        this.thumbnailScaleFactor = thumbnailSizeFactor;
        setVisible(isVisible());
    }

    /**
     * the maximum size for the thumbnail in unscaled pixels
     */
    private int thumbnailSize;

    /**
     * Sets the maximum Thumbnail size
     *
     * @param thumbnailSize the maximum thumbnail size
     */
    public void setThumbnailSize(int thumbnailSize) {
        this.thumbnailSize = thumbnailSize;
    }

    /**
     * Returns the maximum thumbnail size
     *
     * @return the thumbnail size
     */
    public int getThumbnailSize() {
        return this.thumbnailSize;
    }

    /**
     * Returns the maximum thumbnail dimension
     *
     * @return the thumbnail dimension
     */
    public Dimension getThumbnailDimension() {
        return new Dimension(thumbnailSize, thumbnailSize);
    }

    /**
     * Returns the preferred size for the scaled Thumbnail as a Dimension using the
     * thumbnailSize as width and height.
     *
     * @return Returns the preferred size for the scaled Thumbnail as a Dimension using
     * the thumbnailSize as width and height.
     */
    @Override
    public Dimension getPreferredSize() {
        var height = 0;
        if (isVisible()) {
            height = (int) (thumbnailHeight * thumbnailScaleFactor);
        }
        return new Dimension((int) (thumbnailSize * thumbnailScaleFactor), height);
    }

    /**
     * Returns the maximum (scaled) size for the Thumbnail as a Dimension using
     * the thumbnailSize as width and height.
     *
     * @return maximum size for the Thumbnail
     */
    @Override
    public Dimension getMaximumSize() {
        return new Dimension((int) (thumbnailSize * thumbnailScaleFactor), (int) (thumbnailSize * thumbnailScaleFactor));
    }

    /**
     * I've put in this variable because I had real trouble with the
     * getPreferredSize method not being able to access the ImageObserver to
     * query the height of the thumbnail.
     */
    private int thumbnailHeight;  // default is 0

    /**
     * The image that should be displayed
     */
    private transient Image img;

    /**
     * The Image Observer of the image that should be displayed
     */
    private transient ImageObserver imgOb;

    private static ImageIcon getResource(final String resource) {
        final var resourceURL = Thumbnail.class.getClassLoader().getResource(resource);
        if (resourceURL == null) {
            LOGGER.log(Level.SEVERE, "Classloader failed to load file: {0}", resource);
            return null;
        } else {
            return new ImageIcon(resourceURL);
        }
    }

    /**
     * This icon indicates that the thumbnail creation is sitting on the queue.
     */
    private static final ImageIcon QUEUE_ICON = getResource("queued_thumbnail.gif");

    @TestOnly
    ImageIcon getQueueIcon() {
        return QUEUE_ICON;
    }

    /**
     * This icon shows a large yellow folder.
     */
    private static final ImageIcon LARGE_FOLDER_ICON = getResource("icon_folder_large.jpg");

    @TestOnly
    ImageIcon getLargeFolderIcon() {
        return LARGE_FOLDER_ICON;
    }

    /**
     * The icon to superimpose on the picture if the highres picture is not
     * available
     */
    private static final ImageIcon OFFLINE_ICON = getResource("icon_offline.gif");

    @TestOnly
    ImageIcon getOfflineIcon() {
        return OFFLINE_ICON;
    }

    /**
     * The mail icon to superimpose on the picture
     */
    private static final ImageIcon MAIL_ICON = getResource("icon_mail.gif");

    @TestOnly
    ImageIcon getMailIcon() {
        return MAIL_ICON;
    }

    /**
     * The mail icon to superimpose on the picture
     */
    private static final ImageIcon SELECTED_ICON = getResource("icon_selected.gif");

    @TestOnly
    ImageIcon getSelectedIcon() {
        return SELECTED_ICON;
    }


    /**
     * Sets an icon of a clock to indicate being on a queue
     */
    public void setQueueIcon() {
        setImageIcon(QUEUE_ICON);
    }

    /**
     * Sets an icon showing a large yellow folder
     */
    public void setLargeFolderIcon() {
        setImageIcon(LARGE_FOLDER_ICON);
    }

    /**
     * This flag indicates whether the offline icon should be drawn or not.
     */
    private boolean drawOfflineIcon;  // default is false

    /**
     * Indicates to the Thumbnail that it should or should not draw its Offline
     * Status. Calls repaint()
     *
     * @param flag true if the little CD-rom icon should be drawn, false if not.
     */
    public void drawOfflineIcon(final boolean flag) {
        if (drawOfflineIcon != flag) {
            drawOfflineIcon = flag;
            repaint();  // throw a repaint request on the EDT
        }
    }

    /**
     * This flag indicates whether the mail icon should be drawn or not.
     */
    private boolean drawMailIcon;  // default is false

    /**
     * indicates whether the mail icon should be drawn or not and calls
     * repaint()
     *
     * @param flag true if it should be drawn, false if not
     */
    public void drawMailIcon(final boolean flag) {
        if (drawMailIcon != flag) {
            drawMailIcon = flag;
            repaint();  // throw a repaint request on the EDT
        }
    }

    /**
     * Indicates whether the Thumbnail is to draw as a selected Thumbnail or
     * not.
     */
    private boolean isSelected;  // default is false

    /**
     * Tells the thumbnail to render itself as a selected thumbnail
     */
    public void setSelected() {
        if (!isSelected) {
            isSelected = true;
            repaint();
        }
    }

    /**
     * Tells the thumbnail to stop rendering itself as a selected thumbnail
     */
    public void setUnSelected() {
        if (isSelected) {
            isSelected = false;
            repaint();
        }
    }


    /**
     * Set the timestamp string to the supplied String. To suppress
     * the timestamp set it to the empty String ""
     *
     * @param timestamp the new timestamp string.
     */
    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
        repaint();
    }

    private String timestamp = "";

    private Point getCoordsForTimestamp(final int width, final int height) {
        return new Point(width - 176, height - 14);
    }

    /**
     * we are overriding the default paintComponent method, grabbing the
     * Graphics handle and doing our own drawing here. Essentially this method
     * draws a large white rectangle. A drawImage is then painted doing an
     * affine transformation on the image to position it so the desired
     * point is in the middle of the Graphics object.
     *
     * @param graphics Graphics
     */
    @Override
    public void paintComponent(final Graphics graphics) {
        if (!SwingUtilities.isEventDispatchThread()) {
            LOGGER.severe("Not running on EDT!");
        }

        final var windowWidth = getSize().width;
        final var windowHeight = getSize().height;

        if (img != null) {
            final var g2d = (Graphics2D) graphics;

            final int focusPointX = (int) (img.getWidth(imgOb) * thumbnailScaleFactor / 2);
            final int focusPointY = (int) (img.getHeight(imgOb) * thumbnailScaleFactor / 2);

            final int xOffset = (int) ((windowWidth / (double) 2) - (focusPointX));
            final int yOffset = (int) ((windowHeight / (double) 2) - (focusPointY));

            // clear damaged component area
            final Rectangle clipBounds = g2d.getClipBounds();
            g2d.setColor(getBackground());
            g2d.fillRect(clipBounds.x,
                    clipBounds.y,
                    clipBounds.width,
                    clipBounds.height);

            final var af1 = AffineTransform.getTranslateInstance(xOffset, yOffset);
            final var af2 = AffineTransform.getScaleInstance(thumbnailScaleFactor, thumbnailScaleFactor);
            af2.concatenate(af1);

            g2d.drawImage(img, af2, imgOb);
            if (isSelected) {
                int x = xOffset + (int) (img.getWidth(imgOb) * thumbnailScaleFactor) - SELECTED_ICON.getIconWidth();
                g2d.drawImage(SELECTED_ICON.getImage(), x, yOffset, SELECTED_ICON.getImageObserver());
            }

            if (drawOfflineIcon) {
                g2d.drawImage(OFFLINE_ICON.getImage(), xOffset + 10, yOffset + 10, OFFLINE_ICON.getImageObserver());
            }
            if (drawMailIcon) {
                int additionalOffset = drawOfflineIcon ? 40 : 0;
                g2d.drawImage(MAIL_ICON.getImage(), xOffset + 10 + additionalOffset, yOffset + 10, MAIL_ICON.getImageObserver());
            }
            g2d.setColor(new Color(230, 112, 9));
            final var timeStampCoords = getCoordsForTimestamp(windowWidth, windowHeight);
            g2d.drawString(timestamp, timeStampCoords.x, timeStampCoords.y);
        } else {
            // paint a black square
            graphics.setClip(0, 0, windowWidth, windowHeight);
            graphics.setColor(Color.black);
            graphics.fillRect(0, 0, windowWidth, windowHeight);
        }
    }


}
