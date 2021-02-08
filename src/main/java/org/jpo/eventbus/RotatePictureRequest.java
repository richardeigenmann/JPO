package org.jpo.eventbus;

import org.jetbrains.annotations.NotNull;
import org.jpo.cache.QUEUE_PRIORITY;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

/*
 Copyright (C) 2015 - 2021 Richard Eigenmann.
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
 * The receiver of this request is supposed to rotate the picture of the node by the specified angle
 *
 * @param node     The node to rotate
 * @param angle    The angle in degrees for the rotation
 * @param priority The queue priority
 * @author Richard Eigenmann
 */
public record RotatePictureRequest(@NotNull SortableDefaultMutableTreeNode node, double angle,
                                   @NotNull QUEUE_PRIORITY priority) {
}
