package org.jpo.datamodel;

/*
Copyright (C) 2025 Richard Eigenmann, Zürich, Switzerland
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version. This program is distributed
in the hope that it will be useful, but WITHOUT ANY WARRANTY,
without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
more details. You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
The license is in gpl.txt.
See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * Defines the methods that we want to have on a class that implements the Progress GUI
 */
public interface ProgressTracker {
    /**
     * Method to call with the message we want to show in the progress bar.
     * The caller need not be concerned about the EDT. The implementing class must
     * do the checking and putting on EDT etc.
     * @param message The message to show
     */
    public void update( final String message );

    /**
     * When the progress GUI is no longer of interest this method will be called by
     * the pgoducer.
     */
    public void done();
}
