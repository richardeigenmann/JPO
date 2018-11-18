package jpo.gui;

import java.awt.dnd.DropTargetDropEvent;

/**
 * Defines the interface that must be implemented by those components that want to
 * receive drop events for JPO Transferables
 * @author Richard Eigenmann
 */
public interface JpoDropTargetDropEventHandler {
    
    /**
     * The implementing class must handle the drop event if one occurs.
     * @param event The drop event
     */
    void handleJpoDropTargetDropEvent(DropTargetDropEvent event);
    
}
