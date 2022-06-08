package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import java.util.ArrayList;
import java.util.List;

/*
 Copyright (C) 2022  Richard Eigenmann.
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
 * Sets the picture rotation
 */
public class SetPictureRotationHandler {
    /**
     * Handles the SetPictureRotationRequest request by setting the rotation and
     * calling the refresh thumbnails methods
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final SetPictureRotationRequest request) {
        final var pictureInfo = (PictureInfo) request.node().getUserObject();
        pictureInfo.setRotation(request.angle());
        final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
        nodes.add(request.node());
        nodes.add(request.node().getParent());
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, request.priority()));
    }

}
