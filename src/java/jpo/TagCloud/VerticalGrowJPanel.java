package jpo.TagCloud;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/*
VerticalGrowJPanel.java:  A JPanel that grows vertically while maintining the with of it's parent JScrollpane

Copyright (C) 2009  Richard Eigenmann.
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
 * This class was created to work in conjunction with a JScrollPane and FlowLayout.
 * When adding components FlowLayout adds them to the right of each other until
 * the width is filled up. Then FlowLayout adds a line. This class implements
 * Scrollable as this allows us to trap the getPreferredSize and grow the JPanel
 * vertically to show the components. FlowLayout seems to be stopping horizontally
 * because getScrollableTracksViewportWidth is defined to return true;
 *
 * This is based on the article found here:
 * http://forums.sun.com/thread.jspa?forumID=57&threadID=5117549&start=7
 *
 * @author Richard Eigenmann
 */
public class VerticalGrowJPanel extends JPanel implements Scrollable {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( VerticalGrowJPanel.class.getName() );


    
    /**
     * This method gets called by the JSCrollPane to figure out the size oif the
     * Viewport. The getScrollableTracksViewportWidth prevents the components
     * from growing horizontally but we need to figure out the height.
     * @return the preffered size based on the getWidth and getPreferredHeight
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension d = new Dimension( getWidth(), getPreferredHeight() );
        logger.fine( String.format( "getPreferredSize is returning Dimension (%d,%d)", d.width, d.height ) );
        return d;
    }


    /**
     * This method figures out the height of panel by looking at the location and
     * height of the stacked components. FlowLayout will have taken care of the
     * wrap around.
     * Source: http://forums.sun.com/thread.jspa?forumID=57&threadID=5117549&start=7
     * @return the suggested height
     */
    private int getPreferredHeight() {
        int rv = 0;
        for ( int k = 0, count = getComponentCount(); k < count; k++ ) {
            Component comp = getComponent( k );
            Rectangle r = comp.getBounds();
            int height = r.y + r.height;
            if ( height > rv ) {
                rv = height;
            }
        }
        rv += ( (FlowLayout) getLayout() ).getVgap();
        return rv;
    }


    /**
     * Source: http://forums.sun.com/thread.jspa?forumID=57&threadID=5117549&start=7
     * @return super.getPreferredSize()
     */
    public Dimension getPreferredScrollableViewportSize() {
        return super.getPreferredSize();
    }


    /**
     * Source: http://forums.sun.com/thread.jspa?forumID=57&threadID=5117549&start=7
     * @param visibleRect
     * @param orientation
     * @param direction
     * @return the scrollable block increment
     */
    public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ) {
        return orientation == SwingConstants.VERTICAL ? getParent().getHeight() : getParent().getWidth();
    }


    /**
     * Allows the JPanel to grow vertically beyond the height of the
     * JScrollPane Vieport.
     * @return  flase in this classe
     */
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }


    /**
     * Forces FlowLayout to stop growing horizontally at the idth of the
     * JScrollPane's Vieport width.
     * @return true in this class
     */
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }


    /**
     * Source: http://forums.sun.com/thread.jspa?forumID=57&threadID=5117549&start=7
     * @param visibleRect
     * @param orientation
     * @param direction
     * @return the scrollable unit increment
     */
    public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {
        int scrollAmount = ( orientation == SwingConstants.VERTICAL
                ? getParent().getHeight() : getParent().getWidth() ) / 20;
        return ( scrollAmount == 0 ? 1 : scrollAmount );
    }
}
