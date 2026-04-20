package org.jpo.eventbus;

import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.NodeNavigatorInterface;

import java.awt.*;

/*
 Copyright (C) 2019-2024 Richard Eigenmann, Zürich
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
 * This request indicates that the group popup menu should be shown
 *
 * @param nodes   The nodes on which the request was invoked
 * @param index   the current index
 * @param invoker The component on which the request was invoked
 * @param x       the x coordinates for the popup window
 * @param y       the y coordinates for the popup window
 * @author Richard Eigenmann
 */
public record ShowPicturePopUpMenuRequest(@NotNull NodeNavigatorInterface nodes, int index,
                                          @NotNull Component invoker, int x, int y) {
}
