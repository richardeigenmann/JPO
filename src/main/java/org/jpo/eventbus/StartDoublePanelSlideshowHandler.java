package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.RandomNavigator;
import org.jpo.gui.PictureViewer;

import static org.jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_UNDECORATED_LEFT;
import static org.jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_UNDECORATED_RIGHT;

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

public class StartDoublePanelSlideshowHandler {
    /**
     * Starts a double panel slide show
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final StartDoublePanelSlideshowRequest request) {
        final var p1 = new PictureViewer();
        p1.switchWindowMode(WINDOW_UNDECORATED_LEFT);
        final var p2 = new PictureViewer();
        p2.switchWindowMode(WINDOW_UNDECORATED_RIGHT);
        final var rootNode = request.node();
        final var rb1 = new RandomNavigator(rootNode.getChildPictureNodes(true), String.format("Randomised pictures from %s", rootNode.toString()));
        final var rb2 = new RandomNavigator(rootNode.getChildPictureNodes(true), String.format("Randomised pictures from %s", rootNode.toString()));
        p1.showNode(rb1, 0);
        p1.startAdvanceTimer(10);
        p2.showNode(rb2, 0);
        p2.startAdvanceTimer(10);
    }

}
