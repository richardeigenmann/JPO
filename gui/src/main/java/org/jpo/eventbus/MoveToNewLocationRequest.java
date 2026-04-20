package org.jpo.eventbus;

import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jspecify.annotations.NonNull;

import java.util.Collection;

/*
 Copyright (C) 2018-2025 Richard Eigenmann.
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
 * Request to move the pictures of the supplied nodes to the supplied directory
 *
 * @param nodes The nodes for which the user would like move the pictures
 * @author Richard Eigenmann
 */
public record MoveToNewLocationRequest(@NonNull Collection<SortableDefaultMutableTreeNode> nodes) {
}
