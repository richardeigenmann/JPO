package org.jpo.gui;

import java.io.File;

/*
 Copyright (C) 2015-2024 Richard Eigenmann.
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
 * This interface allows the GUI to call back with the directory and whether to
 * consolidate the subgroups.
 *
 * @author Richard Eigenmann
 */
public interface ConsolidateGroupActionCallback {

    /**
     * The callback with the chosen parameters
     *
     * @param highresDirectory The target directory
     * @param recurseSubgroups true if the subgroups are to consolidated,
     *                         false if not
     */
    void consolidateGroupCallback(final File highresDirectory, final boolean recurseSubgroups);
}
