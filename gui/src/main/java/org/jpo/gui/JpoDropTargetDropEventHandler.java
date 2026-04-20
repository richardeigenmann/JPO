package org.jpo.gui;

import java.awt.dnd.DropTargetDropEvent;

/*
 Copyright (C) 2025 Richard Eigenmann.
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
