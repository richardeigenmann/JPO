package org.jpo.EventBus;

import java.awt.Component;
import org.jpo.dataModel.SortableDefaultMutableTreeNode;
import org.jpo.gui.AutoAdvanceInterface;

/*
 Copyright (C) 2017-2017,  Richard Eigenmann, Zürich
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
 * This request indicates that the user wants to see the AutoAdvanceDialog
 *
 * @author Richard Eigenmann
 */
public class ShowAutoAdvanceDialogRequest implements Request {

    public final Component parentComponent;
    public final SortableDefaultMutableTreeNode currentNode;
    public final AutoAdvanceInterface autoAdvanceTarget;

    /**
     * A request to open the AutoAdvanceDialog
     * @param parentComponent The component which anchors the dialog
     * @param currentNode The current node on which the dialog is opened
     * @param autoAdvanceTarget The target widget that will do the auto advance
     */
    public ShowAutoAdvanceDialogRequest( Component parentComponent, SortableDefaultMutableTreeNode currentNode, AutoAdvanceInterface autoAdvanceTarget ) {
        this.parentComponent = parentComponent;
        this.currentNode = currentNode;
        this.autoAdvanceTarget = autoAdvanceTarget;
    }

}
