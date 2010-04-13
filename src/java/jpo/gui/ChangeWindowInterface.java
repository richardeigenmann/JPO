package jpo.gui;


/*
ChangeWindowInterface.java:  defines what a caller of the ChangeWindowPopupMenu
must be able to do.

Copyright (C) 2002  Richard Eigenmann.
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
     *  window positon indicated by the parameter.
     *  @param newMode The new window mode
     */
    public void switchWindowMode( int newMode );


    /**
     * Instruct the caller to switch to the window decorations.
     * @param newDecorations True if the decorations must be changed, false
     * if not
     */
    public void switchDecorations( boolean newDecorations );
}

