package jpo.cache;

import java.util.Properties;
import jpo.dataModel.Settings;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Richard Eigenmann
 */
public class JpoCacheTest {


    @Test
    public void testLoadProperties() {
        Settings.loadSettings();
        Properties props = JpoCache.loadProperties();
        assertTrue( "Expecting more than 30 properties to be defined", props.entrySet().size()  > 30);
    }
}
