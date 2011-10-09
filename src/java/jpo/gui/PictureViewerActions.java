package jpo.gui;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/*
PictureViewerActions.java:  Actions that the PictureViewer needs to support

Copyright (C) 2011-2011  Richard Eigenmann, Zürich, Switzerland
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
 * Interface for the actions the Picture Viewer needs to support
 * @author Richard Eigenmann
 */
public interface PictureViewerActions {

    /**
     * The PictureViewer needs to return the current node it is showing.
     * @return  The current node.
     */
    public SortableDefaultMutableTreeNode getCurrentNode();

    /**
     * Request the PictureViewer to display the prior picture.
     * @return  true if successful, false if not.
     */
    public boolean requestPriorPicture();

    /**
     * Request the PictureViewer to display the next picture.
     * @return  true if successful, false if not.
     */
    public boolean requestNextPicture();

    /**
     * Requests that the current picture be rotated from it's current rotation by 
     * the specified amount.
     * @param angle The angle by which the picture should be rotated
     */
    public void rotate( int angle );

    /**
     *  Requests that the popup menu be shown
     **/
    public void requestPopupMenu();

    /**
     *  This function cycles to the next info display overlay
     **/
    public void cylceInfoDisplay();

    /**
     * Sets the scale of the picture to the current screen size and centres it there.
     */
    public void resetPicture();

    /**
     * Close the viewer and release all resources
     */
    public void closeViewer();

    /**
     * Shows a resize popup menu
     **/
    public void requestScreenSizeMenu();

    /**
     *  If there is no timer it brings up the Auto advance method otherwise stops the timer
     */
    public void requestAutoAdvance();

    /**
     * Makes the picture zoom in
     */
    public void zoomIn();

    /**
     * Makes the picture zoom out
     */
    public void zoomOut();
}
