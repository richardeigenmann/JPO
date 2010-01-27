package jpo.gui.swing;

import java.awt.*;
import javax.swing.text.*;

/*
NonFocussedCaret.java:  a dumb caret that doesn't move.

Copyright (C) 2002-2007  Richard Eigenmann.
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
 * This class overrides the <code>adjustVisibility</code> method of the  
 * <code>DefaultCaret</code> class. This beast took me quite a while to figure out: 
 * when a group was being displayed the thumbnails would appear one after the 
 * other and then they would all scroll to the last one automatically. 
 * The cause of this was the invisible caret in the last description. This 
 * was requesting to be made visible and everything went to hell.  
 */
public class NonFocussedCaret
        extends DefaultCaret {

    /**
     *  All we do in this class is to override this method with nothing
     *  so that scrolling doesn't happen any more.
     *
     * @param nloc
     */
    @Override
    protected void adjustVisibility( Rectangle nloc ) {
    }
} 
