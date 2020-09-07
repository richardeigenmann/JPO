package org.jpo.cache;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

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
                assertNotNull( tcf );
                tcf.endThread();
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail( ex.getMessage() );
            Thread.currentThread().interrupt();
        }
    }
}
