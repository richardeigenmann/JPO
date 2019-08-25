package org.jpo.gui;

import org.jpo.dataModel.PictureCollection;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static junit.framework.TestCase.fail;


/*
 Copyright (C) 2017-2019  Richard Eigenmann.
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
 *
 * @author Richard Eigenmann
 */
public class CollectionJTreeControllerTest {

    /**
     * Test Constructor
     */
    @Test
    public void testConstructor() {
        // TravisCI runs headless so we can't execute the below test
        if ( GraphicsEnvironment.isHeadless() ) {
            return;
        }
        try {
            SwingUtilities.invokeAndWait( () -> {
                PictureCollection pc = new PictureCollection();
                TestCase.assertNotNull(pc.getRootNode());
                CollectionJTreeController cjtc = new CollectionJTreeController(pc);
                Assert.assertNotNull( cjtc );
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail( ex.getCause().getMessage() );
        }
    }
}
