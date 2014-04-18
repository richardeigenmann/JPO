package jpo.gui;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.logging.Logger;

/**
 *
 * @author Richard Eigenmann
 */
public class JpoTransferrableDropTargetListener implements DropTargetListener {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( JpoTransferrableDropTargetListener.class.getName() );

    /**
     * Reference to the node for which this is a drop target listener.
     */
    private final JpoDropTargetDropEventHandler jpoDropTargetDropEventHandler;

    public JpoTransferrableDropTargetListener( JpoDropTargetDropEventHandler jpoDropTargetDropEventHandler ) {
        this.jpoDropTargetDropEventHandler = jpoDropTargetDropEventHandler;
    }
    

    /**
     * this callback method is invoked every time something is dragged onto the
     * ThumbnailController. We check if the desired DataFlavor is supported and
     * then reject the drag if it is not.
     *
     * @param event
     */
    @Override
    public void dragEnter( DropTargetDragEvent event ) {
        if ( !event.isDataFlavorSupported( JpoTransferable.jpoNodeFlavor ) ) {
            event.rejectDrag();
        }

    }

    /**
     * this callback method is invoked every time something is dragged over the
     * ThumbnailController. We could do some highlighting if we so desired.
     *
     * @param event
     */
    @Override
    public void dragOver( DropTargetDragEvent event ) {
        if ( !event.isDataFlavorSupported( JpoTransferable.jpoNodeFlavor ) ) {
            event.rejectDrag();
        }

    }

    /**
     * this callback method is invoked when the user presses or releases shift
     * when doing a drag. He can signal that he wants to change the copy / move
     * of the operation. This method could intercept this change and could
     * modify the event if it needs to. On Thumbnails this does nothing.
     *
     * @param event
     */
    @Override
    public void dropActionChanged( DropTargetDragEvent event ) {
    }

    /**
     * this callback method is invoked to tell the dropTarget that the drag has
     * moved on to something else. We do nothing here.
     *
     * @param event
     */
    @Override
    public void dragExit( DropTargetEvent event ) {
        LOGGER.fine( "Thumbnail.dragExit( DropTargetEvent ): invoked" );
    }

    /**
     * This method is called when the drop occurs. It gives the hard work to the
     * SortableDefaultMutableTreeNode.
     *
     * @param event
     */
    @Override
    public void drop( DropTargetDropEvent event ) {
        jpoDropTargetDropEventHandler.handleJpoDropTargetDropEvent( event );
    }
}
