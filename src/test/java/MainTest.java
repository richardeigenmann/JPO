import org.jpo.Main;
import org.jpo.eventbus.CloseApplicationRequest;
import org.jpo.eventbus.JpoEventBus;
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

    @Test
    @Disabled("Crashes with an NPE somewhere")
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
