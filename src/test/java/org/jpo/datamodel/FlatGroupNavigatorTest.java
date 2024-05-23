package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2023-2024 Richard Eigenmann.
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
class FlatGroupNavigatorTest {

    @Test
    void testFlatGroupNavigator() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final var GROUP_INFO = "GroupInfo";
        final var node = new SortableDefaultMutableTreeNode(new GroupInfo(GROUP_INFO));
        final var navigator = new FlatGroupNavigator(node);
        assertEquals(GROUP_INFO, navigator.getTitle());
    }
}