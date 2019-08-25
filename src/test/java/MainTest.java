
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import org.jpo.EventBus.CloseApplicationRequest;
import org.jpo.EventBus.JpoEventBus;
import static junit.framework.TestCase.fail;

import org.jpo.Main;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Richard Eigenmann
 */
public class MainTest {

    @Test
    @Ignore("Crashes with an NPE somewhere")
    public void constructorTest() {
        try {
            SwingUtilities.invokeAndWait( () -> {
                final String[] args = {""};
                Main.main( args );
                JpoEventBus.getInstance().post( new CloseApplicationRequest() );
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail("This test didn't work. Exception: " + ex.getMessage());
        }

    }
}
