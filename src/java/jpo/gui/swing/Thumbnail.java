package jpo.gui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import jpo.dataModel.Settings;
import jpo.dataModel.Tools;

/*
 Thumbnail.java:  This class shows a single Thumbnail

 Copyright (C) 2010-2014  Richard Eigenmann, Zurich, Switzerland
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
    private static final Logger LOGGER = Logger.getLogger( Thumbnail.class.getName() );

    /**
     * Constructor. Make sure you are on the EDT before calling.
     */
    public Thumbnail() {
        initComponents();
    }

    /**
     * Initialises the Component
     */
    private void initComponents() {
        Tools.checkEDT();
        setVisible( false );
        setOpaque( false );
        setBackground( Settings.UNSELECTED_COLOR );
    }

    /**
     * sets the image the Thumbnail should show If we are not on the EDT this is
     * submitted to the EDT.
     *
     * @param icon The ImageIcon that should be displayed
     */
    public void setImageIcon( final ImageIcon icon ) {
        LOGGER.fine( String.format( "Setting image on thumbnail %d", hashCode() ) );
        Runnable r = new Runnable() {

            @Override
            public void run() {
                if ( icon == null ) {
                    return;
                }
                img = icon.getImage();
                if ( img == null ) {
                    return;
                }
                imgOb = icon.getImageObserver();
                thumbnailHeight = img.getHeight( imgOb );

                RescaleOp darkenOp = new RescaleOp( .6f, 0, null );
                BufferedImage source = new BufferedImage( img.getWidth( imgOb ), thumbnailHeight, BufferedImage.TYPE_INT_BGR );
                source.createGraphics().drawImage( img, 0, 0, null );
                selectedThumbnail = darkenOp.filter( source, null );

                // force update of layout
                setVisible( false );
                setVisible( true );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
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
    public void setVisible( final boolean visibility ) {
        Tools.checkEDT();
        super.setVisible( visibility );
        if ( visibility ) {
            if ( getSize().height != thumbnailHeight ) {
                //logger.info("ThumbnailController.setVisible: The Size is not right!");
                // finally I found the solution to the size issue! Unless it's set to
                //  non visible the whole rendering engine sees no point in fixing the size.
                Thumbnail.super.setVisible( false );
                Thumbnail.super.setVisible( true );
            }
        }
    }

    /**
     * The factor which is multiplied with the ThumbnailController to determine
     * how large it is shown.
     */
    private float thumbnailSizeFactor = 1;

    /**
     * This method sets the scaling factor for the display of a thumbnail. 0 ..
     * 1
     *
     * @param thumbnailSizeFactor factor
     */
    public void setFactor( float thumbnailSizeFactor ) {
        LOGGER.fine( String.format( "Scaling factor is being set to %f", thumbnailSizeFactor ) );
        this.thumbnailSizeFactor = thumbnailSizeFactor;
        setVisible( isVisible() );  //Todo wouldn't revalidate be better to force relayout and repainting?
    }

    /**
     * the desired size for the thumbnail
     *
     */
    private int thumbnailSize;

    /**
     * Sets the maximum Thumbnail size
     *
     * @param thumbnailSize the maximum thumbnail size
     */
    public void setThumbnailSize( int thumbnailSize ) {
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
        return new Dimension( thumbnailSize, thumbnailSize );
    }

    /**
     * Returns the preferred size for the Thumbnail as a Dimension using the
     * thumbnailSize as width and height.
     *
     * @return Returns the preferred size for the Thumbnail as a Dimension using
     * the thumbnailSize as width and height.
     */
    @Override
    public Dimension getPreferredSize() {
        int height = 0;
        if ( isVisible() ) {
            height = (int) ( thumbnailHeight * thumbnailSizeFactor );
        }
        return new Dimension( (int) ( thumbnailSize * thumbnailSizeFactor ), height );
    }

    /**
     * Returns the maximum (scaled) size for the Thumbnail as a Dimension using
     * the thumbnailSize as width and height.
     *
     * @return maximus side for the Thumbnail
     */
    @Override
    public Dimension getMaximumSize() {
        return new Dimension( (int) ( thumbnailSize * thumbnailSizeFactor ), (int) ( thumbnailSize * thumbnailSizeFactor ) );
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
    private Image img;

    /**
     * The Image Observer of the image that should be displayed
     */
    private ImageObserver imgOb;

    /**
     * This variable will hold the darkend or otherwise processed Thumbnail that
     * will be painted when the ThumbnailController is on a selected node.
     */
    private transient BufferedImage selectedThumbnail;

    /**
     * reference to the ClassLoader to allow retrieval of the static icons.
     */
    private static final ClassLoader CLASS_LOADER = Thumbnail.class.getClassLoader();

    /**
     * This icon indicates that the thumbnail creation is sitting on the queue.
     */
    private static final ImageIcon QUEUE_ICON = new ImageIcon( CLASS_LOADER.getResource( "jpo/images/queued_thumbnail.gif" ) );

    /**
     * This icon shows a large yellow folder.
     */
    private static final ImageIcon LARGE_FOLDER_ICON = new ImageIcon( CLASS_LOADER.getResource( "jpo/images/icon_folder_large.jpg" ) );

    /**
     * The icon to superimpose on the picture if the highres picture is not
     * available
     */
    private static final ImageIcon OFFLINE_ICON = new ImageIcon( CLASS_LOADER.getResource( "jpo/images/icon_offline.gif" ) );

    /**
     * An icon that indicates a broken image used when there is a problem
     * rendering the correct thumbnail.
     */
    private static final ImageIcon BROKEN_THUMBNAIL_PICTURE = new ImageIcon( CLASS_LOADER.getResource( "jpo/images/broken_thumbnail.gif" ) );

    /**
     * The icon to superimpose on the picture if the highres picture is not
     * available
     */
    private static final ImageIcon MAIL_ICON = new ImageIcon( CLASS_LOADER.getResource( "jpo/images/icon_mail.gif" ) );

    /**
     * Sets an icon of a clock to indicate being on a queue
     */
    public void setQueueIcon() {
        setImageIcon( QUEUE_ICON );
    }

    /**
     * Sets an icon showing a large yellow folder
     */
    public void setLargeFolderIcon() {
        setImageIcon( LARGE_FOLDER_ICON );
    }

    /**
     * Sets an icon to mark that the thumbnail is in loading state before a
     * final icon is put in place by a ThumbnailCreation
     */
    public void setBrokenIcon() {
        setImageIcon( BROKEN_THUMBNAIL_PICTURE );
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
    public void drawOfflineIcon( boolean flag ) {
        if ( drawOfflineIcon != flag ) {
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
    public void drawMailIcon( boolean flag ) {
        if ( drawMailIcon != flag ) {
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
     * changes the color so that the user sees that the thumbnail is part of the
     * selection.<p>
     * This method is EDT safe.
     */
    public void showAsSelected() {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                setBorder( BorderFactory.createLineBorder( Settings.SELECTED_COLOR, 12 ) );
                setBackground( Settings.SELECTED_COLOR_TEXT );
                isSelected = true;
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }

    }

    /**
     * Changes the color so that the user sees that the thumbnail is not part of
     * the selection<p>
     * This method is EDT safe
     */
    public void showAsUnselected() {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                setBorder( BorderFactory.createEmptyBorder() );
                setBackground( Settings.UNSELECTED_COLOR );
                isSelected = false;
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }

    }

    /**
     * we are overriding the default paintComponent method, grabbing the
     * Graphics handle and doing our own drawing here. Essentially this method
     * draws a large black rectangle. A drawImage is then painted doing an
     * affine transformation on the image to position it so the the desired
     * point is in the middle of the Graphics object.
     *
     * @param graphics Graphics
     */
    @Override
    public void paintComponent( Graphics graphics ) {
        if ( !SwingUtilities.isEventDispatchThread() ) {
            LOGGER.severe( "Not running on EDT!" );
        }

        int WindowWidth = getSize().width;
        int WindowHeight = getSize().height;

        if ( img != null ) {
            Graphics2D g2d = (Graphics2D) graphics;

            int focusPointx = (int) ( img.getWidth( imgOb ) * thumbnailSizeFactor / 2 );
            int focusPointy = (int) ( img.getHeight( imgOb ) * thumbnailSizeFactor / 2 );

            int X_Offset = (int) ( ( WindowWidth / (double) 2 ) - ( focusPointx ) );
            int Y_Offset = (int) ( ( WindowHeight / (double) 2 ) - ( focusPointy ) );

            // clear damaged component area
            Rectangle clipBounds = g2d.getClipBounds();
            g2d.setColor( getBackground() );
            g2d.fillRect( clipBounds.x,
                    clipBounds.y,
                    clipBounds.width,
                    clipBounds.height );

            AffineTransform af1 = AffineTransform.getTranslateInstance( X_Offset, Y_Offset );
            AffineTransform af2 = AffineTransform.getScaleInstance( (double) thumbnailSizeFactor, (double) thumbnailSizeFactor );
            af2.concatenate( af1 );
            //op = new AffineTransformOp( af2, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );

            if ( isSelected ) {
                g2d.drawImage( selectedThumbnail, af2, imgOb );
            } else {
                g2d.drawImage( img, af2, imgOb );
            }

            if ( drawOfflineIcon ) {
                g2d.drawImage( OFFLINE_ICON.getImage(), X_Offset + 10, Y_Offset + 10, OFFLINE_ICON.getImageObserver() );
            }
            if ( drawMailIcon ) {
                int additionalOffset = drawOfflineIcon ? 40 : 0;
                g2d.drawImage( MAIL_ICON.getImage(), X_Offset + 10 + additionalOffset, Y_Offset + 10, MAIL_ICON.getImageObserver() );
            }
        } else {
            // paint a black square
            graphics.setClip( 0, 0, WindowWidth, WindowHeight );
            graphics.setColor( Color.black );
            graphics.fillRect( 0, 0, WindowWidth, WindowHeight );
        }
    }
}
