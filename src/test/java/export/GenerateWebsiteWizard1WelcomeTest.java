package export;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import jpo.export.GenerateWebsiteWizard1Welcome;
import jpo.export.HtmlDistillerOptions;
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
                HtmlDistillerOptions htmlDistillerOptions = new HtmlDistillerOptions();
                GenerateWebsiteWizard1Welcome generateWebsiteWizard1Welcome = new GenerateWebsiteWizard1Welcome(htmlDistillerOptions );
                Assert.assertNotNull(generateWebsiteWizard1Welcome);
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail("Hit an Exception: " + ex.getMessage());
        }

    }

}
