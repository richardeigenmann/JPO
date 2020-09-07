package org.jpo.export;

import org.jpo.eventbus.GenerateWebsiteRequest;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for the GroupPopupMenu Class
 *
 * @author Richard Eigenmann
 */
public class GenerateWebsiteWizard1WelcomeTest {


    /**
     * test Constructor
     */
    @Test
    public void testGenerateWebsiteWizard1WelcomeTestConstructor() {
        try {
            SwingUtilities.invokeAndWait( () -> {
                GenerateWebsiteRequest generateWebsiteRequest = new GenerateWebsiteRequest();
                GenerateWebsiteWizard1Welcome generateWebsiteWizard1Welcome = new GenerateWebsiteWizard1Welcome(generateWebsiteRequest);
                assertNotNull(generateWebsiteWizard1Welcome);
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }

    }

}
