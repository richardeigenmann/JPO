package jpo.gui;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import jpo.dataModel.PictureCollection;
import junit.framework.TestCase;
import static junit.framework.TestCase.fail;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/*
 Copyright (C) 2017-2017  Richard Eigenmann.
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
    @Ignore ("Why is there an NPE?")
    public void testConstructor() {
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
