package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;

/*
 Copyright (C) 2022-2024 Richard Eigenmann.
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
 * Handles the RotatePictureRequest request
 */
@EventHandler
public class RotatePictureHandler {
    /**
     * Handles the RotatePictureRequest request
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final RotatePicturesRequest request) {
        request
                .nodes()
                .stream()
                .filter(e -> e.getUserObject() instanceof PictureInfo)
                .forEach(e -> ((PictureInfo) e.getUserObject()).rotate((request.angle())));
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(request.nodes(), true, request.priority()));
    }

}
