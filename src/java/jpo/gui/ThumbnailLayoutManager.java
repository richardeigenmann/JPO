package jpo.gui;

import java.awt.Dimension;
import java.awt.Container;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.logging.Logger;

/*
 ThumbnailLayoutManger.java:  a Layout Manager for the Thumbnail pane

 Copyright (C) 2006 - 2009 Richard Eigenmann (for the modifications over the original I copied)
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
 * a Layout Manager for the Thumbnail pane
 */
public class ThumbnailLayoutManager implements LayoutManager {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ThumbnailLayoutManager.class.getName() );

    /**
     * This method gets called when an object is added to the parent component.
     *
     * @param name
     * @param comp
     */
    @Override
    public void addLayoutComponent( String name, Component comp ) {
        LOGGER.fine( "ThumbnailLayoutManager.addLayoutComponent (LayoutManager): called" );
    }

    /**
     * This method is called when an object is removed from the parent
     * component.
     *
     * @param comp
     */
    @Override
    public void removeLayoutComponent( Component comp ) {
        LOGGER.fine( "ThumbnailLayoutManager.removeLayoutComponent: called" );
    }

    /**
     * Returns the preferredLayoutSize for the managed component
     *
     * @param parent
     * @return preferred size
     */
    @Override
    public Dimension preferredLayoutSize( Container parent ) {
        LOGGER.fine( "ThumbnailLayoutManager.preferredLayoutSize: requested" );
        synchronized ( parent.getTreeLock() ) {
            calculateCols( parent );
            int columns = getCols();
            int width = columns * ( getThumbnailWidth() + getHorizontalGutter() );
            int height = 0;

            int rowComponentHeight;
            int logicalThumbnail;
            for ( int i = 0; i < parent.getComponentCount(); i = i + 2 ) {
                logicalThumbnail = i / 2;
                if ( ( logicalThumbnail % columns ) == 0 ) {
                    rowComponentHeight = getHeightOfRow( parent, i, columns ) // Thumbnails
                            + getHeightOfRow( parent, i + 1, columns ); // Descriptions
                    LOGGER.fine( "ThumbnailLayoutManager.preferredLayoutSize: Description height of row " + Integer.toString( ( logicalThumbnail / columns ) ) + " is " + Integer.toString( getHeightOfRow( parent, i + 1, columns ) ) );
                    if ( rowComponentHeight > 0 ) {
                        height += rowComponentHeight + ( 2 * getVerticalGutter() );
                    }
                }
            }
            LOGGER.fine( "ThumbnailLayoutManager.preferredLayoutSize: returning width: " + Integer.toString( width ) + " height: " + Integer.toString( height ) );
            return new Dimension( width, height );
        }
    }

    /**
     * Returns the minimumLayoutSize for the managed component
     *
     * @param target
     * @return preferred size
     */
    @Override
    public Dimension minimumLayoutSize( Container target ) {
        return preferredLayoutSize( target );
    }

    /**
     * The width of the thumbnails in pixels
     */
    private int thumbnailWidth = 350;

    /**
     * Sets the width of the thumbnails
     *
     * @param newThumbnailWidth The width of the thumbnails in the layout.
     */
    public void setThumbnailWidth( int newThumbnailWidth ) {
        thumbnailWidth = newThumbnailWidth;
    }

    /**
     * Returns the thumbnail width.
     *
     * @return The width of the thumbnail in the layout
     */
    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    /**
     * The amount of pixels to place between thumbnails
     */
    private int horizontalGutter = 10;

    /**
     * Sets the horizontal gutter between the thumbnails.
     *
     * @param newGutter The amount of pixels to use as gutter.
     */
    public void setHorizontalGutter( int newGutter ) {
        horizontalGutter = newGutter;
    }

    /**
     * Returns the horizontal gutter between the thumbnails.
     *
     * @return The amount of pixels to used as gutter.
     */
    public int getHorizontalGutter() {
        return horizontalGutter;
    }

    /**
     * The amount of pixels to place between rows
     */
    private int verticalGutter = 10;

    /**
     * Sets the vertical gutter between the rows.
     *
     * @param newGutter The amount of pixels to use as gutter.
     */
    public void setVerticalGutter( int newGutter ) {
        verticalGutter = newGutter;
    }

    /**
     * Returns the vertical gutter between the Thumbnails.
     *
     * @return The amount of pixels to used as gutter.
     */
    public int getVerticalGutter() {
        return verticalGutter;
    }

    /**
     * This method seems to be called by the parent container when it wants to
     * have the components laid out. This implementation runs through all the
     * Thumbnail and Thumbnail description objects and puts them to the right
     * place.
     *
     * @param parent
     */
    @Override
    public void layoutContainer( Container parent ) {
        synchronized ( parent.getTreeLock() ) {
            calculateCols( parent );
            int columns = getCols();

            int column = 0;
            int rowBaseline = 0 - getVerticalGutter();
            int previousDescriptionRowHeight = 0;
            int logicalThumbnail;
            for ( int i = 0; i < parent.getComponentCount(); i = i + 2 ) {
                logicalThumbnail = i / 2;
                if ( ( logicalThumbnail % columns ) == 0 ) {
                    rowBaseline = rowBaseline + getHeightOfRow( parent, i, columns ) + previousDescriptionRowHeight + ( 2 * getVerticalGutter() );
                    previousDescriptionRowHeight = 0;
                    column = 0;
                }

                // coordinates for a Thumbnail
                int width = getThumbnailWidth();
                int height = parent.getComponent( i ).getPreferredSize().height;
                int x = ( column * width ) + ( ( 1 + column ) * getHorizontalGutter() );
                int y = rowBaseline - height;
                parent.getComponent( i ).setBounds( x, y, width, height );

                // coordinates for the ThumbnailDescription
                y = rowBaseline + getVerticalGutter();
                height = parent.getComponent( i + 1 ).getPreferredSize().height;
                previousDescriptionRowHeight = Math.max( previousDescriptionRowHeight, height );
                parent.getComponent( i + 1 ).setBounds( x, y, width, height );

                column++;
            }
        }
    }

    /**
     * Returns the height of the thumbnails in the row
     *
     * @param parent The Container with the thumbnails
     * @param index The first position of the row
     * @param cols The number of columns in the row
     * @return The height of the row
     */
    private int getHeightOfRow( Container parent, int index, int cols ) {
        LOGGER.fine( "ThumbnailLayoutManager.getHeightOfRow: called for index=" + Integer.toString( index ) + " and columns: " + Integer.toString( cols ) );
        int height = 0;
        for ( int i = 0; ( ( 2 * i ) + index < parent.getComponentCount() ) && ( i < cols ); i++ ) {
            height = Math.max( height, parent.getComponent( index + ( 2 * i ) ).getPreferredSize().height );
            LOGGER.fine( "ThumbnailLayoutManager.getHeightOfRow: height of component " + Integer.toString( index + ( 2 * i ) ) + " is " + parent.getComponent( index + ( 2 * i ) ).getPreferredSize().height );
        }
        LOGGER.fine( "ThumbnailLayoutManager.getHeightOfRow: index=" + Integer.toString( index ) + " / height=" + Integer.toString( height ) );
        return height;
    }

    /**
     * Calculates the number of columns we have on the panel and saves the
     * result.
     *
     * @param parent
     * @return true if the number of columns changed, false if not changed
     */
    public boolean calculateCols( Container parent ) {
        int width = getParentComponentWidth( parent );
        int newCols = calculateCols( width );
        if ( newCols != getCols() ) {
            setCols( newCols );
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calculates the number of columns we can show given a specified width
     *
     * @param width
     * @return the number of columns
     */
    public int calculateCols( int width ) {
        int newCols = ( width / ( getThumbnailWidth() + getHorizontalGutter() ) );
        if ( newCols < 1 ) {
            newCols = 1;
        }
        return newCols;
    }

    /**
     * Returns the width of the parent component. This most likely is the JPanel
     * and that is not interesting so we go the parent's parent which is the
     * JViewport and ask that for it's width.
     *
     * @param parent
     * @return the width of the parent component.
     */
    public static int getParentComponentWidth( Container parent ) {
        Container queryComponent = parent;
        if ( parent.getParent() != null ) {
            queryComponent = parent.getParent();  // get the size of the JViewport
        }
        return queryComponent.getBounds().width;
    }

    /**
     * The number of columns that are being displayed in the layout.
     *
     * @see #calculateCols
     * @see #getCols
     * @see #setCols
     */
    private int cols = 1;

    /**
     * Returns how many columns we are showing for the current layout. If this
     * needs to be adjusted call {@link #calculateCols} first.
     *
     * @return The number of columns in the layout
     */
    public int getCols() {
        return cols;
    }

    /**
     * Sets the number of columns we are showing on the panel. This should only
     * be set by {@link #calculateCols}.
     *
     * @param cols The new number of columns-
     */
    private void setCols( int cols ) {
        this.cols = cols;
    }
}
