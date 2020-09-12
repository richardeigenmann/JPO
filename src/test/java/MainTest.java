import org.jpo.Main;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.CloseApplicationRequest;
import org.jpo.eventbus.JpoEventBus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author Richard Eigenmann
 */
public class MainTest {

    @BeforeAll
    public static void beforeAll() {
        Settings.loadSettings(); // We need to start the cache
    }

    @Test
    @Disabled("Crashes but I don't know where")
    public void constructorTest() {
        try {
            SwingUtilities.invokeAndWait( () -> {
                final String[] args = {""};
                Main.main( args );
                JpoEventBus.getInstance().post( new CloseApplicationRequest() );
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }

    }
}
