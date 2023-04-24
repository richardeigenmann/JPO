package org.jpo.gui;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.logging.Logger;


/*
Copyright (C) 2020  Richard Eigenmann.
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
 * A drop target listener for the transferable
 *
 * @author Richard Eigenmann
 */
public class JpoTransferableDropTargetListener implements DropTargetListener {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( JpoTransferableDropTargetListener.class.getName() );

    /**
     * Reference to the node for which this is a drop target listener.
     */
    private final JpoDropTargetDropEventHandler jpoDropTargetDropEventHandler;

    /**
     * Constructor
     *
     * @param jpoDropTargetDropEventHandler Event handler
     */
    public JpoTransferableDropTargetListener(final JpoDropTargetDropEventHandler jpoDropTargetDropEventHandler) {
        this.jpoDropTargetDropEventHandler = jpoDropTargetDropEventHandler;
    }


    /**
     * this callback method is invoked every time something is dragged onto the
     * ThumbnailController. We check if the desired DataFlavor is supported and
     * then reject the drag if it is not.
     *
     * @param event The event
     */
    @Override
    public void dragEnter(final DropTargetDragEvent event) {
        if (!event.isDataFlavorSupported(JpoTransferable.jpoNodeFlavor)) {
            event.rejectDrag();
        }

    }

    /**
     * this callback method is invoked every time something is dragged over the
     * ThumbnailController. We could do some highlighting if we so desired.
     *
     * @param event The event
     */
    @Override
    public void dragOver(final DropTargetDragEvent event) {
        if (!event.isDataFlavorSupported(JpoTransferable.jpoNodeFlavor)) {
            event.rejectDrag();
        }

    }

    /**
     * this callback method is invoked when the user presses or releases shift
     * when doing a drag. He can signal that he wants to change the copy / move
     * of the operation. This method could intercept this change and could
     * modify the event if it needs to. On Thumbnails this does nothing.
     *
     * @param event The event
     */
    @Override
    public void dropActionChanged(final DropTargetDragEvent event) {
        // noop
    }

    /**
     * this callback method is invoked to tell the dropTarget that the drag has
     * moved on to something else. We do nothing here.
     *
     * @param event The event
     */
    @Override
    public void dragExit(final DropTargetEvent event) {
        LOGGER.fine("Thumbnail.dragExit( DropTargetEvent ): invoked" );
    }

    /**
     * This method is called when the drop occurs. It gives the hard work to the
     * SortableDefaultMutableTreeNode.
     *
     * @param event The event
     */
    @Override
    public void drop(final DropTargetDropEvent event) {
        jpoDropTargetDropEventHandler.handleJpoDropTargetDropEvent(event );
    }
}
