package jpo.gui;

import jpo.gui.swing.ResizableJFrame.WindowSize;


/*
ChangeWindowInterface.java:  defines what a caller of the ChangeWindowPopupMenu
must be able to do.

Copyright (C) 2002-2015  Richard Eigenmann.
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
 *  This interface defines the methods that an object must implement if it
 *  wants to be able to receive requests from a ChangeWindowPopupMenu.
 *
 **/
public interface ChangeWindowInterface {

    /**
     *  If this method is received the implementing class must switch to a new
     *  window position indicated by the parameter.
     *  @param newMode The new window mode
     */
    void switchWindowMode(WindowSize newMode);

    /**
     * Instruct the caller to show or suppress the window decorations.
     * @param newDecoration Send true if decorations should be shown, false if they should not be shown
     */
    void showWindowDecorations(boolean newDecoration);
}
