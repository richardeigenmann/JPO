/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpo.gui;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import jpo.gui.swing.Thumbnail;
import junit.framework.TestCase;
import static junit.framework.TestCase.fail;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author richi
 */
public class ThumbnailControllerTest {

    public ThumbnailControllerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private Thumbnail thumbnail = null;

    @Test
    public void testConstructor() {

        // TravisCI runs headless so we can't execute the below test
        GraphicsEnvironment ge
                = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if ( ge.isHeadless() ) {
            return;
        }
        

        TestCase.assertNull( thumbnail );
        try {
            SwingUtilities.invokeAndWait( new Runnable() {

                @Override
                public void run() {
                    thumbnail = new Thumbnail();

                }
            } );
            TestCase.assertNotNull( thumbnail );
            ThumbnailController thumbnailController = new ThumbnailController( thumbnail, 350 );
            return;
        } catch ( InterruptedException ex ) {
            Logger.getLogger( ThumbnailControllerTest.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( InvocationTargetException ex ) {
            Logger.getLogger( ThumbnailControllerTest.class.getName() ).log( Level.SEVERE, null, ex );
        }
        fail( "Could not construct a Thumbnail Controller" );

    }
}
