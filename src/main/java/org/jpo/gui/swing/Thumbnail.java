package org.jpo.gui.swing;

import org.jetbrains.annotations.TestOnly;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2010-2020  Richard Eigenmann, Zurich, Switzerland
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
 * This class extends a JComponent showing and ImageIcon. The ImageIcon can be
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
    }

    /**
     * sets the image the Thumbnail should show If we are not on the EDT this is
     * submitted to the EDT.
     *
     * @param icon The ImageIcon that should be displayed
     */
    public void setImageIcon(final ImageIcon icon) {
        LOGGER.log(Level.FINE, "Setting image on thumbnail {0}", hashCode());
        final Runnable runnable = () -> {
            if (icon == null) {
                return;
            }
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
            // non visible the whole rendering engine sees no point in fixing the size.
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
     * This method sets the scaling factor for the display of a thumbnail. 0 ..
     * 1
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
        int height = 0;
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
     * I've put in this variable because I have having real trouble with the
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

    /**
     * This icon indicates that the thumbnail creation is sitting on the queue.
     */
    private static final ImageIcon QUEUE_ICON;

    static {
        final String QUEUE_ICON_FILE = "queued_thumbnail.gif";
        final URL resource = Thumbnail.class.getClassLoader().getResource(QUEUE_ICON_FILE);
        if (resource == null) {
            LOGGER.log(Level.SEVERE, "Classloader failed to load file: {0}", QUEUE_ICON_FILE);
            QUEUE_ICON = null;
        } else {
            QUEUE_ICON = new ImageIcon(resource);
        }
    }

    @TestOnly
    ImageIcon getQueueIcon() {
        return QUEUE_ICON;
    }


    /**
     * This icon shows a large yellow folder.
     */
    private static final ImageIcon LARGE_FOLDER_ICON;

    static {
        final String LARGE_FOLDER_ICON_FILE = "icon_folder_large.jpg";
        final URL resource = Thumbnail.class.getClassLoader().getResource(LARGE_FOLDER_ICON_FILE);
        if (resource == null) {
            LOGGER.log(Level.SEVERE, "Classloader failed to load file: {0}", LARGE_FOLDER_ICON_FILE);
            LARGE_FOLDER_ICON = null;
        } else {
            LARGE_FOLDER_ICON = new ImageIcon(resource);
        }
    }


    @TestOnly
    ImageIcon getLargeFolderIcon() {
        return LARGE_FOLDER_ICON;
    }


    /**
     * The icon to superimpose on the picture if the highres picture is not
     * available
     */
    private static final ImageIcon OFFLINE_ICON;

    static {
        final String OFFLINE_ICON_FILE = "icon_offline.gif";
        final URL resource = Thumbnail.class.getClassLoader().getResource(OFFLINE_ICON_FILE);
        if (resource == null) {
            LOGGER.log(Level.SEVERE, "Classloader failed to load file: {0}", OFFLINE_ICON_FILE);
            OFFLINE_ICON = null;
        } else {
            OFFLINE_ICON = new ImageIcon(resource);
        }
    }

    @TestOnly
    ImageIcon getOfflineIcon() {
        return OFFLINE_ICON;
    }

    /**
     * The mail icon to superimpose on the picture
     */
    private static final ImageIcon MAIL_ICON;

    static {
        final String MAIL_ICON_FILE = "icon_mail.gif";
        final URL resource = Thumbnail.class.getClassLoader().getResource(MAIL_ICON_FILE);
        if (resource == null) {
            LOGGER.log(Level.SEVERE, "Classloader failed to load file: {0}", MAIL_ICON_FILE);
            MAIL_ICON = null;
        } else {
            MAIL_ICON = new ImageIcon(resource);
        }
    }

    @TestOnly
    ImageIcon getMailIcon() {
        return MAIL_ICON;
    }


    /**
     * The mail icon to superimpose on the picture
     */
    private static final ImageIcon SELECTED_ICON;

    static {
        final String SELECTED_ICON_FILE = "icon_selected.gif";
        final URL resource = Thumbnail.class.getClassLoader().getResource(SELECTED_ICON_FILE);
        if (resource == null) {
            LOGGER.log(Level.SEVERE, "Classloader failed to load file: {0}", SELECTED_ICON_FILE);
            SELECTED_ICON = null;
        } else {
            SELECTED_ICON = new ImageIcon(resource);
        }
    }

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
     * Indicates to the Thumbnail that it should or should not draw it's Offline
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
     * we are overriding the default paintComponent method, grabbing the
     * Graphics handle and doing our own drawing here. Essentially this method
     * draws a large white rectangle. A drawImage is then painted doing an
     * affine transformation on the image to position it so the the desired
     * point is in the middle of the Graphics object.
     *
     * @param graphics Graphics
     */
    @Override
    public void paintComponent(final Graphics graphics) {
        if (!SwingUtilities.isEventDispatchThread()) {
            LOGGER.severe("Not running on EDT!");
        }

        final int windowWidth = getSize().width;
        final int windowHeight = getSize().height;

        if (img != null) {
            final Graphics2D g2d = (Graphics2D) graphics;

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

            final AffineTransform af1 = AffineTransform.getTranslateInstance(xOffset, yOffset);
            final AffineTransform af2 = AffineTransform.getScaleInstance(thumbnailScaleFactor, thumbnailScaleFactor);
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
        } else {
            // paint a black square
            graphics.setClip(0, 0, windowWidth, windowHeight);
            graphics.setColor(Color.black);
            graphics.fillRect(0, 0, windowWidth, windowHeight);
        }
    }
}
