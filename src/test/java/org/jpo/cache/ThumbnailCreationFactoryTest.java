package org.jpo.cache;

import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

import static junit.framework.TestCase.fail;

/**
 *
 * @author Richard Eigenmann
 */
public class ThumbnailCreationFactoryTest {

    @Test
    public void thumbnailCreationFactoryTest() {
        try {
            SwingUtilities.invokeAndWait( () -> {
                ThumbnailCreationFactory tcf = new ThumbnailCreationFactory( 500 );
                Assert.assertNotNull( tcf );
                tcf.endThread();
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail( ex.getMessage() );
        }
    }
}
