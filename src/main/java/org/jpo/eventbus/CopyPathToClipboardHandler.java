package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.logging.Logger;

/*
 Copyright (C) 2023 Richard Eigenmann.
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

public class CopyPathToClipboardHandler {
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(CopyPathToClipboardHandler.class.getName());

    /**
     * Copies the path(s) of the supplied picture node(s) to the system clipboard
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final CopyPathToClipboardRequest request) {
        final var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final var sb = new StringBuilder();
        for (final var node : request.nodes()) {
            if (node.getUserObject() instanceof PictureInfo pictureInfo) {
                sb.append(pictureInfo.getImageFile().getAbsoluteFile());
                sb.append(System.lineSeparator());
            }
        }
        final var stringSelection = new StringSelection(sb.toString());
        clipboard.setContents(stringSelection, (Clipboard clipboard1, Transferable contents) -> LOGGER.info("Lost Ownership of clipboard - not an issue"));
    }

}
