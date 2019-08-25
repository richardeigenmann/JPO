package export;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

import org.jpo.EventBus.GenerateWebsiteRequest;
import org.jpo.export.GenerateWebsiteWizard1Welcome;

import static junit.framework.TestCase.fail;
import org.junit.Assert;
import org.junit.Test;

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
    public void testGenerateWebsiteWizard1WelcomeTestConstrutor() {
        try {
            SwingUtilities.invokeAndWait( () -> {
                GenerateWebsiteRequest generateWebsiteRequest = new GenerateWebsiteRequest();
                GenerateWebsiteWizard1Welcome generateWebsiteWizard1Welcome = new GenerateWebsiteWizard1Welcome(generateWebsiteRequest);
                Assert.assertNotNull(generateWebsiteWizard1Welcome);
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail("Hit an Exception: " + ex.getMessage());
        }

    }

}
