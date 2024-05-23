package org.jpo.eventbus;

import org.jetbrains.annotations.NotNull;
import org.jpo.cache.QUEUE_PRIORITY;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import java.util.Collection;

/*
 Copyright (C) 2017-2024 Richard Eigenmann.
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
 * This request indicates that the thumbnails of the specified nodes are
 * supposed to be refreshed
 *
 * @param nodes    The nodes to be refreshed
 * @param priority The priority for the creation queue
 * @author Richard Eigenmann
 */
public record RefreshThumbnailRequest(@NotNull Collection<SortableDefaultMutableTreeNode> nodes, boolean includeParents,
                                      @NotNull QUEUE_PRIORITY priority) {
}