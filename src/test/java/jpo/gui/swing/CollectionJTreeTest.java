package jpo.gui.swing;

/*
 Copyright (C) 2019  Richard Eigenmann.
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

import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.fail;

/**
 * @author Richard Eigenmann
 */


public class CollectionJTreeTest {
    @Test
    public void testImageInitialisation() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                CollectionJTree c = new CollectionJTree();
                Assert.assertNotNull(c);

                Assert.assertNotNull("PICTURE_ICON must not be null", c.getPictureIcon());
                Assert.assertNotNull("CLOSED_FOLDER_ICON must not be null", c.getClosedFolderIcon());
                Assert.assertNotNull("OPEN_FOLDER_ICON must not be null", c.getOpenFolderIcon());

            });
        } catch (InterruptedException | InvocationTargetException ex) {
            fail(ex.getCause().getMessage());
        }
    }
}

