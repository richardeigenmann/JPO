package jpo.gui;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import jpo.gui.swing.Thumbnail;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import org.junit.Test;

/*
 Copyright (C) 2017  Richard Eigenmann.
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
public class ThumbnailControllerTest {

    private Thumbnail thumbnail = null;

    @Test
    public void testConstructor() {
        assertNull( thumbnail );

        // TravisCI runs headless so we can't execute the below test
        if ( GraphicsEnvironment.isHeadless() ) {
            return;
        }

        try {
            SwingUtilities.invokeAndWait( () -> {
                thumbnail = new Thumbnail();
                assertNotNull( thumbnail );

                ThumbnailController thumbnailController = new ThumbnailController( thumbnail, 350 );
                assertNotNull( thumbnailController );
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( ThumbnailControllerTest.class.getName() ).log( Level.SEVERE, null, ex );
        }

    }
}
